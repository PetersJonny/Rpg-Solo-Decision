package criaturas;

import fichas.FichaRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Criatura implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private int nivel;
    private int vida;
    private int defesa;
    private int iniciativa;
    private int bonusAcerto;
    private boolean acertoAutomatico;
    private int testePresenca;
    private int chanceAparecer;
    private int ouroMin, ouroMax, chanceOuro;
    private int xpGanho;
    private List<Ataque> ataques = new ArrayList<>();
    private List<Drop> drops = new ArrayList<>();

    // Dificuldade para fugir com Teste de Destreza (usada nos encontros do labirinto)
    private int dcFuga = 12;

    // Ataque que pode se repetir em cadeia (ex.: Esqueleto): depois do ataque original,
    // tem chance de atacar de novo (chanceAtaqueRepetir) e, se repetir, chance de um terceiro (chanceAtaqueRepetir2).
    private String ataqueRepetivel;
    private int chanceAtaqueRepetir;
    private int chanceAtaqueRepetir2;

    // Ataque que pode infectar o alvo (ex.: Zumbi): ao acertar, o alvo toma 1d4 de dano por rodada
    private String ataqueInfeccioso;
    private int chanceInfeccao;

    // Morto-vivo (Esqueleto, Zumbi, Baú Monstruoso): a Espada Majestral causa dano dobrado contra eles
    private boolean mortoVivo;

    // Enfraquecido pelo Pacto Mortal: -2 em suas rolagens e +5 de dano demoníaco em cada golpe sofrido
    private boolean enfraquecido;

    // Fugi do combate por comando da Coroa do Rei: some sem dar XP nem drops
    private boolean fugiu;

    public Criatura(String nome, int nivel, int vida, int defesa, int iniciativa) {
        this.nome = nome;
        this.nivel = nivel;
        this.vida = vida;
        this.defesa = defesa;
        this.iniciativa = iniciativa;
        this.bonusAcerto = 0;
        this.acertoAutomatico = false;
        this.testePresenca = 10;
        this.chanceAparecer = 100;
    }

    // Configuração de combate
    public void setBonusAcerto(int bonusAcerto) { this.bonusAcerto = bonusAcerto; }
    public void setAcertoAutomatico(boolean acertoAutomatico) { this.acertoAutomatico = acertoAutomatico; }
    public void setTestePresenca(int testePresenca) { this.testePresenca = testePresenca; }
    public void setChanceAparecer(int chanceAparecer) { this.chanceAparecer = chanceAparecer; }

    // Dificuldade para o jogador fugir com Teste de Destreza
    public void setDcFuga(int dcFuga) { this.dcFuga = dcFuga; }
    public int getDcFuga() { return dcFuga; }

    // Configura um ataque que pode se repetir em cadeia: ao usar `nomeAtaque`, a criatura
    // tem `chanceSegundo`% de atacar de novo e, se repetir, `chanceTerceiro`% de um terceiro.
    // Cada repetição é um ataque totalmente novo (nova rolagem de acerto e dano).
    public void configurarAtaqueEncadeado(String nomeAtaque, int chanceSegundo, int chanceTerceiro) {
        this.ataqueRepetivel = nomeAtaque;
        this.chanceAtaqueRepetir = chanceSegundo;
        this.chanceAtaqueRepetir2 = chanceTerceiro;
    }

    // Configura um ataque que infecta o alvo ao acertar (chance%). A infecção causa
    // 1d4 de dano por rodada e some quando o combate acaba.
    public void configurarInfeccao(String nomeAtaque, int chance) {
        this.ataqueInfeccioso = nomeAtaque;
        this.chanceInfeccao = chance;
    }

    // Marca a criatura como morto-vivo (Espada Majestral causa dano dobrado contra ela)
    public void setMortoVivo(boolean mortoVivo) { this.mortoVivo = mortoVivo; }
    public boolean isMortoVivo() { return mortoVivo; }

    // Pacto Mortal: enfraquece o alvo até o fim do combate (-2 em rolagens e +5 de dano sofrido)
    public void setEnfraquecido(boolean enfraquecido) { this.enfraquecido = enfraquecido; }
    public boolean isEnfraquecido() { return enfraquecido; }

    // Fuga ordenada pela Coroa do Rei: a criatura abandona o combate sem XP/drops
    public void setFugiu(boolean fugiu) { this.fugiu = fugiu; }
    public boolean isFugiu() { return fugiu; }

    // Ataques e Drops
    public void adicionarAtaque(String nome, String tipoDano, int qtdDado, int ladosDado) {
        ataques.add(new Ataque(nome, tipoDano, qtdDado, ladosDado));
    }

    public void adicionarDrop(String nomeItem, int qtdMin, int qtdMax, int chance) {
        drops.add(new Drop(nomeItem, qtdMin, qtdMax, chance));
    }

    public void setOuroDrop(int ouroMin, int ouroMax, int chanceOuro) {
        this.ouroMin = ouroMin;
        this.ouroMax = ouroMax;
        this.chanceOuro = chanceOuro;
    }

    public void setXpGanho(int xpGanho) { this.xpGanho = xpGanho; }

    // Getters
    public String getNome() { return nome; }
    public int getNivel() { return nivel; }
    public int getVida() { return vida; }
    public int getDefesa() { return defesa; }
    public int getIniciativa() { return iniciativa; }
    public int getBonusAcerto() { return bonusAcerto; }
    public boolean isAcertoAutomatico() { return acertoAutomatico; }
    public int getTestePresenca() { return testePresenca; }
    public int getChanceAparecer() { return chanceAparecer; }
    public List<Ataque> getAtaques() { return ataques; }
    public int getXpGanho() { return xpGanho; }

    public void setVida(int vida) { this.vida = vida; }

    // Ataque da criatura contra o jogador. `alvoJogadorPrincipal` indica se o alvo é o
    // personagem principal (só ele pode ser infectado). Depois do ataque, se o ataque
    // usado for repetível, a criatura pode atacar novamente (cada repetição é um ataque novo).
    public Ataque atacarJogador(FichaRpg ficha, boolean cascaGrossaAtiva, boolean alvoJogadorPrincipal) {
        Ataque ataqueEscolhido = null;
        int repeticao = 0;
        while (true) {
            ataqueEscolhido = executarAtaque(ficha, cascaGrossaAtiva, alvoJogadorPrincipal);

            boolean podeRepetir = ataqueRepetivel != null
                    && ataqueEscolhido != null
                    && ataqueEscolhido.nome.equals(ataqueRepetivel)
                    && repeticao < 2
                    && ficha.getVidaPersonagem() > 0;
            if (!podeRepetir) break;

            int chance = repeticao == 0 ? chanceAtaqueRepetir : chanceAtaqueRepetir2;
            if (MecanicasRpg.rolarDado(100) > chance) break;

            Interface.MostrarMensagem(nome + " se movimenta e ataca novamente com " + ataqueEscolhido.nome + "!");
            Interface.Pausa(1500);
            repeticao++;
        }
        return ataqueEscolhido;
    }

    // Realiza UM ataque completo (escolhe o ataque, rola acerto/dano e aplica a infecção).
    private Ataque executarAtaque(FichaRpg ficha, boolean cascaGrossaAtiva, boolean alvoJogadorPrincipal) {
        Ataque ataqueEscolhido = ataques.get(MecanicasRpg.rolarDado(ataques.size()) - 1);

        String danoTipo = ataqueEscolhido.tipoDano == null || ataqueEscolhido.tipoDano.isEmpty()
                ? "" : " de " + ataqueEscolhido.tipoDano;

        // Acerto automático não rola d20, portanto sem chance de crítico
        if (acertoAutomatico) {
            int dano = rolarDanoDoAtaque(ataqueEscolhido, false);
            if (cascaGrossaAtiva) {
                dano = Math.max(0, dano - 5);
                Interface.MostrarMensagem("(Casca Grossa ativa! Dano reduzido em 5)");
            }
            ficha.receberDano(dano);
            Interface.MostrarMensagem("-> Ataque do " + nome + " [" + ataqueEscolhido.nome + "] acerta automaticamente! Dano: " + dano + danoTipo + ".");
            if (ficha.isProtecaoAbsolutaAtiva()) {
                refletirProtecaoAbsoluta();
            }
            Interface.Pausa(2000);
            return ataqueEscolhido;
        }

        int dadoAtaque = MecanicasRpg.rolarDado(20);
        int totalAtaque = dadoAtaque + bonusAcerto;
        if (enfraquecido) {
            totalAtaque -= 2;
            Interface.MostrarMensagem("(Pacto Mortal: " + nome + " tem -2 em suas rolagens)");
            Interface.Pausa(1000);
        }
        boolean critico = dadoAtaque == 20;
        Interface.MostrarMensagem("-> Ataque do " + nome + " [" + ataqueEscolhido.nome + "]: " + dadoAtaque + " (Dado) + " + bonusAcerto + " (Bônus) = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
        if (critico) {
            Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
        }
        Interface.Pausa(2000);

        if (critico || totalAtaque >= ficha.getDefesa()) {
            int dano = rolarDanoDoAtaque(ataqueEscolhido, critico);
            if (cascaGrossaAtiva) {
                dano = Math.max(0, dano - 5);
                Interface.MostrarMensagem("(Casca Grossa ativa! Dano reduzido em 5)");
            }
            ficha.receberDano(dano);
            Interface.MostrarMensagem("-> Acertou! Dano: " + dano + danoTipo + " (defesa do jogador: " + ficha.getDefesa() + ")" + (critico ? " CRÍTICO sempre acerta." : ""));
            if (ficha.isProtecaoAbsolutaAtiva()) {
                refletirProtecaoAbsoluta();
            }
            aplicarInfeccao(ficha, alvoJogadorPrincipal, ataqueEscolhido);
        } else {
            int danoQueCausaria = rolarDanoDoAtaque(ataqueEscolhido, false);
            Interface.MostrarMensagem("-> Errou! Dano que causaria: " + danoQueCausaria + danoTipo + " (defesa do jogador: " + ficha.getDefesa() + ")");
        }
        Interface.Pausa(2000);

        return ataqueEscolhido;
    }

    // Se o ataque usado pode infectar e o alvo é o personagem principal, sorteia a infecção
    private void aplicarInfeccao(FichaRpg ficha, boolean alvoJogadorPrincipal, Ataque ataque) {
        if (!alvoJogadorPrincipal || ataqueInfeccioso == null || !ataque.nome.equals(ataqueInfeccioso)) {
            return;
        }
        if (ficha.isInfectado()) return; // não acumula
        if (MecanicasRpg.rolarDado(100) <= chanceInfeccao) {
            ficha.setInfectado(true);
            Interface.MostrarMensagem("A mordida abre uma ferida que infecciona! Você sofrerá 1d4 de dano por rodada.");
            Interface.Pausa(2000);
        }
    }

    // Rola o dano do ataque, dobrando a quantidade de dados em caso de crítico
    private int rolarDanoDoAtaque(Ataque ataque, boolean critico) {
        int dados = ataque.qtdDado * (critico ? 2 : 1);
        int dano = 0;
        for (int i = 0; i < dados; i++) {
            dano += MecanicasRpg.rolarDado(ataque.ladosDado);
        }
        return dano;
    }

    // Proteção Absoluta: reflete dano do elemento no atacante quando ele acerta
    private void refletirProtecaoAbsoluta() {
        int reflexo = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
        this.setVida(this.getVida() - reflexo);
        Interface.MostrarMensagem("(Proteção Absoluta! Reflete " + reflexo + " de dano do elemento no " + nome + ")");
        Interface.Pausa(1500);
    }

    // Processa drops de ouro e itens após a morte
    public void processarDrops(FichaRpg ficha) {
        if (chanceOuro > 0 && MecanicasRpg.rolarDado(100) <= chanceOuro) {
            int ouro = MecanicasRpg.rolarEntre(ouroMin, ouroMax);
            ficha.adicionarOuro(ouro);
            Interface.MostrarMensagem("-> Você encontrou " + ouro + " moedas de ouro!");
            Interface.Pausa(1500);
        }

        for (Drop drop : drops) {
            if (MecanicasRpg.rolarDado(100) <= drop.chance) {
                int qtd = MecanicasRpg.rolarEntre(drop.qtdMin, drop.qtdMax);
                ItemRpg item = criarItemDrop(drop.nomeItem);
                if (item != null) {
                    item.setQuantidade(qtd);
                    ficha.adicionarItem(item);
                    Interface.MostrarMensagem("-> Você coletou " + qtd + "x " + drop.nomeItem + "!");
                    Interface.Pausa(1500);
                }
            }
        }
    }

    // Cria os objetos de itens dropados
    public static ItemRpg criarItemDrop(String nome) {
        switch (nome) {
            case "Couro":
                return new ItemRpg("Couro", "Pele de animal curtida, usada em artesanato e na confecção de equipamentos.", 1);
            case "Dente de Urso":
                return new ItemRpg("Dente de Urso", "Presas grandes e afiadas, valiosas para alquimistas e caçadores.", 1);
            case "Pó da Fada":
                return new ItemRpg("Pó da Fada", "Pó de luz condensada deixado por uma fada.", 1);
            case "Faca":
                return new Arma("Faca", "Uma faca afiada que causa 1d4 de dano corpo a corpo, usando Destreza.", "CaC", 4, 1, 1, "Destreza");
            case "Osso":
                return new ItemRpg("Osso", "Ossos antigos retirados de criaturas do labirinto, valiosos para artesãos e alquimistas.", 1);
            case "Carne Podre":
                return new ItemRpg("Carne Podre", "Carne em decomposição que exala um odor insuportável. Poucos compradores aceitam isso.", 1);
            case "Arco":
                return new Arma("Arco", "Um arco de madeira que dispara flechas, causando 1d6 de dano à distância. Consome flechas.", "LA", 6, 1, 1);
            case "Flechas":
                return new ItemRpg("Flechas", "Munição para arcos e foices.", 1);
            default:
                return null;
        }
    }

    public static class Ataque implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        public String nome;
        public String tipoDano;
        public int qtdDado;
        public int ladosDado;

        public Ataque(String nome, String tipoDano, int qtdDado, int ladosDado) {
            this.nome = nome;
            this.tipoDano = tipoDano;
            this.qtdDado = qtdDado;
            this.ladosDado = ladosDado;
        }
    }

    public static class Drop implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        public String nomeItem;
        public int qtdMin;
        public int qtdMax;
        public int chance;

        public Drop(String nomeItem, int qtdMin, int qtdMax, int chance) {
            this.nomeItem = nomeItem;
            this.qtdMin = qtdMin;
            this.qtdMax = qtdMax;
            this.chance = chance;
        }
    }
}
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

    private static final String RESET = Interface.RESET;
    private static final String VERDE = Interface.VERDE;
    private static final String VERMELHO = Interface.VERMELHO;

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

    // Investida (ex.: Minotauro): chance% do ataque virar uma carga. Se o jogador
    // vencer o teste de Destreza contra o teste de ataque da criatura, ela toma
    // `danoParede` (bate na parede); se falhar, o jogador toma `qtd/lados` de dano.
    private int chanceInvestida;
    private int investidaDanoFalhaQtd;
    private int investidaDanoFalhaLados;
    private int investidaDanoParede;

    // Boss sem fuga (ex.: Minotauro): a porta se fecha e não dá para fugir do combate
    private boolean semFuga;

    // Ao morrer, concede a recompensa exclusiva da classe do jogador
    private boolean dropDeClasse;

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

    // Investida: exige um teste de Destreza do alvo (sentido de esquivar/agarrar).
    public void configurarInvestida(int chance, int qtdDanoFalha, int ladosDanoFalha, int danoParede) {
        this.chanceInvestida = chance;
        this.investidaDanoFalhaQtd = qtdDanoFalha;
        this.investidaDanoFalhaLados = ladosDanoFalha;
        this.investidaDanoParede = danoParede;
    }

    // Boss sem fuga: em combate, a fuga é bloqueada (a porta se fecha)
    public void setSemFuga(boolean semFuga) { this.semFuga = semFuga; }
    public boolean isSemFuga() { return semFuga; }

    // Ao morrer, concede a recompensa exclusiva da classe do jogador
    public void setDropDeClasse(boolean dropDeClasse) { this.dropDeClasse = dropDeClasse; }
    public boolean isDropDeClasse() { return dropDeClasse; }

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
        // INVESTIDA: com chance%, o ataque vira uma carga que pede teste de Destreza
        if (chanceInvestida > 0 && MecanicasRpg.rolarDado(100) <= chanceInvestida) {
            executarInvestida(ficha, cascaGrossaAtiva);
            return null;
        }

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

    // Investida: o alvo faz um teste de Destreza contra o teste de ataque da criatura.
    // Se o alvo passar, a criatura colide com a parede e sofre `danoParede`; se falhar,
    // o alvo recebe `qtd/lados` de dano.
    private void executarInvestida(FichaRpg ficha, boolean cascaGrossaAtiva) {
        Interface.MostrarMensagem("\n" + VERMELHO + nome + " BAIXA A CABEÇA E INVESTE CONTRA VOCÊ COM FÚRIA CEGA!" + RESET);
        Interface.Pausa(2000);

        Interface.pressionarParaTeste("Destreza (Esquivar da investida)");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getDestrezaTeste();
        int dadoMonstro = MecanicasRpg.rolarDado(20);
        int totalMonstro = dadoMonstro + bonusAcerto;
        if (enfraquecido) {
            totalMonstro -= 2;
            Interface.MostrarMensagem("(Pacto Mortal: " + nome + " tem -2 em suas rolagens)");
            Interface.Pausa(1000);
        }
        Interface.MostrarMensagem("-> Investida! Você: " + dadoJogador + " (Dado) + " + ficha.getDestrezaTeste() + " (Destreza) = " + totalJogador);
        Interface.MostrarMensagem("-> " + nome + ": " + dadoMonstro + " (Dado) + " + bonusAcerto + " (Bônus) = " + totalMonstro);
        Interface.Pausa(2000);

        if (totalJogador >= totalMonstro) {
            Interface.MostrarMensagem(VERDE + "Você se joga para o lado e " + nome + " bate de frente na parede! Ele sofre " + investidaDanoParede + " de dano!" + RESET);
            this.setVida(this.getVida() - investidaDanoParede);
            Interface.Pausa(2500);
        } else {
            int dano = 0;
            for (int i = 0; i < investidaDanoFalhaQtd; i++) {
                dano += MecanicasRpg.rolarDado(investidaDanoFalhaLados);
            }
            if (cascaGrossaAtiva) {
                dano = Math.max(0, dano - 5);
                Interface.MostrarMensagem("(Casca Grossa ativa! Dano reduzido em 5)");
            }
            ficha.receberDano(dano);
            Interface.MostrarMensagem(VERMELHO + "A investida te atinge em cheio! Dano: " + dano + " (Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + ")" + RESET);
            if (ficha.isProtecaoAbsolutaAtiva()) {
                refletirProtecaoAbsoluta();
            }
            Interface.Pausa(2500);
        }
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

        if (dropDeClasse) {
            dropExclusivoDaClasse(ficha);
        }
    }

    // Recompensa exclusiva da classe (só o Minotauro concede):
    // Guerreiro ganha a Espada do Minotauro, Mago o Cajado de Sangue e Healer a
    // habilidade Curandeiro Combatente.
    private void dropExclusivoDaClasse(FichaRpg ficha) {
        if (ficha.getClasseDoPersonagem() == null) return;

        if (ficha.getClasseDoPersonagem() instanceof classes.Guerreiro) {
            ItemRpg item = criarItemDrop("Espada do Minotauro");
            ficha.adicionarItem(item);
            Interface.MostrarMensagem(VERDE + "-> Entre as ruínas, você arranca a Espada do Minotauro, troféu digno de um guerreiro!" + RESET);
        } else if (ficha.getClasseDoPersonagem() instanceof classes.Mago) {
            ItemRpg item = criarItemDrop("Cajado de Sangue");
            ficha.adicionarItem(item);
            Interface.MostrarMensagem(VERDE + "-> O sangue do colosso alimenta o Cajado de Sangue, que cai em suas mãos!" + RESET);
        } else if (ficha.getClasseDoPersonagem() instanceof classes.Healer) {
            boolean jaTem = false;
            for (habilidades.Habilidade h : ficha.getHabilidades()) {
                if (h.getNome().equals("Curandeiro Combatente")) {
                    jaTem = true;
                    break;
                }
            }
            if (!jaTem) {
                ficha.getHabilidades().add(new habilidades.ativas.HabilidadeCurandeiroCombatente());
            }
            Interface.MostrarMensagem(VERDE + "-> Suas feridas comandam sangue e aço: você desperta a habilidade Curandeiro Combatente!" + RESET);
        }
        Interface.Pausa(2500);
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
            case "Chifre de Minotauro":
                return new ItemRpg("Chifre de Minotauro", "O troféu de um colosso, cobiçado por caçadores e ferreiros de renome. Vale 100 moedas de ouro.", 1);
            case "Espada do Minotauro":
                return new Arma("Espada do Minotauro", "Forjada das grades do labirinto, pulsa com a fúria do colosso. Causa 2d10 + Força de dano e tem 30% de chance de atacar de novo.", "CaC", 10, 2, 1);
            case "Cajado de Sangue":
                return new Arma("Cajado de Sangue", "Um cajado que pulsa com sangue antigo. Causa 1d6 + Força de dano e concede +1 dado de dano às suas magias.", "CaC/mágico", 6, 1, 1);
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
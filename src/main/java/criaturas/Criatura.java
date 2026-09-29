package criaturas;

import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;
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

    private int dcFuga = 12;

    private String ataqueRepetivel;
    private int chanceAtaqueRepetir;
    private int chanceAtaqueRepetir2;

    private String ataqueInfeccioso;
    private int chanceInfeccao;

    private boolean mortoVivo;

    private boolean enfraquecido;

    private boolean fugiu;

    private int chanceInvestida;
    private int investidaDanoFalhaQtd;
    private int investidaDanoFalhaLados;
    private int investidaDanoParede;

    private boolean semFuga;

    private boolean dropDeClasse;

    private boolean temToqueDeMidas;
    private boolean toqueDeMidasPreparado;
    private int bonusToqueDeMidas;

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

    public void setBonusAcerto(int bonusAcerto) { this.bonusAcerto = bonusAcerto; }
    public void setAcertoAutomatico(boolean acertoAutomatico) { this.acertoAutomatico = acertoAutomatico; }
    public void setTestePresenca(int testePresenca) { this.testePresenca = testePresenca; }
    public void setChanceAparecer(int chanceAparecer) { this.chanceAparecer = chanceAparecer; }

    public void setDcFuga(int dcFuga) { this.dcFuga = dcFuga; }
    public int getDcFuga() { return dcFuga; }

    public void configurarAtaqueEncadeado(String nomeAtaque, int chanceSegundo, int chanceTerceiro) {
        this.ataqueRepetivel = nomeAtaque;
        this.chanceAtaqueRepetir = chanceSegundo;
        this.chanceAtaqueRepetir2 = chanceTerceiro;
    }

    public void configurarInfeccao(String nomeAtaque, int chance) {
        this.ataqueInfeccioso = nomeAtaque;
        this.chanceInfeccao = chance;
    }

    public void setMortoVivo(boolean mortoVivo) { this.mortoVivo = mortoVivo; }
    public boolean isMortoVivo() { return mortoVivo; }

    public void setEnfraquecido(boolean enfraquecido) { this.enfraquecido = enfraquecido; }
    public boolean isEnfraquecido() { return enfraquecido; }

    public void setFugiu(boolean fugiu) { this.fugiu = fugiu; }
    public boolean isFugiu() { return fugiu; }

    public void configurarInvestida(int chance, int qtdDanoFalha, int ladosDanoFalha, int danoParede) {
        this.chanceInvestida = chance;
        this.investidaDanoFalhaQtd = qtdDanoFalha;
        this.investidaDanoFalhaLados = ladosDanoFalha;
        this.investidaDanoParede = danoParede;
    }

    public void setSemFuga(boolean semFuga) { this.semFuga = semFuga; }
    public boolean isSemFuga() { return semFuga; }

    public void setDropDeClasse(boolean dropDeClasse) { this.dropDeClasse = dropDeClasse; }
    public boolean isDropDeClasse() { return dropDeClasse; }

    public void configurarToqueDeMidas() { this.temToqueDeMidas = true; }
    public boolean isTemToqueDeMidas() { return temToqueDeMidas; }
    public int getBonusAcertoEfetivo() { return bonusAcerto + bonusToqueDeMidas; }

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

    public List<Drop> getDrops() { return drops; }

    public Ataque atacarJogador(FichaRpg ficha, boolean cascaGrossaAtiva, boolean alvoJogadorPrincipal) {
        bonusToqueDeMidas = 0;

        if (temToqueDeMidas) {
            if (toqueDeMidasPreparado && ficha.getOuro() > 0) {
                bonusToqueDeMidas = 3;
                toqueDeMidasPreparado = false;
                Interface.MostrarMensagem("\n" + nome + " usa o Toque de Midas e ataca com mais fúria! (+3 para acertar)");
                Interface.Pausa(1500);
            } else if (ficha.getOuro() > 0) {
                toqueDeMidasPreparado = true;
                Interface.MostrarMensagem("\n" + nome + " gastou a ação deste turno ativando o Toque de Midas, sentindo o cheiro de ouro... no próximo turno ele ataca com tudo!");
                Interface.Pausa(1500);
                return null;
            } else {
                toqueDeMidasPreparado = false;
            }
        }

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

    private Ataque executarAtaque(FichaRpg ficha, boolean cascaGrossaAtiva, boolean alvoJogadorPrincipal) {

        if (chanceInvestida > 0 && MecanicasRpg.rolarDado(100) <= chanceInvestida) {
            executarInvestida(ficha, cascaGrossaAtiva);
            return null;
        }

        Ataque ataqueEscolhido = ataques.get(MecanicasRpg.rolarDado(ataques.size()) - 1);

        String danoTipo = ataqueEscolhido.tipoDano == null || ataqueEscolhido.tipoDano.isEmpty()
                ? "" : " de " + ataqueEscolhido.tipoDano;

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
        int totalAtaque = dadoAtaque + getBonusAcertoEfetivo();
        if (enfraquecido) {
            totalAtaque -= 2;
            Interface.MostrarMensagem("(Pacto Mortal: " + nome + " tem -2 em suas rolagens)");
            Interface.Pausa(1000);
        }
        boolean critico = dadoAtaque == 20;
        Interface.MostrarMensagem("-> Ataque do " + nome + " [" + ataqueEscolhido.nome + "]: " + dadoAtaque + " (Dado) + " + getBonusAcertoEfetivo() + " (Bônus) = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
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

    private void executarInvestida(FichaRpg ficha, boolean cascaGrossaAtiva) {
        Interface.MostrarMensagem("\n" + VERMELHO + nome + " BAIXA A CABEÇA E INVESTE CONTRA VOCÊ COM FÚRIA CEGA!" + RESET);
        Interface.Pausa(2000);

        Interface.pressionarParaTeste("Destreza (Esquivar da investida)");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getDestrezaTeste();
        int dadoMonstro = MecanicasRpg.rolarDado(20);
        int totalMonstro = dadoMonstro + getBonusAcertoEfetivo();
        if (enfraquecido) {
            totalMonstro -= 2;
            Interface.MostrarMensagem("(Pacto Mortal: " + nome + " tem -2 em suas rolagens)");
            Interface.Pausa(1000);
        }
        Interface.MostrarMensagem("-> Investida! Você: " + dadoJogador + " (Dado) + " + ficha.getDestrezaTeste() + " (Destreza) = " + totalJogador);
        Interface.MostrarMensagem("-> " + nome + ": " + dadoMonstro + " (Dado) + " + getBonusAcertoEfetivo() + " (Bônus) = " + totalMonstro);
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

    private void aplicarInfeccao(FichaRpg ficha, boolean alvoJogadorPrincipal, Ataque ataque) {
        if (!alvoJogadorPrincipal || ataqueInfeccioso == null || !ataque.nome.equals(ataqueInfeccioso)) {
            return;
        }
        if (ficha.isInfectado()) return;
        if (MecanicasRpg.rolarDado(100) <= chanceInfeccao) {
            ficha.setInfectado(true);
            Interface.MostrarMensagem("A mordida abre uma ferida que infecciona! Você sofrerá 1d4 de dano por rodada.");
            Interface.Pausa(2000);
        }
    }

    private int rolarDanoDoAtaque(Ataque ataque, boolean critico) {
        int dados = ataque.qtdDado * (critico ? 2 : 1);
        int dano = 0;
        for (int i = 0; i < dados; i++) {
            dano += MecanicasRpg.rolarDado(ataque.ladosDado);
        }
        return dano;
    }

    private void refletirProtecaoAbsoluta() {
        int reflexo = MecanicasRpg.rolarDado(8) + MecanicasRpg.rolarDado(8);
        this.setVida(this.getVida() - reflexo);
        Interface.MostrarMensagem("(Proteção Absoluta! Reflete " + reflexo + " de dano do elemento no " + nome + ")");
        Interface.Pausa(1500);
    }

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
                    ficha.coletarItemEncontrado(item, "Você achou");
                }
            }
        }

        if (dropDeClasse) {
            dropExclusivoDaClasse(ficha);
        }
    }

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
            case "Carne de Lobo":
                return new Consumivel("Carne de Lobo", "Carne crua de lobo selvagem. Cozinhe em uma fogueira para ficar segura: crua, pode estar estragada. Sacia a fome e cura 1d3 de vida (se estiver boa).", 1);
            case "Carne de Urso":
                return new Consumivel("Carne de Urso", "Carne crua de urso. Cozinhe em uma fogueira para ficar segura: crua, pode estar estragada. Sacia a fome e cura 1d4 de vida (se estiver boa).", 1);
            case "Carne de Lobo Cozida":
                return new Consumivel("Carne de Lobo Cozida", "Carne de lobo preparada na fogueira. Segura e saborosa: sacia a fome e cura 1d3 de vida.", 1);
            case "Carne de Urso Cozida":
                return new Consumivel("Carne de Urso Cozida", "Carne de urso preparada na fogueira. Segura e saborosa: sacia a fome e cura 1d4 de vida.", 1);
            case "Carne Podre":
                return new Consumivel("Carne Podre", "Carne em decomposição que exala um odor insuportável. Comê-la sacia a fome, mas deixa enjoado. Poucos compradores aceitam isso.", 1);
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

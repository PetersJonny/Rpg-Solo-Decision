package criaturas;

import fichas.FichaRpg;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Criatura {
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
    private List<Ataque> ataques = new ArrayList<>();
    private List<Drop> drops = new ArrayList<>();

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

    public void setVida(int vida) { this.vida = vida; }

    // Ataque da criatura contra o jogador
    public Ataque atacarJogador(FichaRpg ficha, boolean cascaGrossaAtiva) {
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
            ficha.setVidaPersonagem(ficha.getVidaPersonagem() - dano);
            Interface.MostrarMensagem("-> Ataque do " + nome + " [" + ataqueEscolhido.nome + "] acerta automaticamente! Dano: " + dano + danoTipo + ".");
            Interface.Pausa(2000);
            return ataqueEscolhido;
        }

        int dadoAtaque = MecanicasRpg.rolarDado(20);
        int totalAtaque = dadoAtaque + bonusAcerto;
        boolean critico = dadoAtaque == 20;
        Interface.MostrarMensagem("-> Ataque do " + nome + " [" + ataqueEscolhido.nome + "]: " + dadoAtaque + " (Dado) + " + bonusAcerto + " (Bônus) = " + totalAtaque + (critico ? " [CRÍTICO!]" : ""));
        if (critico) {
            Interface.MostrarMensagem("Golpe crítico! O dano de dados será dobrado!");
        }
        Interface.Pausa(2000);

        if (totalAtaque >= ficha.getDefesa()) {
            int dano = rolarDanoDoAtaque(ataqueEscolhido, critico);
            if (cascaGrossaAtiva) {
                dano = Math.max(0, dano - 5);
                Interface.MostrarMensagem("(Casca Grossa ativa! Dano reduzido em 5)");
            }
            ficha.setVidaPersonagem(ficha.getVidaPersonagem() - dano);
            Interface.MostrarMensagem("-> Acertou! Dano: " + dano + danoTipo + " (defesa do jogador: " + ficha.getDefesa() + ")");
        } else {
            Interface.MostrarMensagem("-> Errou! (defesa do jogador: " + ficha.getDefesa() + ")");
        }
        Interface.Pausa(2000);

        return ataqueEscolhido;
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
            case "Brilho Mágico":
                return new ItemRpg("Brilho Mágico", "Fragmento de luz condensada deixado por uma fada.", 1);
            case "Faca":
                return new Arma("Faca", "Uma faca afiada que causa 1d4 de dano corpo a corpo, usando Destreza.", "CaC", 4, 1, 1, "Destreza");
            default:
                return null;
        }
    }

    public static class Ataque {
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

    public static class Drop {
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
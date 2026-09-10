package criaturas;

import java.util.ArrayList;
import java.util.List;
import fichas.FichaRpg;
import mecanicas.MecanicasRpg;

public class Criatura {
    private String nome;
    private int nivel;
    private int vida;
    private int defesa;
    private int iniciativa;
    private List<Ataque> ataques = new ArrayList<>();

    public Criatura(String nome, int nivel, int vida, int defesa, int iniciativa) {
        this.nome = nome;
        this.nivel = nivel;
        this.vida = vida;
        this.defesa = defesa;
        this.iniciativa = iniciativa;
    }

    public void adicionarAtaque(String nome, int qtdDado, int ladosDado) {
        ataques.add(new Ataque(nome, qtdDado, ladosDado));
    }

    public String getNome() { return nome; }
    public int getNivel() { return nivel; }
    public int getVida() { return vida; }
    public int getDefesa() { return defesa; }
    public int getIniciativa() { return iniciativa; }
    public List<Ataque> getAtaques() { return ataques; }

    public void setVida(int vida) { this.vida = vida; }

    public Ataque atacarJogador(FichaRpg ficha, boolean cascaGrossaAtiva) {
        Ataque ataqueEscolhido = ataques.get(MecanicasRpg.rolarDado(ataques.size()) - 1);

        int dano = 0;
        for (int i = 0; i < ataqueEscolhido.qtdDado; i++) {
            dano += MecanicasRpg.rolarDado(ataqueEscolhido.ladosDado);
        }

        if (cascaGrossaAtiva) {
            dano = Math.max(0, dano - 5);
            telas.Interface.MostrarMensagem("(Casca Grossa ativa! Dano reduzido em 5)");
        }

        int dadoAtaque = MecanicasRpg.rolarDado(20);
        int totalAtaque = dadoAtaque + iniciativa;
        telas.Interface.MostrarMensagem("-> Ataque do Lobo [" + ataqueEscolhido.nome + "]: " + dadoAtaque + " (Dado) + " + iniciativa + " (Bônus) = " + totalAtaque);
        telas.Interface.Pausa(2000);

        if (totalAtaque >= ficha.getDefesa()) {
            ficha.setVidaPersonagem(ficha.getVidaPersonagem() - dano);
            telas.Interface.MostrarMensagem("-> Acertou! Dano: " + dano + " (defesa da vítima: " + ficha.getDefesa() + ")");
        } else {
            telas.Interface.MostrarMensagem("-> Errou! (defesa da vítima: " + ficha.getDefesa() + ")");
        }
        telas.Interface.Pausa(2000);

        return ataqueEscolhido;
    }

    public static class Ataque {
        public String nome;
        public int qtdDado;
        public int ladosDado;

        public Ataque(String nome, int qtdDado, int ladosDado) {
            this.nome = nome;
            this.qtdDado = qtdDado;
            this.ladosDado = ladosDado;
        }
    }
}

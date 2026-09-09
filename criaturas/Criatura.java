package criaturas;

public class Criatura {
    private String nome;
    private int nivel;
    private int vida;
    private int defesa;
    private int dadoDano;
    private int qtdDano;
    private int iniciativa;

    public Criatura(String nome, int nivel, int vida, int defesa, int dadoDano, int qtdDano, int iniciativa) {
        this.nome = nome;
        this.nivel = nivel;
        this.vida = vida;
        this.defesa = defesa;
        this.dadoDano = dadoDano;
        this.qtdDano = qtdDano;
        this.iniciativa = iniciativa;
    }

    public String getNome() { return nome; }
    public int getNivel() { return nivel; }
    public int getVida() { return vida; }
    public int getDefesa() { return defesa; }
    public int getDadoDano() { return dadoDano; }
    public int getQtdDano() { return qtdDano; }
    public int getIniciativa() { return iniciativa; }
}

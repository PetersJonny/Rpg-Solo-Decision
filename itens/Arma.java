package itens;

public class Arma extends ItemRpg {
    private String tipoArma;
    private int dadoDanoArma;
    private int quantidadeDanoArma;
    private String atributoAtaque;
    private boolean agil;

    public Arma(String nome, String descricao, String tipoArma, int dadoDanoArma, int quantidadeDanoArma, int quantidade) {
        this(nome, descricao, tipoArma, dadoDanoArma, quantidadeDanoArma, quantidade, tipoArma.contains("CaC") ? "Força" : "Destreza", false);
    }

    public Arma(String nome, String descricao, String tipoArma, int dadoDanoArma, int quantidadeDanoArma, int quantidade, String atributoAtaque) {
        this(nome, descricao, tipoArma, dadoDanoArma, quantidadeDanoArma, quantidade, atributoAtaque, false);
    }

    public Arma(String nome, String descricao, String tipoArma, int dadoDanoArma, int quantidadeDanoArma, int quantidade, String atributoAtaque, boolean agil) {
        super(nome, descricao, quantidade);
        this.tipoArma = tipoArma;
        this.dadoDanoArma = dadoDanoArma;
        this.quantidadeDanoArma = quantidadeDanoArma;
        this.atributoAtaque = atributoAtaque;
        this.agil = agil;
    }

    public String getTipoArma() { return tipoArma; }
    public int getDadoDanoArma() { return dadoDanoArma; }
    public int getQuantidadeDanoArma() { return quantidadeDanoArma; }
    public String getAtributoAtaque() { return atributoAtaque; }
    public boolean isAgil() { return agil; }
}
package itens;

public class Arma extends ItemRpg {
    private String tipoArma;
    private int dadoDanoArma;
    private int quantidadeDanoArma;
    private String atributoAtaque;

    public Arma(String nome, String descricao, String tipoArma, int dadoDanoArma, int quantidadeDanoArma, int quantidade) {
        this(nome, descricao, tipoArma, dadoDanoArma, quantidadeDanoArma, quantidade, tipoArma.contains("CaC") ? "Força" : "Destreza");
    }

    public Arma(String nome, String descricao, String tipoArma, int dadoDanoArma, int quantidadeDanoArma, int quantidade, String atributoAtaque) {
        super(nome, descricao, quantidade);
        this.tipoArma = tipoArma;
        this.dadoDanoArma = dadoDanoArma;
        this.quantidadeDanoArma = quantidadeDanoArma;
        this.atributoAtaque = atributoAtaque;
    }

    public String getTipoArma() { return tipoArma; }
    public int getDadoDanoArma() { return dadoDanoArma; }
    public int getQuantidadeDanoArma() { return quantidadeDanoArma; }
    public String getAtributoAtaque() { return atributoAtaque; }
}
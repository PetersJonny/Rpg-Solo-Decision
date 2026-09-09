package itens;

public class Arma extends ItemRpg {
    private String tipoArma;
    private int dadoDanoArma;
    private int quantidadeDanoArma;

    public Arma(String nome, String tipoArma, int dadoDanoArma, int quantidadeDanoArma, int quantidade) {
        super(nome, quantidade);
        this.tipoArma = tipoArma;
        this.dadoDanoArma = dadoDanoArma;
        this.quantidadeDanoArma = quantidadeDanoArma;
    }

    public String getTipoArma() { return tipoArma; }
    public int getDadoDanoArma() { return dadoDanoArma; }
    public int getQuantidadeDanoArma() { return quantidadeDanoArma; }
}
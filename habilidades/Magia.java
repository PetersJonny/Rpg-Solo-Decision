package habilidades;

public class Magia extends Habilidade {
    private int quantidadeDano;
    private int dadoDano;

    public Magia(String nome, String descricao, int custoMana) {
        this(nome, descricao, custoMana, 0, 0);
    }

    public Magia(String nome, String descricao, int custoMana, int quantidadeDano, int dadoDano) {
        super(nome, descricao, custoMana);
        this.quantidadeDano = quantidadeDano;
        this.dadoDano = dadoDano;
    }

    public int getQuantidadeDano() { return quantidadeDano; }
    public int getDadoDano() { return dadoDano; }
}
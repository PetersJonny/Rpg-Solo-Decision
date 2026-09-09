package itens;

public class Armadura extends ItemRpg {
    private int bonusDefesa;

    public Armadura(String nome, String descricao, int bonusDefesa, int quantidade) {
        super(nome, descricao, quantidade);
        this.bonusDefesa = bonusDefesa;
    }

    public int getBonusDefesa() { return bonusDefesa; }
}

package itens;

public class Armadura extends ItemRpg {
    private int bonusDefesa;

    public Armadura(String nome, int bonusDefesa, int quantidade) {
        super(nome, quantidade);
        this.bonusDefesa = bonusDefesa;
    }

    public int getBonusDefesa() { return bonusDefesa; }
}

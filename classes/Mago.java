package classes;

import itens.Arma;
import itens.Consumivel;

public class Mago extends ClasseRpg {
    
    public Mago() {
        this.nome = "Mago";
        this.armaPrincipal = new Arma("Cajado", "CaC/mágico", 4, 1, 1);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Consumivel("Poção de Mana", 1));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 10 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 8 + presencaBase; }

    @Override
    public int getBonusConstituicao() { return -2; }

    @Override
    public int getBonusPresenca() { return 2; }

    @Override
    public int getBonusIntelecto() { return 1; }
}

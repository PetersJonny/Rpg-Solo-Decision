package classes;

import itens.Arma;
import itens.Armadura;

public class Guerreiro extends ClasseRpg {
    
    public Guerreiro() {
        this.nome = "Guerreiro";
        this.armaPrincipal = new Arma("Espada", "CaC", 8, 1, 1);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Armadura("Armadura Leve", 3, 1));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 20 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 2 + presencaBase; }

    @Override
    public int getBonusConstituicao() { return 2; }

    @Override
    public int getBonusForca() { return 1; }

    @Override
    public int getBonusIntelecto() { return -2; }
}

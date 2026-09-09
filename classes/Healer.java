package classes;

import itens.Arma;
import itens.Consumivel;

public class Healer extends ClasseRpg {
    
    public Healer() {
        this.nome = "Healer";
        this.armaPrincipal = new Arma("Arco", "LA", 6, 1, 1);
        this.itensIniciais.add(this.armaPrincipal);
        this.itensIniciais.add(new Consumivel("Kit Médico", 1));
        this.itensIniciais.add(new Consumivel("Flechas", 15));
    }

    @Override
    public int calcularVidaBase(int constituicaoBase) { return 14 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 5 + presencaBase; }

    @Override
    public int getBonusSabedoria() { return 1; }

    @Override
    public int getBonusIntelecto() { return 2; }

    @Override
    public int getBonusForca() { return -2; }
}

package classes;

import itens.Arma;

public class Healer extends ClasseRpg {
    
    // Construtor
    public Healer() {
        this.nome = "Healer";
        
        // Healer cria sua arma como um Objeto
        this.armaPrincipal = new Arma("arco", "LA", 6, 1);
        
        // Coloca a arma na bolsa de itens
        this.itensIniciais.add(this.armaPrincipal);
    }

    // Status Base
    @Override
    public int calcularVidaBase(int constituicaoBase) { return 14 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 5 + presencaBase; }

    // Modificadores de Status
    @Override
    public int getBonusSabedoria() { return 1; }

    @Override
    public int getBonusIntelecto() { return 2; }

    @Override
    public int getBonusForca() { return -2; }
}

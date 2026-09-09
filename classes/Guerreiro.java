package classes;

import itens.Arma;

public class Guerreiro extends ClasseRpg {
    
    // Construtor
    public Guerreiro() {
        this.nome = "Guerreiro";
        
        // Guerreiro cria sua arma como um Objeto
        this.armaPrincipal = new Arma("espada", "CaC", 8, 1);
        
        // Coloca a arma na bolsa de itens
        this.itensIniciais.add(this.armaPrincipal);
    }

    // Status Base
    @Override
    public int calcularVidaBase(int constituicaoBase) { return 20 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 2 + presencaBase; }

    // Modificadores de Status
    @Override
    public int getBonusConstituicao() { return 2; }

    @Override
    public int getBonusForca() { return 1; }

    @Override
    public int getBonusIntelecto() { return -2; }
}

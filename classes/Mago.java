package classes;

import itens.Arma;

public class Mago extends ClasseRpg {
    
    // Construtor
    public Mago() {
        this.nome = "Mago";
        
        // Mago cria sua arma como um Objeto
        this.armaPrincipal = new Arma("cajado", "CaC/mágico", 4, 1);
        
        // Coloca a arma na bolsa de itens
        this.itensIniciais.add(this.armaPrincipal);
    }

    // Status Base
    @Override
    public int calcularVidaBase(int constituicaoBase) { return 10 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 8 + presencaBase; }

    // Modificadores de Status
    @Override
    public int getBonusConstituicao() { return -2; }

    @Override
    public int getBonusPresenca() { return 2; }

    @Override
    public int getBonusIntelecto() { return 1; }
}

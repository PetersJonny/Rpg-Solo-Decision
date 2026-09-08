package classes;

public class Guerreiro extends ClasseRpg {
    
    public Guerreiro() {
        this.nome = "Guerreiro";
        this.arma = "espada";
        this.tipoArma = "CaC";
        this.dadoDanoArma = 8;
        this.quantidadeDanoArma = 1;
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

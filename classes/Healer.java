package classes;

public class Healer extends ClasseRpg {
    
    public Healer() {
        this.nome = "Healer";
        this.arma = "arco";
        this.tipoArma = "LA";
        this.dadoDanoArma = 6;
        this.quantidadeDanoArma = 1;
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

package classes;

public class Mago extends ClasseRpg {
    
    // No construtor, o Mago preenche sua própria ficha básica
    public Mago() {
        this.nome = "Mago";
        this.arma = "cajado";
        this.tipoArma = "CaC/mágico";
        this.dadoDanoArma = 4;
        this.quantidadeDanoArma = 1;
    }

    // A vida e mana específicas do Mago
    @Override
    public int calcularVidaBase(int constituicaoBase) { return 10 + constituicaoBase; }

    @Override
    public int calcularManaBase(int presencaBase) { return 8 + presencaBase; }

    // Bônus e penalidades exclusivos do Mago
    @Override
    public int getBonusConstituicao() { return -2; }

    @Override
    public int getBonusPresenca() { return 2; }

    @Override
    public int getBonusIntelecto() { return 1; }
}

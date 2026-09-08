package classes;

public abstract class ClasseRpg {
    // Estas são as características que TODA classe vai ter
    protected String nome;
    protected String arma;
    protected String tipoArma;
    protected int dadoDanoArma;
    protected int quantidadeDanoArma;

    // Métodos para a FichaRpg conseguir ler essas informações
    public String getNome() { return nome; }
    public String getArma() { return arma; }
    public String getTipoArma() { return tipoArma; }
    public int getDadoDanoArma() { return dadoDanoArma; }
    public int getQuantidadeDanoArma() { return quantidadeDanoArma; }

    // Cada classe terá uma matemática diferente para Vida e Mana
    public abstract int calcularVidaBase(int constituicaoBase);
    public abstract int calcularManaBase(int presencaBase);

    // Bônus de status padrão (se a classe não alterar, é 0)
    public int getBonusConstituicao() { return 0; }
    public int getBonusForca() { return 0; }
    public int getBonusDestreza() { return 0; }
    public int getBonusIntelecto() { return 0; }
    public int getBonusPresenca() { return 0; }
    public int getBonusSabedoria() { return 0; }
}

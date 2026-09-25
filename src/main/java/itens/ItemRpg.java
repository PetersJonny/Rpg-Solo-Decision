package itens;

public class ItemRpg implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected String nome;
    protected String descricao;
    protected int quantidade;
    protected double peso;

    public ItemRpg(String nome, String descricao, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.peso = pesoPadraoDoNome(nome);
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getQuantidade() { return quantidade; }

    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    // Categorias de peso por unidade: materiais e flechas 0.1, poções normais e
    // kits 0.3, poções grandes 0.6, armas e itens normais 1, itens pesados 2.
    private static double pesoPadraoDoNome(String nome) {
        switch (nome) {
            case "Couro", "Dente de Urso", "Pó da Fada", "Osso", "Carne Podre",
                 "Flechas", "Madeira", "Folha", "Pedra", "Frutas", "Chifre de Minotauro":
                return 0.1;
            case "Poção de Mana", "Kit Médico":
                return 0.3;
            case "Poção Grande de Mana":
                return 0.6;
            case "Espada Pesada", "Machado de Guerra", "Martelo de Guerra",
                 "Espada do Minotauro", "Armadura Pesada":
                return 2.0;
            default:
                return 1.0;
        }
    }
}

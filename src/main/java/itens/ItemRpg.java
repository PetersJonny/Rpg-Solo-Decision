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

            private static double pesoPadraoDoNome(String nome) {
        switch (nome) {
            case "Couro", "Dente de Urso", "Pó da Fada", "Osso", "Carne Podre",
                 "Carne de Lobo", "Carne de Urso", "Carne de Lobo Cozida", "Carne de Urso Cozida",
                 "Flechas", "Madeira", "Folha", "Pedra", "Frutas", "Chifre de Minotauro",
                 "Maçã", "Pera", "Ameixa", "Uva", "Morango Selvagem", "Figo Seco":
                return 0.1;
            case "Poção de Mana", "Kit Médico":
                return 0.3;
            case "Sopa do Vilarejo", "Pão Quente com Manteiga", "Ovos Mexidos",
                 "Caldo de Lobo", "Peixe Assado", "Estofado de Urso",
                 "Torta de Frutas", "Hidromel":
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

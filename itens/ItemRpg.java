package itens;

public class ItemRpg {
    protected String nome;
    protected int quantidade;

    public ItemRpg(String nome, int quantidade) {
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
    
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}

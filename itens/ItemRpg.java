package itens;

public class ItemRpg {
    protected String nome;
    protected String descricao;
    protected int quantidade;

    public ItemRpg(String nome, String descricao, int quantidade) {
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getQuantidade() { return quantidade; }
    
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}

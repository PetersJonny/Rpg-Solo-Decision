package habilidades;

public class Habilidade {
    protected String nome;
    protected String descricao;
    protected int custoMana;

    public Habilidade(String nome, String descricao, int custoMana) {
        this.nome = nome;
        this.descricao = descricao;
        this.custoMana = custoMana;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getCustoMana() { return custoMana; }
}

package habilidades;

public class Habilidade implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    protected String nome;
    protected String descricao;
    protected int custoMana;
    protected boolean passiva;

    public Habilidade(String nome, String descricao, int custoMana, boolean passiva) {
        this.nome = nome;
        this.descricao = descricao;
        this.custoMana = custoMana;
        this.passiva = passiva;
    }

    public Habilidade(String nome, String descricao, int custoMana) {
        this(nome, descricao, custoMana, false);
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getCustoMana() { return custoMana; }
    public boolean isPassiva() { return passiva; }
    public boolean precisaDeAlvo() { return this instanceof Magia || nome.equals("Prisão") || nome.equals("Cura para a Morte"); }
    public boolean executar(fichas.FichaRpg ficha, java.util.List<criaturas.Criatura> inimigos, int alvoIndex) { return true; }
}

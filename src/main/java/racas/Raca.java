package racas;

import java.io.Serializable;

public abstract class Raca implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nome;
    protected String bonusAtributo;
    protected String bonusDescricao;
    protected String passiva;
    protected String passivaDescricao;

    public Raca(String nome, String bonusAtributo, String bonusDescricao,
                   String passiva, String passivaDescricao) {
        this.nome = nome;
        this.bonusAtributo = bonusAtributo;
        this.bonusDescricao = bonusDescricao;
        this.passiva = passiva;
        this.passivaDescricao = passivaDescricao;
    }

    public String getNome() { return nome; }
    public String getBonusAtributo() { return bonusAtributo; }
    public String getBonusDescricao() { return bonusDescricao; }
    public String getPassiva() { return passiva; }
    public String getPassivaDescricao() { return passivaDescricao; }

    public int getBonusConstituicao() { return 0; }
    public int getBonusDestreza() { return 0; }
    public int getBonusForca() { return 0; }
    public int getBonusSabedoria() { return 0; }
    public int getBonusIntelecto() { return 0; }
    public int getBonusPresenca() { return 0; }

    public int getBonusDefesa() { return 0; }

    public int getBonusVidaMaxima() { return 0; }

    public boolean podeSobreviverCom1AoCair0() { return false; }
    public boolean podeRerrolarTeste() { return false; }
    public boolean dobraChanceEncontrarFada() { return false; }
    public boolean temBonusBuscaRecursos() { return false; }
    public boolean temBonusDanoVidaBaixa() { return false; }
    public boolean temBonusTestesNoturnos() { return false; }
    public boolean reduzCustoMana() { return false; }

    public String getDescricaoCompleta() {
        return "  " + nome + " — " + bonusDescricao
                + "\n     Passiva: " + passiva + " — " + passivaDescricao;
    }
}

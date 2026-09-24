package racas;

// Humano — +1 em um atributo à escolha + Vontade de Viver.
public class HumanoRaca extends Raca {
    private static final long serialVersionUID = 1L;
    private String atributoEscolhido = null; // definido na criação (menu)

    public HumanoRaca() {
        super("Humano", null,
                "+1 em um atributo à sua escolha",
                "Vontade de Viver",
                "Uma vez por dia, ao ser reduzido a 0 de vida, você sobrevive com 1.");
    }
    public HumanoRaca(String atributoEscolhido) {
        this();
        this.atributoEscolhido = atributoEscolhido;
    }
    public void setAtributoEscolhido(String a) { this.atributoEscolhido = a; }
    public String getAtributoEscolhido() { return atributoEscolhido; }

    // +1 racial em um atributo à escolha (definido na criação do personagem).
    // Se nenhum foi escolhido ainda, retorna 1 em Constituição (padrão do menu).
    @Override public int getBonusConstituicao() { return bonusNo("Constituição"); }
    @Override public int getBonusDestreza() { return bonusNo("Destreza"); }
    @Override public int getBonusForca() { return bonusNo("Força"); }
    @Override public int getBonusSabedoria() { return bonusNo("Sabedoria"); }
    @Override public int getBonusIntelecto() { return bonusNo("Intelecto"); }
    @Override public int getBonusPresenca() { return bonusNo("Presença"); }

    private int bonusNo(String atributo) {
        String escolhido = (atributoEscolhido == null || atributoEscolhido.isEmpty()) ? "Constituição" : atributoEscolhido;
        return escolhido.equalsIgnoreCase(atributo) ? 1 : 0;
    }

    @Override public boolean podeSobreviverCom1AoCair0() { return true; }
}

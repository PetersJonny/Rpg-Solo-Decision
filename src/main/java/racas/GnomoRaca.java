package racas;

// Gnomo — +1 em Intelecto + Mente Afiada (rerrole teste).
public class GnomoRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public GnomoRaca() {
        super("Gnomo", "Intelecto",
                "+1 em Intelecto",
                "Mente Afiada",
                "Uma vez por dia, você pode refazer um teste de Intelecto ou Sabedoria que falhou.");
    }
    @Override public int getBonusIntelecto() { return 1; }
    @Override public boolean podeRerrolarTeste() { return true; }
}

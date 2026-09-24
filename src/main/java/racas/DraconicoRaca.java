package racas;

// Dracônico — +1 em Constituição + Escamas de Dragão (defesa/vida).
public class DraconicoRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public DraconicoRaca() {
        super("Dracônico", "Constituição",
                "+1 em Constituição",
                "Escamas de Dragão",
                "+2 de defesa permanente e +2 de vida máxima.");
    }
    @Override public int getBonusConstituicao() { return 1; }
    @Override public int getBonusDefesa() { return 2; }
    @Override public int getBonusVidaMaxima() { return 2; }
}

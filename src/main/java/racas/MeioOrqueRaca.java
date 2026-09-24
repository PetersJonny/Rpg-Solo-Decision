package racas;

// Meio-Orque — +1 em Força + Fúria Sombria (dano com vida baixa).
public class MeioOrqueRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public MeioOrqueRaca() {
        super("Meio-Orque", "Força",
                "+1 em Força",
                "Fúria Sombria",
                "Com 30% ou menos de vida, causa +2 de dano em ataques físicos.");
    }
    @Override public int getBonusForca() { return 1; }
    @Override public boolean temBonusDanoVidaBaixa() { return true; }
}

package racas;

// Meio-Orque — +1 em Força + Fúria Sombria (dado de dano maior com vida baixa).
public class MeioOrqueRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public MeioOrqueRaca() {
        super("Meio-Orque", "Força",
                "+1 em Força",
                "Fúria Sombria",
                "Com 30% ou menos de vida, o dado de dano das suas armas sobe um degrau (1d4→1d6, 1d6→1d8, 1d8→1d10, ...).");
    }
    @Override public int getBonusForca() { return 1; }
    @Override public boolean temBonusDanoVidaBaixa() { return true; }
}

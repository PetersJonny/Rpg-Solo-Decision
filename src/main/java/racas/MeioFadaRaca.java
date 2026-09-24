package racas;

// Meio-Fada — +1 em Presença + Encanto Feérico (dois encontros com a Fada).
public class MeioFadaRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public MeioFadaRaca() {
        super("Meio-Fada", "Presença",
                "+1 em Presença",
                "Encanto Feérico",
                "Dobra a chance de encontrar a Fada ao explorar.");
    }
    @Override public int getBonusPresenca() { return 1; }
    @Override public boolean dobraChanceEncontrarFada() { return true; }
}

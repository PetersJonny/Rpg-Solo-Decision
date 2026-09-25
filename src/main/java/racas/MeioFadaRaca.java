package racas;

// Meio-Fada — +1 em Presença + Encanto Feérico (dobra a chance de encontrar a
// Fada ao explorar e reduz em 1 o custo de mana de magias e habilidades).
public class MeioFadaRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public MeioFadaRaca() {
        super("Meio-Fada", "Presença",
                "+1 em Presença",
                "Encanto Feérico",
                "Dobra a chance de encontrar a Fada ao explorar e reduz em 1 o custo de mana de magias e habilidades (nunca abaixo de 1).");
    }
    @Override public int getBonusPresenca() { return 1; }
    @Override public boolean dobraChanceEncontrarFada() { return true; }
    @Override public boolean reduzCustoMana() { return true; }
}

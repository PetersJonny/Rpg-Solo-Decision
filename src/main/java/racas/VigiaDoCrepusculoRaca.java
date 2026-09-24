package racas;

// Vigia do Crepúsculo — +1 em Sabedoria + Visão na Penumbra (testes à noite).
public class VigiaDoCrepusculoRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public VigiaDoCrepusculoRaca() {
        super("Vigia do Crepúsculo", "Sabedoria",
                "+1 em Sabedoria",
                "Visão na Penumbra",
                "+2 em todos os testes durante a noite.");
    }
    @Override public int getBonusSabedoria() { return 1; }
    @Override public boolean temBonusTestesNoturnos() { return true; }
}

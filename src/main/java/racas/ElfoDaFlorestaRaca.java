package racas;

// Elfo da Floresta — +1 em Destreza + Toque da Mata (materiais).
public class ElfoDaFlorestaRaca extends Raca {
    private static final long serialVersionUID = 1L;

    public ElfoDaFlorestaRaca() {
        super("Elfo da Floresta", "Destreza",
                "+1 em Destreza",
                "Toque da Mata",
                "30% de chance de ganhar 1 material extra ao buscar recursos.");
    }
    @Override public int getBonusDestreza() { return 1; }
    @Override public boolean temBonusBuscaRecursos() { return true; }
}

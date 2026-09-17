package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;
import mecanicas.MotorDeCombate;

public class ComandoUsarItem implements ComandoCombate {
    private int itemIndex;

    public ComandoUsarItem(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    @Override
    public boolean isPrioritarioFuga() { return false; }

    @Override
    public int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        MotorDeCombate.usarItemNaVez(ficha, itemIndex);
        return 1;
    }
}

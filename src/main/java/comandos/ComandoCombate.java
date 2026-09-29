package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;

public interface ComandoCombate {

    boolean isPrioritarioFuga();

    int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada);
}

package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;
import mecanicas.MotorDeCombate;

public class ComandoFuga implements ComandoCombate {
    
    @Override
    public boolean isPrioritarioFuga() { return true; }

    @Override
    public int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        return MotorDeCombate.TentarFugirNaVez(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada);
    }
}

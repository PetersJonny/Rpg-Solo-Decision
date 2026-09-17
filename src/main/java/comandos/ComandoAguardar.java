package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;
import telas.Interface;

public class ComandoAguardar implements ComandoCombate {
    
    @Override
    public boolean isPrioritarioFuga() { return false; }

    @Override
    public int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        Interface.MostrarMensagem("\nVocê aguarda, mantendo a guarda.");
        Interface.Pausa(1500);
        return 1;
    }
}

package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;

public interface ComandoCombate {
    /**
     * Retorna se este comando é uma tentativa de fuga (pois foge da regra de iniciativa)
     */
    boolean isPrioritarioFuga();

    /**
     * Executa a ação declarada.
     * @return Para fuga: 0 (escapou), 1 (progrediu), -1 (falhou). Para ações normais: sempre retorna 1.
     */
    int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada);
}

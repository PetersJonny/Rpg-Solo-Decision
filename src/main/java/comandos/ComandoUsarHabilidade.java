package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;
import mecanicas.MotorDeCombate;

public class ComandoUsarHabilidade implements ComandoCombate {
    private int alvoIndex;
    private int habIndex;

    public ComandoUsarHabilidade(int alvoIndex, int habIndex) {
        this.alvoIndex = alvoIndex;
        this.habIndex = habIndex;
    }

    @Override
    public boolean isPrioritarioFuga() { return false; }

    @Override
    public int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        MotorDeCombate.executarAcaoJogador(ficha, inimigos, 2, alvoIndex, -1, habIndex);
        return 1;
    }
}

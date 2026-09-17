package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;
import mecanicas.MotorDeCombate;

public class ComandoAtacarComArma implements ComandoCombate {
    private int alvoIndex;
    private int armaIndex;

    public ComandoAtacarComArma(int alvoIndex, int armaIndex) {
        this.alvoIndex = alvoIndex;
        this.armaIndex = armaIndex;
    }

    @Override
    public boolean isPrioritarioFuga() { return false; }

    @Override
    public int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        MotorDeCombate.executarAcaoJogador(ficha, inimigos, 1, alvoIndex, armaIndex, -1);
        return 1;
    }
}

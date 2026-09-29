package habilidades.ativas;

import habilidades.Habilidade;
import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;

public class HabilidadeCurandeiroCombatente extends Habilidade {
    public HabilidadeCurandeiroCombatente() {
        super("Curandeiro Combatente",
                "Suas feridas cicatrizadas fizeram de você um forte combatente: ao atacar, cada 1 de mana gasto "
                        + "(máximo = seu nível) executa um ataque extra, e todo ataque que acertar cura metade do dano causado.",
                0);
    }

    @Override
    public boolean isPassiva() {
        return true;
    }

    @Override
    public boolean executar(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        return true;
    }
}

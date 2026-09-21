package habilidades.ativas;

import habilidades.Habilidade;
import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import mecanicas.MotorDeCombate;

public class HabilidadePactoMortal extends Habilidade {
    public HabilidadePactoMortal() {
        super("Pacto Mortal", "Amaldiçoa uma criatura e uma parte de você: ela fica enfraquecida (-2 nas rolagens e +5 de dano demoníaco) até o fim do combate, mas TODOS os danos que você receber aumentam em +3 enquanto durar a maldição.", 4);
    }

    @Override
    public boolean precisaDeAlvo() {
        return true;
    }

    @Override
    public boolean executar(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        return MotorDeCombate.usarPactoMortal(ficha, inimigos, alvoIndex, this);
    }
}
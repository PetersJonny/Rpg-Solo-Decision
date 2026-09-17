package habilidades.ativas;

import habilidades.Habilidade;
import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import mecanicas.MotorDeCombate;

public class HabilidadeCuraAbsoluta extends Habilidade {
    public HabilidadeCuraAbsoluta(String nome, String descricao, int custoMana) {
        super(nome, descricao, custoMana);
    }
    
    @Override
    public boolean executar(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        return MotorDeCombate.executarCuraAbsoluta(ficha, this);
    }
}

package habilidades.ativas;

import mecanicas.GerenciadorDeHabilidades;

import habilidades.Habilidade;
import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import mecanicas.MotorDeCombate;

public class HabilidadePrisao extends Habilidade {
    public HabilidadePrisao(String nome, String descricao, int custoMana) {
        super(nome, descricao, custoMana);
    }

    @Override
    public boolean executar(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        return GerenciadorDeHabilidades.usarPrisao(ficha, inimigos, alvoIndex, this);
    }
}

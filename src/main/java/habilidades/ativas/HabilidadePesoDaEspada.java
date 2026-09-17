package habilidades.ativas;

import habilidades.Magia;
import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import mecanicas.MotorDeCombate;

public class HabilidadePesoDaEspada extends Magia {
    public HabilidadePesoDaEspada(String nome, String descricao, int custoMana, int quantidadeDano, int dadoDano) {
        super(nome, descricao, custoMana, quantidadeDano, dadoDano);
    }
}

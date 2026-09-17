package habilidades.ativas;

import habilidades.Habilidade;
import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import telas.Interface;

public class HabilidadeCuraTotal extends Habilidade {
    public HabilidadeCuraTotal(String nome, String descricao, int custoMana) {
        super(nome, descricao, custoMana);
    }
    @Override
    public boolean executar(FichaRpg ficha, List<Criatura> inimigos, int alvoIndex) {
        Interface.MostrarMensagem("Cura Total só pode ser usada para reviver quem morreu em combate.");
        Interface.Pausa(1500);
        return true;
    }
}

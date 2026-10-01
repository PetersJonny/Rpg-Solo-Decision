package comandos;

import fichas.FichaRpg;
import criaturas.Criatura;
import java.util.List;
import java.util.Set;
import telas.Interface;

public class ComandoAplicarVeneno implements ComandoCombate {
    private int itemIndex;

    public ComandoAplicarVeneno(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    @Override
    public boolean isPrioritarioFuga() { return false; }

    @Override
    public int executar(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        if (ficha.isArmaEnvenenada()) {
            Interface.MostrarMensagem("\nSua arma já está untada de veneno. Use-a no próximo ataque!");
            Interface.Pausa(1500);
            return 0;
        }
        if (!ficha.removerItem("Gota de Veneno", 1)) {
            Interface.MostrarMensagem("\nVocê não tem mais Gota de Veneno.");
            Interface.Pausa(1500);
            return 0;
        }

        ficha.setArmaEnvenenada(true);
        Interface.MostrarMensagem("\nVocê abre a Gota de Veneno e espalha o caldo negro pela lâmina, com calma. A arma fica pronta.");
        Interface.MostrarMensagem("Esta rodada inteira foi gasta nisso. " + Interface.AMARELO + "Seu próximo ataque que acertar envenena o alvo (1d4 por rodada até o fim do combate)." + Interface.RESET);
        Interface.Pausa(2500);
        return 1;
    }
}
package mecanicas;

import java.util.Random;
import fichas.FichaRpg;
import itens.Arma;

public class MecanicasRpg {
    // Randomizador
    private static final Random random = new Random();

    // Rolar Dado Genérico
    public static int rolarDado(int lados) {
        return random.nextInt(lados) + 1;
    }

    // Rolar Iniciativa
    public static int rolarIniciativa(FichaRpg ficha) {
        int resultadoDado = rolarDado(20);
        int total = resultadoDado + ficha.getDestreza();
        return total;
    }

    // Cálculo de Dano Físico
    public static int rolarDanoFisico(FichaRpg ficha, Arma armaUsada) {
        int danoBase = 0;
        for (int i = 0; i < armaUsada.getQuantidadeDanoArma(); i++) {
            danoBase += rolarDado(armaUsada.getDadoDanoArma());
        }

        if (armaUsada.getTipoArma().contains("CaC")) {
            danoBase += ficha.getForca();
        }

        return danoBase;
    }
}

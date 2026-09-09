package mecanicas;

import fichas.FichaRpg;
import java.util.Random;

public class MecanicasRpg {
    // Randomizador
    private static final Random random = new Random();

    // Rolar Dado Genérico
    public static int rolarDado(int lados) {
        return random.nextInt(lados);
    }

    // Rolar Iniciativa
    public static int rolarIniciativa(FichaRpg ficha) {
        int resultadoDado = rolarDado(20);
        int total = resultadoDado + ficha.getDestreza();
        return total;
    }
}

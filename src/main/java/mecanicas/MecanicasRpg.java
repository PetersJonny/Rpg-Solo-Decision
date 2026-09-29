package mecanicas;

import java.util.Random;
import fichas.FichaRpg;
import itens.Arma;

public class MecanicasRpg {
        private static final Random random = new Random();

        public static int rolarDado(int lados) {
        return random.nextInt(lados) + 1;
    }

        public static int rolarEntre(int minimo, int maximo) {
        return minimo + random.nextInt(maximo - minimo + 1);
    }

        public static int rolarIniciativa(FichaRpg ficha) {
        int resultadoDado = rolarDado(20);
        int total = resultadoDado + ficha.getDestreza();
        return total;
    }

        public static int rolarDanoFisico(FichaRpg ficha, Arma armaUsada) {
        int danoBase = 0;
        for (int i = 0; i < armaUsada.getQuantidadeDanoArma(); i++) {
            danoBase += rolarDado(armaUsada.getDadoDanoArma());
        }

        String atributo = armaUsada.getAtributoAtaque();
        danoBase += atributo.equals("Destreza") ? ficha.getDestreza() : ficha.getForca();

        return danoBase;
    }
}

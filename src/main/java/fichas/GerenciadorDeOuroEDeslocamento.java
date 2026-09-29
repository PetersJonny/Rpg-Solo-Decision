package fichas;

import classes.ClasseRpg;
import criaturas.Criatura;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import racas.Raca;
import telas.Interface;

public class GerenciadorDeOuroEDeslocamento {

    public static void adicionarOuro(FichaRpg ficha, int quantidade) {

        if (quantidade <= 0) return;
        if (Integer.MAX_VALUE - ficha.ouro < quantidade) {
            ficha.ouro = Integer.MAX_VALUE;
        } else {
            ficha.ouro += quantidade;
        }

    }
    public static boolean gastarOuro(FichaRpg ficha, int quantidade) {

        if (quantidade < 0 || ficha.ouro < quantidade) {
            return false;
        }
        ficha.ouro -= quantidade;
        return true;

    }
    public static void adicionarProfundidade(FichaRpg ficha, int unidades) {

        ficha.profundidadeFloresta = Math.min(FichaRpg.PROFUNDIDADE_PARA_SAIR, ficha.profundidadeFloresta + Math.max(0, unidades));
        ficha.sincronizarLocalizacao();

    }
    public static void reduzirProfundidade(FichaRpg ficha, int unidades) {

        ficha.profundidadeFloresta = Math.max(0, ficha.profundidadeFloresta - Math.max(0, unidades));
        ficha.sincronizarLocalizacao();

    }
}

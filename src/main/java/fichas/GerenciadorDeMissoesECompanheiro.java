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

public class GerenciadorDeMissoesECompanheiro {

    public static void aceitarMissao(FichaRpg ficha, String nome) {

        if (!ficha.missoesAceitas.contains(nome)) ficha.missoesAceitas.add(nome);

    }
    public static boolean companheiroQuerPartir(FichaRpg ficha) {
 return ficha.companheiro != null && ficha.companheiro.isPartindo();
    }
    public static void registrarDormidaDoCompanheiro(FichaRpg ficha) {

        if (ficha.companheiro != null) {
            ficha.companheiro.aoDormir();
        }

    }
    public static void removerCompanheiro(FichaRpg ficha) {
 ficha.companheiro = null;
    }
}

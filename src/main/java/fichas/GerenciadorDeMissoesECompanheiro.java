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

    private static final String AMARELO = Interface.AMARELO;
    private static final String VERDE = Interface.VERDE;
    private static final String CIANO = Interface.CIANO;
    private static final String RESET = Interface.RESET;

    public static void registrarNovidade(FichaRpg ficha, String missao, String texto) {
        if (!ficha.isMissaoAceita(missao)) return;
        String chave = "!" + texto;
        for (String existente : ficha.getNovidades(missao)) {
            if (existente.equals(chave) || existente.equals(texto)) return;
        }
        ficha.adicionarNovidade(missao, chave);
        Interface.MostrarMensagem("\n" + VERDE + "◆ NOVA INFORMAÇÃO" + RESET + " (" + AMARELO + missao + RESET + ")");
        Interface.MostrarMensagem("  " + texto);
        Interface.Pausa(2400);
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

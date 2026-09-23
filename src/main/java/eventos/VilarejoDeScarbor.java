package eventos;

import fichas.FichaRpg;
import telas.Interface;

public class VilarejoDeScarbor {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;

    // Por enquanto o vilarejo ainda não tem conteúdo: olhar em volta é só um passeio
    // pelos arredores, sem nenhum acontecimento.
    public static void OlharEmVolta(FichaRpg ficha) {
        Interface.cabecalhoMenu("OLHAR EM VOLTA");
        Interface.MostrarMensagem("\nVocê percorre as ruas do " + CIANO + ficha.getCidadeAtual() + RESET + ", observando as casas e as pessoas.");
        Interface.MostrarMensagem("Tudo parece tranquilo e pacato por aqui. Ainda não há nada de interessante para descobrir no vilarejo.");
        Interface.Pausa(2500);
    }
}
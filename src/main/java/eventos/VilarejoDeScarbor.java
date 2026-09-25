package eventos;

import fichas.FichaRpg;
import telas.Interface;

public class VilarejoDeScarbor {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERMELHO = Interface.VERMELHO;

    // Cena de chegada à cidade: uma vila dominada por dracônicos, onde todos seguem
    // seus afazeres normalmente, e ao longe uma taverna em plena confusão.
    public static void ObservarCidade(FichaRpg ficha) {
        Interface.MostrarMensagem("\nVocê atravessa a entrada e é recebido por uma visão que o faz parar no meio do caminho.");
        Interface.Pausa(2000);

        boolean draconico = ficha.getRaca() != null && ficha.getRaca().getNome().equals("Dracônico");
        if (draconico) {
            Interface.MostrarMensagem("Por todos os lados, " + CIANO + "pessoas iguais a você" + RESET + ": " + CIANO + "escamas sobre a pele, olhos de réptil, chifres e caudas grossas" + RESET + ". Você está entre os seus — aqui, os " + CIANO + "dracônicos" + RESET + " são maioria absoluta.");
        } else {
            Interface.MostrarMensagem("Por todos os lados, pessoas — mas nenhuma igual a você. " + CIANO + "Escamas sobre a pele, olhos de réptil, chifres e caudas grossas" + RESET + ". Aqui, os " + CIANO + "dracônicos" + RESET + " são maioria absoluta.");
        }
        Interface.Pausa(2200);
        Interface.MostrarMensagem("Mesmo assim, ninguém parece se importar com a sua presença: ferreiros batem o martelo, comerciantes gritam seus preços, crianças correm entre as pernas dos adultos. Cada um segue no seu próprio afazer, como se o mundo girasse normalmente.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("É então que, mais adiante, um barulho chama sua atenção. " + AMARELO + "Uma taverna" + RESET + " — e dela vêm " + VERMELHO + "gritos, arremessos e o som de algo quebrando" + RESET + ". Confusão, discussão, gente brigando.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\nDa porta aberta, você vê corpos se atracando lá dentro, enquanto alguns dracônicos saem correndo e outros apenas assistem em volta.");
        Interface.Pausa(1800);

        System.out.println("\n  O que você faz?");
        System.out.println("  1. Ir até a taverna ver o que está acontecendo");
        System.out.println("  2. Deixar para depois e seguir seu caminho");
        System.out.println("\n  " + Interface.VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            Interface.MostrarMensagem("\nVocê respira fundo e cruza as ruas em direção à taverna, de onde os gritos só aumentam...");
            Interface.Pausa(2500);
        } else {
            Interface.MostrarMensagem("\nVocê desvia o olhar da taverna e segue seu caminho, deixando a confusão para trás.");
            Interface.Pausa(2000);
        }
    }

    // Por enquanto o vilarejo ainda não tem conteúdo: olhar em volta é só um passeio
    // pelos arredores, sem nenhum acontecimento.
    public static void OlharEmVolta(FichaRpg ficha) {
        Interface.cabecalhoMenu("OLHAR EM VOLTA");
        Interface.MostrarMensagem("\nVocê percorre as ruas do " + CIANO + ficha.getCidadeAtual() + RESET + ", observando as casas e as pessoas.");
        Interface.MostrarMensagem("Tudo parece tranquilo e pacato por aqui. Ainda não há nada de interessante para descobrir no vilarejo.");
        Interface.Pausa(2500);
    }
}
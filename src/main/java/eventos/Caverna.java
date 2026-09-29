package eventos;

import fichas.FichaRpg;
import telas.Interface;

public class Caverna {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERMELHO = Interface.VERMELHO;

    public static void IrParaCaverna(FichaRpg ficha) {
        Interface.cabecalhoMenu("CAMINHO PARA A CAVERNA");
        Interface.MostrarMensagem("\nVocê cruza a vila em direção ao fundo dela, deixando as casas para trás.");
        Interface.Pausa(1800);
        Interface.MostrarMensagem("Dracônicos seguem seus afazeres pelo caminho: uns carregam fardos, outros trocam palavras na porta das lojas, e crianças correm entre as pernas dos adultos. O mesmo de sempre.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("Aos poucos, as ruas vão se esvaziando e a vila vai ficando para trás. Um caminho de terra segue entre os barrancos, em direção às " + CIANO + "partes finais do fundo da vila" + RESET + ".");
        Interface.Pausa(2200);

        if (!ficha.isVelhinhaEncontrada()) {
            CenaDaVelhinha(ficha);
            if (ficha.getVidaPersonagem() <= 0) return;
        }

        CenaDaCaverna(ficha);
    }

        private static void CenaDaVelhinha(FichaRpg ficha) {
        Interface.MostrarMensagem("\nNo meio do caminho, uma figura surge na estrada. Uma " + AMARELO + "velha humana" + RESET + " — rosto enrugado, xale surrado sobre os ombros — caminha em sua direção com passos curtos e apressados.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem(CIANO + "\"Boa tarde, querido. Pode me ajudar um minutinho?\"" + RESET);
        Interface.Pausa(1600);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Parar e escutá-la");
        System.out.println("  2. Perguntar o que houve");
        System.out.println("  3. Ignorar e seguir para a caverna");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(3);

        if (escolha == 3) {
            Interface.MostrarMensagem("\nVocê acena com a cabeça, sem parar, e segue adiante. A velhinha fica parada no caminho, observando você ir.");
            Interface.Pausa(1800);
            ficha.setVelhinhaEncontrada(true);
            return;
        }

        Interface.MostrarMensagem("\nA velhinha junta as mãos, com os olhos úmidos: " + AMARELO + "\"Vejo que parece um aventureiro. Minha pequena netinha sumiu... Por favor, ache ela. Eu sinto tanto a falta dela.\"" + RESET);
        Interface.Pausa(2400);

        System.out.println("\n  O que você responde?\n");
        System.out.println("  1. Você vai ajudar a senhora");
        System.out.println("  2. Está ocupado no momento e não pode");
        System.out.println("  3. Só ignorar e seguir para a caverna");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int resposta = Interface.lerOpcao(3);

        if (resposta == 1) {
            ficha.aceitarMissao("A Neta Perdida");
            Interface.MostrarMensagem("\n\"Claro que ajudo\", você diz. O rosto da velhinha se ilumina.");
            Interface.Pausa(1600);
            Interface.MostrarMensagem("\n\"Graças a você!\" — ela enxuga os olhos. \"Ela sumiu enquanto colhiamos frutas na floresta, ali perto da entrada da vila, para fazer uma torta. Eu já procurei por toda parte e não sei mais o que fazer. Estou desesperada...\"");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\"Nem os guardas do vilarejo estão fazendo algo: estão todos ocupados com o sumiço de outra criança. E como a outra é " + AMARELO + "dracônica" + RESET + " — mesmo que ninguém diga em voz alta — eles a colocam como prioridade.\"");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\"Foi bem na entrada da vila, praticamente. Eu fico na " + CIANO + "lojinha de frutas" + RESET + " ali por perto\", ela diz, apontando para as barracas.");
            Interface.Pausa(2200);
            Interface.MostrarMensagem("\"Assim que puder, vou procurá-la\", você promete.");
            Interface.Pausa(1500);
            Interface.MostrarMensagem("\nA velhinha agradece de novo e se despede, seguindo na direção da barraca de frutas. A barraca agora aparece no menu da vila.");
            Interface.Pausa(2200);
        } else if (resposta == 2) {
            Interface.MostrarMensagem("\n\"Sinto muito, senhora, mas não posso agora. Estou ocupado no momento.\"");
            Interface.Pausa(1600);
            Interface.MostrarMensagem("A velhinha baixa os olhos, mas ainda agradece: \"Compreendo, querido. Que os deuses te guardem no seu caminho.\" — e segue seu caminho, devagar.");
            Interface.Pausa(2200);
        } else {
            Interface.MostrarMensagem("\nVocê desvia o olhar e segue para a caverna, deixando a velhinha falar sozinha na estrada.");
            Interface.Pausa(1800);
        }
        ficha.setVelhinhaEncontrada(true);
    }

        private static void CenaDaCaverna(FichaRpg ficha) {
        Interface.MostrarMensagem("\nO caminho de terra termina num barranco alto de pedra escura. Ali, aberta no fundo da vila, " + VERMELHO + "a caverna" + RESET + " engole a luz do dia.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("Da boca da caverna escorre um ar frio e úmido, e o escuro lá dentro é absoluto. É aqui que a filha da moça da alfaiataria foi vista pela última vez.");
        Interface.Pausa(2200);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Espreitar a entrada");
        System.out.println("  2. Voltar para a vila");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        if (Interface.lerOpcao(2) == 2) {
            Interface.MostrarMensagem("\nVocê decide não se aprofundar agora e retorna para a vila.");
            Interface.Pausa(1500);
            return;
        }

        Interface.MostrarMensagem("\nVocê se aproxima da entrada e espreita o interior. Pedras soltas se amontoam no chão, e o escuro se estende sem fim. Sem uma fonte de luz, é impossível enxergar além de alguns passos.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("Por enquanto, você volta para a vila para se preparar antes de se aventurar a fundo.");
        Interface.Pausa(1800);
    }
}

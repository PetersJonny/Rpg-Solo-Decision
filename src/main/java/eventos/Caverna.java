package eventos;

import fichas.FichaRpg;
import fichas.GerenciadorDeInventarioFicha;
import fichas.GerenciadorDeMissoesECompanheiro;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
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
            ficha.setDiaAceitouNeta(ficha.getDiaAtual());
            Interface.MostrarMensagem("\n\"Claro que ajudo\", você diz. O rosto da velhinha se ilumina.");
            Interface.Pausa(1600);
            Interface.MostrarMensagem("\n\"Graças a você!\" — ela enxuga os olhos. \"Ela sumiu enquanto colhiamos frutas na floresta, ali perto da entrada da vila, para fazer uma torta. Eu já procurei por toda parte e não sei mais o que fazer. Estou desesperada...\"");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\"Nem os guardas do vilarejo estão fazendo algo: estão todos ocupados com o sumiço de outra criança. E como a outra é " + AMARELO + "dracônica" + RESET + " — mesmo que ninguém diga em voz alta — eles a colocam como prioridade.\"");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\"Foi bem na entrada da vila, praticamente. Eu fico na " + CIANO + "lojinha de frutas" + RESET + " ali por perto\", ela diz, apontando para as barracas.");
            Interface.Pausa(2200);
            Interface.MostrarMensagem("\"Assim que puder, vou procurá-la\", você promete.");
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Neta Perdida", "A netinha sumiu na mata da entrada da vila, quase em frente à lojinha de frutas, onde elas colhiam frutas para uma torta.");
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

        private static boolean testePresenca(FichaRpg ficha, int dificuldade, String rotulo) {
        Interface.pressionarParaTeste("Presença (" + rotulo + ")");
        int dado = MecanicasRpg.rolarDado(20);
        int atributo = ficha.getPresencaTeste();
        int total = dado + atributo;
        Interface.MostrarMensagem("-> Presença: " + dado + " (Dado) + " + atributo + " (Atributo) = " + total + " (Dificuldade: " + dificuldade + ")");
        Interface.Pausa(2500);
        return total >= dificuldade;
    }

            public static int penalidadeEscuridao(FichaRpg ficha) {
        if (!ficha.isDentroDaCaverna()) return 0;
        return (ficha.isTochaNaMao() && ficha.temItem("Tocha")) ? 0 : -2;
    }

            private static void EntradaDaMina(FichaRpg ficha) {
        Interface.MostrarMensagem("\nO caminho de terra termina num paredão de pedra escura, e ali a " + AMARELO + "montanha" + RESET + " se ergue à frente. Encostado no paredão, um emaranhado de " + CIANO + "pedras empilhadas" + RESET + " desenha o contorno de uma parede sólida — e, no meio dela, um vão estreito e escuro, como se alguém tivesse fechado a boca de um túnel com um montão de Entulho.");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("A configuração não deixa dúvida: aquilo é a " + VERMELHO + "entrada de uma mina" + RESET + ", descendo na direção da montanha. É aqui que a filha da moça da alfaiataria foi vista pela última vez.");
        Interface.Pausa(2400);

        boolean viuGoblin = ficha.isGoblinVistoNaMina();
        if (!viuGoblin) {
            viuGoblin = testePresenca(ficha, 12, " notar algo na entrada");
        }
        if (viuGoblin) {
            ficha.setGoblinVistoNaMina(true);
            boolean draconico = ficha.getRaca() != null && ficha.getRaca().getNome().equals("Dracônico");
            if (draconico) {
                Interface.MostrarMensagem("\nSua atenção atrai um " + VERDE + "pequeno goblin" + RESET + " encostado no lado de fora do vão. Ele te vê, e o corpo inteiro dele " + VERMELHO + "se encolhe" + RESET + " — para um dracônico, ele não quer nem estar perto. Solta um guincho curto e some correndo para dentro da mina.");
            } else {
                Interface.MostrarMensagem("\nSua atenção atrai um " + VERDE + "pequeno goblin" + RESET + " encostado no lado de fora do vão. Ele te vê, dá um pulo para trás e " + VERMELHO + "corre para dentro da mina" + RESET + ", batendo os calcanhares nas pedras até a escuridão engoli-lo.");
            }
            Interface.Pausa(2600);
            if (ficha.isMissaoAceita("A Filha Perdida")) {
                GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "Um goblin foi visto entrando e saindo da boca da mina. Eles se escondem lá dentro.");
            }
        } else {
            Interface.MostrarMensagem("\nVocê examina a entrada por um bom tempo. Não se mexe nada ali dentro — só as pedras, o vão escuro e o silêncio da montanha.");
            Interface.Pausa(2200);
        }
    }

            private static void OlharEmVoltaNaEntrada(FichaRpg ficha) {
        Interface.MostrarMensagem("\nVocê não entra. Em vez disso, se agacha e examina os arredores: as pedras empilhadas, o chão batendo em volta do vão, as frestas de sombra nas laterais.");
        Interface.Pausa(2400);

        if (ficha.isTochaVistaNaMina()) {
            Interface.MostrarMensagem("\nNão tem mais nada por aqui. A tocha que você achou continua onde você a deixou.");
            Interface.Pausa(1800);
            return;
        }

        if (!testePresenca(ficha, 7, " revistar os arredores")) {
            Interface.MostrarMensagem("\nVocê vasculha por um tempo e não encontra nada além de entulho e poeira.");
            Interface.Pausa(2000);
            return;
        }

        Interface.MostrarMensagem("\nSeu olhar pega algo entre as pedras: uma " + AMARELO + "tocha" + RESET + " — toco de madeira envolto em trapo e respingos de resina seca. Deve ter caído de alguém que entrou antes.");
        Interface.Pausa(2400);
        ficha.setTochaVistaNaMina(true);
        GerenciadorDeInventarioFicha.coletarItemEncontrado(ficha, new ItemRpg("Tocha", "Um toco de madeira envolto em trapo e respingos de resina seca. Ilumina o escuro.", 1), "Você encontra");
        if (ficha.temItem("Tocha")) {
            ficha.setTochaNaMao(true);
            Interface.MostrarMensagem("\nVocê acende a tocha e a guarda na mão. A chama ilumina o vão escuro da mina.");
            Interface.Pausa(2000);
        }
    }

            private static void CenaDaCaverna(FichaRpg ficha) {
        EntradaDaMina(ficha);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Olhar em volta da entrada");
        System.out.println("  2. Entrar na mina");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            OlharEmVoltaNaEntrada(ficha);
            System.out.println("\n  O que você faz agora?\n");
            System.out.println("  1. Voltar para a vila");
            System.out.println("  2. Entrar na mina");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            escolha = Interface.lerOpcao(2) == 2 ? 2 : 1;
        }

        if (escolha != 2) {
            Interface.MostrarMensagem("\nVocê se afasta da boca da mina e refaz o caminho de terra de volta, até as ruas da vila.");
            Interface.Pausa(1800);
            return;
        }

        ficha.setDentroDaCaverna(true);
        if (ficha.isTochaNaMao()) {
            Interface.MostrarMensagem("\nVocê atravessa o vão e desce. A tocha na sua mão derrama luz nas paredes");
            Interface.MostrarMensagem("e afasta de você a " + VERMELHO + "penalidade de escuridão" + RESET + ": os testes lá dentro saem sem o -2 enquanto ela estiver na sua mão.");
        } else {
            Interface.MostrarMensagem("\nVocê atravessa o vão e desce. Sem nenhuma luz, o escuro fecha-se em volta de você como água: a partir daqui, todo teste leva " + VERMELHO + "-2" + RESET + " até você achar uma fonte de luz.");
        }
        Interface.Pausa(2400);
        if (ficha.isMissaoAceita("A Filha Perdida")) {
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "Você entrou na mina. Ela é escura e profunda: sem uma fonte de luz, todo teste leva -2.");
        }
        Interface.MostrarMensagem("Por enquanto, você não vai além — volta para a vila para se preparar antes de se aventurar mais fundo.");
        Interface.Pausa(1800);
        ficha.setDentroDaCaverna(false);
    }
}

package eventos;

import criaturas.Criatura;
import criaturas.CriaturaFactory;
import fichas.FichaRpg;
import fichas.GerenciadorDeInventarioFicha;
import fichas.GerenciadorDeMissoesECompanheiro;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import mecanicas.MotorDeCombate;
import telas.Interface;

public class Caverna {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERMELHO = Interface.VERMELHO;

    public static void IrParaCaverna(FichaRpg ficha) {
        Interface.cabecalhoMenu("CAMINHO PARA A CAVERNA");

        if (ficha.isMinaFechadaParaReforma()) {
            Interface.MostrarMensagem("\nVocê cruza a vila até o fundo dela, onde o caminho de terra desce entre os barrancos. É só aí que você percebe que alguma coisa mudou.");
            Interface.Pausa(2000);
            Interface.MostrarMensagem("\nA " + VERMELHO + "boca da mina" + RESET + " está fechada. Tábuas novas, cruzadas e pregadas na frente, com uma corda de obra esticada na altura do peito. Dois guardas de plantão se apoiaram ali de braços cruzados, e nenhum dos dois olha para você.");
            Interface.Pausa(2400);
            Interface.MostrarMensagem("\nUm aviso pregado na tábua do lado, com letra apressada: " + VERMELHO + "\"FECHADA PARA REFORMA. NÃO ENTRAR.\"" + RESET);
            Interface.Pausa(2400);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "A boca da mina segue fechada para reforma, com tábuas novas e guarda no portão. Não entra.");
            Interface.MostrarMensagem("\nVocê fica parado um tempo na frente da corda de obra, sem nada para fazer ali, e depois volta pelo mesmo caminho.");
            Interface.Pausa(2200);
            return;
        }

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
        Interface.Pausa(1800);

        InteriorDaMina(ficha);
    }

            private static void InteriorDaMina(FichaRpg ficha) {
        DescreverEscuridaoProfunda(ficha);
        DescreverTrilhaECristais(ficha);

        boolean sentiu = ficha.isPresencaSentidaNaMina();
        if (!sentiu) {
            if (testePresenca(ficha, 10, " sentir algo à frente")) {
                sentiu = true;
                ficha.setPresencaSentidaNaMina(true);
                Interface.MostrarMensagem("\nVocê para. Não é um som: é um " + VERMELHO + "peso" + RESET + " no ar, e um cheiro leve, de ferrugem e de suor frio. Alguém está ali adiante. Você não sabe quem, nem o que quer, mas sabe que não está sozinho.");
                Interface.Pausa(2600);
            } else {
                Interface.MostrarMensagem("\nVocê para e escuta. Só a sua respiração, o gotejar em algum lugar, e nada mais. Você não tem certeza do que ouviu — talvez nada, talvez algo que você não percebeu. O que quer que fosse, não está mais perto.");
                Interface.Pausa(2400);
            }
        } else {
            Interface.MostrarMensagem("\nVocê lembra do que sentiu antes. O peso no ar continua adiante, e agora um pouco mais perto.");
            Interface.Pausa(2000);
        }

        if (sentiu) {
            System.out.println("\n  O que você faz?\n");
            System.out.println("  1. Seguir em frente, na direção da presença");
            System.out.println("  2. Sair da mina agora");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            if (Interface.lerOpcao(2) == 2) {
                VoltarDaMina(ficha, "\nVocê dá as costas. Não corre, mas não hesita: o caminho de volta até a boca da mina é o mesmo, e você o faz inteiro sem olhar para trás.");
                return;
            }
        }

        CenaDoGoblinEFilhadeDente(ficha);
    }

            private static void DescreverEscuridaoProfunda(FichaRpg ficha) {
        boolean comTocha = ficha.isTochaNaMao() && ficha.temItem("Tocha");
        if (comTocha) {
            Interface.MostrarMensagem("\nMais para dentro, a " + CIANO + "tocha" + RESET + " resolve tudo o que a escuridão esconde: você vê o túnel inteiro, o chão batendo, a largura das paredes. De onde veio a luz, não é de penumbra: é preta e fechada, e sem a tocha seria outra coisa só.");
            Interface.Pausa(2600);
        } else {
            Interface.MostrarMensagem("\nMais para dentro, o escuro deixa de ser absence de luz e vira " + VERMELHO + "presença" + RESET + ". Você não está vendo o túnel: está se orientando por ele. Os ombros nas paredes, os pés no chão, a direção do ar no rosto. Tudo que você sabe da caverna é o pouco que o tato e o ouvido dizem.");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\nEm alguns passos, a mão que você abre para não bater na parede encontra " + AMARELO + "nada" + RESET + " — um vão que não deveria existir ali. Você se orienta pelo som e pelo cheiro de mineral, e segue.");
            Interface.Pausa(2400);
        }
    }

            private static void DescreverTrilhaECristais(FichaRpg ficha) {
        boolean comTocha = ficha.isTochaNaMao() && ficha.temItem("Tocha");

        if (!ficha.isTrilhaDeTremVistaNaMina() && comTocha) {
            Interface.MostrarMensagem("\nNo chão, correndo fundo para dentro da caverna, está uma " + VERMELHO + "trilha de trilhos" + RESET + ": dois fios de metal, meio afundados no cascalho, gastos no meio pelo tanto de passagem. Isso aqui não é buraco de bicho. Alguém cavou, e quem fez isso trouxe um carrinho.");
            Interface.Pausa(2800);
            ficha.setTrilhaDeTremVistaNaMina(true);
        }

        if (!ficha.isCristaisVistosNaMina() && comTocha) {
            Interface.MostrarMensagem("\nNas paredes, de tempos em tempos, a tocha pega " + CIANO + "cristais" + RESET + " — veios pálidos cravados na rocha escura, onde a luz pega e volta. alguns estão rachados, como se alguém tivesse tentado arrancar um e desistido no meio.");
            Interface.Pausa(2600);
            ficha.setCristaisVistosNaMina(true);
        }

        if (ficha.isTrilhaDeTremVistaNaMina() && ficha.isCristaisVistosNaMina()) {
            Interface.MostrarMensagem("\nOs trilhos e os veios de cristal seguem para o mesmo lado: para dentro, para o fundo da mina, onde o túnel começa a alargar.");
            Interface.Pausa(2200);
        }
    }

            private static void CenaDoGoblinEFilhadeDente(FichaRpg ficha) {
        if (!ficha.isCenaDoGoblinVista()) {
            Interface.MostrarMensagem("\nO túnel alarga num ponto onde o teto sobe. E então você vê: pequenas luzes, verdes e baixas, espalhadas pelo chão como se alguém tivesse derrubado. Perto delas, agachado no cascalho, um " + VERDE + "goblin" + RESET + " e, deitado de lado ao lado dele, uma " + AMARELO + "criança desmaiada" + RESET + ".");
            Interface.Pausa(2800);
            Interface.MostrarMensagem("\nO goblin está encolhido, com os ombros subindo e descendo rápido. Ele não estava dormindo: estava " + VERMELHO + "com medo" + RESET + ". A criança não se mexe. É pequena, e as escamas claras estão quase brancas de tão pálidas.");
            Interface.Pausa(2600);
            ficha.setCenaDoGoblinVista(true);
        } else {
            Interface.MostrarMensagem("\nVocê volta ao trecho onde os trilhos alagam o cascalho, e o goblin ainda está aí, encolhido ao lado da criança.");
            Interface.Pausa(2200);
        }

        Interface.MostrarMensagem("\nO goblin levanta a cabeça. Ele te vê. E o que sai dele não é um ataque — é a mesma coisa repetida, cada vez mais rápido, como se não conseguisse parar: " + VERMELHO + "\"não foi culpa minha, eu não quis fazer isso, não não não\"" + RESET + ".");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nEle não chega perto de você. Não chega perto da criança também. Fica exatamente onde está, repetindo.");
        Interface.Pausa(2400);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Tentar acalmar o goblin e entender o que houve");
        System.out.println("  2. Atacar");
        System.out.println("  3. Sair correndo");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(3);

        if (escolha == 3) {
            VoltarDaMina(ficha, "\nVocê não corre de verdade — ninguém persegue ninguém aqui. Mas você dá as costas e sai, rápido, sem olhar para trás, e a caverna te engole de novo até a boca.");
            return;
        }

        if (escolha == 2) {
            AtacarGoblinPequeno(ficha);
            return;
        }

        {
            if (testePresenca(ficha, 15, " acalmar o goblin")) {
                Interface.MostrarMensagem("\nVocê não se aproxima com pressa. Fica onde está, baixo, e deixa ele falar até a frase se acalmar. A repetição diminui, vira sílaba, vira silêncio.");
                Interface.Pausa(2800);
                Interface.MostrarMensagem("\nEntão ele conta, e é tudo que ele sabe dizer: que não controla, que só faz o que mandam. Que os dracônicos " + AMARELO + "mandaram ele e todos os irmãos embora" + RESET + " da mina, e que depois mandaram voltar. Que trouxeram a menina antes, e que ele foi obrigado a olhar.");
                Interface.Pausa(3200);
                Interface.MostrarMensagem("\nEle não pede perdão. Só diz, várias vezes, que não foi culpa dele, e que já conta isso para quem quiser ouvir.");
                Interface.Pausa(2800);
                if (ficha.isMissaoAceita("A Filha Perdida")) {
                    GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "O goblin contou: os dracônicos expulsaram ele e todos os irmãos da mina, e depois os obrigaram a voltar. A menina foi levada antes, e ele foi obrigado a ver.");
                }
            } else {
                Interface.MostrarMensagem("\nVocê tenta. Fala baixo, devagar, do jeito que se fala com bicho acuado. Ele te olha, e o repetido " + VERMELHO + "\"não foi culpa minha\"" + RESET + " não diminui: acelera.");
                Interface.Pausa(2800);
                Interface.MostrarMensagem("\nQuando você chega perto o bastante para tocar nele, ele se levanta de um pulo. Não era para correr. Era para " + VERMELHO + "gritar" + RESET + ".");
                Interface.Pausa(2400);
            }
        }

        TransformacaoDoGoblin(ficha);
        LutaContraGoblinTransformado(ficha);
    }

            private static void AtacarGoblinPequeno(FichaRpg ficha) {
        Interface.MostrarMensagem("\nVocê não espera ele terminar. Avança no espaço aberto, onde ele não tem parede para se esconder, eresolve a distância antes que ele mude de ideia.");
        Interface.Pausa(2400);

        Criatura pequeno = CriaturaFactory.criarGoblin();
        List<Criatura> inimigos = new ArrayList<>();
        inimigos.add(pequeno);
        MotorDeCombate.IniciarCombate(ficha, inimigos, true);
        if (ficha.getVidaPersonagem() <= 0) return;

        if (pequeno.getVida() > 0) {
            Interface.MostrarMensagem("\nO goblin apanha, levanta, e o repetido vira outra coisa: um grito. Ele não recua um passo, porque atrás dele, no chão, está a criança.");
            Interface.Pausa(2800);
            TransformacaoDoGoblin(ficha);
            LutaContraGoblinTransformado(ficha);
            return;
        }

        Interface.MostrarMensagem("\nO goblin pequeno cai sentado no cascalho e não levanta mais. As pequenas luzes verdes ao redor apagam uma a uma, e o túnel fica com bem menos coisa nele.");
        Interface.Pausa(2800);
        Interface.MostrarMensagem("\nA criança continua no chão, com a mesma respiração. Ele não chegou a encostar nela — só ficou ali, repetindo, até você resolver.");
        Interface.Pausa(2600);
        GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "Você atacou o goblin antes dele crescer e ele morreu sem se transformar. Ele não tinha encostado na criança.");
        DecisaoSobreAFilhadeDente(ficha);
    }

            private static void TransformacaoDoGoblin(FichaRpg ficha) {
        Interface.MostrarMensagem("\nE aí acontece o que ia acontecer. Ele começa a falar cada vez mais alto, e o que sai não é mais o repetido: é " + VERMELHO + "\"a culpa é dos de vocês\"" + RESET + ". A culpa é dos dracônicos que mandaram ele e todos os irmãos embora. A culpa é de quem deu a ordem. Ele não foi, ele só foi.");
        Interface.Pausa(3200);
        Interface.MostrarMensagem("\nEle começa a " + VERMELHO + "crescer" + RESET + ".");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nNão é do tamanho que um corpo deveria fazer. Os ombros estalam, os braços engrossam até não caberem na pose, e o que era um goblin pequeno e encolhido vira uma coisa " + VERMELHO + "grande e imponente" + RESET + " no meio do túnel. A caverna parece menor de repente.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nNo meio do crescimento ele " + AMARELO + "vai até o chão e volta com um porrete" + RESET + " na mão, uma coisa pesada e escura que não estava ali antes.");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nO que sai dessa boca não é mais frase. É " + VERMELHO + "rugido" + RESET + ", e tudo que ele tinha de raciocínio, de medo, de hesitação — foi trocado por uma coisa só: ele quer derrubar o que está na frente dele.");
        Interface.Pausa(2800);
        if (ficha.isMissaoAceita("A Filha Perdida")) {
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "O goblin se transformou e atacou. Não parecia mais um bicho acuado: era grande, e só rugia.");
        }
    }

            private static void LutaContraGoblinTransformado(FichaRpg ficha) {
        Criatura transformado = CriaturaFactory.criarGoblinTransformado();
        List<Criatura> inimigos = new ArrayList<>();
        inimigos.add(transformado);
        MotorDeCombate.IniciarCombate(ficha, inimigos, false);
        if (ficha.getVidaPersonagem() <= 0) return;
        if (transformado.getVida() > 0) return;

        Interface.MostrarMensagem("\nA coisa enorme para de rugir e desaba no cascalho, e o túnel está de repente calmo de um jeito ruim.");
        Interface.Pausa(2800);
        Interface.MostrarMensagem("\nO porrete escapa da mão dele e fica deitado na poeira, perto do braço que não se mexe mais. É uma peça pesada e escura, do tamanho de um antebraço, com a ponta marcada de usos.");
        Interface.Pausa(2400);
        PegarPorreteDoGoblin(ficha);

        DecisaoSobreAFilhadeDente(ficha);
    }

            private static void PegarPorreteDoGoblin(FichaRpg ficha) {
        if (ficha.temItem("Porrete")) {
            Interface.MostrarMensagem("\nVocê já tem um porrete. Este fica na poeira.");
            Interface.Pausa(1600);
            return;
        }
        GerenciadorDeInventarioFicha.coletarItemEncontrado(ficha, loja.Vendedor.criarItem("Porrete"), "Você pega");
        if (ficha.temItem("Porrete")) {
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "Você pegou o porrete do goblin transformado. É uma peça pesada, do tamanho de um antebraço.");
        }
    }

            private static void DecisaoSobreAFilhadeDente(FichaRpg ficha) {
        Interface.MostrarMensagem("\nA criança continua no chão, no mesmo lugar, com a mesma respiração. O que fizeram com ela ainda não passou.");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nA criança continua no chão, no mesmo lugar, com a mesma respiração. O que fez com ela ainda não passou.");
        Interface.Pausa(2600);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Pegar a criança e levá-la até a mãe");
        System.out.println("  2. Deixar a criança e ir buscar um guarda");
        System.out.println("  3. Ir embora e deixar a criança lá");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(3);

        if (escolha == 1) {
            LevarAfilhaParaaMae(ficha);
        } else if (escolha == 2) {
            ChamarOsGuardas(ficha);
        } else {
            VoltarDaMina(ficha, "\nVocê olha para a criança uma última vez, e olha para a saída. Escolhe a saída. O som dos seus passos no cascalho é a única coisa que fica.");
            ficha.setDesfechoDaFilha("abandonada");
        }
    }

            private static void LevarAfilhaParaaMae(FichaRpg ficha) {
        ficha.setFilhaResgatada(true);
        ficha.setFilhaEncontrada(true);
        ficha.setDesfechoDaFilha("levada_para_a_mae");
        ficha.encerrarMissao("A Filha Perdida");
        Interface.MostrarMensagem("\nVocê se ajoelha, pega a criança nos braços e a levanta. Ela é mais leve do que deveria, e está gelada. Ela não acorda, mas respira.");
        Interface.Pausa(2800);
        Interface.MostrarMensagem("\nVocê sai da mina carregando ela. A boca da mina fica para trás, e a corrida até a vila é a parte mais longa da noite.");
        Interface.Pausa(3000);
        VoltarParaVilaComAfilha(ficha);
    }

            private static void VoltarParaVilaComAfilha(FichaRpg ficha) {
        Interface.MostrarMensagem("\nVocê entra na vila carregando a criança. As primeiras pessoas que você vê acordam e param de fazer o que estavam fazendo, uma depois da outra, até a rua inteira estar olhando.");
        Interface.Pausa(2800);
        Interface.MostrarMensagem("\nÉ a dona da alfaiataria que chega primeiro. Ela olha a criança nos seus braços, olha seu rosto, e não pergunta nada — chega perto e pega a filha.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\n" + CIANO + "\"Você a encontrou. Você a encontrou na mina.\"" + RESET + " — ela diz, e a voz não sai firme. Depois, mais baixo, para você: " + CIANO + "\"Obrigada. Obrigada. Eu não sei como pagar isso, mas obrigada.\"" + RESET);
        Interface.Pausa(3200);
        Interface.MostrarMensagem("\nEla se afasta com a filha. A vila continua parada, e é por ali que ela vai: Some com a criança na direção da alfaiataria, e ninguém tenta te deter.");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\n" + VERDE + "Você recebe 100 de ouro." + RESET);
        ficha.adicionarOuro(100);
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\nA filha da Célia está viva. A missão acaba aqui.");
        Interface.Pausa(2600);
    }

            private static void ChamarOsGuardas(FichaRpg ficha) {
        ficha.setFilhaEncontrada(true);
        ficha.setDesfechoDaFilha("resgatada_pelos_guardas");
        Interface.MostrarMensagem("\nVocê deixa a criança no mesmo lugar, sai da mina e vai direto aos guardas. É mais sensato que tentar carregar ela sozinha — se ela está ferida, quem entende é quem tem treinamento.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nOs guardas descem com tochas. Você conta o que viu. Eles sobem a mina, e poco depois voltam com a criança nos braços.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nO ponto é que ninguém te paga por isso, e ninguém te culpa também. O que você fez foi certo: você não abandonou a criança, você entregou ela a quem podia cuidar. A filha da Célia está viva, e isso basta.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nA missão acaba. Você não recebe a recompensa — a recompensa era para quem levasse a criança até a mãe, e não foi o caso.");
        Interface.Pausa(2600);
        ficha.encerrarMissao("A Filha Perdida");
    }

            private static void VoltarDaMina(FichaRpg ficha, String mensagem) {
        Interface.MostrarMensagem(mensagem);
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nVocê sai da boca da mina e refaz o caminho de terra de volta, até as ruas da vila.");
        Interface.Pausa(2200);
        ficha.setDentroDaCaverna(false);
    }

    public static boolean ePrimeiraVezQueAfilhaFoiVista(FichaRpg ficha) {
        return !ficha.isCenaDoGoblinVista();
    }

    public static boolean resgateConcluido(FichaRpg ficha) {
        return ficha.isFilhaResgatada() || "resgatada_pelos_guardas".equals(ficha.getDesfechoDaFilha());
    }

    public static boolean filhaFoiAbandonada(FichaRpg ficha) {
        return "abandonada".equals(ficha.getDesfechoDaFilha());
    }

    public static boolean pracaDesertaDespoisDoAbandono(FichaRpg ficha) {
        return filhaFoiAbandonada(ficha);
    }
}

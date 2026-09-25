package eventos;

import java.util.ArrayList;
import java.util.List;

import criaturas.Criatura;
import criaturas.CriaturaFactory;
import fichas.FichaRpg;
import mecanicas.MecanicasRpg;
import mecanicas.MotorDeCombate;
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
            CenaDosGoblins(ficha);
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

    // ==================== TAVERNA DOS GOBLINS ====================

    // Ao entrar na taverna, o jogador se depara com 4 goblins assaltando os clientes.
    public static void CenaDosGoblins(FichaRpg ficha) {
        List<Criatura> goblins = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            goblins.add(CriaturaFactory.criarGoblin());
        }

        Interface.MostrarMensagem("\nVocê empurra a porta da taverna — e o que vê o faz congelar.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("Dentro, " + VERMELHO + "4 goblins" + RESET + " saltam sobre as mesas, apontando adagas para os clientes e exigindo ouro. Pessoas no chão, copos quebrados, gritos abafados: um assalto em plena luz do dia.");
        Interface.Pausa(2500);
        Interface.MostrarMensagem("Ninguém notou você na porta. Ainda.");
        Interface.Pausa(1800);

        System.out.println("\n  O que você faz?");
        System.out.println("  1. Sair dali furtivo");
        System.out.println("  2. Ir lutar contra eles");
        System.out.println("  3. Tentar conversar com eles");
        System.out.println("  4. Tentar ir lutar contra eles furtivo");
        System.out.println("\n  " + Interface.VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(4);

        if (escolha == 1) {
            sairFurtivo(ficha, goblins);
        } else if (escolha == 2) {
            Interface.MostrarMensagem("\nVocê saca sua arma e avança, gritando! Os goblins se viram — o assalto agora tem um novo alvo.");
            Interface.Pausa(2000);
            MotorDeCombate.IniciarCombate(ficha, goblins, false);
        } else if (escolha == 3) {
            conversarComOsGoblins(ficha, goblins);
        } else {
            lutarFurtivo(ficha, goblins);
        }
    }

    // Sair dali furtivo (DT 10): se falhar, os goblins o veem e mandam entregar tudo.
    private static void sairFurtivo(FichaRpg ficha, List<Criatura> goblins) {
        boolean passou = testeDestreza(ficha, 10, "Sair furtivo");
        if (passou) {
            Interface.MostrarMensagem("\nVocê recua pé ante pé, contorna a porta e desaparece para fora sem que ninguém perceba.");
            Interface.Pausa(2200);
            return;
        }

        Interface.MostrarMensagem("\nVocê tropeça em uma cadeira! O barulho faz os goblins se virarem — e eles o veem.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem(VERMELHO + "\"Ei, você! Para onde pensa que vai?\" " + RESET + "Um deles salta até você e aponta a adaga. " + VERMELHO + "\"Entregue TUDO o que você tem ou eu juro que vai se arrepender!\"" + RESET);
        Interface.Pausa(2000);

        comandoRetiradaObrigatoria(ficha, goblins);
    }

    // Quando os goblins exigem a entrega dos pertences (sem saída furtiva).
    private static void comandoRetiradaObrigatoria(FichaRpg ficha, List<Criatura> goblins) {
        System.out.println("\n  O que você faz?");
        System.out.println("  1. Entregar todo o seu dinheiro");
        System.out.println("  2. Não obedecer — e lutar");
        System.out.println("  3. Tentar fugir");
        System.out.println("\n  " + Interface.VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(3);

        if (escolha == 1) {
            int ouroEntregue = ficha.getOuro();
            ficha.gastarOuro(ouroEntregue);
            Interface.MostrarMensagem("\nVocê joga sua bolsa de dinheiro no chão: " + AMARELO + ouroEntregue + " de ouro" + RESET + ".");
            Interface.MostrarMensagem("O goblin a apanha, sorri torto e faz um gesto para a porta: " + VERMELHO + "\"Boa escolha. Some daqui.\"" + RESET);
            Interface.Pausa(2200);
        } else if (escolha == 2) {
            Interface.MostrarMensagem("\nVocê recusa e sua mão fecha no cabo da arma. Os goblins rosnam e se espalham em volta de você.");
            Interface.Pausa(2000);
            MotorDeCombate.IniciarCombate(ficha, goblins, false);
        } else {
            boolean fugiu = testeDestreza(ficha, 15, "Fugir");
            if (fugiu) {
                Interface.MostrarMensagem("\nVocê se vira e dispara porta afora, derrubando o que estiver no caminho. Os goblins berram, mas você já foi.");
                Interface.Pausa(2200);
            } else {
                Interface.MostrarMensagem("\nEles cortam sua saída! Não há para onde correr — resta lutar.");
                Interface.Pausa(2000);
                MotorDeCombate.IniciarCombate(ficha, goblins, false);
            }
        }
    }

    // Tentar ir lutar contra eles furtivo (DT 15): passou, +2 na iniciativa; senão, luta normal.
    private static void lutarFurtivo(FichaRpg ficha, List<Criatura> goblins) {
        boolean passou = testeDestreza(ficha, 15, "Lutar furtivo");
        if (passou) {
            Interface.MostrarMensagem("\nVocê desliza pelas sombras do salão, mudo como um gato, até ficar a poucos passos deles. Eles nem suspeitam.");
            Interface.Pausa(2000);
            Interface.MostrarMensagem(CIANO + "Você surpreende os goblins! (+2 de Iniciativa)" + RESET);
            Interface.Pausa(1500);
            MotorDeCombate.IniciarCombate(ficha, goblins, true);
        } else {
            Interface.MostrarMensagem("\nVocê tenta se aproximar nas sombras, mas um goblin se vira no momento exato e o flagra. Sem cerimônia: luta!");
            Interface.Pausa(2000);
            MotorDeCombate.IniciarCombate(ficha, goblins, false);
        }
    }

    // Diálogo com os goblins: ameaçar ou entender o motivo.
    private static void conversarComOsGoblins(FichaRpg ficha, List<Criatura> goblins) {
        boolean draconico = ficha.getRaca() != null && ficha.getRaca().getNome().equals("Dracônico");

        Interface.MostrarMensagem("\nVocê levanta as mãos e fala em voz alta: " + CIANO + "\"Calma aí! Vamos conversar antes que alguém se machuque.\"" + RESET);
        Interface.Pausa(2000);
        Interface.MostrarMensagem("Os goblins trocam olhares, adagas ainda em punho, e um deles rosna: " + AMARELO + "\"Conversar? O que você quer dizer com isso?\"" + RESET);
        Interface.Pausa(2000);

        System.out.println("\n  Como você responde?");
        System.out.println("  1. Ameaçá-los para que sintam medo");
        System.out.println("  2. Tentar entender o motivo deles");
        System.out.println("\n  " + Interface.VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            ameacarOsGoblins(ficha, goblins);
        } else {
            entenderMotivoDosGoblins(ficha, goblins, draconico);
        }
    }

    // Ameaçar: teste de Presença contra +2 de Presença dos goblins. Passou, eles fogem de medo.
    private static void ameacarOsGoblins(FichaRpg ficha, List<Criatura> goblins) {
        Interface.MostrarMensagem("\nSua voz muda. Seus olhos se apertam e você fala baixo e raspado: " + VERMELHO + "\"Vocês têm cinco segundos para sair por essa porta antes que eu transforme vocês em tapete.\"" + RESET);
        Interface.Pausa(2000);

        Interface.pressionarParaTeste("Presença");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getPresencaTeste();
        int dadoGoblin = MecanicasRpg.rolarDado(20);
        int totalGoblin = dadoGoblin + 2;
        Interface.MostrarMensagem("-> Presença: " + dadoJogador + " (Dado) + " + ficha.getPresencaTeste() + " (Atributo) = " + totalJogador + " | Goblins: " + dadoGoblin + " (Dado) + 2 (Presença) = " + totalGoblin);
        Interface.Pausa(2500);

        if (totalJogador >= totalGoblin) {
            Interface.MostrarMensagem("\nOs goblins empalidecem. A adaga treme na mão do líder. No silêncio que segue, eles trocam olhares e começam a se encolher.");
            Interface.Pausa(2000);
            Interface.MostrarMensagem(CIANO + "Tomados pelo medo, os goblins soltam o que carregam e saem correndo pela porta dos fundos!" + RESET);
            Interface.Pausa(2200);
        } else {
            Interface.MostrarMensagem("\nPor um instante de silêncio, mas então o líder ri alto. " + AMARELO + "\"Boa piada!\" " + RESET + "A ameaça não funcionou — e eles avançam furiosos.");
            Interface.Pausa(2000);
            MotorDeCombate.IniciarCombate(ficha, goblins, false);
        }
    }

    // Entender o motivo: eles explicam o rancor; dracônicos já sabem e pulam a explicação.
    private static void entenderMotivoDosGoblins(FichaRpg ficha, List<Criatura> goblins, boolean draconico) {
        Interface.MostrarMensagem(CIANO + "\"Por que vocês estão fazendo isso?\" " + RESET + "você pergunta, tentando entender.");
        Interface.Pausa(1800);

        if (draconico) {
            Interface.MostrarMensagem("\"Você é dracônico\", rosna o líder, apertando a adaga. \"Então já sabe muito bem o que sua gente fez com a nossa. E não vai ter perdão da nossa parte.\"");
            Interface.Pausa(2000);

            Interface.MostrarMensagem("\nEnquanto ele fala, a memória vem à tona, nítida, como se você tivesse lido ontem...");
            Interface.Pausa(2000);
            Interface.MostrarMensagem("A história dos " + AMARELO + "goblins e de suas minas de ouro" + RESET + ". Auditores dracônicos desceram sobre os territórios deles e tomaram as minas sob a bandeira do avanço da própria espécie — o bem mais valioso que aquela gente possuía, arrancado em nome do progresso.");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("Agora você entende por que eles vieram buscar o ouro de volta — mesmo que seja do bolso de outros.");
            Interface.Pausa(2200);

            comandoLutarOuFugir(ficha, goblins, 10, false);
            return;
        }

        Interface.MostrarMensagem("\"A gente nunca pediu para nascer nessa sociedade\", diz o goblin, com um riso amargo. \"E essa sociedade nunca teve perdão para a nossa raça. Então a gente não tem perdão para ninguém.\"");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("\"Se os dracônicos tiraram tudo de nós, a gente pode muito bem tirar tudo de vocês.\"");
        Interface.Pausa(2200);

        System.out.println("\n  O que você faz?");
        System.out.println("  1. Perguntar o que tiraram dos goblins");
        System.out.println("  2. Lutar contra eles");
        System.out.println("  3. Tentar fugir");
        System.out.println("\n  " + Interface.VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(3);

        if (escolha == 1) {
            explicarMinasDeOuro(ficha, goblins);
        } else if (escolha == 2) {
            Interface.MostrarMensagem("\nBasta de histórias. Você ergue a arma — e a taverna inteira prende a respiração.");
            Interface.Pausa(2000);
            MotorDeCombate.IniciarCombate(ficha, goblins, false);
        } else {
            boolean fugiu = testeDestreza(ficha, 15, "Fugir");
            if (fugiu) {
                Interface.MostrarMensagem("\nVocê dá meia-volta e escapa pela porta enquanto os goblins berram ordens. A confusão fica para trás.");
                Interface.Pausa(2200);
            } else {
                Interface.MostrarMensagem("\nEles cercam a porta! Sem como fugir — é luta.");
                Interface.Pausa(2000);
                MotorDeCombate.IniciarCombate(ficha, goblins, false);
            }
        }
    }

    // Perguntar o que tiraram: a história das minas de ouro; depois, lutar ou fugir.
    private static void explicarMinasDeOuro(FichaRpg ficha, List<Criatura> goblins) {
        Interface.MostrarMensagem(CIANO + "\"O que os dracônicos tiraram de vocês?\" " + RESET + "você pergunta.");
        Interface.Pausa(1500);

        Interface.MostrarMensagem("O goblin líder ri, mas sem alegria. \"Tudo o que a gente tinha de mais valioso. " + AMARELO + "O ouro" + RESET + ".\"");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\"Nossa raça é famosa pela grande história das nossas " + AMARELO + "minas de ouro" + RESET + ". Mas os dracônicos tomaram as minas de nós, para o avanço da própria espécie.\"");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\"Tiraram o bem mais valioso que a gente tinha. Então a gente veio pegar o ouro de volta — nem que seja do bolso desses pelos-sujos daqui.\"");
        Interface.Pausa(2600);

        comandoLutarOuFugir(ficha, goblins, 15, true);
    }

    // Depois de entender os motivos: lutar ou fugir. Se entendeu a história, a DT de
    // fuga cai para 5; se é dracônico (que já sabia), permanece 10.
    private static void comandoLutarOuFugir(FichaRpg ficha, List<Criatura> goblins, int dtFuga, boolean entendeuMotivos) {
        int dificuldade = entendeuMotivos ? 5 : dtFuga;

        System.out.println("\n  O que você faz?");
        System.out.println("  1. Lutar contra eles");
        System.out.println("  2. Tentar fugir (DT " + (dificuldade == 10 ? "10" : dificuldade) + ")");
        System.out.println("\n  " + Interface.VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            Interface.MostrarMensagem("\nVocê desenha a arma. Os goblins se espalham, prontos. A taverna se põe em silêncio.");
            Interface.Pausa(2000);
            MotorDeCombate.IniciarCombate(ficha, goblins, false);
        } else {
            boolean fugiu = testeDestreza(ficha, dificuldade, "Fugir");
            if (fugiu) {
                Interface.MostrarMensagem("\nVocê recua lentamente, cruza a porta e se afasta. Os goblins o veem partir sem dizer palavra — talvez por entenderem que você entendeu.");
                Interface.Pausa(2200);
            } else {
                Interface.MostrarMensagem("\nSua hesitação o entrega. Os goblins avançam antes que você alcance a saída!");
                Interface.Pausa(2000);
                MotorDeCombate.IniciarCombate(ficha, goblins, false);
            }
        }
    }

    // Teste de Destreza contra uma dificuldade; mostra o cálculo e retorna se passou.
    private static boolean testeDestreza(FichaRpg ficha, int dificuldade, String rotulo) {
        Interface.pressionarParaTeste("Destreza (" + rotulo + ")");
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + ficha.getDestrezaTeste();
        Interface.MostrarMensagem("-> Destreza: " + dado + " (Dado) + " + ficha.getDestrezaTeste() + " (Atributo) = " + total + " (Dificuldade: " + dificuldade + ")");
        Interface.Pausa(2500);
        return total >= dificuldade;
    }
}
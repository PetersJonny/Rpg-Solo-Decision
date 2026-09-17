package eventos;

import classes.Guerreiro;
import classes.Mago;
import classes.Healer;
import companheiros.Companheiro;
import criaturas.Criatura;
import criaturas.CriaturaFactory;
import fichas.FichaRpg;
import habilidades.Habilidade;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class EventManager {

    private static final String RESET = Interface.RESET;
    private static final String VERMELHO = "\u001B[31m";
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;

    public static void EventoPerdido(FichaRpg ficha) {
        Interface.MostrarMensagem("\nAo acordar, você percebe que não há ninguém por perto. Onde estará seu companheiro?");
        Interface.Pausa(2000);

        int opcao = 0;
        boolean especialAtivado = false;

        while (opcao != 1 && opcao != 2) {
            System.out.println("\n  " + AMARELO + "O que você faz?" + RESET + "\n");
            System.out.println("  1. " + CIANO + "Sair procurando seu companheiro" + RESET + " (Encontro de criaturas inevitável)");
            System.out.println("  2. " + CIANO + "Não se importar e ir explorar a floresta por conta própria" + RESET);

            opcao = Interface.lerInteiro();

            if (opcao == 2) {
                System.out.println("\n" + AMARELO + "\nAo se afastar da cabana, você escuta um uivo distante vindo da madeira. Pode ser perigoso, mas talvez valha a pena investigar..." + RESET + "\n");
                Interface.Pausa(2000);

                boolean houveEncontro = false;

                if (ficha.getClasseDoPersonagem() instanceof Healer) {
                    System.out.println("\nPor algum motivo, um sentimento estranho domina seu corpo...");
                    Interface.Pausa(2000);
                    especialAtivado = true;
                    houveEncontro = true;
                }

                if (especialAtivado) {
                    System.out.println("Uma aura completamente diferente do seu começa a se espalhar por sua mente...");
                    Interface.Pausa(2000);
                    System.out.println("\nDe repente, um brilho quase impossível de ver começa a se formar...");
                    Interface.Pausa(2000);

                    System.out.println("\nVocê sente uma " + AMARELO + "presença divina" + RESET + " fluindo em sua mente...");
                    System.out.println("Você tem a sensação de " + VERMELHO + "que algo quer através de você..." + RESET);
                    Interface.Pausa(2000);

                    System.out.println("\nUma energia proveniente do nada começa a emanar de seu corpo...");
                    Interface.Pausa(2000);

                    System.out.println("\nAntes que qualquer coisa pudesse acontecer, uma criança aparece na sua frente e ");
                    System.out.println("a energia some. Quando a criança te vê começa a gritar e foge...");
                    Interface.Pausa(2000);
                }

                if (!houveEncontro) {
                    System.out.println("\nEnquanto caminha pela floresta, você se depara com uma " + VERMELHO + "ameaça que dorme." + RESET);
                    System.out.println("Talvez atacar pudesse ser muito arriscado, especialmente porque o som que você emitiu");
                    System.out.println("fez com que os olhos dela se abrissem.");
                    Interface.Pausa(2000);

                    int tipo = 0;
                    while (tipo != 1 && tipo != 2) {
                        System.out.println("\n  " + AMARELO + "O que você faz?" + RESET + "\n");
                        System.out.println("  1. " + CIANO + "Enfrentar a criatura" + RESET);
                        System.out.println("  2. " + CIANO + "Tentar fugir" + RESET);

                        tipo = Interface.lerInteiro();

                        if (tipo == 1) {
                            Criatura c = CriaturaFactory.criarLobo();
                            combate.CombatManager.IniciarCombate(ficha, c, null);
                        } else {
                            System.out.println("\nVocê tenta fugir, mas a criatura não parece querer te deixar ir...");
                            System.out.println("Sem suas devidas armas para te ajudar, você não tem escolha...");
                            System.out.println("Sua única esperança é se esconder.");
                            System.out.println("\n" + AMARELO + "Infelizmente você não encontrou nada e teve que voltar para a cabana." + RESET);
                            System.out.println("\nAo voltar para sua cabana, seu companheiro também acaba de retornar...");
                            Interface.Pausa(2000);
                            return;
                        }
                    }
                } else {
                    System.out.println("\nUma criatura se aproxima de você...");
                    System.out.println("Você não tem escolha, seu corpo já não está respondendo, é hora de lutar!");
                    Interface.Pausa(2000);

                    Criatura c = CriaturaFactory.criarLobo();
                    combate.CombatManager.IniciarCombate(ficha, c, null);
                }

                Companheiro comp = ficha.getCompanheiro();
                System.out.println("\nApós a luta, " + comp.getNome() + " aparece no horizonte e começa a correr na sua direção.");
                System.out.println("\"Ainda bem que você está vivo! Eu ouvi uma criatura rugindo na floresta e saí para explorar.");
                System.out.println("Quando vi que você não estava mais na cabana, saí imediatamente para te procurar.\"");
                Interface.Pausa(2000);
                System.out.println("\nO " + comp.getNome() + " leva você até a cabana novamente...");
                System.out.println("\"Ainda bem que não aconteceu nada, se tivesse acontecido algo, eu nem sei o que faria.\"");
                Interface.Pausa(2000);
                return;
            }

            if (opcao == 1) {
                System.out.println("\nSaindo da cabana, você começa a caminhar pela floresta de Freijord em busca de seu parceiro...");
                System.out.println("O vento gélido começa a soprar cada vez mais forte. De repente, você ouve um uivo de ");
                System.out.println("lobo ao longe, e então a floresta volta ao silêncio.");
                Interface.Pausa(2000);

                System.out.println("\nVocê encontra pegadas de animal que parecem ter vindo de sua cabana...");
                System.out.println("Acredita-se que provavelmente seu companheiro esteja na direção das pegadas.");
                Interface.Pausa(2000);

                System.out.println("\nAo seguir as pegadas, você percebe que o chão está manchado com o que parece ser sangue.");
                System.out.println("Não é muito provável que esteja tudo bem, ou talvez seja de um animal que seu companheiro tenha caçado.");
                Interface.Pausa(2000);

                System.out.println("\n" + AMARELO + "Então um som estranho ecoa na mata..." + RESET);
                System.out.println("\"Socorro! Me ajudem!\"");
                System.out.println("\nA voz parece ser de alguém passando por um forte apuro...");
                Interface.Pausa(2000);

                int acao = 0;
                while (acao != 1 && acao != 2) {
                    System.out.println("\n  " + AMARELO + "O que você faz?" + RESET + "\n");
                    System.out.println("  1. " + CIANO + "Ignorar o pedido de ajuda" + RESET + " (continua procurando seu companheiro)");
                    System.out.println("  2. " + CIANO + "Investigar o pedido de ajuda" + RESET);

                    acao = Interface.lerInteiro();

                    if (acao == 1) {
                        System.out.println("\nVocê ignora o chamado e continua seu caminho...");
                        System.out.println("De repente, um groupe de bandidos aparece e começa a cercar você...");
                        System.out.println("\"Onde você pensava que estava indo, não vai ser tão fácil sair daqui.\"");
                        System.out.println("Embora não pareçam muito fortes, eles claramente estão em maior número e\"");
                        System.out.println("\"não parece que você vai conseguir fugir sem lutar primeiro.\"");
                        Interface.Pausa(2000);

                        System.out.println("\nAo derrotar o grupo de bandidos, você finalmente encontra seu companheiro!");
                        System.out.println("\"Ainda bem que você veio me procurar. Eu estava caçando um coelho quando fui surpreendido por esses bandidos.\"");
                        System.out.println("\"Quando vi que eles eram muitos, fugi, mas machuquei minha perna na queda, por isso não consegui voltar sozinho.\"");
                        Interface.Pausa(2000);

                        System.out.println("\"Vamos voltar para a cabana agora, não quero mais ficar aqui fora.\"");
                        System.out.println("\nAo chegar na cabana, você decide que é hora de descansar.");
                        Interface.Pausa(2000);
                        ficha.setDiaAtual(ficha.getDiaAtual() - 1);
                        return;
                    }

                    if (acao == 2) {
                        System.out.println("\nVocê vai na direção do grito de socorro...");
                        System.out.println("De repente, você se depara com um homem caído no chão, sangrando, com uma");
                        System.out.println("espada fincada em sua perna. Ao seu redor, há restos de equipamento e marcas de batalha.");
                        Interface.Pausa(2000);
                        System.out.println("\"Ainda bem que você veio... Eu fui atacado por um monstro que habitava este bosque...");
                        System.out.println("Eu quase morri, mas consegui arrastar até aqui. Não consigo tirar essa espada,...");
                        System.out.println("ela parece estar presa em algo.\"");
                        System.out.println("\nA dor do homem é nítida, mas ele ainda consegue falar.");
                        Interface.Pausa(2000);

                        int decisao = 0;
                        while (decisao != 1 && decisao != 2) {
                            System.out.println("\n  " + AMARELO + "O que você faz?" + RESET + "\n");
                            System.out.println("  1. " + CIANO + "Ajudar a retirar a espada e levar o homem à sua cabana" + RESET);
                            System.out.println("  2. " + CIANO + "Deixar o homem ali e seguir em frente" + RESET);

                            decisao = Interface.lerInteiro();

                            if (decisao == 1) {
                                System.out.println("\nAo tentar retirar a espada, você se depara com a verdadeira situação do homem...");
                                System.out.println("Ele aparenta ser um guerreiro, ou ex-guerreiro... Porém no momento ele só parece alguém desesperado.");
                                System.out.println("\nAo tirar a espada, o homem grita de dor e pede para ir para algum lugar seguro.");
                                System.out.println("\"Por favor, leve-me para algum lugar seguro, eu não consigo mais andar...\"");
                                Interface.Pausa(2000);

                                System.out.println("\nVocê encontra seu companheiro no caminho, ele aparenta estar bem.");
                                System.out.println("\"Achei que tivesse sumido! Por favor, me desculpa. Eu fui caçar algo para comer e acabei ");
                                System.out.println("demorando. Quando ouvi seus passos, voltei imediatamente.\"");
                                System.out.println("\"Pelo jeito, você encontrou alguém precisando de ajuda. Vamos levar ele para a cabana.\"");
                                Interface.Pausa(2000);

                                System.out.println("\nAo chegar na cabana, você decide que é hora de descansar.");
                                ficha.setDiaAtual(ficha.getDiaAtual() - 1);
                                return;
                            }

                            if (decisao == 2) {
                                System.out.println("\n\"O que? Você vai simplesmente me deixar aqui? Eu vou morrer!\"");
                                System.out.println("\"Por favor, não me abandone...\"");
                                Interface.Pausa(2000);

                                System.out.println("\nVocê se afasta lentamente, sem olhar para trás...");
                                System.out.println("De repente, você ouve um som de algo se movendo atrás de você.");
                                System.out.println("Ao olhar, o homem desapareceu completamente.");
                                Interface.Pausa(2000);

                                System.out.println("\nAo voltar para a cabana, você vê seu companheiro sentado ao lado da fogueira...");
                                System.out.println("\"Ei, eu voltei. Achei que tivesse sumido.\"");
                                System.out.println("\"Ainda bem que você voltou, não gosto de ficar sozinho nesse lugar.\"");
                                Interface.Pausa(2000);
                                System.out.println("\nVocê decide que é hora de descansar.");
                                ficha.setDiaAtual(ficha.getDiaAtual() - 1);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void EventoAnimal(FichaRpg ficha) {
        int chance = ficha.isEhNoite() ? 30 : 15;
        boolean oler = MecanicasRpg.rolarDado(100) <= chance;

        if (oler) {
            Interface.MostrarMensagem("\n" + AMARELO + "Você escuta um som vindo das árvores... Algo se move na mata!" + RESET);
            Interface.Pausa(2000);
            System.out.println("\n" + CIANO + "Ao olhar na direção do som, você se depara com uma criatura selvagem." + RESET);
            System.out.println("\n" + AMARELO + "O que você faz?" + RESET);
            System.out.println("  1. " + CIANO + "Enfrentar a criatura" + RESET);
            System.out.println("  2. " + CIANO + "Tentar fugir" + RESET);

            int escolha = Interface.lerInteiro();
            if (escolha == 1) {
                Criatura c = ficha.isEhNoite()
                        ? CriaturaFactory.criarLobo()
                        : CriaturaFactory.criarUrso();
                if (!ficha.isEhNoite()) {
                    System.out.println("\n" + AMARELO + "Enquanto examina a floresta, uma Urso surge entre as árvores!" + RESET);
                    System.out.println("\n" + AMARELO + "Essa criatura parece ser mais forte que uma simples criatura noturna." + RESET);
                    Interface.Pausa(2000);
                } else {
                    System.out.println("\n" + AMARELO + "Enquanto examina a floresta, um Lobo surge entre as árvores!" + RESET);
                    System.out.println("\n" + AMARELO + "Essa criatura parece ser uma ameaça comum durante a noite." + RESET);
                    Interface.Pausa(2000);
                }
                combate.CombatManager.IniciarCombate(ficha, c, null);
            } else {
                Interface.MostrarMensagem("\nVocê decide não arriscar e recua lentamente...");
                Interface.Pausa(1000);
            }
        }
    }

    public static void EncontrarFada(FichaRpg ficha) {
        Interface.MostrarMensagem("\nUma fada brilhante aparece flutuando no ar à sua frente!");
        Interface.Pausa(2000);
        System.out.println("\"Olá, aventureiro! Eu sou Lyra, a Guardiã desta floresta.\"");
        System.out.println("\"Eu posso te conceder um presente, mas preciso que você escolha sabiamente.\"");
        System.out.println("\n" + AMARELO + "A fada estende as mãos e três opções aparecem flutuando no ar:" + RESET);
        System.out.println("  1. " + CIANO + "Poção de Cura" + RESET + " -恢复 2d4+2 de vida");
        System.out.println("  2. " + CIANO + "Amuleto da Proteção" + RESET + " - +1 em Defesa");
        System.out.println("  3. " + CIANO + "Poção de Mana" + RESET + " -恢复 2d4 de mana");
        System.out.println("\n  " + AMARELO + "0. Recusar o presente" + RESET);
        System.out.println("\n" + AMARELO + "O que você escolhe?" + RESET);

        int escolha = Interface.lerOpcao(0, 3);

        switch (escolha) {
            case 1:
                int vida = MecanicasRpg.rolarDado(4) + MecanicasRpg.rolarDado(4) + 2;
                ficha.setVidaPersonagem(ficha.getVidaPersonagem() + vida);
                System.out.println("\n\"Que bom que escolheu bem! A poção brilha e sua vida é restaurada em " + vida + " pontos!\"");
                System.out.println("\"Cuidado por aí, aventureiro! A floresta pode ser perigosa!\"");
                System.out.println("\n" + AMARELO + "A fada desaparece em um flash de luz." + RESET);
                Interface.Pausa(2000);
                break;
            case 2:
                System.out.println("\n\"Que escolha sábia! O amuleto se ajusta ao seu corpo e sua defesa aumenta!\"");
                System.out.println("\"Cuidado por aí, aventureiro! A floresta pode ser perigosa!\"");
                System.out.println("\n" + AMARELO + "A fada desaparece em um flash de luz." + RESET);
                Interface.Pausa(2000);
                break;
            case 3:
                int mana = MecanicasRpg.rolarDado(4) + MecanicasRpg.rolarDado(4);
                ficha.setManaPersonagem(ficha.getManaPersonagem() + mana);
                System.out.println("\n\"Que bom que escolheu bem! A poção brilha e sua mana é restaurada em " + mana + " pontos!\"");
                System.out.println("\"Cuidado por aí, aventureiro! A floresta pode ser perigosa!\"");
                System.out.println("\n" + AMARELO + "A fada desaparece em um flash de luz." + RESET);
                Interface.Pausa(2000);
                break;
            default:
                System.out.println("\n\"Tudo bem, aventureiro! A floresta é perigosa, então cuidado por aí!\"");
                System.out.println("\n" + AMARELO + "A fada desaparece em um flash de luz." + RESET);
                Interface.Pausa(2000);
                break;
        }
    }
}

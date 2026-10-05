package eventos;

import fichas.FichaRpg;
import itens.ItemRpg;
import loja.Vendedor;
import missoes.QuadroDeMissoes;
import telas.Interface;

public class Taverna {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERMELHO = Interface.VERMELHO;

    private static final String[] CARDAPIO = {
            "Sopa do Vilarejo", "Pão Quente com Manteiga", "Ovos Mexidos", "Caldo de Lobo",
            "Peixe Assado", "Estofado de Urso", "Torta de Frutas", "Hidromel"
    };

    private static String corPratos() { return CIANO; }

    public static void Taverna(FichaRpg ficha) {
        if (FuneralDaFilha.bloquearLojaVazia(ficha, "a taverna")) return;
        Interface.cabecalhoMenu("TAVERNA DA VILA");
        if (ficha.isDonoDaTavernaAgradeceu()) {
            Interface.MostrarMensagem("\nA taverna exala cheiro de madeira velha, fumaça de lareira e comida. " + VERMELHO + "Draven" + RESET + ", o dracônico de pele vermelha, limpa um copo de metal atrás do balcão.");
        } else {
            Interface.MostrarMensagem("\nA taverna exala cheiro de madeira velha, fumaça de lareira e comida. Um " + VERMELHO + "dracônico de pele vermelha" + RESET + " limpa um copo de metal atrás do balcão.");
        }

        if (!ficha.isGoblinsResolvido()) {
            Interface.MostrarMensagem("\n\"Bem-vindo, forasteiro. Mas... cuidado. Pra esses lados o vilarejo est\u00e1 cheio de problemas.\"");
            Interface.Pausa(1800);
            VilarejoDeScarbor.CenaDosGoblins(ficha);
            if (!ficha.isGoblinsResolvido()) {
                Interface.MostrarMensagem("\nA taverna segue ocupada pelos goblins. Você não pode usar seus serviços agora.");
                Interface.Pausa(1500);
                return;
            }
        }

        if (!ficha.isDonoDaTavernaAgradeceu()) {
            CenaDoDono(ficha);
            if (!ficha.isDonoDaTavernaAgradeceu()) return;
        }

        MenuTaverna(ficha);
    }

        private static void CenaDoDono(FichaRpg ficha) {
        Interface.MostrarMensagem("\nO dracônico de pele vermelha vira para você e um largo sorriso surge em seu rosto escamado.");
        Interface.Pausa(1800);
        Interface.MostrarMensagem("Ele se recompõe, encosta ambas as mãos no balcão e fala, com um ar de apresentação: " + CIANO + "\"Draven Moreau. Dono desta casa.\"" + RESET + " Que ela nunca mais caia em mãos erradas.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("\"" + AMARELO + "Você e seus companheiros livraram minha taverna daqueles goblins! Eu não saberia como agradecer..." + RESET + "\"");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\nEle puxa um saquinho e coloca no balcão. " + AMARELO + "30 moedas de ouro" + RESET + ".");
        Interface.Pausa(1600);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. " + VERDE + "Aceitar a gratidão" + RESET + " (receber 30 ouro)");
        System.out.println("  2. " + CIANO + "Recusar" + RESET + " (por dignidade)");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            ficha.adicionarOuro(30);
            Interface.MostrarMensagem("Você aceita o ouro. " + AMARELO + "+30 ouro!" + RESET + " Ouro atual: " + ficha.getOuro());
            Interface.Pausa(1600);
        } else {
            Interface.MostrarMensagem("\nVocê acena, recusando o ouro.");
            Interface.Pausa(1200);
            Interface.MostrarMensagem("O dracônico arqueia a sobrancelha: \"Recusa? Confesso que quase nunca vejo isso.\"");
            Interface.Pausa(1800);
            System.out.println("\n  1. Insistir em recusar (ele respeita sua escolha)");
            System.out.println("  2. Aceitar o ouro, afinal");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            if (Interface.lerOpcao(2) == 1) {
                Interface.MostrarMensagem("\"Como preferir, grande amigo. A porta de minha taverna sempre estará aberta para você.\"");
                Interface.Pausa(1800);
            } else {
                ficha.adicionarOuro(30);
                Interface.MostrarMensagem("Você pensa melhor e aceita o ouro. " + AMARELO + "+30 ouro!" + RESET + " Ouro atual: " + ficha.getOuro());
                Interface.Pausa(1600);
            }
        }

                if (ficha.isComidaPorContaDaCasa()) {
            Interface.MostrarMensagem("\"E, já que está aqui: quando precisar comer, a primeira comida é por conta da casa.\"");
            Interface.Pausa(1800);
        } else if (ficha.getDiasSemComer() >= 1) {
            ficha.setComidaPorContaDaCasa(true);
            Interface.MostrarMensagem("\"Tá com cara de quem passou fome. A primeira comida aqui é por conta da casa.\"");
            Interface.Pausa(1800);
        }

        Interface.MostrarMensagem("\"E não esquece do quadro de missões ali na parede, se quiser ganhar uns cobres.\"");
        Interface.Pausa(1800);
        ficha.setDonoDaTavernaAgradeceu(true);
    }

    private static void MenuTaverna(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("TAVERNA");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + " moedas.");
            if (ficha.isComidaPorContaDaCasa() && !ficha.isComidaDaCasaUsada()) {
                Interface.MostrarMensagem("  " + VERDE + "(A primeira comida é por conta da casa!)" + RESET);
            }

            boolean podePerguntar = Alfaiataria.podePerguntarOndeFica(ficha);
            boolean missaoBalthazarAtiva = ficha.isMissaoAceita("O Labirinto Secreto") && !ficha.isMissaoBalthazarEncerrada();
            boolean deManha = !ficha.isEhNoite();
            boolean sabeOndeBalthazarEsta = ficha.getNovidades("O Labirinto Secreto").stream().anyMatch(n -> n.contains("mesa no canto"));

            System.out.println("\n  O que você deseja fazer?\n");
            System.out.println("  1. " + CIANO + "Comer" + RESET + " (do cardápio da taverna)");
            System.out.println("  2. " + CIANO + "Ler o quadro de missões" + RESET + " na parede");
            
            int opAtual = 3;
            int opPerguntar = -1;
            int opPerguntarBalthazar = -1;
            int opFalarBalthazar = -1;
            
            if (podePerguntar) {
                opPerguntar = opAtual++;
                System.out.println("  " + opPerguntar + ". " + CIANO + "Perguntar onde fica a alfaiataria" + RESET);
            }
            if (missaoBalthazarAtiva && deManha) {
                if (!sabeOndeBalthazarEsta) {
                    opPerguntarBalthazar = opAtual++;
                    System.out.println("  " + opPerguntarBalthazar + ". " + CIANO + "Perguntar a Draven sobre Balthazar" + RESET);
                } else {
                    opFalarBalthazar = opAtual++;
                    System.out.println("  " + opFalarBalthazar + ". " + CIANO + "Falar com Balthazar na mesa do canto" + RESET);
                }
            }
            
            int opSair = opAtual;
            System.out.println("  " + VERMELHO + opSair + ". Sair da taverna" + RESET);
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            int escolha = Interface.lerOpcao(1, opSair);

            if (escolha == 1) {
                Comer(ficha);
            } else if (escolha == 2) {
                QuadroDeMissoes.QuadroDeMissoes(ficha);
            } else if (escolha == opPerguntar) {
                Alfaiataria.PerguntarOndeFica(ficha, VERMELHO + "Draven" + RESET);
            } else if (escolha == opPerguntarBalthazar) {
                Interface.MostrarMensagem("\nVocê chama Draven e aponta para o aviso do Labirinto Secreto.");
                Interface.MostrarMensagem("\"Ah, o velho Balthazar...\" Draven aponta com a cabeça para uma mesa afastada. \"Ele está ali, afogando as mágoas no hidromel de sempre. Vá falar com ele.\"");
                ficha.adicionarNovidade("O Labirinto Secreto", "!Draven apontou Balthazar em uma mesa no canto da taverna.");
                Interface.Pausa(2000);
            } else if (escolha == opFalarBalthazar) {
                falarComBalthazar(ficha);
            } else if (escolha == opSair) {
                Interface.MostrarMensagem("\nVocê se levanta e sai da taverna, deixando o dracônico limpando seus copos.");
                Interface.Pausa(1500);
                return;
            }
        }
    }

        private static void Comer(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("COMER NA TAVERNA");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + " moedas.\n");
            System.out.println("  " + corPratos() + "-- CARDÁPIO --" + RESET);

            for (int i = 0; i < CARDAPIO.length; i++) {
                String prato = CARDAPIO[i];
                int preco = Vendedor.precoBase(prato);
                System.out.println("  " + (i + 1) + ". " + prato + " - " + preco + " ouro");
            }
            System.out.println("  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Qual prato você pede?");
            int escolha = Interface.lerOpcao(0, CARDAPIO.length);
            if (escolha == 0) return;

            String prato = CARDAPIO[escolha - 1];
            int preco = Vendedor.precoBase(prato);
            boolean porContaDaCasa = ficha.isComidaPorContaDaCasa() && !ficha.isComidaDaCasaUsada();

            if (porContaDaCasa) {
                preco = 0;
            }

            ItemRpg item = Vendedor.criarItem(prato);
            if (item == null) {
                Interface.ExibirErro("Erro: este prato não está disponível.");
                Interface.Pausa(1500);
                continue;
            }
            if (item.getPeso() > ficha.getEspacoLivreMochila() + 0.0001) {
                Interface.ExibirErro("Sua mochila não tem espaço para levar o prato! (livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
                Interface.Pausa(1500);
                continue;
            }

            if (!porContaDaCasa && !ficha.gastarOuro(preco)) {
                Interface.ExibirErro("Ouro insuficiente! (Precisa de " + preco + ")");
                Interface.Pausa(1500);
                continue;
            }

            ficha.adicionarItem(item);
            if (porContaDaCasa) {
                ficha.setComidaDaCasaUsada(true);
                Interface.MostrarMensagem("\n" + AMARELO + "Por conta da casa!" + RESET + " O dracônico sorri. Você recebeu " + prato + " de graça. (Guardado na mochila.)");
            } else {
                Interface.MostrarMensagem("\nVocê pagou " + preco + " ouro e recebeu " + prato + ". (Guardado na mochila.)");
            }
            Interface.Pausa(1800);

            System.out.println("\n  Comer mais alguma coisa?\n");
            System.out.println("  1. Sim");
            System.out.println("  2. Voltar");
            if (Interface.lerOpcao(2) == 2) return;
        }
    }
    private static void falarComBalthazar(FichaRpg ficha) {
        Interface.cabecalhoMenu("BALTHAZAR");
        Interface.MostrarMensagem("\nVocê se aproxima da mesa do canto. Um homem velho, de barba grisalha suja e olhar distante, encara uma caneca de hidromel pela metade.");
        Interface.MostrarMensagem("\"O que você quer?\" ele resmunga, sem levantar os olhos.");
        Interface.Pausa(1800);
        Interface.MostrarMensagem("\"Você leu o aviso? Sim, eu postei aquilo... Eu era um aventureiro, como você.\"");
        Interface.Pausa(1800);
        Interface.MostrarMensagem("\"Há muitos anos, entramos naquele labirinto secreto na floresta. Éramos quatro. Apenas eu saí de lá...\" Sua voz embarga. \"Eles morreram lá dentro. Aquele colosso amaldiçoado...\"");
        Interface.Pausa(2500);
        Interface.MostrarMensagem("\"Eu quero vingança. Quero que quem ou o que quer que viva no centro daquele inferno seja destruído. Traga-me uma prova. Traga-me o chifre daquela aberração. Se você conseguir... essa espada será sua. A Espada Jurada. Ela já cortou reis e demônios, mas agora só serve de muleta para um velho bêbado.\"");
        Interface.Pausa(3500);

        if (ficha.isMinotauroDerrotado()) {
            Interface.MostrarMensagem("\nVocê olha para ele, lembrando-se do labirinto escuro e do colosso que tombou diante de você.");
            Interface.Pausa(1500);
            if (ficha.temItem("Chifre de Minotauro")) {
                System.out.println("\n  1. Entregar o Chifre de Minotauro");
                System.out.println("  2. Não falar nada ainda e voltar depois");
                if (Interface.lerOpcao(2) == 1) {
                    ficha.removerItem("Chifre de Minotauro", 1);
                    ficha.adicionarItem(Vendedor.criarItem("Espada Jurada"));
                    Interface.MostrarMensagem("\nVocê coloca o pesado Chifre de Minotauro sobre a mesa.");
                    Interface.MostrarMensagem("Os olhos do velho Balthazar se arregalam. Ele toca a base ensanguentada do chifre, tremendo.");
                    Interface.Pausa(2000);
                    Interface.MostrarMensagem("\"Você... você fez isso. Eles finalmente podem descansar.\"");
                    Interface.MostrarMensagem("Balthazar desamarra a bainha da espada e a entrega para você. " + AMARELO + "+1x Espada Jurada" + RESET);
                    Interface.Pausa(2000);
                    ficha.encerrarMissao("O Labirinto Secreto");
                    ficha.setMissaoBalthazarEncerrada(true);
                }
            } else {
                Interface.MostrarMensagem("\nVocê diz a ele que já encontrou o labirinto e que matou a besta que vivia lá dentro.");
                Interface.Pausa(1500);
                Interface.MostrarMensagem("Balthazar te olha de cima a baixo. \"E cadê a prova? Você fala muito para quem não tem nada nas mãos.\"");
                Interface.Pausa(2000);
                Interface.MostrarMensagem("\"Achei que você fosse diferente, mas é só mais um falastrão! Vá embora!\"");
                Interface.Pausa(2000);
                Interface.MostrarMensagem("\n(Balthazar desiste da missão, descrente de que você ou qualquer um possa ajudá-lo.)");
                Interface.Pausa(2000);
                ficha.encerrarMissao("O Labirinto Secreto");
                ficha.setMissaoBalthazarEncerrada(true);
            }
        } else {
            System.out.println("\n  1. Aceitar o desafio");
            System.out.println("  2. Sair da mesa");
            if (Interface.lerOpcao(2) == 1) {
                Interface.MostrarMensagem("\nVocê acena positivamente. Ele levanta a caneca para você. \"Que os deuses te protejam.\"");
                if (!ficha.getNovidades("O Labirinto Secreto").stream().anyMatch(n -> n.contains("Falei com Balthazar"))) {
                    ficha.adicionarNovidade("O Labirinto Secreto", "!Falei com Balthazar. Ele quer o Chifre do Minotauro como prova de vingança.");
                }
                Interface.Pausa(1800);
            }
        }
    }
}

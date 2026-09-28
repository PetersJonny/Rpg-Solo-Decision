package eventos;

import fichas.FichaRpg;
import itens.ItemRpg;
import loja.Vendedor;
import missoes.QuadroDeMissoes;
import telas.Interface;

// A taverna da vila: um estabelecimento rústico com mesas de madeira, um dono
// dracônico de pele vermelha e um quadro de missões na parede. Primeiro o bando
// de goblins precisa ser resolvido (VilarejoDeScarbor.CenaDosGoblins); depois o
// dono agradece (com 30 moedas e, se o jogador estiver faminto, a "primeira
// comida é por conta da casa").
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
        Interface.cabecalhoMenu("TAVERNA DA VILA");
        Interface.MostrarMensagem("\nA taverna exala cheiro de madeira velha, fumaça de lareira e comida. Um " + VERMELHO + "dracônico de pele vermelha" + RESET + " limpa um copo de metal atrás do balcão.");

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

    // Recompensa e reconhecimento do dono (uma vez só)
    private static void CenaDoDono(FichaRpg ficha) {
        Interface.MostrarMensagem("\nO dracônico de pele vermelha vira para você e um largo sorriso surge em seu rosto escamado.");
        Interface.Pausa(1800);
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

        // Se estiver faminto, o dono oferece a primeira comida por conta da casa
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

            System.out.println("\n  O que você deseja fazer?\n");
            System.out.println("  1. " + CIANO + "Comer" + RESET + " (do cardápio da taverna)");
            System.out.println("  2. " + CIANO + "Ler o quadro de missões" + RESET + " na parede");
            System.out.println("  3. " + VERMELHO + "Sair da taverna" + RESET);
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            int escolha = Interface.lerOpcao(1, 3);

            if (escolha == 1) {
                Comer(ficha);
            } else if (escolha == 2) {
                QuadroDeMissoes.QuadroDeMissoes(ficha);
            } else {
                Interface.MostrarMensagem("\nVocê se levanta e sai da taverna, deixando o dracônico limpando seus copos.");
                Interface.Pausa(1500);
                return;
            }
        }
    }

    // Cardápio da taverna: cada prato vira um item consumível na mochila.
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
}
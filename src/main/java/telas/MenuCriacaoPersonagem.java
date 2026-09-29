package telas;

import itens.ItemRpg;
import salvamento.GerenciadorSaves;
import classes.ClasseRpg;
import fichas.FichaRpg;
import mecanicas.GerenciadorDeItens;
import java.util.Scanner;

import static telas.Interface.*;

public class MenuCriacaoPersonagem {

    public static int MenuCriacaoFicha() {
        System.out.println("\n");
        cabecalhoMenu("CRIAÇÃO DE PERSONAGEM");
        System.out.println("\n  O que deseja fazer?\n");
        System.out.println("  1. Escolher nome do personagem / alterar");
        System.out.println("  2. Distribuir pontos entre atributos / mudar pontos");
        System.out.println("  3. Escolher classe / mudar classe");
        System.out.println("  4. Escolher raça / mudar raça");
        System.out.println("  5. Escolher modo de dificuldade");
        System.out.println("  6. Mostrar ficha");
        System.out.println("  7. Finalizar criação do personagem");
        System.out.println("  8. Fechar o jogo\n");
        System.out.println("  " + VERDE + "Digite a opção:" + RESET);
        return lerOpcao(8);
    }

    public static int MenuEscolherDificuldade() {
        System.out.println("\n");
        cabecalhoMenu("MODO DE DIFICULDADE");
        System.out.println("\n  Escolha o modo da sua jornada:\n");
        System.out.println("  1. " + VERDE + "Normal" + RESET + " — " + fichas.ModoDificuldade.NORMAL.getDescricao());
        System.out.println("  2. " + AMARELO + "Difícil" + RESET + " — " + fichas.ModoDificuldade.DIFICIL.getDescricao());
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);
        return lerOpcao(0, 2);
    }

    public static int MenuDistribuirAtributos(int pontosSobrando) {
        cabecalhoMenu("DISTRIBUIR ATRIBUTOS");
        System.out.println("\n  Você tem " + AMARELO + pontosSobrando + RESET + " pontos para distribuir.\n");
        System.out.println("  Escolha qual atributo quer melhorar:\n");
        System.out.println("  1. Constituição");
        System.out.println("  2. Destreza");
        System.out.println("  3. Força");
        System.out.println("  4. Sabedoria");
        System.out.println("  5. Intelecto");
        System.out.println("  6. Presença\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        return lerOpcao(0, 6);
    }

    public static int PedirQuantidadePontos(int pontosSobrando) {
        cabecalhoMenu("QUANTIDADE DE PONTOS");
        System.out.println("\n  Quantos pontos deseja gastar? Tem " + AMARELO + pontosSobrando + RESET + " pontos ainda.");
        int gasto = lerInteiro();
        return gasto;
    }

    public static int MenuEscolherRaca() {
        cabecalhoMenu("ESCOLHA SUA RAÇA");
        System.out.println("\n  Escolha entre uma das 7 raças disponíveis:\n");
        System.out.println("  1. " + CIANO + "Humano" + RESET + " — +1 em um atributo à sua escolha e Vontade de Viver (sobrevive com 1 PV).");
        System.out.println("  2. " + CIANO + "Elfo da Floresta" + RESET + " — +1 Destreza e Toque da Mata (+30% de material ao coletar recursos).");
        System.out.println("  3. " + CIANO + "Vigia do Crepúsculo" + RESET + " — +1 Sabedoria e Visão na Penumbra (+2 em testes noturnos).");
        System.out.println("  4. " + CIANO + "Meio-Fada" + RESET + " — +1 Presença e Encanto Feérico (dobra a chance de encontrar fadas e reduz em 1 o custo de mana, nunca abaixo de 1).");
        System.out.println("  5. " + CIANO + "Dracônico" + RESET + " — +1 Constituição e Escamas de Dragão (+2 defesa e +2 vida máx).");
        System.out.println("  6. " + CIANO + "Meio-Orque" + RESET + " — +1 Força e Fúria Sombria (dado de dano das armas sobe 1 degrau com vida abaixo de 30%; em 1d12 ganha +1d4).");
        System.out.println("  7. " + CIANO + "Gnomo" + RESET + " — +1 Intelecto e Mente Afiada (pode rolar novamente um teste mental).\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        return lerOpcao(0, 7);
    }

    public static int MenuEscolherAtributoHumano() {
        cabecalhoMenu("ATRIBUTO DO HUMANO");
        System.out.println("\n  Como Humano, você escolhe em qual atributo receber +1:\n");
        System.out.println("  1. Constituição");
        System.out.println("  2. Destreza");
        System.out.println("  3. Força");
        System.out.println("  4. Sabedoria");
        System.out.println("  5. Intelecto");
        System.out.println("  6. Presença\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        return lerOpcao(0, 6);
    }

    public static int MenuEscolherClasse() {
        cabecalhoMenu("ESCOLHA SUA CLASSE");
        System.out.println("\n  Escolha entre uma das 3 classes abaixo:\n");
        System.out.println("  1. " + CIANO + "Mago" + RESET + " — só pode usar cajado, conjura magias poderosas, porém é mais frágil.");
        System.out.println("  2. " + CIANO + "Guerreiro" + RESET + " — só pode usar espada e atacar corpo a corpo, porém é mais resistente.");
        System.out.println("  3. " + CIANO + "Healer" + RESET + " — tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana.\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        return lerOpcao(0, 3);
    }

    public static String EscolherElementoMago() {
        cabecalhoMenu("ELEMENTO DO MAGO");
        System.out.println("\n  Como Mago, você deve escolher o elemento da sua Bola Elementar:\n");
        System.out.println("  1. Fogo");
        System.out.println("  2. Água");
        System.out.println("  3. Gelo");
        System.out.println("  4. Elétrico");
        System.out.println("  5. Terra");
        System.out.println("  6. Ácido\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        int escolha = lerOpcao(0, 6);
        switch (escolha) {
            case 1: return "Fogo";
            case 2: return "Água";
            case 3: return "Gelo";
            case 4: return "Elétrico";
            case 5: return "Terra";
            case 6: return "Ácido";
            default: return null;
        }
    }
}

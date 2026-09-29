package telas;

import itens.ItemRpg;
import salvamento.GerenciadorSaves;
import classes.ClasseRpg;
import fichas.FichaRpg;
import mecanicas.GerenciadorDeItens;
import java.util.Scanner;

import static telas.Interface.*;

public class MenuSalvamento {

    public static int MenuCarregarJogo() {
        while (true) {
            if (GerenciadorSaves.quantidadeSaves() == 0) {
                return -2;
            }

            System.out.println("\n");
            cabecalhoMenu("CARREGAR JOGO");
            System.out.println("\n  Escolha qual save continuar:\n");
            for (int slot = 1; slot <= GerenciadorSaves.MAX_SAVES; slot++) {
                String info = GerenciadorSaves.infoSlot(slot);
                if (GerenciadorSaves.existeSave(slot)) {
                    System.out.println("  " + slot + ". " + CIANO + info + RESET);
                } else {
                    System.out.println("  " + slot + ". " + AMARELO + info + RESET);
                }
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            int escolha = lerOpcao(0, GerenciadorSaves.MAX_SAVES);
            if (escolha == 0) return -1;
            if (GerenciadorSaves.existeSave(escolha)) {
                return escolha;
            }
            ExibirErro("Opção inválida ou save vazio!");
        }
    }

    public static int MenuSalvarJogo(FichaRpg ficha) {
        while (true) {
            System.out.println("\n");
            cabecalhoMenu("SALVAR JOGO");
            System.out.println("\n  Escolha o slot onde deseja salvar:\n");
            for (int slot = 1; slot <= GerenciadorSaves.MAX_SAVES; slot++) {
                String info = GerenciadorSaves.infoSlot(slot);
                if (GerenciadorSaves.existeSave(slot)) {
                    System.out.println("  " + slot + ". " + AMARELO + info + RESET + "  (será substituído)");
                } else {
                    System.out.println("  " + slot + ". " + VERDE + "Vazio" + RESET);
                }
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            int escolha = lerOpcao(0, GerenciadorSaves.MAX_SAVES);
            if (escolha == 0) return -1;
            return escolha;
        }
    }

    public static boolean MenuApagarSave() {
        while (true) {
            if (GerenciadorSaves.quantidadeSaves() == 0) {
                return false;
            }

            System.out.println("\n");
            cabecalhoMenu("APAGAR SAVE");
            System.out.println("\n  Escolha qual save deseja deletar:\n");
            for (int slot = 1; slot <= GerenciadorSaves.MAX_SAVES; slot++) {
                String info = GerenciadorSaves.infoSlot(slot);
                if (GerenciadorSaves.existeSave(slot)) {
                    System.out.println("  " + slot + ". " + CIANO + info + RESET);
                } else {
                    System.out.println("  " + slot + ". " + AMARELO + info + RESET);
                }
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            int escolha = lerOpcao(0, GerenciadorSaves.MAX_SAVES);
            if (escolha == 0) return false;
            if (!GerenciadorSaves.existeSave(escolha)) {
                ExibirErro("Opção inválida ou save vazio!");
                continue;
            }

                        System.out.println("\n  Tem certeza que deseja apagar o save " + escolha + "?\n");
            System.out.println("  " + AMARELO + GerenciadorSaves.infoSlot(escolha) + RESET);
            System.out.println("\n  1. Sim, apagar");
            System.out.println("  2. Não, manter\n");
            System.out.println("  " + VERDE + "Digite a opção:" + RESET);
            int confirma = lerOpcao(2);

            if (confirma == 1) {
                if (GerenciadorSaves.deletar(escolha)) {
                    MostrarMensagem("\n  Save " + escolha + " apagado com sucesso.");
                } else {
                    ExibirErro("Falha ao apagar o save.");
                }
                Pausa(1500);
                return true;
            }
        }
    }

    public static boolean PerguntarSalvarAntesDeSair() {
        System.out.println("\n  Deseja salvar o jogo antes de sair?\n");
        System.out.println("  1. Sim");
        System.out.println("  2. Não\n");
        System.out.println("  " + VERDE + "Digite a opção:" + RESET);
        return lerOpcao(2) == 1;
    }
}

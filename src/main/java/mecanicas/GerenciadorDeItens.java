package mecanicas;

import java.util.List;
import classes.*;
import itens.Arma;
import java.util.ArrayList;
import mecanicas.MecanicasRpg;
import criaturas.Criatura;
import itens.ItemRpg;
import java.util.HashSet;
import java.util.Set;
import fichas.FichaRpg;
import comandos.*;
import itens.Consumivel;
import telas.Interface;

import static mecanicas.MotorDeCombate.*;

public class GerenciadorDeItens {

    public static boolean ehItemConsumivel(ItemRpg item) {
        if (!(item instanceof Consumivel)) return false;
        return !item.getNome().equals("Flechas");
    }

    public static boolean usarItemForaDeCombate(FichaRpg ficha, ItemRpg item, int quantidade) {
        if (item == null || !ehItemConsumivel(item)) return false;
        int qtd = Math.min(Math.max(1, quantidade), item.getQuantidade());
        String nome = item.getNome();

        switch (nome) {
            case "Frutas": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(2);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                ficha.comerFrutas(qtd);
                Interface.MostrarMensagem("Você comeu " + qtd + "x Frutas e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Maçã": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(3);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                ficha.comerFrutas(qtd);
                Interface.MostrarMensagem("Você comeu " + qtd + "x Maçã e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Pera": {
                int cura = 0, mana = 0;
                for (int i = 0; i < qtd; i++) {
                    cura += MecanicasRpg.rolarDado(2);
                    mana += MecanicasRpg.rolarDado(3);
                }
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int antesMana = ficha.getManaPersonagem();
                ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + mana, ficha.getManaMaxima()));
                ficha.comerFrutas(qtd);
                Interface.MostrarMensagem("Você comeu " + qtd + "x Pera: +" + (ficha.getVidaPersonagem() - antes) + " de vida e +" + (ficha.getManaPersonagem() - antesMana) + " de mana! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                break;
            }
            case "Ameixa": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(2);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                ficha.comerFrutas(qtd);
                ficha.curarEnjoo();
                Interface.MostrarMensagem("Você comeu " + qtd + "x Ameixa e recuperou " + curaReal + " de vida" + (ficha.isEnjoado() ? "" : " e curou o enjoo") + "! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Uva": {
                int mana = 0;
                for (int i = 0; i < qtd; i++) mana += MecanicasRpg.rolarDado(4);
                int antes = ficha.getManaPersonagem();
                ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + mana, ficha.getManaMaxima()));
                ficha.comerFrutas(qtd);
                Interface.MostrarMensagem("Você comeu " + qtd + "x Uva e recuperou " + (ficha.getManaPersonagem() - antes) + " de mana! Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                break;
            }
            case "Morango Selvagem": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(3);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                ficha.comerFrutas(qtd);
                Interface.MostrarMensagem("Você comeu " + qtd + "x Morango Selvagem e recuperou " + (ficha.getVidaPersonagem() - antes) + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Figo Seco": {
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu " + qtd + "x Figo Seco. Não cura vida, mas sua fome foi totalmente saciada.");
                break;
            }
            case "Carne de Lobo": {
                int cura = 0;
                boolean estragou = false;
                for (int i = 0; i < qtd; i++) {
                    if (ficha.comerCarneCrua()) {
                        estragou = true;
                    } else {
                        cura += MecanicasRpg.rolarDado(3);
                    }
                }
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                if (estragou) {
                    Interface.MostrarMensagem("A carne de lobo estava ESTRAGADA, mas você come assim mesmo: " + (curaReal > 0 ? "só recuperou " + curaReal + " de vida e " : "não recupera vida e ") + "fica enjoado! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                } else {
                    Interface.MostrarMensagem("Você comeu " + qtd + "x Carne de Lobo (bem fresca!) e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                }
                break;
            }
            case "Carne de Urso": {
                int cura = 0;
                boolean estragou = false;
                for (int i = 0; i < qtd; i++) {
                    if (ficha.comerCarneCrua()) {
                        estragou = true;
                    } else {
                        cura += MecanicasRpg.rolarDado(4);
                    }
                }
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                if (estragou) {
                    Interface.MostrarMensagem("A carne de urso estava ESTRAGADA, mas você come assim mesmo: " + (curaReal > 0 ? "só recuperou " + curaReal + " de vida e " : "não recupera vida e ") + "fica enjoado! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                } else {
                    Interface.MostrarMensagem("Você comeu " + qtd + "x Carne de Urso (bem fresca!) e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                }
                break;
            }
            case "Carne de Lobo Cozida": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(3);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu " + qtd + "x Carne de Lobo Cozida e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Carne de Urso Cozida": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(4);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu " + qtd + "x Carne de Urso Cozida e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Carne Podre": {
                ficha.comerCarnePodre();
                break;
            }
                        case "Sopa do Vilarejo": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(2);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você tomou a Sopa do Vilarejo, recuperou " + (ficha.getVidaPersonagem() - antes) + " de vida e saciou a fome! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Pão Quente com Manteiga": {
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu o Pão Quente com Manteiga. Não cura vida, mas sua fome foi saciada.");
                break;
            }
            case "Ovos Mexidos": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(3);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu " + qtd + "x Ovos Mexidos, recuperou " + (ficha.getVidaPersonagem() - antes) + " de vida e saciou a fome! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Caldo de Lobo": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(4);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você tomou o Caldo de Lobo, recuperou " + (ficha.getVidaPersonagem() - antes) + " de vida e saciou a fome! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Peixe Assado": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(4);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu o Peixe Assado, recuperou " + (ficha.getVidaPersonagem() - antes) + " de vida e saciou a fome! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Estofado de Urso": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(6);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu o Estofado de Urso, recuperou " + (ficha.getVidaPersonagem() - antes) + " de vida e saciou a fome! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Torta de Frutas": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(3);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                ficha.comerComidaBoa();
                Interface.MostrarMensagem("Você comeu a Torta de Frutas, recuperou " + (ficha.getVidaPersonagem() - antes) + " de vida e saciou a fome! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                break;
            }
            case "Hidromel": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(4);
                int antes = ficha.getManaPersonagem();
                ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + cura, ficha.getManaMaxima()));
                Interface.MostrarMensagem("Você bebeu " + qtd + "x Hidromel e recuperou " + (ficha.getManaPersonagem() - antes) + " de mana! Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                break;
            }
            case "Poção de Mana": {
                int antes = ficha.getManaPersonagem();
                ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + 5 * qtd, ficha.getManaMaxima()));
                int curaMana = ficha.getManaPersonagem() - antes;
                Interface.MostrarMensagem("Você bebeu " + qtd + "x Poção de Mana e recuperou " + curaMana + " de mana! Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                break;
            }
            case "Poção Grande de Mana": {
                int antes = ficha.getManaPersonagem();
                ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + 7 * qtd, ficha.getManaMaxima()));
                int curaMana = ficha.getManaPersonagem() - antes;
                Interface.MostrarMensagem("Você bebeu " + qtd + "x Poção Grande de Mana e recuperou " + curaMana + " de mana! Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
                break;
            }
            case "Kit Médico": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(4);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                Interface.MostrarMensagem("Você usou o Kit Médico e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                if (ficha.isInfectado()) {
                    ficha.setInfectado(false);
                    Interface.MostrarMensagem("Os curativos do Kit Médico expulsam a infecção! Você está curado.");
                }
                break;
            }
            default:
                return false;
        }

        ficha.consumirItem(item, qtd);
        if (item.getQuantidade() <= 0) {
            ficha.getInventario().remove(item);
            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
        } else {
            Interface.MostrarMensagem("Restam " + item.getQuantidade() + "x " + item.getNome() + ".");
        }
        Interface.Pausa(1500);
        return true;
    }

    public static int escolherItemParaUsar(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("SUA MOCHILA");
            System.out.println("\n");

            if (ficha.getInventario().isEmpty()) {
                System.out.println("  Sua mochila está vazia.");
            } else {
                for (int i = 0; i < ficha.getInventario().size(); i++) {
                    ItemRpg item = ficha.getInventario().get(i);
                    String tipo = "";
                    if (ehItemConsumivel(item)) tipo = " [Consumível]";
                    else if (item instanceof Arma) tipo = " [Arma]";
                    else if (item.getNome().equals("Flechas")) tipo = " [Munição]";
                    System.out.println("  " + (i + 1) + ". " + CIANO + item.getNome() + RESET + " (x" + item.getQuantidade() + ")" + tipo);
                }
            }
            System.out.println("\n  " + VERDE + "0. Voltar ao combate" + RESET);
            System.out.println("  " + AMARELO + "9. Tentar fugir do combate" + RESET);

            int escolha = Interface.lerInteiro();

            if (escolha == 0) return -1;
            if (escolha == 9) return -2;

            if (ficha.getInventario().isEmpty()) {
                Interface.ExibirErro("Escolha inválida!");
                Interface.Pausa(1500);
                continue;
            }

            if (escolha > 0 && escolha <= ficha.getInventario().size()) {
                ItemRpg itemEscolhido = ficha.getInventario().get(escolha - 1);

                String descExibida = itemEscolhido.getDescricao();
                if (itemEscolhido.getNome().equals("Kit Médico")) {
                    descExibida = "Pode ser usado para curar 1d4 de vida e acaba com uma infecção. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção de Mana")) {
                    descExibida = "Restaura 5 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção Grande de Mana")) {
                    descExibida = "Restaura 7 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Frutas")) {
                    descExibida = "Cada fruta cura 1d2 de vida; comer 3 no dia conta como refeição completa (zera a fome). Frutas restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Carne de Lobo")) {
                    descExibida = "Carne CRUA: cura 1d3 de vida, mas pode estar estragada (30%). Cozinhe na fogueira para ficar segura. Carnes restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Carne de Urso")) {
                    descExibida = "Carne CRUA: cura 1d4 de vida, mas pode estar estragada (30%). Cozinhe na fogueira para ficar segura. Carnes restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Carne de Lobo Cozida")) {
                    descExibida = "Carne COZIDA na fogueira: cura 1d3 de vida, sem risco de estragar. Carnes restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Carne de Urso Cozida")) {
                    descExibida = "Carne COZIDA na fogueira: cura 1d4 de vida, sem risco de estragar. Carnes restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Sopa do Vilarejo")) {
                    descExibida = "Pratos da taverna da vila. Cura 1d2 de vida e sacia a fome. Restam: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Pão Quente com Manteiga")) {
                    descExibida = "Sacia a fome (não cura vida). Restam: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Ovos Mexidos")) {
                    descExibida = "Pratos da taverna da vila. Cura 1d3 de vida e sacia a fome. Restam: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Caldo de Lobo")) {
                    descExibida = "Pratos da taverna da vila. Cura 1d4 de vida e sacia a fome. Restam: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Peixe Assado")) {
                    descExibida = "Pratos da taverna da vila. Cura 1d4 de vida e sacia a fome. Restam: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Estofado de Urso")) {
                    descExibida = "Pratos da taverna da vila. Cura 1d6 de vida e sacia a fome. Restam: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Torta de Frutas")) {
                    descExibida = "Pratos da taverna da vila. Cura 1d3 de vida e sacia a fome. Restam: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Hidromel")) {
                    descExibida = "Restaura 1d4 de mana (não sacia a fome). Restam: " + itemEscolhido.getQuantidade();
                }
                System.out.println("\n" + itemEscolhido.getNome() + ": " + descExibida);
                Interface.Pausa(1000);

                if (ehItemConsumivel(itemEscolhido)) {
                    if (itemEscolhido.getNome().equals("Poção de Mana") && ficha.getManaPersonagem() >= ficha.getManaMaxima()) {
                        Interface.MostrarMensagem("Sua mana já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }
                    if (itemEscolhido.getNome().equals("Poção Grande de Mana") && ficha.getManaPersonagem() >= ficha.getManaMaxima()) {
                        Interface.MostrarMensagem("Sua mana já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }
                    if (itemEscolhido.getNome().equals("Kit Médico") && ficha.getVidaPersonagem() >= ficha.getVidaMaxima()) {
                        Interface.MostrarMensagem("Sua vida já está no máximo!");
                        Interface.Pausa(1500);
                        continue;
                    }

                    System.out.println("\nDeseja usar este item? (Usará sua ação quando chegar sua vez)");
                    System.out.println("1. Sim");
                    System.out.println("2. Não");
                    int confirmar = Interface.lerInteiro();

                    if (confirmar == 1) {
                        return escolha - 1;
                    }
                } else {
                    Interface.MostrarMensagem("Item não é consumível. Apenas visualização.");
                    Interface.Pausa(1500);
                }
            }
        }
    }

    public static void usarItemNaVez(FichaRpg ficha, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= ficha.getInventario().size()) return;
        ItemRpg itemEscolhido = ficha.getInventario().get(itemIndex);

        if (!ehItemConsumivel(itemEscolhido)) {
            Interface.MostrarMensagem("Item não é consumível.");
            Interface.Pausa(1500);
            return;
        }

        if (itemEscolhido.getNome().equals("Poção de Mana")) {
            ficha.setManaPersonagem(ficha.getManaPersonagem() + 5);
            Interface.MostrarMensagem("Você recuperou 5 de mana! Mana atual: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        } else if (itemEscolhido.getNome().equals("Poção Grande de Mana")) {
            ficha.setManaPersonagem(ficha.getManaPersonagem() + 7);
            Interface.MostrarMensagem("Você recuperou 7 de mana! Mana atual: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        } else if (itemEscolhido.getNome().equals("Frutas")) {
            int cura = MecanicasRpg.rolarDado(2);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerFrutas(1);
            Interface.MostrarMensagem("Você comeu uma fruta e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Carne de Lobo")) {
            if (ficha.comerCarneCrua()) {
                Interface.MostrarMensagem("Você morde a carne de lobo e sente um gosto estranho... a carne estava ESTRAGADA! Não recupera vida e fica enjoado.");
            } else {
                int cura = MecanicasRpg.rolarDado(3);
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                Interface.MostrarMensagem("Você comeu carne de lobo e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
            }
        } else if (itemEscolhido.getNome().equals("Carne de Urso")) {
            if (ficha.comerCarneCrua()) {
                Interface.MostrarMensagem("Você morde a carne de urso e sente um gosto estranho... a carne estava ESTRAGADA! Não recupera vida e fica enjoado.");
            } else {
                int cura = MecanicasRpg.rolarDado(4);
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                Interface.MostrarMensagem("Você comeu carne de urso e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
            }
        } else if (itemEscolhido.getNome().equals("Carne de Lobo Cozida")) {
            int cura = MecanicasRpg.rolarDado(3);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você comeu carne de lobo cozida e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Carne de Urso Cozida")) {
            int cura = MecanicasRpg.rolarDado(4);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você comeu carne de urso cozida e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Carne Podre")) {
            ficha.comerCarnePodre();
            Interface.MostrarMensagem("Você não recupera vida, mas sua fome é saciada (e você fica enjoado).");
        } else if (itemEscolhido.getNome().equals("Sopa do Vilarejo")) {
            int cura = MecanicasRpg.rolarDado(2);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você tomou a Sopa do Vilarejo e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Pão Quente com Manteiga")) {
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você comeu o Pão Quente com Manteiga e saciou a fome.");
        } else if (itemEscolhido.getNome().equals("Ovos Mexidos")) {
            int cura = MecanicasRpg.rolarDado(3);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você comeu os Ovos Mexidos e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Caldo de Lobo")) {
            int cura = MecanicasRpg.rolarDado(4);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você tomou o Caldo de Lobo e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Peixe Assado")) {
            int cura = MecanicasRpg.rolarDado(4);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você comeu o Peixe Assado e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Estofado de Urso")) {
            int cura = MecanicasRpg.rolarDado(6);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você comeu o Estofado de Urso e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Torta de Frutas")) {
            int cura = MecanicasRpg.rolarDado(3);
            ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
            ficha.comerComidaBoa();
            Interface.MostrarMensagem("Você comeu a Torta de Frutas e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Hidromel")) {
            int cura = MecanicasRpg.rolarDado(4);
            ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + cura, ficha.getManaMaxima()));
            Interface.MostrarMensagem("Você bebeu Hidromel e recuperou " + cura + " de mana! Mana atual: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        } else if (itemEscolhido.getNome().equals("Kit Médico")) {
                                    companheiros.Companheiro comp = ficha.getCompanheiro();
            boolean podeUsarEmSi = ficha.getVidaPersonagem() < ficha.getVidaMaxima() || ficha.isInfectado();
            boolean podeUsarCompanheiro = comp != null && comp.getFicha().getVidaPersonagem() < comp.getFicha().getVidaMaxima();

            boolean usarNoCompanheiro = false;
            if (podeUsarEmSi && podeUsarCompanheiro) {
                System.out.println("\n  Em quem deseja usar o Kit Médico?\n");
                System.out.println("  1. Em você");
                System.out.println("  2. Em " + comp.getNome());
                int quem = Interface.lerInteiro();
                usarNoCompanheiro = quem == 2;
            } else if (podeUsarCompanheiro) {
                System.out.println("\n  Usar o Kit Médico em " + comp.getNome() + "?\n");
                System.out.println("  1. Sim");
                System.out.println("  2. Não");
                int quem = Interface.lerInteiro();
                usarNoCompanheiro = quem == 1;
            } else if (!podeUsarEmSi) {
                Interface.ExibirErro("Sua vida já está no máximo!");
                Interface.Pausa(1500);
                return;
            }

            if (usarNoCompanheiro) {
                FichaRpg cf = comp.getFicha();
                int cura = MecanicasRpg.rolarDado(4);
                cf.setVidaPersonagem(Math.min(cf.getVidaPersonagem() + cura, cf.getVidaMaxima()));
                Interface.MostrarMensagem("Você usou o Kit Médico em " + comp.getNome() + " e ela(e) recuperou " + cura + " de vida! Vida: " + cf.getVidaPersonagem() + "/" + cf.getVidaMaxima());
            } else {
                int cura = MecanicasRpg.rolarDado(4);
                ficha.setVidaPersonagem(ficha.getVidaPersonagem() + cura);
                Interface.MostrarMensagem("Você recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());

                if (temHabilidade(ficha, "Cura Reforçada") && ficha.getManaPersonagem() >= 1) {
                    System.out.println("\nDeseja gastar 1 de mana para curar 2d4 extras com Cura Reforçada?");
                    System.out.println("1. Sim");
                    System.out.println("2. Não");
                    int usarCura = Interface.lerInteiro();

                    if (usarCura == 1) {
                        ficha.setManaPersonagem(ficha.getManaPersonagem() - 1);
                        int curaExtra = MecanicasRpg.rolarDado(4) + MecanicasRpg.rolarDado(4);
                        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + curaExtra);
                        Interface.MostrarMensagem("Cura Reforçada: você recuperou +" + curaExtra + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
                        Interface.Pausa(1500);
                    }
                }

                if (ficha.isInfectado()) {
                    ficha.setInfectado(false);
                    Interface.MostrarMensagem("Os curativos do Kit Médico expulsam a infecção! Você está curado.");
                    Interface.Pausa(2000);
                }
            }
        }

        ficha.consumirItem(itemEscolhido, 1);
        if (itemEscolhido.getQuantidade() <= 0) {
            ficha.getInventario().remove(itemEscolhido);
            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
        } else {
            Interface.MostrarMensagem("Restam " + itemEscolhido.getQuantidade() + "x " + itemEscolhido.getNome() + ".");
        }

        Interface.Pausa(2000);
    }
}

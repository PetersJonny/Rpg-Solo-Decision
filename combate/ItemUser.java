package combate;

import criaturas.Criatura;
import fichas.FichaRpg;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class ItemUser {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    public static boolean usarItemForaDeCombate(FichaRpg ficha, ItemRpg item, int quantidade) {
        if (item == null || !CombatResolver.ehItemConsumivel(item)) return false;
        int qtd = Math.min(Math.max(1, quantidade), item.getQuantidade());
        String nome = item.getNome();

        switch (nome) {
            case "Frutas": {
                int cura = 0;
                for (int i = 0; i < qtd; i++) cura += MecanicasRpg.rolarDado(2);
                int antes = ficha.getVidaPersonagem();
                ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + cura, ficha.getVidaMaxima()));
                int curaReal = ficha.getVidaPersonagem() - antes;
                Interface.MostrarMensagem("Você comeu " + qtd + "x Frutas e recuperou " + curaReal + " de vida! Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
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
                break;
            }
            default:
                return false;
        }

        item.setQuantidade(item.getQuantidade() - qtd);
        if (item.getQuantidade() <= 0) {
            ficha.getInventario().remove(item);
            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
        } else {
            Interface.MostrarMensagem("Restam " + item.getQuantidade() + "x " + item.getNome() + ".");
        }
        Interface.Pausa(1500);
        return true;
    }

    public static void usarItemNaVez(FichaRpg ficha, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= ficha.getInventario().size()) return;
        ItemRpg itemEscolhido = ficha.getInventario().get(itemIndex);

        if (!CombatResolver.ehItemConsumivel(itemEscolhido)) {
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
            Interface.MostrarMensagem("Você comeu uma fruta e recuperou " + cura + " de vida! Vida atual: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima());
        } else if (itemEscolhido.getNome().equals("Kit Médico")) {
            companheiros.Companheiro comp = ficha.getCompanheiro();
            boolean podeUsarEmSi = ficha.getVidaPersonagem() < ficha.getVidaMaxima();
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

                if (CombatResolver.temHabilidade(ficha, "Cura Reforçada") && ficha.getManaPersonagem() >= 1) {
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
            }
        }

        itemEscolhido.setQuantidade(itemEscolhido.getQuantidade() - 1);
        if (itemEscolhido.getQuantidade() <= 0) {
            ficha.getInventario().remove(itemEscolhido);
            Interface.MostrarMensagem("O item foi consumido e removido do inventário.");
        } else {
            Interface.MostrarMensagem("Restam " + itemEscolhido.getQuantidade() + "x " + itemEscolhido.getNome() + ".");
        }

        Interface.Pausa(2000);
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
                    if (CombatResolver.ehItemConsumivel(item)) tipo = " [Consumível]";
                    else if (item instanceof itens.Arma) tipo = " [Arma]";
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
                    descExibida = "Pode ser usado para curar 1d4 de vida. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção de Mana")) {
                    descExibida = "Restaura 5 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Poção Grande de Mana")) {
                    descExibida = "Restaura 7 pontos de mana. Usos restantes: " + itemEscolhido.getQuantidade();
                } else if (itemEscolhido.getNome().equals("Frutas")) {
                    descExibida = "Cada fruta cura 1d2 de vida. Frutas restantes: " + itemEscolhido.getQuantidade();
                }
                System.out.println("\n" + itemEscolhido.getNome() + ": " + descExibida);
                Interface.Pausa(1000);

                if (CombatResolver.ehItemConsumivel(itemEscolhido)) {
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
                    if ((itemEscolhido.getNome().equals("Kit Médico") || itemEscolhido.getNome().equals("Frutas")) && ficha.getVidaPersonagem() >= ficha.getVidaMaxima()) {
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
}

package fichas;

import classes.ClasseRpg;
import criaturas.Criatura;
import itens.Arma;
import itens.Armadura;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import racas.Raca;
import telas.Interface;

public class GerenciadorDeInventarioFicha {

    public static void adicionarItem(FichaRpg ficha, ItemRpg novoItem) {

        for (ItemRpg existente : ficha.inventario) {
            if (existente.getNome().equals(novoItem.getNome())) {
                existente.setQuantidade(existente.getQuantidade() + novoItem.getQuantidade());
                return;
            }
        }
        ficha.inventario.add(novoItem);

    }
    public static double getCapacidadeMochila(FichaRpg ficha) {

        return Math.max(10, 10 + 5.0 * ficha.getForca());

    }
    public static double getPesoTotalMochila(FichaRpg ficha) {

        double total = 0;
        for (ItemRpg item : ficha.inventario) {
            total += item.getPeso() * item.getQuantidade();
        }
        return total;

    }
    public static double getEspacoLivreMochila(FichaRpg ficha) {

        return ficha.getCapacidadeMochila() - ficha.getPesoTotalMochila();

    }
    public static boolean tentarAdicionarItem(FichaRpg ficha, ItemRpg novoItem) {

        double espacoNecessario = novoItem.getPeso() * novoItem.getQuantidade();
        double espacoLivre = ficha.getEspacoLivreMochila();
        if (espacoNecessario <= espacoLivre + 0.0001) {
            ficha.adicionarItem(novoItem);
            return true;
        }
        return false;

    }
    public static int adicionarItemLimitado(FichaRpg ficha, ItemRpg novoItem) {

        double espacoLivre = ficha.getEspacoLivreMochila();
        double pesoUnit = novoItem.getPeso();
        int qtd = novoItem.getQuantidade();
        if (pesoUnit <= 0 || espacoLivre <= 0) return 0;
        int qtdCabe = (int) Math.floor(espacoLivre / pesoUnit);
        int qtdPegar = Math.min(qtd, qtdCabe);
        if (qtdPegar <= 0) return 0;
        novoItem.setQuantidade(qtdPegar);
        ficha.adicionarItem(novoItem);
        return qtdPegar;

    }
    public static void coletarItemEncontrado(FichaRpg ficha, ItemRpg item, String origem) {

        double pesoUnit = item.getPeso();
        double espacoLivre = ficha.getEspacoLivreMochila();
        int qtd = item.getQuantidade();

        int cabemDeFato = (pesoUnit > 0) ? (int) Math.floor(espacoLivre / pesoUnit) : qtd;
        if (cabemDeFato < 0) cabemDeFato = 0;
        cabemDeFato = Math.min(qtd, cabemDeFato);

        if (cabemDeFato <= 0) {
            Interface.MostrarMensagem(origem + " " + qtd + "x " + item.getNome() + ", mas não há espaço na mochila! (Peso: " + String.format("%.1f", pesoUnit) + " cada, livre: " + String.format("%.1f", espacoLivre) + ")");
            Interface.Pausa(1500);
            return;
        }

        Interface.MostrarMensagem("-> " + origem + " " + qtd + "x " + item.getNome() + " (peso " + String.format("%.1f", pesoUnit) + " cada, espaço livre: " + String.format("%.1f", espacoLivre) + "/" + String.format("%.1f", ficha.getCapacidadeMochila()) + ").");
        Interface.Pausa(800);

        System.out.println("  Deseja pegar?");
        System.out.println("  1. Pegar tudo (" + cabemDeFato + "x)");
        System.out.println("  2. Escolher a quantidade");
        System.out.println("  3. Deixar para trás");
        int escolha = Interface.lerOpcao(3);

        int qtdPegar;
        if (escolha == 1) {
            qtdPegar = cabemDeFato;
        } else if (escolha == 2) {
            System.out.println("  Quantidade (1 a " + cabemDeFato + "):");
            int qtdEscolhida = Interface.lerInteiro();
            qtdPegar = Math.min(Math.max(0, qtdEscolhida), cabemDeFato);
            if (qtdPegar <= 0) {
                Interface.MostrarMensagem("-> Você não pegou nada.");
                Interface.Pausa(1000);
                return;
            }
        } else {
            Interface.MostrarMensagem("-> Você deixou " + item.getNome() + " para trás.");
            Interface.Pausa(1000);
            return;
        }

        item.setQuantidade(qtdPegar);
        ficha.adicionarItem(item);
        Interface.MostrarMensagem("-> Você coletou " + qtdPegar + "x " + item.getNome() + " (peso: " + String.format("%.1f", pesoUnit * qtdPegar) + "/" + String.format("%.1f", ficha.getCapacidadeMochila()) + ").");
        Interface.Pausa(1500);

    }
    public static boolean removerItem(FichaRpg ficha, String nome, int quantidade) {

        for (ItemRpg item : ficha.inventario) {
            if (item.getNome().equals(nome)) {
                int atual = item.getQuantidade();
                int remover = Math.min(atual, quantidade);
                if (atual - remover <= 0) {
                    ficha.inventario.remove(item);
                    if (ficha.armaEquipada != null && ficha.armaEquipada.getNome().equals(nome)) {
                        ficha.armaEquipada = null;
                    }
                    if (ficha.armaduraEquipada != null && ficha.armaduraEquipada.getNome().equals(nome)) {
                        ficha.armaduraEquipada = null;
                        ficha.equiparMelhorArmadura();
                    }
                } else {
                    item.setQuantidade(atual - remover);
                }
                return true;
            }
        }
        return false;

    }
    public static void consumirItem(FichaRpg ficha, ItemRpg item, int quantidade) {

        if (item != null) {
            item.setQuantidade(item.getQuantidade() - Math.max(0, quantidade));
        }

    }
    public static void equiparMelhorArmadura(FichaRpg ficha) {

        itens.Armadura melhor = null;
        for (ItemRpg item : new ArrayList<>(ficha.inventario)) {
            if (item instanceof itens.Armadura) {
                itens.Armadura arm = (itens.Armadura) item;
                if (melhor == null || arm.getBonusDefesa() > melhor.getBonusDefesa()) {
                    melhor = arm;
                }
            }
        }
        if (melhor != null) {
            if (ficha.armaduraEquipada != null) {
                ficha.inventario.add(ficha.armaduraEquipada);
            }
            ficha.inventario.remove(melhor);
            ficha.armaduraEquipada = melhor;
        }

    }
    public static boolean temItem(FichaRpg ficha, String nome) {

        if (ficha.inventario == null) return false;
        for (ItemRpg item : ficha.inventario) {
            if (item.getNome().equals(nome) && item.getQuantidade() > 0) {
                return true;
            }
        }
        return false;

    }
    public static int getQuantidadeDe(FichaRpg ficha, String nome) {

        int total = 0;
        if (ficha.inventario == null) return 0;
        for (ItemRpg item : ficha.inventario) {
            if (item.getNome().equals(nome)) {
                total += item.getQuantidade();
            }
        }
        return total;

    }
}

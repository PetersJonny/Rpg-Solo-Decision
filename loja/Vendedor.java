package loja;

import fichas.FichaRpg;
import itens.Arma;
import itens.Armadura;
import itens.Consumivel;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import telas.Interface;

public class Vendedor {

    // Itens de utilidade que o vendedor pode vender (materiais não entram no catálogo)
    private static final List<String> ITENS_UTILIDADE = List.of(
            "Faca", "Espada", "Cajado", "Bisturi", "Armadura Leve", "Poção de Mana", "Kit Médico"
    );

    public static void EncontrarVendedor(FichaRpg ficha) {
        Interface.barraDivisoria();
        Interface.MostrarMensagem("\nEntre as árvores, um vendedor ambulante coberto de peles surge na sua frente!");
        Interface.MostrarMensagem("\"Grandes novidades, aventureiro! Tenho tudo o que se pode sobreviver por aqui... e compro o que sobra.\"");
        Interface.Pausa(2500);

        while (true) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("\n--- VENDEDOR AMBULANTE ---");
            Interface.MostrarMensagem("Seu ouro: " + ficha.getOuro() + " moedas.");
            System.out.println("1. Comprar");
            System.out.println("2. Vender");
            System.out.println("3. Sair");

            int escolha = Interface.lerInteiro();
            if (escolha == 1) {
                Comprar(ficha);
            } else if (escolha == 2) {
                Vender(ficha);
            } else if (escolha == 3) {
                Interface.MostrarMensagem("\nVocê se despede do vendedor e segue seu caminho.");
                Interface.Pausa(1500);
                return;
            } else {
                Interface.ExibirErro("Opção inválida!");
            }
        }
    }

    // Estoque: 5 itens aleatórios de utilidade (sem Couro, Dente de Urso, Brilho Mágico etc.)
    private static void Comprar(FichaRpg ficha) {
        List<String> estoque = new ArrayList<>(ITENS_UTILIDADE);
        Collections.shuffle(estoque);
        estoque = estoque.subList(0, Math.min(5, estoque.size()));

        while (true) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("\n--- COMPRAR --- (Seu ouro: " + ficha.getOuro() + ")");
            for (int i = 0; i < estoque.size(); i++) {
                String nome = estoque.get(i);
                System.out.println((i + 1) + ". " + nome + " - " + precoDeVenda(nome) + " ouro");
            }
            System.out.println("0. Voltar");

            int escolha = Interface.lerInteiro();
            if (escolha == 0) return;
            if (escolha < 1 || escolha > estoque.size()) {
                Interface.ExibirErro("Opção inválida!");
                continue;
            }

            String nome = estoque.get(escolha - 1);
            int preco = precoDeVenda(nome);
            if (!ficha.gastarOuro(preco)) {
                Interface.ExibirErro("Ouro insuficiente!");
                Interface.Pausa(1500);
                continue;
            }
            ficha.adicionarItem(criarItem(nome));
            Interface.MostrarMensagem("\nVocê comprou: " + nome + " por " + preco + " ouro.");
            Interface.Pausa(1500);
        }
    }

    // O vendedor compra QUALQUER item do jogador (inclusive materiais) por 50% do preço de venda
    private static void Vender(FichaRpg ficha) {
        while (true) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("\n--- VENDER --- (Seu ouro: " + ficha.getOuro() + ")");
            Interface.MostrarMensagem("O vendedor paga 50% do preço de venda dele.");

            List<ItemRpg> vendaveis = new ArrayList<>();
            for (ItemRpg item : ficha.getInventario()) {
                if (item.getQuantidade() > 0) {
                    vendaveis.add(item);
                }
            }

            if (vendaveis.isEmpty()) {
                Interface.MostrarMensagem("Você não possui itens para vender.");
                Interface.Pausa(1500);
                return;
            }

            for (int i = 0; i < vendaveis.size(); i++) {
                ItemRpg item = vendaveis.get(i);
                System.out.println((i + 1) + ". " + item.getNome() + " (x" + item.getQuantidade() + ") - " + precoDeCompra(item.getNome()) + " ouro/un.");
            }
            System.out.println("0. Voltar");

            int escolha = Interface.lerInteiro();
            if (escolha == 0) return;
            if (escolha < 1 || escolha > vendaveis.size()) {
                Interface.ExibirErro("Opção inválida!");
                continue;
            }

            ItemRpg item = vendaveis.get(escolha - 1);
            int quantidade = item.getQuantidade();
            if (quantidade > 1) {
                System.out.println("Quantidade para vender (1 a " + quantidade + "): ");
                int qtd = Interface.lerInteiro();
                if (qtd < 1 || qtd > quantidade) {
                    Interface.ExibirErro("Quantidade inválida!");
                    continue;
                }
                quantidade = qtd;
            }

            int total = precoDeCompra(item.getNome()) * quantidade;
            ficha.adicionarOuro(total);
            ficha.removerItem(item.getNome(), quantidade);
            Interface.MostrarMensagem("\nVocê vendeu " + quantidade + "x " + item.getNome() + " por " + total + " ouro.");
            Interface.Pausa(1500);
        }
    }

    // Preço de venda do vendedor (valor cheio, usado quando o jogador COMPRA)
    private static int precoDeVenda(String nome) {
        switch (nome) {
            case "Faca": return 20;
            case "Espada": return 40;
            case "Cajado": return 30;
            case "Bisturi": return 40;
            case "Armadura Leve": return 50;
            case "Poção de Mana": return 12;
            case "Poção de Vida": return 14;
            case "Kit Médico": return 18;
            default: return 100;
        }
    }

    // Preço que o vendedor paga (jogador VENDE por 50% do preço dele)
    private static int precoDeCompra(String nome) {
        int precoBase;
        switch (nome) {
            case "Couro": precoBase = 6; break;
            case "Dente de Urso": precoBase = 10; break;
            case "Brilho Mágico": precoBase = 25; break;
            case "Kit Médico": precoBase = 18; break;
            case "Poção de Mana": precoBase = 12; break;
            case "Poção de Vida": precoBase = 14; break;
            default: precoBase = precoDeVenda(nome); break;
        }
        return precoBase / 2;
    }

    // Cria a instância do item para venda/compra
    private static ItemRpg criarItem(String nome) {
        switch (nome) {
            case "Faca":
                return new Arma("Faca", "Uma faca afiada que causa 1d4 de dano corpo a corpo, usando Destreza.", "CaC", 4, 1, 1, "Destreza");
            case "Espada":
                return new Arma("Espada", "Uma espada de aço afiada que causa 1d8 de dano.", "CaC", 8, 1, 1);
            case "Cajado":
                return new Arma("Cajado", "Um cajado de madeira simples que causa 1d4 de dano. Pode ser usado para canalizar magia ou para bater.", "CaC/mágico", 4, 1, 1);
            case "Bisturi":
                return new Arma("Bisturi", "Um bisturi afiado e rápido, perfeito para cortes precisos. Causa 1d4 de dano.", "CaC", 4, 1, 1, "Ágil", true);
            case "Armadura Leve":
                return new Armadura("Armadura Leve", "Oferece proteção básica para combate. Concede +3 de Defesa.", 3, 1);
            case "Poção de Mana":
                return new Consumivel("Poção de Mana", "Restaura 5 pontos de mana. É consumida após o uso.", 1);
            case "Kit Médico":
                return new Consumivel("Kit Médico", "Pode ser usado para curar 1d4 de vida. Possui 5 usos.", 1);
            default:
                return null;
        }
    }
}
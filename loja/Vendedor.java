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

    // Categorias (usadas só pela loja; não aparecem no jogo)
    private static final List<String> GERAL = List.of(
            "Faca", "Machado", "Machadinha", "Martelo", "Mangual", "Arco", "Flechas", "Lança",
            "Armadura Leve", "Poção de Mana", "Kit Médico"
    );
    private static final List<String> PESADO = List.of(
            "Espada", "Espada Pesada", "Machado de Guerra", "Martelo de Guerra", "Armadura Pesada"
    );
    private static final List<String> AGIL = List.of(
            "Bisturi", "Arco Refinado", "Nunchako", "Foice"
    );
    private static final List<String> MAGICO = List.of(
            "Cajado", "Chapéu Mágico", "Poção Grande de Mana", "Pequeno Grimório"
    );

    private static final int TAMANHO_ESTOQUE = 5;

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

    // Estoque: 5 itens aleatórios entre os gerais + os da categoria da classe do jogador
    private static List<String> montarEstoque(FichaRpg ficha) {
        List<String> pool = new ArrayList<>(GERAL);
        if (ficha.getClasseDoPersonagem() instanceof classes.Guerreiro) {
            pool.addAll(PESADO);
        } else if (ficha.getClasseDoPersonagem() instanceof classes.Healer) {
            pool.addAll(AGIL);
        } else if (ficha.getClasseDoPersonagem() instanceof classes.Mago) {
            pool.addAll(MAGICO);
        }
        Collections.shuffle(pool);
        return pool.subList(0, Math.min(TAMANHO_ESTOQUE, pool.size()));
    }

    private static void Comprar(FichaRpg ficha) {
        List<String> estoque = montarEstoque(ficha);

        while (true) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("\n--- COMPRAR --- (Seu ouro: " + ficha.getOuro() + ")");
            for (int i = 0; i < estoque.size(); i++) {
                String nome = estoque.get(i);
                String preco = nome.equals("Flechas") ? "3 ouro/un." : precoDeVenda(nome) + " ouro";
                System.out.println((i + 1) + ". " + nome + " - " + preco);
            }
            System.out.println("0. Voltar");

            int escolha = Interface.lerInteiro();
            if (escolha == 0) return;
            if (escolha < 1 || escolha > estoque.size()) {
                Interface.ExibirErro("Opção inválida!");
                continue;
            }

            String nome = estoque.get(escolha - 1);

            if (nome.equals("Flechas")) {
                comprarFlechas(ficha);
                continue;
            }

            int preco = precoDeVenda(nome);
            if (preco < 0) {
                Interface.ExibirErro("Esse item não está disponível!");
                continue;
            }
            if (!ficha.gastarOuro(preco)) {
                Interface.ExibirErro("Ouro insuficiente!");
                Interface.Pausa(1500);
                continue;
            }

            ItemRpg item = criarItem(nome);
            if (item instanceof Armadura) {
                int bonusAtual = ficha.getArmaduraEquipada() != null ? ficha.getArmaduraEquipada().getBonusDefesa() : 0;
                ficha.adicionarItem(item);
                ficha.equiparMelhorArmadura();
                int bonusNovo = ficha.getArmaduraEquipada() != null ? ficha.getArmaduraEquipada().getBonusDefesa() : 0;
                Interface.MostrarMensagem("\nVocê comprou: " + nome + " por " + preco + " ouro.");
                Interface.Pausa(1500);
                if (bonusNovo > bonusAtual) {
                    Interface.MostrarMensagem("(Sua melhor armadura foi equipada automaticamente! Defesa: " + ficha.getDefesa() + ")");
                } else {
                    Interface.MostrarMensagem("(O item foi guardado na mochila.)");
                }
            } else {
                ficha.adicionarItem(item);
                Interface.MostrarMensagem("\nVocê comprou: " + nome + " por " + preco + " ouro.");
            }
            Interface.Pausa(1500);
        }
    }

    // Compra de flechas: venda pela quantidade
    private static void comprarFlechas(FichaRpg ficha) {
        System.out.println("Quantas flechas quer comprar? (3 ouro cada)");
        int qtd = Interface.lerInteiro();
        if (qtd < 1) {
            Interface.ExibirErro("Quantidade inválida!");
            return;
        }
        int custo = qtd * 3;
        if (!ficha.gastarOuro(custo)) {
            Interface.ExibirErro("Ouro insuficiente!");
            Interface.Pausa(1500);
            return;
        }
        ficha.adicionarItem(new Consumivel("Flechas", "Munição para armas à distância. Consumida a cada disparo.", qtd));
        Interface.MostrarMensagem("\nVocê comprou " + qtd + "x Flechas por " + custo + " ouro.");
        Interface.Pausa(1500);
    }

    // O vendedor compra QUALQUER item do jogador (inclusive materiais) por 50% do preço de venda;
    // materiais sem utilidade são comprados a preço CHEIO
    private static void Vender(FichaRpg ficha) {
        while (true) {
            Interface.barraDivisoria();
            Interface.MostrarMensagem("\n--- VENDER --- (Seu ouro: " + ficha.getOuro() + ")");
            Interface.MostrarMensagem("O vendedor paga 50% do preço (materiais a preço cheio).");

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
            if (quantidade > 1 && !item.getNome().equals("Kit Médico")) {
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
            case "Machado": return 35;
            case "Machadinha": return 20;
            case "Martelo": return 50;
            case "Mangual": return 35;
            case "Arco": return 40;
            case "Flechas": return 3;
            case "Lança": return 50;
            case "Armadura Leve": return 60;
            case "Poção de Mana": return 15;
            case "Kit Médico": return 20;
            case "Espada": return 40;
            case "Espada Pesada": return 70;
            case "Machado de Guerra": return 90;
            case "Martelo de Guerra": return 90;
            case "Armadura Pesada": return 100;
            case "Bisturi": return 25;
            case "Arco Refinado": return 60;
            case "Nunchako": return 45;
            case "Foice": return 55;
            case "Cajado": return 30;
            case "Chapéu Mágico": return 50;
            case "Poção Grande de Mana": return 25;
            case "Pequeno Grimório": return 60;
            default: return -1;
        }
    }

    // Preço que o vendedor paga (jogador VENDE por 50% do preço dele);
    // materiais sem utilidade são comprados a preço CHEIO
    private static int precoDeCompra(String nome) {
        switch (nome) {
            case "Couro": return 6;
            case "Dente de Urso": return 14;
            case "Brilho Mágico": return 75;
            default:
                int preco = precoDeVenda(nome);
                return preco < 0 ? 5 : preco / 2;
        }
    }

    // Cria a instância do item para venda/compra
    private static ItemRpg criarItem(String nome) {
        switch (nome) {
            case "Faca":
                return new Arma("Faca", "Uma faca afiada que causa 1d4 de dano corpo a corpo, usando Destreza.", "CaC", 4, 1, 1, "Destreza");
            case "Machado":
                return new Arma("Machado", "Um machado robusto que causa 1d6 de dano, usando Força.", "CaC", 6, 1, 1);
            case "Machadinha":
                return new Arma("Machadinha", "Uma machadinha leve que causa 1d4 de dano, usando Força.", "CaC", 4, 1, 1);
            case "Martelo":
                return new Arma("Martelo", "Um martelo pesado que causa 1d8 de dano, usando Força.", "CaC", 8, 1, 1);
            case "Mangual":
                return new Arma("Mangual", "Um mangual de corrente que causa 1d6 de dano, usando Força.", "CaC", 6, 1, 1);
            case "Arco":
                return new Arma("Arco", "Um arco de madeira que dispara flechas, causando 1d6 de dano à distância. Consome flechas.", "LA", 6, 1, 1);
            case "Lança":
                return new Arma("Lança", "Uma lança versátil que causa 1d6 de dano, usando seu maior atributo entre Força e Destreza.", "CaC", 6, 1, 1, "Ágil", true);
            case "Espada":
                return new Arma("Espada", "Uma espada de aço afiada que causa 1d8 de dano.", "CaC", 8, 1, 1);
            case "Espada Pesada":
                return new Arma("Espada Pesada", "Uma espada gigante que causa 1d10 de dano, usando Força.", "CaC", 10, 1, 1);
            case "Machado de Guerra":
                return new Arma("Machado de Guerra", "Um machado de batalha que causa 1d12 de dano, usando Força.", "CaC", 12, 1, 1);
            case "Martelo de Guerra":
                return new Arma("Martelo de Guerra", "Um martelo de guerra que causa 1d12 de dano, usando Força.", "CaC", 12, 1, 1);
            case "Bisturi":
                return new Arma("Bisturi", "Um bisturi afiado e rápido, perfeito para cortes precisos. Causa 1d4 de dano.", "CaC", 4, 1, 1, "Ágil", true);
            case "Arco Refinado":
                return new Arma("Arco Refinado", "Um arco refinado que dispara flechas, causando 1d8 de dano à distância. Consome flechas.", "LA", 8, 1, 1);
            case "Nunchako":
                return new Arma("Nunchako", "Um nunchako rápido que causa 1d6 de dano, usando Destreza.", "CaC", 6, 1, 1, "Destreza");
            case "Foice":
                return new Arma("Foice", "Uma foice de lâmina curva que causa 1d8 de dano à distância. Consome flechas.", "LA", 8, 1, 1);
            case "Cajado":
                return new Arma("Cajado", "Um cajado de madeira simples que causa 1d4 de dano. Pode ser usado para canalizar magia ou para bater.", "CaC/mágico", 4, 1, 1);
            case "Armadura Leve":
                return new Armadura("Armadura Leve", "Oferece proteção básica para combate. Concede +3 de Defesa.", 3, 1);
            case "Armadura Pesada":
                return new Armadura("Armadura Pesada", "Uma armadura imponente que concede +5 de Defesa.", 5, 1);
            case "Poção de Mana":
                return new Consumivel("Poção de Mana", "Restaura 5 pontos de mana. É consumida após o uso.", 1);
            case "Poção Grande de Mana":
                return new Consumivel("Poção Grande de Mana", "Restaura 7 pontos de mana. É consumida após o uso.", 1);
            case "Kit Médico":
                return new Consumivel("Kit Médico", "Pode ser usado para curar 1d4 de vida. Possui 5 usos.", 1);
            case "Chapéu Mágico":
                return new ItemRpg("Chapéu Mágico", "Um chapéu encantado que aumenta o dano das suas magias em +3.", 1);
            case "Pequeno Grimório":
                return new ItemRpg("Pequeno Grimório", "Faz as suas magias custarem 1 de mana a menos.", 1);
            default:
                return null;
        }
    }
}
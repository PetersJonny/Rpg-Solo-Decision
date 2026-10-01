package eventos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fichas.FichaRpg;
import itens.ItemRpg;
import loja.Vendedor;
import telas.Interface;

public class CasaDoChapeuMagico {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    private static final List<String> ACERVO = List.of(
            "Chapéu Mágico", "Pequeno Grimório", "Poção Grande de Mana",
            "Manto do Astrólogo", "Gema de Mana", "Amuleto do Coração", "Ampulheta de Prata",
            "Luvas de Prata", "Pó de Midas", "Gota de Veneno"
    );

    private static final List<String> CONSUMIVEIS = List.of(
            "Poção Grande de Mana", "Gota de Veneno"
    );

    private static final int ITENS_POR_DIA = 5;
    private static final int CONSUMIVEL_MIN = 1;
    private static final int CONSUMIVEL_MAX = 5;

    private static List<String> sortearEstoqueDoDia(FichaRpg ficha) {
        List<String> pool = new ArrayList<>(ACERVO);
        Collections.shuffle(pool, new Random(ficha.getDiaAtual() * 7919L + 104729L));
        return new ArrayList<>(pool.subList(0, Math.min(ITENS_POR_DIA, pool.size())));
    }

    private static void renovarEstoqueSeNovoDia(FichaRpg ficha, List<String> estoque) {
        if (ficha.getEstoqueLojaDia() == ficha.getDiaAtual() && ficha.getDiaAtual() != 0) return;

        List<String> base = sortearEstoqueDoDia(ficha);
        ficha.getEstoqueConsumiveisLoja().clear();
        for (String nome : base) {
            if (!CONSUMIVEIS.contains(nome)) continue;
            int qtd = 1 + new Random(ficha.getDiaAtual() * 31L + nome.hashCode()).nextInt(CONSUMIVEL_MAX - CONSUMIVEL_MIN + 1);
            ficha.getEstoqueConsumiveisLoja().put(nome, qtd);
        }
        ficha.setEstoqueLojaDia(ficha.getDiaAtual());
    }

    public static int quantidadeEmEstoque(FichaRpg ficha, String nome) {
        if (CONSUMIVEIS.contains(nome)) {
            Integer qtd = ficha.getEstoqueConsumiveisLoja().get(nome);
            return qtd == null ? 0 : qtd;
        }
        return 1;
    }

    public static void renovarEstoqueSeNovoDiaPublico(FichaRpg ficha) {
        renovarEstoqueSeNovoDia(ficha, sortearEstoqueDoDia(ficha));
    }

    public static boolean podeComprarItem(FichaRpg ficha, String nome) {
        if (!CONSUMIVEIS.contains(nome) && ficha.temItem(nome)) return false;
        return quantidadeEmEstoque(ficha, nome) > 0;
    }

    public static boolean ehItemMagico(String nome) {
        return ACERVO.contains(nome);
    }

    private static void Consumir(FichaRpg ficha, String nome, int qtd) {
        if (!CONSUMIVEIS.contains(nome)) return;
        Integer atual = ficha.getEstoqueConsumiveisLoja().get(nome);
        if (atual == null || atual <= 0) return;
        int restante = atual - qtd;
        if (restante <= 0) {
            ficha.getEstoqueConsumiveisLoja().remove(nome);
        } else {
            ficha.getEstoqueConsumiveisLoja().put(nome, restante);
        }
    }

    public static void CasaDoChapeuMagico(FichaRpg ficha) {
        Interface.cabecalhoMenu("CASA DO CHAPÉU MÁGICO");

        List<String> estoque = sortearEstoqueDoDia(ficha);
        renovarEstoqueSeNovoDia(ficha, estoque);

        if (!ficha.isChapeuMagicoConhecido()) {
            Interface.MostrarMensagem("\nDe duas janelas escura escapa uma fumaça roxa e doce. A porta tem uma placa de bronze com " + CIANO + "CHAPÉU MÁGICO" + RESET + " e um chapéu torto pregado no lugar da aldrava.");
            Interface.Pausa(2200);
            Interface.MostrarMensagem("Uma mulher minúscula, do tamanho de um gato, desce voando de uma prateleira e pousa no balcão. " + CIANO + "\"Sou a Dona Maga. Compro e vendo o que brilha, cheira a enxofre e não serve para ceifar.\"" + RESET);
            Interface.Pausa(2200);
            Interface.MostrarMensagem("Ela aponta uma varinha para a sua mochila: " + AMARELO + "\"Nada de armas, armaduras ou comida. Só o que é de magia. E levo só uma de cada coisa — não sirvo duplicata.\"" + RESET);
            Interface.Pausa(2000);
            ficha.setChapeuMagicoConhecido(true);
        } else {
            Interface.MostrarMensagem("\nDona Maga está reordenando frascos na prateleira: \"Voltou! Olha o estoque de hoje, tá mudando.\"");
            Interface.Pausa(1500);
        }

        while (true) {
            Interface.cabecalhoMenu("CHAPÉU MÁGICO");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + " moedas.");
            Interface.MostrarMensagem("  O estoque muda a cada dia. Só item mágico, e " + CIANO + "1 de cada não-consumível" + RESET + ".");
            if (ficha.temItem("Gota de Veneno")) {
                Interface.MostrarMensagem("  " + AMARELO + "Gota de Veneno no seu inventário: " + ficha.getQuantidadeDe("Gota de Veneno") + "." + RESET);
            }

            System.out.println("\n  O que você deseja fazer?\n");
            System.out.println("  1. Comprar do estoque do dia");
            System.out.println("  2. Vender itens mágicos");
            System.out.println("  3. Sair");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(1, 3);
            if (escolha == 1) {
                Comprar(ficha, estoque);
            } else if (escolha == 2) {
                Vender(ficha);
            } else {
                Interface.MostrarMensagem("\nDona Maga acena com a varinha: \"Volte quando quiser mais brilho.\"");
                Interface.Pausa(1500);
                return;
            }
        }
    }

    private static void Comprar(FichaRpg ficha, List<String> estoque) {
        while (true) {
            Interface.cabecalhoMenu("COMPRAR NA CASA DO CHAPÉU MÁGICO");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + "\n");

            List<String> disponiveis = new ArrayList<>();
            for (String nome : estoque) {
                boolean jaTem = !CONSUMIVEIS.contains(nome) && ficha.temItem(nome);
                if (jaTem) {
                    System.out.println("  " + VERDE + "- " + nome + RESET + " (" + VERDE + "você já tem, esgotado para você" + RESET + ")");
                    continue;
                }
                int restante = quantidadeEmEstoque(ficha, nome);
                if (restante <= 0) {
                    System.out.println("  " + VERDE + "- " + nome + RESET + " (" + VERDE + "esgotado hoje" + RESET + ")");
                    continue;
                }
                disponiveis.add(nome);
                int preco = Vendedor.precoBase(nome);
                String sufixo = CONSUMIVEIS.contains(nome) ? " (" + restante + " em estoque)" : "";
                System.out.println("  (" + disponiveis.size() + "). " + nome + " - " + preco + " ouro" + sufixo);
            }

            if (disponiveis.isEmpty()) {
                Interface.MostrarMensagem("\n" + AMARELO + "Nada disponível hoje. A Dona Maga limpa a prateleira." + RESET);
                Interface.Pausa(2000);
                return;
            }

            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);
            System.out.println("\n  Escolha um item para comprar:");
            int escolha = Interface.lerOpcao(0, disponiveis.size());
            if (escolha == 0) return;

            String nome = disponiveis.get(escolha - 1);
            int preco = Vendedor.precoBase(nome);
            ItemRpg detalhe = Vendedor.criarItem(nome);

            System.out.println("\n  " + CIANO + "-- " + nome.toUpperCase() + " --" + RESET);
            if (detalhe != null) {
                System.out.println("  Descrição: " + detalhe.getDescricao());
            }
            System.out.println("  Preço: " + preco + " ouro");
            if (!CONSUMIVEIS.contains(nome)) {
                System.out.println("  " + AMARELO + "(você só pode ter 1 deste)" + RESET);
            }
            System.out.println(CIANO + "  -----------------------" + RESET);
            System.out.println("\n  Deseja comprar este item?\n");
            System.out.println("  1. Sim");
            System.out.println("  2. Não");
            if (Interface.lerOpcao(2) != 1) {
                Interface.MostrarMensagem("\nCompra cancelada.");
                Interface.Pausa(1000);
                continue;
            }

            if (!ficha.gastarOuro(preco)) {
                Interface.ExibirErro("Ouro insuficiente! (Precisa de " + preco + ")");
                Interface.Pausa(1500);
                continue;
            }

            ItemRpg item = Vendedor.criarItem(nome);
            if (item == null) {
                ficha.adicionarOuro(preco);
                Interface.ExibirErro("Erro: não foi possível criar o item. Ouro devolvido.");
                Interface.Pausa(1500);
                continue;
            }

            int qtd = CONSUMIVEIS.contains(nome) ? Math.max(1, quantidadeEmEstoque(ficha, nome)) : 1;
            item.setQuantidade(qtd);

            double espaco = item.getPeso() * qtd;
            if (espaco > ficha.getEspacoLivreMochila() + 0.0001) {
                ficha.adicionarOuro(preco);
                Interface.ExibirErro("Sua mochila não tem espaço para " + qtd + "x " + nome + "! (livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
                Interface.Pausa(1500);
                continue;
            }

            if (CONSUMIVEIS.contains(nome)) {
                Consumir(ficha, nome, qtd);
            }

            int vidaAntes = ficha.getVidaMaxima();
            int manaAntes = ficha.getManaMaxima();
            ficha.adicionarItem(item);
            int vidaDepois = ficha.getVidaMaxima();
            int manaDepois = ficha.getManaMaxima();

            Interface.MostrarMensagem("\nVocê comprou " + nome + " da Dona Maga por " + preco + " ouro.");
            Interface.Pausa(1500);
            if (vidaDepois > vidaAntes) {
                ficha.setVidaPersonagem(ficha.getVidaPersonagem() + (vidaDepois - vidaAntes));
                Interface.MostrarMensagem("(" + CIANO + nome + "! +" + (vidaDepois - vidaAntes) + " de vida máxima, e sua vida sobe junto)" + RESET);
                Interface.Pausa(1500);
            }
            if (manaDepois > manaAntes) {
                ficha.setManaPersonagem(ficha.getManaPersonagem() + (manaDepois - manaAntes));
                Interface.MostrarMensagem("(" + CIANO + nome + "! +" + (manaDepois - manaAntes) + " de mana máxima, e sua mana sobe junto)" + RESET);
                Interface.Pausa(1500);
            }
        }
    }

    private static void Vender(FichaRpg ficha) {
        List<ItemRpg> vendaveis = new ArrayList<>();
        for (ItemRpg item : ficha.getInventario()) {
            if (item.getQuantidade() > 0 && ehItemMagico(item.getNome())) {
                vendaveis.add(item);
            }
        }

        if (vendaveis.isEmpty()) {
            Interface.MostrarMensagem("\nDona Maga faz uma careta: \"" + AMARELO + "Nada disso brilha, nada disso cheira a enxofre. Só vendo coisa de magia." + RESET + "\"");
            Interface.Pausa(2000);
            return;
        }

        while (true) {
            Interface.cabecalhoMenu("VENDER À DONA MAGA");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET);
            Interface.MostrarMensagem("  A Dona Maga paga 80% do valor de cada item mágico.\n");

            for (int i = 0; i < vendaveis.size(); i++) {
                ItemRpg item = vendaveis.get(i);
                int preco = Vendedor.precoDeCompraMelhorado(item.getNome());
                System.out.println("  " + (i + 1) + ". " + item.getNome() + " (x" + item.getQuantidade() + ") - " + preco + " ouro/un.");
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Escolha um item para vender:");
            int escolha = Interface.lerOpcao(0, vendaveis.size());
            if (escolha == 0) return;

            ItemRpg item = vendaveis.get(escolha - 1);
            int quantidade = item.getQuantidade();
            if (quantidade > 1) {
                System.out.println("  Quantidade para vender (1 a " + quantidade + "): ");
                int qtd = Interface.lerInteiro();
                if (qtd < 1 || qtd > quantidade) {
                    Interface.ExibirErro("Quantidade inválida!");
                    Interface.Pausa(1500);
                    continue;
                }
                quantidade = qtd;
            }

            int preco = Vendedor.precoDeCompraMelhorado(item.getNome());
            int total = preco * quantidade;
            ficha.adicionarOuro(total);
            ficha.removerItem(item.getNome(), quantidade);
            Interface.MostrarMensagem("\nVocê vendeu " + quantidade + "x " + item.getNome() + " por " + total + " ouro.");
            Interface.Pausa(1800);

            vendaveis.clear();
            for (ItemRpg it : ficha.getInventario()) {
                if (it.getQuantidade() > 0 && ehItemMagico(it.getNome())) {
                    vendaveis.add(it);
                }
            }
            if (vendaveis.isEmpty()) {
                Interface.MostrarMensagem("\nDona Maga: \"" + AMARELO + "Isso foi tudo. Tchau!" + RESET + "\"");
                Interface.Pausa(1500);
                return;
            }
        }
    }
}
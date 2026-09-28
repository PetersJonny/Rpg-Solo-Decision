package eventos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fichas.FichaRpg;
import itens.Armadura;
import itens.ItemRpg;
import loja.Vendedor;
import telas.Interface;

// Ferreiro da vila: um dracônico enorme e parrudo. Vende armas e armaduras,
// girando 5 itens (e só itens de arma/armadura) a cada dia. Itens restritos a
// uma classe aparecem para todo mundo, mas só a classe certa consegue comprar.
// Também aceita encomendas sob medida: qualquer arma/armadura vendável por +20%
// do preço, pronta após 1 dia completo (voltar para retirar).
public class Ferreiro {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERMELHO = Interface.VERMELHO;

    // Tudo o que o ferreiro pode vender/encomendar: armas e armaduras do jogo.
    private static final List<String> ACERVO = List.of(
            "Faca", "Machado", "Machadinha", "Martelo", "Mangual", "Arco", "Lança", "Espada",
            "Espada Pesada", "Machado de Guerra", "Martelo de Guerra", "Armadura Pesada",
            "Bisturi", "Arco Refinado", "Nunchako", "Foice",
            "Cajado", "Armadura Leve"
    );

    private static final int ITENS_POR_DIA = 5;

    // Classe que pode usar o item ("" = qualquer classe).
    // Guerreiro: pesada; Mago: cajado; Healer: ágil; o resto é geral.
    private static String classeRestrita(String nome) {
        switch (nome) {
            case "Espada Pesada":
            case "Machado de Guerra":
            case "Martelo de Guerra":
            case "Armadura Pesada":
                return "Guerreiro";
            case "Cajado":
                return "Mago";
            case "Bisturi":
            case "Arco Refinado":
            case "Nunchako":
            case "Foice":
                return "Healer";
            default:
                return "";
        }
    }

    // O estoque do dia é sorteado de forma determinística pelo dia atual: o mesmo
    // dia sempre mostra os MESMOS 5 itens (e itens já de classes variadas sempre
    // aparecem para qualquer um ver).
    private static List<String> estoqueDoDia(FichaRpg ficha) {
        List<String> pool = new ArrayList<>(ACERVO);
        Collections.shuffle(pool, new Random(ficha.getDiaAtual() * 7919L + 31));
        return pool.subList(0, Math.min(ITENS_POR_DIA, pool.size()));
    }

    public static void Ferreiro(FichaRpg ficha) {
        Interface.cabecalhoMenu("FERREIRO DA VILA");
        Interface.MostrarMensagem("\nNa oficina aquecida pelo braseiro, um " + VERMELHO + "dracônico enorme" + RESET + " levanta o martelo em sua direção. É grande, parrudo, com grossos braços de escamas vermelho-escuras — cada golpe no aço faz o ar estremecer.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("\"Precisa de uma boa lâmina ou armadura, forasteiro? Aqui eu forjo os melhores aços da vila.\"");
        Interface.Pausa(1800);

        while (true) {
            Interface.cabecalhoMenu("FERREIRO");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + " moedas.");
            if (ficha.isOrdemDoFerreiroPendente()) {
                String status = ficha.isOrdemDoFerreiroPronta() ? CIANO + "sua encomenda (" + ficha.getFerreiroOrdemItem() + ") está PRONTA para retirar!" + RESET : "há uma encomenda (" + ficha.getFerreiroOrdemItem() + ") ainda na forja.";
                Interface.MostrarMensagem("  " + status);
            }
            Interface.MostrarMensagem("  (O estoque da forja muda a cada dia: hoje são 5 itens por conta do dia de hoje.)");

            System.out.println("\n  O que você deseja fazer?\n");
            System.out.println("  1. Comprar do estoque do dia");
            System.out.println("  2. Encomendar uma arma/armadura sob medida");
            System.out.println("  3. Retirar encomenda");
            System.out.println("  4. Sair");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(1, 4);
            if (escolha == 1) {
                ComprarDoEstoque(ficha, estoqueDoDia(ficha));
            } else if (escolha == 2) {
                Encomendar(ficha);
            } else if (escolha == 3) {
                RetirarEncomenda(ficha);
            } else {
                Interface.MostrarMensagem("\nVocê se despede do ferreiro, que volta ao seu trabalho no braseiro.");
                Interface.Pausa(1500);
                return;
            }
        }
    }

    // Compra direto do estoque do dia. Itens de classe aparecem para todos, mas só
    // a classe correspondente pode comprá-los.
    private static void ComprarDoEstoque(FichaRpg ficha, List<String> estoque) {
        while (true) {
            Interface.cabecalhoMenu("COMPRAR DO FERREIRO");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + "\n");

            for (int i = 0; i < estoque.size(); i++) {
                String nome = estoque.get(i);
                int preco = Vendedor.precoBase(nome);
                String restricao = classeRestrita(nome);
                String sufixo = restricao.isEmpty() ? "" : CIANO + " (só " + restricao + ")" + RESET;
                System.out.println("  " + (i + 1) + ". " + nome + " - " + preco + " ouro" + sufixo);
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Escolha um item para comprar:");
            int escolha = Interface.lerOpcao(0, estoque.size());
            if (escolha == 0) return;

            String nome = estoque.get(escolha - 1);
            String restricao = classeRestrita(nome);
            if (!restricao.isEmpty() && !restricao.equals(ficha.getClasseDoPersonagem().getNome())) {
                Interface.ExibirErro("O ferreiro franze a testa: 'Essa peça só se vende para um " + restricao + ". Não posso te vendê-la.'");
                Interface.Pausa(1500);
                continue;
            }

            int preco = Vendedor.precoBase(nome);
            ItemRpg itemDetalhe = Vendedor.criarItem(nome);
            if (itemDetalhe != null && itemDetalhe.getPeso() > ficha.getEspacoLivreMochila() + 0.0001) {
                Interface.ExibirErro("Sua mochila não tem espaço para " + nome + "! (livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
                Interface.Pausa(1500);
                continue;
            }

            System.out.println("\n  " + CIANO + "-- " + nome.toUpperCase() + " --" + RESET);
            if (itemDetalhe != null) {
                System.out.println("  Descrição: " + itemDetalhe.getDescricao());
            }
            System.out.println("  Preço: " + preco + " ouro");
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

            comprarItemEfetivo(ficha, nome, "Você comprou " + nome + " do ferreiro por " + preco + " ouro.");
        }
    }

    // Encomenda sob medida: +20% do preço original, pronta em 1 dia completo.
    private static void Encomendar(FichaRpg ficha) {
        if (ficha.isOrdemDoFerreiroPendente()) {
            Interface.ExibirErro("O ferreiro já está forjando sua encomenda anterior (" + ficha.getFerreiroOrdemItem() + "). Espere ficar pronta antes de pedir outra.");
            Interface.Pausa(1500);
            return;
        }

        Interface.cabecalhoMenu("ENCOMENDAR ARMA/ARMADURA");
        Interface.MostrarMensagem("\n  Encomendas custam " + AMARELO + "+20% do preço original" + RESET + " e ficam prontas em " + CIANO + "1 dia completo" + RESET + ". Volte amanhã para retirar.\n");

        List<String> lista = new ArrayList<>(ACERVO);
        for (int i = 0; i < lista.size(); i++) {
            String nome = lista.get(i);
            int preco = Vendedor.precoBase(nome) * 120 / 100;
            String restricao = classeRestrita(nome);
            String sufixo = restricao.isEmpty() ? "" : CIANO + " (só " + restricao + ")" + RESET;
            System.out.println("  " + (i + 1) + ". " + nome + " - " + preco + " ouro" + sufixo);
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        System.out.println("\n  Escolha a peça a encomendar:");
        int escolha = Interface.lerOpcao(0, lista.size());
        if (escolha == 0) return;

        String nome = lista.get(escolha - 1);
        String restricao = classeRestrita(nome);
        if (!restricao.isEmpty() && !restricao.equals(ficha.getClasseDoPersonagem().getNome())) {
            Interface.ExibirErro("'Essa peça eu só forjo para um " + restricao + ".', diz o ferreiro.");
            Interface.Pausa(1500);
            return;
        }

        int preco = Vendedor.precoBase(nome) * 120 / 100;
        System.out.println("\n  " + CIANO + "-- " + nome.toUpperCase() + " (sob medida) --" + RESET);
        System.out.println("  Preço (com +20%): " + preco + " ouro");
        System.out.println(CIANO + "  -----------------------" + RESET);
        System.out.println("\n  Confirmar a encomenda?\n");
        System.out.println("  1. Sim");
        System.out.println("  2. Não");
        if (Interface.lerOpcao(2) != 1) {
            Interface.MostrarMensagem("\nEncomenda cancelada.");
            Interface.Pausa(1000);
            return;
        }

        if (!ficha.gastarOuro(preco)) {
            Interface.ExibirErro("Ouro insuficiente! (Precisa de " + preco + ")");
            Interface.Pausa(1500);
            return;
        }

        ficha.setFerreiroOrdemItem(nome);
        ficha.setFerreiroOrdemDia(ficha.getDiaAtual());
        Interface.MostrarMensagem("\nO ferreiro estuda o desenho e concorda com a cabeça: " + AMARELO + "\"Fica pronta em um dia inteiro. Volte amanhã para buscar.\"" + RESET);
        Interface.MostrarMensagem("Você pagou " + preco + " ouro pela encomenda de " + nome + ".");
        Interface.Pausa(2000);
    }

    // Retirar a encomenda quando o dia seguinte (ou posterior) chegar.
    private static void RetirarEncomenda(FichaRpg ficha) {
        if (!ficha.isOrdemDoFerreiroPendente()) {
            Interface.MostrarMensagem("\nVocê não tem nenhuma encomenda no ferreiro.");
            Interface.Pausa(1500);
            return;
        }

        if (!ficha.isOrdemDoFerreiroPronta()) {
            Interface.MostrarMensagem("\nO ferreiro ainda está forjando sua " + ficha.getFerreiroOrdemItem() + ". \"Volte amanhã, num novo dia, que ela estará pronta.\"");
            Interface.Pausa(2000);
            return;
        }

        String nome = ficha.getFerreiroOrdemItem();
        ItemRpg item = Vendedor.criarItem(nome);
        if (item == null) {
            Interface.ExibirErro("Erro: não foi possível criar a peça encomendada.");
            ficha.setFerreiroOrdemItem("");
            ficha.setFerreiroOrdemDia(0);
            Interface.Pausa(1500);
            return;
        }

        if (item.getPeso() > ficha.getEspacoLivreMochila() + 0.0001) {
            Interface.ExibirErro("Sua mochila não tem espaço para retirar a " + nome + "! (livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
            Interface.Pausa(1500);
            return;
        }

        ficha.setFerreiroOrdemItem("");
        ficha.setFerreiroOrdemDia(0);
        comprarItemEfetivo(ficha, nome, "Sua encomenda de " + nome + " está pronta e você a retirou!");
    }

    // Entrega o item comprado/retirado (equipa armadura automaticamente, como na loja).
    private static void comprarItemEfetivo(FichaRpg ficha, String nome, String mensagem) {
        ItemRpg item = Vendedor.criarItem(nome);
        if (item instanceof Armadura) {
            int bonusAtual = ficha.getArmaduraEquipada() != null ? ficha.getArmaduraEquipada().getBonusDefesa() : 0;
            ficha.adicionarItem(item);
            ficha.equiparMelhorArmadura();
            int bonusNovo = ficha.getArmaduraEquipada() != null ? ficha.getArmaduraEquipada().getBonusDefesa() : 0;
            Interface.MostrarMensagem("\n" + mensagem);
            Interface.Pausa(1500);
            if (bonusNovo > bonusAtual) {
                Interface.MostrarMensagem("(Sua melhor armadura foi equipada automaticamente! Defesa: " + ficha.getDefesa() + ")");
            } else {
                Interface.MostrarMensagem("(O item foi guardado na mochila.)");
            }
        } else {
            ficha.adicionarItem(item);
            Interface.MostrarMensagem("\n" + mensagem);
        }
        Interface.Pausa(1500);
    }
}
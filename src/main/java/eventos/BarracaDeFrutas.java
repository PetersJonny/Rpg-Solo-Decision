package eventos;

import java.util.List;

import fichas.FichaRpg;
import fichas.GerenciadorDeMissoesECompanheiro;
import itens.ItemRpg;
import loja.Vendedor;
import telas.Interface;

public class BarracaDeFrutas {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    private static final List<String> FRUTAS = List.of(
            "Maçã",
            "Pera",
            "Ameixa",
            "Uva",
            "Morango Selvagem",
            "Figo Seco"
    );

    public static void BarracaDeFrutas(FichaRpg ficha) {
        Interface.cabecalhoMenu("BARRACA DE FRUTAS");
        Interface.MostrarMensagem("\nPerto da entrada da vila, uma barraca simples de madeira enche o ar com o cheiro doce de frutas frescas. Cestas de maçãs, peras e ameixas se empilham sob o toldo de lona.");
        Interface.Pausa(2200);

        boolean missaoNeta = ficha.isMissaoAceita("A Neta Perdida");
        if (missaoNeta) {
            if (!ficha.isNetaEncontrada()) {
                Interface.MostrarMensagem("A velhinha do xale surrado se inclina no balcão, esperançosa: \"Querido? Alguma notícia da minha netinha?\"");
                Interface.Pausa(1800);
                Interface.MostrarMensagem("Você balança a cabeça: \"Ainda não, senhora... Só vim dar uma olhada.\" Ela assente, triste: \"Tudo bem, querido. Fica à vontade. E se precisar de algo, eu estou aqui.\"");
                Interface.Pausa(2400);
            }
        } else {
            Interface.MostrarMensagem("A velhinha do xale surrado penteia as frutas, sorrindo: \"Boas frutas frescas, forasteiro! Colhidas na mata da entrada.\"");
            Interface.Pausa(2000);
        }

        while (true) {
            Interface.cabecalhoMenu("BARRACA DE FRUTAS");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + " moedas.\n");
            System.out.println("  1. Comprar frutas");
            System.out.println("  2. Olhar ao redor, procurando pistas");
            System.out.println("  3. Voltar para a vila");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(3);
            if (escolha == 1) {
                ComprarFrutas(ficha);
            } else if (escolha == 2) {
                Interface.MostrarMensagem("\nVocê percorre os arredores com o olhar: a barraca, as cestas de frutas, a estrada de terra que leva à mata... Tudo parece em ordem — sem pegadas, sem sinais de luta.");
                Interface.Pausa(2400);
                if (missaoNeta) {
                    Interface.MostrarMensagem("A velhinha acompanha o seu olhar e comenta: \"Ela foi vista pela última vez ali, na mata da entrada. Se a menina entrou por algum lado, foi por ali.\"");
                    Interface.Pausa(2400);
                    GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Neta Perdida", "A netinha foi vista pela última vez na mata da entrada da vila, perto da barraca.");
                }
                Interface.MostrarMensagem("Por enquanto, não há pistas novas por aqui.");
                Interface.Pausa(1600);
            } else {
                Interface.MostrarMensagem("\nVocê se despede da velhinha, que acena com a cabeça, esperançosa.");
                Interface.Pausa(1600);
                return;
            }
        }
    }

        private static void ComprarFrutas(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("COMPRAR FRUTAS");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + "\n");

            for (int i = 0; i < FRUTAS.size(); i++) {
                String fruta = FRUTAS.get(i);
                int preco = Vendedor.precoBase(fruta);
                System.out.println("  " + (i + 1) + ". " + fruta + " - " + preco + " ouro");
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Escolha uma fruta:");
            int escolha = Interface.lerOpcao(0, FRUTAS.size());
            if (escolha == 0) return;

            String fruta = FRUTAS.get(escolha - 1);
            int preco = Vendedor.precoBase(fruta);

            System.out.println("  Quantidade para comprar (1 a " + (ficha.getOuro() / Math.max(1, preco)) + "): ");
            int qtd = Interface.lerInteiro();
            if (qtd < 1) {
                Interface.ExibirErro("Quantidade inválida!");
                continue;
            }

            ItemRpg item = Vendedor.criarItem(fruta);
            int custo = preco * qtd;
            double espaco = item.getPeso() * qtd;
            if (espaco > ficha.getEspacoLivreMochila() + 0.0001) {
                Interface.ExibirErro("Sua mochila não tem espaço para " + qtd + "x " + fruta + "! (livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
                Interface.Pausa(1500);
                continue;
            }
            if (!ficha.gastarOuro(custo)) {
                Interface.ExibirErro("Ouro insuficiente! (Precisa de " + custo + ")");
                Interface.Pausa(1500);
                continue;
            }

            item.setQuantidade(qtd);
            ficha.adicionarItem(item);
            Interface.MostrarMensagem("\nVocê comprou " + qtd + "x " + fruta + " por " + custo + " ouro. (Guardado na mochila.)");
            Interface.Pausa(1800);
        }
    }
}

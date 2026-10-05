package eventos;

import java.util.List;

import fichas.FichaRpg;
import fichas.GerenciadorDeMissoesECompanheiro;
import itens.ItemRpg;
import loja.Vendedor;
import telas.Interface;

public class Alfaiataria {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

            private static final List<String> ROUPAS = List.of(
            "Capa do Viajante",
            "Túnica de Aventureiro",
            "Manto do Atirador",
            "Lenço de Seda",
            "Botas de Correio"
    );

            public static boolean podePerguntarOndeFica(FichaRpg ficha) {
        return ficha.isMissaoAceita("A Filha Perdida") && !ficha.isAlfaiatariaConhecida();
    }

    public static void PerguntarOndeFica(FichaRpg ficha, String interlocutor) {
        Interface.MostrarMensagem("\nVocê pergunta pela alfaiataria, e " + interlocutor + " aponta para o meio da vila.");
        Interface.Pausa(1800);
        Interface.MostrarMensagem("\"Fica no " + CIANO + "centro do comércio" + RESET + ", bem no meio da praça. É a loja com tecidos estendidos na porta, dá pra ver de longe. A dracônica que cuida dela é a " + AMARELO + "Célia Morel" + RESET + ".\"");
        Interface.Pausa(2500);
        Interface.MostrarMensagem("\nAgora você sabe onde fica a alfaiataria.");
        Interface.Pausa(1200);
        ficha.setAlfaiatariaConhecida(true);
    }

    public static void Alfaiataria(FichaRpg ficha) {
        if (FuneralDaFilha.bloquearLojaVazia(ficha, "a alfaiataria")) return;
        Interface.cabecalhoMenu("ALFAIATARIA DA VILA");
        Interface.MostrarMensagem("\nNo meio da praça de comércio, a alfaiataria é a única loja com tecidos estendidos na porta: linho, lã e retalhos coloridos balançam ao vento. A dona, uma " + CIANO + "dracônica de escamas verde-acinzentadas e olhar cansado" + RESET + ", mexe em uma agulha sem erguer os olhos.");
        Interface.Pausa(2200);

        boolean missaoFilha = ficha.isMissaoAceita("A Filha Perdida");

        if (missaoFilha) {
                        Interface.MostrarMensagem("\nAo mencionar o aviso do quadro da taverna, " + AMARELO + "Célia Morel" + RESET + " larga a agulha e te encara. Os olhos dela brilham, úmidos: \"Aviso? Você... aceitou aquele aviso sobre a minha " + AMARELO + "filha" + RESET + "?\"");
            Interface.Pausa(2400);

            if (!ficha.isCaveConhecida()) {
                Interface.MostrarMensagem("\"Você precisa saber o lugar certo\", ela diz, limpando os olhos. \"Ela foi vista pela última vez nas partes finais do fundo da vila, onde uma " + CIANO + "caverna" + RESET + " se abre no barranco. É para lá que todo mundo já olhou, mas ninguém teve coragem de entrar a fundo.\"");
                Interface.Pausa(2800);
                Interface.MostrarMensagem("\nEla aponta para o fim das ruas, além das últimas casas. Agora você sabe onde fica a caverna.");
                Interface.Pausa(1800);
                ficha.setCaveConhecida(true);
                GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "Célia Morel disse que a filha foi vista por último nas partes finais do fundo da vila, onde uma caverna se abre no barranco.");
            }

                        if (!ficha.isFilhaEncontrada()) {
                Interface.MostrarMensagem("Ela pergunta baixinho, atenta ao seu rosto: \"E a minha filha? Achou alguma coisa?\"");
                Interface.Pausa(1800);
                Interface.MostrarMensagem("Você balança a cabeça: \"Ainda não, Célia... Só vim dar uma olhada.\" Ela assente, forçando um sorriso: \"Tudo bem. Se precisar de algo, estou aqui. Cuidado naquela caverna.\"");
                Interface.Pausa(2400);
            }
        } else {
            Interface.MostrarMensagem("Ela sorri fraco ao te ver entrar: \"Procura roupas, forasteiro? Aqui tenho boas peças costuradas à mão. E se o destino te trouxer couro de sobra, eu compro a um bom preço.\"");
            Interface.Pausa(2200);
        }

        while (true) {
            Interface.cabecalhoMenu("ALFAIATARIA");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + " moedas.\n");
            System.out.println("  1. Comprar roupas");
            System.out.println("  2. Vender couro");
            int opPista = -1, opSair = 3;
            if (missaoFilha) {
                opPista = 3;
                System.out.println("  3. Perguntar se ela tem mais alguma pista");
                opSair = 4;
            }
            System.out.println("  " + opSair + ". Sair");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(1, opSair);
            if (escolha == 1) {
                ComprarRoupas(ficha);
            } else if (escolha == 2) {
                VenderCouro(ficha);
            } else if (escolha == opPista) {
                Interface.MostrarMensagem("\n\"Só sei de uma coisa com certeza: ela ia sempre à beira da mata colher frutas perto da entrada da vila. Foi a última vez que a vi.\"");
                Interface.Pausa(2400);
                GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Filha Perdida", "A filha ia sempre à beira da mata da entrada da vila colher frutas. Foi a última vez que a mãe a viu.");
            } else {
                Interface.MostrarMensagem("\nVocê se despede da moça, que volta à costura.");
                Interface.Pausa(1500);
                return;
            }
        }
    }

        private static void ComprarRoupas(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("COMPRAR ROUPAS");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + "\n");

            for (int i = 0; i < ROUPAS.size(); i++) {
                String nome = ROUPAS.get(i);
                int preco = Vendedor.precoBase(nome);
                System.out.println("  " + (i + 1) + ". " + nome + " - " + preco + " ouro");
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Escolha uma peça:");
            int escolha = Interface.lerOpcao(0, ROUPAS.size());
            if (escolha == 0) return;

            String nome = ROUPAS.get(escolha - 1);

                        if (ficha.getQuantidadeDe(nome) >= 1) {
                Interface.ExibirErro("Você já tem um(a) " + nome + ". Uma peça só dá o bônus — não dá para empilhar.");
                Interface.Pausa(1600);
                continue;
            }

            int preco = Vendedor.precoBase(nome);
            ItemRpg item = Vendedor.criarItem(nome);
            if (item == null) {
                Interface.ExibirErro("Erro: esta peça não está disponível.");
                Interface.Pausa(1500);
                continue;
            }
            if (item.getPeso() > ficha.getEspacoLivreMochila() + 0.0001) {
                Interface.ExibirErro("Sua mochila não tem espaço para " + nome + "! (livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
                Interface.Pausa(1500);
                continue;
            }

            System.out.println("\n  " + CIANO + "-- " + nome.toUpperCase() + " --" + RESET);
            System.out.println("  Efeito: " + item.getDescricao());
            System.out.println("  Preço: " + preco + " ouro");
            System.out.println(CIANO + "  -----------------------" + RESET);
            System.out.println("\n  Deseja comprar?\n");
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

            ficha.adicionarItem(item);
            Interface.MostrarMensagem("\nVocê comprou " + nome + " por " + preco + " ouro. (Guardado na mochila.)");
            Interface.Pausa(1800);
        }
    }

        private static void VenderCouro(FichaRpg ficha) {
        int qtdCouro = ficha.getQuantidadeDe("Couro");
        if (qtdCouro <= 0) {
            Interface.MostrarMensagem("\n\"Sem couro para vender? Quando os animais da mata quiserem abrir mão do casaco, traga para mim.\", diz Célia, costurando.");
            Interface.Pausa(2000);
            return;
        }

        int preco = Vendedor.precoDeCompraMelhorado("Couro");
        System.out.println("\n  " + AMARELO + "Couro (x" + qtdCouro + ")" + RESET + " - Célia paga " + preco + " ouro por unidade (mais que o vendedor ambulante).");
        System.out.println("  Quantidade para vender (1 a " + qtdCouro + "): ");
        int qtd = Interface.lerInteiro();
        if (qtd < 1 || qtd > qtdCouro) {
            Interface.ExibirErro("Quantidade inválida!");
            Interface.Pausa(1500);
            return;
        }

        int total = preco * qtd;
        ficha.adicionarOuro(total);
        ficha.removerItem("Couro", qtd);
        Interface.MostrarMensagem("\nVocê vendeu " + qtd + "x Couro para Célia por " + total + " ouro.");
        Interface.Pausa(1800);
    }
}

package telas;

import mecanicas.GerenciadorDeItens;

import classes.ClasseRpg;
import fichas.FichaRpg;
import itens.ItemRpg;
import java.util.Scanner;
import salvamento.GerenciadorSaves;

public class Interface {
        public static final String RESET = "\u001B[0m";
    public static final String AMARELO = "\u001B[33m";
    public static final String CIANO = "\u001B[36m";
    public static final String VERDE = "\u001B[32m";
    public static final String VERMELHO = "\u001B[31m";
    private static final String NEGRITO = "\u001B[1m";

        public static final Scanner scanner = new Scanner(System.in);
    public static boolean modoTeste = false;

        public static void esperarEnter() {
        if (modoTeste) return;
        System.out.println("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    public static void esperarEnter(String mensagem) {
        if (modoTeste) return;
        System.out.println("\n" + mensagem);
        scanner.nextLine();
    }

        public static void pressionarParaRolar() {
        if (modoTeste) return;
        System.out.println("\nPressione ENTER para rolar os dados...");
        scanner.nextLine();
    }

        public static void pressionarParaTeste(String atributo) {
        if (modoTeste) return;
        System.out.println("\nPressione ENTER para rodar um teste de " + atributo + "...");
        scanner.nextLine();
    }

        public static int lerInteiro() {
        if (modoTeste) return 1;
        while (true) {
            String entrada = scanner.nextLine();
            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                ExibirErro("Opção inválida! Digite um número.");
            }
        }
    }

        public static int lerOpcao(int min, int max) {
        if (modoTeste) return 1;
        while (true) {
            int escolha = lerInteiro();
            if (escolha >= min && escolha <= max) return escolha;
            ExibirErro("Opção inválida! Escolha entre " + min + " e " + max + ".");
        }
    }

        public static int lerOpcao(int max) {
        return lerOpcao(1, max);
    }

        public static void Pausa(int milisegundos) {
        try { Thread.sleep(milisegundos); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

        public static void ExibirBoasVindas() {
        System.out.println(CIANO + "==========================================================================================" + RESET);
        System.out.println(AMARELO + NEGRITO +
            " ____   ___   _       ___    ____  ____   ____   ____  _____ ____ ___ ____ ___ ___   _   _ \n" +
            "/ ___| / _ \\ | |     / _ \\  |  _ \\|  _ \\ / ___| |  _ \\| ____/ ___|_ _/ ___|_ _/ _ \\ | \\ | |\n" +
            "\\___ \\| | | || |    | | | | | |_) | |_) | |  _  | | | |  _|| |    | |\\___ \\ | | | | |  \\| |\n" +
            " ___) | |_| || |___ | |_| | |  _ <|  __/| |_| | | |_| | |__| |___ | | ___) || | |_| | |\\  |\n" +
            "|____/ \\___/ |_____| \\___/  |_| \\_\\_|    \\____| |____/|_____\\____|___|____/___\\___/ |_| \\_|"
            + RESET);
        System.out.println(CIANO + "==========================================================================================" + RESET);
        System.out.println(NEGRITO + "                       SEJA BEM-VINDO AO MUNDO DE SOLORPGDECISION!" + RESET);
        System.out.println(CIANO + "==========================================================================================" + RESET);
        System.out.println();
    }

    public static void barraDivisoria() {
        System.out.println(CIANO + "==========================================================================================" + RESET);
    }

        public static void cabecalhoMenu(String titulo) {
        barraDivisoria();
        String texto = titulo.toUpperCase();
        int espacos = Math.max(0, (92 - texto.length()) / 2);
        System.out.println(CIANO + " ".repeat(espacos) + texto + RESET);
        barraDivisoria();
    }

    public static void BarraCarregamento(String mensagem) {
        int totalBlocos = 100;
        System.out.print(NEGRITO + mensagem + RESET + "\n");
        for (int i = 0; i <= totalBlocos; i++) {
            int percentual = (i * 100) / totalBlocos;
            String preenchido = "█".repeat(i);
            String vazio = " ".repeat(totalBlocos - i);
            System.out.print("\r" + CIANO + "[" + preenchido + vazio + "] " + percentual + "%" + RESET);
            try { Thread.sleep(50); } catch (InterruptedException e) {}
        }
        System.out.println("\n" + AMARELO + "Sincronização concluída!" + RESET + "\n");
    }

    public static String PedirNomeJogador() {
        System.out.print("Qual seu nome?\n");
        return lerNome();
    }

    public static String PedirNomePersonagem() {
        cabecalhoMenu("NOME DO PERSONAGEM");
        System.out.println("\n  Qual o nome do seu personagem?");
        return lerNome();
    }

        private static String lerNome() {
        while (true) {
            String nome = scanner.nextLine().trim();
            if (nome.isEmpty()) {
                ExibirErro("O nome não pode ser vazio. Digite novamente:");
                continue;
            }
            if (nome.length() > 20) {
                ExibirErro("O nome pode ter no máximo 20 caracteres. Digite novamente:");
                continue;
            }
            return nome;
        }
    }

        public static int MenuInicial() {
        System.out.println("\n");
        cabecalhoMenu("MENU PRINCIPAL");
        System.out.println("\n  O que deseja fazer?\n");
        System.out.println("  1. " + CIANO + "Novo Jogo" + RESET + " — começar uma nova aventura do zero");
        System.out.println("  2. " + CIANO + "Carregar Jogo" + RESET + " — continuar um de seus saves");
        System.out.println("  3. " + AMARELO + "Apagar Save" + RESET + " — deletar um save salvo");
        System.out.println("  4. Fechar o jogo\n");
        System.out.println("  " + VERDE + "Digite a opção:" + RESET);
        return lerOpcao(4);
    }

                public static int[] opcoesMenuFloresta(FichaRpg ficha) {
        int num = 5;
        int opViajar = num++;
        int opLabirinto = -1, opConversar = -1, opNeta = -1;
        if (ficha.isLabirintoDisponivel()) opLabirinto = num++;
        if (ficha.temCompanheiro()) opConversar = num++;
        if (checarMissaoNeta(ficha)) opNeta = num++;
        int opSalvar = num++;
        int opEncerrar = num++;
        return new int[]{opViajar, opLabirinto, opConversar, opNeta, opSalvar, opEncerrar};
    }

    public static boolean checarMissaoNeta(FichaRpg ficha) {
        return ficha.isMissaoAceita("A Neta Perdida") && !ficha.isNetaEncontrada();
    }

    public static int MenuPrincipalAventura(FichaRpg ficha) {
        System.out.println("\n");
        cabecalhoMenu("FLORESTA DE FREIJORD");

        String periodo = ficha.getPeriodoDescritivoMaiusculo();
        String statusFome = ficha.descreverFome();
        System.out.println("\n  Período: " + AMARELO + periodo + RESET + "  (" + (3 - ficha.getProgressoPeriodo()) + "/3 para virar)" + (ficha.isCansado() ? "  |  " + AMARELO + "CANSADO (-1 em testes até dormir)" + RESET : ""));
        if (!statusFome.isEmpty()) {
            System.out.println("  " + VERMELHO + statusFome + RESET);
        }
        if (ficha.temCompanheiro()) {
            System.out.println("  Companheiro(a): " + CIANO + ficha.getCompanheiro().getNome() + RESET + " (" + ficha.getCompanheiro().getClasseNome() + ", Nível " + ficha.getCompanheiro().getFicha().getNivel() + ")");
        }
        if (ficha.isLabirintoDisponivel()) {
            System.out.println("  " + CIANO + "Labirinto descoberto" + RESET);
        }
        System.out.println("\n  O que você deseja fazer?\n");

        int[] ops = opcoesMenuFloresta(ficha);
        System.out.println("  1. Ver ficha");
        System.out.println("  2. Explorar a Floresta");
        System.out.println("  3. Buscar Recursos na Floresta");
        System.out.println("  4. Construção (Dormir)");
        if (ficha.getProfundidadeFloresta() > 0) {
            System.out.println("  " + ops[0] + ". Continuar tentando sair da floresta");
        } else {
            System.out.println("  " + ops[0] + ". Tentar sair da floresta");
        }
        if (ops[1] > 0) {
            System.out.println("  " + ops[1] + ". Labirinto");
        }
        if (ops[3] > 0) {
            System.out.println("  " + ops[3] + ". " + AMARELO + "Procurar a netinha da velhinha" + RESET + " na mata da entrada");
        }
        if (ops[2] > 0) {
            System.out.println("  " + ops[2] + ". Conversar com " + ficha.getCompanheiro().getNome());
        }
        System.out.println("  " + ops[4] + ". Salvar Jogo");
        System.out.println("  " + ops[5] + ". Encerrar jogo");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        return lerOpcao(1, ops[5]);
    }

                public static int[] opcoesMenuVilarejo(FichaRpg ficha) {
        int num = 5;
        int opMissoes = num++;
        int opAlfaiataria = -1, opBarraca = -1, opCaverna = -1;
        if (ficha.isAlfaiatariaConhecida()) opAlfaiataria = num++;
        if (ficha.isMissaoAceita("A Neta Perdida")) opBarraca = num++;
        if (ficha.isCaveConhecida()) opCaverna = num++;
        int opVoltar = num++;
        int opSalvar = num++;
        int opEncerrar = num++;
        return new int[]{opMissoes, opAlfaiataria, opBarraca, opCaverna, opVoltar, opSalvar, opEncerrar};
    }

            public static int MenuVilarejo(FichaRpg ficha) {
        System.out.println("\n");
        String cidade = ficha.getCidadeAtual() != null ? ficha.getCidadeAtual().toUpperCase() : "VILAREJO DE SCARBOR";
        cabecalhoMenu(cidade);

        String periodo = ficha.getPeriodoDescritivoMaiusculo();
        String statusFome = ficha.descreverFome();
        System.out.println("\n  Período: " + AMARELO + periodo + RESET + "  (" + (3 - ficha.getProgressoPeriodo()) + "/3 para virar)" + (ficha.isCansado() ? "  |  " + AMARELO + "CANSADO (-1 em testes até dormir)" + RESET : ""));
        if (!statusFome.isEmpty()) {
            System.out.println("  " + VERMELHO + statusFome + RESET);
        }
        if (ficha.temCompanheiro()) {
            System.out.println("  Companheiro(a): " + CIANO + ficha.getCompanheiro().getNome() + RESET + " (" + ficha.getCompanheiro().getClasseNome() + ", Nível " + ficha.getCompanheiro().getFicha().getNivel() + ")");
        }
        System.out.println("\n  O que você deseja fazer?\n");

        int[] ops = opcoesMenuVilarejo(ficha);
        int ativas = ficha.getMissoesAceitas().size();
        System.out.println("  1. Ver ficha");
        System.out.println("  2. Olhar em volta");
        System.out.println("  3. Ir à " + CIANO + "taverna" + RESET + " (comida + quadro de missões)");
        System.out.println("  4. Ir ao " + CIANO + "ferreiro" + RESET + " (armas e armaduras)");
        System.out.println("  " + ops[0] + ". Ver missões em andamento (" + ativas + ")");
        if (ops[1] > 0) {
            System.out.println("  " + ops[1] + ". Ir à " + CIANO + "alfaiataria" + RESET + " (centro do comércio)");
        }
        if (ops[2] > 0) {
            System.out.println("  " + ops[2] + ". Ir à " + CIANO + "barraca de frutas" + RESET + " (entrada da vila)");
        }
        if (ops[3] > 0) {
            System.out.println("  " + ops[3] + ". Ir para a " + CIANO + "caverna" + RESET + " (fundo da vila)");
        }
        System.out.println("  " + ops[4] + ". Voltar para a floresta");
        System.out.println("  " + ops[5] + ". Salvar Jogo");
        System.out.println("  " + ops[6] + ". Encerrar jogo");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        return lerOpcao(1, ops[6]);
    }

        public static void droparItemDoInventario(FichaRpg ficha, ItemRpg item) {
        int qtdAtual = item.getQuantidade();
        System.out.println("\n  Você tem " + qtdAtual + "x " + item.getNome() + ".");
        System.out.println("  Quantidade para dropar (1 a " + qtdAtual + ", 0 para cancelar): ");
        int qtd = lerInteiro();
        if (qtd <= 0) {
            System.out.println(AMARELO + "  Descarte cancelado." + RESET);
            return;
        }
        if (qtd > qtdAtual) {
            ExibirErro("Quantidade inválida! Você só tem " + qtdAtual + "x.");
            return;
        }

        System.out.println("  Tem certeza que deseja dropar " + qtd + "x " + item.getNome() + "?");
        System.out.println("  1. Sim, dropar");
        System.out.println("  2. Não, cancelar");
        int confirmar = lerOpcao(2);

        if (confirmar != 1) {
            System.out.println(AMARELO + "  Descarte cancelado." + RESET);
            return;
        }

        double pesoDropado = item.getPeso() * qtd;
        ficha.removerItem(item.getNome(), qtd);
        System.out.println(VERDE + "  Você dropou " + qtd + "x " + item.getNome() + "." + RESET);
        if (pesoDropado > 0) {
            System.out.println("  Espaço liberado: " + String.format("%.1f", pesoDropado) + " (mochila: " + String.format("%.1f", ficha.getPesoTotalMochila()) + "/" + String.format("%.1f", ficha.getCapacidadeMochila()) + ")");
        }
    }

    public static void ExibirErro(String erro) {
        System.out.println(AMARELO + "[AVISO] " + erro + RESET);
    }

    public static void MostrarMensagem(String msg) {
        System.out.println(msg);
    }
}

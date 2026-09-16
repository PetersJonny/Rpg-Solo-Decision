package telas;

import classes.ClasseRpg;
import fichas.FichaRpg;
import itens.ItemRpg;
import java.util.Scanner;

public class Interface {
    // Códigos de Cores ANSI
    private static final String RESET = "\u001B[0m";
    private static final String AMARELO = "\u001B[33m";
    private static final String CIANO = "\u001B[36m";
    private static final String VERDE = "\u001B[32m";
    private static final String NEGRITO = "\u001B[1m";

    // Entrada de Dados
    public static final Scanner scanner = new Scanner(System.in);

    // Aguarda o jogador apertar ENTER
    public static void esperarEnter() {
        System.out.println("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    public static void esperarEnter(String mensagem) {
        System.out.println("\n" + mensagem);
        scanner.nextLine();
    }

    // Aguarda o ENTER do jogador antes de rolar dados (não retorna o valor rolado)
    public static void pressionarParaRolar() {
        System.out.println("\nPressione ENTER para rolar os dados...");
        scanner.nextLine();
    }

    // Aguarda o ENTER antes de um teste, citando o atributo
    public static void pressionarParaTeste(String atributo) {
        System.out.println("\nPressione ENTER para rodar um teste de " + atributo + "...");
        scanner.nextLine();
    }

    // Lê um número inteiro com validação (letras/caracteres mostram opção inválida e pedem novamente)
    public static int lerInteiro() {
        while (true) {
            String entrada = scanner.nextLine();
            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                ExibirErro("Opção inválida! Digite um número.");
            }
        }
    }

    // Pausa para leitura
    public static void Pausa(int milisegundos) {
        try { Thread.sleep(milisegundos); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // Menus e Telas
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

    // Título centralizado nas barras ciano (padrão dos menus)
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

    // Lê um nome com limite de 20 caracteres
    private static String lerNome() {
        while (true) {
            String nome = scanner.nextLine().trim();
            if (nome.length() > 20) {
                ExibirErro("O nome pode ter no máximo 20 caracteres. Digite novamente:");
                continue;
            }
            return nome;
        }
    }

    public static int MenuCriacaoFicha() {
        System.out.println("\n");
        cabecalhoMenu("CRIAÇÃO DE PERSONAGEM");
        System.out.println("\n  O que deseja fazer?\n");
        System.out.println("  1. Escolher nome do personagem / alterar");
        System.out.println("  2. Distribuir pontos entre atributos / mudar pontos");
        System.out.println("  3. Escolher classe / mudar classe");
        System.out.println("  4. Mostrar ficha");
        System.out.println("  5. Finalizar criação do personagem");
        System.out.println("  6. Fechar o jogo\n");
        System.out.println("  " + VERDE + "Digite a opção:" + RESET);
        int escolha = lerInteiro();
        return escolha;
    }

    public static int MenuDistribuirAtributos(int pontosSobrando) {
        cabecalhoMenu("DISTRIBUIR ATRIBUTOS");
        System.out.println("\n  Você tem " + AMARELO + pontosSobrando + RESET + " pontos para distribuir.\n");
        System.out.println("  Escolha qual atributo quer melhorar:\n");
        System.out.println("  1. Constituição");
        System.out.println("  2. Destreza");
        System.out.println("  3. Força");
        System.out.println("  4. Sabedoria");
        System.out.println("  5. Intelecto");
        System.out.println("  6. Presença\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        int escolha = lerInteiro();
        return escolha;
    }

    public static int PedirQuantidadePontos(int pontosSobrando) {
        cabecalhoMenu("QUANTIDADE DE PONTOS");
        System.out.println("\n  Quantos pontos deseja gastar? Tem " + AMARELO + pontosSobrando + RESET + " pontos ainda.");
        int gasto = lerInteiro();
        return gasto;
    }

    public static int MenuEscolherClasse() {
        cabecalhoMenu("ESCOLHA SUA CLASSE");
        System.out.println("\n  Escolha entre uma das 3 classes abaixo:\n");
        System.out.println("  1. " + CIANO + "Mago" + RESET + " — só pode usar cajado, conjura magias poderosas, porém é mais frágil.");
        System.out.println("  2. " + CIANO + "Guerreiro" + RESET + " — só pode usar espada e atacar corpo a corpo, porém é mais resistente.");
        System.out.println("  3. " + CIANO + "Healer" + RESET + " — tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana.\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        int escolha = lerInteiro();
        return escolha;
    }

    public static String EscolherElementoMago() {
        cabecalhoMenu("ELEMENTO DO MAGO");
        System.out.println("\n  Como Mago, você deve escolher o elemento da sua Bola Elementar:\n");
        System.out.println("  1. Fogo");
        System.out.println("  2. Água");
        System.out.println("  3. Gelo");
        System.out.println("  4. Elétrico");
        System.out.println("  5. Terra");
        System.out.println("  6. Ácido\n");
        System.out.println("  " + VERDE + "0. Voltar" + RESET);
        int escolha = lerInteiro();
        switch (escolha) {
            case 1: return "Fogo";
            case 2: return "Água";
            case 3: return "Gelo";
            case 4: return "Elétrico";
            case 5: return "Terra";
            case 6: return "Ácido";
            default: return null;
        }
    }

    public static int MenuPrincipalAventura(FichaRpg ficha) {
        System.out.println("\n");
        cabecalhoMenu("FLORESTA DE FREIJORD");

        String periodo = ficha.isEhNoite() ? "NOITE" : "DIA";
        System.out.println("\n  Período: " + AMARELO + periodo + RESET + "  (" + (3 - ficha.getProgressoPeriodo()) + "/3 para virar)" + (ficha.isCansado() ? "  |  " + AMARELO + "CANSADO (-1 em testes até dormir)" + RESET : ""));
        if (ficha.temCompanheiro()) {
            System.out.println("  Companheiro(a): " + CIANO + ficha.getCompanheiro().getNome() + RESET + " (" + ficha.getCompanheiro().getClasseNome() + ", Nível " + ficha.getCompanheiro().getFicha().getNivel() + ")");
        }
        System.out.println("\n  O que você deseja fazer?\n");
        System.out.println("  1. Ver ficha");
        System.out.println("  2. Explorar a Floresta");
        System.out.println("  3. Buscar Recursos na Floresta");
        System.out.println("  4. Construção (Dormir)");
        if (ficha.temCompanheiro()) {
            System.out.println("  5. Conversar com " + ficha.getCompanheiro().getNome());
            System.out.println("  6. Encerrar jogo");
        } else {
            System.out.println("  5. Encerrar jogo");
        }
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int escolha = lerInteiro();
        return escolha;
    }

    // Interação com a Ficha
    public static int MenuFicha() {
        System.out.println("\n");
        cabecalhoMenu("SUA FICHA");
        System.out.println("\n  O que deseja fazer?\n");
        System.out.println("  1. Ver Habilidades");
        System.out.println("  2. Ver Inventário (Ler descrições)");
        System.out.println("  3. Voltar para a Aventura");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        int escolha = lerInteiro();
        return escolha;
    }

    public static void InspecionarInventario(FichaRpg ficha) {
        while (true) {
            if (ficha.getInventario().isEmpty()) {
                System.out.println(AMARELO + "  Seu inventário está vazio." + RESET);
                return;
            }

            cabecalhoMenu("SEU INVENTÁRIO");
            System.out.println("\n  Escolha um item para ver a descrição:\n");
            for (int i = 0; i < ficha.getInventario().size(); i++) {
                ItemRpg item = ficha.getInventario().get(i);
                String tipo = eventos.Floresta.ehItemConsumivel(item) ? "  [Consumível]" : "";
                System.out.println("  " + (i + 1) + ". " + CIANO + item.getNome() + RESET + " (x" + item.getQuantidade() + ")" + tipo);
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Digite o número do item que deseja ver a descrição:");
            int escolha = lerInteiro();

            if (escolha == 0) return;
            if (escolha < 1 || escolha > ficha.getInventario().size()) {
                ExibirErro("Opção inválida!");
                continue;
            }

            ItemRpg itemEscolhido = ficha.getInventario().get(escolha - 1);
            System.out.println("\n" + CIANO + "  -- " + itemEscolhido.getNome().toUpperCase() + " --" + RESET);
            System.out.println("  Descrição: " + itemEscolhido.getDescricao());
            System.out.println(CIANO + "  -----------------------" + RESET);

            if (eventos.Floresta.ehItemConsumivel(itemEscolhido)) {
                System.out.println("\n  Deseja usar este item?");
                System.out.println("  1. Sim");
                System.out.println("  2. Não usar");
                int usar = lerInteiro();

                if (usar == 1) {
                    String nomeItem = itemEscolhido.getNome();
                    boolean cheio = false;
                    if ((nomeItem.equals("Frutas") || nomeItem.equals("Kit Médico")) && ficha.getVidaPersonagem() >= ficha.getVidaMaxima()) {
                        ExibirErro("Sua vida já está no máximo!");
                        cheio = true;
                    } else if ((nomeItem.equals("Poção de Mana") || nomeItem.equals("Poção Grande de Mana")) && ficha.getManaPersonagem() >= ficha.getManaMaxima()) {
                        ExibirErro("Sua mana já está no máximo!");
                        cheio = true;
                    }
                    if (!cheio) {
                        int quantidade = 1;
                        if (itemEscolhido.getQuantidade() > 1) {
                            System.out.println("  Quantidade para usar (1 a " + itemEscolhido.getQuantidade() + "): ");
                            int qtd = lerInteiro();
                            if (qtd > 0 && qtd <= itemEscolhido.getQuantidade()) {
                                quantidade = qtd;
                            }
                        }
                        eventos.Floresta.usarItemForaDeCombate(ficha, itemEscolhido, quantidade);
                    }
                }
            }
        }
    }

    public static void InspecionarHabilidades(FichaRpg ficha) {
        if (ficha.getHabilidades().isEmpty()) {
            System.out.println(AMARELO + "  Você não possui nenhuma habilidade." + RESET);
            return;
        }

        cabecalhoMenu("SUAS HABILIDADES");
        System.out.println("\n  Escolha uma habilidade para ler a descrição:\n");
        for (int i = 0; i < ficha.getHabilidades().size(); i++) {
            habilidades.Habilidade hab = ficha.getHabilidades().get(i);
            System.out.println("  " + (i + 1) + ". " + CIANO + hab.getNome() + RESET + " (Custo: " + hab.getCustoMana() + " Mana)");
        }
        System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

        System.out.println("\n  Digite o número da habilidade que deseja ler a descrição:");
        int escolha = lerInteiro();

        if (escolha > 0 && escolha <= ficha.getHabilidades().size()) {
            habilidades.Habilidade habEscolhida = ficha.getHabilidades().get(escolha - 1);
            System.out.println("\n" + CIANO + "  -- " + habEscolhida.getNome().toUpperCase() + " --" + RESET);
            System.out.println("  Custo de Mana: " + habEscolhida.getCustoMana());
            System.out.println("  Descrição: " + habEscolhida.getDescricao());
            System.out.println(CIANO + "  -----------------------" + RESET);
        } else if (escolha != 0) {
            ExibirErro("Opção inválida!");
        }
    }

    public static void ExibirErro(String erro) {
        System.out.println(AMARELO + "[AVISO] " + erro + RESET);
    }

    public static void MostrarMensagem(String msg) {
        System.out.println(msg);
    }

    public static void MostrarFicha(FichaRpg ficha) {
        cabecalhoMenu("FICHA DO PERSONAGEM");
        ClasseRpg classe = ficha.getClasseDoPersonagem();
        String nomeDaClasse = (classe != null) ? classe.getNome() : "Nenhuma";

        // Lista todas as armas do inventário na seção de Combate
        StringBuilder combate = new StringBuilder();
        for (ItemRpg item : ficha.getInventario()) {
            if (item instanceof itens.Arma) {
                itens.Arma arma = (itens.Arma) item;
                combate.append("\n    - ").append(arma.getNome())
                    .append("  (Dano: ").append(arma.getQuantidadeDanoArma())
                    .append("d").append(arma.getDadoDanoArma())
                    .append(" | Tipo: ").append(arma.getTipoArma())
                    .append(" | Atributo: ").append(arma.getAtributoAtaque()).append(")");
            }
        }

        // Ataque desarmado
        String socoNome = "Soco";
        String socoTipo = "-";
        int socoQtdDano = 0;
        int socoDado = 0;
        if (classe != null && classe.getAtaqueDesarmado() != null) {
            socoNome = classe.getAtaqueDesarmado().getNome();
            socoTipo = classe.getAtaqueDesarmado().getTipoArma();
            socoQtdDano = classe.getAtaqueDesarmado().getQuantidadeDanoArma();
            socoDado = classe.getAtaqueDesarmado().getDadoDanoArma();
        }
        combate.append("\n    - ").append(socoNome)
            .append("  (Dano: ").append(socoQtdDano).append("d").append(socoDado)
            .append(" | Tipo: ").append(socoTipo).append(")");

        System.out.println("\n  Nome: " + CIANO + ficha.getNomePersonagem() + RESET + "        Nível: " + ficha.getNivel() + (ficha.getNivel() < 10 ? "  (XP: " + ficha.getXp() + "/" + fichas.FichaRpg.getXpNecessaria(ficha.getNivel()) + ")" : "  (XP: " + ficha.getXp() + " - Nível máximo)"));
        System.out.println("  Dono da ficha: " + ficha.getNomePessoa() + "      Classe: " + nomeDaClasse);
        System.out.println("  Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + "        Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
        System.out.println("  Ouro: " + ficha.getOuro());

        System.out.println("\n  " + CIANO + "[ ATRIBUTOS ]" + RESET);
        System.out.println("    Constituição: " + ficha.getConstituicao() + "    Destreza: " + ficha.getDestreza());
        System.out.println("    Força: " + ficha.getForca() + "    Sabedoria: " + ficha.getSabedoria());
        System.out.println("    Intelecto: " + ficha.getIntelecto() + "    Presença: " + ficha.getPresenca());

        System.out.println("\n  " + CIANO + "[ COMBATE ]" + RESET + combate);
        System.out.println("    Defesa: " + ficha.getDefesa());

        System.out.println("\n  " + CIANO + "[ ABRIGO E TEMPO ]" + RESET);
        System.out.println("    Período: " + (ficha.isEhNoite() ? "Noite" : "Dia") + " (" + (3 - ficha.getProgressoPeriodo()) + "/3 para virar)");
        String cabanaStatus = !ficha.isTemCabana()
                ? "Não construída"
                : (ficha.isNaCabana() ? "Construída (você está nela)" : "Construída (você está longe dela)");
        System.out.println("    Cabana: " + cabanaStatus + " | Dias sem dormir: " + ficha.getDiasSemDormir() + (ficha.isCansado() ? " (CANSADO: -1 em testes)" : ""));
        String salaStatus = !ficha.isTemSalaTreino()
                ? "Não construída"
                : (ficha.isSalaJuntoCabana() ? "Construída (junto à cabana)" : "Construída (longe da cabana)");
        System.out.println("    Sala de Treino: " + salaStatus + (ficha.isNaSalaTreino() ? " (você está nela)" : "") + (ficha.getTreinoBonusPeriodosRestantes() > 0 ? " | Bônus de treino: +3 em " + ficha.getTreinoBonusAtributo() + " (restam " + ficha.getTreinoBonusPeriodosRestantes() + " períodos)" : ""));

        System.out.println("\n  " + CIANO + "[ INVENTÁRIO ]" + RESET);
        if (ficha.getInventario().isEmpty()) {
            System.out.println("    - Vazio");
        } else {
            for (ItemRpg item : ficha.getInventario()) {
                System.out.println("    - " + item.getNome() + " (x" + item.getQuantidade() + ")");
            }
        }

        System.out.println("\n  " + CIANO + "[ HABILIDADES ]" + RESET);
        if (ficha.getHabilidades().isEmpty()) {
            System.out.println("    - Nenhuma");
        } else {
            for (habilidades.Habilidade hab : ficha.getHabilidades()) {
                System.out.println("    - " + hab.getNome() + " (Custo: " + hab.getCustoMana() + " Mana)");
            }
        }

        System.out.println("\n" + CIANO + "==========================================================================================" + RESET);
    }
}

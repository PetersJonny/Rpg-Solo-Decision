package telas;

import java.util.Scanner;
import fichas.FichaRpg;
import classes.ClasseRpg;
import itens.ItemRpg;

public class Interface {
    // Códigos de Cores ANSI
    private static final String RESET = "\u001B[0m";
    private static final String AMARELO = "\u001B[33m";
    private static final String CIANO = "\u001B[36m";
    private static final String NEGRITO = "\u001B[1m";

    // Entrada de Dados
    public static final Scanner scanner = new Scanner(System.in);

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
        return scanner.nextLine();
    }

    public static String PedirNomePersonagem() {
        barraDivisoria();
        System.out.println("\nQual o nome do seu personagem?");
        return scanner.nextLine();
    }

    public static int MenuCriacaoFicha() {
        System.out.println("\n");
        barraDivisoria();
        System.out.println("O que deseja fazer entre as seguintes opções?");
        System.out.println("\n1.Escolher nome do personagem/alterar");
        System.out.println("\n2.Distribuir pontos entre atributos/mudar pontos");
        System.out.println("\n3.Escolher classe/mudar classe");
        System.out.println("\n4.Mostrar ficha");
        System.out.println("\n5.Finalizar criação do personagem.");
        int escolha = scanner.nextInt();
        scanner.nextLine();
        return escolha;
    }

    public static int MenuDistribuirAtributos(int pontosSobrando) {
        barraDivisoria();
        System.out.println("\nVocê tem " + pontosSobrando + " pontos para distribuir. Escolha qual atributo quer melhorar: \n\n1.Constituição\n2.Destreza\n3.Força\n4.Sabedoria\n5.Intelecto\n6.Presença"); 
        int escolha = scanner.nextInt();
        scanner.nextLine();
        return escolha;
    }

    public static int PedirQuantidadePontos(int pontosSobrando) {
        System.out.println("\nQuantos pontos deseja gastar? Tem " + pontosSobrando + " pontos ainda.");
        int gasto = scanner.nextInt();
        scanner.nextLine();
        return gasto;
    }

    public static int MenuEscolherClasse() {
        barraDivisoria();
        System.out.println("\n Escolha entre uma das 3 classes abaixo: \n 1.Mago (só pode usar cajado, conjura magias poderosas, porém é mais fragil). \n 2.Guerreiro (só pode usar espada e atacar corpo a corpo, porém é mais resistente). \n 3.Healer (tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana).");
        int escolha = scanner.nextInt();
        scanner.nextLine();
        return escolha;
    }

    public static int MenuPrincipalAventura() {
        System.out.println("\n");
        barraDivisoria();
        System.out.println("O que você deseja fazer?");
        System.out.println("\n1. Ver ficha");
        System.out.println("2. Começar a aventura");
        System.out.println("3. Encerrar jogo");
        int escolha = scanner.nextInt();
        scanner.nextLine();
        return escolha;
    }

    public static void ExibirErro(String erro) {
        System.out.println(AMARELO + "[AVISO] " + erro + RESET);
    }

    public static void MostrarMensagem(String msg) {
        System.out.println(msg);
    }

    public static void MostrarFicha(FichaRpg ficha) {
        barraDivisoria();
        ClasseRpg classe = ficha.getClasseDoPersonagem();
        String nomeDaClasse = (classe != null) ? classe.getNome() : "Nenhuma";
        
        // Puxando dados da Arma Equipada (agora um Objeto)
        String armaDaClasse = "Nenhuma";
        String tipoArma = "-";
        int qtdDano = 0;
        int dadoDano = 0;

        if (ficha.getArmaEquipada() != null) {
            armaDaClasse = ficha.getArmaEquipada().getNome();
            tipoArma = ficha.getArmaEquipada().getTipoArma();
            qtdDano = ficha.getArmaEquipada().getQuantidadeDanoArma();
            dadoDano = ficha.getArmaEquipada().getDadoDanoArma();
        }

        // Puxando dados do Soco
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

        System.out.println("\n --------FICHA-------- \n\nNome: " + ficha.getNomePersonagem() + "\t\tDono da ficha: " + ficha.getNomePessoa() + "\t\tClasse: " + nomeDaClasse + "\nVida: " + ficha.getVidaPersonagem() + "\t\tMana: " + ficha.getManaPersonagem() + "\n\nAtributos: \nConstituição: " + ficha.getConstituicao() + "\nDestreza: " + ficha.getDestreza() + "\nForça: " + ficha.getForca() + "\nSabedoria: " + ficha.getSabedoria() + "\nIntelecto: " + ficha.getIntelecto() + "\nPresença: " + ficha.getPresenca() + "\n\nCombate: \n- " + armaDaClasse + "\t(Dano: " + qtdDano + "d" + dadoDano + ", Tipo: " + tipoArma + ")\n- " + socoNome + "\t(Dano: " + socoQtdDano + "d" + socoDado + ", Tipo: " + socoTipo + ")\n\nDefesa: " + ficha.getDefesa());
        
        System.out.println("\nInventário:");
        if (ficha.getInventario().isEmpty()) {
            System.out.println("- Vazio");
        } else {
            for (ItemRpg item : ficha.getInventario()) {
                System.out.println("- " + item.getNome() + " (x" + item.getQuantidade() + ")");
            }
        }
        
        System.out.println("----------------------");
    }
}

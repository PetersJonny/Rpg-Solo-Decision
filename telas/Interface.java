package telas;

public class Interface {

    // códigos de Cores ANSI
    private static final String RESET = "\u001B[0m";
    private static final String AMARELO = "\u001B[33m";
    private static final String CIANO = "\u001B[36m";
    private static final String NEGRITO = "\u001B[1m";

    // tela de boas vindas

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

    // barra divisoria
    public static void barraDivisoria() {
        System.out.println(CIANO + "==========================================================================================" + RESET);
    }

    // criação barra grafica de carregamento
    public static void BarraCarregamento(String mensagem) {
        int totalBlocos = 100;

        System.out.print(NEGRITO + mensagem + RESET + "\n");

        for (int i = 0; i <= totalBlocos; i++) {
            int percentual = (i * 100) / totalBlocos;
            String preenchido = "█".repeat(i);
            String vazio = " ".repeat(totalBlocos - i);

            System.out.print("\r" + CIANO + "[" + preenchido + vazio + "] " + percentual + "%" + RESET);

            try {
                Thread.sleep(50); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n" + AMARELO + "Sincronização concluída!" + RESET + "\n");
    }
}

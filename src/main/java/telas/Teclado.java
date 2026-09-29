package telas;

import java.io.IOException;

public class Teclado {

    private Teclado() {}

    private static boolean modoCruAtivo = false;
    private static String estadoSalvo;
    private static Thread hookRestauracao;

    public static synchronized void modoTeclaUnica() {
        if (modoCruAtivo) return;
        estadoSalvo = capturarEstado();
        executar("stty -icanon min 1 -echo < /dev/tty");
        modoCruAtivo = true;
        hookRestauracao = new Thread(Teclado::restaurar, "teclado-restaurar");
        Runtime.getRuntime().addShutdownHook(hookRestauracao);
    }

    public static synchronized void restaurar() {
        if (!modoCruAtivo) return;
        modoCruAtivo = false;
        if (hookRestauracao != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(hookRestauracao);
            } catch (IllegalStateException e) {

            }
            hookRestauracao = null;
        }

        if (estadoSalvo != null && !estadoSalvo.isBlank()) {
            executar("stty " + estadoSalvo + " < /dev/tty");
        }

        executar("stty icanon echo < /dev/tty");
    }

    public static void limparBuffer() {
        try {
            while (System.in.available() > 0) System.in.read();
        } catch (IOException e) {

        }
    }

    public static void assegurarTerminalSaudavel() {
        executar("stty icanon echo < /dev/tty");
    }

    public static char lerTecla() {
        try {
            int b = System.in.read();
            if (b == -1) return '\0';
            if (b != 27) return (char) b;

            if (System.in.available() == 0) return '\0';
            int br = System.in.read();
            if (br != '[') return '\0';
            int dir = System.in.read();
            switch (dir) {
                case 'A': return '↑';
                case 'B': return '↓';
                case 'C': return '→';
                case 'D': return '←';
                default: return '\0';
            }
        } catch (IOException e) {
            return '\0';
        }
    }

    private static String capturarEstado() {
        try {
            Process p = new ProcessBuilder("sh", "-c", "stty -g < /dev/tty").redirectErrorStream(true).start();
            return new String(p.getInputStream().readAllBytes()).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static void executar(String comando) {
        try {
            new ProcessBuilder("sh", "-c", comando + " < /dev/tty").inheritIO().start().waitFor();
        } catch (Exception e) {

        }
    }
}

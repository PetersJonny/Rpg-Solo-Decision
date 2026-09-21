package telas;

import java.io.IOException;

// Leitura de tecla única (sem precisar de Enter) em terminais unix/linux.
// Usa stty para colocar o terminal em modo cru e restaura ao final.
//
// ATENÇÃO (motivo desta blindagem): se o jogo for fechado de forma anormal no meio
// do labirinto (ex.: Ctrl+C), o finally não roda e o terminal fica sem eco (nada do
// que o jogador digita aparece fora do labirinto). Por isso registramos um hook de
// encerramento da JVM que SEMPRE restaura o terminal, mesmo em Ctrl+C/erro.
public class Teclado {

    private Teclado() {}

    private static boolean modoCruAtivo = false;
    private static String estadoSalvo; // stty -g antes de entrar em modo cru
    private static Thread hookRestauracao;

    // Ativa o modo cru: cada tecla é lida imediatamente, sem eco.
    public static synchronized void modoTeclaUnica() {
        if (modoCruAtivo) return;
        estadoSalvo = capturarEstado();
        executar("stty -icanon min 1 -echo < /dev/tty");
        modoCruAtivo = true;
        hookRestauracao = new Thread(Teclado::restaurar, "teclado-restaurar");
        Runtime.getRuntime().addShutdownHook(hookRestauracao);
    }

    // Restaura o terminal para o modo normal (eco + leitura por linha).
    // Idempotente e seguro para ser chamado no finally E no hook de encerramento.
    public static synchronized void restaurar() {
        if (!modoCruAtivo) return;
        modoCruAtivo = false;
        if (hookRestauracao != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(hookRestauracao);
            } catch (IllegalStateException e) {
                // JVM já está encerrando (o próprio hook está rodando): segue em frente.
            }
            hookRestauracao = null;
        }
        // Volta ao estado anterior salvo (preserva configs do usuário)...
        if (estadoSalvo != null && !estadoSalvo.isBlank()) {
            executar("stty " + estadoSalvo + " < /dev/tty");
        }
        // ...e garante o padrão sanado (eco + modo canônico), mesmo que o estado salvo falhe.
        executar("stty icanon echo < /dev/tty");
    }

    // Descarta bytes de entrada pendentes (ex.: sobra do ENTER dos menus) para que a
    // primeira tecla de movimento do labirinto nunca seja "engolida".
    public static void limparBuffer() {
        try {
            while (System.in.available() > 0) System.in.read();
        } catch (IOException e) {
            // Sem acesso à entrada: ignora.
        }
    }

    // Rede de segurança chamada no início do jogo: garante que o terminal está em
    // modo saudável (eco + canônico), recuperando até de uma sessão anterior que
    // tenha fechado de forma anormal no meio do labirinto.
    public static void assegurarTerminalSaudavel() {
        executar("stty icanon echo < /dev/tty");
    }

    // Lê uma única tecla pressionada (bloqueia até algo ser pressionado).
    // No modo cru, as setas chegam como sequência de escape: ESC [ A/B/C/D.
    // Aqui elas são convertidas para ↑ ↓ ← → (mesma direção do WASD); qualquer
    // outra tecla/sequência sem ser seta ou WASD é descartada (não faz nada).
    public static char lerTecla() {
        try {
            int b = System.in.read();
            if (b == -1) return '\0';
            if (b != 27) return (char) b; // não é ESC: tecla comum
            // Começou uma sequência de escape. Sem mais bytes é só um ESC solto.
            if (System.in.available() == 0) return '\0';
            int br = System.in.read();
            if (br != '[') return '\0'; // sequência desconhecida: nada acontece
            int dir = System.in.read();
            switch (dir) {
                case 'A': return '↑'; // seta para cima
                case 'B': return '↓'; // seta para baixo
                case 'C': return '→'; // seta para a direita
                case 'D': return '←'; // seta para a esquerda
                default: return '\0';  // outra tecla: nada acontece
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
            // Sem terminal disponível (ex.: execução em testes/CI): ignora.
        }
    }
}
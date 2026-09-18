package estruturas;

import fichas.FichaRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;
import telas.Teclado;

// O Labirinto do Minotauro concentra TODA a mecânica da estrutura (mesmo padrão
// do Vendedor): sorteio da descoberta, evento de encontro, menu do lugar e a
// navegação interna — a classe Floresta apenas dispara um gancho.
//
// Ao ser encontrado, o caminho é gerado de forma aleatória e salvo junto da
// ficha (Labirinto é serializável). Dentro do labirinto o jogador vê apenas a
// grade escura com o que já percorreu (iluminado) e o que está adjacente,
// movendo-se imediatamente com W/A/S/D. O objetivo desta etapa é chegar ao centro.
public class LabirintoDoMinotauro {

    // TEMP (só para teste): em true, a descoberta é sempre garantida (100%).
    // Reverter para false para voltar ao sorteio normal (1% + 1% por dia).
    private static final boolean TESTE_DESCOBERTA_GARANTIDA = true;

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERDE = Interface.VERDE;
    private static final String VERMELHO = Interface.VERMELHO;

    // Sorteia a descoberta durante uma exploração (1% + 1% a cada dia, até 100%).
    // Só é sorteado enquanto o labirinto não foi encontrado. Ao encontrar, o
    // caminho é randomizado/salvo e o evento de descoberta é rodado.
    public static boolean tentarDescoberta(FichaRpg ficha) {
        if (ficha.isLabirintoEncontrado()) return false;
        if (!TESTE_DESCOBERTA_GARANTIDA && MecanicasRpg.rolarDado(100) > ficha.getLabirintoChanceDescoberta()) {
            return false;
        }
        ficha.setLabirintoEncontrado(true);
        ficha.setLabirinto(new Labirinto());
        EncontrarEntrada(ficha);
        return true;
    }

    // Evento de descoberta: narrativa da entrada + escolha de entrar agora ou não.
    // Se não entrar agora, o local fica marcado e vira opção no menu principal.
    private static void EncontrarEntrada(FichaRpg ficha) {
        Interface.MostrarMensagem("\nEnquanto avança pela mata fechada, você tropeça em algo sólido e antigo...");
        Interface.Pausa(2500);
        Interface.MostrarMensagem("\nAfastando a vegetação, revela-se uma boca de pedra escura, engolida por raízes: a entrada de um labirinto antigo. O ar gelado que sai de dentro carrega o cheiro de ferrugem e de escuridão.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nVocê encontrou um " + CIANO + "LABIRINTO" + RESET + " antigo!");

        System.out.println("\n  O que deseja fazer?");
        System.out.println("  1. Entrar no Labirinto agora");
        System.out.println("  2. Não entrar agora (o local ficará marcado no menu)");
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            MenuLabirinto(ficha);
        } else {
            Interface.MostrarMensagem("\nVocê anota mentalmente a localização e se afasta da entrada.");
            Interface.Pausa(2000);
        }
    }

    // Menu do lugar: ao entrar, a interface vira total sobre o labirinto.
    public static void MenuLabirinto(FichaRpg ficha) {
        // Compat: saves antigos que já tinham encontrado, mas sem a grade gerada
        if (ficha.getLabirinto() == null) {
            ficha.setLabirinto(new Labirinto());
        }
        Labirinto lab = ficha.getLabirinto();

        while (true) {
            Interface.cabecalhoMenu("LABIRINTO");

            System.out.println("\n  " + CIANO + "A boca de pedra se abre diante de você. Corredores antigos serpenteiam na escuridão," + RESET);
            System.out.println("  cobertos de poeira e marcados por passos que não são humanos.");

            if (lab.isCentroAlcancado()) {
                System.out.println("\n  " + VERDE + "Você já alcançou o CENTRO do labirinto." + RESET);
            }

            System.out.println("\n  O que deseja fazer?\n");
            System.out.println("  1. " + (lab.isCentroAlcancado() ? "Percorrer os corredores do labirinto" : "Adentrar o labirinto"));
            System.out.println("  2. Ver ficha");
            System.out.println("  3. Voltar para a floresta");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            int escolha = Interface.lerOpcao(3);

            if (escolha == 1) {
                AdentrarLabirinto(ficha);
            } else if (escolha == 2) {
                Interface.MostrarFicha(ficha);
            } else {
                Interface.MostrarMensagem("\nVocê deixa a entrada do labirinto e retorna à floresta.");
                Interface.Pausa(1500);
                return;
            }
        }
    }

    // Navegação interna: só a grade do labirinto e as teclas W/A/S/D aparecem.
    // Cada tecla move imediatamente (sem Enter); qualquer outra tecla não faz nada.
    // Ao alcançar o centro (pela primeira vez), o objetivo é concluído.
    private static void AdentrarLabirinto(FichaRpg ficha) {
        Labirinto lab = ficha.getLabirinto();

        if (lab.isCentroAlcancado()) {
            Interface.MostrarMensagem("\nVocê está no centro do labirinto. Por ora, o objetivo aqui está completo.");
            Interface.Pausa(2000);
            return;
        }

        Interface.MostrarMensagem("\nVocê desce pela boca de pedra e adentra os corredores gelados do labirinto...");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\nA escuridão engole a luz atrás de você. Siga pela escuridão até o centro!");
        Interface.Pausa(2000);

        desenhar(lab, false, ' ');

        Teclado.modoTeclaUnica();
        try {
            Teclado.limparBuffer(); // descarta sobra do ENTER dos menus antes do 1º movimento
            while (true) {
                char tecla = Character.toLowerCase(Teclado.lerTecla());
                int[] delta = direcao(tecla);
                if (delta == null) continue; // qualquer outra tecla: nada acontece
                lab.mover(delta[0], delta[1]);
                desenhar(lab, false, Character.toUpperCase(tecla));
                processarEvento(lab); // casas de encontro/recompensa
                if (lab.isCentroAlcancado()) {
                    desenhar(lab, true, Character.toUpperCase(tecla));
                    Interface.MostrarMensagem("\n" + CIANO + "Você adentra o coração do labirinto!" + RESET);
                    Interface.Pausa(2500);
                    break;
                }
            }
        } finally {
            Teclado.restaurar();
        }
    }

    // Casas especiais: ENCONTRO (por ora placeholder; aqui entram os monstros a definir)
    // e RECOMPENSA (por ora placeholder; aqui entram os itens a definir). Cada casa
    // especial ativa apenas UMA vez.
    private static void processarEvento(Labirinto lab) {
        Labirinto.TipoCelula evento = lab.consumirEventoNaPosicao();
        if (evento == Labirinto.TipoCelula.ENCONTRO) {
            System.out.println("\n  " + VERMELHO + "!!!!!! ALGO SURGE DA ESCURIDÃO !!!!!!" + RESET);
            // TODO(monstros): o usuário vai passar a lista de monstros do labirinto —
            // sortear um aqui, mostrar o nome e abrir o combate (mecânicas já existem).
            System.out.println("  Uma criatura bestial estala a mandíbula diante de você e ataca!");
            Interface.Pausa(4000);
            desenhar(lab, false, ' ');
        } else if (evento == Labirinto.TipoCelula.RECOMPENSA) {
            System.out.println("\n  " + AMARELO + "Você tropeça em um baú antigo, intacto sob o pó." + RESET);
            // TODO(recompensas): o usuário vai passar a lista de recompensas —
            // sortear uma aqui e adicionar ao inventário.
            System.out.println("  Algo de valor brilha lá dentro quando você abre a tampa...");
            Interface.Pausa(4000);
            desenhar(lab, false, ' ');
        }
    }

    // Desenha a tela do labirinto: apenas o entorno do personagem (paredes laterais e
    // caminhos vizinhos) e o que já foi percorrido aparecem; o resto fica escuro.
    // O centro é sempre marcado por um grande quadrado. Uma "câmera" vertical acompanha
    // o jogador para que o labirinto (bem maior) caiba na tela.
    private static final int ALTURA_JANELA = 21;

    private static void desenhar(Labirinto lab, boolean centroAlcancado, char ultimaTecla) {
        System.out.print("\033[2J\033[H");
        int t = lab.getTamanho();
        int camIni = Math.max(0, Math.min(lab.getJogadorLinha() - ALTURA_JANELA / 2, t - ALTURA_JANELA));

        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n\n\n\n"); // desce o labirinto na tela
        for (int l = camIni; l < camIni + ALTURA_JANELA; l++) {
            for (int c = 0; c < t; c++) {
                sb.append(desenharCelula(lab, l, c));
            }
            sb.append("\n");
        }
        sb.append("\n");
        sb.append("  " + (centroAlcancado ? VERDE : AMARELO) + "W = cima   S = baixo   A = esquerda   D = direita" + RESET);
        if (ultimaTecla != ' ') {
            sb.append("      " + CIANO + "Última tecla: " + ultimaTecla + RESET);
        }
        sb.append("\n");
        System.out.print(sb);
        System.out.println();
    }

    private static String desenharCelula(Labirinto lab, int l, int c) {
        boolean entorno = Math.abs(l - lab.getJogadorLinha()) <= 1 && Math.abs(c - lab.getJogadorColuna()) <= 1;

        if (l == lab.getJogadorLinha() && c == lab.getJogadorColuna()) {
            return AMARELO + "@ " + RESET; // jogador
        }

        if (!lab.isCaminho(l, c)) {
            return entorno ? "██" : "  "; // paredes só aparecem ao redor do personagem
        }

        if (entorno) {
            return lab.isVisitado(l, c) ? CIANO + "· " + RESET : VERDE + ". " + RESET; // vizinho andável
        }
        return lab.isVisitado(l, c) ? CIANO + "· " + RESET : "  "; // só o que andou fica iluminado
    }

    // Converte a tecla em deslocamento de célula (W=↑, S=↓, A=←, D=→); null se inválida.
    private static int[] direcao(char tecla) {
        switch (Character.toLowerCase(tecla)) {
            case 'w': return new int[]{-1, 0};
            case 's': return new int[]{1, 0};
            case 'a': return new int[]{0, -1};
            case 'd': return new int[]{0, 1};
            default: return null;
        }
    }
}
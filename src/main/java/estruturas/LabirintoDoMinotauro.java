package estruturas;

import criaturas.Criatura;
import fichas.FichaRpg;
import java.util.Collections;
import java.util.List;
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
        Interface.Pausa(3500);
        Interface.MostrarMensagem("\nAfastando a vegetação, revela-se uma boca de pedra escura, engolida por raízes: a entrada de um labirinto antigo. O ar gelado que sai de dentro carrega o cheiro de ferrugem e de escuridão.");
        Interface.Pausa(4000);
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

        // Defensivo: com o centro alcançado, o labirinto já desmoronou e o menu
        // normalmente nem é aberto (a opção some do menu principal).
        if (lab.isCentroAlcancado()) {
            Interface.MostrarMensagem("\nO labirinto já desmoronou e se fechou para sempre.");
            Interface.Pausa(2000);
            return;
        }

        while (true) {
            Interface.cabecalhoMenu("LABIRINTO");

            System.out.println("\n  " + CIANO + "A boca de pedra se abre diante de você. Corredores antigos serpenteiam na escuridão," + RESET);
            System.out.println("  cobertos de poeira e marcados por passos que não são humanos.");

            System.out.println("\n  O que deseja fazer?\n");
            System.out.println("  1. Adentrar o labirinto");
            System.out.println("  2. Ver ficha");
            System.out.println("  3. Voltar para a floresta");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
            int escolha = Interface.lerOpcao(3);

            if (escolha == 1) {
                if (AdentrarLabirinto(ficha)) {
                    return; // o labirinto desmoronou: volta direto para a floresta
                }
                if (ficha.getVidaPersonagem() <= 0) {
                    return; // morreu dentro do labirinto: o Main encerra a jornada
                }
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
    // Ao alcançar o centro (pela primeira vez), o labirinto desmorona, o jogador
    // foge e volta à floresta — retorna true (ele não pode mais entrar).
    private static boolean AdentrarLabirinto(FichaRpg ficha) {
        Labirinto lab = ficha.getLabirinto();

        if (lab.isCentroAlcancado()) {
            Interface.MostrarMensagem("\nO labirinto já desmoronou. Não há mais nada aqui.");
            Interface.Pausa(2000);
            return false;
        }

        Interface.MostrarMensagem("\nVocê desce pela boca de pedra e adentra os corredores gelados do labirinto...");
        Interface.Pausa(3500);
        Interface.MostrarMensagem("\nA escuridão engole a luz atrás de você. Siga pela escuridão até o centro!");
        Interface.Pausa(3500);

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
                processarEvento(lab, ficha); // casas de encontro/recompensa
                if (ficha.getVidaPersonagem() <= 0) return false; // morreu no labirinto
                if (lab.isCentroAlcancado()) {
                    desenhar(lab, true, Character.toUpperCase(tecla));
                    Interface.MostrarMensagem("\n" + CIANO + "Você adentra o coração do labirinto!" + RESET);
                    Interface.Pausa(4000);
                    desmoronar();
                    return true;
                }
            }
        } finally {
            Teclado.restaurar();
        }
    }

    // Sequência de desmoronamento: ao alcançar o centro, o labirinto racha e desaba;
    // o jogador foge correndo pela trilha e volta à floresta. O lugar se fecha para sempre.
    // As frases se acumulam na tela (uma por uma, sem apagar as anteriores) e cada uma
    // fica tempo suficiente para o jogador ler com calma.
    private static void desmoronar() {
        String[] fases = {
            AMARELO + "De repente, as paredes de pedra começam a tremer..." + RESET,
            VERMELHO + "CRACH! As passagens racham e blocos do teto despencam atrás de você!" + RESET,
            VERMELHO + "Você corre de volta pela trilha iluminada, com o labirinto ruindo logo atrás..." + RESET,
            CIANO + "Por pouco, você atravessa a entrada antes que ela se feche em um estrondo." + RESET,
            VERDE + "O labirinto desmoronou e se fechou para sempre. Você está a salvo na floresta." + RESET
        };
        for (String fase : fases) {
            System.out.println();
            System.out.println("  " + fase);
            Interface.Pausa(4500);
        }
    }

    // Casas especiais do labirinto, ativadas uma única vez:
//   ENCONTRO    -> 40% Esqueleto, 40% Zumbi, 20% Baú
//   RECOMPENSA  -> 20% Baú (os outros 80% por enquanto não dão nada)
// O combate e os menus usam o terminal canonico (Enter), então esta rotina sai do
// modo tecla única antes das escolhas e volta para ele no final.
    private static void processarEvento(Labirinto lab, FichaRpg ficha) {
        Labirinto.TipoCelula evento = lab.consumirEventoNaPosicao();
        if (evento == Labirinto.TipoCelula.NORMAL) return;

        Teclado.restaurar(); // menus e combate esperam terminal canonico
        try {
            if (evento == Labirinto.TipoCelula.ENCONTRO) {
                int sorteio = MecanicasRpg.rolarDado(100);
                if (sorteio <= 40) {
                    encontrarMonstro(ficha, criaturas.CriaturaFactory.criarEsqueleto());
                } else if (sorteio <= 80) {
                    encontrarMonstro(ficha, criaturas.CriaturaFactory.criarZumbi());
                } else {
                    encontrarBau(ficha);
                }
            } else if (MecanicasRpg.rolarDado(100) <= 20) {
                encontrarBau(ficha); // RECOMPENSA: só 20% tem baú
            }
        } finally {
            Teclado.modoTeclaUnica(); // volta ao modo de movimento
            Teclado.limparBuffer();
        }
        desenhar(lab, false, ' '); // redesenha o labirinto depois do evento
    }

    // Encontro de combate: oferece Lutar ou Fugir (Teste de Destreza contra a
    // dificuldade da criatura). Se fugir falhar, o combate começa mesmo assim.
    private static void encontrarMonstro(FichaRpg ficha, Criatura criatura) {
        List<Criatura> inimigos = Collections.singletonList(criatura);

        Interface.MostrarMensagem("\nAlgo se move nas sombras dos corredores...");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\n" + VERMELHO + criatura.getNome().toUpperCase() + RESET + " surge diante de você, bloqueando o corredor!");
        Interface.Pausa(3000);

        System.out.println("  O que deseja fazer?");
        System.out.println("  1. Lutar");
        System.out.println("  2. Tentar Fugir");
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            Interface.MostrarMensagem("\nVocê saca sua arma e parte para cima!");
            Interface.Pausa(2500);
            mecanicas.MotorDeCombate.IniciarCombate(ficha, inimigos, false);
            return;
        }

        Interface.pressionarParaTeste("Destreza (Fuga)");
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + ficha.getDestrezaTeste();
        Interface.MostrarMensagem("-> Teste de Destreza (Fuga): " + dado + " (Dado) + " + ficha.getDestrezaTeste() + " (Atributo) = " + total + " (Dificuldade: " + criatura.getDcFuga() + ")");
        Interface.Pausa(3000);

        if (total >= criatura.getDcFuga()) {
            Interface.MostrarMensagem("\nVocê recua pelas sombras do corredor e " + criatura.getNome() + " perde o seu rastro.");
            Interface.Pausa(3000);
        } else {
            Interface.MostrarMensagem("\n" + criatura.getNome() + " corta seu caminho de volta e avança sobre você!");
            Interface.Pausa(3000);
            mecanicas.MotorDeCombate.IniciarCombate(ficha, inimigos, false);
        }
    }

    // Baú do labirinto: dá para abrir ou não. Se abrir, 50% ouro (7-14) ou um
    // Baú Monstruoso salta — e aí vale lutar ou fugir (Destreza, dificuldade 12).
    private static void encontrarBau(FichaRpg ficha) {
        Interface.MostrarMensagem("\n" + AMARELO + "Você tropeça em um baú antigo, intacto sob o pó." + RESET);
        Interface.Pausa(3000);

        System.out.println("  O que deseja fazer?");
        System.out.println("  1. Abrir o baú");
        System.out.println("  2. Não abrir e seguir caminho");
        int escolha = Interface.lerOpcao(2);

        if (escolha == 2) {
            Interface.MostrarMensagem("\nVocê deixa o baú selado e segue pelos corredores.");
            Interface.Pausa(2500);
            return;
        }

        Interface.MostrarMensagem("\nVocê ergue a tampa do baú...");
        Interface.Pausa(2500);

        if (MecanicasRpg.rolarDado(100) <= 50) {
            int ouro = MecanicasRpg.rolarEntre(7, 14);
            ficha.adicionarOuro(ouro);
            Interface.MostrarMensagem(VERDE + "O baú está cheio de moedas! Você coleta " + ouro + " de ouro." + RESET);
            Interface.Pausa(3000);
        } else {
            Interface.MostrarMensagem(VERMELHO + "A tampa se escancara! Dentes afiados cerram contra você: é uma armadilha!" + RESET);
            Interface.Pausa(3000);
            encontrarMonstro(ficha, criaturas.CriaturaFactory.criarBauMonstruoso());
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
        sb.append("  " + (centroAlcancado ? VERDE : AMARELO) + "Mover: W/↑ = cima   S/↓ = baixo   A/← = esquerda   D/→ = direita" + RESET);
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

    // Converte a tecla em deslocamento de célula. WASD e as setas ↑ ↓ ← → funcionam
    // iguais; qualquer outra tecla retorna null (nada acontece).
    private static int[] direcao(char tecla) {
        switch (Character.toLowerCase(tecla)) {
            case 'w':
            case '↑': return new int[]{-1, 0};
            case 's':
            case '↓': return new int[]{1, 0};
            case 'a':
            case '←': return new int[]{0, -1};
            case 'd':
            case '→': return new int[]{0, 1};
            default: return null;
        }
    }
}
package estruturas;

import telas.MenuVisualizacao;

import criaturas.Criatura;
import fichas.FichaRpg;
import java.util.Collections;
import java.util.List;
import mecanicas.MecanicasRpg;
import telas.Interface;
import telas.Teclado;

public class LabirintoDoMinotauro {

    private static final boolean TESTE_DESCOBERTA_GARANTIDA = false;

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERDE = Interface.VERDE;
    private static final String VERMELHO = Interface.VERMELHO;

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

    public static void MenuLabirinto(FichaRpg ficha) {

        if (ficha.getLabirinto() == null) {
            ficha.setLabirinto(new Labirinto());
        }
        Labirinto lab = ficha.getLabirinto();

        if (lab.isCentroAlcancado()) {
            Interface.MostrarMensagem("\nO labirinto já desmoronou e se fechou para sempre.");
            Interface.Pausa(2000);
            return;
        }

        ficha.sairDaCabana();

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
                    return;
                }
                if (ficha.getVidaPersonagem() <= 0) {
                    return;
                }
            } else if (escolha == 2) {
                MenuVisualizacao.MostrarFicha(ficha);
            } else {
                Interface.MostrarMensagem("\nVocê deixa a entrada do labirinto e retorna à floresta.");
                Interface.Pausa(1500);
                return;
            }
        }
    }

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
            Teclado.limparBuffer();
            while (true) {
                char tecla = Character.toLowerCase(Teclado.lerTecla());
                int[] delta = direcao(tecla);
                if (delta == null) continue;
                lab.mover(delta[0], delta[1]);
                desenhar(lab, false, Character.toUpperCase(tecla));
                processarEvento(lab, ficha);
                if (ficha.getVidaPersonagem() <= 0) return false;
                if (lab.isCentroAlcancado()) {
                    desenhar(lab, true, Character.toUpperCase(tecla));
                    Interface.MostrarMensagem("\n" + CIANO + "Você adentra o coração do labirinto!" + RESET);
                    Interface.Pausa(4000);
                    if (!ficha.isMinotauroDerrotado()) {
                        Teclado.restaurar();
                        try {
                            enfrentarMinotauro(ficha);
                        } finally {
                            Teclado.modoTeclaUnica();
                            Teclado.limparBuffer();
                        }
                        if (ficha.getVidaPersonagem() <= 0) return false;
                        if (!ficha.isMinotauroDerrotado()) continue;
                    }
                    desmoronar();
                    return true;
                }
            }
        } finally {
            Teclado.restaurar();
        }
    }

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

    private static void processarEvento(Labirinto lab, FichaRpg ficha) {
        Labirinto.TipoCelula evento = lab.consumirEventoNaPosicao();
        if (evento == Labirinto.TipoCelula.NORMAL) return;

        Teclado.restaurar();
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
            } else {
                gerarRecompensa(ficha);
            }
        } finally {
            Teclado.modoTeclaUnica();
            Teclado.limparBuffer();
        }
        desenhar(lab, false, ' ');
    }

    private static void gerarRecompensa(FichaRpg ficha) {
        Interface.MostrarMensagem("\nEntre as pedras do corredor, algo foi esquecido por alguém há muito tempo.");
        Interface.Pausa(3000);

        System.out.println("  O que deseja fazer?");
        System.out.println("  1. Pegar a recompensa");
        System.out.println("  2. Não pegar e seguir caminho");
        int escolha = Interface.lerOpcao(2);

        if (escolha == 2) {
            Interface.MostrarMensagem("\nVocê deixa a recompensa para trás e segue pelos corredores.");
            Interface.Pausa(2500);
            return;
        }

        int sorteio = MecanicasRpg.rolarDado(100);
        if (sorteio <= 80) {
            int ouro = MecanicasRpg.rolarEntre(7, 19);
            ficha.adicionarOuro(ouro);
            Interface.MostrarMensagem(VERDE + "Você pegou " + ouro + " de ouro de um pote de moedas esquecidas!" + RESET);
            Interface.Pausa(3000);
        } else if (sorteio <= 97) {
            itens.ItemRpg arma = loja.Vendedor.sortearArmaDoJogo();
            arma.setQuantidade(1);
            ficha.coletarItemEncontrado(arma, "Você pegou");
        } else if (!ficha.isOlhoDemonicoEncontrado()) {
            encontrarOlhoDemonico(ficha);
        } else if (!ficha.isEspadaMajestralEncontrada()) {
            encontrarEspadaMajestral(ficha);
        } else if (!ficha.isCoroaReiEncontrada()) {
            encontrarCoroaDoRei(ficha);
        } else {
            int ouro = MecanicasRpg.rolarEntre(7, 19);
            ficha.adicionarOuro(ouro);
            Interface.MostrarMensagem(VERDE + "Você pegou " + ouro + " de ouro de um pote de moedas esquecidas!" + RESET);
            Interface.Pausa(3000);
        }
    }

    private static void encontrarOlhoDemonico(FichaRpg ficha) {
        ficha.setOlhoDemonicoEncontrado(true);
        Interface.MostrarMensagem(VERMELHO + "Nas trevas, algo a observa com um olho único e pulsante..." + RESET);
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nÉ um Olho Demoníaco, borbulhando sobre um pedestal de pedra. Um chamado sussurra em sua mente, oferecendo poder sobre as criaturas do abismo.");
        Interface.Pausa(3500);

        System.out.println("  O que deseja fazer?");
        System.out.println("  1. Aceitar o chamado");
        System.out.println("  2. Recusar e seguir caminho");
        int escolha = Interface.lerOpcao(2);

        if (escolha == 1) {
            ficha.setOlhoDemonicoFundido(true);
            ficha.getHabilidades().add(new habilidades.ativas.HabilidadePactoMortal());
            Interface.MostrarMensagem(VERMELHO + "O olho desliza pelo seu corpo até seu rosto e se funde à sua própria carne. Ele agora é PARTE de você... para sempre." + RESET);
            Interface.MostrarMensagem("O conhecimento do Pacto Mortal flui pelos seus veios.");
            Interface.Pausa(3500);
        } else {
            Interface.MostrarMensagem("\nVocê vira as costas ao olho pulsante, que se dissolve em pó por trás de você.");
            Interface.Pausa(2500);
        }
    }

    private static void encontrarEspadaMajestral(FichaRpg ficha) {
        ficha.setEspadaMajestralEncontrada(true);
        Interface.MostrarMensagem(CIANO + "Uma luz dourada rasga as sombras: cravada na rocha, uma espada majestral banhada a ouro espera por você." + RESET);
        Interface.Pausa(3500);
        ficha.adicionarItem(new itens.Arma("Espada Majestral", "Uma espada banhada em ouro e magia antiga. Causa 1d12 de dano e +1d4 de dano de luz, usando Força. Dobra o dano contra mortos-vivos.", "CaC", 12, 1, 1));
        Interface.MostrarMensagem(VERDE + "Você ergue a Espada Majestral! Ela brilha com a promessa de destruir os mortos-vivos." + RESET);
        Interface.Pausa(3000);
    }

    private static void encontrarCoroaDoRei(FichaRpg ficha) {
        ficha.setCoroaReiEncontrada(true);
        Interface.MostrarMensagem(AMARELO + "Sentada em um trono de pedra, uma coroa enferrujada aguarda. Perto dela, criaturas parecem hesitar em avançar." + RESET);
        Interface.Pausa(3500);
        ficha.adicionarItem(new itens.ItemRpg("Coroa do Rei", "A coroa do senhor do labirinto. Vale 1000 moedas de ouro... e talvez guarde um segredo.", 1));
        Interface.MostrarMensagem(VERDE + "Você pegou a Coroa do Rei!" + RESET);
        Interface.Pausa(2500);

        Interface.pressionarParaTeste("Intelecto (Desvendar o segredo)");
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + ficha.getIntelectoTeste();
        Interface.MostrarMensagem("-> Teste de Intelecto: " + dado + " (Dado) + " + ficha.getIntelectoTeste() + " (Atributo) = " + total + " (Dificuldade: 18)");
        Interface.Pausa(3000);

        if (total < 18 && ficha.podeUsarMenteAfiada()) {
            Interface.MostrarMensagem("\n(Mente Afiada!) Sua mente aguçada permite reavaliar as runas... Deseja rolar novamente?");
            if (Interface.lerOpcao(2) == 1) {
                ficha.marcarMenteAfiadaUsada();
                dado = MecanicasRpg.rolarDado(20);
                total = dado + ficha.getIntelectoTeste();
                Interface.MostrarMensagem("-> Nova tentativa (Intelecto): " + dado + " (Dado) + " + ficha.getIntelectoTeste() + " (Atributo) = " + total + " (Dificuldade: 18)");
                Interface.Pausa(3000);
            }
        }

        if (total >= 18) {
            ficha.setReiDasCriaturas(true);
            Interface.MostrarMensagem(AMARELO + "Você decifra as runas esculpidas sob o aro da coroa... e sente o título de Rei das Criaturas pulsar em seu peito!" + RESET);
            Interface.Pausa(3000);
        } else {
            Interface.MostrarMensagem("\nA coroa parece apenas uma peça velha e valiosa. Você a guarda mesmo assim.");
            Interface.Pausa(2500);
        }
    }

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

    private static void enfrentarMinotauro(FichaRpg ficha) {
        Interface.MostrarMensagem(VERMELHO + "Um rugido estala entre as pedras e o chão treme. Das sombras do coração do labirinto surge uma silhueta colossal..." + RESET);
        Interface.Pausa(4000);
        Interface.MostrarMensagem(VERMELHO + "O MINOTAURO ergue-se diante de você, e a porta atrás de você se fecha com um estrondo. Não há como fugir!" + RESET);
        Interface.Pausa(4000);

        List<Criatura> inimigos = Collections.singletonList(criaturas.CriaturaFactory.criarMinotauro());
        mecanicas.MotorDeCombate.IniciarCombate(ficha, inimigos, false);

        if (ficha.getVidaPersonagem() <= 0) return;

        for (Criatura c : inimigos) {
            if (c.getVida() <= 0 && !c.isFugiu()) {
                ficha.setMinotauroDerrotado(true);
                return;
            }
        }

        Interface.MostrarMensagem("\nO Minotauro recua para as sombras, e a porta se reabre com um rangido. Você pode tentar de novo.");
        Interface.Pausa(3000);
    }

    private static final int ALTURA_JANELA = 21;

    private static void desenhar(Labirinto lab, boolean centroAlcancado, char ultimaTecla) {
        System.out.print("\033[2J\033[H");
        int t = lab.getTamanho();
        int camIni = Math.max(0, Math.min(lab.getJogadorLinha() - ALTURA_JANELA / 2, t - ALTURA_JANELA));

        StringBuilder sb = new StringBuilder();
        sb.append("\n\n\n\n\n\n");
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
            return AMARELO + "@ " + RESET;
        }

        if (!lab.isCaminho(l, c)) {
            return entorno ? "██" : "  ";
        }

        if (entorno) {
            return lab.isVisitado(l, c) ? CIANO + "· " + RESET : VERDE + ". " + RESET;
        }
        return lab.isVisitado(l, c) ? CIANO + "· " + RESET : "  ";
    }

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

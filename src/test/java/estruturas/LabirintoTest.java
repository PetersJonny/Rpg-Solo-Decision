package estruturas;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LabirintoTest {

    private Labirinto lab;
    private int t;
    private int centro;

    @BeforeEach
    void setUp() {
        lab = new Labirinto();
        t = lab.getTamanho();
        centro = t / 2;
    }

    @Test
    void testTamanhoEEntradaConfigurados() {
        assertEquals(31, t);
        // Entrada única na borda inferior, alinhada ao centro em coluna
        assertEquals(t - 1, lab.getEntradaLinha());
        assertEquals(centro, lab.getEntradaColuna());
        assertEquals(lab.getEntradaLinha(), lab.getJogadorLinha());
        assertEquals(lab.getEntradaColuna(), lab.getJogadorColuna());
    }

    @Test
    void testExisteApenasUmaEntrada() {
        // Só a célula da entrada é caminho na borda inferior; as demais bordas são paredes
        int entradaNaBorda = 0;
        for (int c = 0; c < t; c++) {
            if (lab.isCaminho(t - 1, c)) entradaNaBorda++;
            assertFalse(lab.isCaminho(0, c), "Borda superior não pode ter passagem");
            assertFalse(lab.isCaminho(c, 0), "Borda esquerda não pode ter passagem");
            assertFalse(lab.isCaminho(c, t - 1), "Borda direita não pode ter passagem");
        }
        assertEquals(1, entradaNaBorda);
    }

    @Test
    void testCentroEhCaminhoEConectado() {
        assertTrue(lab.isCaminho(centro, centro), "Centro deve ser um corredor");
        assertTrue(lab.estaConectado(), "Todos os corredores devem se conectar à entrada");
    }

    @Test
    void testPracaCentralAberta() {
        // A praça 5x5 ao redor do centro é toda aberta (o "grande quadrado")
        for (int l = centro - 2; l <= centro + 2; l++) {
            for (int c = centro - 2; c <= centro + 2; c++) {
                assertTrue(lab.isCaminho(l, c));
            }
        }
        // Borda da praça reconhecida; interior não é borda
        assertTrue(lab.isRegiaoCentro(centro, centro));
        assertTrue(lab.isBordaCentro(centro - 2, centro));
        assertTrue(lab.isBordaCentro(centro, centro + 2));
        assertFalse(lab.isBordaCentro(centro, centro));
        assertTrue(lab.estaConectado());
    }

    @Test
    void testNaoMoveParaParede() {
        int antesL = lab.getJogadorLinha();
        int antesC = lab.getJogadorColuna();
        // Da entrada, qualquer direção que não seja "cima" é parede/fora
        assertFalse(lab.mover(0, 1));
        assertFalse(lab.mover(0, -1));
        assertFalse(lab.mover(1, 0));
        assertEquals(antesL, lab.getJogadorLinha());
        assertEquals(antesC, lab.getJogadorColuna());
    }

    @Test
    void testMovePeloCaminhoEMarcaVisitado() {
        // Da entrada o vizinho de cima é um corredor
        assertTrue(lab.isCaminho(t - 2, centro));
        assertTrue(lab.mover(-1, 0));
        assertEquals(t - 2, lab.getJogadorLinha());
        assertEquals(centro, lab.getJogadorColuna());
        assertTrue(lab.isVisitado(t - 1, centro), "Posição anterior deve ficar iluminada");
        assertTrue(lab.isVisitado(t - 2, centro), "Posição atual deve ficar iluminada");
        assertFalse(lab.chegouAoCentro());
    }

    @Test
    void testPercorreAteOCentroPeloCaminho() {
        // Resolve o labirinto (BFS) e aplica os movimentos até alcançar o centro
        int[] caminho = caminhoAteO(lab.getEntradaLinha(), lab.getEntradaColuna(), centro, centro);
        for (int[] passo : direcoesDoCaminho(caminho)) {
            assertTrue(lab.mover(passo[0], passo[1]));
        }
        assertTrue(lab.chegouAoCentro());
        assertTrue(lab.isCentroAlcancado());
    }

    @Test
    void testMovimentoInválidoNãoIlumina() {
        // Paredes longe do jogador não ficam visitadas nem são caminho
        assertFalse(lab.isVisitado(0, 0));
        assertFalse(lab.isCaminho(0, 0));
    }

    @Test
    void testCasasEspeciaisPosicionadasEmCorredoresValidos() {
        int encontros = 0;
        int recompensas = 0;
        for (int l = 0; l < t; l++) {
            for (int c = 0; c < t; c++) {
                Labirinto.TipoCelula tipo = lab.getTipoCelula(l, c);
                if (tipo == Labirinto.TipoCelula.ENCONTRO) encontros++;
                if (tipo == Labirinto.TipoCelula.RECOMPENSA) recompensas++;
                if (tipo != Labirinto.TipoCelula.NORMAL) {
                    assertTrue(lab.isCaminho(l, c), "Casa especial fora de um corredor");
                    assertFalse(lab.isRegiaoCentro(l, c), "Casa especial dentro da área central");
                    assertFalse(l == lab.getEntradaLinha() && c == lab.getEntradaColuna(), "Casa especial na entrada");
                }
            }
        }
        assertEquals(Labirinto.QUANTIDADE_ENCONTROS, encontros);
        assertEquals(Labirinto.QUANTIDADE_RECOMPENSAS, recompensas);
    }

    @Test
    void testCasaEspecialAtivaUmaUnicaVez() {
        // Encontra uma casa de encontro e caminha até ela
        int lAlvo = -1;
        int cAlvo = -1;
        for (int l = 0; l < t && lAlvo < 0; l++) {
            for (int c = 0; c < t; c++) {
                if (lab.getTipoCelula(l, c) == Labirinto.TipoCelula.ENCONTRO) {
                    lAlvo = l;
                    cAlvo = c;
                    break;
                }
            }
        }
        assertTrue(lAlvo >= 0, "Deveria existir pelo menos uma casa de encontro");

        int[] caminho = caminhoAteO(lab.getEntradaLinha(), lab.getEntradaColuna(), lAlvo, cAlvo);
        for (int[] passo : direcoesDoCaminho(caminho)) {
            assertTrue(lab.mover(passo[0], passo[1]));
        }
        assertEquals(Labirinto.TipoCelula.ENCONTRO, lab.consumirEventoNaPosicao());
        assertEquals(Labirinto.TipoCelula.NORMAL, lab.consumirEventoNaPosicao(), "Casa especial não pode ativar de novo");
    }

    @Test
    void testEntrarNaPrimeiraCasaDoCentroEncerraOLabirinto() {
        assertFalse(lab.isCentroAlcancado());
        // Borda do centro: a primeira casa "dentro do meio"
        int alvoL = lab.getCentroLinha() - 2;
        int alvoC = lab.getCentroColuna();
        int[] caminho = caminhoAteO(lab.getEntradaLinha(), lab.getEntradaColuna(), alvoL, alvoC);
        for (int[] passo : direcoesDoCaminho(caminho)) {
            assertTrue(lab.mover(passo[0], passo[1]));
            if (lab.isCentroAlcancado()) break;
        }
        assertTrue(lab.isCentroAlcancado(), "Ao pisar na primeira casa do centro o labirinto deve terminar");
        assertTrue(lab.chegouAoCentro());
    }

    @Test
    void testConcluirLabirintoRemoveDisponibilidadeDoMenu() {
        fichas.FichaRpg ficha = new fichas.FichaRpg("Teste");
        ficha.setLabirintoEncontrado(true);
        ficha.setLabirinto(lab);
        assertTrue(ficha.isLabirintoDisponivel(), "Antes do centro, a opção deve existir no menu");

        int alvoL = lab.getCentroLinha() - 2;
        int alvoC = lab.getCentroColuna();
        int[] caminho = caminhoAteO(lab.getEntradaLinha(), lab.getEntradaColuna(), alvoL, alvoC);
        for (int[] passo : direcoesDoCaminho(caminho)) {
            assertTrue(lab.mover(passo[0], passo[1]));
            if (lab.isCentroAlcancado()) break;
        }
        assertTrue(lab.isCentroAlcancado(), "O centro deve ter sido alcançado");
        assertFalse(ficha.isLabirintoDisponivel(), "Após o centro, a opção deve sumir do menu");
    }

    // BFS da posição atual até um destino; retorna o caminho de células [linha, coluna]
    private int[] caminhoAteO(int origemL, int origemC, int destinoL, int destinoC) {
        int total = t * t;
        boolean[][] visitado = new boolean[t][t];
        int[] anterior = new int[total];
        for (int i = 0; i < total; i++) anterior[i] = -1;
        ArrayDeque<Integer> fila = new ArrayDeque<>();
        int ini = origemL * t + origemC;
        int dst = destinoL * t + destinoC;
        visitado[origemL][origemC] = true;
        fila.add(ini);

        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!fila.isEmpty()) {
            int atual = fila.poll();
            if (atual == dst) break;
            int l = atual / t, c = atual % t;
            for (int[] d : dirs) {
                int nl = l + d[0], nc = c + d[1];
                if (nl < 0 || nc < 0 || nl >= t || nc >= t) continue;
                if (!lab.isCaminho(nl, nc) || visitado[nl][nc]) continue;
                visitado[nl][nc] = true;
                anterior[nl * t + nc] = atual;
                fila.add(nl * t + nc);
            }
        }
        assertNotEquals(-1, anterior[dst], "Destino deve ser alcançável");

        // Reconstrói do destino até a origem
        int[] reverso = new int[total];
        int n = 0;
        int cel = dst;
        while (cel != ini) {
            reverso[n++] = cel;
            cel = anterior[cel];
        }
        reverso[n++] = ini;
        int[] resultado = new int[n];
        for (int i = 0; i < n; i++) {
            resultado[i] = reverso[n - 1 - i];
        }
        return resultado;
    }

    // Converte células do caminho em deslocamentos [dLinha, dColuna]
    private int[][] direcoesDoCaminho(int[] caminho) {
        int[][] dirs = new int[caminho.length - 1][2];
        for (int i = 0; i < caminho.length - 1; i++) {
            dirs[i][0] = (caminho[i + 1] / t) - (caminho[i] / t);
            dirs[i][1] = (caminho[i + 1] % t) - (caminho[i] % t);
        }
        return dirs;
    }
}
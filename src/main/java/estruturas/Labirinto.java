package estruturas;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Grade navegável do Labirinto do Minotauro (serializável: fica salva na ficha).
// O caminho é gerado de forma aleatória uma única vez, no momento da descoberta;
// o jogador reconhece apenas que já andou (iluminado) e o que está adjacente.
// Algumas casas são especiais: de ENCONTRO (monstros) e de RECOMPENSA (itens),
// sorteadas na geração. O labirinto termina ao entrar na primeira casa do centro.
public class Labirinto implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int TAMANHO = 31; // grade 31x31 (15x15 de corredores)
    private static final int RAIO_CENTRO = 2; // praça central 5x5 (o "meio" do labirinto)

    // Quantidade de casas especiais por labirinto (ajustável)
    static final int QUANTIDADE_ENCONTROS = 8;
    static final int QUANTIDADE_RECOMPENSAS = 6;

    // Tipos de casa: ENCONTRO (monstro) e RECOMPENSA (item) ativam UMA vez por labirinto.
    public enum TipoCelula { NORMAL, ENCONTRO, RECOMPENSA }

    private final boolean[][] caminhos; // true = célula andável
    private final boolean[][] visitados; // true = já percorrido (fica iluminado)
    private final TipoCelula[][] tipoCelula; // marca as casas especiais
    private int jogadorLinha, jogadorColuna;
    private boolean centroAlcancado = false;
    private final int centroLinha, centroColuna;
    private final int entradaLinha, entradaColuna;

    public Labirinto() {
        this.caminhos = new boolean[TAMANHO][TAMANHO];
        this.visitados = new boolean[TAMANHO][TAMANHO];
        this.tipoCelula = new TipoCelula[TAMANHO][TAMANHO];
        for (int l = 0; l < TAMANHO; l++) {
            for (int c = 0; c < TAMANHO; c++) {
                tipoCelula[l][c] = TipoCelula.NORMAL;
            }
        }
        this.centroLinha = TAMANHO / 2;
        this.centroColuna = TAMANHO / 2;
        this.entradaLinha = TAMANHO - 1;
        this.entradaColuna = TAMANHO / 2;

        gerarCaminhos();
        abrirPracaCentral(); // espaço aberto ao redor do centro
        posicionarEventos(); // sorteia as casas de encontro e de recompensa
        // Abre a única passagem na borda inferior (a entrada)
        caminhos[entradaLinha][entradaColuna] = true;
        this.jogadorLinha = entradaLinha;
        this.jogadorColuna = entradaColuna;
        visitados[entradaLinha][entradaColuna] = true;
    }

    // Backtracker aleatório: começa no centro e abre passagens entre as células
    // ímpares (corredores). As células pares ficam como paredes.
    private void gerarCaminhos() {
        boolean[][] passou = new boolean[TAMANHO][TAMANHO];
        List<int[]> pilha = new ArrayList<>();
        Random rnd = new Random();
        int[][] direcoes = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};

        caminhos[centroLinha][centroColuna] = true;
        passou[centroLinha][centroColuna] = true;
        pilha.add(new int[]{centroLinha, centroColuna});

        while (!pilha.isEmpty()) {
            int[] atual = pilha.get(pilha.size() - 1);
            List<int[]> vizinhosLivres = new ArrayList<>();
            for (int[] dir : direcoes) {
                int nl = atual[0] + dir[0];
                int nc = atual[1] + dir[1];
                if (nl < 1 || nc < 1 || nl >= TAMANHO - 1 || nc >= TAMANHO - 1) continue;
                if (passou[nl][nc]) continue;
                vizinhosLivres.add(new int[]{nl, nc, dir[0] / 2, dir[1] / 2});
            }
            if (vizinhosLivres.isEmpty()) {
                pilha.remove(pilha.size() - 1);
                continue;
            }
            int[] escolhido = vizinhosLivres.get(rnd.nextInt(vizinhosLivres.size()));
            // Abre a parede entre a célula atual e a escolhida
            caminhos[atual[0] + escolhido[2]][atual[1] + escolhido[3]] = true;
            caminhos[escolhido[0]][escolhido[1]] = true;
            passou[escolhido[0]][escolhido[1]] = true;
            pilha.add(new int[]{escolhido[0], escolhido[1]});
        }
    }

    // Tenta andar uma célula; retorna false se a posição for parede/fora do labirinto.
    // Ao pisar, o local fica iluminado (visitado) e persiste no save.
    public boolean mover(int deslocLinha, int deslocColuna) {
        int nl = jogadorLinha + deslocLinha;
        int nc = jogadorColuna + deslocColuna;
        if (nl < 0 || nc < 0 || nl >= TAMANHO || nc >= TAMANHO) return false;
        if (!caminhos[nl][nc]) return false;
        jogadorLinha = nl;
        jogadorColuna = nc;
        visitados[nl][nc] = true;
        // O labirinto termina ao entrar na primeira casa do centro (área do minotauro)
        if (isRegiaoCentro(nl, nc)) {
            centroAlcancado = true;
        }
        return true;
    }

    // Abre uma praça 5x5 ao redor do centro: o "meio" do labirinto (área do minotauro).
    private void abrirPracaCentral() {
        for (int l = centroLinha - RAIO_CENTRO; l <= centroLinha + RAIO_CENTRO; l++) {
            for (int c = centroColuna - RAIO_CENTRO; c <= centroColuna + RAIO_CENTRO; c++) {
                caminhos[l][c] = true;
            }
        }
    }

    // Sorteia aleatoriamente as casas de ENCONTRO (monstros) e de RECOMPENSA (itens)
    // entre os corredores válidos. A entrada e a área central ficam livres.
    private void posicionarEventos() {
        List<int[]> candidatas = new ArrayList<>();
        for (int l = 0; l < TAMANHO; l++) {
            for (int c = 0; c < TAMANHO; c++) {
                if (!caminhos[l][c]) continue;
                if (isRegiaoCentro(l, c)) continue;
                if (l == entradaLinha && c == entradaColuna) continue;
                candidatas.add(new int[]{l, c});
            }
        }
        Collections.shuffle(candidatas, new Random());
        for (int i = 0; i < QUANTIDADE_ENCONTROS && i < candidatas.size(); i++) {
            tipoCelula[candidatas.get(i)[0]][candidatas.get(i)[1]] = TipoCelula.ENCONTRO;
        }
        for (int i = QUANTIDADE_ENCONTROS; i < QUANTIDADE_ENCONTROS + QUANTIDADE_RECOMPENSAS && i < candidatas.size(); i++) {
            tipoCelula[candidatas.get(i)[0]][candidatas.get(i)[1]] = TipoCelula.RECOMPENSA;
        }
    }

    // Retorna o evento da casa atual e o consome (cada casa especial ativa UMA vez).
    public TipoCelula consumirEventoNaPosicao() {
        TipoCelula tipo = tipoCelula[jogadorLinha][jogadorColuna];
        if (tipo != TipoCelula.NORMAL) {
            tipoCelula[jogadorLinha][jogadorColuna] = TipoCelula.NORMAL;
        }
        return tipo;
    }

    // Praça central (5x5) e sua borda (o quadrado grande, sempre visível)
    public boolean isRegiaoCentro(int linha, int coluna) {
        return Math.abs(linha - centroLinha) <= RAIO_CENTRO && Math.abs(coluna - centroColuna) <= RAIO_CENTRO;
    }

    public boolean isBordaCentro(int linha, int coluna) {
        return isRegiaoCentro(linha, coluna)
                && (Math.abs(linha - centroLinha) == RAIO_CENTRO || Math.abs(coluna - centroColuna) == RAIO_CENTRO);
    }

    public boolean chegouAoCentro() {
        return isRegiaoCentro(jogadorLinha, jogadorColuna);
    }

    public boolean isCentroAlcancado() { return centroAlcancado; }

    public boolean isCaminho(int linha, int coluna) { return caminhos[linha][coluna]; }
    public boolean isVisitado(int linha, int coluna) { return visitados[linha][coluna]; }
    public TipoCelula getTipoCelula(int linha, int coluna) { return tipoCelula[linha][coluna]; }
    public boolean isCentro(int linha, int coluna) {
        return linha == centroLinha && coluna == centroColuna;
    }
    public boolean isPertoDoJogador(int linha, int coluna) {
        return Math.abs(linha - jogadorLinha) + Math.abs(coluna - jogadorColuna) == 1;
    }

    public int getTamanho() { return TAMANHO; }
    public int getJogadorLinha() { return jogadorLinha; }
    public int getJogadorColuna() { return jogadorColuna; }
    public int getCentroLinha() { return centroLinha; }
    public int getCentroColuna() { return centroColuna; }
    public int getEntradaLinha() { return entradaLinha; }
    public int getEntradaColuna() { return entradaColuna; }

    // Integridade: todos os corredores se conectam à entrada (usado em testes).
    public boolean estaConectado() {
        boolean[][] alcancado = new boolean[TAMANHO][TAMANHO];
        ArrayDeque<int[]> fila = new ArrayDeque<>();
        fila.add(new int[]{entradaLinha, entradaColuna});
        alcancado[entradaLinha][entradaColuna] = true;

        int totalCaminhos = 0;
        for (int l = 0; l < TAMANHO; l++) {
            for (int c = 0; c < TAMANHO; c++) {
                if (caminhos[l][c]) totalCaminhos++;
            }
        }

        int alcancados = 0;
        while (!fila.isEmpty()) {
            int[] pos = fila.poll();
            alcancados++;
            for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int nl = pos[0] + d[0];
                int nc = pos[1] + d[1];
                if (nl < 0 || nc < 0 || nl >= TAMANHO || nc >= TAMANHO) continue;
                if (!caminhos[nl][nc] || alcancado[nl][nc]) continue;
                alcancado[nl][nc] = true;
                fila.add(new int[]{nl, nc});
            }
        }
        return alcancados == totalCaminhos;
    }
}
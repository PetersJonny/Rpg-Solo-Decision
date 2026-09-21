package criaturas;

import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;

public class CriaturaFactory {

    public static List<Criatura> criarGrupoMonstros(int tipo, boolean deNoite) {
        List<Criatura> grupo = new ArrayList<>();
        int quantidade;

        switch (tipo) {
            case 1: { // Lobo Selvagem: de dia 1-2, de noite 1-4
                quantidade = deNoite ? MecanicasRpg.rolarEntre(1, 4) : MecanicasRpg.rolarEntre(1, 2);
                for (int i = 0; i < quantidade; i++) {
                    grupo.add(criarLobo());
                }
                break;
            }
            case 2: { // Urso: 1
                grupo.add(criarUrso());
                break;
            }
            default: { // Bandido: de dia 1-3, de noite 1-5
                quantidade = deNoite ? MecanicasRpg.rolarEntre(1, 5) : MecanicasRpg.rolarEntre(1, 3);
                for (int i = 0; i < quantidade; i++) {
                    grupo.add(criarBandido());
                }
                break;
            }
        }

        return grupo;
    }

    public static Criatura criarLobo() {
        Criatura c = new Criatura("Lobo Selvagem", 1, 14, 10, 3);
        c.setBonusAcerto(3);
        c.setTestePresenca(8);
        c.setXpGanho(25);
        c.adicionarAtaque("Mordida", "", 1, 6);
        c.adicionarAtaque("Aranhão", "", 2, 4);
        c.adicionarDrop("Couro", 1, 2, 40);
        return c;
    }

    public static Criatura criarUrso() {
        Criatura c = new Criatura("Urso", 3, 35, 7, 0);
        c.setBonusAcerto(1);
        c.setTestePresenca(5);
        c.setXpGanho(50);
        c.adicionarAtaque("Mordida", "", 1, 10);
        c.adicionarAtaque("Aranhão", "", 2, 8);
        c.adicionarDrop("Couro", 2, 4, 60);
        c.adicionarDrop("Dente de Urso", 1, 1, 20);
        return c;
    }

    public static Criatura criarBandido() {
        Criatura c = new Criatura("Bandido", 2, 9, 12, 1);
        c.setBonusAcerto(2);
        c.setTestePresenca(15);
        c.setXpGanho(10);
        c.adicionarAtaque("Facada", "", 1, 4);
        c.adicionarAtaque("Soco", "", 1, 3);
        c.setOuroDrop(4, 17, 100);
        c.adicionarDrop("Faca", 1, 1, 35);
        return c;
    }

    public static Criatura criarFada() {
        Criatura c = new Criatura("Fada", 1, 4, 14, 0);
        c.setAcertoAutomatico(true);
        c.setTestePresenca(18);
        c.setChanceAparecer(20);
        c.setXpGanho(30);
        c.adicionarAtaque("Brilho Cintilante", "luz", 1, 6);
        c.adicionarDrop("Pó da Fada", 1, 1, 100);
        return c;
    }

    // ==================== CRIATURAS DO LABIRINTO ====================

    // Esqueleto: arqueiro veloz. O Ataque de Ossos (1d4) pode se repetir: 80% de um
    // segundo ataque e, se repetir, 33% de um terceiro — cada um é um ataque novo.
    public static Criatura criarEsqueleto() {
        Criatura c = new Criatura("Esqueleto", 2, 12, 12, 4);
        c.setBonusAcerto(2);
        c.setDcFuga(15);
        c.setXpGanho(40);
        c.adicionarAtaque("Arco", "", 1, 6); // mesmo dano do Arco do jogo (1d6)
        c.adicionarAtaque("Ataque de Ossos", "", 1, 4);
        c.configurarAtaqueEncadeado("Ataque de Ossos", 80, 33);
        c.adicionarDrop("Osso", 1, 3, 30);
        c.adicionarDrop("Arco", 1, 1, 10);
        c.adicionarDrop("Flechas", 1, 7, 35);
        return c;
    }

    // Zumbi: resistente e lento. A Mordida (1d6) tem 30% de chance de infectar,
    // causando 1d4 de dano por rodada enquanto o combate durar.
    public static Criatura criarZumbi() {
        Criatura c = new Criatura("Zumbi", 2, 18, 10, 2);
        c.setBonusAcerto(3);
        c.setDcFuga(10);
        c.setXpGanho(40);
        c.adicionarAtaque("Mordida", "", 1, 6);
        c.configurarInfeccao("Mordida", 30);
        c.adicionarDrop("Carne Podre", 1, 4, 40);
        return c;
    }

    // Baú Monstruoso: armadilha viva dentro dos baús do labirinto.
    public static Criatura criarBauMonstruoso() {
        Criatura c = new Criatura("Baú Monstruoso", 3, 25, 10, 4);
        c.setBonusAcerto(4);
        c.setDcFuga(12);
        c.setXpGanho(50);
        c.adicionarAtaque("Mordida", "", 1, 8);
        c.setOuroDrop(4, 17, 100);
        return c;
    }
}

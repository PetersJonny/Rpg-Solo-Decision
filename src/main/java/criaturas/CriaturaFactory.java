package criaturas;

import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;

public class CriaturaFactory {

    public static List<Criatura> criarGrupoMonstros(int tipo, boolean deNoite) {
        List<Criatura> grupo = new ArrayList<>();
        int quantidade;

        switch (tipo) {
            case 1: {                 quantidade = deNoite ? MecanicasRpg.rolarEntre(1, 4) : MecanicasRpg.rolarEntre(1, 2);
                for (int i = 0; i < quantidade; i++) {
                    grupo.add(criarLobo());
                }
                break;
            }
            case 2: {                 grupo.add(criarUrso());
                break;
            }
            default: {                 quantidade = deNoite ? MecanicasRpg.rolarEntre(1, 5) : MecanicasRpg.rolarEntre(1, 3);
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
        c.adicionarDrop("Carne de Lobo", 1, 2, 40);
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
        c.adicionarDrop("Carne de Urso", 1, 2, 40);
        return c;
    }

                public static Criatura criarGoblin() {
        Criatura c = new Criatura("Goblin", 3, 15, 12, 3);
        c.setBonusAcerto(2);
        c.setTestePresenca(2);
        c.setXpGanho(70);
        c.adicionarAtaque("Facada", "", 1, 4);
        c.adicionarAtaque("Soco", "", 1, 3);
        c.configurarToqueDeMidas();
        c.setOuroDrop(12, 20, 100);
        c.adicionarDrop("Faca", 1, 1, 30);
        return c;
    }

    public static Criatura criarGoblinTransformado() {
        Criatura c = new Criatura("Goblin Transformado", 5, 90, 14, 6);
        c.setBonusAcerto(4);
        c.setBonusDano(5);
        c.setTestePresenca(12);
        c.setXpGanho(200);
        c.adicionarAtaque("Porretada", "", 1, 8);
        c.setSemFuga(true);
        c.setOuroDrop(12, 26, 100);
        c.adicionarDrop("Porrete", 1, 1, 50);
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

    public static List<Criatura> criarBandoDaNeta() {
        List<Criatura> grupo = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            grupo.add(criarBandidoAcampamento());
        }
        return grupo;
    }

    public static Criatura criarBandidoAcampamento() {
        Criatura c = new Criatura("Bandido", 4, 16, 11, 2);
        c.setBonusAcerto(2);
        c.setTestePresenca(15);
        c.setDesertaEmGrupo(true);
        c.setXpGanho(30);
        c.adicionarAtaque("Facada", "", 1, 4);
        c.adicionarAtaque("Soco", "", 1, 3);
        c.setOuroDrop(4, 17, 100);
        c.adicionarDrop("Faca", 1, 1, 35);
        return c;
    }

    public static Criatura criarMagoMacabro() {
        Criatura c = new Criatura("Mago Macabro", 5, 80, 14, 4);
        c.setTestePresenca(12);
        c.setXpGanho(100);
        c.setSemFuga(true);
        c.adicionarAtaque("Cutelo", "corte", 2, 8);
        c.configurarSangramento("Cutelo", 30);
        c.adicionarAtaque("Soco", "", 1, 6);
        c.configurarMagiaCura("Sede de Carne e Planta", 20, 3, 4, 5);
        c.adicionarDrop("Cutelo", 1, 1, 15);
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

            public static Criatura criarEsqueleto() {
        Criatura c = new Criatura("Esqueleto", 2, 12, 12, 4);
        c.setBonusAcerto(2);
        c.setDcFuga(15);
        c.setMortoVivo(true);
        c.setXpGanho(40);
        c.adicionarAtaque("Arco", "", 1, 6);         c.adicionarAtaque("Ataque de Ossos", "", 1, 4);
        c.configurarAtaqueEncadeado("Ataque de Ossos", 80, 33);
        c.adicionarDrop("Osso", 1, 3, 30);
        c.adicionarDrop("Arco", 1, 1, 10);
        c.adicionarDrop("Flechas", 1, 7, 35);
        return c;
    }

            public static Criatura criarZumbi() {
        Criatura c = new Criatura("Zumbi", 2, 18, 10, 2);
        c.setBonusAcerto(3);
        c.setDcFuga(10);
        c.setMortoVivo(true);
        c.setXpGanho(40);
        c.adicionarAtaque("Mordida", "", 1, 6);
        c.configurarInfeccao("Mordida", 30);
        c.adicionarDrop("Carne Podre", 1, 4, 40);
        return c;
    }

        public static Criatura criarBauMonstruoso() {
        Criatura c = new Criatura("Baú Monstruoso", 3, 25, 10, 4);
        c.setBonusAcerto(4);
        c.setDcFuga(12);
        c.setXpGanho(50);
        c.adicionarAtaque("Mordida", "", 1, 8);
        c.setOuroDrop(4, 17, 100);
        return c;
    }

                        public static Criatura criarMinotauro() {
        Criatura c = new Criatura("Minotauro", 5, 150, 15, 5);
        c.setBonusAcerto(4);
        c.setDcFuga(25);
        c.setSemFuga(true);
        c.setDropDeClasse(true);
        c.setMortoVivo(true);
        c.setXpGanho(500);
        c.adicionarAtaque("Garras", "corte", 2, 6);
        c.adicionarAtaque("Chifre", "perfurante", 1, 12);
        c.configurarInvestida(20, 2, 10, 25);
        c.adicionarDrop("Chifre de Minotauro", 1, 2, 50);
        return c;
    }
}

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
        c.adicionarDrop("Brilho Mágico", 1, 1, 100);
        return c;
    }
}

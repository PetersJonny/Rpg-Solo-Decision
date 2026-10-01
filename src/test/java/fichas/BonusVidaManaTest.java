package fichas;

import loja.Vendedor;
import mecanicas.GerenciadorDeHabilidades;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BonusVidaManaTest {

    @Test
    void amuletoSomeAoSairDoInventario() {
        FichaRpg ficha = new FichaRpg("T");
        int base = ficha.getVidaMaxima();

        ficha.adicionarItem(Vendedor.criarItem("Amuleto do Coração"));
        assertEquals(base + 10, ficha.getVidaMaxima());

        ficha.removerItem("Amuleto do Coração", 1);
        assertEquals(base, ficha.getVidaMaxima(), "o bônus não pode sobrar");
        assertEquals(ficha.getVidaMaxima(), ficha.getVidaMaximaBase());
    }

    @Test
    void gemaSomeAoSairDoInventario() {
        FichaRpg ficha = new FichaRpg("T");
        int base = ficha.getManaMaxima();

        ficha.adicionarItem(Vendedor.criarItem("Gema de Mana"));
        assertEquals(base + 10, ficha.getManaMaxima());

        ficha.removerItem("Gema de Mana", 1);
        assertEquals(base, ficha.getManaMaxima());
    }

    @Test
    void curaAbsolutaNaoCongelaOBonusNaVidaBase() {
        FichaRpg ficha = new FichaRpg("T");
        ficha.adicionarItem(Vendedor.criarItem("Amuleto do Coração"));
        int baseAntes = ficha.getVidaMaximaBase();

        GerenciadorDeHabilidades.executarCuraAbsoluta(ficha, new habilidades.ativas.HabilidadeCuraAbsoluta("Cura Absoluta", "x", 0));

        assertEquals(baseAntes, ficha.getVidaMaximaBase(),
                "a Cura Absoluta não pode gravar o máximo efetivo na base");
    }

    @Test
    void vidaNuncaPassaDoMaximoComBonsus() {
        FichaRpg ficha = new FichaRpg("T");
        ficha.adicionarItem(Vendedor.criarItem("Amuleto do Coração"));
        ficha.setVidaPersonagem(500);

        assertEquals(ficha.getVidaMaxima(), ficha.getVidaPersonagem());
    }

    @Test
    void dormirUsaOVidaMaximaComBonus() {
        FichaRpg ficha = new FichaRpg("T");
        ficha.adicionarItem(Vendedor.criarItem("Amuleto do Coração"));
        ficha.setVidaMaxima(60);
        ficha.setVidaPersonagem(1);
        ficha.temCabana = true;
        ficha.naCabana = true;
        ficha.ehNoite = true;
        ficha.comeuHoje = true;

        int vidaAntes = ficha.getVidaPersonagem();
        boolean dormiu = ficha.dormir();

        assertTrue(dormiu, "deveria dormir");
        int esperado = Math.min(1 + (70 / 2), 70);
        assertEquals(esperado, ficha.getVidaPersonagem(),
                "a cura do sono deve usar a vida máxima com o bônus do amuleto");
        assertTrue(ficha.getVidaPersonagem() > vidaAntes);
    }
}
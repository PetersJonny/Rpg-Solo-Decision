package fichas;

import itens.ItemRpg;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MesaMagiasTest {

    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Tester");
        ficha.setNomePersonagem("Mago");
    }

    @Test
    void testNaoConstroiSemMateriais() {
        assertFalse(ficha.construirMesaMagias());
        assertFalse(ficha.isTemMesaMagias());
    }

    @Test
    void testNaoConstroiFaltandoPóDaFada() {
        ficha.adicionarItem(new ItemRpg("Madeira", "", 5));
        ficha.adicionarItem(new ItemRpg("Folha", "", 4));
        ficha.adicionarItem(new ItemRpg("Pedra", "", 4));

        assertFalse(ficha.construirMesaMagias());
        assertFalse(ficha.isTemMesaMagias());
    }

    @Test
    void testConstruirMesaMagiasDescontaMateriais() {
        ficha.adicionarItem(new ItemRpg("Madeira", "", 5));
        ficha.adicionarItem(new ItemRpg("Folha", "", 4));
        ficha.adicionarItem(new ItemRpg("Pedra", "", 4));
        ficha.adicionarItem(new ItemRpg("Pó da Fada", "Pó de luz condensada deixado por uma fada.", 1));

        assertTrue(ficha.construirMesaMagias());
        assertTrue(ficha.isTemMesaMagias());
        assertEquals(0, ficha.getQuantidadeDe("Madeira"));
        assertEquals(0, ficha.getQuantidadeDe("Folha"));
        assertEquals(0, ficha.getQuantidadeDe("Pedra"));
        assertEquals(0, ficha.getQuantidadeDe("Pó da Fada"));

        // Não constrói duas vezes
        assertFalse(ficha.construirMesaMagias());
    }

    @Test
    void testEstudarMagiaAtivaBonus() {
        ficha.estudarMagia();
        assertTrue(ficha.isMagiaBonusAtivo());
        assertEquals(2, ficha.getMagiaBonusPeriodosRestantes());
    }

    @Test
    void testBonusDaMesaExpiraDepoisDe2Periodos() {
        ficha.estudarMagia();
        assertEquals(2, ficha.getMagiaBonusPeriodosRestantes());

        // 3 unidades viram o período: Dia 1 -> Noite 1
        ficha.avancarTempo(3);
        assertTrue(ficha.isMagiaBonusAtivo());
        assertEquals(1, ficha.getMagiaBonusPeriodosRestantes());

        // 3 unidades viram o período: Noite 1 -> Dia 2 (bônus acaba)
        ficha.avancarTempo(3);
        assertFalse(ficha.isMagiaBonusAtivo());
        assertEquals(0, ficha.getMagiaBonusPeriodosRestantes());

        // Pode estudar de novo
        ficha.estudarMagia();
        assertTrue(ficha.isMagiaBonusAtivo());
    }
}
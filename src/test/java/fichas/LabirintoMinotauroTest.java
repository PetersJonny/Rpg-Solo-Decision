package fichas;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LabirintoMinotauroTest {

    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Tester");
        ficha.setNomePersonagem("Explorador");
    }

    @Test
    void testLabirintoComeçaNaoEncontrado() {
        assertFalse(ficha.isLabirintoEncontrado());
    }

    @Test
    void testChanceComeçaEm1Porcento() {
        // Dia 1: 1% de chance por exploração
        assertEquals(1, ficha.getLabirintoChanceDescoberta());
    }

    @Test
    void testChanceAumenta1PorcentoACadaDia() {
        // (Dia 1 -> Noite 1: 3 unidades) + (Noite 1 -> Dia 2: 3 unidades)
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        assertEquals(2, ficha.getDiaAtual());
        assertEquals(2, ficha.getLabirintoChanceDescoberta());

        // Mais um dia completo: Dia 3
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        assertEquals(3, ficha.getDiaAtual());
        assertEquals(3, ficha.getLabirintoChanceDescoberta());
    }

    @Test
    void testChanceNaoPassaDe100Porcento() {
        for (int i = 0; i < 300; i++) {
            ficha.avancarTempo(3);
        }
        assertTrue(ficha.getDiaAtual() >= 100);
        assertEquals(100, ficha.getLabirintoChanceDescoberta());
    }

    @Test
    void testUmaVezEncontradoParaDeSortear() {
        ficha.setLabirintoEncontrado(true);
        assertTrue(ficha.isLabirintoEncontrado());
        // Mesmo com o dia avançando, permanece encontrado
        ficha.avancarTempo(6);
        assertTrue(ficha.isLabirintoEncontrado());
    }

    @Test
    void testJaEncontradoNaoRolaDescoberta() {
        ficha.setLabirintoEncontrado(true);
        // O guard impede o sorteio: não encontra de novo
        assertFalse(estruturas.LabirintoDoMinotauro.tentarDescoberta(ficha));
    }
}
package companheiros;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import fichas.FichaRpg;

class CompanheiroCoerenciaTest {

    private FichaRpg ficha;
    private Companheiro comp;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Teste");
        comp = new Companheiro();
        ficha.setCompanheiro(comp);
    }

    @Test
    void testRemocaoSegura() {
        ficha.removerCompanheiro();
        assertNull(ficha.getCompanheiro());
        
        // Chamada repetida não deve crashar
        ficha.removerCompanheiro();
        assertNull(ficha.getCompanheiro());
    }

    @Test
    void testDiasParaPartirNaoFicaNegativo() {
        // companheiro.aoDormir() não deve dar problemas de limites
        for (int i=0; i<10; i++) {
            comp.aoDormir();
        }
        
        // Ele vai embora após dormir 5 vezes (se a lógica estiver correta)
        assertTrue(comp.isPartindo(), "Após dormir muitas vezes, ele deve estar querendo partir");
    }
}

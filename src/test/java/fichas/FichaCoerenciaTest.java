package fichas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import classes.Guerreiro;
import classes.Healer;

class FichaCoerenciaTest {

    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Teste");
        ficha.setNomePersonagem("Hero");
    }

    @Test
    void testAdicionarOuroNegativo() {
        ficha.adicionarOuro(100);
        ficha.adicionarOuro(-50);
        // O método adicionarOuro(Math.max(0, quantidade)) ignora negativos
        assertEquals(100, ficha.getOuro(), "Adicionar ouro negativo não deve subtrair ouro");
    }

    @Test
    void testGastarOuro() {
        ficha.adicionarOuro(50);
        
        boolean resultado = ficha.gastarOuro(60);
        assertFalse(resultado, "Não pode gastar mais ouro do que possui");
        assertEquals(50, ficha.getOuro());

        boolean resultadoNegativo = ficha.gastarOuro(-10);
        assertFalse(resultadoNegativo, "Não deve ser possível gastar um valor negativo para ganhar ouro");
        assertEquals(50, ficha.getOuro());
    }

    @Test
    void testDanoENegativos() {
        ficha.adicionarAtributo(1, 10);
        ficha.setClasse(new Guerreiro());
        
        ficha.receberDano(1000);
        assertEquals(0, ficha.getVidaPersonagem(), "A vida não pode ficar negativa");

        ficha.setVidaPersonagem(-10);
        assertEquals(0, ficha.getVidaPersonagem(), "setVidaPersonagem não deve aceitar valores que fujam da lógica, embora o método atual permita?");
        // Se falhar, achamos um erro!
    }

    @Test
    void testCuraAcimaDoMaximo() {
        ficha.adicionarAtributo(1, 10);
        ficha.setClasse(new Healer());
        
        int maxVida = ficha.getVidaMaxima();
        ficha.setVidaPersonagem(maxVida - 5);
        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + 10);
        
        assertEquals(maxVida, ficha.getVidaPersonagem(), "A vida não pode ultrapassar o máximo");
    }
}

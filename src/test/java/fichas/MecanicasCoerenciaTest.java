package fichas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class MecanicasCoerenciaTest {

    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Teste");
    }

    @Test
    void testAdicionarXPNegativo() {
        ficha.adicionarXp(100);
        ficha.adicionarXp(-50);
        // O método deve ignorar XP negativo com Math.max(0, quantidade)
        assertEquals(0, ficha.getXp(), "Ganhar XP negativo não deve remover XP da ficha (era 100, mas 100 sobe de nivel, então xp vai pra 0. Se xp foi pra 0, adicionar -50 deve manter 0 e não ficar negativo)");
        
        // Vamos testar sem subir de nível
        FichaRpg ficha2 = new FichaRpg("Teste2");
        ficha2.adicionarXp(50);
        ficha2.adicionarXp(-20);
        assertEquals(50, ficha2.getXp(), "Ganhar XP negativo não deve subtrair");
    }

    @Test
    void testVidaMaximaNaoDeveSerZeradaNaCriacao() {
        // Se constituição for negativa, a vida não deve ficar menor que 1.
        // O sistema de criação de personagem do jogo só permite no mínimo 0 em atributos base
        ficha.resetarPontosBase();
        ficha.adicionarAtributo(1, 0); 
        ficha.setClasse(new classes.Guerreiro()); 
        
        assertTrue(ficha.getVidaMaxima() > 0, "Personagem não pode começar morto/vida <= 0");
    }
}

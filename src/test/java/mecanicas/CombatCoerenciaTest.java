package mecanicas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import fichas.FichaRpg;
import classes.Healer;
import criaturas.Criatura;

class CombatCoerenciaTest {

    private FichaRpg ficha;
    private Criatura inimigo;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Tester");
        ficha.adicionarAtributo(1, 2);
        ficha.setClasse(new Healer()); // Healer tem Cura Absoluta e coisas complexas

        inimigo = new Criatura("Lobo", 20, 10, 4, 1);
    }

    @Test
    void testCuraAbsolutaProtegeDano() {
        // A Cura Absoluta do Healer gasta vida extra primeiro
        ficha.setCuraAbsolutaBonus(30);
        ficha.setCuraAbsolutaVidaOriginalMax(ficha.getVidaMaxima());
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        
        // Recebe dano letal (maior que a vida maxima mas menor que escudo)
        ficha.receberDano(25);
        
        assertEquals(5, ficha.getCuraAbsolutaBonus(), "Dano deve ser abatido do bonus primeiro");
        assertEquals(ficha.getVidaMaxima(), ficha.getVidaPersonagem(), "A vida real não deve ser tocada");
        
        // Recebe dano que quebra a proteção
        ficha.receberDano(10);
        assertEquals(0, ficha.getCuraAbsolutaBonus(), "A proteção deve acabar");
        assertEquals(ficha.getVidaMaxima() - 5, ficha.getVidaPersonagem(), "O restante do dano vai para a vida real");
    }

    @Test
    void testReceberDanoNegativo() {
        // Personagem sendo curado com receberDano(negativo)?
        int vidaAnterior = ficha.getVidaPersonagem();
        ficha.receberDano(-10);
        
        // Se não tratado, vida iria subir?
        assertEquals(vidaAnterior, ficha.getVidaPersonagem(), "Receber dano negativo não deve curar o personagem");
    }

    @Test
    void testModificadoresEmCriatura() {
        // Dano base do lobo é 1d4 + (Força? O lobo tem isso?)
        // Testar se as rolagens não ficam < 1
        int resultadoIniciativa = MecanicasRpg.rolarDado(20);
        assertTrue(resultadoIniciativa >= 1 && resultadoIniciativa <= 20, "O dado puro nunca deve ser <= 0 ou maior que lados");
    }
}

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

    @Test
    void inimigosFugidosSaoRemovidosDaContagem() {
        Criatura lobo1 = new Criatura("Lobo", 20, 10, 4, 1);
        Criatura lobo2 = new Criatura("Lobo", 20, 10, 4, 1);
        Criatura lobo3 = new Criatura("Lobo", 20, 10, 4, 1);
        java.util.List<Criatura> inimigos = java.util.List.of(lobo1, lobo2, lobo3);

        // Todos vivos e sem fugir
        assertEquals(3, MotorDeCombate.inimigosVivos(inimigos).size());

        // Um morto e um fugido: só o restante vale
        lobo2.setVida(0);
        lobo3.setFugiu(true);
        java.util.List<Criatura> vivos = MotorDeCombate.inimigosVivos(inimigos);
        assertEquals(1, vivos.size());
        assertEquals(lobo1, vivos.get(0));

        // escolherAlvo também ignora fugidos
        assertEquals(0, MotorDeCombate.escolherAlvo(inimigos), "Só o lobo1 está combatendo");
    }

    @Test
    void aplicarDanoCriaturaAplicaBonusDemonicoAEnfraquecido() {
        Criatura alvo = new Criatura("Lobo", 1, 30, 10, 4);
        alvo.setEnfraquecido(true);
        MotorDeCombate.aplicarDanoCriatura(alvo, 10);
        assertEquals(15, alvo.getVida(), "10 + 5 de dano demoníaco");
    }

    @Test
    void bossSemFugaBloqueiaAFugaDoCombate() {
        Criatura minotauro = criaturas.CriaturaFactory.criarMinotauro();
        assertTrue(MotorDeCombate.temBossSemFuga(java.util.List.of(minotauro)), "Minotauro vivo bloqueia a fuga");

        // Morto: deixa de bloquear
        minotauro.setVida(0);
        assertFalse(MotorDeCombate.temBossSemFuga(java.util.List.of(minotauro)));

        // Criatura comum não bloqueia
        Criatura lobo = new Criatura("Lobo", 20, 10, 4, 1);
        assertFalse(MotorDeCombate.temBossSemFuga(java.util.List.of(lobo)));
    }
}

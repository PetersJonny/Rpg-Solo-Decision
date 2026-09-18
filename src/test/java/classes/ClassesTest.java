package classes;

import org.junit.jupiter.api.Test;
import fichas.FichaRpg;

import static org.junit.jupiter.api.Assertions.*;

class ClassesTest {

    @Test
    void testGuerreiroAtributosEHabilidades() {
        Guerreiro guerreiro = new Guerreiro();
        
        assertEquals(2, guerreiro.getBonusConstituicao());
        assertEquals(1, guerreiro.getBonusForca());
        
        // Verifica itens iniciais
        assertEquals(2, guerreiro.getItensIniciais().size());
        assertEquals("Espada", guerreiro.getArmaPrincipal().getNome());
        
        // Verifica habilidades
        assertEquals(1, guerreiro.getHabilidadesIniciais().size());
        assertEquals("Casca Grossa", guerreiro.getHabilidadesIniciais().get(0).getNome());
    }

    @Test
    void testMagoAtributosEHabilidades() {
        Mago magoFogo = new Mago("Fogo");
        
        assertEquals(1, magoFogo.getBonusIntelecto());
        assertEquals(2, magoFogo.getBonusPresenca());
        assertEquals(0, magoFogo.getBonusConstituicao());
        assertEquals(-2, magoFogo.getBonusForca());
        
        // Magias mudam pelo elemento
        assertEquals("Bola Elementar (Fogo)", magoFogo.getHabilidadesIniciais().get(0).getNome());
        
        Mago magoGelo = new Mago("Gelo");
        assertEquals("Bola Elementar (Gelo)", magoGelo.getHabilidadesIniciais().get(0).getNome());
    }

    @Test
    void testHealerAtributosEHabilidades() {
        Healer healer = new Healer();
        
        assertEquals(1, healer.getBonusSabedoria());
        assertEquals(2, healer.getBonusIntelecto());
        
        assertEquals(1, healer.getHabilidadesIniciais().size());
        assertEquals("Conhecimento Avançado", healer.getHabilidadesIniciais().get(0).getNome());
    }

    @Test
    void testAplicarBonusNivel() {
        FichaRpg ficha = new FichaRpg("Teste");
        ficha.adicionarAtributo(1, 2); // 2 Const
        Guerreiro guerreiro = new Guerreiro();
        ficha.setClasse(guerreiro);
        
        int vidaNivel1 = ficha.getVidaMaxima();
        
        // Nivel 2
        guerreiro.aplicarBonusNivel(ficha);
        
        int vidaNivel2 = ficha.getVidaMaxima();
        
        assertTrue(vidaNivel2 > vidaNivel1);
    }
}

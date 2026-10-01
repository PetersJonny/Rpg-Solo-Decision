package itens;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PesoDosItensTest {

    private double peso(String nome) throws Exception {
        Method m = ItemRpg.class.getDeclaredMethod("pesoPadraoDoNome", String.class);
        m.setAccessible(true);
        return (double) m.invoke(null, nome);
    }

    @Test
    void consumiveisPesamDoisDecimos() throws Exception {
        assertEquals(0.2, peso("Fruta do Diabo"), 0.0001);
        assertEquals(0.2, peso("Gota de Veneno"), 0.0001);
    }

    @Test
    void roupasPesamSeisDecimos() throws Exception {
        assertEquals(0.6, peso("Botas de Correio"), 0.0001);
        assertEquals(0.6, peso("Capa do Viajante"), 0.0001);
        assertEquals(0.6, peso("Lenço de Seda"), 0.0001);
        assertEquals(0.6, peso("Manto do Atirador"), 0.0001);
        assertEquals(0.6, peso("Túnica de Aventureiro"), 0.0001);
    }

    @Test
    void itensMagicosPesamMeioQuilo() throws Exception {
        assertEquals(0.5, peso("Chapéu Mágico"), 0.0001);
        assertEquals(0.5, peso("Pequeno Grimório"), 0.0001);
        assertEquals(0.5, peso("Manto do Astrólogo"), 0.0001);
        assertEquals(0.5, peso("Gema de Mana"), 0.0001);
        assertEquals(0.5, peso("Amuleto do Coração"), 0.0001);
        assertEquals(0.5, peso("Ampulheta de Prata"), 0.0001);
        assertEquals(0.5, peso("Luvas de Prata"), 0.0001);
        assertEquals(0.5, peso("Pó de Midas"), 0.0001);
        assertEquals(0.5, peso("Tocha"), 0.0001);
    }

    @Test
    void pesosAntigosNaoMudaram() throws Exception {
        assertEquals(0.1, peso("Couro"), 0.0001);
        assertEquals(0.1, peso("Madeira"), 0.0001);
        assertEquals(0.1, peso("Frutas"), 0.0001);
        assertEquals(0.3, peso("Poção de Mana"), 0.0001);
        assertEquals(0.3, peso("Sopa do Vilarejo"), 0.0001);
        assertEquals(0.6, peso("Poção Grande de Mana"), 0.0001);
        assertEquals(2.0, peso("Espada Pesada"), 0.0001);
        assertEquals(2.0, peso("Armadura Pesada"), 0.0001);
    }

    @Test
    void pesoDoItemCriadoEONomeado() {
        assertEquals(0.2, new ItemRpg("Gota de Veneno", "x", 1).getPeso(), 0.0001);
        assertEquals(0.6, new ItemRpg("Capa do Viajante", "x", 1).getPeso(), 0.0001);
        assertEquals(0.5, new ItemRpg("Amuleto do Coração", "x", 1).getPeso(), 0.0001);
    }
}
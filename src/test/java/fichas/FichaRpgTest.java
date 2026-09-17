package fichas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import classes.Guerreiro;
import itens.Consumivel;
import itens.Arma;
import itens.Armadura;

import static org.junit.jupiter.api.Assertions.*;

class FichaRpgTest {
    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Marcus");
        ficha.setNomePersonagem("Herói");
    }

    @Test
    void testCriacaoInicial() {
        assertEquals("Herói", ficha.getNomePersonagem());
        assertEquals("Marcus", ficha.getNomePessoa());
        assertEquals(1, ficha.getNivel());
        assertEquals(0, ficha.getXp());
        assertEquals(0, ficha.getOuro());
        assertFalse(ficha.isFichaCompleta());
    }

    @Test
    void testAdicionarAtributos() {
        ficha.adicionarAtributo(1, 2); // 2 de Constituição
        ficha.adicionarAtributo(3, 4); // 4 de Força
        
        ficha.aplicarBonus(); // Precisa aplicar para ver na Ficha base
        
        assertEquals(2, ficha.getConstituicao());
        assertEquals(4, ficha.getForca());
    }

    @Test
    void testCompletarFichaComClasse() {
        ficha.adicionarAtributo(1, 2);
        ficha.adicionarAtributo(2, 2);
        ficha.adicionarAtributo(3, 2);
        ficha.setClasse(new Guerreiro());

        assertTrue(ficha.isFichaCompleta());
        assertNotNull(ficha.getArmaEquipada());
        assertEquals(1, ficha.getInventario().size()); // Guerreiro começa com alguns itens
    }

    @Test
    void testAumentarXpESubirNivel() {
        ficha.adicionarAtributo(1, 3);
        ficha.setClasse(new Guerreiro());

        int niveisGanhos = ficha.adicionarXp(150); // XP para Nivel 2 é 100
        
        assertEquals(1, niveisGanhos);
        assertEquals(2, ficha.getNivel());
        assertEquals(50, ficha.getXp());
    }

    @Test
    void testAdicionarRemoverItens() {
        Consumivel pocao = new Consumivel("Poção de Vida", "Cura 20", 1);
        pocao.setQuantidade(1);
        
        ficha.adicionarItem(pocao);
        assertTrue(ficha.temItem("Poção de Vida"));
        assertEquals(1, ficha.getQuantidadeDe("Poção de Vida"));
        
        // Adiciona mais uma
        Consumivel pocao2 = new Consumivel("Poção de Vida", "Cura 20", 2);
        pocao2.setQuantidade(2);
        ficha.adicionarItem(pocao2);
        
        assertEquals(3, ficha.getQuantidadeDe("Poção de Vida"));
        
        // Remove
        boolean removed = ficha.removerItem("Poção de Vida", 2);
        assertTrue(removed);
        assertEquals(1, ficha.getQuantidadeDe("Poção de Vida"));
    }

    @Test
    void testEquiparMelhorArmadura() {
        Armadura armaduraFraca = new Armadura("Armadura de Couro", "Defesa +2", 2, 1);
        armaduraFraca.setQuantidade(1);
        Armadura armaduraForte = new Armadura("Armadura de Ferro", "Defesa +5", 5, 1);
        armaduraForte.setQuantidade(1);

        ficha.adicionarItem(armaduraFraca);
        ficha.equiparMelhorArmadura();
        
        assertEquals("Armadura de Couro", ficha.getArmaduraEquipada().getNome());
        
        ficha.adicionarItem(armaduraForte);
        ficha.equiparMelhorArmadura();
        
        assertEquals("Armadura de Ferro", ficha.getArmaduraEquipada().getNome());
    }

    @Test
    void testAvancarTempoEDormir() {
        assertFalse(ficha.isEhNoite());
        assertEquals(1, ficha.getDiaAtual());

        ficha.avancarTempo(3); // 3 unidades vira para Noite 1
        
        assertTrue(ficha.isEhNoite());
        assertEquals(1, ficha.getDiaAtual());
        
        ficha.avancarTempo(3); // Mais 3 unidades vira para Dia 2
        
        assertFalse(ficha.isEhNoite());
        assertEquals(2, ficha.getDiaAtual());
        assertEquals(1, ficha.getDiasSemDormir());
        
        // Testa cabana e dormir
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Pedra", "", 5));
        
        assertTrue(ficha.montarCabana());
        
        ficha.avancarTempo(3); // Vai para Noite 2
        assertTrue(ficha.isEhNoite());
        
        // Agora dorme
        ficha.setVidaMaxima(100);
        ficha.setVidaPersonagem(10);
        ficha.setManaMaxima(50);
        ficha.setManaPersonagem(5);
        
        assertTrue(ficha.dormir());
        
        // Verifica se descansou e amanheceu
        assertFalse(ficha.isEhNoite());
        assertEquals(3, ficha.getDiaAtual());
        assertEquals(0, ficha.getDiasSemDormir());
        assertEquals(60, ficha.getVidaPersonagem()); // 10 + 50 (metade)
        assertEquals(30, ficha.getManaPersonagem()); // 5 + 25 (metade)
    }
}

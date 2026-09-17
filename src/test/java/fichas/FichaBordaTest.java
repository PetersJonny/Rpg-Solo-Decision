package fichas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import classes.Guerreiro;
import itens.Consumivel;
import itens.ItemRpg;

import static org.junit.jupiter.api.Assertions.*;

class FichaBordaTest {

    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Borda");
        ficha.setNomePersonagem("EdgeHero");
        ficha.adicionarAtributo(1, 1);
        ficha.setClasse(new Guerreiro());
    }

    @Test
    void testLevelMaximoNaoUltrapassa10() {
        // Tentar ganhar 1.000.000 de XP (suficiente para passar do 10, se não houvesse limite)
        int ganhos = ficha.adicionarXp(1000000);
        
        assertEquals(10, ficha.getNivel(), "O nível não deve ultrapassar 10");
        assertTrue(ganhos <= 9, "Não deve ter ganhado mais que 9 níveis (já estava no 1)");
    }

    @Test
    void testOverflowDeOuro() {
        ficha.adicionarOuro(Integer.MAX_VALUE);
        ficha.adicionarOuro(10);
        
        assertEquals(Integer.MAX_VALUE, ficha.getOuro(), "O ouro deve ser travado no máximo possível e não estourar para negativo");
    }
    
    @Test
    void testRemoverItemNaoExistente() {
        boolean removed = ficha.removerItem("Item Fantasma", 1);
        assertFalse(removed, "Tentar remover um item inexistente deve retornar false");
    }

    @Test
    void testRemoverQuantidadeMaiorQueEstoque() {
        ItemRpg erva = new ItemRpg("Erva", "Curativa", 5);
        ficha.adicionarItem(erva);
        
        boolean removed = ficha.removerItem("Erva", 10); // Tentando remover mais que possui
        assertTrue(removed, "Deve permitir e simplesmente zerar/remover o item, ou retornar false se não for para remover?");
        assertEquals(0, ficha.getQuantidadeDe("Erva"), "A quantidade do item deve ser 0");
    }

    @Test
    void testGastarManaMaiorQueAtual() {
        ficha.setManaMaxima(20);
        ficha.setManaPersonagem(5);
        
        // Se usar magia que custa 10, o sistema deveria impedir antes, mas vamos testar se setMana aceita negativo
        ficha.setManaPersonagem(ficha.getManaPersonagem() - 10);
        assertEquals(0, ficha.getManaPersonagem(), "Mana não deve ficar negativa caso setManaPersonagem receba valor negativo");
    }
}

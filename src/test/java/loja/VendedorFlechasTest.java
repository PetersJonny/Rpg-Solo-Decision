package loja;

import itens.ItemRpg;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VendedorFlechasTest {

    @Test
    void flechasTemCasoEmCriarItem() {
        ItemRpg item = Vendedor.criarItem("Flechas");

        assertNotNull(item, "criarItem(\"Flechas\") não pode devolver null (crash ao comprar)");
        assertEquals("Flechas", item.getNome());
    }

    @Test
    void flechasTemPrecoValido() {
        assertTrue(Vendedor.precoBase("Flechas") > 0, "precoBase não pode ser -1");
        assertTrue(Vendedor.precoDeCompraMelhorado("Flechas") > 0);
    }

    @Test
    void todoItemDoGeralTemCasoEmCriarItem() {
        String[] itens = {
                "Faca", "Machado", "Machadinha", "Martelo", "Mangual", "Arco", "Flechas", "Lança",
                "Poção de Mana", "Kit Médico",
                "Madeira", "Folha", "Pedra", "Frutas",
                "Espada", "Espada Pesada", "Machado de Guerra", "Martelo de Guerra", "Armadura Pesada",
                "Bisturi", "Arco Refinado", "Nunchako", "Foice",
                "Cajado", "Chapéu Mágico", "Poção Grande de Mana", "Pequeno Grimório",
                "Manto do Astrólogo", "Gema de Mana", "Amuleto do Coração", "Ampulheta de Prata",
                "Luvas de Prata", "Pó de Midas", "Gota de Veneno"
        };
        for (String nome : itens) {
            assertNotNull(Vendedor.criarItem(nome), nome + " está no GERAL mas criarItem devolve null");
        }
    }
}
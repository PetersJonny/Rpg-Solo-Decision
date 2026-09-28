package eventos;

import org.junit.jupiter.api.Test;

import fichas.FichaRpg;
import itens.Consumivel;
import loja.Vendedor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FerreiroTest {

    @SuppressWarnings("unchecked")
    private static List<String> estoqueDoDia(FichaRpg ficha) throws Exception {
        Method m = Ferreiro.class.getDeclaredMethod("estoqueDoDia", FichaRpg.class);
        m.setAccessible(true);
        try {
            return (List<String>) m.invoke(null, ficha);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private static void setDiaAtual(FichaRpg ficha, int dia) throws Exception {
        java.lang.reflect.Field f = FichaRpg.class.getDeclaredField("diaAtual");
        f.setAccessible(true);
        f.setInt(ficha, dia);
    }

    @Test
    void estoqueDoDiaTemCincoItensEDeterministico() throws Exception {
        FichaRpg ficha = new FichaRpg("T");
        setDiaAtual(ficha, 3);
        List<String> a = estoqueDoDia(ficha);
        List<String> b = estoqueDoDia(ficha);
        assertEquals(5, a.size());
        assertEquals(a, b, "mesmo dia deve gerar o mesmo estoque");
        setDiaAtual(ficha, 4);
        assertNotEquals(a, estoqueDoDia(ficha), "dias diferentes mudam o estoque");
    }

    @Test
    void estoqueSoTemArmasEArmaduras() throws Exception {
        FichaRpg ficha = new FichaRpg("T");
        for (int dia = 1; dia <= 40; dia++) {
            setDiaAtual(ficha, dia);
            for (String item : estoqueDoDia(ficha)) {
                assertTrue(Vendedor.precoBase(item) > 0, item + " deveria ter preço base");
            }
        }
    }

    @Test
    void pratosDaTavernaSaoCriadosPeloVendedor() {
        assertTrue(Vendedor.criarItem("Sopa do Vilarejo") instanceof Consumivel);
        assertTrue(Vendedor.criarItem("Hidromel") instanceof Consumivel);
        assertEquals(6, Vendedor.precoBase("Sopa do Vilarejo"));
        assertEquals(4, Vendedor.precoBase("Pão Quente com Manteiga"));
        assertEquals(7, Vendedor.precoBase("Ovos Mexidos"));
        assertEquals(9, Vendedor.precoBase("Caldo de Lobo"));
        assertEquals(9, Vendedor.precoBase("Peixe Assado"));
        assertEquals(13, Vendedor.precoBase("Estofado de Urso"));
        assertEquals(7, Vendedor.precoBase("Torta de Frutas"));
        assertEquals(8, Vendedor.precoBase("Hidromel"));
    }

    @Test
    void armadurasSeguemOsPrecosAprovados() {
        assertEquals(230, Vendedor.precoBase("Armadura Leve"));
        assertEquals(450, Vendedor.precoBase("Armadura Pesada"));
        assertEquals(110, Vendedor.precoBase("Espada Pesada"));
        assertEquals(50, Vendedor.precoBase("Cajado"));
    }
}
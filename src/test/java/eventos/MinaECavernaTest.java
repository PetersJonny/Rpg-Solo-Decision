package eventos;

import fichas.FichaRpg;
import itens.ItemRpg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinaECavernaTest {

    @BeforeEach
    void modoTeste() {
        telas.Interface.modoTeste = true;
    }

    @AfterEach
    void restauraModoTeste() {
        telas.Interface.modoTeste = false;
    }

    private FichaRpg fichaNova() {
        FichaRpg ficha = new FichaRpg("T");
        ficha.setCidadeAtual("Vilarejo de Scarbor");
        ficha.setDentroDaCaverna(false);
        ficha.setTochaNaMao(false);
        return ficha;
    }

    @Test
    void tochaPesaMeioQuilo() {
        ItemRpg tocha = new ItemRpg("Tocha", "tocha", 1);
        assertEquals(0.5, tocha.getPeso(), 0.0001, "a tocha deve pesar 0,5");
    }

    @Test
    void foraDaCavernaNaoTemPenalidadeDeEscuridao() {
        FichaRpg ficha = fichaNova();
        assertEquals(0, Caverna.penalidadeEscuridao(ficha));
    }

    @Test
    void dentroDaCavernaSemTochaPenalizaMenosDois() {
        FichaRpg ficha = fichaNova();
        ficha.setDentroDaCaverna(true);
        assertEquals(-2, Caverna.penalidadeEscuridao(ficha));
    }

    @Test
    void dentroDaCavernaComTochaNaMaoNaoPenaliza() {
        FichaRpg ficha = fichaNova();
        ficha.setDentroDaCaverna(true);
        ficha.setTochaNaMao(true);
        ficha.adicionarItem(new ItemRpg("Tocha", "tocha", 1));

        assertEquals(0, Caverna.penalidadeEscuridao(ficha),
                "com a tocha na mao a escuridao nao atrapalha");
    }

    @Test
    void tochaNaMaoSemTochaNoInventarioNaoAnulaAPenalidade() {
        FichaRpg ficha = fichaNova();
        ficha.setDentroDaCaverna(true);
        ficha.setTochaNaMao(true);

        assertEquals(-2, Caverna.penalidadeEscuridao(ficha),
                "sem a tocha na mochila o -2 volta");
    }

    @Test
    void fichaNovaNaoNasceMarcadaComoVista() {
        FichaRpg ficha = fichaNova();

        assertFalse(ficha.isGoblinVistoNaMina());
        assertFalse(ficha.isTochaVistaNaMina());
        assertFalse(ficha.isTochaNaMao());
        assertFalse(ficha.isDentroDaCaverna());
    }

    @Test
    void tochaConsomeMeioQuiloDaMochila() {
        FichaRpg ficha = fichaNova();
        double antes = ficha.getEspacoLivreMochila();

        ficha.adicionarItem(new ItemRpg("Tocha", "tocha", 1));

        assertEquals(0.5, antes - ficha.getEspacoLivreMochila(), 0.0001);
    }
}
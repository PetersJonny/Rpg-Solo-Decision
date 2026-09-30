package fichas;

import itens.ItemRpg;
import loja.Vendedor;
import mecanicas.GerenciadorDeItens;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SistemaDeCheioTest {

    private void comer(FichaRpg ficha, int refeicoes) {
        for (int i = 0; i < refeicoes; i++) {
            ficha.comerComidaBoa();
        }
    }

    @Test
    void comTresRefeicoesOCharFicaCheio() {
        FichaRpg ficha = new FichaRpg("T");

        assertFalse(ficha.isCheio());
        assertEquals(3, ficha.getRefeicoesRestantesHoje());

        comer(ficha, 2);
        assertFalse(ficha.isCheio());
        assertEquals(1, ficha.getRefeicoesRestantesHoje());

        comer(ficha, 1);
        assertTrue(ficha.isCheio());
        assertEquals(3, ficha.getRefeicoesHoje());
        assertEquals(0, ficha.getRefeicoesRestantesHoje());
    }

    @Test
    void naQuartaRefeicaoNaoComeMais() {
        FichaRpg ficha = new FichaRpg("T");
        comer(ficha, 3);

        ficha.comerComidaBoa();
        ficha.comerCarnePodre();
        ficha.comerFrutas(FichaRpg.FRUTAS_PARA_REFEICAO);

        assertEquals(3, ficha.getRefeicoesHoje(), "nenhuma refeição pode passar do limite");
        assertTrue(ficha.isCheio());
    }

    @Test
    void comidaEstragadaTambemContaComoRefeicao() {
        FichaRpg ficha = new FichaRpg("T");
        comer(ficha, 1);
        ficha.comerCarnePodre();

        assertEquals(2, ficha.getRefeicoesHoje());
        assertTrue(ficha.isEnjoado());

        comer(ficha, FichaRpg.REFEICOES_POR_DIA);
        ficha.comerCarnePodre();
        assertEquals(FichaRpg.REFEICOES_POR_DIA, ficha.getRefeicoesHoje());
    }

    @Test
    void saciedadeSomeAoVirarODia() {
        FichaRpg ficha = new FichaRpg("T");
        comer(ficha, 3);
        assertTrue(ficha.isCheio());

        ficha.registrarNovoDiaFome();

        assertFalse(ficha.isCheio());
        assertEquals(0, ficha.getRefeicoesHoje());
        assertEquals(0, ficha.getFrutasComidasHoje());
    }

    @Test
    void tresFrutasSaoUmaRefeicao() {
        FichaRpg ficha = new FichaRpg("T");

        ficha.comerFrutas(1);
        assertEquals(0, ficha.getRefeicoesHoje());
        ficha.comerFrutas(1);
        assertEquals(0, ficha.getRefeicoesHoje());
        ficha.comerFrutas(1);
        assertEquals(1, ficha.getRefeicoesHoje());
    }

    @Test
    void comidaNaoSendoConsumidaQuandoCheio() {
        FichaRpg ficha = new FichaRpg("T");
        comer(ficha, 3);
        ficha.setVidaMaxima(30);
        ficha.setVidaPersonagem(10);

        ItemRpg torta = Vendedor.criarItem("Torta de Frutas");
        ficha.adicionarItem(torta);

        assertFalse(GerenciadorDeItens.usarItemForaDeCombate(ficha, torta, 1));

        assertEquals(1, torta.getQuantidade(), "a comida não pode ser perdida");
        assertTrue(ficha.getInventario().contains(torta));
        assertEquals(10, ficha.getVidaPersonagem());
    }

    @Test
    void comidaAindaSobeQuandoHaEspaco() {
        FichaRpg ficha = new FichaRpg("T");
        comer(ficha, 2);
        ficha.setVidaMaxima(30);
        ficha.setVidaPersonagem(10);

        ItemRpg torta = Vendedor.criarItem("Torta de Frutas");
        ficha.adicionarItem(torta);

        assertTrue(GerenciadorDeItens.usarItemForaDeCombate(ficha, torta, 1));

        assertEquals(3, ficha.getRefeicoesHoje());
        assertTrue(ficha.isCheio());
        assertTrue(ficha.getVidaPersonagem() > 10);
    }

    @Test
    void hidromelNaoContaComoRefeicao() {
        FichaRpg ficha = new FichaRpg("T");
        ficha.setManaMaxima(20);
        ficha.setManaPersonagem(2);

        ItemRpg hidromel = Vendedor.criarItem("Hidromel");
        ficha.adicionarItem(hidromel);

        assertTrue(GerenciadorDeItens.usarItemForaDeCombate(ficha, hidromel, 1));
        assertEquals(0, ficha.getRefeicoesHoje());
        assertFalse(ficha.isCheio());
    }

    @Test
    void menuMostraQueEstaCheio() {
        FichaRpg ficha = new FichaRpg("T");
        assertEquals("", ficha.descreverFome());

        comer(ficha, 1);
        assertTrue(ficha.descreverFome().contains("1/3"));

        comer(ficha, 2);
        assertTrue(ficha.descreverFome().contains("CHEIO"));
        assertTrue(ficha.descreverFome().contains("3/3"));
    }
}
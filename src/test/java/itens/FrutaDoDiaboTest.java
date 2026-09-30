package itens;

import fichas.FichaRpg;
import loja.Vendedor;
import mecanicas.GerenciadorDeItens;
import fichas.GerenciadorDeVida;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrutaDoDiaboTest {

    private int frutaNaMochila(FichaRpg ficha) {
        ItemRpg fruta = Vendedor.criarItem("Fruta do Diabo");
        ficha.adicionarItem(fruta);
        return ficha.getInventario().indexOf(fruta);
    }

    @Test
    void frutaExisteComoConsumivelDeUmUso() {
        ItemRpg fruta = Vendedor.criarItem("Fruta do Diabo");
        assertInstanceOf(Consumivel.class, fruta);
        assertEquals(1, fruta.getQuantidade());
    }

    @Test
    void foraDeCombateAFrutaNaoPodeSerUsada() {
        FichaRpg f = new FichaRpg("T");
        int idx = frutaNaMochila(f);
        f.setVidaMaxima(40);
        f.setVidaPersonagem(3);

        assertFalse(GerenciadorDeItens.usarItemForaDeCombate(f, f.getInventario().get(idx), 1));

        assertEquals(3, f.getVidaPersonagem());
        assertEquals(1, f.getInventario().get(idx).getQuantidade());
        assertFalse(f.isFrutaDoDiaboAtiva());
    }

    @Test
    void consumidaEmCombateRestauraVidaEManaEAtivaOPoder() {
        FichaRpg f = new FichaRpg("T");
        f.setEmCombate(true);
        f.setVidaMaxima(40);
        f.setManaMaxima(25);
        f.setVidaPersonagem(5);
        f.setManaPersonagem(2);
        int idx = frutaNaMochila(f);

        GerenciadorDeItens.usarItemNaVez(f, idx);

        assertEquals(40, f.getVidaPersonagem());
        assertEquals(25, f.getManaPersonagem());
        assertTrue(f.isFrutaDoDiaboAtiva());
        assertTrue(f.isFrutaImuneNestaRodada());
        assertTrue(f.getInventario().stream().noneMatch(i -> i.getNome().equals("Fruta do Diabo")),
                "a fruta tem um uso só e deve sumir do inventario");
    }

    @Test
    void poderLevaADanoDeTodosOsDadosUmDegrau() {
        FichaRpg f = new FichaRpg("T");
        assertEquals(4, f.upgradeDadoFruta(4));
        assertEquals(6, f.upgradeDadoFruta(6));
        assertEquals(12, f.upgradeDadoFruta(12));

        f.setFrutaDoDiaboAtiva(true);
        assertEquals(6, f.upgradeDadoFruta(4));
        assertEquals(8, f.upgradeDadoFruta(6));
        assertEquals(10, f.upgradeDadoFruta(8));
        assertEquals(12, f.upgradeDadoFruta(10));
        assertEquals(12, f.upgradeDadoFruta(12));
    }

    @Test
    void semOFrutaODadoNaoMuda() {
        FichaRpg f = new FichaRpg("T");
        for (int d = 2; d <= 12; d += 2) {
            assertEquals(d, f.upgradeDadoFruta(d));
        }
    }

    @Test
    void dadoD12GanhaUmDadoExtraQueSobeDeEscada() {
        FichaRpg f = new FichaRpg("T");
        assertEquals(0, f.ladoDadoExtraFruta(1));

        f.setFrutaDoDiaboAtiva(true);
        assertEquals(4, f.ladoDadoExtraFruta(1));
        assertEquals(6, f.ladoDadoExtraFruta(2));
        assertEquals(8, f.ladoDadoExtraFruta(3));
        assertEquals(10, f.ladoDadoExtraFruta(4));
        assertEquals(12, f.ladoDadoExtraFruta(5));
    }

    @Test
    void nadaMaisENoFimDoCombate() {
        FichaRpg f = new FichaRpg("T");
        f.setFrutaDoDiaboAtiva(true);
        f.setFrutaImuneNestaRodada(true);
        f.setEmCombate(true);

        f.resetarEfeitosCombate();

        assertEquals(6, f.upgradeDadoFruta(6));
        assertEquals(0, f.ladoDadoExtraFruta(1));
    }

    @Test
    void nenhumaRondaDeCombateNaoAtingeOJogador() {
        FichaRpg f = new FichaRpg("T");
        f.setVidaMaxima(30);
        f.setVidaPersonagem(10);
        f.setFrutaImuneNestaRodada(true);

        GerenciadorDeVida.receberDano(f, 999);

        assertEquals(10, f.getVidaPersonagem());
    }

    @Test
    void semImunidadeODanoPassaNormalmente() {
        FichaRpg f = new FichaRpg("T");
        f.setVidaMaxima(30);
        f.setVidaPersonagem(10);
        f.setFrutaImuneNestaRodada(false);

        GerenciadorDeVida.receberDano(f, 4);

        assertEquals(6, f.getVidaPersonagem());
    }

    @Test
    void resetarEfeitosCombateEncerraOPoder() {
        FichaRpg f = new FichaRpg("T");
        f.setFrutaDoDiaboAtiva(true);
        f.setFrutaImuneNestaRodada(true);
        f.setEmCombate(true);

        f.resetarEfeitosCombate();

        assertFalse(f.isFrutaDoDiaboAtiva());
        assertFalse(f.isFrutaImuneNestaRodada());
        assertFalse(f.isEmCombate());
    }
}
package eventos;

import criaturas.Criatura;
import criaturas.CriaturaFactory;
import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;
import itens.ItemRpg;
import loja.Vendedor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MissaoNetaPerdidaTest {

    private static void setDiaAtual(FichaRpg ficha, int dia) throws Exception {
        java.lang.reflect.Field f = FichaRpg.class.getDeclaredField("diaAtual");
        f.setAccessible(true);
        f.setInt(ficha, dia);
    }

    private FichaRpg ficha() {
        FichaRpg f = new FichaRpg("Teste");
        f.setDiaAceitouNeta(3);
        return f;
    }

    @Test
    void prazoDeSeteDiasContaDesdeAceitacao() throws Exception {
        FichaRpg f = ficha();
        setDiaAtual(f, 3);
        assertFalse(f.isPrazoNetaEstourado());
        setDiaAtual(f, 9);
        assertFalse(f.isPrazoNetaEstourado());
        setDiaAtual(f, 10);
        assertTrue(f.isPrazoNetaEstourado());
    }

    @Test
    void semDiaDeAceitacaoNaoEstoura() throws Exception {
        FichaRpg f = new FichaRpg("Teste");
        setDiaAtual(f, 500);
        assertFalse(f.isPrazoNetaEstourado());
    }

    @Test
    void bandoTemSeteBandidosComOBuff() {
        var grupo = CriaturaFactory.criarBandoDaNeta();
        assertEquals(7, grupo.size());
        for (Criatura c : grupo) {
            assertEquals(4, c.getNivel());
            assertEquals(16, c.getVida());
            assertEquals(11, c.getDefesa());
            assertEquals(2, c.getIniciativa());
            assertEquals(30, c.getXpGanho());
            assertTrue(c.isDesertaEmGrupo());
        }
    }

    @Test
    void bandidoComumContinuaNaoBuffado() {
        Criatura c = CriaturaFactory.criarBandido();
        assertEquals(2, c.getNivel());
        assertEquals(9, c.getVida());
        assertEquals(12, c.getDefesa());
        assertEquals(10, c.getXpGanho());
        assertFalse(c.isDesertaEmGrupo());
    }

    @Test
    void magoMacabroTemOsAtributosDoChefe() {
        Criatura m = CriaturaFactory.criarMagoMacabro();
        assertEquals(5, m.getNivel());
        assertEquals(80, m.getVida());
        assertEquals(14, m.getDefesa());
        assertEquals(4, m.getIniciativa());
        assertEquals(100, m.getXpGanho());
        assertTrue(m.isSemFuga());
        assertEquals(2, m.getAtaques().size());
        assertEquals(1, m.getDrops().size());
        assertEquals("Cutelo", m.getDrops().get(0).nomeItem);
        assertEquals(15, m.getDrops().get(0).chance);
    }

    @Test
    void cuteloEFrutaDoDiaboExiste() {
        ItemRpg cutelo = Vendedor.criarItem("Cutelo");
        assertInstanceOf(Arma.class, cutelo);
        Arma a = (Arma) cutelo;
        assertEquals(8, a.getDadoDanoArma());
        assertEquals(2, a.getQuantidadeDanoArma());
        assertEquals("Força", a.getAtributoAtaque());

        assertInstanceOf(Consumivel.class, Vendedor.criarItem("Fruta do Diabo"));
    }

    @Test
    void kitMedicoEstancaOSangramento() {
        FichaRpg f = ficha();
        f.setSangrando(true);
        assertTrue(f.isSangrando());
        f.setSangrando(false);
        assertFalse(f.isSangrando());
    }

    @Test
    void estadosDaTrilhaSaoIndependentes() {
        FichaRpg f = ficha();
        assertFalse(f.isPegadasEncontradas());
        assertFalse(f.isTrilhaIniciada());
        assertFalse(f.isAcampamentoAlcancado());
        assertFalse(f.isBandoVencido());
        assertFalse(f.isGaiolaVasculhada());
        assertFalse(f.isCabanaAlcancada());
        assertFalse(f.isCabanaVisitada());
        assertEquals(3, f.getTurnosParaVoltar());
    }

    @Test
    void missaoEncerradaEVilaNaoRepeteFinal() {
        FichaRpg f = ficha();
        f.aceitarMissao("A Neta Perdida");
        assertTrue(f.isMissaoAceita("A Neta Perdida"));
        f.setNetaEncontrada(true);
        assertFalse(f.isMissaoNetaEncerrada());
        f.setMissaoNetaEncerrada(true);
        assertTrue(f.isMissaoNetaEncerrada());
    }

    @Test
    void presencaPassouDefineCaminhoDireto() {
        FichaRpg f = ficha();
        assertFalse(f.isPresencaNetaPassou());
        f.setPresencaNetaPassou(true);
        assertTrue(f.isPresencaNetaPassou());
    }
}

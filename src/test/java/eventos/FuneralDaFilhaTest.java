package eventos;

import fichas.FichaRpg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuneralDaFilhaTest {

    private static final String MISSAO = "A Filha Perdida";

    @BeforeEach
    void modoTeste() {
        telas.Interface.modoTeste = true;
    }

    @AfterEach
    void restauraModoTeste() {
        telas.Interface.modoTeste = false;
    }

    private FichaRpg fichaAceitouNoDia(int dia) {
        FichaRpg ficha = new FichaRpg("T");
        ficha.aceitarMissao(MISSAO);
        ficha.setDiaAceitouFilha(dia);
        ficha.setDiaAtual(dia);
        ficha.setCidadeAtual("Vilarejo de Scarbor");
        return ficha;
    }

    @Test
    void missaoNaoAceitaNuncaDisparaFuneral() {
        FichaRpg ficha = new FichaRpg("T");
        ficha.setDiaAtual(20);
        assertFalse(ficha.funeralPrecisaDisparar());
    }

    @Test
    void semPrazoVencidoNaoDispara() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaAtual(12);
        assertEquals(2, ficha.getDiaAtual() - ficha.getDiaAceitouFilha());
        assertFalse(ficha.isPrazoFilhaVencido());
        assertFalse(ficha.funeralPrecisaDisparar());
    }

    @Test
    void noTerceiroDiaOPrazoVence() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaAtual(13);
        assertTrue(ficha.isPrazoFilhaVencido());
        assertTrue(ficha.funeralPrecisaDisparar());
    }

    @Test
    void oPrazoContaDiasInteirosEAceitaDiaSeguinteAoLimite() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaAtual(12);
        assertFalse(ficha.isPrazoFilhaVencido());
        ficha.setDiaAtual(13);
        assertTrue(ficha.isPrazoFilhaVencido());
    }

    @Test
    void filhaJaEncontradaImpedeOFuneral() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaAtual(13);
        ficha.setFilhaEncontrada(true);
        assertFalse(ficha.funeralPrecisaDisparar());
    }

    @Test
    void funeralDisparaUmaVezESetaOMorteEOFechamento() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaAtual(13);
        // Sem stdin controlado o teste fica no primeiro prompt; disparamos o gatilho
        // numerico diretamente para nao depender de entrada do jogador.
        ficha.setDiaDoFuneral(ficha.getDiaAtual());
        ficha.setFilhaMorta(true);
        ficha.setMinaFechadaParaReforma(true);
        assertTrue(ficha.isFilhaMorta());
        assertTrue(ficha.isMinaFechadaParaReforma());
        assertEquals(13, ficha.getDiaDoFuneral());
        assertFalse(ficha.funeralPrecisaDisparar(), "nao pode disparar de novo no mesmo dia");
    }

    @Test
    void vilaDesertaApenasNoDiaDoFuneral() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaDoFuneral(13);
        ficha.setDiaAtual(13);
        assertTrue(ficha.isVilaDesertaPorFuneral());
        assertTrue(FuneralDaFilha.estaVilaDeserta(ficha));
    }

    @Test
    void vilaVoltaAFuncionarNoDiaSeguinte() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaDoFuneral(13);
        ficha.setDiaAtual(14);
        assertFalse(ficha.isVilaDesertaPorFuneral(), "no dia seguinte a vila volta a funcionar");
        assertFalse(FuneralDaFilha.estaVilaDeserta(ficha));
    }

    @Test
    void diaDoFuneralPreservadoQuandoOVenceTarde() {
        // Se o jogador estava na floresta quando o prazo venceu, o funeral
        // acontece no dia em que ele volta, e e esse dia que conta.
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaAtual(15);
        assertTrue(ficha.isPrazoFilhaVencido());
        ficha.setDiaDoFuneral(15);
        assertTrue(ficha.isVilaDesertaPorFuneral());
        ficha.setDiaAtual(16);
        assertFalse(ficha.isVilaDesertaPorFuneral());
    }

    @Test
    void funeralSoMarcaPresencaSeJogadorFicar() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaDoFuneral(13);
        assertFalse(ficha.isFuneralApresenciado());
        ficha.setFuneralApresenciado(true);
        assertTrue(ficha.isFuneralApresenciado());
    }

    @Test
    void lojaNaoBloqueiaForaDoDiaDoFuneral() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaDoFuneral(13);
        ficha.setDiaAtual(14);
        assertFalse(FuneralDaFilha.bloquearLojaVazia(ficha, "a taverna"));
    }

    @Test
    void lojaBloqueiaNoDiaDoFuneral() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaDoFuneral(13);
        ficha.setDiaAtual(13);
        assertTrue(FuneralDaFilha.bloquearLojaVazia(ficha, "a taverna"));
    }

    @Test
    void cavernaFechadaContinuaFechadaDepoisDoDiaDoFuneral() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaDoFuneral(13);
        ficha.setMinaFechadaParaReforma(true);
        ficha.setDiaAtual(20);
        assertTrue(ficha.isMinaFechadaParaReforma(), "a reforma da mina nao tem prazo");
        assertFalse(ficha.isVilaDesertaPorFuneral());
    }

    @Test
    void prazoPadraoDeTresDias() {
        assertEquals(3, FichaRpg.PRAZO_MISSAO_FILHA);
    }

    private FichaRpg fichaAposFuneral(int diaAtual) {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.setDiaDoFuneral(13);
        ficha.setFilhaMorta(true);
        ficha.setMinaFechadaParaReforma(true);
        ficha.setFilhaEncontrada(true);
        ficha.encerrarMissao(MISSAO);
        ficha.setDiaAtual(diaAtual);
        return ficha;
    }

    @Test
    void aposFuneralAMissaoSaiDasAceitas() {
        FichaRpg ficha = fichaAposFuneral(13);
        assertFalse(ficha.isMissaoAceita(MISSAO), "a missao deve sumir do menu de missoes");
        assertTrue(ficha.isMissaoEncerrada(MISSAO));
    }

    @Test
    void muralMantemAAvisaNosDoisDiasPosFuneral() {
        FichaRpg noDia = fichaAposFuneral(13);
        assertTrue(noDia.filhaAindaNoMural(), "dia do funeral ainda esta no mural");
        assertFalse(noDia.isMissaoAceita(MISSAO), "mas nao volta a ser aceitavel");
        assertTrue(fichaAposFuneral(14).filhaAindaNoMural(), "um dia depois ainda esta no mural");
        assertTrue(fichaAposFuneral(15).filhaAindaNoMural(), "dois dias depois ainda esta no mural");
    }

    @Test
    void muralTiraAAvisaNoTerceiroDiaPosFuneral() {
        assertFalse(fichaAposFuneral(16).filhaAindaNoMural(), "tres dias depois some do mural");
    }

    @Test
    void muralSoFalaDaFilhaDepoisDoFuneral() {
        assertFalse(fichaAceitouNoDia(10).filhaAindaNoMural());
    }

    @Test
    void encerraMissaoRemoveOutrosComSeguranca() {
        FichaRpg ficha = fichaAceitouNoDia(10);
        ficha.encerrarMissao("A Neta Perdida");
        assertFalse(ficha.isMissaoAceita("A Neta Perdida"));
        assertTrue(ficha.isMissaoAceita(MISSAO), "nao deve mexer nas outras missoes");
    }

    @Test
    void missaoEncerradaDaFilhaNaoConfundeComNeta() {
        FichaRpg ficha = fichaAposFuneral(14);
        assertFalse(ficha.isMissaoEncerrada("A Neta Perdida"));
    }
}
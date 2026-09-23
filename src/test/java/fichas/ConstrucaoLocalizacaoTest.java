package fichas;

import itens.ItemRpg;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConstrucaoLocalizacaoTest {

    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Tester");
        ficha.setNomePersonagem("Arquiteto");
        ficha.adicionarItem(new ItemRpg("Pó da Fada", "", 2));
    }

    private void darMateriaisConstrucao() {
        ficha.adicionarItem(new ItemRpg("Madeira", "", 25));
        ficha.adicionarItem(new ItemRpg("Folha", "", 30));
        ficha.adicionarItem(new ItemRpg("Pedra", "", 15));
        ficha.adicionarItem(new ItemRpg("Couro", "", 6));
    }

    @Test
    void mesaConstruidaLongeNaoUsaDaCabana() {
        darMateriaisConstrucao();
        assertTrue(ficha.construirMesaMagias());
        assertFalse(ficha.isMesaJuntoCabana());
        assertFalse(ficha.isMesaJuntoSala());
        assertTrue(ficha.podeUsarMesaMagias());

        ficha.adicionarProfundidade(5);
        ficha.montarCabana();
        assertTrue(ficha.isNaCabana());
        assertFalse(ficha.podeUsarMesaMagias(), "Mesa a 5 períodos de distância não pode ser usada estando na cabana");
        assertEquals(5, ficha.getDistanciaAte(ficha.getProfundidadeMesaMagias()));

        ficha.reduzirProfundidade(5);
        assertTrue(ficha.podeUsarMesaMagias(), "Após caminhar de volta até a mesa, ela fica acessível");
    }

    @Test
    void mesaJuntoCabanaUsavelDaCabana() {
        darMateriaisConstrucao();
        ficha.montarCabana();
        assertTrue(ficha.construirMesaMagias());
        assertTrue(ficha.isMesaJuntoCabana());
        assertTrue(ficha.isNaCabana());
        assertTrue(ficha.podeUsarMesaMagias(), "Mesa junto à cabana é usada sem se deslocar");
    }

    @Test
    void mesaJuntoSalaFicaAcessivelDaSala() {
        darMateriaisConstrucao();
        assertTrue(ficha.construirSalaTreino());
        assertTrue(ficha.isNaSalaTreino());
        assertFalse(ficha.isSalaJuntoCabana());

        assertTrue(ficha.construirMesaMagias());
        assertTrue(ficha.isMesaJuntoSala());
        assertTrue(ficha.podeUsarMesaMagias(), "Mesa construída na sala fica no mesmo local dela");
        assertTrue(ficha.podeUsarSalaTreino(), "Estando nela, a vizinha sala também fica acessível");

        ficha.irParaMesaMagias();
        assertTrue(ficha.podeUsarMesaMagias());
        assertTrue(ficha.podeUsarSalaTreino(), "Proximidade: da mesa, a sala continua acessível");
    }

    @Test
    void salaConstruidaNaMesaFicaJuntoMesa() {
        darMateriaisConstrucao();
        assertTrue(ficha.construirMesaMagias());
        assertTrue(ficha.isNaMesaMagias());

        assertTrue(ficha.construirSalaTreino());
        assertFalse(ficha.isSalaJuntoCabana());
        assertTrue(ficha.isSalaJuntoMesa());
        assertTrue(ficha.podeUsarSalaTreino(), "Sala construída na mesa fica no mesmo local dela");
        assertTrue(ficha.podeUsarMesaMagias(), "Proximidade: da sala, a mesa também fica acessível");
    }

    @Test
    void salaLongeNaoUsaDaCabana() {
        darMateriaisConstrucao();
        assertTrue(ficha.construirSalaTreino());
        assertTrue(ficha.podeUsarSalaTreino());

        ficha.adicionarProfundidade(5);
        ficha.montarCabana();
        assertTrue(ficha.isNaCabana());
        assertFalse(ficha.podeUsarSalaTreino(), "Sala a 5 períodos de distância não pode ser usada estando na cabana");

        ficha.reduzirProfundidade(5);
        assertTrue(ficha.podeUsarSalaTreino(), "Após caminhar de volta até a sala, ela fica acessível");
    }

    @Test
    void irAOutraEstruturaSaiDasAnteriores() {
        darMateriaisConstrucao();
        ficha.montarCabana();
        assertTrue(ficha.construirSalaTreino());
        assertTrue(ficha.isSalaJuntoCabana());
        assertTrue(ficha.podeUsarSalaTreino());
        assertTrue(ficha.isNaCabana());

        ficha.entrarSalaTreino();
        assertTrue(ficha.podeUsarSalaTreino());
        assertFalse(ficha.isNaMesaMagias());

        assertTrue(ficha.construirMesaMagias());
        assertTrue(ficha.isMesaJuntoCabana());
        assertTrue(ficha.isNaMesaMagias());
        assertTrue(ficha.isNaCabana());
        assertTrue(ficha.podeUsarMesaMagias(), "construída no conjunto da cabana, fica acessível de lá");

        ficha.voltarParaCabana();
        assertTrue(ficha.isNaCabana());
        assertFalse(ficha.isNaSalaTreino());
        assertFalse(ficha.isNaMesaMagias());

        ficha.sairDaCabana();
        assertFalse(ficha.isNaCabana());
        assertFalse(ficha.isNaSalaTreino());
        assertFalse(ficha.isNaMesaMagias(), "Explorar a partir da cabana conta como ter saído dela");
    }

    @Test
    void sairDaEstruturaLongeZeraTudo() {
        darMateriaisConstrucao();
        assertTrue(ficha.construirSalaTreino());
        ficha.sairDaCabana();
        assertFalse(ficha.isNaSalaTreino());

        assertTrue(ficha.construirMesaMagias());
        ficha.sairDaCabana();
        assertFalse(ficha.isNaMesaMagias(), "Explorar a partir da mesa conta como ter saído dela");
    }

    @Test
    void cabanaFicaAcessivelSoEstandoNela() {
        darMateriaisConstrucao();
        ficha.montarCabana();
        assertTrue(ficha.podeUsarCabana());

        ficha.sairDaCabana();
        assertFalse(ficha.isNaCabana());
        assertFalse(ficha.podeUsarCabana());
    }

    @Test
    void dormirZeraPosicaoDaMesa() {
        darMateriaisConstrucao();
        ficha.montarCabana();
        ficha.construirMesaMagias();
        assertTrue(ficha.isNaMesaMagias());

        assertFalse(ficha.dormir(), "De dia não dá para dormir");
        ficha.avancarTempo(3);
        assertTrue(ficha.dormir());
        assertFalse(ficha.isNaMesaMagias(), "Acordar na cabana deixa a mesa para trás");
        assertTrue(ficha.isNaCabana());
        assertTrue(ficha.podeUsarMesaMagias(), "Mesa junto à cabana continua acessível");
    }

    @Test
    void construcaoAncoradaNoPontoOndeFoiMontada() {
        darMateriaisConstrucao();
        ficha.montarCabana();
        assertEquals(0, ficha.getProfundidadeCabana());
        assertTrue(ficha.podeUsarCabana());

        ficha.adicionarProfundidade(10);
        assertFalse(ficha.podeUsarCabana(), "Estar 10 períodos longe não permite usar a cabana");
        assertEquals(10, ficha.getDistanciaAte(ficha.getProfundidadeCabana()));

        assertTrue(ficha.construirMesaMagias());
        assertEquals(10, ficha.getProfundidadeMesaMagias());
        assertFalse(ficha.isMesaJuntoCabana(), "Mesa montada em outro ponto não fica junto da cabana");
        assertTrue(ficha.podeUsarMesaMagias(), "Quem monta no ponto atual usa a mesa daqui");

        ficha.adicionarProfundidade(2);
        assertEquals(2, ficha.getDistanciaAte(ficha.getProfundidadeMesaMagias()),
                "Voltar à mesa fica a 2 períodos do ponto atual");
        assertEquals(12, ficha.getDistanciaAte(ficha.getProfundidadeCabana()),
                "Voltar à cabana fica a 12 períodos do ponto atual");
    }

    @Test
    void moverConstrucaoCriaNovoPonto() {
        darMateriaisConstrucao();
        ficha.adicionarItem(new ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new ItemRpg("Folha", "", 10));
        ficha.adicionarItem(new ItemRpg("Pedra", "", 10));
        ficha.adicionarItem(new ItemRpg("Pó da Fada", "", 2));
        ficha.montarCabana();

        ficha.adicionarProfundidade(10);
        assertTrue(ficha.moverCabana(), "Dá para montar uma cabana nova no ponto atual");
        assertEquals(10, ficha.getProfundidadeCabana());
        assertTrue(ficha.podeUsarCabana());

        assertTrue(ficha.construirMesaMagias());
        assertTrue(ficha.moverMesaMagias());
        assertEquals(10, ficha.getProfundidadeMesaMagias());
        assertEquals(10, ficha.getProfundidadeCabana(), "A mesa nova passou a ser o ponto do construtor");
    }

    @Test
    void voltaParaConstrucaoMaisProximaDaBorda() {
        darMateriaisConstrucao();
        ficha.montarCabana();
        ficha.adicionarProfundidade(10);
        ficha.construirMesaMagias();
        assertEquals(10, ficha.getProfundidadeConstrucaoMaisProxima());

        ficha.adicionarProfundidade(FichaRpg.PROFUNDIDADE_PARA_SAIR);
        assertEquals(10, ficha.getProfundidadeConstrucaoMaisProxima(),
                "A construção mais próxima de quem volta do vilarejo é a do ponto 10");
    }
}
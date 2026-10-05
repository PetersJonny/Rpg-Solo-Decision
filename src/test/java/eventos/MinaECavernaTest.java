package eventos;

import fichas.FichaRpg;
import itens.ItemRpg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    void goblinTransformadoTemOsNumerosPedidos() {
        criaturas.Criatura t = criaturas.CriaturaFactory.criarGoblinTransformado();
        assertEquals(5, t.getNivel());
        assertEquals(90, t.getVidaMaxima());
        assertEquals(3, t.getIniciativa() - criaturas.CriaturaFactory.criarGoblin().getIniciativa(), "+3 de iniciativa sobre o goblin comum");
        assertEquals(200, t.getXpGanho());
    }

    @Test
    void goblinTransformadoBateComPorreteDe1d8Mais5() {
        criaturas.Criatura t = criaturas.CriaturaFactory.criarGoblinTransformado();
        assertEquals(1, t.getAtaques().size());
        criaturas.Criatura.Ataque ataque = t.getAtaques().get(0);
        assertEquals(1, ataque.qtdDado);
        assertEquals(8, ataque.ladosDado);
        assertEquals(5, t.getBonusDano());
    }

    @Test
    void goblinTransformadoDroporaPorreteCom50PorCento() {
        criaturas.Criatura t = criaturas.CriaturaFactory.criarGoblinTransformado();
        criaturas.Criatura.Drop drop = t.getDrops().get(0);
        assertEquals("Porrete", drop.nomeItem);
        assertEquals(50, drop.chance);
        assertEquals(1, drop.qtdMin);
        assertEquals(1, drop.qtdMax);
    }

    @Test
    void goblinTransformadoDroporaEntre12E26DeOuro() {
        criaturas.Criatura t = criaturas.CriaturaFactory.criarGoblinTransformado();
        assertEquals(12, t.getOuroMin());
        assertEquals(26, t.getOuroMax());
        assertEquals(100, t.getChanceOuro());
    }

    @Test
    void porretePesarDoisEOApareceNaLoja() {
        itens.Arma porrete = (itens.Arma) loja.Vendedor.criarItem("Porrete");
        assertNotNull(porrete);
        assertEquals(2.0, porrete.getPeso(), 0.0001);
        assertEquals(8, porrete.getDadoDanoArma(), "1d8");
        assertEquals("Força", porrete.getAtributoAtaque());
        assertEquals(40, loja.Vendedor.precoBase("Porrete"), "custo definido");
    }

    @Test
    void porreteTemPrecoJustoDeVendaParaOFerreiro() {
        int cheio = loja.Vendedor.precoDeCompraMelhorado("Porrete");
        assertEquals(32, cheio, "80% do valor cheio de 40");
        assertTrue(cheio < loja.Vendedor.precoDeCompraMelhorado("Martelo"), "vale menos que um martelo igual em dano");
        assertTrue(cheio > loja.Vendedor.precoDeCompraMelhorado("Faca"), "vale mais que uma faca bem inferior");
    }

    @Test
    void goblinComumContinuaNormalParaOCombateAntecipado() {
        criaturas.Criatura pequeno = criaturas.CriaturaFactory.criarGoblin();
        assertEquals(15, pequeno.getVidaMaxima(), "o goblin pequeno nao muda de tamanho");
        assertEquals("Goblin", pequeno.getNome());
        assertFalse(pequeno.isSemFuga(), "o goblin pequeno ainda pode fugir");
    }

    @Test
    void bonusDeDanoDaCriaturaSomeEmQuemNaoTem() {
        criaturas.Criatura t = criaturas.CriaturaFactory.criarGoblinTransformado();
        assertEquals(5, t.getBonusDano());
        assertEquals(0, criaturas.CriaturaFactory.criarGoblin().getBonusDano());
        assertEquals(0, criaturas.CriaturaFactory.criarLobo().getBonusDano());
    }

    @Test
    void interiorRegistraAVistaDaFilhaEVaFilha() {
        FichaRpg ficha = fichaNova();
        ficha.aceitarMissao("A Filha Perdida");
        assertFalse(ficha.isCenaDoGoblinVista());
        ficha.setCenaDoGoblinVista(true);
        assertTrue(ficha.isCenaDoGoblinVista());
        assertFalse(Caverna.ePrimeiraVezQueAfilhaFoiVista(ficha));
    }

    @Test
    void levarParaaMaeMarcaResgateEEncerraAMissao() {
        FichaRpg ficha = fichaNova();
        ficha.aceitarMissao("A Filha Perdida");
        ficha.setFilhaResgatada(true);
        ficha.setFilhaEncontrada(true);
        ficha.setDesfechoDaFilha("levada_para_a_mae");
        ficha.encerrarMissao("A Filha Perdida");
        assertTrue(Caverna.resgateConcluido(ficha));
        assertFalse(ficha.isMissaoAceita("A Filha Perdida"));
    }

    @Test
    void guardasResgatamMasNaoSaoResgateDoJogador() {
        FichaRpg ficha = fichaNova();
        ficha.aceitarMissao("A Filha Perdida");
        ficha.setFilhaEncontrada(true);
        ficha.setDesfechoDaFilha("resgatada_pelos_guardas");
        assertTrue(Caverna.resgateConcluido(ficha), "a filha foi salva");
        assertFalse(ficha.isFilhaResgatada(), "mas quem levou nao foi o jogador");
        assertFalse(Caverna.filhaFoiAbandonada(ficha));
    }

    @Test
    void abandonarAFilhaNaoEncerraAMissaoNemSalvaNinguem() {
        FichaRpg ficha = fichaNova();
        ficha.aceitarMissao("A Filha Perdida");
        ficha.setDesfechoDaFilha("abandonada");
        assertTrue(Caverna.filhaFoiAbandonada(ficha));
        assertFalse(ficha.isFilhaResgatada());
        assertFalse(ficha.isFilhaEncontrada());
        assertTrue(ficha.isMissaoAceita("A Filha Perdida"), "o prazo continua correndo ate o funeral");
    }

    @Test
    void trilhoECristalSoAparecemUmaVez() {
        FichaRpg ficha = fichaNova();
        ficha.setTrilhaDeTremVistaNaMina(true);
        ficha.setCristaisVistosNaMina(true);
        assertTrue(ficha.isTrilhaDeTremVistaNaMina());
        assertTrue(ficha.isCristaisVistosNaMina());
    }

    @Test
    void presencaSentidaEhPersistente() {
        FichaRpg ficha = fichaNova();
        ficha.setPresencaSentidaNaMina(true);
        assertTrue(ficha.isPresencaSentidaNaMina());
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

    @Test
    void porreteColetadoDoGoblinEUmaArmaEquipavel() throws Exception {
        FichaRpg ficha = fichaNova();

        pegarPorreteDoGoblin(ficha);

        assertTrue(ficha.temItem("Porrete"), "o porrete entra na mochila");
        ItemRpg coletado = ficha.getInventario().stream()
                .filter(i -> i.getNome().equals("Porrete"))
                .findFirst()
                .orElseThrow();
        assertInstanceOf(itens.Arma.class, coletado, "tem que ser uma arma, nao um item genérico");
        assertEquals(8, ((itens.Arma) coletado).getDadoDanoArma(), "1d8");
        assertEquals("Força", ((itens.Arma) coletado).getAtributoAtaque());
        assertEquals(2.0, coletado.getPeso(), 0.0001, "mesmo peso do porrete da loja");
    }

    @Test
    void porreteColetadoNaoDuplicaSeJaTemUm() throws Exception {
        FichaRpg ficha = fichaNova();
        ficha.adicionarItem(loja.Vendedor.criarItem("Porrete"));
        int antes = ficha.getQuantidadeDe("Porrete");

        pegarPorreteDoGoblin(ficha);

        assertEquals(antes, ficha.getQuantidadeDe("Porrete"), "nao ganha um segundo porrete");
    }

    private static void pegarPorreteDoGoblin(FichaRpg ficha) throws Exception {
        Method m = Caverna.class.getDeclaredMethod("PegarPorreteDoGoblin", FichaRpg.class);
        m.setAccessible(true);
        try {
            m.invoke(null, ficha);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }
}

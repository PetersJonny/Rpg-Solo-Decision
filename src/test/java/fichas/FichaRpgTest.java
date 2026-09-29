package fichas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import classes.Guerreiro;
import itens.Consumivel;
import itens.Arma;
import itens.Armadura;

import static org.junit.jupiter.api.Assertions.*;

class FichaRpgTest {
    private FichaRpg ficha;

    @BeforeEach
    void setUp() {
        ficha = new FichaRpg("Marcus");
        ficha.setNomePersonagem("Herói");
    }

    @Test
    void testCriacaoInicial() {
        assertEquals("Herói", ficha.getNomePersonagem());
        assertEquals("Marcus", ficha.getNomePessoa());
        assertEquals(1, ficha.getNivel());
        assertEquals(0, ficha.getXp());
        assertEquals(0, ficha.getOuro());
        assertFalse(ficha.isFichaCompleta());
    }

    @Test
    void testAdicionarAtributos() {
        ficha.adicionarAtributo(1, 2); // 2 de Constituição
        ficha.adicionarAtributo(3, 4); // 4 de Força
        
        ficha.aplicarBonus(); // Precisa aplicar para ver na Ficha base
        
        assertEquals(2, ficha.getConstituicao());
        assertEquals(4, ficha.getForca());
    }

    @Test
    void testCompletarFichaComClasse() {
        ficha.adicionarAtributo(1, 2);
        ficha.adicionarAtributo(2, 2);
        ficha.adicionarAtributo(3, 2);
        ficha.setClasse(new Guerreiro());

        assertTrue(ficha.isFichaCompleta());
        assertNotNull(ficha.getArmaEquipada());
        assertEquals(1, ficha.getInventario().size()); // Guerreiro começa com alguns itens
    }

    @Test
    void testAumentarXpESubirNivel() {
        ficha.adicionarAtributo(1, 3);
        ficha.setClasse(new Guerreiro());

        int niveisGanhos = ficha.adicionarXp(150); // XP para Nivel 2 é 100
        
        assertEquals(1, niveisGanhos);
        assertEquals(2, ficha.getNivel());
        assertEquals(50, ficha.getXp());
    }

    @Test
    void testAdicionarRemoverItens() {
        Consumivel pocao = new Consumivel("Poção de Vida", "Cura 20", 1);
        pocao.setQuantidade(1);
        
        ficha.adicionarItem(pocao);
        assertTrue(ficha.temItem("Poção de Vida"));
        assertEquals(1, ficha.getQuantidadeDe("Poção de Vida"));
        
        // Adiciona mais uma
        Consumivel pocao2 = new Consumivel("Poção de Vida", "Cura 20", 2);
        pocao2.setQuantidade(2);
        ficha.adicionarItem(pocao2);
        
        assertEquals(3, ficha.getQuantidadeDe("Poção de Vida"));
        
        // Remove
        boolean removed = ficha.removerItem("Poção de Vida", 2);
        assertTrue(removed);
        assertEquals(1, ficha.getQuantidadeDe("Poção de Vida"));
    }

    @Test
    void testEquiparMelhorArmadura() {
        Armadura armaduraFraca = new Armadura("Armadura de Couro", "Defesa +2", 2, 1);
        armaduraFraca.setQuantidade(1);
        Armadura armaduraForte = new Armadura("Armadura de Ferro", "Defesa +5", 5, 1);
        armaduraForte.setQuantidade(1);

        ficha.adicionarItem(armaduraFraca);
        ficha.equiparMelhorArmadura();
        
        assertEquals("Armadura de Couro", ficha.getArmaduraEquipada().getNome());
        
        ficha.adicionarItem(armaduraForte);
        ficha.equiparMelhorArmadura();
        
        assertEquals("Armadura de Ferro", ficha.getArmaduraEquipada().getNome());
    }

    @Test
    void testAvancarTempoEDormir() {
        assertFalse(ficha.isEhNoite());
        assertEquals(1, ficha.getDiaAtual());

        ficha.avancarTempo(3); // 3 unidades vira para Noite 1
        
        assertTrue(ficha.isEhNoite());
        assertEquals(1, ficha.getDiaAtual());
        
        ficha.avancarTempo(3); // Mais 3 unidades vira para Dia 2
        
        assertFalse(ficha.isEhNoite());
        assertEquals(2, ficha.getDiaAtual());
        assertEquals(1, ficha.getDiasSemDormir());
        
        // Testa cabana e dormir
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Pedra", "", 5));
        
        assertTrue(ficha.montarCabana());
        
        ficha.avancarTempo(3); // Vai para Noite 2
        assertTrue(ficha.isEhNoite());
        
        // Agora dorme
        ficha.setVidaMaxima(100);
        ficha.setVidaPersonagem(10);
        ficha.setManaMaxima(50);
        ficha.setManaPersonagem(5);
        
        assertTrue(ficha.dormir());
        
        // Verifica se descansou e amanheceu
        assertFalse(ficha.isEhNoite());
        assertEquals(3, ficha.getDiaAtual());
        assertEquals(0, ficha.getDiasSemDormir());
        assertEquals(43, ficha.getVidaPersonagem()); // 10 + 33 (1/3 de 100)
        assertEquals(21, ficha.getManaPersonagem()); // 5 + 16 (1/3 de 50)
    }

    @Test
    void pactoMortalEhLimpadoNoResetDeCombate() {
        boolean pacto = ficha.isPactoMortalAtivo();
        assertFalse(pacto);
        ficha.setPactoMortalAtivo(true);
        assertTrue(ficha.isPactoMortalAtivo());
        ficha.resetarEfeitosCombate();
        assertFalse(ficha.isPactoMortalAtivo(), "Reset de combate deve limpar o Pacto Mortal");
    }

    @Test
    void pactoMortalAumentaDanoSofridoEmTres() {
        ficha.setVidaMaxima(100);
        ficha.setVidaPersonagem(50);
        ficha.setPactoMortalAtivo(true);
        ficha.receberDano(10);
        assertEquals(37, ficha.getVidaPersonagem(), "50 - (10 + 3) = 37");
    }

    @Test
    void olhoDemonicoEncontradoESaveFlags() {
        assertFalse(ficha.isOlhoDemonicoEncontrado());
        assertFalse(ficha.isEspadaMajestralEncontrada());
        assertFalse(ficha.isCoroaReiEncontrada());
        assertFalse(ficha.isReiDasCriaturas());
        assertFalse(ficha.isOlhoDemonicoFundido());
        ficha.setOlhoDemonicoEncontrado(true);
        ficha.setEspadaMajestralEncontrada(true);
        ficha.setCoroaReiEncontrada(true);
        ficha.setReiDasCriaturas(true);
        assertTrue(ficha.isOlhoDemonicoEncontrado());
        assertTrue(ficha.isEspadaMajestralEncontrada());
        assertTrue(ficha.isCoroaReiEncontrada());
        assertTrue(ficha.isReiDasCriaturas());
        assertFalse(ficha.isOlhoDemonicoFundido(), "Aceitar o chamado é separado de apenas encontrar o olho");
        ficha.setOlhoDemonicoFundido(true);
        assertTrue(ficha.isOlhoDemonicoFundido());
    }

    @Test
    void travessiaComecaNaProfundidadeZero() {
        assertEquals(0, ficha.getProfundidadeFloresta());
        assertFalse(ficha.isNoVilarejo());
        assertNull(ficha.getCidadeAtual());
    }

    @Test
    void adicionarProfundidadeAcumulaAteSairDaFloresta() {
        ficha.adicionarProfundidade(5);
        assertEquals(5, ficha.getProfundidadeFloresta());
        assertFalse(ficha.isNoVilarejo());
    }

    @Test
    void profundidadeMaximaLevaAoVilarejo() {
        ficha.adicionarProfundidade(FichaRpg.PROFUNDIDADE_PARA_SAIR);
        assertTrue(ficha.isNoVilarejo());
        ficha.adicionarProfundidade(1);
        assertEquals(FichaRpg.PROFUNDIDADE_PARA_SAIR, ficha.getProfundidadeFloresta(),
                "A profundidade não pode passar do limite (fora da floresta)");
    }

    @Test
    void reduzirProfundidadeVoltaAsConstrucoes() {
        ficha.adicionarProfundidade(FichaRpg.PROFUNDIDADE_PARA_SAIR);
        assertTrue(ficha.isNoVilarejo());
        ficha.reduzirProfundidade(FichaRpg.PROFUNDIDADE_PARA_SAIR);
        assertEquals(0, ficha.getProfundidadeFloresta());
        assertFalse(ficha.isNoVilarejo());
        ficha.reduzirProfundidade(10);
        assertEquals(0, ficha.getProfundidadeFloresta(),
                "A profundidade não pode ficar negativa");
    }

    @Test
    void cidadeAtualEhSalvaNaFicha() {
        assertNull(ficha.getCidadeAtual());
        ficha.setCidadeAtual("Vilarejo de Scarbor");
        assertEquals("Vilarejo de Scarbor", ficha.getCidadeAtual());
    }

    @Test
    void fomeComecaComPenalidadeZero() {
        assertEquals(0, ficha.getDiasSemComer());
        assertEquals(0, ficha.getPenalidadeFome());
        assertFalse(ficha.isEnjoado());
        assertEquals(0, ficha.getPerdaVidaPorFome());
    }

    @Test
    void fomeExigeComecarNoDiaSeguinte() {
        // Dia 1 para Noite 1: ainda não vira dia, sem penalidade
        ficha.avancarTempo(3);
        assertEquals(0, ficha.getDiasSemComer());
        // Noite 1 para Dia 2: não comeu no dia 1
        ficha.avancarTempo(3);
        assertEquals(1, ficha.getDiasSemComer());
        assertEquals(1, ficha.getPenalidadeFome());
        assertEquals(ficha.getDestreza() - 1, ficha.getDestrezaTeste());
        assertEquals(ficha.getForca() - 1, ficha.getForcaTeste());
    }

    @Test
    void tresDiasSemComerDaPenalidadeDois() {
        // Simula 3 dias sem comer: vira Dia 4
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        assertEquals(4, ficha.getDiaAtual());
        assertEquals(3, ficha.getDiasSemComer());
        assertEquals(2, ficha.getPenalidadeFome());
    }

    @Test
    void cincoDiasSemComerComecaPerderVidaPorPeriodo() {
        ficha.setVidaMaxima(100);
        ficha.setVidaPersonagem(100);
        int vidaBase = ficha.getVidaPersonagem();
        // Avança até Dia 6 (5 dias sem comer acumulados)
        for (int i = 0; i < 10; i++) {
            ficha.avancarTempo(3);
        }
        assertEquals(6, ficha.getDiaAtual());
        assertEquals(5, ficha.getDiasSemComer());
        assertEquals(1, ficha.getPerdaVidaPorFome());
        // A perda já foi aplicada em cada virada (dia e noite): espera-se que tenha perdido vida
        assertTrue(ficha.getVidaPersonagem() < vidaBase);
    }

    @Test
    void comerComidaBoaResetAFomeEEnjoo() {
        ficha.avancarTempo(3);
        ficha.avancarTempo(3); // Dia 2, 1 dia sem comer
        assertEquals(1, ficha.getPenalidadeFome());
        ficha.comerCarnePodre(); // fica enjoado
        assertTrue(ficha.isEnjoado());
        assertEquals(1, ficha.getPenalidadeFome());
        ficha.comerComidaBoa();
        assertEquals(0, ficha.getDiasSemComer());
        assertEquals(0, ficha.getPenalidadeFome());
        assertFalse(ficha.isEnjoado());
        assertTrue(ficha.isComeuHoje());
    }

    @Test
    void carnePodreMantemDebuffDeForcaEDestreza() {
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        assertEquals(1, ficha.getPenalidadeFome());
        int destrezaBase = ficha.getDestreza();
        int forcaBase = ficha.getForca();
        int destrezaTesteAntes = ficha.getDestrezaTeste();
        int forcaTesteAntes = ficha.getForcaTeste();
        ficha.comerCarnePodre();
        // Continua com -1 nos testes mesmo resetando a contagem
        assertEquals(0, ficha.getDiasSemComer());
        assertEquals(1, ficha.getPenalidadeFome());
        assertEquals(destrezaTesteAntes, ficha.getDestrezaTeste());
        assertEquals(forcaTesteAntes, ficha.getForcaTeste());
        assertEquals(destrezaBase - 1, ficha.getDestrezaTeste());
        assertEquals(forcaBase - 1, ficha.getForcaTeste());
    }

    @Test
    void comerCarnePodreSemDebuffAnteriorGanhaMenosUm() {
        ficha.comerCarnePodre();
        assertEquals(0, ficha.getDiasSemComer());
        assertTrue(ficha.isEnjoado());
        assertEquals(1, ficha.getPenalidadeFome());
        assertEquals(ficha.getDestreza() - 1, ficha.getDestrezaTeste());
        assertEquals(ficha.getForca() - 1, ficha.getForcaTeste());
    }

    @Test
    void comerNoDiaEvitaFraquezaNoDiaSeguinte() {
        ficha.comerComidaBoa();
        ficha.avancarTempo(3); // Dia 1 -> Noite 1
        ficha.avancarTempo(3); // Noite 1 -> Dia 2
        assertEquals(0, ficha.getDiasSemComer());
        assertEquals(0, ficha.getPenalidadeFome());
        // Mas não comeu no dia 2 -> Dia 3 fica fraco
        ficha.avancarTempo(3);
        ficha.avancarTempo(3);
        assertEquals(1, ficha.getDiasSemComer());
        assertEquals(1, ficha.getPenalidadeFome());
    }

    @Test
    void dormirRecuperaMetadeSeComeuNoDia() {
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Pedra", "", 5));
        ficha.montarCabana();
        ficha.avancarTempo(3); // Noite 1
        assertTrue(ficha.isEhNoite());
        ficha.setVidaMaxima(100);
        ficha.setVidaPersonagem(10);
        ficha.setManaMaxima(50);
        ficha.setManaPersonagem(5);
        ficha.comerComidaBoa(); // comeu no dia
        assertTrue(ficha.dormir());
        // 1/2 de 100 = 50 de cura (vida vira 60); 1/2 de 50 = 25 (mana vira 30)
        assertEquals(60, ficha.getVidaPersonagem());
        assertEquals(30, ficha.getManaPersonagem());
    }

    @Test
    void comidaBoaPodeSerUsadaComVidaCheia() {
        ficha.setVidaMaxima(100);
        ficha.setVidaPersonagem(100);
        ficha.avancarTempo(3);
        ficha.avancarTempo(3); // 1 dia sem comer
        // 1 fruta é lanche: cura, mas NÃO zera a fome (precisa 3 no dia)
        assertTrue(mecanicas.GerenciadorDeItens.usarItemForaDeCombate(ficha,
                new itens.Consumivel("Frutas", "", 1), 1));
        assertEquals(1, ficha.getDiasSemComer());
        assertEquals(100, ficha.getVidaPersonagem());
        // Mais 2 frutas completam a refeição do dia
        assertTrue(mecanicas.GerenciadorDeItens.usarItemForaDeCombate(ficha,
                new itens.Consumivel("Frutas", "", 2), 2));
        assertEquals(0, ficha.getDiasSemComer());
        assertEquals(100, ficha.getVidaPersonagem());
    }

    @Test
    void frutasPrecisamDeTresNoDiaParaContarComoRefeicao() {
        // Dois usos de frutas (1 + 2) no MESMO dia viram comida completa
        ficha.avancarTempo(3);
        ficha.avancarTempo(3); // 1 dia sem comer
        ficha.comerFrutas(1);
        assertEquals(1, ficha.getDiasSemComer());
        assertFalse(ficha.isComeuHoje());
        ficha.comerFrutas(2);
        assertEquals(0, ficha.getDiasSemComer());
        assertTrue(ficha.isComeuHoje());
        // O contador reseta ao virar o dia
        ficha.avancarTempo(3); // Dia 2 -> Noite 2
        ficha.avancarTempo(3); // Noite 2 -> Dia 3
        assertEquals(0, ficha.getFrutasComidasHoje());
        assertEquals(0, ficha.getDiasSemComer()); // comeu no dia 2
    }

    @Test
    void carneDeLoboCozidaCuraSempreEliDandoFome() {
        ficha.avancarTempo(3);
        ficha.avancarTempo(3); // 1 dia sem comer
        ficha.setVidaMaxima(50);
        ficha.setVidaPersonagem(20);
        assertTrue(mecanicas.GerenciadorDeItens.usarItemForaDeCombate(ficha,
                new itens.Consumivel("Carne de Lobo Cozida", "", 1), 1));
        assertEquals(0, ficha.getDiasSemComer());
        assertTrue(ficha.getVidaPersonagem() >= 21 && ficha.getVidaPersonagem() <= 23);
        assertFalse(ficha.isEnjoado());
    }

    @Test
    void carneCruaSempreZeraAFomeCurandoOuNao() {
        // Carne crua: 30% de chance de estragar. Em QUALQUER caso zera a fome
        // e marca comeuHoje (se estragou, não cura; se boa, cura 1d3).
        ficha.avancarTempo(3);
        ficha.avancarTempo(3); // 1 dia sem comer
        assertEquals(1, ficha.getDiasSemComer());
        for (int i = 0; i < 20; i++) {
            ficha.comerCarneCrua();
            assertEquals(0, ficha.getDiasSemComer());
            assertTrue(ficha.isComeuHoje());
            // Deixa passar 2 dias (4 períodos) sem comer para voltar a 1 dia
            for (int j = 0; j < 4; j++) {
                ficha.avancarTempo(3);
            }
            assertEquals(1, ficha.getDiasSemComer());
        }
    }

    @Test
    void montarFogueiraGasta4MadeiraE3Folha() {
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 5));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 4));
        assertTrue(ficha.montarFogueira());
        assertTrue(ficha.isTemFogueira());
        assertTrue(ficha.podeUsarFogueira());
        assertEquals(1, ficha.getQuantidadeDe("Madeira"));
        assertEquals(1, ficha.getQuantidadeDe("Folha"));
    }

    @Test
    void montarFogueiraSemMateriaisFalha() {
        assertFalse(ficha.montarFogueira());
        assertFalse(ficha.isTemFogueira());
    }

    @Test
    void montarFogueiraEMoverParaOutroPonto() {
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 10));
        ficha.montarFogueira(); // ponto 0
        assertTrue(ficha.podeUsarFogueira());
        ficha.adicionarProfundidade(3); // ponto 3
        assertFalse(ficha.podeUsarFogueira());
        assertTrue(ficha.moverFogueira()); // move para o ponto atual (3)
        assertTrue(ficha.isNaFogueira());
        assertTrue(ficha.podeUsarFogueira());
    }

    @Test
    void cozinharTodasAsCarnesGasta2MadeirasEConverteTudo() {
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 5));
        ficha.adicionarItem(new itens.Consumivel("Carne de Lobo", "", 1));
        ficha.adicionarItem(new itens.Consumivel("Carne de Urso", "", 2));
        assertTrue(ficha.montarFogueira()); // gasta 4 madeiras: ficam 6

        assertTrue(ficha.cozinharTodasAsCarnes()); // gasta 2 madeiras: ficam 4
        assertEquals(4, ficha.getQuantidadeDe("Madeira"));
        assertEquals(0, ficha.getQuantidadeDe("Carne de Lobo"));
        assertEquals(1, ficha.getQuantidadeDe("Carne de Lobo Cozida"));
        assertEquals(0, ficha.getQuantidadeDe("Carne de Urso"));
        assertEquals(2, ficha.getQuantidadeDe("Carne de Urso Cozida"));
    }

    @Test
    void cozinharTodasAsCarnesRequerEstarJuntoDaFogueira() {
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 5));
        ficha.adicionarItem(new itens.Consumivel("Carne de Lobo", "", 1));
        ficha.montarFogueira(); // ponto 0
        assertTrue(ficha.podeUsarFogueira());

        ficha.adicionarProfundidade(5); // se afasta do ponto da fogueira
        assertFalse(ficha.podeUsarFogueira());
        assertFalse(ficha.cozinharTodasAsCarnes());

        ficha.reduzirProfundidade(5); // volta ao ponto 0
        assertTrue(ficha.podeUsarFogueira());
        assertTrue(ficha.cozinharTodasAsCarnes());
    }

    @Test
    void cozinharTodasAsCarnesSem2MadeirasFalha() {
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 5));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 5));
        ficha.adicionarItem(new itens.Consumivel("Carne de Urso", "", 1));
        ficha.montarFogueira(); // gasta 4 madeiras: fica 1
        assertEquals(1, ficha.getQuantidadeDe("Madeira"));
        assertFalse(ficha.cozinharTodasAsCarnes());
        assertEquals(1, ficha.getQuantidadeDe("Carne de Urso"));
    }

    @Test
    void cozinharTodasAsCarnesSemCarneNaoGastaMadeira() {
        ficha.adicionarItem(new itens.ItemRpg("Madeira", "", 10));
        ficha.adicionarItem(new itens.ItemRpg("Folha", "", 5));
        ficha.montarFogueira(); // gasta 4 madeiras: ficam 6
        assertFalse(ficha.cozinharTodasAsCarnes()); // sem carne crua
        assertEquals(6, ficha.getQuantidadeDe("Madeira"));
    }

    @Test
    void mochilaComForcaNegativaFicaFixaEm10() {
        ficha.adicionarAtributo(3, -6); // Força negativa
        ficha.aplicarBonus();
        assertEquals(-6, ficha.getForca());
        assertEquals(10.0, ficha.getCapacidadeMochila());
        assertEquals(10.0, ficha.getEspacoLivreMochila());
    }

    @Test
    void mochilaEscalaComForcaPositiva() {
        ficha.adicionarAtributo(3, 4); // Força 4
        ficha.aplicarBonus();
        assertEquals(4, ficha.getForca());
        assertEquals(30.0, ficha.getCapacidadeMochila());
    }

    @Test
    void flagsDaTavernaComecamDesligados() {
        assertFalse(ficha.isGoblinsResolvido());
        assertFalse(ficha.isDonoDaTavernaAgradeceu());
        assertFalse(ficha.isComidaPorContaDaCasa());
        assertFalse(ficha.isComidaDaCasaUsada());
    }

    @Test
    void missaoAceitaNaoDuplica() {
        assertFalse(ficha.isMissaoAceita("A Filha Perdida"));
        ficha.aceitarMissao("A Filha Perdida");
        ficha.aceitarMissao("A Filha Perdida");
        assertEquals(1, ficha.getMissoesAceitas().size());
        assertTrue(ficha.isMissaoAceita("A Filha Perdida"));
    }

    @Test
    void encomendaDoFerreiroFicaProntaNoDiaSeguinte() {
        assertFalse(ficha.isOrdemDoFerreiroPendente());
        assertFalse(ficha.isOrdemDoFerreiroPronta());
        ficha.setFerreiroOrdemItem("Espada Pesada");
        ficha.setFerreiroOrdemDia(ficha.getDiaAtual());
        assertTrue(ficha.isOrdemDoFerreiroPendente());
        assertFalse(ficha.isOrdemDoFerreiroPronta(), "no mesmo dia ainda não está pronta");
        ficha.avancarTempo(6); // vira para a noite e depois amanhece (dia seguinte)
        assertTrue(ficha.isOrdemDoFerreiroPronta());
    }
}

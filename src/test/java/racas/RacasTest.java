package racas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fichas.FichaRpg;

class RacasTest {

    // ==================== BÔNUS RACIAL ====================

    @Test
    void humanoSobreviveCom1Pv() {
        FichaRpg ficha = new FichaRpg("Teste");
        ficha.setNomePersonagem("Hero");
        ficha.setRaca(new HumanoRaca());
        ficha.setVidaPersonagem(10);
        ficha.receberDano(50);

        assertEquals(1, ficha.getVidaPersonagem(), "Vontade de Viver deve manter 1 PV na primeira queda");
        assertTrue(ficha.isSobrevivenciaUsada(), "Flag de Vontade de Viver deve ser marcada");

        // Segunda vez: a passiva não se repete no mesmo dia
        ficha.setVidaPersonagem(5);
        ficha.receberDano(50);
        assertEquals(0, ficha.getVidaPersonagem(), "Vontade de Viver só vale 1x por dia");
    }

    @Test
    void humanoBonusAtributo() {
        HumanoRaca raca = new HumanoRaca();
        int total = raca.getBonusConstituicao() + raca.getBonusDestreza() + raca.getBonusForca()
                + raca.getBonusSabedoria() + raca.getBonusIntelecto() + raca.getBonusPresenca();
        assertEquals(1, total, "Humano concede +1 em um atributo à escolha");
    }

    @Test
    void cadaRacaDaExatamenteUmBonusDeAtributo() {
        Raca[] racas = new Raca[] {
                new DraconicoRaca(), new ElfoDaFlorestaRaca(), new GnomoRaca(),
                new MeioFadaRaca(), new MeioOrqueRaca(), new VigiaDoCrepusculoRaca()
        };
        for (Raca raca : racas) {
            int total = raca.getBonusConstituicao() + raca.getBonusDestreza() + raca.getBonusForca()
                    + raca.getBonusSabedoria() + raca.getBonusIntelecto() + raca.getBonusPresenca();
            assertEquals(1, total, "Toda raça não-humana concede exatamente +1 racial (" + raca.getNome() + ")");
        }
    }

    // ==================== PASSIVAS ====================

    @Test
    void draconicoDaDefesaEVida() {
        DraconicoRaca raca = new DraconicoRaca();
        assertEquals(2, raca.getBonusDefesa(), "Escamas de Dragão concedem +2 de defesa");
        assertEquals(2, raca.getBonusVidaMaxima(), "Escamas de Dragão concedem +2 de vida máxima");
    }

    @Test
    void flagsPassivas() {
        assertTrue(new HumanoRaca().podeSobreviverCom1AoCair0());
        assertTrue(new ElfoDaFlorestaRaca().temBonusBuscaRecursos());
        assertTrue(new GnomoRaca().podeRerrolarTeste());
        assertTrue(new MeioFadaRaca().dobraChanceEncontrarFada());
        assertTrue(new MeioOrqueRaca().temBonusDanoVidaBaixa());
        assertTrue(new VigiaDoCrepusculoRaca().temBonusTestesNoturnos());
    }

    @Test
    void dracônicoAplicaBonusNaFicha() {
        FichaRpg ficha = new FichaRpg("Teste");
        ficha.setNomePersonagem("Hero");
        ficha.setClasse(new classes.Guerreiro());
        ficha.aplicarBonus();
        int vidaBase = ficha.getVidaMaxima();
        int defesaBase = ficha.getDefesa();

        ficha.setRaca(new DraconicoRaca());
        ficha.aplicarBonus();

        assertEquals(vidaBase + 2, ficha.getVidaMaxima(), "+2 de vida máxima do Dracônico");
        assertEquals(defesaBase + 2, ficha.getDefesa(), "+2 de defesa do Dracônico");
    }

    @Test
    void vigiaBonusNoturnoNosTestes() {
        FichaRpg ficha = new FichaRpg("Teste");
        ficha.setNomePersonagem("Hero");
        ficha.setClasse(new classes.Guerreiro());
        int saberBase = ficha.getSabedoriaTeste();

        ficha.setRaca(new VigiaDoCrepusculoRaca());
        ficha.aplicarBonus();
        // À noite, +2 (Visão na Penumbra) + +1 racial de sabedoria
        ficha.avancarTempo(3);
        assertEquals(saberBase + 1 + 2, ficha.getSabedoriaTeste(), "+1 racial e +2 noturno na Sabedoria");

        // De dia, sem o bônus noturno
        ficha.avancarTempo(3);
        assertEquals(saberBase + 1, ficha.getSabedoriaTeste(), "Sem bônus noturno de dia");
    }

    @Test
    void resetDiarioRenovaPassivas() {
        FichaRpg ficha = new FichaRpg("Teste");
        ficha.setNomePersonagem("Hero");
        ficha.setRaca(new HumanoRaca());
        ficha.setVidaPersonagem(5);
        ficha.receberDano(50);
        assertTrue(ficha.isSobrevivenciaUsada());

        ficha.avancarTempo(3); // vira o período: passivas diárias renovam
        assertFalse(ficha.isSobrevivenciaUsada(), "Avançar o período renova a Vontade de Viver");
    }
}
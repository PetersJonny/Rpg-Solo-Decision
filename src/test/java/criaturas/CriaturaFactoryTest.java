package criaturas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

class CriaturaFactoryTest {

    @Test
    void esqueletoComConfiguracaoCorreta() {
        Criatura e = CriaturaFactory.criarEsqueleto();
        assertEquals("Esqueleto", e.getNome());
        assertEquals(2, e.getNivel());
        assertEquals(12, e.getVida());
        assertEquals(12, e.getDefesa());
        assertEquals(4, e.getIniciativa());
        assertEquals(2, e.getBonusAcerto());
        assertEquals(15, e.getDcFuga());
        assertEquals(40, e.getXpGanho());
        assertEquals(2, e.getAtaques().size());
        Criatura.Ataque arco = e.getAtaques().get(0);
        assertEquals("Arco", arco.nome);
        assertEquals(1, arco.qtdDado);
        assertEquals(6, arco.ladosDado);
        Criatura.Ataque ossos = e.getAtaques().get(1);
        assertEquals("Ataque de Ossos", ossos.nome);
        assertEquals(1, ossos.qtdDado);
        assertEquals(4, ossos.ladosDado);
    }

    @Test
    void zumbiComConfiguracaoCorreta() {
        Criatura z = CriaturaFactory.criarZumbi();
        assertEquals("Zumbi", z.getNome());
        assertEquals(2, z.getNivel());
        assertEquals(18, z.getVida());
        assertEquals(10, z.getDefesa());
        assertEquals(2, z.getIniciativa());
        assertEquals(3, z.getBonusAcerto());
        assertEquals(10, z.getDcFuga());
        assertEquals(40, z.getXpGanho());
        assertEquals(1, z.getAtaques().size());
        assertEquals("Mordida", z.getAtaques().get(0).nome);
        assertEquals(1, z.getAtaques().get(0).qtdDado);
        assertEquals(6, z.getAtaques().get(0).ladosDado);
    }

    @Test
    void bauMonstruosoComConfiguracaoCorreta() {
        Criatura b = CriaturaFactory.criarBauMonstruoso();
        assertEquals("Baú Monstruoso", b.getNome());
        assertEquals(3, b.getNivel());
        assertEquals(25, b.getVida());
        assertEquals(10, b.getDefesa());
        assertEquals(4, b.getIniciativa());
        assertEquals(4, b.getBonusAcerto());
        assertEquals(12, b.getDcFuga());
        assertEquals(50, b.getXpGanho());
        assertEquals("Mordida", b.getAtaques().get(0).nome);
        assertEquals(1, b.getAtaques().get(0).qtdDado);
        assertEquals(8, b.getAtaques().get(0).ladosDado);
    }

    @Test
    void criarItemDropCobreOsNovosItens() {
        assertNotNull(Criatura.criarItemDrop("Osso"));
        assertNotNull(Criatura.criarItemDrop("Carne Podre"));
        assertNotNull(Criatura.criarItemDrop("Arco"));
        assertNotNull(Criatura.criarItemDrop("Flechas"));
        assertEquals("Arco", Criatura.criarItemDrop("Arco").getNome());
        assertEquals("Flechas", Criatura.criarItemDrop("Flechas").getNome());
    }

    @Test
    void infeccaoNaoVazaParaDepoisDoReset() {
        fichas.FichaRpg ficha = new fichas.FichaRpg("Teste");
        assertFalse(ficha.isInfectado());
        ficha.setInfectado(true);
        org.junit.jupiter.api.Assertions.assertTrue(ficha.isInfectado());
        ficha.resetarEfeitosCombate();
        assertFalse(ficha.isInfectado(), "Reset de combate deve limpar a infecção");
    }

    @Test
    void kitMedicoCuraInfeccao() {
        fichas.FichaRpg ficha = new fichas.FichaRpg("Teste");
        ficha.setInfectado(true);
        ficha.setVidaPersonagem(ficha.getVidaMaxima());
        itens.Consumivel kit = new itens.Consumivel("Kit Médico", "curar", 1);
        ficha.getInventario().add(kit);
        mecanicas.MotorDeCombate.usarItemForaDeCombate(ficha, kit, 1);
        assertFalse(ficha.isInfectado(), "Kit Médico deve curar a infecção");
        assertEquals(0, kit.getQuantidade());
        assertEquals(ficha.getVidaMaxima(), ficha.getVidaPersonagem());
    }
}
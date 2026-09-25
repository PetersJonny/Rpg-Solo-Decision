package criaturas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CriaturaFactoryTest {

    @BeforeAll
    static void antesDeTudo() {
        telas.Interface.modoTeste = true;
    }

    @AfterAll
    static void depoisDeTudo() {
        telas.Interface.modoTeste = false;
    }

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

    @Test
    void criaturasDoLabirintoSaoMortosVivos() {
        assertTrue(CriaturaFactory.criarEsqueleto().isMortoVivo());
        assertTrue(CriaturaFactory.criarZumbi().isMortoVivo());
        assertTrue(CriaturaFactory.criarMinotauro().isMortoVivo(), "Minotauro conta como morto-vivo");
        assertFalse(CriaturaFactory.criarBauMonstruoso().isMortoVivo(), "Baú Monstruoso não é morto-vivo");
        assertFalse(CriaturaFactory.criarLobo().isMortoVivo(), "Lobo é animal comum, não morto-vivo");
    }

    @Test
    void espadaMajestralEncantaCorretamente() {
        itens.Arma espada = new itens.Arma("Espada Majestral", "1d12 + 1d4 de luz, dobro contra mortos-vivos", "CaC", 12, 1, 1);
        assertEquals("Espada Majestral", espada.getNome());
        assertEquals(12, espada.getDadoDanoArma());
        assertEquals(1, espada.getQuantidadeDanoArma());
        assertTrue(espada.getTipoArma().contains("CaC"));
    }

    @Test
    void minotauroComConfiguracaoCorreta() {
        Criatura m = CriaturaFactory.criarMinotauro();
        assertEquals("Minotauro", m.getNome());
        assertEquals(5, m.getNivel());
        assertEquals(150, m.getVida());
        assertEquals(15, m.getDefesa());
        assertEquals(5, m.getIniciativa());
        assertEquals(4, m.getBonusAcerto());
        assertEquals(25, m.getDcFuga());
        assertEquals(500, m.getXpGanho());
        assertTrue(m.isSemFuga(), "A porta se fecha: não há como fugir do Minotauro");
        assertTrue(m.isDropDeClasse(), "Minotauro concede a recompensa exclusiva da classe");
        assertTrue(m.isMortoVivo(), "Minotauro conta como morto-vivo");
        assertEquals(2, m.getAtaques().size());
        assertEquals("Garras", m.getAtaques().get(0).nome);
        assertEquals(2, m.getAtaques().get(0).qtdDado);
        assertEquals(6, m.getAtaques().get(0).ladosDado);
        assertEquals("Chifre", m.getAtaques().get(1).nome);
        assertEquals(1, m.getAtaques().get(1).qtdDado);
        assertEquals(12, m.getAtaques().get(1).ladosDado);
    }

    @Test
    void criarItemDropCobreOsItensDoMinotauro() {
        itens.ItemRpg chifre = Criatura.criarItemDrop("Chifre de Minotauro");
        assertNotNull(chifre);
        assertEquals("Chifre de Minotauro", chifre.getNome());

        itens.Arma espada = (itens.Arma) Criatura.criarItemDrop("Espada do Minotauro");
        assertNotNull(espada);
        assertEquals("Espada do Minotauro", espada.getNome());
        assertEquals(10, espada.getDadoDanoArma());
        assertEquals(2, espada.getQuantidadeDanoArma());
        assertTrue(espada.getTipoArma().contains("CaC"));
        assertEquals("Força", espada.getAtributoAtaque());

        itens.Arma cajado = (itens.Arma) Criatura.criarItemDrop("Cajado de Sangue");
        assertNotNull(cajado);
        assertEquals("Cajado de Sangue", cajado.getNome());
        assertEquals(6, cajado.getDadoDanoArma());
        assertEquals(1, cajado.getQuantidadeDanoArma());
        assertTrue(cajado.getTipoArma().contains("CaC"));
    }

    @Test
    void minotauroConcedeRecompensaPorClasse() {
        // Guerreiro -> Espada do Minotauro
        fichas.FichaRpg guerreiro = new fichas.FichaRpg("G");
        guerreiro.setClasse(new classes.Guerreiro());
        CriaturaFactory.criarMinotauro().processarDrops(guerreiro);
        assertTrue(guerreiro.temItem("Espada do Minotauro"), "Guerreiro recebe a Espada do Minotauro");

        // Mago -> Cajado de Sangue
        fichas.FichaRpg mago = new fichas.FichaRpg("M");
        mago.setClasse(new classes.Mago("Fogo"));
        CriaturaFactory.criarMinotauro().processarDrops(mago);
        assertTrue(mago.temItem("Cajado de Sangue"), "Mago recebe o Cajado de Sangue");

        // Healer -> habilidade Curandeiro Combatente (sem item e sem duplicar)
        fichas.FichaRpg healer = new fichas.FichaRpg("H");
        healer.setClasse(new classes.Healer());
        CriaturaFactory.criarMinotauro().processarDrops(healer);
        assertFalse(healer.temItem("Espada do Minotauro"));
        assertEquals(1, contarHabilidade(healer, "Curandeiro Combatente"), "Healer desperta o Curandeiro Combatente");
        CriaturaFactory.criarMinotauro().processarDrops(healer);
        assertEquals(1, contarHabilidade(healer, "Curandeiro Combatente"), "A habilidade não pode duplicar");
    }

    private static int contarHabilidade(fichas.FichaRpg ficha, String nome) {
        int total = 0;
        for (habilidades.Habilidade h : ficha.getHabilidades()) {
            if (h.getNome().equals(nome)) total++;
        }
        return total;
    }
}
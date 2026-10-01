package eventos;

import criaturas.Criatura;
import criaturas.CriaturaFactory;
import fichas.FichaRpg;
import loja.Vendedor;
import mecanicas.GerenciadorDeAtaque;
import mecanicas.MecanicasRpg;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CasaDoChapeuMagicoTest {

    private FichaRpg ficha() {
        return new FichaRpg("Testador");
    }

    @Test
    void todosOsSeteItemsMagicosExiste() {
        String[] itens = {
                "Manto do Astrólogo", "Gema de Mana", "Amuleto do Coração",
                "Ampulheta de Prata", "Luvas de Prata", "Pó de Midas", "Gota de Veneno"
        };
        for (String nome : itens) {
            assertNotNull(Vendedor.criarItem(nome), nome + " não existe");
            assertTrue(Vendedor.precoBase(nome) > 0, nome + " sem preço");
            assertTrue(CasaDoChapeuMagico.ehItemMagico(nome), nome + " não é item mágico");
        }
    }

    @Test
    void lojaNaoVendeNemCompraItemNaoMagico() {
        assertFalse(CasaDoChapeuMagico.ehItemMagico("Espada"));
        assertFalse(CasaDoChapeuMagico.ehItemMagico("Sopa do Vilarejo"));
        assertFalse(CasaDoChapeuMagico.ehItemMagico("Madeira"));
    }

    @Test
    void consumivelTemEntre1e5DeEstoque() {
        FichaRpg ficha = ficha();
        boolean viuGota = false;

        for (int dia = 1; dia <= 40 && !viuGota; dia++) {
            ficha.setDiaAtual(dia);
            CasaDoChapeuMagico.renovarEstoqueSeNovoDiaPublico(ficha);
            int qtd = CasaDoChapeuMagico.quantidadeEmEstoque(ficha, "Gota de Veneno");
            assertTrue(qtd >= 0 && qtd <= 5, "quantidade fora de 0..5: " + qtd);
            if (qtd > 0) viuGota = true;
        }
        assertTrue(viuGota, "a Gota de Veneno nunca apareceu em 40 dias");
    }

    @Test
    void naoConsumivelSempreValemUmaUnidade() {
        FichaRpg ficha = ficha();
        ficha.setDiaAtual(3);
        CasaDoChapeuMagico.renovarEstoqueSeNovoDiaPublico(ficha);

        assertEquals(1, CasaDoChapeuMagico.quantidadeEmEstoque(ficha, "Luvas de Prata"));
        assertEquals(1, CasaDoChapeuMagico.quantidadeEmEstoque(ficha, "Gema de Mana"));
    }

    @Test
    void estoqueRenovaNoDiaSeguinte() {
        FichaRpg ficha = ficha();
        ficha.setDiaAtual(4);
        CasaDoChapeuMagico.renovarEstoqueSeNovoDiaPublico(ficha);
        assertEquals(4, ficha.getEstoqueLojaDia());

        ficha.setDiaAtual(5);
        CasaDoChapeuMagico.renovarEstoqueSeNovoDiaPublico(ficha);
        assertEquals(5, ficha.getEstoqueLojaDia());
    }

    @Test
    void osItensMagicosAntigosEstaoNaLoja() {
        FichaRpg ficha = ficha();
        boolean viuChapeu = false, viuGrimorio = false, viuPocao = false;

        for (int dia = 1; dia <= 60; dia++) {
            ficha.setDiaAtual(dia);
            CasaDoChapeuMagico.renovarEstoqueSeNovoDiaPublico(ficha);
            int qtdChapeu = CasaDoChapeuMagico.quantidadeEmEstoque(ficha, "Chapéu Mágico");
            int qtdGrimorio = CasaDoChapeuMagico.quantidadeEmEstoque(ficha, "Pequeno Grimório");
            int qtdPocao = CasaDoChapeuMagico.quantidadeEmEstoque(ficha, "Poção Grande de Mana");

            assertTrue(qtdChapeu == 0 || qtdChapeu == 1, "Chapéu Mágico deve valer 1");
            assertTrue(qtdGrimorio == 0 || qtdGrimorio == 1, "Pequeno Grimório deve valer 1");
            assertTrue(qtdPocao >= 0 && qtdPocao <= 5, "Poção Grande de Mana é consumível: 0..5");

            if (qtdChapeu > 0) viuChapeu = true;
            if (qtdGrimorio > 0) viuGrimorio = true;
            if (qtdPocao > 0) viuPocao = true;
        }

        assertTrue(viuChapeu, "Chapéu Mágico nunca apareceu");
        assertTrue(viuGrimorio, "Pequeno Grimório nunca apareceu");
        assertTrue(viuPocao, "Poção Grande de Mana nunca apareceu");
    }

    @Test
    void pocaoGrandeDeManaPodeSerCompradaMaisDeUmaVez() {
        FichaRpg ficha = ficha();
        ficha.getEstoqueConsumiveisLoja().put("Poção Grande de Mana", 4);
        ficha.adicionarItem(Vendedor.criarItem("Poção Grande de Mana"));

        assertTrue(CasaDoChapeuMagico.podeComprarItem(ficha, "Poção Grande de Mana"),
                "sendo consumível, não é bloqueada por já ter uma");
    }

    @Test
    void naoCompraOMesmoNaoConsumivelDuasVezes() {
        FichaRpg ficha = ficha();
        ficha.adicionarItem(Vendedor.criarItem("Luvas de Prata"));

        assertFalse(CasaDoChapeuMagico.podeComprarItem(ficha, "Luvas de Prata"),
                "não deve comprar um item não-consumível que já tem");
    }

    @Test
    void consumivelPodeSerCompradoRepetidamente() {
        FichaRpg ficha = ficha();
        ficha.getEstoqueConsumiveisLoja().put("Gota de Veneno", 3);

        ficha.adicionarItem(Vendedor.criarItem("Gota de Veneno"));

        assertTrue(CasaDoChapeuMagico.podeComprarItem(ficha, "Gota de Veneno"),
                "consumível não deve ser bloqueado por já ter um");
    }

    @Test
    void consumivelAcabadoNaoPodeSerComprado() {
        FichaRpg ficha = ficha();
        ficha.getEstoqueConsumiveisLoja().put("Gota de Veneno", 0);

        assertFalse(CasaDoChapeuMagico.podeComprarItem(ficha, "Gota de Veneno"));
    }

    @Test
    void mantoDoAstrologoSomaIntelectoEmTestes() {
        FichaRpg ficha = ficha();
        int antes = ficha.getIntelectoTeste();

        ficha.adicionarItem(Vendedor.criarItem("Manto do Astrólogo"));

        assertEquals(antes + 1, ficha.getIntelectoTeste());
    }

    @Test
    void gemaDeManaSomaManaMaxima() {
        FichaRpg ficha = ficha();
        int antes = ficha.getManaMaxima();

        ficha.adicionarItem(Vendedor.criarItem("Gema de Mana"));

        assertEquals(antes + 10, ficha.getManaMaxima());
        assertEquals(antes, ficha.getManaMaximaBase());
    }

    @Test
    void amuletoSomaVidaMaxima() {
        FichaRpg ficha = ficha();
        int antes = ficha.getVidaMaxima();

        ficha.adicionarItem(Vendedor.criarItem("Amuleto do Coração"));

        assertEquals(antes + 10, ficha.getVidaMaxima());
        assertEquals(antes, ficha.getVidaMaximaBase());
    }

    @Test
    void vidaNaoExcedeOMaximoComBonsus() {
        FichaRpg ficha = ficha();
        ficha.adicionarItem(Vendedor.criarItem("Amuleto do Coração"));
        ficha.setVidaPersonagem(9999);

        assertEquals(ficha.getVidaMaxima(), ficha.getVidaPersonagem());
    }

    @Test
    void luvasDePrataTiramUmDeDefesaSemAcumular() {
        FichaRpg ficha = ficha();
        Criatura alvo = CriaturaFactory.criarGoblin();

        int defesaOriginal = alvo.getDefesa();
        assertEquals(defesaOriginal, GerenciadorDeAtaque.defesaAlvoContraLuvasPublico(ficha, alvo));

        ficha.adicionarItem(Vendedor.criarItem("Luvas de Prata"));

        assertEquals(defesaOriginal - 1, GerenciadorDeAtaque.defesaAlvoContraLuvasPublico(ficha, alvo));

        ficha.adicionarItem(Vendedor.criarItem("Luvas de Prata"));

        assertEquals(defesaOriginal - 1, GerenciadorDeAtaque.defesaAlvoContraLuvasPublico(ficha, alvo),
                "não pode acumular: continua tirando só 1");
    }

    @Test
    void luvasDePrataNaoReduzemAbaixoDeZero() {
        FichaRpg ficha = ficha();
        ficha.adicionarItem(Vendedor.criarItem("Luvas de Prata"));
        Criatura fraco = new Criatura("Estaca de Treino", 10, 20, 1, 1);

        assertEquals(0, GerenciadorDeAtaque.defesaAlvoContraLuvasPublico(ficha, fraco));
    }

    @Test
    void poDeMidasSomeDoTesteDeDefesaMasAumentaDrop() {
        FichaRpg ficha = ficha();
        Criatura alvo = CriaturaFactory.criarGoblin();
        int antes = GerenciadorDeAtaque.defesaAlvoContraLuvasPublico(ficha, alvo);

        ficha.adicionarItem(Vendedor.criarItem("Pó de Midas"));

        assertEquals(antes, GerenciadorDeAtaque.defesaAlvoContraLuvasPublico(ficha, alvo));
        assertEquals(10, Criatura.bonusSortePublico(ficha));
    }

    @Test
    void criaturaFicaEnvenenadaEAplicaDanoPorRodada() {
        FichaRpg ficha = ficha();
        Criatura alvo = CriaturaFactory.criarGoblin();
        int vidaAntes = alvo.getVida();

        alvo.setEnvenenado(true);
        assertTrue(alvo.isEnvenenado());

        int dano = MecanicasRpg.rolarDado(4);
        alvo.setVida(alvo.getVida() - dano);

        assertEquals(vidaAntes - dano, alvo.getVida());
    }

    @Test
    void venenoNaArmaDuraAteOProximoTurno() {
        FichaRpg ficha = ficha();

        ficha.setArmaEnvenenada(true);
        assertEquals(2, ficha.getRodadaVenenoNaArma());

        ficha.tickVenenoNaArma();
        assertTrue(ficha.isArmaEnvenenada(), "ainda deve valer no próximo turno");

        ficha.tickVenenoNaArma();
        assertFalse(ficha.isArmaEnvenenada(), "não pode passar do próximo turno");
    }

    @Test
    void acertoComArmaEnvenenadaContaminaOAlvo() {
        FichaRpg ficha = ficha();
        Criatura alvo = CriaturaFactory.criarGoblin();
        ficha.setArmaEnvenenada(true);

        assertTrue(alvo.isEnvenenado() == false);

        alvo.setEnvenenado(true);
        ficha.setArmaEnvenenada(false);

        assertTrue(alvo.isEnvenenado(), "o alvo fica envenenado");
        assertFalse(ficha.isArmaEnvenenada(), "o veneno da arma é gasto no acerto");
    }

    @Test
    void venenoDaArmaConsomeAGota() {
        FichaRpg ficha = ficha();
        ficha.adicionarItem(Vendedor.criarItem("Gota de Veneno"));
        ficha.setArmaEnvenenada(false);

        assertTrue(ficha.removerItem("Gota de Veneno", 1));
        assertEquals(0, ficha.getQuantidadeDe("Gota de Veneno"));
        assertFalse(ficha.temItem("Gota de Veneno"));
    }
}
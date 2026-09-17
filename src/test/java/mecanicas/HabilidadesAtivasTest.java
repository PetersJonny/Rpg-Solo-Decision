package mecanicas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import fichas.FichaRpg;
import criaturas.Criatura;
import itens.Consumivel;
import habilidades.ativas.HabilidadeEstrondo;
import habilidades.ativas.HabilidadePrisao;
import habilidades.ativas.HabilidadeProtecaoAbsoluta;
import habilidades.Magia;
import mecanicas.MecanicasRpg;
import java.util.ArrayList;
import java.util.List;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import telas.Interface;


class HabilidadesAtivasTest {

    private FichaRpg ficha;
    private List<Criatura> inimigos;

    @BeforeEach

    void setUp() {
        ficha = new FichaRpg("Tester");
        ficha.adicionarAtributo(1, 10); // Força 10 para o estrondo dar dano fixo se os dados derem baixo
        ficha.setManaMaxima(20);
        ficha.setManaPersonagem(20);
        
        inimigos = new ArrayList<>();
        inimigos.add(new Criatura("Goblin", 10, 5, 2, 1));
        inimigos.add(new Criatura("Lobo", 15, 6, 4, 1));
    }

    @Test
    void testHabilidadeEstrondoAcertaTodosEAplicaDebuff() {
        HabilidadeEstrondo estrondo = new HabilidadeEstrondo("Estrondo", "...", 5);
        
        // Assegurar que os inimigos estão de vida cheia
        assertEquals(5, inimigos.get(0).getVida());
        assertEquals(6, inimigos.get(1).getVida());
        assertEquals(0, ficha.getRodadasSemHabilidade());
        

        boolean executou = false;
        try (MockedStatic<Interface> mocked = Mockito.mockStatic(Interface.class)) {
            mocked.when(Interface::pressionarParaRolar).thenAnswer(i -> null);
            mocked.when(() -> Interface.Pausa(Mockito.anyInt())).thenAnswer(i -> null);
            mocked.when(() -> Interface.MostrarMensagem(Mockito.anyString())).thenAnswer(i -> null);
            
            executou = estrondo.executar(ficha, inimigos, 0);
        }

        
        assertTrue(executou, "A habilidade deve retornar true após ser executada");
        
        // Verifica se a mana reduziu
        assertEquals(15, ficha.getManaPersonagem());
        
        // Verifica punição de rodadas sem habilidade (2)
        assertEquals(2, ficha.getRodadasSemHabilidade(), "O jogador não pode usar habilidades por 2 rodadas");
        
        // O dano de estrondo é 7d10 + forca (10). O dano mínimo é 17. 
        // Goblin tem 10 de vida, Lobo tem 15. Ambos devem estar mortos.
        assertTrue(inimigos.get(0).getVida() <= 0, "Goblin deve tomar dano em área");
        assertTrue(inimigos.get(1).getVida() <= 0, "Lobo deve tomar dano em área");
    }

    @Test
    void testHabilidadePrisaoImobilizaAlvo() {
        HabilidadePrisao prisao = new HabilidadePrisao("Prisão", "...", 5);
        
        // Ninguém está preso no início
        assertNull(ficha.getPrisaoAtiva(), "Ninguém deve estar preso inicialmente");
        
        // Prende o lobo (índice 1)

        try (MockedStatic<Interface> mocked = Mockito.mockStatic(Interface.class)) {
            mocked.when(() -> Interface.Pausa(Mockito.anyInt())).thenAnswer(i -> null);
            mocked.when(() -> Interface.MostrarMensagem(Mockito.anyString())).thenAnswer(i -> null);
            
            prisao.executar(ficha, inimigos, 1);
        }

        
        assertEquals(15, ficha.getManaPersonagem());
        assertNotNull(ficha.getPrisaoAtiva());
        assertEquals("Lobo", ficha.getPrisaoAtiva().getNome(), "A flag de prisão deve apontar para o Lobo");
    }

    @Test
    void testPequenoGrimorioReduzCustoDeMagia() {
        Magia magiaTeste = new Magia("Fogo", "...", 5, 2, 8);
        
        // Com o item, o custo deve ser reduzido de 5 para 4.
        ficha.getInventario().add(new itens.ItemRpg("Pequeno Grimório", "Reduz o custo das magias em 1.", 1));
        
        // O custo efetivo é calculado por MotorDeCombate.custoEfetivoMagia
        int custoReal = MotorDeCombate.custoEfetivoMagia(ficha, magiaTeste);
        
        assertEquals(4, custoReal, "O Pequeno Grimório deve reduzir o custo de 5 para 4");
    }
    
    @Test
    void testPequenoGrimorioNaoZeraCusto() {
        Magia magiaPequena = new Magia("Faísca", "...", 1, 1, 4);
        ficha.getInventario().add(new itens.ItemRpg("Pequeno Grimório", "Reduz o custo em 1", 1));
        
        int custoReal = MotorDeCombate.custoEfetivoMagia(ficha, magiaPequena);
        
        assertEquals(1, custoReal, "O Pequeno Grimório não deve reduzir o custo para 0 (min 1 para magias que custam algo)");
    }

    @Test
    void testMagiaDescontaManaDoJogador() {
        Magia magia = new Magia("Bola Elementar", "...", 3, 2, 8);

        try (MockedStatic<Interface> mocked = Mockito.mockStatic(Interface.class)) {
            mocked.when(Interface::pressionarParaRolar).thenAnswer(i -> null);
            mocked.when(() -> Interface.Pausa(Mockito.anyInt())).thenAnswer(i -> null);
            mocked.when(() -> Interface.MostrarMensagem(Mockito.anyString())).thenAnswer(i -> null);

            magia.executar(ficha, inimigos, 0);
        }

        assertEquals(17, ficha.getManaPersonagem(), "Lançar a magia deve custar 3 de mana (20 -> 17)");
    }

    @Test
    void testMagiaAcertaDiretoSemBonusDeIntelectoOuSemiDeus() {
        ficha.adicionarAtributo(5, 10); // Intelecto 10
        ficha.setSemiDeusAtivo(true);

        Magia magia = new Magia("Bola Elementar", "...", 3, 2, 8);

        try (MockedStatic<Interface> mocked = Mockito.mockStatic(Interface.class);
             MockedStatic<MecanicasRpg> dados = Mockito.mockStatic(MecanicasRpg.class)) {
            mocked.when(Interface::pressionarParaRolar).thenAnswer(i -> null);
            mocked.when(() -> Interface.Pausa(Mockito.anyInt())).thenAnswer(i -> null);
            mocked.when(() -> Interface.MostrarMensagem(Mockito.anyString())).thenAnswer(i -> null);
            dados.when(() -> MecanicasRpg.rolarDado(Mockito.anyInt())).thenReturn(4);

            magia.executar(ficha, inimigos, 0);
        }

        // 2d8 = 8 de dano direto, sem rolagem de defesa e sem bônus de Intelecto/Semi Deus.
        // Goblin (vida 5) - 8 = -3.
        assertEquals(-3, inimigos.get(0).getVida(), "Goblin (vida 5) deve receber exatamente 8 de dano direto");
    }
}

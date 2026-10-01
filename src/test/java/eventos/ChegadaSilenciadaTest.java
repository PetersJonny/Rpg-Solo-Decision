package eventos;

import fichas.FichaRpg;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChegadaSilenciadaTest {

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
        ficha.setCidadeAtual(null);
        return ficha;
    }

    @Test
    void primeiraChegadaComViradaSilenciaATaverna() {
        assertTrue(VilarejoDeScarbor.chegadaSilenciaTaverna(fichaNova(), true));
    }

    @Test
    void primeiraChegadaSemViradaNaoSilencia() {
        assertFalse(VilarejoDeScarbor.chegadaSilenciaTaverna(fichaNova(), false));
    }

    @Test
    void segundaChegadaNuncaSilencia() {
        FichaRpg ficha = fichaNova();
        ficha.setCidadeAtual("Scarbor");

        assertFalse(VilarejoDeScarbor.chegadaSilenciaTaverna(ficha, true),
                "a regra vale so na primeira chegada a vila");
    }

    @Test
    void chegadaSilenciadaLiberaATavernaSemFalarComODraven() {
        FichaRpg ficha = fichaNova();

        VilarejoDeScarbor.ChegadaSilenciadaPorViradaDeTempo(ficha);

        assertTrue(ficha.isGoblinsResolvido(), "a taverna nao pode ficar trancada pelos goblins");
        assertTrue(ficha.isDonoDaTavernaAgradeceu(), "a cena do Draven nao pode rodar");
    }

    @Test
    void chegadaNormalNaoLiberaNemUmNemOutro() {
        FichaRpg ficha = fichaNova();

        assertFalse(ficha.isGoblinsResolvido());
    }

    @Test
    void confusaoDaTavernaSoFicaPendenteUmaVez() {
        FichaRpg ficha = fichaNova();

        assertTrue(VilarejoDeScarbor.tavernaTemConfusaoPendente(ficha),
                "antes de resolver, a confusao esta pendente");

        ficha.setGoblinsResolvido(true);

        assertFalse(VilarejoDeScarbor.tavernaTemConfusaoPendente(ficha),
                "depois de resolver, a confusao nao pode voltar a ser narrada");
    }

    @Test
    void chegarDeNovoNaVilaNaoRepeteOAssalto() {
        FichaRpg ficha = fichaNova();
        ficha.setCidadeAtual("Vilarejo de Scarbor");
        ficha.setGoblinsResolvido(true);
        int vidaAntes = ficha.getVidaPersonagem();

        VilarejoDeScarbor.ObservarCidade(ficha);

        assertFalse(ficha.isEmCombate(), "chegar de novo na vila nao pode iniciar combate de goblins");
        assertEquals(vidaAntes, ficha.getVidaPersonagem(), "o jogador nao pode tomar dano ao chegar de novo");
        assertFalse(ficha.isDonoDaTavernaAgradeceu(),
                "a taverna ja resolvida nao pode rodar a cena do Draven na chegada");
    }

    @Test
    void chegadaSilenciadaTambemNaoRepeteAtaqueGoblin() {
        FichaRpg ficha = fichaNova();

        VilarejoDeScarbor.ChegadaSilenciadaPorViradaDeTempo(ficha);
        ficha.setCidadeAtual("Vilarejo de Scarbor");
        VilarejoDeScarbor.ObservarCidade(ficha);

        assertFalse(ficha.isEmCombate());
    }
}
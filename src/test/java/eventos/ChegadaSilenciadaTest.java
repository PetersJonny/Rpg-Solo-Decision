package eventos;

import fichas.FichaRpg;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChegadaSilenciadaTest {

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
        assertFalse(ficha.isDonoDaTavernaAgradeceu());
    }
}
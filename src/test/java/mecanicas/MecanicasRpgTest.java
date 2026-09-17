package mecanicas;

import org.junit.jupiter.api.Test;
import fichas.FichaRpg;
import itens.Arma;
import classes.Guerreiro;

import static org.junit.jupiter.api.Assertions.*;

class MecanicasRpgTest {

    @Test
    void testRolarDado() {
        for (int i = 0; i < 100; i++) {
            int resultado = MecanicasRpg.rolarDado(6);
            assertTrue(resultado >= 1 && resultado <= 6);
        }
    }

    @Test
    void testRolarEntre() {
        for (int i = 0; i < 100; i++) {
            int resultado = MecanicasRpg.rolarEntre(10, 20);
            assertTrue(resultado >= 10 && resultado <= 20);
        }
    }

    @Test
    void testRolarIniciativa() {
        FichaRpg ficha = new FichaRpg("Teste");
        ficha.adicionarAtributo(2, 5); // Destreza +5
        ficha.setClasse(new Guerreiro()); // Aplica bônus

        int iniciativa = MecanicasRpg.rolarIniciativa(ficha);
        
        // Dado d20 + destreza (5 + bonus classe)
        int destrezaFinal = ficha.getDestreza();
        assertTrue(iniciativa >= 1 + destrezaFinal && iniciativa <= 20 + destrezaFinal);
    }

    @Test
    void testRolarDanoFisico() {
        FichaRpg ficha = new FichaRpg("Teste");
        ficha.adicionarAtributo(3, 3); // Força +3
        ficha.setClasse(new Guerreiro());
        
        Arma espada = new Arma("Espada Curta", "Dano base", "CaC", 6, 1, 1, "Força");
        
        int dano = MecanicasRpg.rolarDanoFisico(ficha, espada);
        
        // 1d6 + Força
        int forcaFinal = ficha.getForca();
        assertTrue(dano >= 1 + forcaFinal && dano <= 6 + forcaFinal);
    }
}

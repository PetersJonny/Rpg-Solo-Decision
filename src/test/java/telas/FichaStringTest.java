package telas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fichas.FichaRpg;
import salvamento.GerenciadorSaves;
import classes.Mago;

class FichaStringTest {

    @Test
    void testCriacaoComCaracteresEspeciais() {
        FichaRpg ficha = new FichaRpg("Jogador1");
        
        // Strings longas ou com unicode
        String nomeComplexo = "Herói ۞ \n \r \t <html>";
        ficha.setNomePersonagem(nomeComplexo);
        assertEquals(nomeComplexo, ficha.getNomePersonagem());
        
        // Verifica se a deleção funciona mesmo com nomes estranhos
        ficha.setClasse(new Mago("Gelo"));
        
        GerenciadorSaves.salvar(ficha, 1);
        int apagados = GerenciadorSaves.deletarSavesDoPersonagem(nomeComplexo);
        assertEquals(1, apagados, "Deve conseguir deletar mesmo com caracteres complexos no nome");
    }

    @Test
    void testTratamentoDeNomeNulo() {
        FichaRpg ficha = new FichaRpg("Jogador1");
        ficha.setNomePersonagem(null);
        
        GerenciadorSaves.salvar(ficha, 2);
        
        // A busca por um nome não deve quebrar (NullPointerException)
        // se a ficha carregada tem o nome do personagem "null".
        int apagados = 0;
        try {
            apagados = GerenciadorSaves.deletarSavesDoPersonagem("Heroi");
        } catch (NullPointerException e) {
            fail("GerenciadorSaves não deve lançar NullPointerException ao comparar nome nulo do save.");
        }
        
        assertEquals(0, apagados);
        GerenciadorSaves.deletar(2); // Limpar o slot
    }
}

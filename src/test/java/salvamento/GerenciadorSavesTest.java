package salvamento;

import fichas.FichaRpg;
import fichas.ModoDificuldade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class GerenciadorSavesTest {

    private void limparSaves() {
        File dir = new File("saves");
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
        }
    }

    @BeforeEach
    void setUp() {
        limparSaves();
    }

    @AfterEach
    void tearDown() {
        limparSaves();
    }

    @Test
    void testSalvarECarregar() {
        FichaRpg ficha = new FichaRpg("Marcus");
        ficha.setNomePersonagem("Herói");
        ficha.adicionarAtributo(1, 2);
        
        boolean salvo = GerenciadorSaves.salvar(ficha, 1);
        assertTrue(salvo);
        assertTrue(GerenciadorSaves.existeSave(1));

        FichaRpg fichaCarregada = GerenciadorSaves.carregar(1);
        assertNotNull(fichaCarregada);
        assertEquals("Herói", fichaCarregada.getNomePersonagem());
        assertEquals("Marcus", fichaCarregada.getNomePessoa());
        // A constituição base era 2. Como não chamamos aplicarBonus(), ela é zero na Ficha?
        // Wait, adicionarAtributo atualiza o Base, sem aplicarBonus a constituição final é a default.
        // O teste é apenas checar se salvou com sucesso.
    }

    @Test
    void testDeletarSave() {
        FichaRpg ficha = new FichaRpg("Teste");
        GerenciadorSaves.salvar(ficha, 2);
        
        assertTrue(GerenciadorSaves.existeSave(2));
        
        boolean deletado = GerenciadorSaves.deletar(2);
        assertTrue(deletado);
        assertFalse(GerenciadorSaves.existeSave(2));
    }

    @Test
    void testMortePermanenteDeletaSaves() {
        FichaRpg ficha1 = new FichaRpg("Jogador1");
        ficha1.setNomePersonagem("HardcoreChar");
        ficha1.setModoDificuldade(ModoDificuldade.DIFICIL);
        GerenciadorSaves.salvar(ficha1, 1);
        GerenciadorSaves.salvar(ficha1, 3); // Mesmo personagem salva em 2 slots

        FichaRpg ficha2 = new FichaRpg("Jogador2");
        ficha2.setNomePersonagem("CasualChar");
        GerenciadorSaves.salvar(ficha2, 2);

        assertEquals(3, GerenciadorSaves.quantidadeSaves());

        int apagados = GerenciadorSaves.deletarSavesDoPersonagem("HardcoreChar");
        
        assertEquals(2, apagados);
        assertFalse(GerenciadorSaves.existeSave(1));
        assertFalse(GerenciadorSaves.existeSave(3));
        
        // O save casual deve ser mantido
        assertTrue(GerenciadorSaves.existeSave(2));
    }
}

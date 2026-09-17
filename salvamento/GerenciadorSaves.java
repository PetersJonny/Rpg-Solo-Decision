package salvamento;

import fichas.FichaRpg;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// Sistema de save em arquivos: até 3 slots, guardados na pasta "saves".
// Cada save serializa a FichaRpg (que já carrega inventário, habilidades,
// companheiro, progresso da cabana e do tempo).
public class GerenciadorSaves {

    public static final int MAX_SAVES = 3;
    private static final String PASTA = "saves";

    private GerenciadorSaves() {}

    // Salva a ficha no slot informado (slot de 1 a 3). Retorna false se falhar.
    public static boolean salvar(FichaRpg ficha, int slot) {
        if (ficha == null || slot < 1 || slot > MAX_SAVES) return false;
        File dir = new File(PASTA);
        if (!dir.exists() && !dir.mkdirs()) return false;
        File arquivo = new File(dir, nomeArquivo(slot));
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            out.writeObject(ficha);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Carrega a ficha do slot informado; retorna null se o save não existir ou estiver corrompido.
    public static FichaRpg carregar(int slot) {
        if (slot < 1 || slot > MAX_SAVES) return null;
        File arquivo = new File(new File(PASTA), nomeArquivo(slot));
        if (!arquivo.exists()) return null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (FichaRpg) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean existeSave(int slot) {
        if (slot < 1 || slot > MAX_SAVES) return false;
        return new File(new File(PASTA), nomeArquivo(slot)).exists();
    }

    // Quantos slots estão ocupados (usado para saber se há algo a carregar)
    public static int quantidadeSaves() {
        int total = 0;
        for (int slot = 1; slot <= MAX_SAVES; slot++) {
            if (existeSave(slot)) total++;
        }
        return total;
    }

    // Descrição do slot para os menus: mostra o personagem ou "Vazio".
    public static String infoSlot(int slot) {
        if (slot < 1 || slot > MAX_SAVES) return "";
        File arquivo = new File(new File(PASTA), nomeArquivo(slot));
        if (!arquivo.exists()) {
            return "Vazio";
        }
        FichaRpg ficha = carregar(slot);
        if (ficha == null) {
            return "Corrompido";
        }
        String classe = ficha.getClasseDoPersonagem() != null ? ficha.getClasseDoPersonagem().getNome() : "Sem classe";
        String periodo = ficha.isEhNoite() ? "Noite" : "Dia";
        String data = new SimpleDateFormat("dd/MM HH:mm").format(new Date(arquivo.lastModified()));
        return ficha.getNomePersonagem() + " (" + classe + ", Nível " + ficha.getNivel() + ") | "
                + periodo + " " + (3 - ficha.getProgressoPeriodo()) + "/3 | Salvo em " + data;
    }

    public static boolean deletar(int slot) {
        if (slot < 1 || slot > MAX_SAVES) return false;
        File arquivo = new File(new File(PASTA), nomeArquivo(slot));
        return arquivo.exists() && arquivo.delete();
    }

    private static String nomeArquivo(int slot) {
        return "save" + slot + ".dat";
    }
}
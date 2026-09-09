package narrativa;

import telas.Interface;
import fichas.FichaRpg;

public class Aventura {

    public static void IniciarPrologo(FichaRpg ficha) {
        Interface.barraDivisoria();
        System.out.println("\n");
        
        Narrar("No mundo gélido de Freijord, uma terra implacável moldada pelas antigas forças da magia...", 6000);
        Narrar("Inúmeras lendas aguardam aqueles corajosos o suficiente para forjá-las. Porém, a glória cobra o seu preço.", 6500);
        Narrar("Apenas os verdadeiramente fortes, movidos por uma inabalável vontade de viver, conseguem suportar os perigos que espreitam nas nevascas.", 7000);
        Narrar("E você, " + ficha.getNomePersonagem() + ", tomou a sua decisão...", 5500);
        Narrar("Deixar o conforto para trás e buscar o seu próprio destino como aventureiro, arriscando sua vida através de missões.", 7000);
        
        System.out.println("\n");
        Interface.barraDivisoria();
        
        System.out.println("\n(Pressione ENTER para continuar...)");
        Interface.scanner.nextLine();
    }

    private static void Narrar(String texto, int atrasoMilisegundos) {
        System.out.println(texto);
        if (atrasoMilisegundos > 0) {
            try {
                Thread.sleep(atrasoMilisegundos);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

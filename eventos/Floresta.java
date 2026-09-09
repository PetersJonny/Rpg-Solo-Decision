package eventos;

import fichas.FichaRpg;
import criaturas.Criatura;
import telas.Interface;
import mecanicas.MecanicasRpg;

public class Floresta {
    
    public static void Explorar(FichaRpg ficha) {
        Interface.barraDivisoria();
        Interface.MostrarMensagem("\nVocê adentra as matas geladas da floresta de Freijord...");
        
        // Sorteia um evento. Por enquanto, sempre será achar o animal.
        EventoAnimal(ficha);
    }
    
    private static void EventoAnimal(FichaRpg ficha) {
        // Lobo Selvagem agora tem 12 de Iniciativa (como bônus fixo para rolagem)
        Criatura animal = new Criatura("Lobo Selvagem", 1, 15, 10, 4, 1, 12);
        
        Interface.MostrarMensagem("\nAlgo se move por entre as árvores...");
        
        int dadoPresenca = MecanicasRpg.rolarDado(20);
        int totalPresenca = dadoPresenca + ficha.getPresenca();
        
        Interface.MostrarMensagem("-> Teste de Presença: " + dadoPresenca + " (Dado) + " + ficha.getPresenca() + " (Atributo) = " + totalPresenca);
        
        if (totalPresenca > 8 && animal.getNivel() <= 2) {
            Interface.MostrarMensagem("\nVocê conseguiu avistar um " + animal.getNome() + " (Nível " + animal.getNivel() + ") antes que ele o visse.");
            
            System.out.println("O que deseja fazer?");
            System.out.println("1. Lutar (Você terá +2 de Iniciativa extra por surpreendê-lo)");
            System.out.println("2. Tentar Fugir furtivamente");
            int escolha = Interface.scanner.nextInt();
            Interface.scanner.nextLine();
            
            if (escolha == 1) {
                Interface.MostrarMensagem("\nVocê saca sua arma e parte para cima do " + animal.getNome() + "!");
                IniciarCombate(ficha, animal, true);
            } else if (escolha == 2) {
                int dadoDestreza = MecanicasRpg.rolarDado(20);
                int totalDestreza = dadoDestreza + ficha.getDestreza();
                Interface.MostrarMensagem("-> Teste de Destreza (Fuga): " + dadoDestreza + " (Dado) + " + ficha.getDestreza() + " (Atributo) = " + totalDestreza);
                
                if (totalDestreza >= 12) {
                    Interface.MostrarMensagem("\nVocê recua lentamente pelas sombras e foge com sucesso, sem ser notado.");
                } else {
                    Interface.MostrarMensagem("\nVocê pisa em um galho seco! O " + animal.getNome() + " percebe você e avança!");
                    IniciarCombate(ficha, animal, false);
                }
            } else {
                Interface.ExibirErro("Escolha inválida! Você hesitou e a criatura te notou!");
                IniciarCombate(ficha, animal, false);
            }
            
        } else {
            Interface.MostrarMensagem("\nUm " + animal.getNome() + " (Nível " + animal.getNivel() + ") saltou das sombras e te surpreendeu!");
            IniciarCombate(ficha, animal, false);
        }
    }

    private static void IniciarCombate(FichaRpg ficha, Criatura inimigo, boolean jogadorSurpreendeu) {
        Interface.MostrarMensagem("\n================ COMBATE ================");
        
        int bonusIniciativaJogador = jogadorSurpreendeu ? 2 : 0;
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int iniciativaJogador = dadoJogador + ficha.getDestreza() + bonusIniciativaJogador;
        
        int dadoInimigo = MecanicasRpg.rolarDado(20);
        int iniciativaInimigo = dadoInimigo + inimigo.getIniciativa();
        
        Interface.MostrarMensagem("-> Iniciativa [" + ficha.getNomePersonagem() + "]: " + dadoJogador + " (Dado) + " + ficha.getDestreza() + " (Destreza) + " + bonusIniciativaJogador + " (Bônus) = " + iniciativaJogador);
        Interface.MostrarMensagem("-> Iniciativa [" + inimigo.getNome() + "]: " + dadoInimigo + " (Dado) + " + inimigo.getIniciativa() + " (Iniciativa Base) = " + iniciativaInimigo);
        
        if (iniciativaJogador >= iniciativaInimigo) {
            Interface.MostrarMensagem("\n" + ficha.getNomePersonagem() + " é mais ágil e age primeiro!");
        } else {
            Interface.MostrarMensagem("\nO " + inimigo.getNome() + " avança furiosamente e age primeiro!");
        }
        
        Interface.MostrarMensagem("\n(Em breve: Rodadas de Combate...)");
        Interface.barraDivisoria();
    }
}

import classes.*;
import fichas.FichaRpg;
import telas.Interface;

public class Main {
    public static void main(String[] args) {
        
        Interface.BarraCarregamento("Carregando jogo...");
        Interface.ExibirBoasVindas();

        // Inicialização
        String nomePessoa = Interface.PedirNomeJogador();
        FichaRpg ficha = new FichaRpg(nomePessoa);

        // Fase de Criação de Personagem
        boolean criandoFicha = true;

        while (criandoFicha) {
            int escolhaInterface = Interface.MenuCriacaoFicha();

            switch (escolhaInterface) {
                case 1:
                    String nomePersonagem = Interface.PedirNomePersonagem();
                    ficha.setNomePersonagem(nomePersonagem);
                    break;
                
                case 2:
                    ficha.resetarPontosBase();
                    int totalDePontos = 6;

                    while (totalDePontos > 0) {
                        int atributoEscolhido = Interface.MenuDistribuirAtributos(totalDePontos);
                        int gastoDePontos = Interface.PedirQuantidadePontos(totalDePontos);

                        if (gastoDePontos <= 0) {
                            Interface.ExibirErro("Por favor, insira um valor maior que zero!");
                            continue;
                        }

                        if (gastoDePontos > totalDePontos) {
                            gastoDePontos = totalDePontos;
                        }

                        if (atributoEscolhido >= 1 && atributoEscolhido <= 6) {
                            ficha.adicionarAtributo(atributoEscolhido, gastoDePontos);
                            totalDePontos -= gastoDePontos;
                        } else {
                            Interface.ExibirErro("Opção inválida!");
                        }
                    }
                    ficha.aplicarBonus();
                    break;
                
                case 3:
                    int escolhaClasse = Interface.MenuEscolherClasse();
                    switch (escolhaClasse) {
                        case 1: ficha.setClasse(new Mago()); break;
                        case 2: ficha.setClasse(new Guerreiro()); break;
                        case 3: ficha.setClasse(new Healer()); break;
                        default: Interface.ExibirErro("Opção inválida!");
                    }
                    break;
                
                case 4:
                    Interface.MostrarFicha(ficha);
                    break;
                
                case 5:
                    if (ficha.isFichaCompleta()) {
                        criandoFicha = false;
                    } else {
                        Interface.ExibirErro("Ficha incompleta! Preencha seu Nome, distribua os 6 Atributos e escolha sua Classe.");
                    }
                    break;
                
                default:
                    Interface.ExibirErro("Opção inválida!");
                    break;
            }
        }
        
        Interface.MostrarMensagem("\nA criação da ficha foi finalizada com sucesso!");
        
        // Fase da Aventura
        boolean jogando = true;

        while (jogando) {
            int escolhaAventura = Interface.MenuPrincipalAventura();

            switch (escolhaAventura) {
                case 1:
                    Interface.MostrarFicha(ficha);
                    break;
                case 2:
                    Interface.MostrarMensagem("\nA aventura está prestes a começar... (Em breve)");
                    break;
                case 3:
                    Interface.MostrarMensagem("\nEncerrando o jogo... Até a próxima aventura!");
                    jogando = false;
                    break;
                default:
                    Interface.ExibirErro("Opção inválida!");
                    break;
            }
        }
    }
}

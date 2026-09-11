import classes.*;
import fichas.FichaRpg;
import telas.Interface;

public class Main {
    public static void main(String[] args) {
        
        Interface.BarraCarregamento("Carregando jogo...");
        Interface.ExibirBoasVindas();

        boolean jogoAberto = true;

        while (jogoAberto) {
            // Inicialização
            String nomePessoa = Interface.PedirNomeJogador();
            FichaRpg ficha = new FichaRpg(nomePessoa);

            // Fase de Criação de Personagem
            boolean criandoFicha = true;

            while (criandoFicha) {
                Interface.limparTela();
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

                            if (atributoEscolhido == 0) break;

                            if (atributoEscolhido < 1 || atributoEscolhido > 6) {
                                Interface.ExibirErro("Opção inválida!");
                                continue;
                            }

                            int gastoDePontos = Interface.PedirQuantidadePontos(totalDePontos);

                            if (gastoDePontos <= 0) {
                                Interface.ExibirErro("Por favor, insira um valor maior que zero!");
                                continue;
                            }

                            if (gastoDePontos > totalDePontos) {
                                gastoDePontos = totalDePontos;
                            }

                            ficha.adicionarAtributo(atributoEscolhido, gastoDePontos);
                            totalDePontos -= gastoDePontos;
                        }
                        ficha.aplicarBonus();
                        break;
                    
                    case 3:
                        int escolhaClasse = Interface.MenuEscolherClasse();
                        switch (escolhaClasse) {
                            case 0: break;
                            case 1: 
                                String elemento = Interface.EscolherElementoMago();
                                if (elemento != null) ficha.setClasse(new Mago(elemento)); 
                                break;
                            case 2: 
                                ficha.setClasse(new Guerreiro()); 
                                break;
                            case 3: 
                                ficha.setClasse(new Healer()); 
                                break;
                            default: 
                                Interface.ExibirErro("Opção inválida!");
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
                    
                    case 6:
                        jogoAberto = false;
                        criandoFicha = false;
                        Interface.MostrarMensagem("\nEncerrando o jogo... Até a próxima aventura!");
                        break;
                    
                    default:
                        Interface.ExibirErro("Opção inválida!");
                        break;
                }
            }

            if (!jogoAberto) {
                break;
            }
            
            Interface.MostrarMensagem("\nA criação da ficha foi finalizada com sucesso!");
            
            // Fase da Aventura
            boolean jogando = true;
            boolean prologoFeito = false;
            boolean personagemFaleceu = false;

            while (jogando) {
                Interface.limparTela();
                int escolhaAventura = Interface.MenuPrincipalAventura();

                switch (escolhaAventura) {
                    case 1:
                        Interface.MostrarFicha(ficha);
                        boolean naFicha = true;
                        while (naFicha) {
                            Interface.limparTela();
                            int acaoFicha = Interface.MenuFicha();
                            switch (acaoFicha) {
                                case 1:
                                    Interface.InspecionarHabilidades(ficha);
                                    break;
                                case 2:
                                    Interface.InspecionarInventario(ficha);
                                    break;
                                case 3:
                                    naFicha = false;
                                    break;
                                default:
                                    Interface.ExibirErro("Opção inválida!");
                                    break;
                            }
                        }
                        break;
                    case 2:
                        if (!prologoFeito) {
                            narrativa.Aventura.IniciarPrologo(ficha);
                            prologoFeito = true;
                        }
                        
                        boolean explorando = true;
                        while (explorando) {
                            Interface.limparTela();
                            System.out.println("\n--- MAPA DE FREIJORD ---");
                            System.out.println("1. Explorar a Floresta");
                            System.out.println("2. Ver Ficha");
                            System.out.println("3. Voltar ao Acampamento Seguro (Menu Principal)");
                            int escMapa = Interface.lerInteiro();

                            
                            if (escMapa == 1) {
                                eventos.Floresta.Explorar(ficha);
                                if (ficha.getVidaPersonagem() <= 0) {
                                    personagemFaleceu = true;
                                    explorando = false;
                                }
                            } else if (escMapa == 2) {
                                Interface.MostrarFicha(ficha);
                            } else if (escMapa == 3) {
                                explorando = false;
                            } else {
                                Interface.ExibirErro("Opção inválida!");
                            }
                        }
                        
                        if (personagemFaleceu) {
                            jogando = false;
                        }
                        break;
                    case 3:
                        Interface.MostrarMensagem("\nEncerrando o jogo... Até a próxima aventura!");
                        jogando = false;
                        jogoAberto = false;
                        break;
                    default:
                        Interface.ExibirErro("Opção inválida!");
                        break;
                }
            }

            if (personagemFaleceu) {
                Interface.MostrarMensagem("\n==================================================");
                Interface.MostrarMensagem("Sua jornada termina aqui, mas toda lenda pode recomeçar!");
                Interface.MostrarMensagem("==================================================");
                Interface.Pausa(3000);
            }
        }
    }
}
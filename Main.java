import classes.*;
import fichas.FichaRpg;
import telas.Interface;
import salvamento.GerenciadorSaves;

public class Main {
    public static void main(String[] args) {

        Interface.BarraCarregamento("Carregando jogo...");
        Interface.ExibirBoasVindas();

        boolean jogoAberto = true;

        while (jogoAberto) {
            int escolhaInicial = Interface.MenuInicial();

            if (escolhaInicial == 4) {
                Interface.MostrarMensagem("\nEncerrando o jogo... Até a próxima aventura!");
                break;
            }

            if (escolhaInicial == 3) {
                // ==================== APAGAR SAVE ====================
                if (GerenciadorSaves.quantidadeSaves() == 0) {
                    Interface.ExibirErro("Você ainda não possui nenhum save para apagar.");
                    Interface.Pausa(1500);
                    continue;
                }
                Interface.MenuApagarSave();
                continue;
            }

            if (escolhaInicial == 2) {
                // ==================== CARREGAR JOGO ====================
                int slot = Interface.MenuCarregarJogo();
                if (slot == -2) {
                    Interface.ExibirErro("Você ainda não possui nenhum save! Comece um Novo Jogo.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (slot == -1) continue; // voltou ao menu principal

                FichaRpg ficha = GerenciadorSaves.carregar(slot);
                if (ficha == null) {
                    Interface.ExibirErro("Não foi possível carregar esse save.");
                    Interface.Pausa(1500);
                    continue;
                }
                ficha.resetarEfeitosCombate();

                Interface.MostrarMensagem("\nSave carregado! Boa sorte na jornada, " + ficha.getNomePersonagem() + "!");
                Interface.Pausa(2000);

                if (jogarPartida(ficha)) {
                    jogoAberto = false;
                }
                continue;
            }

            if (escolhaInicial != 1) {
                Interface.ExibirErro("Opção inválida!");
                continue;
            }

            // ==================== NOVO JOGO ====================
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

                            if (gastoDePontos > 6) {
                                Interface.MostrarMensagem("Você colocou mais que 6 pontos! Por isso, foram aplicados apenas 6.");
                                gastoDePontos = 6;
                            }

                            if (gastoDePontos > totalDePontos && totalDePontos < 6) {
                                gastoDePontos = totalDePontos;
                                Interface.MostrarMensagem("Como só restavam " + totalDePontos + " pontos, foram aplicados apenas " + totalDePontos + ".");
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

            // O prólogo começa assim que a ficha é finalizada
            narrativa.Aventura.IniciarPrologo(ficha);

            if (jogarPartida(ficha)) {
                jogoAberto = false;
            }
        }
    }

    // Loop da aventura. Retorna true se o jogador encerrou o jogo de vez
    // (voltando ao menu principal quando o personagem falece, por exemplo).
    private static boolean jogarPartida(FichaRpg ficha) {
        boolean jogando = true;
        boolean personagemFaleceu = false;
        boolean encerrouJogo = false;

        while (jogando) {
            int escolhaAventura = Interface.MenuPrincipalAventura(ficha);

            switch (escolhaAventura) {
                case 1:
                    Interface.MostrarFicha(ficha);
                    boolean naFicha = true;
                    while (naFicha) {
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
                    eventos.Floresta.Explorar(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                    break;

                case 3:
                    eventos.Floresta.BuscarRecursos(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                    break;

                case 4:
                    eventos.Floresta.MenuConstrucao(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                    break;

                case 5:
                    if (ficha.temCompanheiro()) {
                        eventos.Floresta.ConversarComCompanheiro(ficha);
                    } else {
                        salvarJogo(ficha);
                    }
                    break;

                case 6:
                    if (ficha.temCompanheiro()) {
                        salvarJogo(ficha);
                    } else {
                        encerrarJogo(ficha);
                        jogando = false;
                        encerrouJogo = true;
                    }
                    break;

                case 7:
                    encerrarJogo(ficha);
                    jogando = false;
                    encerrouJogo = true;
                    break;

                default:
                    Interface.ExibirErro("Opção inválida!");
                    break;
            }

            if (personagemFaleceu) {
                jogando = false;
            }
        }

        if (personagemFaleceu) {
            Interface.MostrarMensagem("\n==================================================");
            Interface.MostrarMensagem("Sua jornada termina aqui, mas toda lenda pode recomeçar!");
            Interface.MostrarMensagem("==================================================");
            Interface.Pausa(3000);
            return false;
        }

        return encerrouJogo;
    }

    // Fluxo de "Salvar Jogo": escolhe o slot e salva
    private static void salvarJogo(FichaRpg ficha) {
        int slot = Interface.MenuSalvarJogo(ficha);
        if (slot == -1) return; // voltou

        if (GerenciadorSaves.salvar(ficha, slot)) {
            Interface.MostrarMensagem("\nJogo salvo com sucesso no slot " + slot + "!");
        } else {
            Interface.ExibirErro("Não foi possível salvar. Verifique a pasta saves/.");
        }
        Interface.Pausa(1500);
    }

    // Fluxo de sair: pergunta se quer salvar e encerra o jogo
    private static void encerrarJogo(FichaRpg ficha) {
        if (Interface.PerguntarSalvarAntesDeSair()) {
            salvarJogo(ficha);
        }
        Interface.MostrarMensagem("\nEncerrando o jogo... Até a próxima aventura!");
    }
}
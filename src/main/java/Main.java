import classes.*;
import fichas.FichaRpg;
import telas.Interface;
import telas.Teclado;
import salvamento.GerenciadorSaves;

public class Main {
    public static void main(String[] args) {

        Teclado.assegurarTerminalSaudavel(); // recupera o terminal (eco) mesmo se a última sessão fechou no modo cru
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
                ficha.setSlotAtual(slot);

                Interface.MostrarMensagem("\nSave carregado! Boa sorte na jornada, " + ficha.getNomePersonagem() + "!");
                Interface.Pausa(2000);

                if (jogarPartida(ficha)) {
                    jogoAberto = false;
                }
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
                        int escolhaDificuldade = Interface.MenuEscolherDificuldade();
                        switch (escolhaDificuldade) {
                            case 0: break;
                            case 1:
                                ficha.setModoDificuldade(fichas.ModoDificuldade.NORMAL);
                                Interface.MostrarMensagem("\nModo Normal selecionado: seus saves são mantidos se você morrer.");
                                break;
                            case 2:
                                ficha.setModoDificuldade(fichas.ModoDificuldade.DIFICIL);
                                Interface.MostrarMensagem("\nModo Difícil selecionado: morte permanente apaga o save do personagem!");
                                break;
                            default:
                                Interface.ExibirErro("Opção inválida!");
                        }
                        Interface.Pausa(1200);
                        break;

                    case 5:
                        Interface.MostrarFicha(ficha);
                        break;

                    case 6:
                        if (ficha.isFichaCompleta()) {
                            criandoFicha = false;
                        } else {
                            Interface.ExibirErro("Ficha incompleta! Preencha seu Nome, distribua os 6 Atributos e escolha sua Classe.");
                        }
                        break;

                    case 7:
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
    public static boolean jogarPartida(FichaRpg ficha) {
        boolean jogando = true;
        boolean personagemFaleceu = false;
        boolean encerrouJogo = false;

        while (jogando) {
            int[] ops = Interface.opcoesMenuFloresta(ficha);

            if (ficha.isNoVilarejo()) {
                // ==================== VILAREJO (fora da floresta) ====================
                int escolhaVilarejo = Interface.MenuVilarejo(ficha);

                if (escolhaVilarejo == 1) {
                    abrirFicha(ficha);
                } else if (escolhaVilarejo == 2) {
                    eventos.VilarejoDeScarbor.OlharEmVolta(ficha);
                } else if (escolhaVilarejo == 3) {
                    eventos.TravessiaDaFloresta.VoltarParaFloresta(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaVilarejo == 4) {
                    salvarJogo(ficha);
                } else if (escolhaVilarejo == 5) {
                    encerrarJogo(ficha);
                    jogando = false;
                    encerrouJogo = true;
                } else {
                    Interface.ExibirErro("Opção inválida!");
                }
            } else {
                // ==================== FLORESTA DE FREIJORD ====================
                int escolhaAventura = Interface.MenuPrincipalAventura(ficha);

                if (escolhaAventura == 1) {
                    abrirFicha(ficha);
                } else if (escolhaAventura == 2) {
                    eventos.Floresta.Explorar(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaAventura == 3) {
                    eventos.Floresta.BuscarRecursos(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaAventura == 4) {
                    eventos.Floresta.MenuConstrucao(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaAventura == ops[0]) {
                    eventos.TravessiaDaFloresta.TentarSairDaFloresta(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (ops[1] > 0 && escolhaAventura == ops[1]) {
                    estruturas.LabirintoDoMinotauro.MenuLabirinto(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (ops[2] > 0 && escolhaAventura == ops[2]) {
                    eventos.Floresta.ConversarComCompanheiro(ficha);
                } else if (escolhaAventura == ops[3]) {
                    salvarJogo(ficha);
                } else if (escolhaAventura == ops[4]) {
                    encerrarJogo(ficha);
                    jogando = false;
                    encerrouJogo = true;
                } else {
                    Interface.ExibirErro("Opção inválida!");
                }
            }

            if (personagemFaleceu) {
                jogando = false;
            }
        }

        if (personagemFaleceu) {
            Interface.MostrarMensagem("\n==================================================");
            Interface.MostrarMensagem("Sua jornada termina aqui, mas toda lenda pode recomeçar!");
            Interface.MostrarMensagem("==================================================");

            if (ficha.isModoDificil()) {
                int apagados = GerenciadorSaves.deletarSavesDoPersonagem(ficha.getNomePersonagem());
                Interface.MostrarMensagem("\n  " + Interface.AMARELO + "MODO DIFÍCIL \u2014 MORTE PERMANENTE!" + Interface.RESET);
                if (apagados > 0) {
                    Interface.MostrarMensagem("  O destino consumiu " + apagados + " save(s) deste personagem.");
                } else {
                    Interface.MostrarMensagem("  Este personagem não possuía saves para serem consumidos.");
                }
            } else {
                Interface.MostrarMensagem("\n  " + Interface.VERDE + "MODO NORMAL" + Interface.RESET + " \u2014 seus saves foram mantidos.");
            }
            Interface.Pausa(3000);
            return false;
        }

        return encerrouJogo;
    }

    // Fluxo de "Ver ficha": mostra a ficha e permite navegar pelas suas telas
    private static void abrirFicha(FichaRpg ficha) {
        Interface.MostrarFicha(ficha);
        boolean naFicha = true;
        while (naFicha) {
            int acaoFicha = Interface.MenuFicha();
            if (acaoFicha == 1) {
                Interface.InspecionarHabilidades(ficha);
            } else if (acaoFicha == 2) {
                Interface.InspecionarInventario(ficha);
            } else if (acaoFicha == 3) {
                naFicha = false;
            } else {
                Interface.ExibirErro("Opção inválida!");
            }
        }
    }

    // Fluxo de "Salvar Jogo": escolhe o slot e salva
    private static void salvarJogo(FichaRpg ficha) {
        int slot = Interface.MenuSalvarJogo(ficha);
        if (slot == -1) return; // voltou

        if (GerenciadorSaves.salvar(ficha, slot)) {
            ficha.setSlotAtual(slot);
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
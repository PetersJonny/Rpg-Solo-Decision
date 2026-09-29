import classes.*;
import fichas.FichaRpg;
import telas.Interface;
import telas.MenuCriacaoPersonagem;
import telas.MenuSalvamento;
import telas.MenuVisualizacao;
import telas.Teclado;
import salvamento.GerenciadorSaves;

public class Main {
    public static void main(String[] args) {

        Teclado.assegurarTerminalSaudavel();
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
                                if (GerenciadorSaves.quantidadeSaves() == 0) {
                    Interface.ExibirErro("Você ainda não possui nenhum save para apagar.");
                    Interface.Pausa(1500);
                    continue;
                }
                MenuSalvamento.MenuApagarSave();
                continue;
            }

            if (escolhaInicial == 2) {
                                int slot = MenuSalvamento.MenuCarregarJogo();
                if (slot == -2) {
                    Interface.ExibirErro("Você ainda não possui nenhum save! Comece um Novo Jogo.");
                    Interface.Pausa(1500);
                    continue;
                }
                if (slot == -1) continue;
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

                        String nomePessoa = Interface.PedirNomeJogador();
            FichaRpg ficha = new FichaRpg(nomePessoa);

                        boolean criandoFicha = true;

            while (criandoFicha) {
                int escolhaInterface = MenuCriacaoPersonagem.MenuCriacaoFicha();

                switch (escolhaInterface) {
                    case 1:
                        String nomePersonagem = Interface.PedirNomePersonagem();
                        ficha.setNomePersonagem(nomePersonagem);
                        break;

                    case 2:
                        ficha.resetarPontosBase();
                        int totalDePontos = 6;

                        while (totalDePontos > 0) {
                            int atributoEscolhido = MenuCriacaoPersonagem.MenuDistribuirAtributos(totalDePontos);

                            if (atributoEscolhido == 0) break;

                            int gastoDePontos = MenuCriacaoPersonagem.PedirQuantidadePontos(totalDePontos);

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
                        int escolhaClasse = MenuCriacaoPersonagem.MenuEscolherClasse();
                        switch (escolhaClasse) {
                            case 0: break;
                            case 1:
                                String elemento = MenuCriacaoPersonagem.EscolherElementoMago();
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
                        int escolhaRaca = MenuCriacaoPersonagem.MenuEscolherRaca();
                        switch (escolhaRaca) {
                            case 0: break;
                            case 1:
                                int atributoHumano = MenuCriacaoPersonagem.MenuEscolherAtributoHumano();
                                if (atributoHumano == 0) break;
                                String[] atributosHumano = {"", "Constituição", "Destreza", "Força", "Sabedoria", "Intelecto", "Presença"};
                                ficha.setRaca(new racas.HumanoRaca(atributosHumano[atributoHumano]));
                                break;
                            case 2: ficha.setRaca(new racas.ElfoDaFlorestaRaca()); break;
                            case 3: ficha.setRaca(new racas.VigiaDoCrepusculoRaca()); break;
                            case 4: ficha.setRaca(new racas.MeioFadaRaca()); break;
                            case 5: ficha.setRaca(new racas.DraconicoRaca()); break;
                            case 6: ficha.setRaca(new racas.MeioOrqueRaca()); break;
                            case 7: ficha.setRaca(new racas.GnomoRaca()); break;
                            default: Interface.ExibirErro("Opção inválida!");
                        }
                        ficha.aplicarBonus();
                        break;

                    case 5:
                        int escolhaDificuldade = MenuCriacaoPersonagem.MenuEscolherDificuldade();
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

                    case 6:
                        MenuVisualizacao.MostrarFicha(ficha);
                        break;

                    case 7:
                        if (ficha.isFichaCompleta()) {
                            criandoFicha = false;
                        } else {
                            Interface.ExibirErro("Ficha incompleta! Preencha seu Nome, distribua os 6 Atributos e escolha sua Classe.");
                        }
                        break;

                    case 8:
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

                        narrativa.Aventura.IniciarPrologo(ficha);

            if (jogarPartida(ficha)) {
                jogoAberto = false;
            }
        }
    }

            public static boolean jogarPartida(FichaRpg ficha) {
        boolean jogando = true;
        boolean personagemFaleceu = false;
        boolean encerrouJogo = false;

        while (jogando) {
            int[] ops = Interface.opcoesMenuFloresta(ficha);

            if (ficha.isNoVilarejo()) {
                                int[] opsVila = Interface.opcoesMenuVilarejo(ficha);
                int escolhaVilarejo = Interface.MenuVilarejo(ficha);

                if (escolhaVilarejo == 1) {
                    abrirFicha(ficha);
                } else if (escolhaVilarejo == 2) {
                    eventos.VilarejoDeScarbor.OlharEmVolta(ficha);
                } else if (escolhaVilarejo == 3) {
                    eventos.Taverna.Taverna(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaVilarejo == 4) {
                    eventos.Ferreiro.Ferreiro(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaVilarejo == opsVila[0]) {
                    missoes.QuadroDeMissoes.MissoesEmAndamento(ficha);
                } else if (opsVila[1] > 0 && escolhaVilarejo == opsVila[1]) {
                    eventos.Alfaiataria.Alfaiataria(ficha);
                } else if (opsVila[2] > 0 && escolhaVilarejo == opsVila[2]) {
                    eventos.BarracaDeFrutas.BarracaDeFrutas(ficha);
                } else if (opsVila[3] > 0 && escolhaVilarejo == opsVila[3]) {
                    eventos.Caverna.IrParaCaverna(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaVilarejo == opsVila[4]) {
                    eventos.TravessiaDaFloresta.VoltarParaFloresta(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaVilarejo == opsVila[5]) {
                    salvarJogo(ficha);
                } else if (escolhaVilarejo == opsVila[6]) {
                    encerrarJogo(ficha);
                    jogando = false;
                    encerrouJogo = true;
                } else {
                    Interface.ExibirErro("Opção inválida!");
                }
            } else {
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
                    eventos.Acampamento.MenuConstrucao(ficha);
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
                } else if (ops[3] > 0 && escolhaAventura == ops[3]) {
                    eventos.TrilhaDaNeta.MenuNeta(ficha);
                    if (ficha.getVidaPersonagem() <= 0) {
                        personagemFaleceu = true;
                    }
                } else if (escolhaAventura == ops[4]) {
                    salvarJogo(ficha);
                } else if (escolhaAventura == ops[5]) {
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

        private static void abrirFicha(FichaRpg ficha) {
        MenuVisualizacao.MostrarFicha(ficha);
        boolean naFicha = true;
        while (naFicha) {
            int acaoFicha = MenuVisualizacao.MenuFicha();
            if (acaoFicha == 1) {
                MenuVisualizacao.InspecionarHabilidades(ficha);
            } else if (acaoFicha == 2) {
                MenuVisualizacao.InspecionarInventario(ficha);
            } else if (acaoFicha == 3) {
                naFicha = false;
            } else {
                Interface.ExibirErro("Opção inválida!");
            }
        }
    }

        private static void salvarJogo(FichaRpg ficha) {
        int slot = MenuSalvamento.MenuSalvarJogo(ficha);
        if (slot == -1) return;
        if (GerenciadorSaves.salvar(ficha, slot)) {
            ficha.setSlotAtual(slot);
            Interface.MostrarMensagem("\nJogo salvo com sucesso no slot " + slot + "!");
        } else {
            Interface.ExibirErro("Não foi possível salvar. Verifique a pasta saves/.");
        }
        Interface.Pausa(1500);
    }

        private static void encerrarJogo(FichaRpg ficha) {
        if (MenuSalvamento.PerguntarSalvarAntesDeSair()) {
            salvarJogo(ficha);
        }
        Interface.MostrarMensagem("\nEncerrando o jogo... Até a próxima aventura!");
    }
}

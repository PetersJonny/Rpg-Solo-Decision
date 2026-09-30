package mecanicas;

import classes.*;
import comandos.*;
import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Arma;
import itens.Consumivel;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mecanicas.MecanicasRpg;
import telas.Interface;
import telas.MenuVisualizacao;

import static mecanicas.GerenciadorDeAcoes.*;
import static mecanicas.GerenciadorDeAtaque.*;
import static mecanicas.GerenciadorDeCompanheiro.*;
import static mecanicas.GerenciadorDeEvolucao.*;
import static mecanicas.GerenciadorDeHabilidades.*;
import static mecanicas.GerenciadorDeItens.*;
import static mecanicas.MotorDeCombate.*;

public class GerenciadorDeTurnos {

    static void verificarDesercao(List<Criatura> inimigos) {
        int total = 0, fora = 0;
        boolean grupo = false;
        for (Criatura c : inimigos) {
            if (!c.isDesertaEmGrupo()) continue;
            grupo = true;
            total++;
            if (c.getVida() <= 0 || c.isFugiu()) fora++;
        }
        if (!grupo || total == 0 || fora * 2 <= total || fora >= total) return;
        Interface.MostrarMensagem("\nVendo a力地 o que aconteceu, os bandidos restantes jogam fora as armas e se dispersam no mato!");
        Interface.Pausa(2500);
        for (Criatura c : inimigos) {
            if (c.isDesertaEmGrupo() && c.getVida() > 0 && !c.isFugiu()) {
                c.setFugiu(true);
                Interface.MostrarMensagem(c.getNome() + " foge para o fundo da mata.");
            }
        }
        Interface.Pausa(2000);
    }

    public static void RodadasDeCombate(FichaRpg ficha, List<Criatura> inimigos, List<int[]> ordem) {
        boolean[] cascaGrossaAtiva = {false};
        int[] tentativasFuga = {0};
        List<Criatura> mortesProcessadas = new ArrayList<>();
        ficha.resetarEfeitosCombate();
        ficha.setEmCombate(true);

        if (temHabilidade(ficha, "Defesa Absoluta")) {
            ficha.setDefesaAbsolutaAtiva(true);
            Interface.MostrarMensagem("\n(Defesa Absoluta ativa! +5 de defesa até você atacar.)");
            Interface.Pausa(1500);
        }

        while ((ficha.getVidaPersonagem() > 0
                || (ficha.getCompanheiro() != null && ficha.getCompanheiro().getFicha().getVidaPersonagem() > 0))
                && !inimigosVivos(inimigos).isEmpty()) {
            ficha.setFrutaImuneNestaRodada(false);
            for (Criatura c : inimigos) {
                if (c.getVida() > 0 && c.isSangrando() && !c.isFugiu()) {
                    int dano = MecanicasRpg.rolarDado(6);
                    c.setVida(c.getVida() - dano);
                    Interface.MostrarMensagem("\n" + c.getNome() + " sangra profusamente! Dano: " + dano + " (Vida: " + Math.max(0, c.getVida()) + ")");
                    Interface.Pausa(1500);
                }
            }
            if (inimigosVivos(inimigos).isEmpty()) break;
            Interface.cabecalhoMenu("COMBATE");
            Interface.MostrarMensagem("  Sua Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + " | Mana: " + ficha.getManaPersonagem() + "/" + ficha.getManaMaxima());
            if (ficha.getCuraAbsolutaBonus() > 0) {
                Interface.MostrarMensagem("Proteção da Cura Absoluta: +" + ficha.getCuraAbsolutaBonus());
            }
            Interface.MostrarMensagem("  Inimigos:");
            for (int i = 0; i < inimigos.size(); i++) {
                Criatura c = inimigos.get(i);
                if (c.getVida() > 0 && !c.isFugiu()) {
                    Interface.MostrarMensagem("  " + (i + 1) + ". " + c.getNome() + " (Vida: " + c.getVida() + ")");
                }
            }
            if (tentativasFuga[0] > 0) {
                Interface.MostrarMensagem("  Tentativas de fuga: " + tentativasFuga[0] + "/3");
            }
            Interface.Pausa(1500);

            cascaGrossaAtiva[0] = false;
            ficha.setMagiaProibidaAtiva(false);

            if (ficha.isInfectado() && ficha.getVidaPersonagem() > 0) {
                int danoInfecao = MecanicasRpg.rolarDado(4);
                ficha.receberDano(danoInfecao);
                Interface.MostrarMensagem("\nSua infecção zumbi corrói as feridas! Dano: " + danoInfecao + " (Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + ")");
                Interface.Pausa(2000);
                if (ficha.getVidaPersonagem() <= 0 && !companheiroEmPe(ficha)) break;
                if (inimigosVivos(inimigos).isEmpty()) break;
            }

            if (ficha.isSangrando() && ficha.getVidaPersonagem() > 0) {
                int danoSangramento = MecanicasRpg.rolarDado(6);
                ficha.receberDano(danoSangramento);
                Interface.MostrarMensagem("\nVocê está sangrando! O ferimento não para: " + danoSangramento + " de dano (Vida: " + ficha.getVidaPersonagem() + "/" + ficha.getVidaMaxima() + ")");
                Interface.Pausa(2000);
                if (ficha.getVidaPersonagem() <= 0 && !companheiroEmPe(ficha)) break;
                if (inimigosVivos(inimigos).isEmpty()) break;
            }

            if (ficha.isCuraParaMortePreparado()) {
                ficha.setCuraParaMortePreparado(false);
                ficha.setCuraParaMorteAtivo(true);
                Interface.MostrarMensagem("\nO líquido mortal injetado começa a agir!");
                Interface.Pausa(1500);
            }

            if (ficha.getVidaPersonagem() <= 0 && !companheiroEmPe(ficha)) break;
            if (inimigosVivos(inimigos).isEmpty()) break;

            boolean semHabilidades = ficha.getRodadasSemHabilidade() > 0;
            ComandoCombate acaoDeclarada = ficha.getVidaPersonagem() > 0
                    ? declararAcao(ficha, inimigos, cascaGrossaAtiva, semHabilidades)
                    : new ComandoAguardar();

            Set<Criatura> jaAtacouNaRodada = new HashSet<>();

            boolean acaoResolvida = false;
            if (acaoDeclarada.isPrioritarioFuga()) {
                int resultadoFuga = acaoDeclarada.executar(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada);
                if (resultadoFuga == 0) {
                    Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                    Interface.Pausa(2500);
                    ficha.setInfectado(false);
                    ficha.setPactoMortalAtivo(false);
                    return;
                }
                if (resultadoFuga == 1) {

                    continue;
                }

                acaoResolvida = true;
                processarMortes(inimigos, mortesProcessadas, ficha);
            }

            for (int[] token : ordem) {
                if (ficha.getVidaPersonagem() <= 0 && !companheiroEmPe(ficha)) break;
                if (inimigosVivos(inimigos).isEmpty()) break;

                if (token[1] == 0) {
                    if (acaoResolvida) continue;
                    if (ficha.getVidaPersonagem() <= 0) continue;
                    int resultado = acaoDeclarada.executar(ficha, inimigos, cascaGrossaAtiva, tentativasFuga, jaAtacouNaRodada);
                    if (resultado == 0) {
                        Interface.MostrarMensagem("\nVocê conseguiu escapar da floresta!");
                        Interface.Pausa(2500);
                        ficha.setInfectado(false);
                    ficha.setPactoMortalAtivo(false);
                        return;
                    }

                    processarMortes(inimigos, mortesProcessadas, ficha);
                } else if (token[1] == -1) {

                    companheiros.Companheiro comp = ficha.getCompanheiro();
                    if (comp != null && comp.getFicha().getVidaPersonagem() > 0) {
                        acaoDoCompanheiro(ficha, comp, inimigos);
                        processarMortes(inimigos, mortesProcessadas, ficha);
                    }
                } else {
                    Criatura c = inimigos.get(token[1] - 1);
                    if (c.getVida() > 0 && !c.isFugiu() && !jaAtacouNaRodada.contains(c)) {

                        if (ficha.getPrisaoAtiva() == c) {
                            int testePrisao = MecanicasRpg.rolarDado(20);
                            Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, c) + " tenta se libertar da prisão (d20, precisa de 15 ou mais): " + testePrisao);
                            Interface.Pausa(1500);
                            if (testePrisao < 15) {
                                Interface.MostrarMensagem("A prisão o mantém imóvel! " + rotuloCriatura(inimigos, c) + " não consegue agir.");
                                Interface.Pausa(1500);
                                continue;
                            }
                            ficha.setPrisaoAtiva(null);
                            Interface.MostrarMensagem("A prisão se desfaz! " + rotuloCriatura(inimigos, c) + " está livre!");
                            Interface.Pausa(1500);
                        }

                        if (ficha.isMagiaProibidaAtiva()) {
                            Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, c) + " investe, mas a Magia Proibida corrompe seu golpe e ele erra!");
                            Interface.Pausa(1500);
                            continue;
                        }

                        Interface.MostrarMensagem("\n" + rotuloCriatura(inimigos, c) + " avança para atacar!");
                        Interface.Pausa(1500);

                        companheiros.Companheiro comp2 = ficha.getCompanheiro();
                        boolean atacarCompanheiro = comp2 != null
                                && comp2.getFicha().getVidaPersonagem() > 0
                                && (ficha.getVidaPersonagem() <= 0 || MecanicasRpg.rolarDado(2) == 1);
                        if (atacarCompanheiro) {
                            Interface.MostrarMensagem(rotuloCriatura(inimigos, c) + " mira em " + comp2.getNomeCompleto() + "!");
                            Interface.Pausa(1500);
                            c.atacarJogador(comp2.getFicha(), false, false);
                            if (comp2.getFicha().getVidaPersonagem() <= 0) {
                                Interface.MostrarMensagem("\n" + comp2.getNomeCompleto() + " cai em combate!");
                                Interface.Pausa(1500);
                            }
                        } else {
                            c.atacarJogador(ficha, cascaGrossaAtiva[0], true);
                        }

                        processarMortes(inimigos, mortesProcessadas, ficha);
                    verificarDesercao(inimigos);
                    }
                }
            }

            if (ficha.getRodadasSemHabilidade() > 0) {
                ficha.setRodadasSemHabilidade(ficha.getRodadasSemHabilidade() - 1);
            }
        }

        companheiros.Companheiro comp = ficha.getCompanheiro();
        boolean grupoVenceu = inimigosVivos(inimigos).isEmpty();

        if (grupoVenceu) {

            if (comp != null && ficha.getVidaPersonagem() > 0) {
                FichaRpg cf = comp.getFicha();
                if (cf.getVidaPersonagem() <= 0) {
                    Interface.cabecalhoMenu("RESGATE DO COMPANHEIRO");
                    Interface.MostrarMensagem("\n  " + comp.getNomeCompleto() + " cai no chão, sem vida...");
                    Interface.Pausa(1500);

                    boolean salvo = tentarResgate(ficha, "Você", cf, comp.getNomeCompleto());

                    if (!salvo) {
                        Interface.MostrarMensagem("\n  Ele(a) parte em silêncio.");
                        Interface.Pausa(1500);

                        List<ItemRpg> itensComp = new ArrayList<>(cf.getInventario());
                        for (ItemRpg item : itensComp) {
                            ficha.adicionarItem(item);
                        }
                        int ouroComp = cf.getOuro();
                        if (ouroComp > 0) {
                            ficha.adicionarOuro(ouroComp);
                        }

                        if (!itensComp.isEmpty() || ouroComp > 0) {
                            Interface.MostrarMensagem("\n  Você recolhe os pertences de " + comp.getNomeCompleto() + ".");
                            for (ItemRpg item : itensComp) {
                                Interface.MostrarMensagem("    + " + item.getNome() + " (" + item.getQuantidade() + ")");
                            }
                            if (ouroComp > 0) {
                                Interface.MostrarMensagem("    + " + ouroComp + " de ouro");
                            }
                        }

                        ficha.removerCompanheiro();
                        comp = null;
                    }
                    Interface.Pausa(2000);
                } else if (cf.getVidaPersonagem() < cf.getVidaMaxima() * 0.3) {

                    Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " respira fundo e se recupera um pouco após o combate.");
                    cf.setVidaPersonagem(Math.max((int) (cf.getVidaMaxima() * 0.5), 1));
                    Interface.Pausa(2000);
                }
            }

            if (ficha.getVidaPersonagem() <= 0) {
                if (!tentarReviver(ficha) && companheiroEmPe(ficha)) {
                    FichaRpg cf = comp.getFicha();
                    Interface.cabecalhoMenu("RESGATE DO JOGADOR");
                    Interface.MostrarMensagem("\n  Você cai... " + comp.getNomeCompleto() + " se joga ao seu lado!");
                    Interface.Pausa(1500);

                    tentarResgate(cf, comp.getNomeCompleto(), ficha, "você");

                    Interface.Pausa(2000);
                }
            }
        }

        Interface.cabecalhoMenu("FIM DO COMBATE");
        if (ficha.isFrutaDoDiaboAtiva()) {
            Interface.MostrarMensagem("\nO poder da " + Interface.AMARELO + "Fruta do Diabo" + Interface.RESET + " se desfaz. O calor some do seu peito.");
            Interface.Pausa(2500);
        }
        ficha.setFrutaDoDiaboAtiva(false);
        ficha.setFrutaImuneNestaRodada(false);
        ficha.setEmCombate(false);
        ficha.setInfectado(false);
                    ficha.setPactoMortalAtivo(false);
        if (ficha.getVidaPersonagem() <= 0) {
            Interface.MostrarMensagem("\n  Você foi derrotado... A floresta recupera o silêncio.");
            Interface.Pausa(3000);
        } else {
            Interface.MostrarMensagem("\n  Você sobreviveu ao combate!");
            Interface.Pausa(2500);
        }
        Interface.barraDivisoria();
    }

    public static void processarMortes(List<Criatura> inimigos, List<Criatura> mortesProcessadas, FichaRpg ficha) {
        for (Criatura c : inimigos) {
            if (c.getVida() <= 0 && !mortesProcessadas.contains(c)) {
                mortesProcessadas.add(c);
                if (ficha.getPrisaoAtiva() == c) {
                    ficha.setPrisaoAtiva(null);
                }
                Interface.MostrarMensagem("\nVocê derrotou " + rotuloCriatura(inimigos, c) + "!");
                Interface.Pausa(1500);
                c.processarDrops(ficha);

                if (c.getXpGanho() > 0) {
                    Interface.MostrarMensagem("-> Você ganhou " + c.getXpGanho() + " XP!");
                    Interface.Pausa(1500);
                    int vidasAntes = ficha.getVidaMaxima();
                    int manaAntes = ficha.getManaMaxima();
                    int nivelAntes = ficha.getNivel();
                    List<String> habilidadesAntes = new ArrayList<>();
                    for (habilidades.Habilidade hh : ficha.getHabilidades()) {
                        habilidadesAntes.add(hh.getNome());
                    }
                    int niveisGanhos = ficha.adicionarXp(c.getXpGanho());
                    if (niveisGanhos > 0) {
                        Interface.MostrarMensagem("\n*** SUBIU PARA O NÍVEL " + ficha.getNivel() + "! ***");
                        Interface.Pausa(2000);
                        Interface.MostrarMensagem("+ " + (ficha.getVidaMaxima() - vidasAntes) + " de vida máxima.");
                        Interface.MostrarMensagem("+ " + (ficha.getManaMaxima() - manaAntes) + " de mana máxima.");
                        Interface.Pausa(2000);
                        if (ficha.getNivel() < 10) {
                            Interface.MostrarMensagem("XP para o próximo nível: " + fichas.FichaRpg.getXpNecessaria(ficha.getNivel()));
                        } else {
                            Interface.MostrarMensagem("Você atingiu o nível máximo!");
                        }
                        Interface.Pausa(2000);

                        for (habilidades.Habilidade hh : ficha.getHabilidades()) {
                            if (!habilidadesAntes.contains(hh.getNome())) {
                                Interface.MostrarMensagem("\nVocê aprendeu a habilidade: " + hh.getNome() + "!");
                                Interface.MostrarMensagem(hh.getDescricao());
                                Interface.Pausa(2000);
                            }
                        }

                        if (ficha.getClasseDoPersonagem() instanceof classes.Mago) {
                            for (habilidades.Habilidade hh : ficha.getHabilidades()) {
                                if (hh instanceof habilidades.Magia && ((habilidades.Magia) hh).isAtaqueArea()) {
                                    Interface.MostrarMensagem("\nSua " + hh.getNome() + " agora ataca em área!");
                                    Interface.Pausa(2000);
                                }
                            }
                        }

                        for (int nivelGanho = nivelAntes + 1; nivelGanho <= ficha.getNivel(); nivelGanho++) {
                            if (nivelGanho == 5 || nivelGanho == 7 || nivelGanho == 9 || nivelGanho == 10) {
                                List<habilidades.Habilidade> opcoes = ficha.getClasseDoPersonagem().getEscolhasDisponiveis(ficha, nivelGanho);
                                if (opcoes != null && !opcoes.isEmpty()) {
                                    GerenciadorDeEvolucao.escolherHabilidadeNivel(ficha, nivelGanho, opcoes);
                                }
                            }
                            if (nivelGanho == 2 || nivelGanho == 4 || nivelGanho == 6 || nivelGanho == 8) {
                                GerenciadorDeEvolucao.escolherPontoAtributo(ficha);
                            }
                        }
                    }
                }
            }
        }
    }

    public static int TentarFugirNaVez(FichaRpg ficha, List<Criatura> inimigos, boolean[] cascaGrossaAtiva, int[] tentativasFuga, Set<Criatura> jaAtacouNaRodada) {
        int resultadoFuga = TentarFugir(ficha, inimigos, tentativasFuga[0]);

        if (resultadoFuga == tentativasFuga[0] && resultadoFuga != -1) {
            Interface.MostrarMensagem("\nVocê hesita e perde a oportunidade!");
            Interface.Pausa(1500);
            return -1;
        }

        if (resultadoFuga == -1) {
            Interface.MostrarMensagem("\nA ameaça te alcança e aproveita a abertura!");
            Interface.Pausa(1500);
            Criatura maisRapido = inimigoMaisRapidoVivo(inimigos);
            if (maisRapido != null) {
                jaAtacouNaRodada.add(maisRapido);
                maisRapido.atacarJogador(ficha, cascaGrossaAtiva[0], true);
            }
            return -1;
        } else if (resultadoFuga == 3) {
            return 0;
        } else {
            tentativasFuga[0] = resultadoFuga;
            return 1;
        }
    }

    public static boolean temBossSemFuga(List<Criatura> inimigos) {
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && c.isSemFuga()) {
                return true;
            }
        }
        return false;
    }

    public static Criatura inimigoMaisRapidoVivo(List<Criatura> inimigos) {
        Criatura maisRapido = null;
        for (Criatura c : inimigos) {
            if (c.getVida() <= 0) continue;
            if (maisRapido == null || c.getIniciativa() > maisRapido.getIniciativa()) {
                maisRapido = c;
            }
        }
        return maisRapido;
    }

    public static Criatura escolherAlvoAleatorio(List<Criatura> inimigos) {
        List<Criatura> vivos = inimigosVivos(inimigos);
        if (vivos.isEmpty()) return null;
        return vivos.get(MecanicasRpg.rolarDado(vivos.size()) - 1);
    }

    public static List<Criatura> inimigosVivos(List<Criatura> inimigos) {
        List<Criatura> vivos = new ArrayList<>();
        for (Criatura c : inimigos) {
            if (c.getVida() > 0 && !c.isFugiu()) {
                vivos.add(c);
            }
        }
        return vivos;
    }

    public static String rotuloCriatura(List<Criatura> inimigos, Criatura alvo) {
        int contagem = 0;
        for (Criatura c : inimigos) {
            if (c.getNome().equals(alvo.getNome())) {
                contagem++;
            }
        }
        if (contagem <= 1) return alvo.getNome();
        return alvo.getNome() + " " + (inimigos.indexOf(alvo) + 1);
    }

    public static String nomesDosInimigos(List<Criatura> inimigos) {
        Criatura c = inimigos.get(0);
        if (inimigos.size() == 1) return c.getNome();
        switch (c.getNome()) {
            case "Lobo Selvagem": return "Lobos Selvagens";
            case "Urso": return "Ursos";
            case "Bandido": return "Bandidos";
            default: return c.getNome();
        }
    }

    public static int TentarFugir(FichaRpg ficha, List<Criatura> inimigos, int tentativasAtuais) {
        Interface.cabecalhoMenu("TENTAR FUGIR");
        System.out.println("\n  Deseja realmente tentar fugir?\n");
        System.out.println("  1. Sim, tentar fugir");
        System.out.println("  2. Não, voltar ao combate");

        int confirmar = Interface.lerInteiro();

        if (confirmar != 1) return tentativasAtuais;

        Interface.MostrarMensagem("\nVocê tenta se esquivar e recuar...");
        Interface.Pausa(2000);

        int melhorIniciativa = -1000;
        for (Criatura c : inimigos) {
            if (c.getVida() <= 0) continue;
            if (c.getIniciativa() > melhorIniciativa) {
                melhorIniciativa = c.getIniciativa();
            }
        }
        int dificuldadeFuga = 10 + melhorIniciativa;

        Interface.pressionarParaTeste("Destreza");
        int dadoJogador = MecanicasRpg.rolarDado(20);
        int totalJogador = dadoJogador + ficha.getDestrezaTeste();
        Interface.MostrarMensagem("-> Sua Tentativa de Fuga: " + dadoJogador + " (Dado) + " + ficha.getDestrezaTeste() + " (Destreza) = " + totalJogador + " (Dificuldade: " + dificuldadeFuga + ")");
        Interface.Pausa(2000);

        if (totalJogador < dificuldadeFuga) {
            Interface.MostrarMensagem("\nA ameaça te alcança! Você perdeu a chance de fugir dessa vez.");
            return -1;
        }

        int novasTentativas = tentativasAtuais + 1;
        Interface.MostrarMensagem("\nVocê se afasta um passo! Tentativa " + novasTentativas + "/3");
        Interface.Pausa(2000);
        return novasTentativas;
    }

    public static void aplicarDanoCriatura(Criatura c, int dano) {
        if (dano <= 0) return;
        if (c.isEnfraquecido()) {
            dano += 5;
            Interface.MostrarMensagem("(Pacto Mortal! " + c.getNome() + " sofre +5 de dano demoníaco)");
            Interface.Pausa(1000);
        }
        c.setVida(Math.max(0, c.getVida() - dano));
    }

    public static List<Criatura> outrosVivos(List<Criatura> inimigos, Criatura excluida) {
        List<Criatura> outros = new ArrayList<>();
        for (Criatura c : inimigosVivos(inimigos)) {
            if (c != excluida) outros.add(c);
        }
        return outros;
    }

    public static void criaturaAtacaCriatura(Criatura atacante, Criatura alvo, List<Criatura> inimigos) {
        int dado = MecanicasRpg.rolarDado(20);
        int total = dado + atacante.getBonusAcerto();
        boolean critico = dado == 20;
        Interface.MostrarMensagem("-> " + atacante.getNome() + " ataca " + (alvo == atacante ? "a si mesma" : rotuloCriatura(inimigos, alvo)) + ": " + dado + " (Dado) + " + atacante.getBonusAcerto() + " (Bônus) = " + total + " (Defesa: " + alvo.getDefesa() + ")" + (critico ? " [CRÍTICO!]" : ""));
        Interface.Pausa(2000);

        if (!critico && total < alvo.getDefesa()) {
            Interface.MostrarMensagem("-> O golpe erra!");
            Interface.Pausa(1500);
            return;
        }

        Criatura.Ataque ataque = atacante.getAtaques().get(MecanicasRpg.rolarDado(atacante.getAtaques().size()) - 1);
        int dadosTotais = ataque.qtdDado * (critico ? 2 : 1);
        int dano = 0;
        for (int i = 0; i < dadosTotais; i++) {
            dano += MecanicasRpg.rolarDado(ataque.ladosDado);
        }
        Interface.MostrarMensagem("-> " + atacante.getNome() + " acerta com " + ataque.nome + " causando " + dano + " de dano" + (critico ? " (crítico!)" : "") + "!");
        Interface.Pausa(1500);
        aplicarDanoCriatura(alvo, dano);
        Interface.MostrarMensagem(rotuloCriatura(inimigos, alvo) + " agora tem " + Math.max(0, alvo.getVida()) + " de vida.");
        Interface.Pausa(1500);
    }
}

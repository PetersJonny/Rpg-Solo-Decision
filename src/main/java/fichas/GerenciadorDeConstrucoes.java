package fichas;

import classes.ClasseRpg;
import criaturas.Criatura;
import itens.Arma;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.List;
import mecanicas.MecanicasRpg;
import racas.Raca;
import telas.Interface;

public class GerenciadorDeConstrucoes {

    public static void sincronizarLocalizacao(FichaRpg ficha) {

        ficha.naCabana = ficha.temCabana && ficha.profundidadeFloresta == ficha.profundidadeCabana;
        ficha.naSalaTreino = ficha.temSalaTreino && ficha.profundidadeFloresta == ficha.profundidadeSalaTreino;
        ficha.naMesaMagias = ficha.temMesaMagias && ficha.profundidadeFloresta == ficha.profundidadeMesaMagias;
        ficha.naFogueira = ficha.temFogueira && ficha.profundidadeFloresta == ficha.profundidadeFogueira;

    }
    public static void entrarNaConstrucao(FichaRpg ficha, int profundidadeAlvo) {

        if (ficha.temCabana && ficha.profundidadeCabana == profundidadeAlvo) {
            ficha.naCabana = true;
        }
        if (ficha.temSalaTreino && ficha.profundidadeSalaTreino == profundidadeAlvo) {
            ficha.naSalaTreino = true;
        }
        if (ficha.temMesaMagias && ficha.profundidadeMesaMagias == profundidadeAlvo) {
            ficha.naMesaMagias = true;
        }
        if (ficha.temFogueira && ficha.profundidadeFogueira == profundidadeAlvo) {
            ficha.naFogueira = true;
        }

    }
    public static void recomputarAdjacencias(FichaRpg ficha) {

        ficha.salaJuntoCabana = ficha.temSalaTreino && ficha.temCabana && ficha.profundidadeSalaTreino == ficha.profundidadeCabana;
        ficha.salaJuntoMesa = ficha.temSalaTreino && ficha.temMesaMagias && ficha.profundidadeSalaTreino == ficha.profundidadeMesaMagias;
        ficha.mesaJuntoCabana = ficha.temMesaMagias && ficha.temCabana && ficha.profundidadeMesaMagias == ficha.profundidadeCabana;
        ficha.mesaJuntoSala = ficha.temMesaMagias && ficha.temSalaTreino && ficha.profundidadeMesaMagias == ficha.profundidadeSalaTreino;
        ficha.fogueiraJuntoCabana = ficha.temFogueira && ficha.temCabana && ficha.profundidadeFogueira == ficha.profundidadeCabana;
        ficha.fogueiraJuntoSala = ficha.temFogueira && ficha.temSalaTreino && ficha.profundidadeFogueira == ficha.profundidadeSalaTreino;
        ficha.fogueiraJuntoMesa = ficha.temFogueira && ficha.temMesaMagias && ficha.profundidadeFogueira == ficha.profundidadeMesaMagias;

    }
    public static void sairDaCabana(FichaRpg ficha) {

        if (ficha.temCabana) {
            ficha.naCabana = false;
        }
        ficha.naSalaTreino = false;
        ficha.naMesaMagias = false;
        ficha.naFogueira = false;

    }
    public static void voltarParaCabana(FichaRpg ficha) {

        if (ficha.temCabana) {
            ficha.naCabana = true;
        }
        ficha.naSalaTreino = false;
        ficha.naMesaMagias = false;
        if (ficha.fogueiraJuntoCabana) {
            ficha.naFogueira = true;
        } else {
            ficha.naFogueira = false;
        }

    }
    public static void irParaSalaTreino(FichaRpg ficha) {

        ficha.naSalaTreino = true;
        ficha.naMesaMagias = false;
        ficha.naCabana = ficha.salaJuntoCabana;
        ficha.naFogueira = ficha.fogueiraJuntoSala;

    }
    public static void irParaMesaMagias(FichaRpg ficha) {

        ficha.naMesaMagias = true;
        ficha.naSalaTreino = false;
        ficha.naCabana = ficha.mesaJuntoCabana;
        ficha.naFogueira = ficha.fogueiraJuntoMesa;

    }
    public static void irParaFogueira(FichaRpg ficha) {

        ficha.naFogueira = true;
        ficha.naSalaTreino = false;
        ficha.naMesaMagias = false;
        ficha.naCabana = ficha.fogueiraJuntoCabana;

    }
    public static boolean avancarTempo(FichaRpg ficha, int unidades) {

        ficha.progressoPeriodo += Math.max(0, unidades);
        boolean virou = false;
        while (ficha.progressoPeriodo >= 3) {
            ficha.progressoPeriodo -= 3;
            boolean eraNoite = ficha.ehNoite;
            ficha.ehNoite = !ficha.ehNoite;
            if (ficha.ehNoite) {
                ficha.diasSemDormir++;
            } else {
                ficha.diaAtual++;
                ficha.registrarNovoDiaFome();
                if (ficha.companheiro != null) {
                    ficha.companheiro.passarTempo();
                }
                if (eraNoite && ficha.companheiro != null) {
                                        ficha.registrarDormidaDoCompanheiro();
                }
            }
                        ficha.sobrevivenciaUsada = false;
            ficha.menteAfiadaUsada = false;
                        if (ficha.treinoBonusPeriodosRestantes > 0) {
                ficha.treinoBonusPeriodosRestantes--;
                if (ficha.treinoBonusPeriodosRestantes <= 0) {
                    ficha.treinoBonusAtributo = null;
                }
            }
                        if (ficha.magiaBonusPeriodosRestantes > 0) {
                ficha.magiaBonusPeriodosRestantes--;
            }
                        ficha.aplicarPerdaVidaPorFome();
            virou = true;
        }
        ficha.cansado = ficha.diasSemDormir > 2;
        return virou;

    }
    public static boolean dormir(FichaRpg ficha) {

        if (!ficha.ehNoite) return false;
        if (!ficha.temCabana || !ficha.naCabana) return false;
        ficha.aplicarPerdaVidaPorFome();
        int divisor = ficha.comeuHoje ? 2 : 3;
        int curaVida = ficha.getVidaMaxima() / divisor;
        int curaMana = ficha.getManaMaxima() / divisor;
        if (ficha.temItem("Capa do Viajante")) {
            curaVida += 4;         }
        ficha.setVidaPersonagem(ficha.getVidaPersonagem() + curaVida);
        ficha.setManaPersonagem(ficha.getManaPersonagem() + curaMana);
        ficha.ehNoite = false;
        ficha.progressoPeriodo = 0;
        ficha.diaAtual++;
        ficha.registrarNovoDiaFome();
        ficha.diasSemDormir = 0;
        ficha.cansado = false;
        ficha.naMesaMagias = false;
        ficha.sobrevivenciaUsada = false;
        ficha.menteAfiadaUsada = false;
        ficha.registrarDormidaDoCompanheiro();
        return true;

    }
    public static String descreverFome(FichaRpg ficha) {

        if (ficha.isCheio()) {
            return "CHEIO (" + ficha.getRefeicoesHoje() + "/" + FichaRpg.REFEICOES_POR_DIA + " refeições hoje | não cabe mais nada)";
        }
        if (ficha.refeicoesHoje > 0) {
            return "Saciado (" + ficha.getRefeicoesHoje() + "/" + FichaRpg.REFEICOES_POR_DIA + " refeições hoje)";
        }
        if (ficha.enjoado) {
            return "FAMINTO? " + ficha.getDiasSemComer() + " dias sem comer | ENJOADO (-" + ficha.penalidadeEnjoado + " em testes de Destreza e Força)";
        }
        if (ficha.diasSemComer >= 5) {
            return "FAMINTO " + ficha.getDiasSemComer() + " dias (-" + ficha.getPenalidadeFome() + " em testes; perde " + ficha.getPerdaVidaPorFome() + " por período)";
        }
        if (ficha.diasSemComer >= 1) {
            return "FAMINTO " + ficha.getDiasSemComer() + " dias (-" + ficha.getPenalidadeFome() + " em testes de Destreza e Força)";
        }
        return "";

    }
    public static void registrarNovoDiaFome(FichaRpg ficha) {

        if (!ficha.comeuHoje) {
            ficha.diasSemComer++;
        }
        ficha.comeuHoje = false;
        ficha.frutasComidasHoje = 0;
        ficha.refeicoesHoje = 0;

    }
    public static int aplicarPerdaVidaPorFome(FichaRpg ficha) {

        int perda = ficha.getPerdaVidaPorFome();
        if (perda <= 0) return 0;
        ficha.vidaPersonagem = Math.max(0, ficha.vidaPersonagem - perda);
        return perda;

    }
    public static void comerComidaBoa(FichaRpg ficha) {
        if (ficha.isCheio()) return;
        ficha.refeicoesHoje++;
        ficha.diasSemComer = 0;
        ficha.comeuHoje = true;
        ficha.enjoado = false;
        ficha.penalidadeEnjoado = 0;

    }
    public static void curarEnjoo(FichaRpg ficha) {

        ficha.enjoado = false;
        ficha.penalidadeEnjoado = 0;

    }
    public static void comerCarnePodre(FichaRpg ficha) {
        if (ficha.isCheio()) return;
        ficha.refeicoesHoje++;
        int penalidadeAnterior = ficha.getPenalidadeFome();
        ficha.diasSemComer = 0;
        ficha.comeuHoje = true;
        ficha.enjoado = true;
        ficha.penalidadeEnjoado = Math.max(penalidadeAnterior, 1);

    }
    public static boolean comerCarneCrua(FichaRpg ficha) {
        if (ficha.isCheio()) return false;

        if (MecanicasRpg.rolarDado(100) <= FichaRpg.CHANCE_CARNE_ESTRAGADA) {
            ficha.comerCarnePodre();
            return true;
        }
        ficha.comerComidaBoa();
        return false;

    }
    public static void comerFrutas(FichaRpg ficha, int qtd) {

        if (ficha.isCheio()) return;
        ficha.frutasComidasHoje += Math.max(1, qtd);
        if (ficha.frutasComidasHoje >= FichaRpg.FRUTAS_PARA_REFEICAO) {
            ficha.comerComidaBoa();
        }

    }
    public static boolean montarCabana(FichaRpg ficha) {

        if (ficha.temCabana) return false;
        if (ficha.getQuantidadeDe("Madeira") < 7 || ficha.getQuantidadeDe("Folha") < 10 || ficha.getQuantidadeDe("Pedra") < 4) {
            return false;
        }
        ficha.removerItem("Madeira", 7);
        ficha.removerItem("Folha", 10);
        ficha.removerItem("Pedra", 4);
        ficha.temCabana = true;
        ficha.profundidadeCabana = ficha.profundidadeFloresta;
        ficha.naCabana = true;
        ficha.naSalaTreino = false;
        ficha.naMesaMagias = false;
        ficha.recomputarAdjacencias();
        return true;

    }
    public static boolean construirSalaTreino(FichaRpg ficha) {

        if (ficha.temSalaTreino) return false;
        if (ficha.getQuantidadeDe("Madeira") < 10 || ficha.getQuantidadeDe("Folha") < 15 || ficha.getQuantidadeDe("Pedra") < 5 || ficha.getQuantidadeDe("Couro") < 4) {
            return false;
        }
        ficha.removerItem("Madeira", 10);
        ficha.removerItem("Folha", 15);
        ficha.removerItem("Pedra", 5);
        ficha.removerItem("Couro", 4);
        ficha.temSalaTreino = true;
        ficha.profundidadeSalaTreino = ficha.profundidadeFloresta;
        ficha.recomputarAdjacencias();
        ficha.naSalaTreino = true;
        ficha.naMesaMagias = false;
        return true;

    }
    public static void entrarSalaTreino(FichaRpg ficha) {

        ficha.naSalaTreino = true;
        ficha.naMesaMagias = false;
        if (ficha.salaJuntoCabana) {
            ficha.naCabana = true;
        } else {
            ficha.naCabana = false;
        }
        ficha.naFogueira = ficha.fogueiraJuntoSala;

    }
    public static void treinarAtributo(FichaRpg ficha, String atributo) {

        ficha.treinoBonusAtributo = atributo;
        ficha.treinoBonusPeriodosRestantes = 2;

    }
    public static void terminarTreino(FichaRpg ficha) {

        ficha.naMesaMagias = false;
        if (ficha.salaJuntoCabana) {
            ficha.naCabana = true;
            ficha.naSalaTreino = false;
        } else {
            ficha.naCabana = false;
            ficha.naSalaTreino = true;
        }
        ficha.naFogueira = ficha.fogueiraJuntoSala;

    }
    public static boolean construirMesaMagias(FichaRpg ficha) {

        if (ficha.temMesaMagias) return false;
        if (ficha.getQuantidadeDe("Madeira") < 5 || ficha.getQuantidadeDe("Folha") < 4 || ficha.getQuantidadeDe("Pedra") < 4 || ficha.getQuantidadeDe("Pó da Fada") < 1) {
            return false;
        }
        ficha.removerItem("Madeira", 5);
        ficha.removerItem("Folha", 4);
        ficha.removerItem("Pedra", 4);
        ficha.removerItem("Pó da Fada", 1);
        ficha.temMesaMagias = true;
        ficha.profundidadeMesaMagias = ficha.profundidadeFloresta;
        ficha.recomputarAdjacencias();
        ficha.naMesaMagias = true;
        ficha.naSalaTreino = false;
        ficha.naCabana = ficha.mesaJuntoCabana;
        return true;

    }
    public static boolean montarFogueira(FichaRpg ficha) {

        if (ficha.temFogueira) return false;
        if (ficha.getQuantidadeDe("Madeira") < 4 || ficha.getQuantidadeDe("Folha") < 3) {
            return false;
        }
        ficha.removerItem("Madeira", 4);
        ficha.removerItem("Folha", 3);
        ficha.temFogueira = true;
        ficha.profundidadeFogueira = ficha.profundidadeFloresta;
        ficha.recomputarAdjacencias();
        ficha.naFogueira = true;
        ficha.naSalaTreino = false;
        ficha.naMesaMagias = false;
        ficha.naCabana = ficha.fogueiraJuntoCabana;
        return true;

    }
    public static boolean cozinharTodasAsCarnes(FichaRpg ficha) {

        if (!ficha.podeUsarFogueira()) return false;
        if (ficha.getQuantidadeDe("Madeira") < 2) return false;
        int lobos = ficha.getQuantidadeDe("Carne de Lobo");
        int ursos = ficha.getQuantidadeDe("Carne de Urso");
        if (lobos == 0 && ursos == 0) return false;
        ficha.removerItem("Madeira", 2);
        ficha.cozinharTipoCarne("Carne de Lobo", "Carne de Lobo Cozida", lobos);
        ficha.cozinharTipoCarne("Carne de Urso", "Carne de Urso Cozida", ursos);
        return true;

    }
    public static void cozinharTipoCarne(FichaRpg ficha, String crua, String cozida, int qtd) {

        if (qtd <= 0) return;
        ficha.removerItem(crua, qtd);
        ItemRpg cozido = criaturas.Criatura.criarItemDrop(cozida);
        cozido.setQuantidade(qtd);
        ficha.adicionarItem(cozido);

    }
    public static boolean moverCabana(FichaRpg ficha) {

        if (!ficha.temCabana) return false;
        if (ficha.getQuantidadeDe("Madeira") < 7 || ficha.getQuantidadeDe("Folha") < 10 || ficha.getQuantidadeDe("Pedra") < 4) {
            return false;
        }
        ficha.removerItem("Madeira", 7);
        ficha.removerItem("Folha", 10);
        ficha.removerItem("Pedra", 4);
        ficha.profundidadeCabana = ficha.profundidadeFloresta;
        ficha.naCabana = true;
        ficha.naSalaTreino = false;
        ficha.naMesaMagias = false;
        ficha.recomputarAdjacencias();
        return true;

    }
    public static boolean moverSalaTreino(FichaRpg ficha) {

        if (!ficha.temSalaTreino) return false;
        if (ficha.getQuantidadeDe("Madeira") < 10 || ficha.getQuantidadeDe("Folha") < 15 || ficha.getQuantidadeDe("Pedra") < 5 || ficha.getQuantidadeDe("Couro") < 4) {
            return false;
        }
        ficha.removerItem("Madeira", 10);
        ficha.removerItem("Folha", 15);
        ficha.removerItem("Pedra", 5);
        ficha.removerItem("Couro", 4);
        ficha.profundidadeSalaTreino = ficha.profundidadeFloresta;
        ficha.naSalaTreino = true;
        ficha.naMesaMagias = false;
        ficha.recomputarAdjacencias();
        ficha.naCabana = ficha.salaJuntoCabana;
        return true;

    }
    public static boolean moverMesaMagias(FichaRpg ficha) {

        if (!ficha.temMesaMagias) return false;
        if (ficha.getQuantidadeDe("Madeira") < 5 || ficha.getQuantidadeDe("Folha") < 4 || ficha.getQuantidadeDe("Pedra") < 4 || ficha.getQuantidadeDe("Pó da Fada") < 1) {
            return false;
        }
        ficha.removerItem("Madeira", 5);
        ficha.removerItem("Folha", 4);
        ficha.removerItem("Pedra", 4);
        ficha.removerItem("Pó da Fada", 1);
        ficha.profundidadeMesaMagias = ficha.profundidadeFloresta;
        ficha.naMesaMagias = true;
        ficha.naSalaTreino = false;
        ficha.recomputarAdjacencias();
        ficha.naCabana = ficha.mesaJuntoCabana;
        return true;

    }
    public static boolean moverFogueira(FichaRpg ficha) {

        if (!ficha.temFogueira) return false;
        if (ficha.getQuantidadeDe("Madeira") < 4 || ficha.getQuantidadeDe("Folha") < 3) {
            return false;
        }
        ficha.removerItem("Madeira", 4);
        ficha.removerItem("Folha", 3);
        ficha.profundidadeFogueira = ficha.profundidadeFloresta;
        ficha.naFogueira = true;
        ficha.naSalaTreino = false;
        ficha.naMesaMagias = false;
        ficha.recomputarAdjacencias();
        ficha.naCabana = ficha.fogueiraJuntoCabana;
        return true;

    }
    public static int getProfundidadeConstrucaoMaisProxima(FichaRpg ficha) {

        int p = 0;
        if (ficha.temCabana) p = Math.max(p, ficha.profundidadeCabana);
        if (ficha.temSalaTreino) p = Math.max(p, ficha.profundidadeSalaTreino);
        if (ficha.temMesaMagias) p = Math.max(p, ficha.profundidadeMesaMagias);
        if (ficha.temFogueira) p = Math.max(p, ficha.profundidadeFogueira);
        return p;

    }
    public static void estudarMagia(FichaRpg ficha) {

        ficha.magiaBonusPeriodosRestantes = 2;

    }
}

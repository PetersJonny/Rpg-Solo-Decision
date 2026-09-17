package eventos;

import criaturas.Criatura;
import fichas.FichaRpg;
import itens.Consumivel;
import itens.ItemRpg;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class TimeManager {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String AMARELO = Interface.AMARELO;

    public static void avancarTempoComMensagens(FichaRpg ficha, int unidades) {
        boolean virou = ficha.avancarTempo(unidades);
        if (!virou) {
            if (ficha.getProgressoPeriodo() >= 2) {
                String proximo = ficha.isEhNoite() ? "dia" : "noite";
                Interface.MostrarMensagem("\n(Falta pouco para " + proximo + " chegar: " + (3 - ficha.getProgressoPeriodo()) + "/3 restantes.)");
                Interface.Pausa(1000);
            }
            return;
        }
        Interface.Pausa(1000);
        if (ficha.isEhNoite()) {
            Interface.MostrarMensagem("\nO sol se põe no horizonte e a noite cai sobre Freijord... " + AMARELO + "(" + ficha.getPeriodoDescritivo() + ")" + RESET);
            Interface.Pausa(2000);
            if (ficha.temCompanheiro()) {
                companheiros.Companheiro comp = ficha.getCompanheiro();
                if (!comp.isDormiuPrimeiraVez()) {
                    Interface.MostrarMensagem("\nPela primeira vez, " + comp.getNomeCompleto() + " se acomoda na cabana para dormir. De manhã, volta a te seguir.");
                } else {
                    Interface.MostrarMensagem("\n" + comp.getNomeCompleto() + " continua contigo por mais uma noite.");
                }
                Interface.Pausa(2000);
            }
            if (ficha.isCansado()) {
                Interface.MostrarMensagem("\n(Você está há mais de 2 dias sem dormir! Está cansado: -1 em todos os atributos em testes até dormir.)");
                Interface.Pausa(2000);
            }
        } else {
            Interface.MostrarMensagem("\nOs primeiros raios de sol anunciam o amanhecer... é " + AMARELO + ficha.getPeriodoDescritivo() + RESET + " em Freijord.");
            Interface.Pausa(2000);
            companheiros.CompanionManager.verificarCompanheiroPosDormir(ficha);
        }

        if (ficha.isTemCabana() && !ficha.temCompanheiro() && MecanicasRpg.rolarDado(100) <= 20) {
            companheiros.CompanionManager.EventoPerdido(ficha);
        }
    }

    public static void Explorar(FichaRpg ficha) {
        Interface.cabecalhoMenu("EXPLORAÇÃO");
        Interface.MostrarMensagem("\n  Você adentra as matas geladas da floresta de Freijord...  " + CIANO + "(1/3 de período)" + RESET);
        Interface.Pausa(2000);
        Interface.MostrarMensagem("O vento frio corta entre as árvores e você observa o ambiente ao redor...");
        Interface.Pausa(2000);

        if (ficha.isTemCabana() && ficha.isNaCabana()) {
            Interface.MostrarMensagem("\nVocê deixa sua cabana para trás e se embrenha na floresta.");
            Interface.Pausa(1500);
            ficha.sairDaCabana();
        }

        EventManager.EventoAnimal(ficha);
        avancarTempoComMensagens(ficha, 1);
    }

    public static void BuscarRecursos(FichaRpg ficha) {
        Interface.cabecalhoMenu("BUSCAR RECURSOS");
        Interface.MostrarMensagem("\n  Você percorre a floresta em busca de materiais úteis...  " + CIANO + "(1/3 de período)" + RESET);
        Interface.Pausa(2000);

        if (ficha.isTemCabana() && ficha.isNaCabana()) {
            Interface.MostrarMensagem("\nVocê deixa sua cabana para trás e se afasta em direção aos bosques.");
            Interface.Pausa(1500);
            ficha.sairDaCabana();
        }

        boolean achouAlgo = false;
        achouAlgo |= coletarRecurso(ficha, "Madeira", 40, "Troncos e galhos fortes para construção.");
        achouAlgo |= coletarRecurso(ficha, "Folha", 55, "Folhas secas e verdes, úteis como cobertura.");
        achouAlgo |= coletarRecurso(ficha, "Pedra", 35, "Pedras arredondadas de rio, boas para construir.");
        achouAlgo |= coletarRecurso(ficha, "Frutas", 15, "Frutas silvestres comestíveis. Cada uma cura 1d2 de vida.");

        if (!achouAlgo) {
            Interface.MostrarMensagem("\nVocê vasculhou os arredores, mas não encontrou nada aproveitável desta vez.");
            Interface.Pausa(2000);
        }

        int chanceEncontro = ficha.isEhNoite() ? 50 : 30;
        if (MecanicasRpg.rolarDado(100) <= chanceEncontro) {
            Interface.MostrarMensagem("\nEnquanto recolhe materiais, você percebe um movimento suspeito nas sombras...");
            Interface.Pausa(1500);
            EventManager.EventoAnimal(ficha);
        }

        avancarTempoComMensagens(ficha, 1);
    }

    private static boolean coletarRecurso(FichaRpg ficha, String nome, int chance, String descricao) {
        if (MecanicasRpg.rolarDado(100) > chance) return false;
        int quantidade = MecanicasRpg.rolarEntre(1, 3);
        ItemRpg item = nome.equals("Frutas")
                ? new Consumivel(nome, descricao, quantidade)
                : new ItemRpg(nome, descricao, quantidade);
        ficha.adicionarItem(item);
        Interface.MostrarMensagem("Você encontrou " + quantidade + "x " + nome + "!");
        Interface.Pausa(1200);
        return true;
    }
}

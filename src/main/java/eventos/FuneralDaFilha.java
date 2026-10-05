package eventos;

import fichas.FichaRpg;
import fichas.GerenciadorDeMissoesECompanheiro;
import telas.Interface;

public class FuneralDaFilha {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;
    private static final String VERMELHO = Interface.VERMELHO;

    private static final String MISSAO = "A Filha Perdida";

    public static boolean estaVilaDeserta(FichaRpg ficha) {
        return ficha.isVilaDesertaPorFuneral();
    }

    public static boolean bloquearLojaVazia(FichaRpg ficha, String lugar) {
        if (!estaVilaDeserta(ficha)) return false;

        Interface.MostrarMensagem("\nVocê empurra a porta de " + lugar + " e empurra de novo. Não há ninguém lá dentro: nenhuma mesa posta, nenhuma brasa acesa, nenhum balcão atendido.");
        Interface.Pausa(2000);
        Interface.MostrarMensagem("\n" + VERMELHO + "Não há quem compre, quem venda e quem conte nada." + RESET + " A vila inteira está igual a isso hoje, e hoje não está ninguém em lugar nenhum dela.");
        Interface.Pausa(2200);
        return true;
    }

    public static void dispararSePrecisa(FichaRpg ficha) {
        if (!ficha.funeralPrecisaDisparar()) return;

        ficha.setDiaDoFuneral(ficha.getDiaAtual());
        ficha.setFilhaMorta(true);
        ficha.setMinaFechadaParaReforma(true);
        ficha.setFilhaEncontrada(true);

        Interface.MostrarMensagem("\n" + AMARELO + "No terceiro dia desde que você aceitou o aviso, a vila acordou errada." + RESET);
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\n" + VERDE + "Os guardas desceram à mina de madrugada." + RESET + " Entraram, viram o que havia lá dentro, e voltaram. Não trouxeram ninguém.");
        Interface.Pausa(2400);
        GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Os guardas entraram na mina e voltaram sem ninguém. A filha foi encontrada morta lá dentro.");
        GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "A boca da mina está fechada para reforma. Não dá para entrar.");

        ficha.encerrarMissao(MISSAO);

        CenarioDoPanico(ficha);
    }

    private static void CenarioDoPanico(FichaRpg ficha) {
        Interface.MostrarMensagem("\nVocê abre a porta e para. As ruas, que costumam ter gente atravessando, estão todas viradas para o mesmo lado — e é um lado só.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\nDracônicos vão com passos apressados, alguns quase correndo. Rostos fechados, olhos arregalados, bocas apertadas. Ninguém fala com ninguém. Todos vão para o " + CIANO + "largo da praça" + RESET + ", onde já se juntou um monte de gente.");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nUma mulher passa por você segurando o braço de uma criança e cochicha alto, sem olhar para trás: " + VERMELHO + "\"É a menina da Célia.\"" + RESET);
        Interface.Pausa(2600);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Seguir o povo até o largo");
        System.out.println("  2. Ficar para trás");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

        if (Interface.lerOpcao(2) == 2) {
            Interface.MostrarMensagem("\nVocê não segue. Encosta numa parede e deixa a corrente passar na sua frente, pessoa por pessoa.");
            Interface.Pausa(2400);
            Interface.MostrarMensagem("\nEm poucos minutos a rua esvazia. O barulho dos passos some, depois o burburinho, depois o movimento. As portas se fecham. A vila fica " + AMARELO + "vazia" + RESET + ".");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\nVocê tenta a taverna, o ferreiro, a lojinha de roupas, a barraca: nada. Não há gente em lugar nenhum, e sem gente não há quem venda nem quem fale.");
            Interface.Pausa(2400);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Você não seguiu o povo. A vila esvaziou ao redor e ficou sem ninguém durante o dia inteiro.");
            return;
        }

        Interface.MostrarMensagem("\nVocê entra na corrente e vai junto, seguindo o fluxo até a praça.");
        Interface.Pausa(2200);
        CenarioDoFuneral(ficha);
    }

    private static void CenarioDoFuneral(FichaRpg ficha) {
        Interface.MostrarMensagem("\nO largo está lotado. E quando você chega perto, o que parecia ruído de gente se desfaz no que é de verdade: um " + VERMELHO + "funeral" + RESET + ".");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nNo centro, sobre uma mesa baixa, está a filha da Célia: uma menina dracônica pequena, de escamas ainda claras, deitada com as mãos cruzadas sobre o peito. Ninguém alisou o cabelo dela. Ao redor, gente que você não conhece segura a mão de gente que você não conhece.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nNão há caixão. Não há padre. Só um silêncio que não cabe na praça, e o som de alguém chorando em segundo plano, sem nunca parar de vez.");
        Interface.Pausa(3000);

        VerQuemEstaLa(ficha);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. " + VERDE + "Ficar para o funeral" + RESET);
        System.out.println("  2. " + CIANO + "Voltar para a vila" + RESET + " sem ficar");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

        if (Interface.lerOpcao(2) == 1) {
            ficha.setFuneralApresenciado(true);
            Interface.MostrarMensagem("\nVocê fica. Encontra um lugar na borda do largo, encostado num muro, e não se mexe dali.");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\nNão dura muito. A Célia se aproxima da mesa, olha a filha por um tempo que parece longo, e toca a testa dela com dois dedos. Depois se afasta, porque as pernas não seguram.");
            Interface.Pausa(3000);
            Interface.MostrarMensagem("\nUma criança pequena pergunta bem alto, na fila, por que a tia está chorando se a menina não está mais aí. Alguém tapa a própria boca e muda de assunto.");
            Interface.Pausa(3000);
            Interface.MostrarMensagem("\nUm homem mais velho começa a dizer baixinho o nome dela. Outro entra. Mais outro. Os nomes passando de boca em boca pelo largo, cada um dizendo o nome dela uma vez, até o nome virar a única coisa que existe ali.");
            Interface.Pausa(3400);
            Interface.MostrarMensagem("\nEm algum momento o largo começa a esvaziar. A gente vai embora em silêncio, devagar, e a praça fica só com a mesa no meio.");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\nVocê se levanta. Não sobrou nada para fazer, e mesmo que sobrasse, não seria coisa sua a fazer.");
            Interface.Pausa(2600);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Você ficou no funeral. A Celia tocou a testa da filha e foi preciso afastá-la. A vila inteira passou o dia chamando o nome da menina.");
        } else {
            Interface.MostrarMensagem("\nVocê não fica. Se vira e sai do largo antes que a multidão se feche de novo, voltando pelas ruas até a vila.");
            Interface.Pausa(2600);
            Interface.MostrarMensagem("\nÉ a mesma vila de sempre, e não é: sem ninguém, cada porta fechada é uma porta fechada mesmo. Nada abre, nada atende, ninguém para quem chegue.");
            Interface.Pausa(2600);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, MISSAO, "Você viu o funeral de longe e voltou para a vila. Ninguém ficou para falar com você.");
        }

        Interface.MostrarMensagem("\n" + AMARELO + "Você volta para a vila, e a vila continua vazia." + RESET);
        Interface.Pausa(2200);
    }

    private static void VerQuemEstaLa(FichaRpg ficha) {
        boolean alguem = false;

        if (ficha.isDonoDaTavernaAgradeceu()) {
            alguem = true;
            Interface.MostrarMensagem("\nNum dos lados do largo, você vê " + AMARELO + "Draven" + RESET + ", o dracônico de pele vermelha do balcão da taverna. Está sem o avental e não se move, só olha.");
            Interface.Pausa(2600);
        }
        if (ficha.isAlfaiatariaConhecida()) {
            alguem = true;
            Interface.MostrarMensagem("\n" + AMARELO + "Célia Morel" + RESET + " está ajoelhada diante da mesa, com a agulha ainda na mão, como se tivesse vindo direto do trabalho e não da costura.");
            Interface.Pausa(2600);
        }
        if (ficha.isFerreiroSeApresentou()) {
            alguem = true;
            Interface.MostrarMensagem("\n" + AMARELO + "Gorak" + RESET + ", o ferreiro, está no fundo da multidão, braços cruzados, com a mão grande ainda arranhada de uma queimadura que ele não tratou.");
            Interface.Pausa(2600);
        }
        if (ficha.isChapeuMagicoConhecido()) {
            alguem = true;
            Interface.MostrarMensagem("\n" + AMARELO + "Dona Maga" + RESET + " está sentada no degrau de uma casa próxima, as mãos no colo, olhando para o chão.");
            Interface.Pausa(2600);
        }
        if (ficha.isVelhinhaEncontrada()) {
            alguem = true;
            Interface.MostrarMensagem("\nA " + AMARELO + "velha" + RESET + " de xale surrado está na primeira fila, em pé, e é a única pessoa ali que parece ter vindo para ficar.");
            Interface.Pausa(2600);
        }

        if (!alguem) {
            Interface.MostrarMensagem("\nVocê não reconhece ninguém naquela multidão. São rostos que você nunca viu e provavelmente nunca mais vai ver.");
            Interface.Pausa(2600);
        }
    }
}
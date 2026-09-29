package eventos;

import java.util.List;

import fichas.FichaRpg;
import fichas.GerenciadorDeMissoesECompanheiro;
import itens.ItemRpg;
import loja.Vendedor;
import telas.Interface;

public class BarracaDeFrutas {

    private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;
    private static final String VERDE = Interface.VERDE;
    private static final String AMARELO = Interface.AMARELO;

    private static final List<String> FRUTAS = List.of(
            "Maçã",
            "Pera",
            "Ameixa",
            "Uva",
            "Morango Selvagem",
            "Figo Seco"
    );

    public static void BarracaDeFrutas(FichaRpg ficha) {
        Interface.cabecalhoMenu("BARRACA DE FRUTAS");
        Interface.MostrarMensagem("\nPerto da entrada da vila, uma barraca simples de madeira enche o ar com o cheiro doce de frutas frescas. Cestas de maçãs, peras e ameixas se empilham sob o toldo de lona.");
        Interface.Pausa(2200);

        boolean missaoNeta = ficha.isMissaoAceita("A Neta Perdida");
        if (missaoNeta) {
            if (ficha.isNetaEncontrada() && !ficha.isMissaoNetaEncerrada()) {
                if (ficha.isNetaMorta()) {
                    FinalNetaMorta(ficha);
                } else {
                    FinalNetaViva(ficha);
                }
                return;
            }
            if (!ficha.isNetaEncontrada()) {
                Interface.MostrarMensagem("A velhinha do xale surrado se inclina no balcão, esperançosa: \"Querido? Alguma notícia da minha netinha?\"");
                Interface.Pausa(1800);
                Interface.MostrarMensagem("Você balança a cabeça: \"Ainda não, senhora... Só vim dar uma olhada.\" Ela assente, triste: \"Tudo bem, querido. Fica à vontade. E se precisar de algo, eu estou aqui.\"");
                Interface.Pausa(2400);
            }
        } else {
            Interface.MostrarMensagem("A velhinha do xale surrado penteia as frutas, sorrindo: \"Boas frutas frescas, forasteiro! Colhidas na mata da entrada.\"");
            Interface.Pausa(2000);
        }

        while (true) {
            Interface.cabecalhoMenu("BARRACA DE FRUTAS");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + " moedas.\n");
            System.out.println("  1. Comprar frutas");
            System.out.println("  2. Olhar ao redor, procurando pistas");
            System.out.println("  3. Voltar para a vila");
            System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);

            int escolha = Interface.lerOpcao(3);
            if (escolha == 1) {
                ComprarFrutas(ficha);
            } else if (escolha == 2) {
                Interface.MostrarMensagem("\nVocê percorre os arredores com o olhar: a barraca, as cestas de frutas, a estrada de terra que leva à mata... Tudo parece em ordem — sem pegadas, sem sinais de luta.");
                Interface.Pausa(2400);
                if (missaoNeta) {
                    Interface.MostrarMensagem("A velhinha acompanha o seu olhar e comenta: \"Ela foi vista pela última vez ali, na mata da entrada. Se a menina entrou por algum lado, foi por ali.\"");
                    Interface.Pausa(2400);
                    GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Neta Perdida", "A netinha foi vista pela última vez na mata da entrada da vila, perto da barraca.");
                }
                Interface.MostrarMensagem("Por enquanto, não há pistas novas por aqui.");
                Interface.Pausa(1600);
            } else {
                Interface.MostrarMensagem("\nVocê se despede da velhinha, que acena com a cabeça, esperançosa.");
                Interface.Pausa(1600);
                return;
            }
        }
    }

        private static void FinalNetaViva(FichaRpg ficha) {
        Interface.cabecalhoMenu("DE VOLTA À VILA");
        Interface.MostrarMensagem("\nVocês chegam à entrada da vila. A menina segura na barra da sua roupa e não solta.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\nDois " + AMARELO + "guardas do vilarejo" + RESET + " chegam correndo, lanças na mão. Vendo a menina, a mais nova grita e se esconde atrás de você.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\n\"Que diabos aconteceu com você?\" — um deles pergunta, mas o outro já olha para a criança e baixa a arma.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("\nVocê conta: a mata, as pegadas, os bandidos, a cabana, o homem de roupas de médico. Nenhum dos dois interrompe.");
        Interface.Pausa(2800);
        Interface.MostrarMensagem("\n\"O que é isso?\" — o primeiro guarda aponta para a própria roupa, onde você está ensanguentado.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("\n\"Leva ela até a avó, agora\", o segundo corta. \"E depois volta para a gente. Tem mais coisa nessa cabana.\"");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nOs guardas conduzem você e a menina até a barraca. A velhinha está lá, penteando frutas como se nada tivesse acontecido.");
        Interface.Pausa(2600);
        Interface.MostrarMensagem("\nEntão ela ergue o rosto, vê a criança, e todo o corpo dela treme.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\nA velhinha solta as frutas, abre a boca sem som, e chora. Chora como quem não chorava há sete dias.");
        Interface.Pausa(2800);
        Interface.MostrarMensagem("\nA menina corre e se joga nos braços da avó. \"Voltei, avó. Voltei.\" E a velhinha a abraça com força, como se a mata fosse tentar tirar de novo.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nDepois de um tempo, a velha se recompõe, enxuga os olhos no xale surrado e se vira para você. Tira do avental uma fruta escura, quase preta, que brilha de um jeito errado.");
        Interface.Pausa(2800);
        Interface.MostrarMensagem("\n\"Tome, querido. Não sei o que é, mas é tudo o que eu tenho.\" Ela aperta a fruta na sua mão. \"Só use em momento de extrema urgência. Nunca se sabe.\"");
        Interface.Pausa(2800);

        ItemRpg fruta = Vendedor.criarItem("Fruta do Diabo");
        if (ficha.getEspacoLivreMochila() < fruta.getPeso()) {
            Interface.MostrarMensagem("\nSua mochila está cheia demais e a fruta não cabe. A velhinha a embala num pano e você a guarda junto ao corpo.");
            Interface.Pausa(2200);
            Interface.MostrarMensagem("\n(A " + AMARELO + "Fruta do Diabo" + RESET + " ficou com a velhinha. Volte à barraca para recebê-la.)");
        } else {
            ficha.adicionarItem(fruta);
            Interface.MostrarMensagem("\nVocê recebe: " + AMARELO + "Fruta do Diabo" + RESET + ".");
        }
        Interface.Pausa(2200);
        GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Neta Perdida", "Você entregou a netinha viva à velhinha e recebeu dela a Fruta do Diabo.");
        ficha.setMissaoNetaEncerrada(true);
        Interface.Pausa(2000);
    }

    private static void FinalNetaMorta(FichaRpg ficha) {
        Interface.cabecalhoMenu("DE VOLTA À VILA");
        Interface.MostrarMensagem("\nVocê chega à entrada do vilarejo. Os guardas do turno estão ali.");
        Interface.Pausa(2200);
        Interface.MostrarMensagem("\nEles te veem chegando e um deles grita. Todos Levantam a lança na sua direção.");
        Interface.Pausa(2400);
        if (ficha.isNetaCorpoLevado()) {
            Interface.MostrarMensagem("\n\"O que é isso nas suas costas?\"");
            Interface.Pausa(2200);
        } else {
            Interface.MostrarMensagem("\n\"Você veio da mata? O que aconteceu?\"");
            Interface.Pausa(2200);
        }
        Interface.MostrarMensagem("\nVocê conta. A mata, as pegadas, os bandidos, a cabana, o homem de roupas de médico, e que quando você chegou a criança já estava morta no chão.");
        Interface.Pausa(3000);
        Interface.MostrarMensagem("\nOs guardas se entreolham. Um deles larga a lança no ombro e aponta para a mata.");
        Interface.Pausa(2400);
        Interface.MostrarMensagem("\n\"Vamos lá\", ele diz. \"Depois você explica tudo com mais calma.\" E saem correndo na direção da cabana.");
        Interface.Pausa(2600);

        System.out.println("\n  O que você faz?\n");
        System.out.println("  1. Ir até a velhinha contar o que aconteceu");
        System.out.println("  2. Deixar os guardas resolverem e seguir em frente");
        System.out.println("\n  " + VERDE + "Digite a opção:" + RESET);
        if (Interface.lerOpcao(2) == 1) {
            Interface.MostrarMensagem("\nVocê caminha até a barraca. A velhinha penteia frutas, ainda esperando.");
            Interface.Pausa(2400);
            Interface.MostrarMensagem("\nQuando te vê, abre um sorriso de esperança que morre no meio do caminho, ao ver o pano que você carrega.");
            Interface.Pausa(2600);
            if (ficha.isNetaCorpoLevado()) {
                Interface.MostrarMensagem("\nVocê coloca a criança nos braços dela e conta tudo.");
            } else {
                Interface.MostrarMensagem("\nVocê conta tudo e explica que deixou o corpo na cabana, num pano, porque não conseguia carregar e ainda precisava sair de lá.");
            }
            Interface.Pausa(2800);
            Interface.MostrarMensagem("\nA velhinha não grita. Não se debate. Ela apenas olha para a neta por um tempo muito longo, e as lágrimas caem em silêncio sobre o rosto da menina.");
            Interface.Pausa(3000);
            Interface.MostrarMensagem("\nDepois, com as mãos tremendo, ela embala a netinha num pano e diz baixinho que vai enterrá-la perto das macieiras, onde a avó dela também dorme.");
            Interface.Pausa(2800);
            Interface.MostrarMensagem("\n\"Obrigada por trazer ela até aqui, querido\", ela diz, e é a última coisa que você ouve antes de se afastar.");
            Interface.Pausa(2800);
            GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Neta Perdida", "Você entregou o corpo da netinha à velhinha, que chorou em silêncio e prometeu enterrá-la junto às macieiras.");
        } else {
            Interface.MostrarMensagem("\nVocê deixa os guardas irem e fica olhando a vila por um momento. Depois volta ao normal.");
            Interface.Pausa(2400);
        }
        GerenciadorDeMissoesECompanheiro.registrarNovidade(ficha, "A Neta Perdida", "Os guardas do vilarejo foram até a cabana depois que você contou o que aconteceu.");
        ficha.setMissaoNetaEncerrada(true);
        Interface.Pausa(2000);
    }

    private static void ComprarFrutas(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("COMPRAR FRUTAS");
            Interface.MostrarMensagem("\n  Seu ouro: " + AMARELO + ficha.getOuro() + RESET + "\n");

            for (int i = 0; i < FRUTAS.size(); i++) {
                String fruta = FRUTAS.get(i);
                int preco = Vendedor.precoBase(fruta);
                System.out.println("  " + (i + 1) + ". " + fruta + " - " + preco + " ouro");
            }
            System.out.println("\n  " + VERDE + "0. Voltar" + RESET);

            System.out.println("\n  Escolha uma fruta:");
            int escolha = Interface.lerOpcao(0, FRUTAS.size());
            if (escolha == 0) return;

            String fruta = FRUTAS.get(escolha - 1);
            int preco = Vendedor.precoBase(fruta);

            System.out.println("  Quantidade para comprar (1 a " + (ficha.getOuro() / Math.max(1, preco)) + "): ");
            int qtd = Interface.lerInteiro();
            if (qtd < 1) {
                Interface.ExibirErro("Quantidade inválida!");
                continue;
            }

            ItemRpg item = Vendedor.criarItem(fruta);
            int custo = preco * qtd;
            double espaco = item.getPeso() * qtd;
            if (espaco > ficha.getEspacoLivreMochila() + 0.0001) {
                Interface.ExibirErro("Sua mochila não tem espaço para " + qtd + "x " + fruta + "! (livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
                Interface.Pausa(1500);
                continue;
            }
            if (!ficha.gastarOuro(custo)) {
                Interface.ExibirErro("Ouro insuficiente! (Precisa de " + custo + ")");
                Interface.Pausa(1500);
                continue;
            }

            item.setQuantidade(qtd);
            ficha.adicionarItem(item);
            Interface.MostrarMensagem("\nVocê comprou " + qtd + "x " + fruta + " por " + custo + " ouro. (Guardado na mochila.)");
            Interface.Pausa(1800);
        }
    }
}

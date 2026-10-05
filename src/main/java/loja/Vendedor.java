package loja;

import fichas.FichaRpg;
import itens.Arma;
import itens.Armadura;
import itens.Consumivel;
import itens.ItemRpg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mecanicas.MecanicasRpg;
import telas.Interface;

public class Vendedor {

        private static final String RESET = Interface.RESET;
    private static final String CIANO = Interface.CIANO;

                    private static final List<String> GERAL = List.of(
            "Faca", "Machado", "Machadinha", "Martelo", "Porrete", "Mangual", "Arco", "Flechas", "Lança",
            "Poção de Mana", "Kit Médico",
            "Madeira", "Folha", "Pedra", "Frutas"
    );
    private static final List<String> PESADO = List.of(
            "Espada", "Espada Pesada", "Machado de Guerra", "Martelo de Guerra", "Armadura Pesada"
    );
    private static final List<String> AGIL = List.of(
            "Bisturi", "Arco Refinado", "Nunchako", "Foice"
    );
    private static final List<String> MAGICO = List.of(
            "Cajado", "Chapéu Mágico", "Poção Grande de Mana", "Pequeno Grimório",
            "Manto do Astrólogo", "Gema de Mana", "Amuleto do Coração", "Ampulheta de Prata",
            "Luvas de Prata", "Pó de Midas", "Gota de Veneno"
    );

        private static final List<String> ARMAS_DO_JOGO = List.of(
            "Faca", "Machado", "Machadinha", "Martelo", "Porrete", "Mangual", "Arco", "Lança", "Espada",
            "Espada Pesada", "Machado de Guerra", "Martelo de Guerra", "Bisturi", "Arco Refinado",
            "Nunchako", "Foice", "Cajado"
    );

    private static final int TAMANHO_ESTOQUE = 5;

        public static ItemRpg sortearArmaDoJogo() {
        String nome = ARMAS_DO_JOGO.get(MecanicasRpg.rolarDado(ARMAS_DO_JOGO.size()) - 1);
        ItemRpg item = criarItem(nome);
        if (item == null) return new Arma("Faca", "Uma faca afiada que causa 1d4 de dano corpo a corpo, usando Destreza.", "CaC", 4, 1, 1, "Destreza");
        return item;
    }

    public static void EncontrarVendedor(FichaRpg ficha) {
        Interface.barraDivisoria();
        Interface.MostrarMensagem("\nEntre as árvores, um vendedor ambulante coberto de peles surge na sua frente!");
        Interface.MostrarMensagem("\"Grandes novidades, aventureiro! Tenho tudo o que se pode sobreviver por aqui... e compro o que sobra.\"");
        Interface.Pausa(2500);

                Map<String, Integer> estoque = montarEstoque(ficha);

        while (true) {
            Interface.cabecalhoMenu("VENDEDOR AMBULANTE");
            Interface.MostrarMensagem("\n  Seu ouro: " + ficha.getOuro() + " moedas.\n");
            System.out.println("  1. Comprar");
            System.out.println("  2. Vender");
            System.out.println("  3. Sair\n");
            System.out.println("  Escolha uma opção:");

            int escolha = Interface.lerOpcao(3);
            if (escolha == 1) {
                Comprar(ficha, estoque);
            } else if (escolha == 2) {
                Vender(ficha);
            } else if (escolha == 3) {
                Interface.MostrarMensagem("\nVocê se despede do vendedor e segue seu caminho.");
                Interface.Pausa(1500);
                return;
            } else {
                Interface.ExibirErro("Opção inválida!");
            }
        }
    }

            private static int quantidadeEmEstoque(String nome) {
        switch (nome) {
            case "Flechas":
                return MecanicasRpg.rolarEntre(6, 24);
            case "Poção de Mana":
            case "Poção Grande de Mana":
            case "Kit Médico":
            case "Frutas":
            case "Madeira":
            case "Folha":
            case "Pedra":
                return MecanicasRpg.rolarEntre(1, 7);
            default:
                return 1;         }
    }

                private static Map<String, Integer> montarEstoque(FichaRpg ficha) {
        List<String> pool = new ArrayList<>(GERAL);
        Collections.shuffle(pool);

        Map<String, Integer> estoque = new LinkedHashMap<>();
        int limite = Math.min(TAMANHO_ESTOQUE, pool.size());
        for (int i = 0; i < limite; i++) {
            String nome = pool.get(i);
            estoque.put(nome, quantidadeEmEstoque(nome));
        }
        return estoque;
    }

    private static void Comprar(FichaRpg ficha, Map<String, Integer> estoque) {

        while (true) {
            if (estoque.isEmpty()) {
                Interface.MostrarMensagem("\nO vendedor não tem mais nada à venda.");
                Interface.Pausa(1500);
                return;
            }

            Interface.cabecalhoMenu("COMPRAR");
            Interface.MostrarMensagem("\n  Seu ouro: " + ficha.getOuro() + "\n");
            List<String> nomes = new ArrayList<>(estoque.keySet());
            for (int i = 0; i < nomes.size(); i++) {
                String nome = nomes.get(i);
                int qtd = estoque.get(nome);
                String preco = nome.equals("Flechas") ? precoDeVenda(nome) + " ouro/un." : precoDeVenda(nome) + " ouro";
                System.out.println("  " + (i + 1) + ". " + nome + " - " + preco + " (estoque: " + qtd + ")");
            }
            System.out.println("\n  Escolha um item para comprar (ou " + CIANO + "0" + RESET + " para Voltar):");

            int escolha = Interface.lerOpcao(0, nomes.size());
            if (escolha == 0) return;

            String nome = nomes.get(escolha - 1);
            int qtdEstoque = estoque.get(nome);
            int preco = precoDeVenda(nome);
            if (preco < 0) {
                Interface.ExibirErro("Esse item não está disponível!");
                continue;
            }

            int qtdComprar = 1;
            if (podeComprarEmQuantidade(nome)) {
                System.out.println("  Quantidade para comprar (1 a " + qtdEstoque + "): ");
                int qtd = Interface.lerInteiro();
                if (qtd < 1) {
                    Interface.ExibirErro("Quantidade inválida!");
                    continue;
                }
                qtdComprar = Math.min(qtd, qtdEstoque);
            }

            int custo = preco * qtdComprar;

                        ItemRpg itemDetalhe = criarItem(nome);
            double pesoItem = itemDetalhe != null ? itemDetalhe.getPeso() : 1.0;
            double espacoNecessario = pesoItem * qtdComprar;
            if (espacoNecessario > ficha.getEspacoLivreMochila() + 0.0001) {
                Interface.ExibirErro("Sua mochila não tem espaço para " + qtdComprar + "x " + nome + "! (cabe " + (int) Math.floor(ficha.getEspacoLivreMochila() / pesoItem) + "x, livre: " + String.format("%.1f", ficha.getEspacoLivreMochila()) + ")");
                Interface.Pausa(1500);
                continue;
            }
            System.out.println("\n  " + CIANO + "-- " + nome.toUpperCase() + " (x" + qtdComprar + ") --" + RESET);
            if (itemDetalhe != null) {
                System.out.println("  Descrição: " + itemDetalhe.getDescricao());
            }
            System.out.println("  Preço: " + custo + " ouro");
            System.out.println(CIANO + "  -----------------------" + RESET);
            System.out.println("\n  Deseja comprar este item?\n");
            System.out.println("  1. Sim");
            System.out.println("  2. Não");
            int confirmar = Interface.lerOpcao(2);
            if (confirmar != 1) {
                Interface.MostrarMensagem("\nCompra cancelada.");
                Interface.Pausa(1000);
                continue;
            }

            if (!ficha.gastarOuro(custo)) {
                Interface.ExibirErro("Ouro insuficiente! (Precisa de " + custo + ")");
                Interface.Pausa(1500);
                continue;
            }

            ItemRpg item = criarItem(nome);
            item.setQuantidade(qtdComprar);
            if (item instanceof Armadura) {
                int bonusAtual = ficha.getArmaduraEquipada() != null ? ficha.getArmaduraEquipada().getBonusDefesa() : 0;
                ficha.adicionarItem(item);
                ficha.equiparMelhorArmadura();
                int bonusNovo = ficha.getArmaduraEquipada() != null ? ficha.getArmaduraEquipada().getBonusDefesa() : 0;
                Interface.MostrarMensagem("\nVocê comprou: " + nome + " por " + custo + " ouro.");
                Interface.Pausa(1500);
                if (bonusNovo > bonusAtual) {
                    Interface.MostrarMensagem("(Sua melhor armadura foi equipada automaticamente! Defesa: " + ficha.getDefesa() + ")");
                } else {
                    Interface.MostrarMensagem("(O item foi guardado na mochila.)");
                }
            } else {
                ficha.adicionarItem(item);
                Interface.MostrarMensagem("\nVocê comprou " + qtdComprar + "x " + nome + " por " + custo + " ouro.");
            }
            Interface.Pausa(1500);

                        int restante = qtdEstoque - qtdComprar;
            if (restante <= 0) {
                estoque.remove(nome);
            } else {
                estoque.put(nome, restante);
            }
        }
    }

        private static boolean podeComprarEmQuantidade(String nome) {
        switch (nome) {
            case "Flechas":
            case "Poção de Mana":
            case "Poção Grande de Mana":
            case "Kit Médico":
            case "Frutas":
            case "Madeira":
            case "Folha":
            case "Pedra":
                return true;
            default:
                return false;
        }
    }

        private static void Vender(FichaRpg ficha) {
        while (true) {
            Interface.cabecalhoMenu("VENDER");
            Interface.MostrarMensagem("\n  Seu ouro: " + ficha.getOuro() + "");
            Interface.MostrarMensagem("  O vendedor paga 70% do valor de cada item.");

            List<ItemRpg> vendaveis = new ArrayList<>();
            for (ItemRpg item : ficha.getInventario()) {
                if (item.getQuantidade() > 0
                        && !item.getNome().equals("Olho Demoníaco")) {
                    vendaveis.add(item);
                }
            }

            if (vendaveis.isEmpty()) {
                Interface.MostrarMensagem("  Você não possui itens para vender.");
                Interface.Pausa(1500);
                return;
            }

            System.out.println();
            for (int i = 0; i < vendaveis.size(); i++) {
                ItemRpg item = vendaveis.get(i);
                System.out.println("  " + (i + 1) + ". " + item.getNome() + " (x" + item.getQuantidade() + ") - " + precoDeCompra(item.getNome()) + " ouro/un.");
            }
            System.out.println("\n  Escolha um item para vender (ou " + CIANO + "0" + RESET + " para Voltar):");

            int escolha = Interface.lerOpcao(0, vendaveis.size());
            if (escolha == 0) return;

            ItemRpg item = vendaveis.get(escolha - 1);
            int quantidade = item.getQuantidade();
            if (quantidade > 1 && !item.getNome().equals("Kit Médico")) {
                System.out.println("  Quantidade para vender (1 a " + quantidade + "): ");
                int qtd = Interface.lerInteiro();
                if (qtd < 1 || qtd > quantidade) {
                    Interface.ExibirErro("Quantidade inválida!");
                    continue;
                }
                quantidade = qtd;
            }

            int total = precoDeCompra(item.getNome()) * quantidade;
            ficha.adicionarOuro(total);
            ficha.removerItem(item.getNome(), quantidade);
            Interface.MostrarMensagem("\nVocê vendeu " + quantidade + "x " + item.getNome() + " por " + total + " ouro.");
            Interface.Pausa(1500);
        }
    }

        private static int precoDeVenda(String nome) {
        switch (nome) {
            case "Faca": return 30;
            case "Cutelo": return 260;
            case "Fruta do Diabo": return 500;
            case "Machado": return 55;
            case "Machadinha": return 30;
            case "Martelo": return 80;
            case "Porrete": return 40;
            case "Mangual": return 55;
            case "Arco": return 65;
            case "Flechas": return 5;
            case "Lança": return 80;
            case "Armadura Leve": return 230;
            case "Poção de Mana": return 18;
            case "Kit Médico": return 25;
            case "Espada": return 65;
            case "Espada Pesada": return 110;
            case "Machado de Guerra": return 140;
            case "Martelo de Guerra": return 140;
            case "Armadura Pesada": return 450;
            case "Bisturi": return 40;
            case "Arco Refinado": return 95;
            case "Nunchako": return 70;
            case "Foice": return 85;
case "Cajado": return 50;
            case "Chapéu Mágico": return 70;
            case "Poção Grande de Mana": return 30;
            case "Pequeno Grimório": return 90;
            case "Manto do Astrólogo": return 150;
            case "Gema de Mana": return 100;
            case "Amuleto do Coração": return 110;
            case "Ampulheta de Prata": return 80;
            case "Luvas de Prata": return 90;
            case "Pó de Midas": return 70;
            case "Gota de Veneno": return 40;
            case "Capa do Viajante": return 80;
            case "Túnica de Aventureiro": return 90;
            case "Manto do Atirador": return 90;
            case "Lenço de Seda": return 60;
            case "Botas de Correio": return 70;
            case "Maçã": return 6;
            case "Pera": return 8;
            case "Ameixa": return 7;
            case "Uva": return 9;
            case "Morango Selvagem": return 12;
            case "Figo Seco": return 5;
            case "Madeira": return 6;
            case "Folha": return 4;
            case "Pedra": return 5;
            case "Frutas": return 5;
                        case "Sopa do Vilarejo": return 6;
            case "Pão Quente com Manteiga": return 4;
            case "Ovos Mexidos": return 7;
            case "Caldo de Lobo": return 9;
            case "Peixe Assado": return 9;
            case "Estofado de Urso": return 13;
            case "Torta de Frutas": return 7;
            case "Hidromel": return 8;
            default: return -1;
        }
    }

        public static int precoBase(String nome) {
        return precoDeVenda(nome);
    }

                private static int precoDeCompra(String nome) {
        return valorCheio(nome) * 70 / 100;
    }

            private static int valorCheio(String nome) {
switch (nome) {
            case "Flechas": return 5;
            case "Couro": return 6;
            case "Dente de Urso": return 14;
            case "Pó da Fada": return 75;
            case "Osso": return 23;
            case "Carne de Lobo": return 10;
            case "Carne de Urso": return 18;
            case "Carne de Lobo Cozida": return 14;
            case "Carne de Urso Cozida": return 24;
            case "Carne Podre": return 15;
            case "Coroa do Rei": return 1000;
            case "Chifre de Minotauro": return 150;
            case "Espada do Minotauro": return 1250;
            case "Espada Majestral": return 700;
            case "Cajado de Sangue": return 1500;
            default:
                int preco = precoDeVenda(nome);
                return preco < 0 ? 1 : preco;
        }
    }

                    public static int precoDeCompraMelhorado(String nome) {
        return (int) Math.round(valorCheio(nome) * 0.8);
    }

        public static ItemRpg criarItem(String nome) {
        switch (nome) {
            case "Flechas":
                return new ItemRpg("Flechas", "Munição para arcos e foices.", 1);
            case "Faca":
                return new Arma("Faca", "Uma faca afiada que causa 1d4 de dano corpo a corpo, usando Destreza.", "CaC", 4, 1, 1, "Destreza");
            case "Cutelo":
                return new Arma("Cutelo", "O cutelo do Mago Macabro. Causa 2d8 de dano usando Força e deixa o alvo sangrando (1d6 por rodada).", "CaC", 8, 2, 1, "Força");
            case "Sopa do Vilarejo":
                return new Consumivel("Sopa do Vilarejo", "Uma sopa quente de legumes da vila. Cura 1d2 de vida e sacia a fome.", 1);
            case "Pão Quente com Manteiga":
                return new Consumivel("Pão Quente com Manteiga", "Um pão fresquinho com manteiga. Sacia a fome (não cura vida).", 1);
            case "Ovos Mexidos":
                return new Consumivel("Ovos Mexidos", "Ovos mexidos quentes. Cura 1d3 de vida e sacia a fome.", 1);
            case "Caldo de Lobo":
                return new Consumivel("Caldo de Lobo", "Um caldo encorpado de carne de lobo. Cura 1d4 de vida e sacia a fome.", 1);
            case "Peixe Assado":
                return new Consumivel("Peixe Assado", "Peixe do rio assado na brasa. Cura 1d4 de vida e sacia a fome.", 1);
            case "Estofado de Urso":
                return new Consumivel("Estofado de Urso", "Um estofado generoso de carne de urso. Cura 1d6 de vida e sacia a fome.", 1);
            case "Torta de Frutas":
                return new Consumivel("Torta de Frutas", "Uma torta doce de frutas. Cura 1d3 de vida e sacia a fome.", 1);
            case "Hidromel":
                return new Consumivel("Hidromel", "Uma bebida fermentada de mel. Restaura 1d4 de mana (não sacia a fome).", 1);
            case "Machado":
                return new Arma("Machado", "Um machado robusto que causa 1d6 de dano, usando Força.", "CaC", 6, 1, 1);
            case "Machadinha":
                return new Arma("Machadinha", "Uma machadinha leve que causa 1d4 de dano, usando Força.", "CaC", 4, 1, 1);
            case "Martelo":
                return new Arma("Martelo", "Um martelo pesado que causa 1d8 de dano, usando Força.", "CaC", 8, 1, 1);
            case "Porrete":
                return new Arma("Porrete", "Um porrete pesado de madeira maciça que causa 1d8 de dano, usando Força.", "CaC", 8, 1, 1);
            case "Mangual":
                return new Arma("Mangual", "Um mangual de corrente que causa 1d6 de dano, usando Força.", "CaC", 6, 1, 1);
            case "Arco":
                return new Arma("Arco", "Um arco de madeira que dispara flechas, causando 1d6 de dano à distância. Consome flechas.", "LA", 6, 1, 1);
            case "Lança":
                return new Arma("Lança", "Uma lança versátil que causa 1d6 de dano, usando seu maior atributo entre Força e Destreza.", "CaC", 6, 1, 1, "Ágil", true);
            case "Espada":
                return new Arma("Espada", "Uma espada de aço afiada que causa 1d8 de dano.", "CaC", 8, 1, 1);
            case "Espada Pesada":
                return new Arma("Espada Pesada", "Uma espada gigante que causa 1d10 de dano, usando Força.", "CaC", 10, 1, 1);
            case "Espada Jurada":
                return new Arma("Espada Jurada", "A lâmina de Balthazar, imbuída com o poder do Vazio. Causa 1d12 de dano (Força). Ao acertar, você rola d20+Força contra d20+Nível do alvo; vencendo, causa +2d6 de dano do vazio.", "CaC", 12, 1, 1);
            case "Machado de Guerra":
                return new Arma("Machado de Guerra", "Um machado de batalha que causa 1d12 de dano, usando Força.", "CaC", 12, 1, 1);
            case "Martelo de Guerra":
                return new Arma("Martelo de Guerra", "Um martelo de guerra que causa 1d12 de dano, usando Força.", "CaC", 12, 1, 1);
            case "Bisturi":
                return new Arma("Bisturi", "Um bisturi afiado e rápido, perfeito para cortes precisos. Causa 1d4 de dano.", "CaC", 4, 1, 1, "Ágil", true);
            case "Arco Refinado":
                return new Arma("Arco Refinado", "Um arco refinado que dispara flechas, causando 1d8 de dano à distância. Consome flechas.", "LA", 8, 1, 1);
            case "Nunchako":
                return new Arma("Nunchako", "Um nunchako rápido que causa 1d6 de dano, usando Destreza.", "CaC", 6, 1, 1, "Destreza");
            case "Foice":
                return new Arma("Foice", "Uma foice de lâmina curva que causa 1d8 de dano à distância. Consome flechas.", "LA", 8, 1, 1);
            case "Cajado":
                return new Arma("Cajado", "Um cajado de madeira simples que causa 1d4 de dano. Pode ser usado para canalizar magia ou para bater.", "CaC/mágico", 4, 1, 1);
            case "Armadura Leve":
                return new Armadura("Armadura Leve", "Oferece proteção básica para combate. Concede +3 de Defesa.", 3, 1);
            case "Armadura Pesada":
                return new Armadura("Armadura Pesada", "Uma armadura imponente que concede +5 de Defesa.", 5, 1);
            case "Poção de Mana":
                return new Consumivel("Poção de Mana", "Restaura 5 pontos de mana. É consumida após o uso.", 1);
            case "Poção Grande de Mana":
                return new Consumivel("Poção Grande de Mana", "Restaura 7 pontos de mana. É consumida após o uso.", 1);
            case "Kit Médico":
                return new Consumivel("Kit Médico", "Pode ser usado para curar 1d4 de vida e acaba com uma infecção. Possui 5 usos.", 1);
            case "Fruta do Diabo":
                return new Consumivel("Fruta do Diabo", "Uma fruta proibida, dada pela velhinha em agradecimento. Só deve ser usada em momento de extrema urgência — e o que acontece quando ela é mordida, ninguém sabe.", 1);
            case "Chapéu Mágico":
                return new ItemRpg("Chapéu Mágico", "Um chapéu encantado que aumenta o dano das suas magias em +3.", 1);
            case "Pequeno Grimório":
                return new ItemRpg("Pequeno Grimório", "Faz as suas magias custarem 1 de mana a menos.", 1);
            case "Manto do Astrólogo":
                return new ItemRpg("Manto do Astrólogo", "Manto de lã azul-escura bordada de constelações. Concede +1 em todos os testes de Intelecto.", 1);
            case "Gema de Mana":
                return new ItemRpg("Gema de Mana", "Uma gema azul translúcida que pulsa quando você usa magia. Aumenta sua mana máxima em +10.", 1);
            case "Amuleto do Coração":
                return new ItemRpg("Amuleto do Coração", "Um amuleto de bronze com um coração esmaltado de vermelho. Aumenta sua vida máxima em +10.", 1);
            case "Ampulheta de Prata":
                return new ItemRpg("Ampulheta de Prata", "Ampulheta encantada que cede sempre a areia um instante antes. Concede +1 de Iniciativa.", 1);
            case "Luvas de Prata":
                return new ItemRpg("Luvas de Prata", "Luvas de malha de prata que tiram a firmeza do golpe. Reduzem em 1 a Defesa do alvo dos seus ataques com arma (não acumula).", 1);
            case "Pó de Midas":
                return new ItemRpg("Pó de Midas", "Pó dourado que atrai tesouros. Aumenta em 10% a chance de encontrar itens e ouro nas criaturas.", 1);
            case "Gota de Veneno":
                return new Consumivel("Gota de Veneno", "Uma gota de veneno negro. Gasta uma rodada inteira para untar sua arma: o próximo ataque que acertar envenena o alvo, causando 1d4 de dano por rodada até o fim do combate.", 1);
            case "Capa do Viajante":
                return new ItemRpg("Capa do Viajante", "Uma capa grossa de lã para as noites na estrada. Recupera +4 de vida ao dormir.", 1);
            case "Túnica de Aventureiro":
                return new ItemRpg("Túnica de Aventureiro", "Túnica reforçada nos ombros e cotovelos. Aumenta em +1 o dano corpo a corpo.", 1);
            case "Manto do Atirador":
                return new ItemRpg("Manto do Atirador", "Manto leve com capuz que protege a mira. Aumenta em +1 o dano à distância.", 1);
            case "Lenço de Seda":
                return new ItemRpg("Lenço de Seda", "Um lenço macio enrolado no pescoço. Concede +1 de Defesa.", 1);
            case "Botas de Correio":
                return new ItemRpg("Botas de Correio", "Botas de sola fina e leve. Concedem +1 em testes de Destreza.", 1);
            case "Maçã":
                return new Consumivel("Maçã", "Uma maçã fresca e crocante. Cura 1d3 de vida e conta como comida.", 1);
            case "Pera":
                return new Consumivel("Pera", "Uma pera doce e suculenta. Cura 1d2 de vida e restaura 1d3 de mana.", 1);
            case "Ameixa":
                return new Consumivel("Ameixa", "Uma ameixa azedinha. Cura 1d2 de vida e cura o enjoo.", 1);
            case "Uva":
                return new Consumivel("Uva", "Cachos de uva doces. Restauram 1d4 de mana.", 1);
            case "Morango Selvagem":
                return new Consumivel("Morango Selvagem", "Um morango raro e vermelho-vivo. Cura 1d3 de vida.", 1);
            case "Figo Seco":
                return new Consumivel("Figo Seco", "Figos secos que sustentam a viagem. Saciam totalmente a fome (não curam vida).", 1);
            case "Madeira":
                return new ItemRpg("Madeira", "Troncos e galhos fortes para construção.", 1);
            case "Folha":
                return new ItemRpg("Folha", "Folhas secas e verdes, úteis como cobertura.", 1);
            case "Pedra":
                return new ItemRpg("Pedra", "Pedras arredondadas de rio, boas para construir.", 1);
            case "Frutas":
                return new Consumivel("Frutas", "Frutas silvestres comestíveis. Cada uma cura 1d2 de vida.", 1);
            default:
                return null;
        }
    }
}

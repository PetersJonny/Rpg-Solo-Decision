package fichas;

import classes.*;
import java.util.Random;
import java.util.Scanner;

public class FichaRpg {

    // cores
    private static final String CIANO = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    // variáveis base do personagem
    private String nomePersonagem, nomePessoa;
    private int vidaPersonagem, manaPersonagem;

    // atributos base (como vieram da distribuição, antes da classe modificar)
    private int constituicaoBase, destrezaBase, forcaBase, sabedoriaBase, intelectoBase, presencaBase;
    
    // atributos finais (depois da classe aplicar os bônus)
    private int constituicao, destreza, forca, sabedoria, intelecto, presenca;

    private int escolhaDeGastos, gastoDePontos, totalDePontos = 6;

    // A mágica da POO: O personagem tem UM objeto do tipo ClasseRpg
    private ClasseRpg classeDoPersonagem = null;
    
    private int defesa, bonusDeDefesa;

    // iniciativa player
    int iniciativaPlayer;

    Random random = new Random();
    Scanner scanner = new Scanner(System.in);

    public FichaRpg(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public void DistribuirAtributos(int opcao, int pontos) {
        switch (opcao) {
            case 1 -> constituicaoBase += pontos;
            case 2 -> destrezaBase += pontos;
            case 3 -> forcaBase += pontos;
            case 4 -> sabedoriaBase += pontos;
            case 5 -> intelectoBase += pontos;
            case 6 -> presencaBase += pontos;
        }
    }

    public void EscolhaInterface(int escolhaInterface) {
        switch (escolhaInterface) {
            case 1:
                System.out.println(CIANO + "==========================================================================================" + RESET);
                System.out.println("\nQual o nome do seu personagem?");
                nomePersonagem = scanner.nextLine();
                break;
            case 2:
                ResetarPontos();
                totalDePontos = 6;

                while (totalDePontos > 0) {
                    System.out.println(CIANO + "==========================================================================================" + RESET);
                    System.out.println("\nVocê tem " + totalDePontos + " pontos para distribuir entre constituição, destreza, força, sabedoria, intelecto e presença. Caso escolha gastar mais pontos do que tem gasta tudo e não mais do que tem. Escolha qual atributo quer botar pontos: \n\n1.Constituição\n2.Destreza\n3.Força\n4.Sabedoria\n5.Intelecto\n6.Presença"); 
                    escolhaDeGastos = scanner.nextInt();
                    scanner.nextLine();

                    System.out.println("\nQuantos pontos deseja gastar? Tem " + totalDePontos + " pontos ainda.");
                    gastoDePontos = scanner.nextInt();
                    scanner.nextLine();

                    if (gastoDePontos <= 0) {
                        System.out.println("Por favor, insira um valor maior que zero!");
                        continue;
                    }

                    if(gastoDePontos > totalDePontos) {
                        gastoDePontos = totalDePontos;
                    }

                    if (escolhaDeGastos >= 1 && escolhaDeGastos <= 6) {
                        DistribuirAtributos(escolhaDeGastos, gastoDePontos);
                        totalDePontos -= gastoDePontos;
                    } else {
                        System.out.println("Opção inválida!");
                    }
                }
                
                AplicandoBonus();
                break;
            case 3:
                System.out.println(CIANO + "==========================================================================================" + RESET);
                System.out.println("\n Escolha entre uma das 3 classes abaixo: \n 1.Mago (só pode usar cajado, conjura magias poderosas, porém é mais fragil). \n 2.Guerreiro (só pode usar espada e atacar corpo a corpo, porém é mais resistente). \n 3.Healer (tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana).");
 			    int escolhaClasse = scanner.nextInt();
 		        scanner.nextLine();

                EscolhendoClasse(escolhaClasse);
                AplicandoBonus();

                break;
            case 4:
                System.out.println(CIANO + "==========================================================================================" + RESET);
                MostrarFicha();
                break;
            default:
                if (escolhaInterface == 5) {
                    break;
                } else {
                    System.out.println("Opção inválida!");
                }
        }
    }

    public void EscolhendoClasse(int escolhaClasse) {
        switch (escolhaClasse) {
            case 1:
                classeDoPersonagem = new Mago();
                break;
            case 2:
                classeDoPersonagem = new Guerreiro();
                break;
            case 3:
                classeDoPersonagem = new Healer();
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    public void AplicandoBonus() {
        // Inicialmente, os atributos finais são iguais aos atributos distribuídos (base)
        this.constituicao = constituicaoBase;
        this.destreza = destrezaBase;
        this.forca = forcaBase;
        this.sabedoria = sabedoriaBase;
        this.intelecto = intelectoBase;
        this.presenca = presencaBase;
        
        // Se ainda não escolheu classe, não calcula vida e mana
        if (classeDoPersonagem == null) {
            this.vidaPersonagem = 0;
            this.manaPersonagem = 0;
            this.defesa = 10 + this.destreza + bonusDeDefesa;
            return;
        }

        // Calcula a Vida e Mana USANDO os atributos base ORIGINAIS (como era no seu jogo)
        this.vidaPersonagem = classeDoPersonagem.calcularVidaBase(this.constituicaoBase);
        this.manaPersonagem = classeDoPersonagem.calcularManaBase(this.presencaBase);
        
        // Agora aplicamos os bônus/penalidades da classe
        this.constituicao += classeDoPersonagem.getBonusConstituicao();
        this.forca += classeDoPersonagem.getBonusForca();
        this.destreza += classeDoPersonagem.getBonusDestreza();
        this.sabedoria += classeDoPersonagem.getBonusSabedoria();
        this.intelecto += classeDoPersonagem.getBonusIntelecto();
        this.presenca += classeDoPersonagem.getBonusPresenca();
        
        this.defesa = 10 + this.destreza + bonusDeDefesa;
    }

    public void ResetarPontos() {
        this.constituicaoBase = 0;
        this.presencaBase = 0;
        this.destrezaBase = 0;
        this.sabedoriaBase = 0;
        this.intelectoBase = 0;
        this.forcaBase = 0;
        AplicandoBonus();
    }

    public void MostrarFicha() {
        String nomeDaClasse = (classeDoPersonagem != null) ? classeDoPersonagem.getNome() : "Nenhuma";
        String armaDaClasse = (classeDoPersonagem != null) ? classeDoPersonagem.getArma() : "Nenhuma";
        String tipoArma = (classeDoPersonagem != null) ? classeDoPersonagem.getTipoArma() : "-";
        int qtdDano = (classeDoPersonagem != null) ? classeDoPersonagem.getQuantidadeDanoArma() : 0;
        int dadoDano = (classeDoPersonagem != null) ? classeDoPersonagem.getDadoDanoArma() : 0;

        System.out.println("\n --------FICHA-------- \n\nNome: " + nomePersonagem + "\t\tDono da ficha: " + nomePessoa + "\t\tClasse: " + nomeDaClasse + "\nVida: " + vidaPersonagem + "\t\tMana: " + manaPersonagem + "\n\nAtributos: \nConstituição: " + constituicao + "\nDestreza: " + destreza + "\nForça: " + forca + "\nSabedoria: " + sabedoria + "\nIntelecto: " + intelecto + "\nPresença: " + presenca + "\n\nCombate: \nArma: " + armaDaClasse + "\tDano da arma: " + qtdDano + "d" + dadoDano + "\t tipo da arma: " + tipoArma + "\nDefesa: " + defesa + "\n----------------------");
    }

    public void IniciativaPlayer() {
        this.iniciativaPlayer = (random.nextInt(20) + 1) + this.destreza;
        System.out.println("O " + nomePersonagem + " rolou " + this.iniciativaPlayer+ " de iniciativa!");
    }
}

import java.util.Random;
import java.util.Scanner;

public class FichaRpg {

    // cores
    private static final String CIANO = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    // variáveis base do personagem
    private String nomePersonagem, nomePessoa;
    private int vidaPersonagem, manaPersonagem;

    // atributos base
    private int constituicao, destreza, forca, sabedoria, intelecto, presenca;
    private int escolhaDeGastos, gastoDePontos, totalDePontos = 6;

    //classe
    private String arma, tipoArma;
    private String classeEscolhida ="";
    private int dadoDanoArma, quantidadeDanoArma;
    private int defesa, bonusDeDefesa;

    // iniciativa player
    int iniciativaPlayer;

    // criando objetos
    Random random = new Random();
    Scanner scanner = new Scanner(System.in);

    // definir nome do personagem e dono da ficha
    public FichaRpg(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }



    // distribuição de atributos base
    public void DistribuirAtributos(int opcao, int pontos) {
        switch (opcao) {
            case 1 -> constituicao += pontos;
            case 2 -> destreza += pontos;
            case 3 -> forca += pontos;
            case 4 -> sabedoria += pontos;
            case 5 -> intelecto += pontos;
            case 6 -> presenca += pontos;
        }
    }

    // escolha de ação
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
                
                if (classeEscolhida != "") {
                    AplicandoBonus(classeEscolhida);
                }
                break;
            case 3:
                if (classeEscolhida != "") {
                      switch (classeEscolhida) {
                        case "Mago": // Mago
                            this.constituicao += 2;
                            this.presenca -= 2;
                            this.intelecto -= 1;                    
                            break;
                        case "Guerreiro": // Guerreiro
                            this.constituicao -= 2;
                            this.forca -= 1;
                            this.intelecto += 2;
                            break;
                        case "Healer": // Healer
                            this.sabedoria -= 1;
                            this.intelecto -= 2;
                            this.forca += 2;
                            break;
                    }  
                }

                System.out.println(CIANO + "==========================================================================================" + RESET);
                System.out.println("\n Escolha entre uma das 3 classes abaixo: \n 1.Mago (só pode usar cajado, conjura magias poderosas, porém é mais fragil). \n 2.Guerreiro (só pode usar espada e atacar corpo a corpo, porém é mais resistente). \n 3.Healer (tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana).");
 			    int escolhaClasse = scanner.nextInt();
 		        scanner.nextLine();

                EscolhendoClasse(escolhaClasse);
                AplicandoBonus(classeEscolhida);

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

    // escolhendo classe
    public void EscolhendoClasse(int escolhaClasse) {
        switch (escolhaClasse) {
            case 1:
                classeEscolhida = "Mago";
                break;
            case 2:
                classeEscolhida = "Guerreiro";
                break;
            case 3:
                classeEscolhida = "Healer";
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    // definir coisas da classe
    public void AplicandoBonus(String classeEscolhida) {
        switch (classeEscolhida) {
            case "Mago": // Mago
                this.vidaPersonagem = 10 + constituicao;
                this.manaPersonagem = 8 + presenca;
                this.constituicao -= 2;
                this.presenca += 2;
                this.intelecto += 1;
                this.arma = "cajado";
                this.dadoDanoArma = 4;
                this.quantidadeDanoArma = 1;
                this.tipoArma = "CaC/mágico";
                this.defesa = 10 + destreza + bonusDeDefesa;
                break;
            case "Guerreiro": // Guerreiro
                this.vidaPersonagem = 20 + constituicao;
                this.manaPersonagem = 2 + presenca;
                this.constituicao += 2;
                this.forca += 1;
                this.intelecto -= 2;
                this.arma = "espada";
                this.dadoDanoArma = 8;
                this.quantidadeDanoArma = 1;
                this.tipoArma = "CaC";
                break;
            case "Healer": // Healer
                this.vidaPersonagem = 14 + constituicao;
                this.manaPersonagem = 5 + presenca;
                this.sabedoria += 1;
                this.intelecto += 2;
                this.forca -= 2;
                this.arma = "arco";
                this.tipoArma = "LA";
                this.dadoDanoArma = 6;
                this.quantidadeDanoArma = 1;
                break;
        }
    }

    // reset pontos 
    public void ResetarPontos() {
        this.constituicao = 0;
        this.presenca = 0;
        this.destreza = 0;
        this.sabedoria = 0;
        this.intelecto = 0;
        this.forca = 0;
    }

    // mostrar ficha
    public void MostrarFicha() {
        System.out.println("\n --------FICHA-------- \n\nNome: " + nomePersonagem + "\t\tDono da ficha: " + nomePessoa + "\t\tClasse: " + classeEscolhida + "\nVida: " + vidaPersonagem + "\t\tMana: " + manaPersonagem + "\n\nAtributos: \nConstituição: " + constituicao + "\nDestreza: " + destreza + "\nForça: " + forca + "\nSabedoria: " + sabedoria + "\nIntelecto: " + intelecto + "\nPresença: " + presenca + "\n\nCombate: \nArma: " + arma + "\tDano da arma: " + quantidadeDanoArma + "d" + dadoDanoArma + "\t tipo da arma: " + tipoArma + "\n----------------------");
    }

    // iniciativa player
    public void IniciativaPlayer() {
        this.iniciativaPlayer = (random.nextInt(20) + 1) + this.destreza;
        System.out.println("O " + nomePersonagem + " rolou " + this.iniciativaPlayer+ " de iniciativa!");
    }
}

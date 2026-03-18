// import java.util.Scanner;

// public class Main {
//     public static void main(String[] args) {
//         Scanner scn = new Scanner(System.in);

//         // pontos de atributo
//         int totalDePontos = 6;
//         int escolhaDeGastos;
//         int gastoDePontos;

//         // classe
//         int classe = 0;

//         // interface de escolhas de nome
// 		System.out.println("Escolha o nome do seu personagem:");
// 		String nomePersonagem = scn.nextLine();
		
// 		System.out.println("\nEscolha o nome de quem é a ficha:");
// 		String nomePessoa = scn.nextLine();

//         // criação do objeto do nome dos personagens
//         FichaRpg FichaRpg = new FichaRpg(nomePersonagem, nomePessoa);

//         // distribuição dos pontos
//         while(totalDePontos > 0) {
//             System.out.println("\nVocê tem 6 pontos para distribuir entre constituição, destreza, força, sabedoria, intelecto e presença. Caso escolha gastar mais pontos do que tem gasta tudo e não mais do que tem. Escolha qual atributo quer botar pontos: \n\n1.Constituição\n2.Destreza\n3.Força\n4.sabedoria\n5.Intelecto\n6.Presença"); 
// 			escolhaDeGastos = scn.nextInt();
// 			scn.nextLine();

//             System.out.println("\nQuantos pontos deseja gastar? Tem " + totalDePontos + " pontos ainda.");
// 			gastoDePontos = scn.nextInt();
// 			scn.nextLine();

//             if(gastoDePontos > totalDePontos) {
//                 gastoDePontos = totalDePontos;
//             }

//             if (escolhaDeGastos >= 1 && escolhaDeGastos <= 6) {
//                 FichaRpg.DistribuirAtributos(escolhaDeGastos, gastoDePontos);
//                 totalDePontos -= gastoDePontos;
//             } else {
//                 System.out.println("Opção inválida!");
//             }
//         }

//         // escolha da classe
//         while (classe < 1 || classe > 3) {
//             System.out.println("\n Escolha entre uma das 3 classes abaixo: \n 1.Mago (só pode usar cajado, conjura magias poderosas, porém é mais fragil). \n 2.Guerreiro (só pode usar espada e atacar corpo a corpo, porém é mais resistente). \n 3.Healer (tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana).");
// 			classe = scn.nextInt();
// 			scn.nextLine();

//             if (classe >= 1 && classe <= 3) {
//                 FichaRpg.EscolhaDaClasse(classe);
//             } else {
//                 System.out.println("Escolha entre 1, 2 ou 3.");
//             }

//         }

//         // print da ficha
//             FichaRpg.mostrarFicha();
//             scn.close();
    
//     }
// }

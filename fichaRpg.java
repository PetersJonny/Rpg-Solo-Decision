import java.util.Scanner;

public class fichaRpg {
	public static void main (String[] args) {
		Scanner scn = new Scanner (System.in);
		
		// declaração das classes do jogo
		String mago = "mago";
		String guerreiro = "guerreiro";
		String healer = "healer";

		// vida e mana do personagem
		int manaPersonagem = 0;
		int vidaPersonagem = 0;

		// variáveis sobre a classe
		String classeEscolhida = "";
		int classe = 0;

		// atributos
		int constituicao = 0;
		int destreza = 0;
		int forca = 0;
		int sabedoria = 0;
		int intelecto = 0;
		int presenca = 0;	

		// interface de escolhas
		System.out.println("Escolha o nome do seu personagem:");
		String nomePersonagem = scn.nextLine();
		
		System.out.println("\nEscolha o nome de quem é a ficha:");
		String nomePessoa = scn.nextLine();

		while(classe < 1 || classe > 3) {
			System.out.println("\n Escolha entre uma das 3 classes abaixo: \n 1.Mago (só pode usar cajado, conjura magias poderosas, porém é mais fragil). \n 2.Guerreiro (só pode usar espada e atacar corpo a corpo, porém é mais resistente). \n 3.Healer (tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana).");
			classe = scn.nextInt();

			// definição de valores para escolha
			switch(classe) {
				case 1:
					classeEscolhida = mago;
					vidaPersonagem = 10 + constituicao;
					manaPersonagem = 8 + presenca;
					constituicao = constituicao - 2;
					presenca = presenca + 2;
					intelecto = intelecto + 1;
					break;
				case 2:
					classeEscolhida = guerreiro;
					vidaPersonagem = 20 + constituicao;
					manaPersonagem = 2 + presenca;
					constituicao = constituicao + 2;
					forca = forca + 1;
					intelecto = intelecto - 2;
					break;
				case 3: 
					classeEscolhida = healer;
					vidaPersonagem = 14 + constituicao;
					manaPersonagem = 5 + presenca;
					sabedoria = sabedoria + 1;
					intelecto = intelecto + 2;
					forca = forca - 2;
					break;
				default:
					System.out.println("Escolha entre 1, 2 ou 3.");
					
			}
		
		}

		// print da ficha
		System.out.println("\n --------FICHA-------- \n\nNome: " + nomePersonagem + "\t\tDono da ficha: " + nomePessoa + "\t\tClasse: " + classeEscolhida + "\nVida: " + vidaPersonagem + "\t\tMana: " + manaPersonagem + "\n----------------------");
	}
}

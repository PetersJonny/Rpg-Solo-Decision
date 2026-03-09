import java.util.Scanner;

public class fichaRpg {
	public static void main (String[] args) {
		Scanner scn = new Scanner (System.in);
	
		String mago = "mago";
		int vidaMago = 10;
		int manaMago = 8;

		String guerreiro = "guerreiro";
		int vidaGuerreiro = 20;
		int manaGuerreiro = 2;

		String healer = "healer";
		int vidaHealer = 14;
		int manaHealer = 5;

		int manaPersonagem = 0;
		int vidaPersonagem = 0;

		String classeEscolhida = "";
		
		int classe = 0;

		System.out.println("Escolha o nome do seu personagem:");
		String nomePersonagem = scn.nextLine();
		
		System.out.println("\nEscolha o nome de quem é a ficha:");
		String nomePessoa = scn.nextLine();

		while(classe < 1 || classe > 3) {
			System.out.println("\n Escolha entre uma das 3 classes abaixo: \n 1.Mago (só pode usar cajado, conjura magias poderosas, porém é mais fragil). \n 2.Guerreiro (só pode usar espada e atacar corpo a corpo, porém é mais resistente). \n 3.Healer (tem poderes de cura, pode curar a si mesmo e aos outros, tem uma vida mediana).");
			classe = scn.nextInt();

			switch(classe) {
				case 1:
					classeEscolhida = mago;
					vidaPersonagem = vidaMago;
					manaPersonagem = manaMago;
					break;
				case 2:
					classeEscolhida = guerreiro;
					vidaPersonagem = vidaGuerreiro;
					manaPersonagem = manaGuerreiro;
					break;
				case 3: 
					classeEscolhida = healer;
					vidaPersonagem = vidaHealer;
					manaPersonagem = manaHealer;
					break;
				default:
					System.out.println("Escolha entre 1, 2 ou 3.");
					
			}
		
		}

		System.out.println("\n --------FICHA-------- \n\nNome: " + nomePersonagem + "\t\tDono da ficha: " + nomePessoa + "\t\tClasse: " + classeEscolhida + "\nVida: " + vidaPersonagem + "\t\tMana: " + manaPersonagem + "\n----------------------");
	}
}

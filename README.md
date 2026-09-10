# Solo RPG Decision

Um RPG de mesa single-player em **Java** executado no terminal. Você cria sua ficha de personagem, atravessa o prólogo e adentra a floresta gélida de **Freijord**, onde precisa enfrentar criaturas, saquear espólios e sobreviver a cada encontro.

## Como Rodar

1. Tenha o [JDK](https://www.oracle.com/java/technologies/downloads/) instalado (Java 17+).
2. Compile o projeto:
   ```bash
   javac -d bin $(find . -name "*.java")
   ```
3. Execute:
   ```bash
   java -cp bin Main
   ```

## Fluxo do Jogo

1. **Criação de Ficha** — defina nome do personagem, distribua os atributos e escolha a classe. A ficha só é liberada quando completa (nome + 6 pontos de atributo + classe).
2. **Acampamento (Menu Principal)** — consulte sua ficha, inspecione habilidades/inventário ou parta para o mapa.
3. **Prólogo** — uma introdução narrada sobre Freijord, com opção de pular.
4. **Mapa de Freijord** — explore a floresta, veja sua ficha ou volte ao acampamento.
5. **Floresta** — encontros aleatórios, combates por iniciativa, fuga, saque e ouro.

## Criação de Personagem

Você distribui **6 pontos** entre seis atributos: **Constituição, Destreza, Força, Sabedoria, Intelecto e Presença**. Em seguida escolhe uma das três classes, que aplicam modificadores, itens iniciais, habilidades e definem os valores base de Vida e Mana.

### Mago

- Vida base: 10 + Constituição | Mana base: 8 + Presença
- Modificadores: Constituição -2, Presença +2, Intelecto +1
- Itens: Cajado (1d4, CaC/mágico), Poção de Mana (restaura 5)
- Habilidade: **Bola Elementar** (2d8 de dano do elemento escolhido, custa 3 mana)

### Guerreiro

- Vida base: 20 + Constituição | Mana base: 2 + Presença
- Modificadores: Constituição +2, Força +1, Intelecto -2
- Itens: Espada (1d8), Armadura Leve (+3 de Defesa)
- Habilidade: **Casca Grossa** (reduz 5 de dano recebido de um ataque)

### Healer

- Vida base: 14 + Constituição | Mana base: 5 + Presença
- Modificadores: Sabedoria +1, Intelecto +2, Força -2
- Itens: Arco (1d6 à distância, consome Flechas), Kit Médico (cura 1d4, 5 usos), 15 Flechas
- Habilidade: **Conhecimento Avançado** (rerrolla um teste falho, custa 2 mana)

## Ficha

Exibe nome, dono, nível, classe, Vida/Mana, **Ouro**, atributos, **Combate** (todas as armas do inventário + o ataque desarmado, com dados, tipo e atributo), **Defesa** (10 + Destreza + bônus de armaduras) e inventário/habilidades.

A Defesa é calculada automaticamente: `10 + Destreza + bônus de armaduras na mochila`.

## Floresta de Freijord

Ao explorar, um **Teste de Presença** decide se você vê a ameaça primeiro. Se passar, você pode **Lutar (ganhando +2 de Iniciativa)** ou **fugir furtivamente** (Destreza contra dificuldade 12). Se falhar, a criatura te surpreende e o combate começa sem bônus.

Há 20% de chance de encontrar uma **Fada**, um encontro especial com três opções:

- **Conversar**: Teste de Sabedoria (dificuldade 14) — sucesso concede **+1 em um atributo aleatório**.
- **Lutar**: combate contra a Fada (acerto automático).
- **Deixá-la em paz**: seguir caminho.

Sorteios de encontro (além da Fada):

| Criatura | Quantidade | Vida | Defesa | Iniciativa | Acerto | Presença | Ataques | Recompensa |
|---|---|---|---|---|---|---|---|---|
| Lobo Selvagem | 1-3 | 14 | 10 | +3 | +3 | 8 | Mordida 1d6 / Aranhão 2d4 | Couro 1-2 (40%) |
| Urso | 1 | 35 | 7 | +0 | +1 | 5 | Mordida 1d10 / Aranhão 2d8 | Couro 2-4 (60%), Dente de Urso (20%) |
| Bandido | 1-5 | 9 | 12 | +1 | +2 | 15 | Facada 1d4 / Soco 1d3 | Ouro 9-27 (100%), Faca (35%) |
| Fada | 1 | 4 | 14 | +0 | automático | 18 | Brilho Cintilante 1d6 (luz) | Brilho Mágico (100%) |

## Combate

- **Iniciativa:** cada combatente rola `d20 + Destreza` (jogador) ou `d20 + Iniciativa` (criatura). Todos agem em **ordem decrescente de iniciativa** a cada rodada — se você tirar menos que os inimigos, eles atacam antes de você.
- **Ações por turno:** Lutar (arma, soco ou habilidade), Abrir Mochila (usar consumíveis ou tentar fugir), Tentar Fugir ou Ver Ficha.
- **Ataque:** `d20 + atributo` (Força para armas corpo a corpo e soco; Destreza para armas à distância e a Faca) contra a **Defesa** do alvo.
- **Crítico:** rolar 20 natural **dobra os dados de dano** (tanto para você quanto para os inimigos).
- **Dano:** soma dos dados + atributo usado pela arma.
- **Magia:** Bola Elementar rola e exibe cada dado (ex.: `5 + 8 = 13`), custa mana.
- **Fugas:** 3 tentativas; você rola `d20 + Destreza` contra **10 + iniciativa** da ameaça mais rápida viva. Se alcançado, sofre um ataque imediato. Também é possível tentar fugir direto pela mochila.

Encontros em grupo permitem **escolher o alvo** dos seus ataques/habilidades.

### Fim de Combate

- **Vitória:** a criatura processa os drops — ouro e itens (que acumulam no inventário).
- **Derrota:** sua jornada termina e o jogo reinicia na criação de uma nova ficha.

## Itens e Dinheiro

- **Consumíveis:** Kit Médico (cura 1d4), Poção de Mana (+5 mana) — usáveis em combate gastando o turno.
- **Munição:** Flechas são consumidas a cada disparo do arco.
- **Armas:** Espada, Cajado, Arco e a **Faca** (drop dos bandidos, usa Destreza). Todas as armas do inventário aparecem na seção Combate da ficha.
- **Materiais:** Couro, Dente de Urso e Brilho Mágico são itens de valor/artesanato.
- **Ouro:** obtido como recompensa (especialmente dos bandidos).

## Estrutura do Projeto

```
├── Main.java               # Loop principal, criação de ficha e mapa
├── classes/                # ClasseRpg, Mago, Guerreiro, Healer
├── fichas/FichaRpg.java    # Atributos, inventário, ouro, defesa, habilidades
├── criaturas/Criatura.java # Monstros, ataques, acerto/crítico, drops
├── eventos/Floresta.java   # Encontros e sistema de combate completo
├── mecanicas/MecanicasRpg.java # Rolagens de dado e dano
├── narrativa/Aventura.java # Prólogo
├── telas/Interface.java    # Menus, ficha e entradas do jogador
├── itens/                  # ItemRpg, Arma, Armadura, Consumivel
└── habilidades/            # Habilidade e Magia
```

## Conceitos Aplicados

- Lógica de RPG: atributos, modificadores de classe, testes (Presença, Destreza, Sabedoria), defesa por Destreza e armaduras.
- Java OO com pacotes, herança entre classes de personagem e composição de itens/habilidades/criaturas.
- Entrada via terminal (Scanner) e ritmo de leitura com pausas (Thread.sleep).
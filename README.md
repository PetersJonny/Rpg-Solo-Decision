# Solo RPG Decision

Um **RPG de mesa single-player** jogado no terminal, em **Java** (requer Java 21+). Você cria sua ficha, enfrenta criaturas, evolui com XP, constrói abrigos e luta para sobreviver na floresta gélida de **Freijord**.

Este README é o guia completo do jogo: se ficar perdido em qualquer momento, volte aqui.

> **Aviso:** este guia explica **todas as mecânicas** do jogo (combate, progressão, construção, vendedor, salvamento) — mecânica **não é spoiler**. O que é spoiler é o **mundo em si**: as criaturas/monstros, os encontros secretos e os mistérios da floresta. Essas partes estão marcadas com aviso de spoiler e podem ser recolhidas.

---

## Como Rodar

### Requisitos
- **Java 21+** (JDK 21)
- **Apache Maven** (ou uma IDE compatível: IntelliJ IDEA, Eclipse, VS Code)

### Comandos

```bash
# Testar a arquitetura (suíte de testes automatizados)
mvn clean test

# Compilar apenas os binários
mvn clean compile

# Executar o jogo
mvn exec:java
```

---

## Fluxo do Jogo

1. **Menu principal** — Novo Jogo, Carregar Jogo, Apagar Save ou Fechar o jogo.
2. **Criação de Ficha** — nome, distribuição dos atributos, classe e dificuldade. A ficha só é liberada quando completa (nome + 6 pontos de atributo + classe).
3. **Prólogo** — introdução narrada sobre Freijord (opção de pular).
4. **Floresta de Freijord** — o "hub" do jogo. Daqui você explora, coleta recursos, constrói, dorme, conversa com o companheiro e salva o jogo.
5. **Exploração** — cada exploração gera um encontro (com comerciantes, criaturas e, raramente, algo mais). O dia e a noite alternam a cada 3 unidades de período.
6. **Combate** — resolução por iniciativa, com ataques, magias, habilidades, fuga e saque.

Não existe autosave: **salve manualmente** (`Salvar Jogo` no menu principal da floresta).

---

## Criação de Personagem

Você distribui **6 pontos** entre os seis atributos. Depois escolhe a classe (Mago com um elemento, Guerreiro ou Healer) e a dificuldade.

### Atributos

| Atributo | Papel principal |
|---|---|
| **Constituição** | Vida; pontos extras aqui dão vida retroativa por nível já ganho |
| **Destreza** | Iniciativa, Defesa, testes de fuga, armas à distância e armas "Ágeis" |
| **Força** | Dano e acerto de armas corpo a corpo e do soco |
| **Sabedoria** | Testes sociais (ex.: conversas e negociações) |
| **Intelecto** | Testes de conhecimento, salvamento de companheiro |
| **Presença** | Detecta ameaças antes de serem emboscadas; define a Mana |

### Fadiga (cansado)

Depois de **2 noites sem dormir**, o personagem fica **cansado**: **−1 em todos os testes de atributo** (iniciativa, presença, fuga, intelecto, etc.). Não afeta vida, mana ou dano. Dormir na cabana remove o cansaço.

### Dificuldade

- **Normal** — ao morrer, os saves do personagem são mantidos.
- **Difícil** — morte permanente: ao morrer, **todos os saves com o nome do personagem são apagados**. Não há segunda chance.

---

## Classes

### Mago

- **Vida base:** 10 + Constituição | **Mana base:** 8 + Presença
- **Modificadores:** Força −2, Presença +2, Intelecto +1
- **Itens:** Cajado (1d4, CaC/mágico), Poção de Mana (+5 mana)
- **Magias iniciais:** **Bola Elementar (3d10, 2 de mana)** e **Pequena Magia (2d8, grátis)**
- **A cada nível:** vida +2 + Constituição | mana +3 + Presença
- **Nível 3 (automático):** a Bola Elementar vira **ataque em área** (atinge também os inimigos adjacentes)

| Nível | Escolha | Custo | Efeito |
|---|---|---|---|
| 5 | **Magia Desperta** | 6 | 6d12 de dano em um alvo |
| 5 | **Proteção Absoluta** | 5 | +3 de defesa e reflete 2d8 do seu elemento em quem te acertar (até o fim do combate) |
| 7 | **Prisão** | 5 | Prende um inimigo; na vez dele, ele precisa tirar 15+ em um d20 para se libertar |
| 7 | **Magia Proibida** | 5 | Uma vez por combate, sem gastar ação: todos os testes dos inimigos desta rodada falham |
| 9 | **Poder Absoluto** | 15 | Dobra a quantidade de dados de dano das suas magias até o fim do combate |
| 10 | **Explosão de Poder** | variável (mín. 2) | Cada 2 de mana gasta causa 2d12 em **todos** os inimigos |

> Para usar magias, o Mago precisa de um **Cajado** no inventário.

### Guerreiro

- **Vida base:** 20 + Constituição | **Mana base:** 2 + Presença
- **Modificadores:** Constituição +2, Força +1, Intelecto −2
- **Itens:** Espada (1d8, CaC), Armadura Leve (+3 Defesa)
- **Habilidade inicial:** **Casca Grossa** (passiva ativável por 1 mana: reduz 5 do dano de um ataque recebido)
- **A cada nível:** vida +5 + Constituição | mana +1 + Presença
- **Nível 3 (automático):** **Peso da Espada** (3d8 + Força, custo 2 — conta como magia)

| Nível | Escolha | Custo | Efeito |
|---|---|---|---|
| 5 | **Giro** | 1 por giro (máx. = Destreza) | Cada giro causa 1d10 + Força em **área** (todos os inimigos) |
| 5 | **Espada Afiada** | 3 | Passiva: +2d8 de dano em todos os seus ataques com arma durante o combate |
| 7 | **Defesa Absoluta** | — | Passiva: +5 de defesa no início do combate, até você atacar |
| 7 | **Estrondo** | 5 | 7d10 + Força em **área**; você fica sem poder usar habilidades por **2 rodadas** |
| 9 | **Semi Deus** | toda a mana | +50% de vida máxima, cura total e **+4 dados de dano corpo a corpo** até o fim do combate |
| 10 | **Deus** | — | Passiva: Semi Deus fica sempre ativo (+50% de vida, +4 dados CaC) e ganha **Cura Incessante** (cura total, uma vez por combate) |

### Healer

- **Vida base:** 14 + Constituição | **Mana base:** 5 + Presença
- **Modificadores:** Sabedoria +1, Intelecto +2, Força −2
- **Itens:** Bisturi (1d4, CaC, **Ágil** — usa o maior entre Força e Destreza), Kit Médico ×5 (cura 1d4 por uso)
- **Habilidade inicial:** **Conhecimento Avançado** (custo 2: rerrola um teste/ataque falho)
- **A cada nível:** vida +3 + Constituição | mana +2 + Presença
- **Nível 3 (automático):** **Cura Reforçada** (ao usar Kit Médico em si mesmo, gaste 1 de mana para curar +2d4)
- **Nível 5 (automático, ambos):** **Cura para a Morte** (custo 3: envenena um alvo, que recebe 3d8 ao final de cada ataque seu contra ele) e **Cura Total** (custo 10: se você cair a 0 de vida, revive com a vida cheia, uma vez por combate)

| Nível | Escolha | Custo | Efeito |
|---|---|---|---|
| 7 | **Conhecimento Avassalador** | 0 (gasta a ação) | Teste de Intelecto (dificuldade 15): revela vida, defesa, iniciativa, ataques e XP de todos os inimigos |
| 7 | **Arma Mental** | — | Passiva: o Bisturi passa a causar **3d8** |
| 9 | **Cura Absoluta** | 10 | Cura toda a vida e cria um escudo igual à sua vida máxima (o escudo é gasto antes da vida real) |
| 10 | **Conhecimento Absoluto** | — | Passiva: **+2 em todos os atributos** |

---

## Progressão e Níveis

O nível máximo é **10**. O XP vem apenas de **matar criaturas**.

### Tabela de XP

| De nível | XP necessário |
|---|---|
| 1 → 2 | 100 |
| 2 → 3 | 300 |
| 3 → 4 | 700 |
| 4 → 5 | 1.500 |
| 5 → 6 | 3.500 |
| 6 → 7 | 8.000 |
| 7 → 8 | 15.000 |
| 8 → 9 | 40.000 |
| 9 → 10 | 100.000 |

O excesso de XP é acumulado (não é desperdiçado ao subir de nível).

### Recompensas por nível

- **Nível 3:** habilidade automática da classe (Guerreiro: Peso da Espada; Healer: Cura Reforçada; Mago: Bola Elementar vira área).
- **Níveis 2, 4, 6 e 8:** **+1 ponto de atributo** (Constituição dá vida retroativa).
- **Níveis 5, 7, 9 e 10:** **escolha 1 habilidade** entre as opções da sua classe (nesta e nas anteriores — o que você deixou para trás pode ser escolhido depois).

---

## Defesa

A Defesa é calculada assim:

```
Defesa = 10 + Destreza   (+ bônus da armadura equipada)
        (+ 3 se Proteção Absoluta ativa)
        (+ 5 se Defesa Absoluta ativa, até você atacar)
```

- A **melhor armadura** do inventário é equipada automaticamente (as armaduras extras na mochila não somam defesa).
- Um ataque acerta se `d20 + bônus` for maior ou igual à Defesa do alvo.

---

## Combate

### Iniciativa

Cada combatente rola `d20 + Destreza` (você e o companheiro) ou `d20 + Iniciativa` (criaturas). Todos agem em **ordem decrescente** a cada rodada. Se você detectou a ameaça primeiro (teste de Presença) e escolheu lutar, ganha **+2 de Iniciativa**.

### Ações por turno

1. **Lutar** — atacar com arma ou soco, usar habilidade, ativar passiva manual (ex.: Casca Grossa, Espada Afiada) ou usar Magia Proibida (ação livre).
2. **Abrir Mochila** — usar um consumível (gasta o turno).
3. **Tentar Fugir** — precisa passar **3 vezes** no teste de fuga.
4. **Ver Ficha** — sem custo de ação.

Depois do **Estrondo**, você fica **2 rodadas sem poder usar habilidades** (itens, ataques e fuga continuam liberados).

### Ataque

```
Teste:          d20 + atributo  vs  Defesa do alvo
Atributo:       Força (armas CaC e soco) | Destreza (à distância e Faca/Nunchako)
Ágeis:          usa o maior entre Força e Destreza (Bisturi, Lança)
Dano:           dados da arma + atributo
```

- **Crítico:** rolar **20 natural** sempre acerta e **dobra os dados de dano** (vale para você e para os inimigos).
- **Semi Deus** dá **+4 dados de dano** em soco e armas corpo a corpo (armas à distância não recebem).
- **Arcos e Foices** consomem **1 Flecha por disparo**.
- **Espada Afiada** adiciona 2d8 em ataques com arma.

### Fuga

Teste: `d20 + Destreza` contra **10 + Iniciativa da criatura mais rápida viva**. Você precisa de **3 sucessos** para fugir. Falhar dá um **ataque grátis da criatura mais rápida** e o turno segue normalmente.

### Magia

- Custo de mana é descontado ao lançar. Cada dado é rolado e exibido.
- **Pequeno Grimório** (item) reduz o custo das magias pagas em 1 (mínimo 1).
- **Chapéu Mágico** (item) adiciona **+3** de dano a todas as magias.
- **Poder Absoluto** dobra a quantidade de dados das magias.
- **Mesa de Magias** (construção) adiciona **+1 dado de dano** a **todas** as habilidades de dano (magias, Estrondo, Giro e Explosão de Poder) por 2 períodos.
- **Peso da Espada** soma + Força ao dano.
- **Magia em área** atinge o alvo e os inimigos adjacentes na lista.
- Magias **sempre acertam** (sem teste de defesa) e aplicam o veneno da Cura para a Morte no alvo.

### Companheiro em combate

O companheiro age sozinho no seu turno de iniciativa:

- **Healer:** cura você (2d4) se estiver abaixo de 60% de vida; senão, cura a si mesmo.
- **Mago:** lança sua magia do elemento (60%) ou a Pequena Magia grátis (30%); senão, ataque físico.
- **Guerreiro/outros:** ataque físico com a arma.
- Os inimigos têm **50% de chance** de atacar o companheiro em vez de você (quando ele está vivo).

### Morte e resgate

- Se **você** cair a 0 de vida, o Healer tenta reviver com **Cura Total** (10 de mana, uma vez por combate).
- Se o **companheiro** morrer, você tenta estabilizá-lo: precisa de **Intelecto ≥ 14** e um **d20 ≥ 16**. Se o resgate falhar, o companheiro morre em definitivo e **todos os itens e o ouro dele passam para você**.
- Companheiro vivo com menos de 30% de vida após a vitória se recupera para 50%.

### Vitória e Derrota

- **Vitória:** cada criatura processa seus drops (ouro e itens) e concede XP.
- **Derrota:** sua jornada termina e o jogo volta ao menu principal (a dificuldade define se os saves sobrevivem).

### Criaturas

<details>
<summary>⚠️ <b>Spoiler: as criaturas da floresta</b> — clique para revelar</summary>

| Criatura | Qtd (dia / noite) | Vida | Defesa | Iniciativa | Acerto | Teste Presença | XP | Ataques | Saque |
|---|---|---|---|---|---|---|---|---|---|
| Lobo Selvagem | 1–2 / 1–4 | 14 | 10 | +3 | +3 | 8 | 25 | Mordida 1d6, Arranhão 2d4 | Couro 1–2 (40%) |
| Urso | 1 / 1 | 35 | 7 | +0 | +1 | 5 | 50 | Mordida 1d10, Arranhão 2d8 | Couro 2–4 (60%), Dente de Urso (20%) |
| Bandido | 1–3 / 1–5 | 9 | 12 | +1 | +2 | 15 | 10 | Facada 1d4, Soco 1d3 | Ouro 4–17 (100%), Faca (35%) |

Além dos animais e bandidos, existe um encontro raro: a **Fada** (vida 4, defesa 14, acerto automático, ataque Brilho Cintilante 1d6, XP 30). Ela acontece **uma única vez por personagem** (20% de chance a cada exploração até ser encontrada) e oferece três opções:

- **Conversar** — teste de Sabedoria (dificuldade 14): se passar, ganha **+1 em um atributo aleatório**.
- **Lutar** — combate contra a fada; o **Pó da Fada** dela (drop garantido) vale **75 de ouro** — o maior item de valor do jogo, e é o ingrediente da **Mesa de Magias**.
- **Deixá-la em paz** — nada acontece.
</details>

---

## Floresta de Freijord

O hub do jogo é o menu **"FLORESTA DE FREIJORD"**:

1. **Ver ficha**
2. **Explorar a Floresta** — sempre gera um encontro (1/3 de período)
3. **Buscar Recursos na Floresta** — coleta materiais (1/3 de período)
4. **Construção (Dormir)** — construir/dormir
5. **Conversar com o companheiro** / **Salvar Jogo** / **Encerrar jogo** (os números variam se você tem um companheiro)

### Tempo

- Cada ação gasta **1/3 de período** (construir custa 2/3; treinar e estudar magia gastam o período inteiro).
- A cada 3 unidades, o período alterna entre **Dia** e **Noite**. O dia nº sobe no amanhecer.
- Ficar **2 noites sem dormir** deixa você **cansado** (−1 em testes).
- À **noite**, encontros ficam mais perigosos: grupos maiores de criaturas, e mais chance de emboscada.

### Teste de Presença (detectar ameaça)

Ao encontrar uma criatura, você rola `d20 + Presença` contra o teste de Presença dela:

- **Passou:** você a vê primeiro — **Lutar** (com **+2 de Iniciativa**) **ou fugir furtivamente** (teste de Destreza, dificuldade 12).
- **Falhou:** a criatura te surpreende e o combate começa sem bônus.

### Recursos na floresta

| Recurso | Chance | Quantidade | Uso |
|---|---|---|---|
| Madeira | 40% | 1–3 | Construção |
| Folha | 55% | 1–3 | Construção |
| Pedra | 35% | 1–3 | Construção |
| Frutas | 15% | 1–3 | Comer (cura 1d2) |

Coletar recursos (ou voltar para a cabana) tem **30% de chance de gerar um encontro de dia e 50% à noite**.

### Encontros aleatórios (a cada exploração)

1. **Descoberta do Labirinto** (só enquanto não encontrado; veja a seção Estruturas) — chance começa em **1%** e aumenta **+1% a cada dia** que passa
2. **10%** — Vendedor ambulante
3. **20%** — encontro secreto raro (veja o aviso de spoiler na seção de Criaturas)
4. Senão — **uma criatura da floresta** (a tabela com os nomes é spoiler; veja a seção de Criaturas)

---

## Construção

### Cabana

- **Custo:** 7 Madeira + 10 Folha + 4 Pedra (2/3 de período)
- Permite **dormir à noite** (recupera **metade da vida e metade da mana**) e zera o cansaço.
- É o pré-requisito para receber visitas (veja a seção de Companheiros).

### Sala de Treino

- **Custo:** 10 Madeira + 15 Folha + 5 Pedra + 4 **Couro** (2/3 de período; o Couro vem de criaturas — veja spoiler na seção de Criaturas)
- **Treinar:** gasta o período restante e dá **+2 em Força ou Destreza por 2 períodos** (reativa a cada novo treino). O bônus vale para dano, acerto, iniciativa e testes.

### Mesa de Magias

- **Custo:** 5 Madeira + 4 Folha + 4 Pedra + 1 **Pó da Fada** (2/3 de período; o Pó da Fada vem de um encontro secreto — veja spoiler na seção de Criaturas)
- **Estudar:** gasta o período restante e dá **+1 dado de dano em TODAS as habilidades de dano por 2 períodos** (magias, Estrondo, Giro e Explosão de Poder). O efeito vale os 2 períodos seguintes ao estudo (estudou de dia → vale na noite + no dia seguinte; estudou de noite → vale no dia + na noite seguinte). Não dá para estudar de novo enquanto o bônus estiver ativo.

---

## Estruturas Encontradas

### Labirinto

- **Como encontrar:** apenas **explorando a floresta**. A chance começa em **1%** por exploração e aumenta **+1% a cada dia** que passa (até 100%). Uma vez encontrado, não é sorteado de novo.
- Ao descobrir a entrada, o jogador escolhe **entrar agora** ou **não entrar** — se não entrar, o local fica acessível pelo menu principal (como as construções), na opção **Labirinto**.
- Ao entrar, a interface vira sobre o labirinto (apenas **ver a ficha** continua disponível).
- **O caminho é randomizado na descoberta e salvo na ficha** — ao voltar depois, continua exatamente de onde parou (células visitadas permanecem iluminadas).
- **Navegação:** a tela mostra apenas a grade ao redor do personagem — as **laterais/paredes** onde você está (`##`) e os corredores vizinhos (`.`) — mais o que **já foi percorrido** (iluminado `·`) e sua posição (`@`). O resto do labirinto fica escuro, inclusive o centro (visível apenas enquanto se explora). **W** = cima, **S** = baixo, **A** = esquerda, **D** = direita — executa no momento da tecla, **sem Enter**; qualquer outra tecla não faz nada. Uma câmera acompanha o personagem para que o labirinto (grade 31x31, bem maior) caiba na tela.
- Há **apenas uma entrada**. Ao pisar na **primeira casa do centro** (área central), o labirinto se encerra. **Casas especiais:** em algumas casas aleatórias há **encontros** (8 por labirinto) e em outras há **recompensas** (6 por labirinto) — cada casa especial é sorteada na geração e ativa uma única vez; as listas de encontros/recompensas ainda serão definidas.
  <details>
  <summary>⚠️ <b>Spoiler: o segredo do centro</b> — clique para revelar</summary>
  No coração do labirinto aguarda o **Minotauro** — a mecânica dele virá em etapa futura.
  </details>

---

## Vendedor

O **vendedor ambulante** aparece em 10% das explorações. O estoque é sorteado a cada visita (5 itens): itens do catálogo geral + itens exclusivos da sua classe.

### Preços de compra

| Item | Preço | Item | Preço |
|---|---|---|---|
| Faca | 20 | Espada (Guerreiro) | 40 |
| Machado | 35 | Espada Pesada (Guerreiro) | 70 |
| Machadinha | 20 | Machado de Guerra (Guerreiro) | 90 |
| Martelo | 50 | Martelo de Guerra (Guerreiro) | 90 |
| Mangual | 35 | Armadura Pesada (Guerreiro) | 100 |
| Arco | 40 | Bisturi (Healer) | 25 |
| Flechas | 3 / un. | Arco Refinado (Healer) | 60 |
| Lança | 50 | Nunchako (Healer) | 45 |
| Armadura Leve | 60 | Foice (Healer) | 55 |
| Poção de Mana | 15 | Cajado (Mago) | 30 |
| Kit Médico | 20 | Chapéu Mágico (Mago) | 50 |
| Poção Grande de Mana | 25 | Pequeno Grimório (Mago) | 60 |
| Madeira / Folha / Pedra | 6 / 4 / 5 | Frutas | 5 |

### Regras de venda

- O vendedor compra qualquer item seu por **50% do preço de compra**.
- **Exceção (preço cheio):** **Couro (6)**, **Dente de Urso (14)** e **Pó da Fada (75)** — os materiais raros valem o preço cheio. Guarde-os para construir ou venda quando precisar de ouro.

### Itens mágicos do Mago

- **Chapéu Mágico** (50g): **+3 de dano** em todas as suas magias.
- **Pequeno Grimório** (60g): magias pagas custam **1 de mana a menos** (mínimo 1).

---

## Companheiros

<details>
<summary>⚠️ <b>AVISO DE SPOILER: obtenção e permanência de companheiros</b> — clique para revelar</summary>

Depois que você **constrói a cabana**, a cada troca de período (dia/noite) há **20% de chance** de uma **pessoa perdida** aparecer pedindo abrigo. Você pode acolhê-la ou recusar. Se já tiver um companheiro, o visitante vai embora.

O companheiro é gerado com nome, classe aleatória (Mago/Guerreiro/Healer), nível 1 ou 2 e 6 pontos de atributos sorteados.

**Atenção — ele vai embora:** cada noite que dorme na cabana, o companheiro recupera metade da vida/mana. Na **primeira noite**, um contador de `1–3 dias` é sorteado; quando ele zera, **na manhã seguinte o companheiro agradece e parte para sempre.**
</details>

Enquanto estiver com você, o companheiro:

- Aparece no topo do menu (nome, classe e nível).
- Luta ao seu lado (iniciativa própria, agindo sozinho).
- **Conversar** mostra falas da classe e as habilidades dele.
- **Ver itens** mostra o inventário dele.
- **Curar com Kit Médico** cura ele em 1d4 (consome 1 do seu Kit). Ele também dorme na cabana à noite.
- **Despedir-se** (menu de conversa) faz o companheiro se despedir na hora e seguir o próprio caminho — útil se você quiser se livrar dele antes da hora (não há segunda chance).

---

## Itens

### Armas

| Arma | Dano | Tipo | Atributo | Observação |
|---|---|---|---|---|
| Soco | 1d3 | CaC | Força | Ataque desarmado universal |
| Espada | 1d8 | CaC | Força | Inicial do Guerreiro |
| Espada Pesada | 1d10 | CaC | Força | Vendedor (Guerreiro) |
| Machado de Guerra | 1d12 | CaC | Força | Vendedor (Guerreiro) |
| Martelo de Guerra | 1d12 | CaC | Força | Vendedor (Guerreiro) |
| Machado | 1d6 | CaC | Força | Vendedor |
| Machadinha | 1d4 | CaC | Força | Vendedor |
| Martelo | 1d8 | CaC | Força | Vendedor |
| Mangual | 1d6 | CaC | Força | Vendedor |
| Lança | 1d6 | CaC | **Ágil** | Usa o maior entre Força/Destreza |
| Faca | 1d4 | CaC | Destreza | Drop dos bandidos |
| Nunchako | 1d6 | CaC | Destreza | Vendedor (Healer) |
| Cajado | 1d4 | CaC/mágico | Força | Inicial do Mago; **obrigatório para lançar magias** |
| Bisturi | 1d4 → 3d8 | CaC | Ágil | Inicial do Healer; vira 3d8 com **Arma Mental** |
| Arco | 1d6 | à distância | Destreza | Consome Flechas |
| Arco Refinado | 1d8 | à distância | Destreza | Consome Flechas |
| Foice | 1d8 | à distância | Destreza | Consome Flechas |

### Armaduras

| Armadura | Bônus | Preço |
|---|---|---|
| Armadura Leve | +3 Defesa | 60g |
| Armadura Pesada | +5 Defesa | 100g (Guerreiro) |

A melhor armadura é equipada automaticamente; as demais ficam na mochila sem efeito.

### Consumíveis

| Item | Efeito | Use |
|---|---|---|
| Kit Médico | Cura 1d4 (em você ou no companheiro) | Combate (gasta o turno) ou inventário |
| Frutas | Cura 1d2 | Recurso coletado na floresta |
| Poção de Mana | +5 de mana | ✔ |
| Poção Grande de Mana | +7 de mana | Vendedor (Mago) |
| Flechas | Munição (3g/un.) | Consumida pelos arcos |

> Kit Médico: cada "uso" é uma unidade do item (o Healer começa com 5).

### Materiais e valor

| Material | De onde vem | Uso / valor |
|---|---|---|
| Couro | Saque de criaturas (ver spoiler na seção de Criaturas) | Construção da Sala de Treino (4x) · vende por 6g |
| Dente de Urso | Saque de criaturas (ver spoiler na seção de Criaturas) | Vende por 14g |
| Pó da Fada | Encontro secreto (ver spoiler na seção de Criaturas) | Ingrediente da Mesa de Magias · vende por 75g |
| Madeira / Folha / Pedra | Recursos da floresta | Construção |

---

## Salvar e Carregar

- Existem **3 slots** de save (`saves/save1.dat` a `save3.dat`).
- O menu mostra nome, classe, nível, dificuldade, período atual e a data do último save.
- **Não há autosave** — salve com frequência.
- **Apagar Save** pede confirmação antes de apagar.

---

## Estrutura do Projeto

```
├── pom.xml                        # Build Maven, dependências e testes
├── src/main/java/
│   ├── Main.java                  # Loop principal, menus e navegação
│   ├── classes/                   # ClasseRpg, Mago, Guerreiro, Healer
│   ├── fichas/FichaRpg.java       # Atributos, inventário, ouro, defesa, tempo
│   ├── fichas/ModoDificuldade.java# Normal e Difícil
│   ├── criaturas/                 # Criatura e CriaturaFactory (monstros e drops)
│   ├── eventos/Floresta.java      # Exploração, encontros, construção e vendedor
│   ├── mecanicas/                 # MotorDeCombate e MecanicasRpg (dados, fórmulas)
│   ├── habilidades/               # Habilidade e Magia
│   ├── habilidades/ativas/        # Implementações das habilidades ativas (Command)
│   ├── companheiros/              # Companheiro (NPC que te acompanha)
│   ├── itens/                     # ItemRpg, Arma, Armadura, Consumivel
│   ├── loja/Vendedor.java         # Comércio e preços
│   ├── narrativa/Aventura.java    # Prólogo
│   ├── comandos/                  # Padrão Command das ações de combate
│   └── telas/Interface.java       # Menus, ficha e entradas do jogador
└── src/test/java/                 # Suíte de testes automatizados
```

## Conceitos Aplicados

- RPG de decisões: atributos, modificadores de classe, testes de atributo, defesa por Destreza/armadura, iniciativa, crítico e gerenciamento de recursos (vida, mana, ouro, itens, fadiga).
- Java OO com pacotes, herança, polimorfismo e serialização de saves.
- Padrões de projeto: **Command** (ações de combate) e **Strategy** (classes de personagem).
- Suíte de testes automatizados (JUnit 5 + Mockito) cobrindo regras matemáticas, inventário e combate.
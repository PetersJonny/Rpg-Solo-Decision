# Solo RPG Decision

Um **RPG de mesa single-player** jogado no terminal, em **Java** (requer Java 21+). Você cria sua ficha, enfrenta criaturas, evolui com XP, constrói abrigos e luta para sobreviver na floresta gélida de **Freijord**.

Este README é o guia completo do jogo: se ficar perdido em qualquer momento, volte aqui.

> **O que é spoiler aqui**
>
> **Mecânica não é spoiler.** Regras, fórmulas, tabelas de atributos, classes, raças, progressão, combate, economia das lojas, pesos, construções e o funcionamento dos sistemas são documentação — eles são implementados no código e não revelam nada do mundo que você ainda não tenha visto ao jogar.
>
> **O que é spoiler é o conteúdo do mundo:** criaturas específicas e seus números, encontros, eventos, personagens que aparecem, segredos, chefes e tesouros. Saber que "existe um goblin na entrada da mina" ou "o labirinto tem um chefe" já é uma vantagem que o jogador não teria.
>
> Tudo que se enquadrar nesse segundo tipo está dentro de um bloco `<details>` com aviso. Os blocos são **recolhidos por padrão** — dá para ler o guia inteiro sem encontrar spoiler nenhum.

---

## Sumário

- [Como Rodar](#como-rodar)
- [Fluxo do Jogo](#fluxo-do-jogo)
- [Criação de Personagem](#criação-de-personagem)
  - [Atributos](#atributos) · [Raças](#raças) · [Fadiga](#fadiga-cansado) · [Fome e saciedade](#fome-e-saciedade) · [Dificuldade](#dificuldade)
- [Mochila e Peso](#mochila-e-peso)
- [Classes](#classes)
- [Progressão e Níveis](#progressão-e-níveis)
- [Defesa](#defesa)
- [Combate](#combate)
- [Criaturas](#criaturas) *(spoiler)*
- [Floresta de Freijord](#floresta-de-freijord)
- [Travessia (Sair da Floresta)](#travessia-sair-da-floresta)
- [Vilarejo de Scarbor](#vilarejo-de-scarbor)
  - [Taverna](#taverna) · [Ferreiro](#ferreiro) · [Alfaiataria](#alfaiataria) · [Barraca de Frutas](#barraca-de-frutas) · [Casa do Chapéu Mágico](#casa-do-chapéu-mágico)
- [Missões](#missões) *(spoiler)*
- [Construção](#construção)
- [Estruturas Encontradas](#estruturas-encontradas) *(spoiler parcial)*
- [Vendedor](#vendedor)
- [Companheiros](#companheiros)
- [Itens](#itens)
- [Salvar e Carregar](#salvar-e-carregar)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Conceitos Aplicados](#conceitos-aplicados)

---

## Como Rodar

#### Requisitos

- **Java 21+** (JDK 21)
- **Apache Maven** (ou uma IDE compatível: IntelliJ IDEA, Eclipse, VS Code)

#### Comandos

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
2. **Criação de Ficha** — nome, **raça**, distribuição dos atributos, classe e dificuldade. A ficha só é liberada quando completa.
3. **Prólogo** — introdução narrada sobre Freijord (opção de pular).
4. **Floresta de Freijord** — o "hub" do jogo. Daqui você explora, coleta recursos, constrói, dorme, conversa com o companheiro e salva o jogo.
5. **Exploração** — cada exploração gera um encontro (com comerciantes, criaturas e, raramente, algo mais). O dia e a noite alternam a cada 3 unidades de período.
6. **Combate** — resolução por iniciativa, com ataques, magias, habilidades, fuga e saque.

Não existe autosave: **salve manualmente** (`Salvar Jogo`, disponível na floresta e no vilarejo).

---

## Criação de Personagem

Você distribui **6 pontos** entre os seis atributos, escolhe uma **raça**, uma **classe** (Mago com um elemento, Guerreiro ou Healer) e a **dificuldade**.

#### Atributos

| Atributo | Papel principal |
|---|---|
| **Constituição** | Vida; pontos extras aqui dão vida retroativa por nível já ganho |
| **Destreza** | Iniciativa, Defesa, testes de fuga, armas à distância e armas "Ágeis" |
| **Força** | Dano e acerto de armas corpo a corpo e do soco |
| **Sabedoria** | Testes sociais (ex.: conversas e negociações) |
| **Intelecto** | Testes de conhecimento, salvamento de companheiro |
| **Presença** | Detecta ameaças antes de serem emboscadas; define a Mana |

#### Raças

Cada raça dá **+1 em um atributo** e uma **passiva**. As duas coisas valem para sempre — não há escolha de build que "descarte" uma passiva.

| Raça | Atributo | Passiva | Efeito |
|---|---|---|---|
| **Humano** | +1 em um atributo à sua escolha | **Vontade de Viver** | Uma vez por dia, ao ser reduzido a 0 de vida, você sobrevive com 1 |
| **Elfo da Floresta** | +1 em Destreza | **Toque da Mata** | 30% de chance de ganhar 1 material extra ao buscar recursos |
| **Vigia do Crepúsculo** | +1 em Sabedoria | **Visão na Penumbra** | +2 em todos os testes durante a noite |
| **Meio-Fada** | +1 em Presença | **Encanto Feérico** | Dobra a chance de um certo **encontro secreto** ao explorar e reduz em 1 o custo de mana de magias e habilidades (nunca abaixo de 1) |
| **Dracônico** | +1 em Constituição | **Escamas de Dragão** | +2 de defesa permanente e +2 de vida máxima |
| **Meio-Orque** | +1 em Força | **Fúria Sombria** | Com 30% ou menos de vida, o dado de dano das suas armas sobe um degrau (1d4→1d6, 1d6→1d8, ...); em 1d12 (máximo), ganha +1d4 extra |
| **Gnomo** | +1 em Intelecto | **Mente Afiada** | Uma vez por dia, você pode refazer um teste de Intelecto ou Sabedoria que falhou |

> **Humano** é a única raça em que o atributo bônus é escolhido na criação (o padrão, se nada for escolhido, é Constituição).

#### Fadiga (cansado)

Depois de **2 noites sem dormir**, o personagem fica **cansado**: **−1 em todos os testes de atributo** (iniciativa, presença, fuga, intelecto, etc.). Não afeta vida, mana ou dano. Dormir na cabana remove o cansaço.

#### Fome e saciedade

- A fome é contada em **dias sem comer** e zera assim que você come qualquer coisa que sacie.
- **1 dia sem comer:** −1 em testes de Destreza e Força. **5 dias ou mais:** além da penalidade, o personagem **perde vida a cada período** (o valor cresce com os dias).
- **Comida estragada** (carne que passou do ponto, inclusive a carne crua que ficou guardada tempo demais) sacia a fome, mas deixa **enjoado**, com −1 em testes de Destreza e Força até o próximo dia.
- **Frutas**: **3 frutas** (ou a opção "Frutas", que junta várias) contam como **1 refeição**.
- **Saciedade (estar cheio):** cada refeição que sacia a fome conta **1 de 3 por dia**. Ao comer a **terceira**, o personagem fica **cheio** e **não consegue comer mais nada** — o jogo recusa o uso de qualquer comida e **a comida permanece no inventário**. A saciedade zera ao virar o dia. Os menus da floresta e do vilarejo mostram o estado: `Saciado (1/3 refeições hoje)` e `CHEIO (3/3 refeições hoje | não cabe mais nada)`.
- **Dormir** divide a cura pela metade quando você comeu no dia (`vida máxima / 2`) e usa a divisão completa quando não comeu (`vida máxima / 3`).

#### Dificuldade

- **Normal** — ao morrer, os saves do personagem são mantidos.
- **Difícil** — morte permanente: ao morrer, **todos os saves com o nome do personagem são apagados**. Não há segunda chance.

---

## Mochila e Peso

Toda a sua munição, comida, ouro em forma de item e sucata ocupa **peso** — e a mochila tem limite.

```
Capacidade da mochila = 10 + 5 × Força      (mínimo de 10)
```

Itens com peso `0` não ocupam espaço. Quando um item não cabe, o jogo informa **quantas unidades ainda cabem** em vez de simplesmente recusar — a exceção são equipamentos: para **equipar** uma peça nova é preciso que ela caiba inteira.

| Peso | Itens |
|---|---|
| **0,0** | (nenhum item comum) |
| **0,1** | Couro, presas e dentes de criaturas, Carnes (cruas, cozidas e estragadas), Ossos, duas armas raras de chefe, um troféu de chefe, um pó raro de encontro secreto, Flechas, Madeira, Folha, Pedra, Frutas e as frutas da barraca |
| **0,2** | Gota de Veneno e um consumível raro de missão (ver spoiler) |
| **0,3** | Poção de Mana, Kit Médico, pratos e bebidas da taverna |
| **0,5** | Tocha, Ampulheta de Prata, Amuleto do Coração, Chapéu Mágico, Gema de Mana, Luvas de Prata, Manto do Astrólogo, Pequeno Grimório, Pó de Midas |
| **0,6** | Poção Grande de Mana, roupas (Capa do Viajante, Botas de Correio, Lenço de Seda, Manto do Atirador, Túnica de Aventureiro) |
| **1,0** | Demais armas leves e itens de valor |
| **2,0** | Espada Pesada, Machado de Guerra, Martelo de Guerra, Armadura Pesada, Porrete e a espada rara de chefe do Guerreiro |

Dica: **Flechas, Madeira, Folha, Pedra e materiais** são baratos em peso. Já armas pesadas ocupam o equivalente a dezenas de unidades de material.

---

## Classes

#### Mago

- **Vida base:** 10 + Constituição | **Mana base:** 8 + Presença
- **Modificadores:** Força −2, Presença +2, Intelecto +1
- **Itens:** Cajado (1d4, CaC/mágico), Poção de Mana (+5 mana)
- **Magias iniciais:** **Bola Elementar (3d10, 2 de mana)** e **Pequena Magia (2d8, grátis)**
- **A cada nível:** vida +2 + Constituição | mana +3 + Presença
- **Nível 3 (automático):** a Pequena Magia vira **ataque em área** (atinge também os inimigos adjacentes)

| Nível | Escolha | Custo | Efeito |
|---|---|---|---|
| 5 | **Magia Desperta** | 6 | 6d12 de dano em um alvo |
| 5 | **Proteção Absoluta** | 5 | +3 de defesa e reflete 2d8 do seu elemento em quem te acertar (até o fim do combate) |
| 7 | **Prisão** | 5 | Prende um inimigo; na vez dele, ele precisa tirar 15+ em um d20 para se libertar |
| 7 | **Magia Proibida** | 5 | Uma vez por combate, sem gastar ação: todos os testes dos inimigos desta rodada falham |
| 9 | **Poder Absoluto** | 15 | Dobra a quantidade de dados de dano das suas magias até o fim do combate |
| 10 | **Explosão de Poder** | variável (mín. 2) | Cada 2 de mana gasta causa 2d12 em **todos** os inimigos |

> Para usar magias, o Mago precisa de um **Cajado** no inventário.

#### Guerreiro

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

#### Healer

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

O nível máximo é **10**. O XP vem **apenas de matar criaturas**.

#### Tabela de XP

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

#### Recompensas por nível

- **Nível 3:** habilidade automática da classe (Guerreiro: Peso da Espada; Healer: Cura Reforçada; Mago: Pequena Magia vira área).
- **Níveis 2, 4, 6 e 8:** **+1 ponto de atributo** (Constituição dá vida retroativa).
- **Níveis 5, 7, 9 e 10:** **escolha 1 habilidade** entre as opções da sua classe (nesta e nas anteriores — o que você deixou para trás pode ser escolhido depois).

---

## Defesa

A Defesa é calculada assim:

```
Defesa = 10 + Destreza   (+ bônus da armadura equipada)
        (+ 3 se Proteção Absoluta ativa)
        (+ 5 se Defesa Absoluta ativa, até você atacar)
        (+ 1 se você tem o Lenço de Seda)
        (+ 2 se sua raça é Dracônico)
```

- A **melhor armadura** do inventário é equipada automaticamente (as armaduras extras na mochila não somam defesa).
- Um ataque acerta se `d20 + bônus` for maior ou igual à Defesa do alvo.

---

## Combate

#### Iniciativa

Cada combatente rola `d20 + Destreza` (você e o companheiro) ou `d20 + Iniciativa` (criaturas). Todos agem em **ordem decrescente** a cada rodada. Se você detectou a ameaça primeiro (teste de Presença) e escolheu lutar, ganha **+2 de Iniciativa**.

#### Ações por turno

1. **Lutar** — atacar com arma ou soco, usar habilidade, ativar passiva manual (ex.: Casca Grossa, Espada Afiada) ou usar Magia Proibida (ação livre).
2. **Abrir Mochila** — usar um consumível (gasta o turno).
3. **Tentar Fugir** — precisa passar **3 vezes** no teste de fuga.
4. **Ver Ficha** — sem custo de ação.

Depois do **Estrondo**, você fica **2 rodadas sem poder usar habilidades** (itens, ataques e fuga continuam liberados).

#### Ataque

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
- **Túnica de Aventureiro** dá +1 de dano corpo a corpo; **Manto do Atirador** dá +1 de dano à distância.

#### Fuga

Teste: `d20 + Destreza` contra **10 + Iniciativa da criatura mais rápida viva**. Você precisa de **3 sucessos** para fugir. Falhar dá um **ataque grátis da criatura mais rápida** e o turno segue normalmente.

#### Magia

- Custo de mana é descontado ao lançar. Cada dado é rolado e exibido.
- **Pequeno Grimório** (item) reduz o custo das magias pagas em 1 (mínimo 1).
- **Chapéu Mágico** (item) adiciona **+3** de dano a todas as magias.
- **Pó de Midas** (item) adiciona **+10%** na chance de encontrar itens e ouro nas criaturas.
- **Poder Absoluto** dobra a quantidade de dados das magias.
- **Mesa de Magias** (construção) adiciona **+1 dado de dano** a **todas** as habilidades de dano (magias, Estrondo, Giro e Explosão de Poder) por 2 períodos.
- **Peso da Espada** soma + Força ao dano.
- **Magia em área** atinge o alvo e os inimigos adjacentes na lista.
- Magias **sempre acertam** (sem teste de defesa) e aplicam o veneno da Cura para a Morte no alvo.
- **Meio-Fada** reduz em 1 o custo de mana de magias e habilidades (nunca abaixo de 1).

#### Veneno em combate

A **Gota de Veneno** (item da Casa do Chapéu Mágico) é aplicada por uma ação inteira: a opção **"Aplicar Gota de Veneno na arma"** consome a gota e gasta a rodada. O próximo ataque **com arma** que acertar envenena o alvo, causando **1d4 por rodada até o fim do combate**. Se o ataque errar, o veneno seca. Não funciona com magia.

#### Companheiro em combate

O companheiro age sozinho no seu turno de iniciativa:

- **Healer:** cura você (2d4) se estiver abaixo de 60% de vida; senão, cura a si mesmo.
- **Mago:** lança sua magia do elemento (60%) ou a Pequena Magia grátis (30%); senão, ataque físico.
- **Guerreiro/outros:** ataque físico com a arma.
- Os inimigos têm **50% de chance** de atacar o companheiro em vez de você (quando ele está vivo).

#### Morte e resgate

- Se **você** cair a 0 de vida, o Healer tenta reviver com **Cura Total** (10 de mana, uma vez por combate).
- Se **você** for curado para 0 e sua raça for **Humano**, a passiva **Vontade de Viver** pode deixá-lo com 1 de vida (uma vez por dia).
- Se o **companheiro** morrer, você tenta estabilizá-lo: precisa de **Intelecto ≥ 14** e um **d20 ≥ 16**. Se o resgate falhar, o companheiro morre em definitivo e **todos os itens e o ouro dele passam para você**.
- Companheiro vivo com menos de 30% de vida após a vitória se recupera para 50%.

#### Vitória e Derrota

- **Vitória:** cada criatura processa seus drops (ouro e itens) e concede XP.
- **Derrota:** sua jornada termina e o jogo volta ao menu principal (a dificuldade define se os saves sobrevivem).

---

## Criaturas

<details>
<summary>⚠️ <b>Spoiler: as criaturas da floresta</b> — clique para revelar</summary>

Além dos animais e bandidos, existe um encontro raro: a **Fada** (vida 4, defesa 14, acerto automático, ataque Brilho Cintilante 1d6, XP 30). Ela acontece **uma única vez por personagem** (20% de chance a cada exploração até ser encontrada; **40%** se sua raça for **Meio-Fada**) e oferece três opções:

- **Conversar** — teste de Sabedoria (dificuldade 14): se passar, ganha **+1 em um atributo aleatório**.
- **Lutar** — combate contra a fada; o **Pó da Fada** dela (drop garantido) vale **75 de ouro** (o vendedor paga **52**) e é o ingrediente da **Mesa de Magias**.
- **Deixá-la em paz** — nada acontece.

Os grupos de criaturas da floresta são sorteados assim (a noite sempre permite grupos maiores):

| Tipo | Qtd de dia | Qtd de noite |
|---|---|---|
| Lobo Selvagem | 1–2 | 1–4 |
| Urso | 1 | 1 |
| Bandido | 1–3 | 1–5 |

| Criatura | Vida | Defesa | Iniciativa | Acerto | Teste Presença | XP | Ataques | Saque |
|---|---|---|---|---|---|---|---|---|
| Lobo Selvagem | 14 | 10 | +3 | +3 | 8 | 25 | Mordida 1d6, Aranhão 2d4 | Couro 1–2 (40%), Carne de Lobo 1–2 (40%) |
| Urso | 35 | 7 | +0 | +1 | 5 | 50 | Mordida 1d10, Aranhão 2d8 | Couro 2–4 (60%), Carne de Urso 1–2 (40%), Dente de Urso (20%) |
| Bandido | 9 | 12 | +1 | +2 | 15 | 10 | Facada 1d4, Soco 1d3 | Ouro 4–17 (100%), Faca (35%) |

</details>

<details>
<summary>⚠️ <b>Spoiler: as criaturas do Labirinto</b> — clique para revelar</summary>

Nos 8 **encontros** do labirinto: **40% Esqueleto, 40% Zumbi e 20% Baú**. Fugir usa **Teste de Destreza** contra a dificuldade de cada criatura (dificuldade mostrada abaixo).

| Criatura | Nível | Vida | Defesa | Iniciativa | Acerto | Fuga (Destreza) | XP | Ataques | Saque |
|---|---|---|---|---|---|---|---|---|---|
| Esqueleto | 2 | 12 | 12 | +4 | +2 | 15 | 40 | Arco 1d6, Ataque de Ossos 1d4 (80% de atacar de novo, 33% de um terceiro) | Osso 1–3 (30%), Arco (10%), Flechas 1–7 (35%) |
| Zumbi | 2 | 18 | 10 | +2 | +3 | 10 | 40 | Mordida 1d6 (30% de infectar: 1d4 de dano por rodada de combate, curada com Kit Médico) | Carne Podre 1–4 (40%) |
| Baú Monstruoso | 3 | 25 | 10 | +4 | +4 | 12 | 50 | Mordida 1d8 | Ouro 4–17 (100%) |
| Minotauro\* | 5 | 150 | 15 | +5 | +4 | 25\*\* | 500 | Garras 2d6, Chifre 1d12, **Investida 20%** | Chifre de Minotauro 1–2 (50%) + recompensa por classe |

\* **Minotauro** é o chefe do centro e não pode ser encontrado nas casas comuns. \*\* A fuga dele é bloqueada: a porta se fecha, então nem o Teste de Destreza é oferecido.

O **Baú** (encontro) oferece um **baú antigo**: dá para **abrir** ou **não abrir**. Abrindo, **50%** tem ouro (7–14) e **50%** um **Baú Monstruoso** salta para o combate. O vendedor ambulante compra **Osso** e **Carne Podre** normalmente. Os três são **mortos-vivos**: a **Espada Majestral** causa **dano dobrado** contra eles.

</details>

<details>
<summary>⚠️ <b>Spoiler: os tesouros raros do Labirinto</b> — clique para revelar</summary>

Os **3%** das casas de recompensa sorteiam um **tesouro raro** entre os que ainda não foram encontrados (cada um cai **uma única vez** por ficha, na ordem Olho Demoníaco → Espada Majestral → Coroa do Rei):

| Tesouro | Efeito |
|---|---|
| **Olho Demoníaco** | Ao tocá-lo, um **chamado** sussurra: **Aceitar** funde o olho em você e concede a habilidade **Pacto Mortal** (4 de mana) — o olho não vira item de mochila, é a própria habilidade que ele abre; **Recusar** dá nada, mas o olho já conta como encontrado e não aparece de novo. |
| **Espada Majestral** | Arma **CaC**: **1d12 + Força + 1d4 de dano de luz** (luz também dobra no crítico) e **dano total dobrado contra mortos-vivos** (Esqueleto, Zumbi e Baú Monstruoso). |
| **Coroa do Rei** | Item de **valor alto** (o vendedor paga **700**). Exige um **Teste de Intelecto 18+** para desvendar o segredo: passando, revela a habilidade **Rei das Criaturas** (funciona enquanto a Coroa estiver consigo); falhando, guarda a Coroa mesmo assim. |

**Pacto Mortal** (habilidade ativa, 4 de mana, alvo obrigatório): até o fim do combate o alvo tem **-2 em suas rolagens** e **+5 de dano demoníaco** em cada golpe sofrido, mas **todos os danos que você receber aumentam em +3** enquanto a maldição durar (usar o Olho é obrigatório).

**Rei das Criaturas** (3 de mana, **não gasta a ação**): rode um **Teste de Presença** (1d20 + Presença) contra cada criatura (1d20 + nível). Vencendo, você a **comanda**: **fugir** do combate (some sem dar XP nem saque), **atacar a si mesma** ou **atacar outro monstro** do combate.

</details>

---

## Floresta de Freijord

O hub do jogo é o menu **"FLORESTA DE FREIJORD"**:

1. **Ver ficha**
2. **Explorar a Floresta** — sempre gera um encontro (1/3 de período)
3. **Buscar Recursos na Floresta** — coleta materiais (1/3 de período)
4. **Construção (Dormir)** — construir, caminhar até uma construção ou dormir (veja a seção [Construção](#construção))
5. **Tentar sair da floresta** — caminha adentrando a mata, em busca de algo além de árvores e mato (veja a seção [Travessia](#travessia-sair-da-floresta))
6. Opções condicionais: **Labirinto** (depois de descoberto), **uma busca na mata da entrada** (só depois de certo acontecimento), **Conversar com o companheiro**
7. **Salvar Jogo** / **Encerrar jogo**

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

A rolagem acontece nesta ordem:

1. **Descoberta do Labirinto** (só enquanto não encontrado; veja [Estruturas Encontradas](#estruturas-encontradas)) — chance começa em **1%** e aumenta **+1% a cada dia** que passa
2. **10%** — Vendedor ambulante
3. **20%** — um encontro secreto raro (40% para **Meio-Fada**) — acontece uma única vez por personagem (ver spoiler na seção de Criaturas)
4. Senão — **uma criatura da floresta** (a tabela com os nomes é spoiler; veja a seção de Criaturas)

---

## Travessia (Sair da Floresta)

Dizem que quem caminha por tempo suficiente, adentrando a mata em busca de algo além de árvores e mato, acaba saindo da floresta gélida de Freijord:

- A opção **"Tentar sair da floresta"** (menu principal da floresta) faz você caminhar para o fundo da mata. Você escolhe **quantos períodos caminhar por vez** (de **1 a 3**, cada um gasta 1/3 do período) e, **após cada período**, decide se **continua caminhando ou para por aqui**.
- Leva **20 períodos no total** para sair da floresta — mas esse **contador é oculto**: você nunca sabe o quanto falta. A única forma de descobrir é pela distância mostrada até suas construções.
- Cada período de caminhada tem **a mesma chance de encontro da exploração** (30% de dia, 50% à noite) — só que **sem encontrar recursos**.
- Pode acontecer de você parar e o menu **Construção** ficar acessível no ponto atual: cada construção é **ancorada no ponto da mata onde foi montada**, então a opção sempre mostra **a distância (em períodos de caminhada) até cada construção** e permite **caminhar até ela** (mesmo custo turno a turno da ida) ou **montar uma construção nova ali**, que passa a ser o novo ponto (veja a seção [Construção](#construção)).
- Ao sair da floresta, o jogador chega a uma **cidade para além dela** (o destino é sorteado; hoje o único destino é o **Vilarejo de Scarbor**). Enquanto estiver fora das suas construções, **não dá para construir, treinar ou dormir** — é preciso voltar para as construções.
- **Virada de tempo na chegada:** se o jogador sair da floresta pela primeira vez e o tempo virar (dia→noite ou noite→dia) **exatamente** no momento em que ele chega à vila, a cena de chegada **não acontece**. Ele vê apenas a descrição da chegada e entra direto no menu da taverna, podendo comer e ler o quadro de missões. Isso vale **só na primeira chegada**.
- **A cena é um evento de chegada, não contínuo.** Depois de resolvida, ela nunca mais é narrada, mesmo que o jogador entre e saia da floresta várias vezes. Se ele ignorou a taverna na primeira chegada, a cena continua pendente e aparece ao entrar na taverna.

<details>
<summary>⚠️ <b>Spoiler: o assalto na chegada à vila</b> — clique para revelar</summary>

Na **primeira chegada** ao Vilarejo de Scarbor, a taverna está sendo assaltada: **4 goblins** pulam sobre as mesas exigindo ouro.

Você pode:

- **Ir até a taverna ver o que está acontecendo** ou **deixar para depois e seguir seu caminho** (a confusão fica pendente até você entrar).
- Diante dos goblins: **sair dali furtivo** (teste de Destreza), **ir lutar**, **tentar conversar com eles** ou **tentar ir lutar furtivo**.
- **Conversar** tem duas rotas: **ameaçar** (teste de Presença) ou **tentar entender o motivo** — é aqui que o jogo explica que os **dracônicos confiscaram as minas de ouro dos goblins**, e o grupo passa a ver você como parte da dívida. Se você **perguntar o que tiraram deles**, entende na hora e a fuga seguinte fica mais fácil (DT 5 em vez de 15).
- **Se você for Dracônico**, o líder já começa com a história na cara — ele reconhece a raça antes de você dizer nada, e a memória das minas tomadas volta à tona. Isso muda o diálogo e a fuga.

Quem resolve o assalto vira conhecido da casa: o dono da taverna, **Draven Moreau**, agradece pessoalmente e oferece **30 de ouro** — mas só na primeira vez. Se você recusar, ele insiste uma vez.

Se **Draven** te pagou a comida uma vez (por ter chegado com fome), a **primeira refeição na taverna é por conta da casa**.

</details>

---

## Vilarejo de Scarbor

- O menu do vilarejo permite **Ver ficha**, **Olhar em volta**, **ir à taverna**, **ir ao ferreiro**, **ir à Casa do Chapéu Mágico**, **Ver missões em andamento**, **Voltar para a floresta** (refaz todo o caminho de volta até as construções), **Salvar Jogo** e **Encerrar jogo**.
- **Opções condicionais:** a **alfaiataria** só aparece depois que você conheceu a alfaiateira; a **barraca de frutas** e a **caverna** só entram no menu depois de certos acontecimentos do jogo (veja os spoilers das missões).
- **Cada visita a uma loja gasta 1 período do dia** (taverna, ferreiro, alfaiataria, barraca de frutas e Chapéu Mágico). O período é cobrado ao sair da loja, então comprar vários itens no mesmo dia só custa 1 período. A caverna e o quadro de missões **não** cobram período.

### Taverna

A taverna é o **coração da vila**: serve comida, tem o quadro de missões na parede, e é onde o dono se apresenta.

**Cardápio** (o prato vai para a mochila e você come de dentro dela):

| Prato | Preço | Efeito |
|---|---|---|
| Pão Quente com Manteiga | 4 | Sacia a fome (não cura vida) |
| Sopa do Vilarejo | 6 | Cura 1d2 e sacia |
| Ovos Mexidos | 7 | Cura 1d3 e sacia |
| Torta de Frutas | 7 | Cura 1d3 e sacia |
| Hidromel | 8 | Restaura 1d4 de **mana** (não sacia) |
| Caldo de Lobo | 9 | Cura 1d4 e sacia |
| Peixe Assado | 9 | Cura 1d4 e sacia |
| Estofado de Urso | 13 | Cura 1d6 e sacia |

Além de comer, na taverna você pode **ler o quadro de missões** e **perguntar onde fica a alfaiataria** (é assim que você descobre a Célia). Se você chegou à vila com fome, o dono pode pagar a primeira refeição.

### Ferreiro

O **Gorak Vieira** é o ferreiro da vila. O menu tem quatro abas: comprar do estoque do dia, encomendar, vender e retirar encomenda.

- **Estoque do dia:** 5 itens sorteados (com a semente do dia, então é o mesmo estoque o dia inteiro). Pode incluir armas gerais, armaduras e itens restritos de classe.
- **Encomenda:** qualquer peça do acervo do ferreiro, por **+20%** do preço, **pronta em 1 dia completo**. Você pode encomendar e buscar depois — o pedido fica salvo no save.
- **Vender:** o ferreiro compra **armas e armaduras** por **80%** do valor cheio. É o melhor comprador do jogo para equipamento.

### Alfaiataria

A alfaiateira **Célia Morel** costura na praça do comércio. A alfaiataria só aparece no menu depois que você a conheceu (perguntando na taverna).

- **Comprar roupas:** 5 peças, uma de cada (não dá para comprar duas iguais nem empilhar bônus).

| Roupa | Preço | Efeito |
|---|---|---|
| **Lenço de Seda** | 60 | +1 de Defesa permanente |
| **Botas de Correio** | 70 | +1 em testes de Destreza |
| **Capa do Viajante** | 80 | +4 de vida ao dormir na cabana |
| **Túnica de Aventureiro** | 90 | +1 de dano em ataques corpo a corpo |
| **Manto do Atirador** | 90 | +1 de dano em ataques à distância |

- **Vender couro:** Célia paga **80%** do valor cheio do Couro — mais que o vendedor ambulante.

### Barraca de Frutas

Perto da entrada da vila, com uma **velhinha de xale surrado** no balcão. Só aparece no menu depois de um certo acontecimento (ver spoilers das missões).

- **Comprar frutas:** 6 frutas, preço por unidade, quantidade à vontade (respeitando o espaço da mochila).

| Fruta | Preço | Efeito |
|---|---|---|
| Figo Seco | 5 | Sacia totalmente a fome (não cura vida) |
| Maçã | 6 | Cura 1d3 e conta como comida |
| Ameixa | 7 | Cura 1d2 e **cura o enjoo** |
| Pera | 8 | Cura 1d2 e restaura 1d3 de mana |
| Uva | 9 | Restaura 1d4 de mana |
| Morango Selvagem | 12 | Cura 1d3 |

- **Olhar ao redor:** procura pistas. Com a missão ativa, a velhinha confirma onde a criança desapareceu — e isso é registrado nas novidades da missão (ver spoilers).
- **Missões concluídas:** voltar à barraca depois de resolver a missão encerra ela e entrega a recompensa (ver spoilers).

### Casa do Chapéu Mágico

Loja da **Dona Maga**, fica **no começo da vila, ao lado do ferreiro**. Como o ferreiro, só vende **itens mágicos** — e também só **compra** itens mágicos, pagando 80% do valor.

- O estoque muda a cada dia: **5 itens sorteados**.
- **Não-consumível: 1 unidade cada.** Depois de comprar, aquele item fica esgotado para você (a loja não vende duplicata).
- **Consumível: entre 1 e 5 unidades** do mesmo item, e o estoque só renova no dia seguinte.

| Item | Preço | Efeito |
|---|---|---|
| Poção Grande de Mana | 30 | Restaura 7 de mana (consumível, 1–5 por dia) |
| Gota de Veneno | 40 | Ver [Veneno em combate](#veneno-em-combate) (consumível, 1–5 por dia) |
| Chapéu Mágico | 70 | +3 de dano nas suas magias |
| Ampulheta de Prata | 80 | +1 de Iniciativa |
| Pequeno Grimório | 90 | As magias custam 1 de mana a menos |
| Luvas de Prata | 90 | Ataques com arma tiram **1 da Defesa do alvo**. Não acumula por ataque nem por par de luvas |
| Gema de Mana | 100 | +10 de mana máxima (a mana atual sobe junto) |
| Amuleto do Coração | 110 | +10 de vida máxima (a vida atual sobe junto) |
| Manto do Astrólogo | 150 | +1 em todos os testes de Intelecto |
| Pó de Midas | 70 | +10% na chance de encontrar itens e ouro nas criaturas |

---

## Missões

O quadro de missões fica **na parede da taverna**. Ele guarda avisos pregados com alfinete, e cada um tem local, objetivo e recompensa. A estrutura é a mesma para todas: você lê o aviso, aceita, e o jogo passa a registrar **novidades** sobre o que você já descobriu daquela missão.

<details>
<summary>⚠️ <b>Spoiler: as missões do quadro</b> — clique para revelar</summary>

Só **A Filha Perdida** aparece no quadro. **A Neta Perdida** existe, mas nunca é anunciada no quadro — você só a recebe pessoalmente (ver o bloco da netinha abaixo).

| Missão | Onde | Objetivo | Recompensa |
|---|---|---|---|
| **A Filha Perdida** | Perto das cavernas ao redor da vila | Uma filha foi vista por último perto das cavernas da vila. É preciso encontrá-la antes que algo pior aconteça. | 100 ouro — falar com a dona da alfaiataria ao encontrá-la |

O aviso fica no quadro mesmo depois de encerrada (a formalização do funeral), e some 2 dias depois.

</details>

---

<details>
<summary>⚠️ <b>AVISO DE SPOILER: a mina, o prazo e o funeral de A Filha Perdida</b> — clique para revelar</summary>

#### A Caverna (A Filha Perdida)

A caverna fica no fundo da vila, encostada no paredão da montanha. É o lugar que Célia Morel indicou — e ela só conta onde fica **depois que você aceita a missão**.

Ao chegar na entrada:

1. **Teste de Presença DT 12** — se passar, você percebe um **pequeno goblin** encostado do lado de fora do vão. Ele te vê e corre para dentro. Se você for **Dracônico**, ele se encolhe assustado antes de sumir na escuridão (texto diferente). Se falhar, você não vê nada. O goblin só precisa ser notado uma vez.
2. Escolha: **olhar em volta da entrada** ou **entrar na caverna**.
3. Se olhar em volta, **Teste de Presença DT 7** — se passar, encontra uma **tocha** (peso 0,5 kg) entre as pedras, com a mesma pergunta de pegar ou deixar para trás usada no resto do jogo. A tocha só precisa ser vista uma vez.
4. Escolha final: **voltar para a vila** ou **entrar na caverna**.

**Escuridão:** lá dentro, todo teste leva **-2** por está escuro — a menos que você esteja **com a tocha na mão**, o que anula a penalidade. O bônus só vale com a tocha realmente na mochila: se ela for vendida ou derrubada, o -2 volta.

#### O Prazo de 3 Dias (A Filha Perdida)

Ao aceitar **A Filha Perdida**, o jogo registra o dia do aceite e passa a contar **3 dias inteiros**. Não há nenhum aviso antes: o jogador não recebe nenhuma dica de que existe um prazo.

**No terceiro dia (ou no dia em que o jogador voltar à vila depois disso), se ele estiver dentro da vila:**

1. A vila acorda errada. Os guardas desceram à mina de madrugada, entraram, viram o que havia lá dentro e voltaram **sem ninguém**.
2. A filha é encontrada **morta** dentro da mina, e a **boca da mina é fechada para reforma** — com tábuas novas, corda de obra e guarda no portão. O bloqueio é permanente, sem prazo.
3. É perceptível que vários **dracônicos estão assustados e em pânico**, todos indo na mesma direção. O jogador escolhe:
   - **Seguir o povo** — chega ao largo e percebe que é um **funeral**. A cena só menciona os NPCs que o jogador **já conheceu** (Draven, Célia, Gorak, Dona Maga, a velha). Se não conheceu ninguém, o texto diz que não reconhece ninguém. Depois o jogador decide **ficar** ou **voltar**:
     - **Ficar** — cena triste: Célia toca a testa da filha e precisa ser afastada, uma criança pergunta por que ela chora, o largo passa o dia chamando o nome da menina.
     - **Não ficar** — sai do largo e volta para a vila.
   - **Ficar para trás** — a rua esvazia em minutos e a vila fica sem ninguém.

**A vila deserta:** no dia do funeral, as lojas **continuam aparecendo no menu** mas estão vazias — é possível entrar, ver que não há ninguém e voltar. **Nenhuma compra funciona e é impossível falar com qualquer NPC.** A visita a uma loja fechada **cobra 1 período** normalmente, o que permite virar o dia dentro da própria vila. O `Olhar em volta` também muda de texto.

**No dia seguinte**, tudo volta a funcionar sozinho: as lojas reabrem, os NPCs voltam e o menu da vila perde os avisos de "vila vazia" e "mina fechada".

#### O Interior da Caverna

Ao entrar, a caverna é explorada em sequência:

1. **Escuridão.** Com a tocha na mão você vê o túnel inteiro: o chão, as paredes, a largura. Sem tocha, o escuro deixa de ser ausência de luz e vira um problema prático — você se orienta pelo tato, pelo som e pelo cheiro de mineral.
2. **Trilha de trem e cristais** (só visíveis com tocha): dois fios de metal meio afundados no cascalho, gastos no meio por muita passagem, correndo fundo para dentro da mina. E veios de cristal cravados nas paredes, alguns rachados como se alguém tivesse tentado arrancar um. Isso não é buraco de bicho: alguém cavou e trouxe um carrinho. Cada detalhe só aparece uma vez.
3. **Teste de Presença DT 10** — "sentir algo à frente":
   - **Passou:** você sente um peso no ar e um cheiro de ferrugem e suor frio. Alguém está adiante, e surge a opção de **seguir em frente** ou **sair da mina**.
   - **Falhou:** só barulho de água e nada mais. Não há como recuar aqui — só seguir.
4. **O goblin e a criança.** O túnel alarga e aparecem pequenas luzes verdes baixas, espalhadas pelo chão. Perto delas, agachado, um goblin **com medo**; ao lado, deitada, uma criança desmaiada de escamas quase brancas. O goblin te vê e repete sem parar: *"não foi culpa minha, eu não quis fazer isso, não não não"*. Três opções:
   - **Tentar acalmar (Presença DT 15).** **Passou:** ele conta que não controla, que só faz o que mandam — os dracônicos expulsaram ele e todos os irmãos da mina, depois mandaram voltar, e trouxeram a menina antes, obrigando-o a olhar. **Falhou:** ele se levanta de um pulo e não é para correr, é para gritar.
   - **Atacar.** O combate começa **antes** da transformação, contra um **Goblin comum** (nível 3, 15 de vida) — o mesmo que você vê fugindo da entrada da mina. Se ele morrer aqui, acabou: ele desaba sem crescer, as pequenas luzes se apagam e a criança continua no chão — ele não tinha encostado nela. Você segue direto para a decisão sobre a criança. Se ele **sobreviver**, ele se levanta gritando e aí sim se transforma.
   - **Sair correndo.** Ninguém persegue ninguém. A criança fica lá, o prazo de 3 dias continua rodando e o funeral acontece como se você nunca tivesse entrado.

**Passe ou falhe, a luta acontece.** Depois do teste ele grita que a culpa é dos dracônicos que mandaram ele e todos os irmãos embora, começa a **crescer** — os ombros estalam, os braços engrossam, e o goblin pequeno vira uma coisa grande e imponente no meio do túnel. No meio do crescimento ele pega um **porrete** do chão. Só sabe rugir e atacar o que está na frente.

**O goblin transformado** é nível **5**, com **90 de vida**, **+3 de iniciativa** sobre o goblin comum, ataque **Porretada de 1d8 + 5**, e não foge. Derruba **200 XP**, pode soltar o **Porrete** (50% de chance) e sempre deixa entre **12 e 26 de ouro**. Depois de vencer, o porrete que ele segurava **fica no chão e dá para pegar**, independentemente do drop.

**Se você vencer a luta, três escolhas:**

| Escolha | Resultado |
|---|---|
| **Levar a criança até a mãe** | Célia pega a filha, agradece, você recebe **100 de ouro**. Missão encerrada. |
| **Deixar a criança e buscar um guarda** | Os guardas descem e levam a criança até a mãe. **Sem recompensa.** Missão encerrada. |
| **Ir embora e deixar a criança lá** | O prazo de 3 dias continua e o funeral acontece depois, normalmente. |

#### O Fim da Missão no Quadro

Assim que o funeral acontece, a missão **some na hora** do menu de **missões em andamento** — ela é encerrada e não aparece mais entre as aceitas.

O aviso no **quadro de missões da taverna**, porém, **continua pregado por mais 2 dias** (o dia do funeral e os dois dias seguintes). Nesse período ele pode ser **lido normalmente**, exatamente como estava antes — local, objetivo e recompensa aparecem igual. A única diferença é que **não aparece a opção de aceitar**: o jogo só informa que a missão ainda está lá e que ninguém mexeu no papel.

Passados os 2 dias, o aviso **desaparece do quadro** de vez.

> **Nota de escopo:** o interior e o resgate existem, mas a trilha de trem e os cristais só aparecem se você tiver levado a tocha. Sem ela, o túnel continua sendo atravessado às cegas — o que é o ponto da penalidade de escuridão, mas significa que dá para chegar ao goblin sem ter visto nenhum dos dois detalhes.

</details>

<details>
<summary>⚠️ <b>AVISO DE SPOILER: a trilha da netinha (A Neta Perdida)</b> — clique para revelar</summary>

#### Como se aceita

A missão **não está no quadro de missões** — ela nunca aparece para nenhum jogador. Você só a recebe **cruzando com a velhinha no caminho para a caverna**, depois que já conhece a caverna (ou depois que a velhinha te para na estrada, com as duas missões):

- Se você **parar e escutar**, ela conta da netinha.
- Se você **ignorar e seguir**, ela desiste na hora e some — a missão é perdida para sempre.

Ao aceitar, a **barraca de frutas passa a aparecer no menu da vila**, e a floresta ganha a opção **"Procurar a netinha da velhinha na mata da entrada"**.

#### O prazo de 7 dias

A missão conta **7 dias inteiros** a partir do aceite, com a mesma mecânica da filha: **nenhum aviso**. Passados os 7 dias, quando você chega à cabana, a criança **já está morta** — a cabana está lá, o homem de roupas de médico está de costas sobre a mesa, e a netinha está no chão, num canto.

#### A trilha, passo a passo

1. **Pegadas** — frutas caídas rolando numa direção só, marcadas por pegadas de criança arrastando um pé: a netinha corria com a bolsa de frutas.
2. **O acampamento** — a trilha termina numa clareira com um **acampamento de bandidos**. Dá para tentar ver quantos são (**Teste de Presença**, 3 turnos de caminho até lá) ou advance direto. São **sete bandidos armados**: você pode **atacar de frente** ou **tentar pegá-los de surpresa** (Teste de Destreza DT 15).
3. **Vasculhar** — depois de vencer, o local mostra uma **gaiola arrombada por dentro**: a netinha foi mantida presa ali e fugiu correndo.
4. **A cabana** — as pegadas terminam numa clareira menor com uma **cabana de madeira e luz piscando**. Dá para observar de longe ou **espiar o interior** (Teste de Destreza DT 7). Lá dentro há um **grande homem de roupas de médico** trabalhando em algo sobre a mesa.
5. **Entrar** — **de frente** ou **furtivo** (Teste de Destreza DT 12, +3 de iniciativa se passar). A luz apaga e o que estava na mesa aparece: **carne humana** aberta, em pedaços. Ele faz magias com carne e plantas e usa pessoas como matéria-prima.

#### O Mago Macabro

O dono da cabana é o **Mago Macabro**: nível **5**, **80 de vida**, **14 de defesa**, **+4 de iniciativa**, **100 XP**, **não foge**.

| Ataque | Efeito |
|---|---|
| **Cutelo** (2d8 + Força) | 30% de chance de **sangrar**: 1d6 por rodada até o fim do combate |
| **Soco** (1d6) | Ataque simples |
| **Sede de Carne e Planta** | Cura a si mesmo com 3d4 — até **5 vezes** por combate |

Ele derruba o **Cutelo** (arma de **2d8 + Força**, 15% de chance) com 15% de chance. É a **arma mais pesada do jogo em valor de venda** entre as que você pode encontrar antes do chefe do labirinto.

#### O final

Se a criança está **viva**, ela se solta e te segue de volta até a vila. Na entrada, os **guardas** aparecem, ouvem sua história e mandam você **levar ela até a avó** antes de voltar — a cabana ainda tem o que explicar. Na barraca, a velhinha chora em silêncio e a menina se joga nos braços da avó. A velhinha então tira do avental uma **fruta escura, quase preta, que brilha de um jeito errado** e a dá para você, dizendo para usar só em momento de extrema urgência.

Se a criança está **morta**, você escolhe **levar o corpo até a vila** ou **deixá-lo na cabana**. Na vila, os guardas ouvem a história e saem correndo para a cabana. Você pode **contar para a velhinha** (ela chora em silêncio e promete enterrar a netinha junto às macieiras) ou **deixar os guardas resolverem**.

#### A Fruta do Diabo

| Item | Valor | Peso | Efeito |
|---|---|---|---|
| **Fruta do Diabo** | 500 (vendedor paga 350) | 0,2 | Consumível de **uso único**, só funciona dentro de combate. Restaura **vida e mana ao máximo**, deixa você **imune a todo dano na primeira rodada** e **sobe um dado de dano em todos os seus golpes** (1d4→1d6, 1d6→1d8, 1d8→1d10, 1d10→1d12). O que já é 1d12 não perde dado: ganha um **dado extra** (1d12 vira 1d12+1d4, 2d12 vira 2d12+1d6, e assim por diante). O poder dura **até o fim daquele combate** — depois se desfaz. |

É o item mais poderoso do jogo em termos de burst de dano, e o único que pode literalmente te salvar de um golpe fatal. Ninguém no jogo explica o que ela é.

</details>

---

## Construção

Cada construção fica **ancorada no ponto da mata onde foi montada** — na trilha da travessia (distância oculta até a borda). O menu Construção (sempre acessível) mostra, de cada construção, a **distância em períodos de caminhada** até o seu ponto:

- **"Montar X aqui"** constrói (ou reconstrói) a construção **no ponto onde você está agora**, pagando o custo normal — a nova passa a ser o ponto dela (a antiga fica para trás).
- **"Ir para X (N período(s))"** caminha de onde você está até a construção, turno a turno (mesma chance de encontro de caminhar).
- Montar duas construções **na mesma profundidade** deixa-as **junto** no mesmo ponto (usa as duas sem novo deslocamento). Em pontos diferentes, cada uma exige a real distância de caminhada.

### Cabana

- **Custo:** 7 Madeira + 10 Folha + 4 Pedra (2/3 de período)
- Permite **dormir à noite** (recupera **metade da vida e metade da mana**) e zera o cansaço — só estando no ponto dela. A **Capa do Viajante** acrescenta +4 de vida ao sono.
- É o pré-requisito para receber visitas (veja a seção de [Companheiros](#companheiros)).
- Sair da cabana para a floresta (explorar/recursos) ou **ir ao Labirinto** conta como **não estar mais nela**.

### Fogueira

- **Custo:** 4 Madeira + 3 Folha (2/3 de período)
- **Cozinhar** (só no ponto da fogueira): gasta **2 Madeiras** e converte **todas as carnes cruas** da mochila (Lobo e Urso) na versão **cozida** correspondente. A carne cozida não estraga — é a forma segura de comer carne de criatura selvagem sem risco de enjoo.

### Sala de Treino

- **Custo:** 10 Madeira + 15 Folha + 5 Pedra + 4 **Couro** (2/3 de período; o Couro vem de criaturas — veja spoiler na seção de Criaturas)
- **Treinar:** gasta o período restante e dá **+2 em Força ou Destreza por 2 períodos** (reativa a cada novo treino). O bônus vale para dano, acerto, iniciativa e testes.
- **Localização:** fica no ponto desta travessia onde você a montou. Se for o mesmo ponto da cabana ou da mesa, elas ficam **junto** (usa sem novo deslocamento); caso contrário, é preciso **caminhar até a sala** (a distância entre os pontos), não dá para usar de longe.

### Mesa de Magias

- **Custo:** 5 Madeira + 4 Folha + 4 Pedra + 1 **pó raro** (2/3 de período; esse pó vem de um encontro secreto — veja o spoiler na seção de Criaturas)
- **Estudar:** gasta o período restante e dá **+1 dado de dano em TODAS as habilidades de dano por 2 períodos** (magias, Estrondo, Giro e Explosão de Poder). O efeito vale os 2 períodos seguintes ao estudo (estudou de dia → vale na noite + no dia seguinte; estudou de noite → vale o dia + a noite seguinte). Não dá para estudar de novo enquanto o bônus estiver ativo.
- **Localização:** fica no ponto desta travessia onde você a montou. Se for o mesmo ponto da cabana ou da sala, elas ficam **junto** (usa sem novo deslocamento); caso contrário, é preciso **caminhar até a mesa** (a distância entre os pontos), não dá para usar de longe.

---

## Estruturas Encontradas

<details>
<summary>⚠️ <b>Spoiler: o Labirinto</b> — clique para revelar</summary>

- **Como encontrar:** apenas **explorando a floresta**. A chance começa em **1%** por exploração e aumenta **+1% a cada dia** que passa (até 100%). Uma vez encontrado, não é sorteado de novo.
- Ao descobrir a entrada, o jogador escolhe **entrar agora** ou **não entrar** — se não entrar, o local fica acessível pelo menu principal (como as construções), na opção **Labirinto**.
- Ir ao labirinto (pelo menu) conta como **ter saído** da cabana/sala/mesa em que se estava — ao voltar, o jogador está na floresta.
- Ao entrar, a interface vira sobre o labirinto (apenas **ver a ficha** continua disponível).
- **O caminho é randomizado na descoberta e salvo na ficha** — ao voltar depois, continua exatamente de onde parou (células visitadas permanecem iluminadas).
- **Navegação:** a tela mostra apenas a grade ao redor do personagem — as **laterais/paredes** onde você está (`##`) e os corredores vizinhos (`.`) — mais o que **já foi percorrido** (iluminado `·`) e sua posição (`@`). O resto do labirinto fica escuro, inclusive o centro (visível apenas enquanto se explora). Movimento via **W/A/S/D ou as setas do teclado** (↑ ↓ ← →) — executa no momento da tecla, **sem Enter**; qualquer outra tecla (número, caractere etc.) não faz nada. Uma câmera acompanha o personagem para que o labirinto (grade 31x31, bem maior) caiba na tela.
- Há **apenas uma entrada**. **Casas especiais:** em algumas casas aleatórias há **encontros** (8 por labirinto) e em outras há **recompensas** (6 por labirinto) — cada casa especial é sorteada na geração e ativa uma única vez. **Encontros:** 40% Esqueleto, 40% Zumbi e 20% Baú. **Recompensas:** ao achar, o jogo pergunta **"quer pegar?"** — escolher **sim** sorteia: **80%** ouro (7–19), **17%** uma arma sorteada entre as armas do vendedor e **3%** um dos **tesouros raros** (Olho Demoníaco, Espada Majestral e Coroa do Rei — cada um só cai **uma vez** por ficha; detalhes no spoiler abaixo) e mostra **"Você pegou X"**; escolher **não** ainda **consome a casa** e a recompensa é **perdida** — não dá para voltar para pegá-la. Ao perceber um monstro, dá para **Lutar** ou **Tentar Fugir** com um **Teste de Destreza** (a dificuldade é por criatura — veja o spoiler abaixo). Ao pisar na **primeira casa do centro**, o **Minotauro** enfrenta você — **a porta se fecha e não dá para fugir** do combate. **Vencendo o Minotauro**, o labirinto **desmorona**: o jogador foge correndo, volta à floresta e o labirinto se **fecha para sempre** — a opção **Labirinto** some do menu.

<details>
<summary>⚠️ <b>Spoiler: o Minotauro (chefe do centro)</b> — clique para revelar</summary>

Ao pisar na **primeira casa do centro**, uma silhueta colossal barra seu caminho e a porta se fecha:

- **Minotauro** (Nível 5): **150** de vida, **15** de defesa, **+5** de iniciativa e **+4** de bônus de ataque; ataques comuns de **Garras 2d6** e **Chifre 1d12**.
- **Investida (20% dos ataques):** o Minotauro baixa a cabeça e investe. Você rola um **Teste de Destreza** contra o **teste de ataque** dele (empate favorece você). **Passou:** ele bate de frente na parede e sofre **25** de dano. **Falhou:** ele te atinge em cheio e você sofre **2d10** de dano.
- **A porta se fecha:** não há como fugir — a opção de fuga fica bloqueada até a vitória (se o **Rei das Criaturas** ordenar que ele fuja, ele se retira e reaparece com a vida cheia numa próxima tentativa).
- Recompensas: **500 XP**, **Chifre de Minotauro** (50% de cair, 1–2, vale **150g** no total — o vendedor paga **105g** por unidade) e a **recompensa exclusiva da classe**:
  - **Guerreiro:** a **Espada do Minotauro** — **2d10 + Força**, com **30%** de chance de **atacar de novo** após cada golpe.
  - **Mago:** o **Cajado de Sangue** — **1d6 + Força** e **+1 dado de dano** em todas as suas magias (conta como Cajado para conjurar).
  - **Healer:** a habilidade passiva **Curandeiro Combatente** — ao atacar, cada **1 de mana** (máximo = seu nível) compra **1 ataque extra**, e **todo ataque que acerta cura metade do dano causado**.
- **Vitória:** o labirinto **desmorona e se fecha para sempre** (XP e drops são aplicados na morte do chefe). **Derrota:** você morre no coração do labirinto.

</details>

</details>

---

## Vendedor

O **vendedor ambulante** aparece em 10% das explorações. O estoque é sorteado a cada visita entre os **15 itens do catálogo geral** (5 itens por visita). O vendedor não vende armaduras nem itens exclusivos de classe — esses ficam com o **ferreiro**.

### Preços de compra (catálogo geral)

| Item | Preço | Item | Preço |
|---|---|---|---|
| Faca | 30 | Poção de Mana | 18 |
| Machado | 55 | Kit Médico | 25 |
| Machadinha | 30 | Madeira | 6 |
| Martelo | 80 | Folha | 4 |
| Porrete | 40 | Pedra | 5 |
| Mangual | 55 | Frutas | 5 |
| Arco | 65 | Flechas | 5 / un. |
| Lança | 80 | | |

### Regras de venda

- O vendedor ambulante compra **qualquer item** seu por **70% do valor original** — o preço da loja para os produtos dele e o valor cheio de raridade para drops e tesouros (materiais de saque viram 16g, 9g, 105g; um tesouro raro de 1000g vira **700g**).
- O **ferreiro** paga **80%** do valor cheio, mas só de **armas e armaduras**.
- A **Dona Maga** paga **80%** do valor cheio, mas só de **itens mágicos**.
- A **alfaiataria** paga **80%** do valor cheio do **Couro**.

### Onde ficam os itens exclusivos de classe

Espada, Espada Pesada, Machado de Guerra, Martelo de Guerra, Armadura Pesada (Guerreiro), Bisturi, Arco Refinado, Nunchako, Foice (Healer) e Cajado (Mago) são vendidos **apenas pelo ferreiro**, com restrição de classe.

---

## Companheiros

Depois que você **constrói a cabana**, a cada troca de período (dia/noite) há **20% de chance** de uma **pessoa perdida** aparecer pedindo abrigo. Você pode acolhê-la ou recusar. Se já tiver um companheiro, o visitante vai embora.

O companheiro é gerado com nome, classe aleatória (Mago/Guerreiro/Healer), nível 1 ou 2 e 6 pontos de atributos sorteados.

Enquanto estiver com você, o companheiro:

- Luta ao seu lado (iniciativa própria, agindo sozinho).
- Aparece no topo do menu da floresta (nome, classe e nível).
- **Conversar** permite saber mais sobre sua classe, habilidades, além de ver seus itens e status de convivência.
- **Curar** (via Kit Médico) restaura a vida dele.
- **Despedir-se** faz com que ele vá embora pacificamente.
- **Lutar contra:** A convivência na floresta não tem leis. Você pode escolher atacá-lo de surpresa; numa luta até a morte, se vencer, você ganha o XP dele e saqueia todos os itens e ouro que carregava.

### Lealdade, Necessidades e Avaliação
Seu companheiro avalia como é viajar com você. A **Afinidade** (lealdade) começa em 30% e dita se ele continuará ao seu lado ou irá embora. 

- **A Cada 2 a 4 dias**, ele avalia a jornada. Se a Afinidade estiver abaixo de 50%, ele vai embora. Se estiver entre 50% e 99%, ele renova sua estadia por mais 2 a 4 dias. Se chegar a 100%, ele se torna um **companheiro permanente** e não te deixa mais!
- **Necessidades:** Companheiros sentem fome e sono. Ao passar do tempo sem dormir ou comer, perdem afinidade. Você deve **alimentá-los** (usando as comidas do seu inventário no menu de conversa) e dormir na cabana (onde também recuperam vida/mana). Se ficarem muito tempo sem comer ou dormir, vão reclamar em voz alta no acampamento.

<details>
<summary>⚠️ <b>Spoiler: A verdadeira índole dos companheiros</b> — clique para revelar</summary>

Nem todos que pedem abrigo têm boas intenções. O jogo sorteia secretamente se o seu companheiro é do bem ou do mal:

- **Companheiros do mal:** fingem ganhar afinidade e se comportam como aliados, mas nunca se tornarão companheiros permanentes. O verdadeiro objetivo deles é ganhar sua confiança para te roubar.
- **Traição e Roubo:** Enquanto exploram, há chance de ele se virar contra você num ataque surpresa. Ao dormir, ele pode roubar furtivamente parte do seu ouro e fugir para a floresta durante a noite.
- **Pistas:** Fique atento. Um companheiro do mal às vezes vai dar dicas de suas intenções, como olhar fixamente para sua bolsa de moedas disfarçadamente durante o dia.

Se você sobreviver à traição e derrotá-lo em combate, ou o caçar após ser roubado, recupera o ouro roubado (e fica com os pertences dele).

</details>

---

## Itens

### Armas

| Arma | Dano | Tipo | Atributo | Preço | Observação |
|---|---|---|---|---|---|
| Soco | 1d3 | CaC | Força | — | Ataque desarmado universal |
| Espada | 1d8 | CaC | Força | 65 | Inicial do Guerreiro |
| Espada Pesada | 1d10 | CaC | Força | 110 | Ferreiro (Guerreiro) |
| Machado de Guerra | 1d12 | CaC | Força | 140 | Ferreiro (Guerreiro) |
| Martelo de Guerra | 1d12 | CaC | Força | 140 | Ferreiro (Guerreiro) |
| Machado | 1d6 | CaC | Força | 55 | Vendedor |
| Machadinha | 1d4 | CaC | Força | 30 | Vendedor |
| Martelo | 1d8 | CaC | Força | 80 | Vendedor |
| Porrete | 1d8 | CaC | Força | 40 | Vendedor |
| Mangual | 1d6 | CaC | Força | 55 | Vendedor |
| Lança | 1d6 | CaC | **Ágil** | 80 | Usa o maior entre Força/Destreza |
| Faca | 1d4 | CaC | Destreza | 30 | Drop dos bandidos |
| Nunchako | 1d6 | CaC | Destreza | 70 | Ferreiro (Healer) |
| Cajado | 1d4 | CaC/mágico | Força | 50 | Inicial do Mago; **obrigatório para lançar magias** |
| Bisturi | 1d4 → 3d8 | CaC | Ágil | 40 | Inicial do Healer; vira 3d8 com **Arma Mental** |
| Arco | 1d6 | à distância | Destreza | 65 | Consome Flechas |
| Arco Refinado | 1d8 | à distância | Destreza | 95 | Ferreiro (Healer); consome Flechas |
| Foice | 1d8 | à distância | Destreza | 85 | Ferreiro (Healer); consome Flechas |

Existem quatro armas que só aparecem bem depois do começo do jogo. Os nomes, a origem e as estatísticas delas estão no spoiler de itens raros, mais abaixo.

### Armaduras

| Armadura | Bônus | Preço |
|---|---|---|
| Armadura Leve | +3 Defesa | 230g |
| Armadura Pesada | +5 Defesa | 450g (Guerreiro) |

A melhor armadura é equipada automaticamente; as demais ficam na mochila sem efeito.

### Roupas

| Roupa | Bônus | Preço |
|---|---|---|
| Lenço de Seda | +1 Defesa | 60g |
| Botas de Correio | +1 em testes de Destreza | 70g |
| Capa do Viajante | +4 de vida ao dormir | 80g |
| Túnica de Aventureiro | +1 de dano CaC | 90g |
| Manto do Atirador | +1 de dano à distância | 90g |

### Consumíveis

| Item | Efeito | Use |
|---|---|---|
| Kit Médico | Cura 1d4 (em você ou no companheiro) e **acaba com uma infecção** | Combate (gasta o turno) ou inventário |
| Frutas | Cura 1d2 | Recurso coletado na floresta |
| Poção de Mana | +5 de mana | ✔ |
| Poção Grande de Mana | +7 de mana | Casa do Chapéu Mágico |
| Hidromel | +1d4 de mana | Taverna |
| Gota de Veneno | Envenena o próximo ataque com arma que acertar (1d4 por rodada) | Uma rodada inteira para aplicar |
| *Um consumível raro* | Existe um item que só aparece depois de uma missão específica | Só em combate — ver spoiler |

> Kit Médico: cada "uso" é uma unidade do item (o Healer começa com 5). Comidas da taverna e frutas da barraca também contam como comida e saciam a fome.

### Materiais e valor

| Material | De onde vem | Uso / valor |
|---|---|---|
| Couro | Saque de criaturas (ver spoiler na seção de Criaturas) | Construção da Sala de Treino (4x) · alfaiataria paga 5g |
| Presas e dentes | Saque de criaturas (ver spoiler na seção de Criaturas) | Vende por 9g |
| Carne de animal | Saque de criaturas | Cozinhe na **Fogueira** para não estragar (vende por 7g / 12g) |
| Pó raro de encontro secreto | Ver spoiler na seção de Criaturas | Ingrediente da Mesa de Magias · vende por 52g |
| Madeira / Folha / Pedra | Recursos da floresta | Construção |

<details>
<summary>⚠️ <b>Spoiler: os itens raros e onde eles aparecem</b> — clique para revelar</summary>

| Material | De onde vem | Uso / valor |
|---|---|---|
| Osso | Labirinto (Esqueleto) | Vende por 16g (70% de 23) |
| Carne Podre | Labirinto (Zumbi) | Vende por 10g (70% de 15) |
| Fruta do Diabo | Recompensa da velhinha (ver spoiler da netinha) | Burst de combate · vende por 350g |
| Cutelo | Mago Macabro (ver spoiler da netinha) | Arma 2d8 com sangramento · vende por 182g |
| Coroa do Rei | Labirinto (recompensa rara) | Desbloqueia o **Rei das Criaturas** · vende por 700g |
| Chifre de Minotauro | Labirinto (chefe Minotauro) | Vende por 105g (70% de 150) |
| Espada do Minotauro | Labirinto (chefe, Guerreiro) | Arma 2d10, 30% de atacar de novo · venda 875g |
| Cajado de Sangue | Labirinto (chefe, Mago) | Arma 1d6, +1d de magia · venda 1050g |
| Espada Majestral | Labirinto (recompensa rara) | Arma 1d12 + 1d4 de luz, dobro contra mortos-vivos · venda 490g |
| Olho Demoníaco | Labirinto (recompensa rara) | Abre o **Pacto Mortal** · não é um item de mochila |

</details>

---

## Salvar e Carregar

- Existem **3 slots** de save (`saves/save1.dat` a `save3.dat`), gravados com serialização de objeto Java (`ObjectOutputStream`).
- O menu mostra nome, classe, nível, dificuldade, período atual e a data do último save.
- **Não há autosave** — salve com frequência.
- **Apagar Save** pede confirmação antes de apagar.

---

## Estrutura do Projeto

```
├── pom.xml                        # Build Maven, dependências e testes
├── src/main/java/
│   ├── Main.java                  # Loop principal, navegação e fluxo entre menus
│   ├── classes/                   # ClasseRpg, Mago, Guerreiro, Healer (Strategy)
│   ├── fichas/                    # FichaRpg, gerenciadores de inventário/construções/vida
│   ├── racas/                     # Raças e suas passivas
│   ├── criaturas/                 # Criatura e CriaturaFactory (monstros e drops)
│   ├── eventos/                   # Floresta, Travessia, Vilarejo, Taverna, Ferreiro,
│   │                              #   Alfaiataria, BarracaDeFrutas, CasaDoChapeuMagico,
│   │                              #   Caverna, funeral, trilha da neta, acampamento
│   ├── estruturas/                # Labirinto e a disputa com o chefe dele
│   ├── missoes/                   # QuadroDeMissoes (avisos, aceite, novidades)
│   ├── mecanicas/                 # MotorDeCombate, Gerenciadores de Turnos/Ataque/
│   │                              #   Habilidades/Itens/Evolucao/Companheiro, MecanicasRpg
│   ├── habilidades/               # Habilidade, Magia e habilidades ativas (Command)
│   ├── comandos/                  # Padrão Command das ações de combate
│   ├── companheiros/              # Companheiro (NPC que te acompanha)
│   ├── itens/                     # ItemRpg, Arma, Armadura, Consumivel (+ pesos)
│   ├── loja/                      # Vendedor (catálogo, preços, criação de itens)
│   ├── narrativa/                 # Aventura (prólogo)
│   ├── salvamento/                # GerenciadorSaves (3 slots)
│   ├── telas/                     # Interface, MenuCriacaoPersonagem, MenuSalvamento,
│   │                              #   MenuVisualizacao, Teclado (WASD)
│   └── dev/                       # ModificarFicha
└── src/test/java/                 # Suíte de testes automatizados (JUnit 5 + Mockito)
```

---

## Testes

A suíte cobre regras matemáticas, inventário, combate, evolução, creaturas, missões e os eventos de história.

```bash
mvn clean test
```

**264 testes** passam com 0 falhas e 0 erros.

---

## Conceitos Aplicados

- RPG de decisões: atributos, modificadores de classe, testes de atributo, defesa por Destreza/armadura, iniciativa, crítico e gerenciamento de recursos (vida, mana, ouro, itens, fadiga).
- Java OO com pacotes, herança, polimorfismo e serialização de saves.
- Padrões de projeto: **Command** (ações de combate) e **Strategy** (classes de personagem).
- Suíte de testes automatizados (JUnit 5 + Mockito) cobrindo regras matemáticas, inventário e combate.

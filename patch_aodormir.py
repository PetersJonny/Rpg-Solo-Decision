with open('src/main/java/companheiros/Companheiro.java', 'r', encoding='utf-8') as f:
    content = f.read()

target = """    public void aoDormir() {
        ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + ficha.getVidaMaxima() / 3, ficha.getVidaMaxima()));
        ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + ficha.getManaMaxima() / 3, ficha.getManaMaxima()));

        if (!dormiuPrimeiraVez) {
            dormiuPrimeiraVez = true;
            diasRestantes = MecanicasRpg.rolarEntre(1, 3);
            partindo = false;
            return;
        }
        diasRestantes--;
        if (diasRestantes <= 0) {
            partindo = true;
        }
    }"""

replacement = """    public void aoDormir() {
        diasSemDormir = 0;
        alterarAfinidade(10); // Dormir na cabana aumenta afinidade

        ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + ficha.getVidaMaxima() / 3, ficha.getVidaMaxima()));
        ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + ficha.getManaMaxima() / 3, ficha.getManaMaxima()));

        if (permanente) return;

        if (!dormiuPrimeiraVez) {
            dormiuPrimeiraVez = true;
            diasRestantes = MecanicasRpg.rolarEntre(2, 4);
            partindo = false;
            return;
        }
        
        diasRestantes--;
        
        if (afinidade >= 100 && !doMal) {
            permanente = true;
            telas.Interface.MostrarMensagem("\\n( " + nome + " sente que a cabana é seu verdadeiro lar agora. Ele(a) decidiu ficar com você permanentemente! )");
            telas.Interface.Pausa(1500);
            return;
        }
        
        if (diasRestantes <= 0) {
            if (afinidade < 50) {
                partindo = true; // Vai embora
            } else {
                diasRestantes = MecanicasRpg.rolarEntre(2, 4); // Renova a estadia
            }
        }
    }"""

content = content.replace(target, replacement)

with open('src/main/java/companheiros/Companheiro.java', 'w', encoding='utf-8') as f:
    f.write(content)

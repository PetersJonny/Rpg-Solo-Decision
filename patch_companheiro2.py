import re

with open('src/main/java/companheiros/Companheiro.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add new properties
new_props = """    private boolean dormiuPrimeiraVez = false;
    private int diasRestantes = 0;
    private boolean partindo = false;
    private final boolean doMal;
    private int ouroRoubado = 0;
    
    private int afinidade = 30; // Starts at 30
    private boolean permanente = false;
    private int diasSemComer = 0;
    private int diasSemDormir = 0;"""

content = re.sub(r'    private boolean dormiuPrimeiraVez.*?(?=\n\n|\n    public Companheiro\(\))', new_props, content, flags=re.DOTALL)

# Update aoDormir()
old_aoDormir = """    public void aoDormir() {
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

new_aoDormir = """    public void aoDormir() {
        ficha.setVidaPersonagem(Math.min(ficha.getVidaPersonagem() + ficha.getVidaMaxima() / 3, ficha.getVidaMaxima()));
        ficha.setManaPersonagem(Math.min(ficha.getManaPersonagem() + ficha.getManaMaxima() / 3, ficha.getManaMaxima()));
        
        diasSemDormir = 0; // Dormiu e zerou cansaço
        alterarAfinidade(5); // Dormir seguro aumenta um pouco a afinidade

        if (!dormiuPrimeiraVez) {
            dormiuPrimeiraVez = true;
            diasRestantes = MecanicasRpg.rolarEntre(2, 4); // Avaliação entre 2 a 4 dias
            partindo = false;
            return;
        }
        
        if (permanente) return; // Se já é permanente, não tem mais contador

        diasRestantes--;
        if (diasRestantes <= 0) {
            if (doMal) {
                // Companheiro do mal finge avaliar, e só renova o tempo pra continuar sugando/tentando roubar
                diasRestantes = MecanicasRpg.rolarEntre(2, 4);
                return;
            }
            
            if (afinidade >= 100) {
                permanente = true;
            } else if (afinidade < 50) {
                partindo = true;
            } else {
                // Fica mais um tempo, ainda está em avaliação
                diasRestantes = MecanicasRpg.rolarEntre(2, 4);
            }
        }
    }
    
    public void alterarAfinidade(int valor) {
        if (!doMal && !permanente) {
            this.afinidade += valor;
            if (this.afinidade > 100) this.afinidade = 100;
            if (this.afinidade < 0) this.afinidade = 0;
        } else if (doMal) {
            // Do mal finge que a afinidade muda pra o jogador não desconfiar pelos números
            this.afinidade += valor;
            if (this.afinidade > 99) this.afinidade = 99; // Nunca 100
            if (this.afinidade < 0) this.afinidade = 0;
        }
    }
    
    public void passarTempo() {
        diasSemComer++;
        diasSemDormir++;
        if (diasSemComer >= 3) {
            alterarAfinidade(-10); // Fome perde muita afinidade
        }
        if (diasSemDormir >= 3) {
            alterarAfinidade(-5); // Sono também perde
        }
    }
    
    public void alimentar() {
        this.diasSemComer = 0;
        alterarAfinidade(15);
    }
    
    public int getDiasSemComer() { return diasSemComer; }
    public int getDiasSemDormir() { return diasSemDormir; }
    
    public int getAfinidade() { return afinidade; }
    public boolean isPermanente() { return permanente; }"""

content = content.replace(old_aoDormir, new_aoDormir)

# Update mostrarResumo()
old_resumo = """        if (dormiuPrimeiraVez) {
            Interface.MostrarMensagem("  Pretende ficar mais " + diasRestantes + " dia(s) contigo.");
        } else {
            Interface.MostrarMensagem("  Ainda não dormiu na cabana; vai decidir o futuro depois da primeira noite.");
        }"""
        
new_resumo = """        if (permanente) {
            Interface.MostrarMensagem("  Confia em você com a própria vida. É seu parceiro permanente.");
        } else if (dormiuPrimeiraVez) {
            Interface.MostrarMensagem("  Está te avaliando. Faltam " + diasRestantes + " dia(s) para decidir o futuro.");
            String humor = (afinidade >= 80) ? "Muito amigável" : (afinidade >= 50 ? "Neutro" : "Desconfiado");
            Interface.MostrarMensagem("  Status de Afinidade: " + afinidade + "% (" + humor + ")");
        } else {
            Interface.MostrarMensagem("  Ainda não dormiu na cabana; vai decidir o futuro depois da primeira noite.");
        }
        
        if (diasSemComer > 0) Interface.MostrarMensagem("  Fome: " + diasSemComer + " dias sem comer.");
        if (diasSemDormir > 0) Interface.MostrarMensagem("  Cansaço: " + diasSemDormir + " dias sem dormir na cabana.");"""
content = content.replace(old_resumo, new_resumo)

with open('src/main/java/companheiros/Companheiro.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("Companheiro.java patched")

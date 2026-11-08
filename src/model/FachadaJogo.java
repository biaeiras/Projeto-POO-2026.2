package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FachadaJogo implements ObservadoJogo {

    // Singleton
    private static FachadaJogo instancia = null;
    
 // Impede a criação de outras instâncias
    private FachadaJogo() {
    }

    // Único ponto de acesso à fachada
    public static FachadaJogo getInstancia() {
        if (instancia == null) {
        	instancia = new FachadaJogo();
        }
    	return instancia;
    }

    // Partida gerenciada pela fachada
    private Partida partida;

    
    // Inicia uma partida com as cores escolhidas
    public void iniciarPartida(ArrayList<Cor> coresEscolhidas) {
        definirPartida(new Partida(coresEscolhidas));
    }
    
    public boolean escolherConselheiroJogadorAtual(int indice) {
        verificarPartidaIniciada();

        boolean escolheu =
            partida.escolherConselheiroJogadorAtual(indice);

        if (escolheu) {
            notificarObservadores();
        }

        return escolheu;
    }

    public void avancarJogadorNaPreparacao() {
        verificarPartidaIniciada();

        if (partida.isEncerrada() || partida.getTurnoAtual() != null) {
            throw new IllegalStateException(
                "Não é possível avançar na preparação neste momento."
            );
        }

        partida.avancarJogador();
        notificarObservadores();
    }

    // Associa a partida criada à fachada
    void definirPartida(Partida partida) {

        if (partida == null) {
            throw new IllegalArgumentException(
                "A partida não pode ser nula."
            );
        }

        this.partida = partida;
        notificarObservadores();
    }

    // Consulta se existe uma partida
    public boolean existePartida() {
        return partida != null;
    }
    
    private void verificarPartidaIniciada() {
        if (partida == null) {
            throw new IllegalStateException(
                "Nenhuma partida foi iniciada."
            );
        }
    }
    
    private final ArrayList<ObservadorJogo> observadores = new ArrayList<>();

	@Override
	public void adicionarObservador(ObservadorJogo observador) {
	    if (observador == null) {
	        throw new IllegalArgumentException(
	            "O observador deve ser informado."
	        );
	    }

	    if (!observadores.contains(observador)) {
	        observadores.add(observador);
	    }
	}

	@Override
	public void removerObservador(ObservadorJogo observador) {
	    observadores.remove(observador);
	}

	private void notificarObservadores() {
	    ArrayList<ObservadorJogo> copia =
	        new ArrayList<>(observadores);

	    for (ObservadorJogo observador : copia) {
	        observador.atualizar(this);
	    }
	}

    // Consulta as melhorias de uma coluna do jogador atual
    public ArrayList<String> getMelhoriasTabuleiroJogador(
            int coluna
    ) {

        if (partida == null) {
            throw new IllegalStateException(
                "Nenhuma partida foi iniciada."
            );
        }

        if (coluna < 0 || coluna > 3) {
            throw new IllegalArgumentException(
                "Coluna inválida."
            );
        }

        Jogador jogador = partida.getJogadorAtual();

        TabuleiroJogador tabuleiro =
                jogador.getTabuleiroJogador();

        ArrayList<Melhoria> melhorias;

        switch (coluna) {

            case 0:
                melhorias = tabuleiro.getMelhorias(
                        Regiao.CHINA
                );
                break;

            case 1:
                melhorias = tabuleiro.getMelhorias(
                        Regiao.PERSIA
                );
                break;

            case 2:
                melhorias = tabuleiro.getMelhorias(
                        Regiao.RUSSIA
                );
                break;

            case 3:
                melhorias =
                        tabuleiro.getMelhoriasColunaBranca();
                break;

            default:
                throw new IllegalArgumentException(
                    "Coluna inválida."
                );
        }

        ArrayList<String> nomesImagens = new ArrayList<>();

        for (Melhoria melhoria : melhorias) {

            String nomeArquivo =
                    melhoria.getTipo().name().toLowerCase()
                    + "_"
                    + melhoria.getRegiao().name().toLowerCase()
                    + ".jpg";

            nomesImagens.add(nomeArquivo);
        }

        return nomesImagens;
    }

    public void moverPeao(ParadaID id) {
        verificarPartidaIniciada();
        if (partida.moverPeao(id)) {
            notificarObservadores();
        }
	}
	
	public void pegarTributo(ProvinciaID id) {
        verificarPartidaIniciada();
        if (partida.pegarTributo(id)) {
            notificarObservadores();
        }
	}
	
	public HashMap<ProvinciaID, Integer> getCargasProvincias() {
		return partida == null ? new HashMap<ProvinciaID, Integer>() : partida.getCargasProvincias();
	}
	
	public void construirYurt(ParadaID id) {
        verificarPartidaIniciada();
        if (partida.construirYurt(id)) {
            notificarObservadores();
        }
	}
	
	public void avancarTurno() {
		terminarTurnoJogadorAtual();
	}
	
	public HashMap<ParadaID, ArrayList<Cor>> getYurts(){
        HashMap<ParadaID, ArrayList<Cor>> copia = new HashMap<>();
        if (partida != null) {
            for (java.util.Map.Entry<ParadaID, ArrayList<Cor>> entrada : partida.getYurts().entrySet()) {
                copia.put(entrada.getKey(), new ArrayList<>(entrada.getValue()));
            }
        }
        return copia;
	}
	
	public HashMap<ParadaID, ArrayList<Cor>> getPosicoesPeoes(){
		HashMap<ParadaID, ArrayList<Cor>> ret = new HashMap<ParadaID, ArrayList<Cor>>();
        if (partida == null) {
            return ret;
        }
		for(Jogador jogador: partida.getJogadores()) {
			Cor cor = jogador.getTabuleiroJogador().getPersonagem().getCor();
			for(int i = 0; i < jogador.getQuantidadePeoes(); ++i) {
				ParadaID id = jogador.getPeao(i).getPosicao().getId();
				if(!ret.containsKey(id)) ret.put(id, new ArrayList<Cor>());
				ret.get(id).add(cor);
			}
		}
		return ret;
	}
	
	public HashSet<ParadaID> getAlcancaveisTotais(){
        if (!isTurnoEmAndamento() || isPartidaEncerrada()) {
            return new HashSet<>();
        }
		return partida.getAlcancaveisTotais();
	}
	
	public HashSet<ParadaID> getVizinhosPossiveis(){
        if (!isTurnoEmAndamento() || isPartidaEncerrada()) {
            return new HashSet<>();
        }
		return partida.getVizinhosPossiveis();
	}
	
	public HashSet<ProvinciaID> getProvincias(){
        if (!isTurnoEmAndamento() || isPartidaEncerrada()) {
            return new HashSet<>();
        }
		return partida.getProvincias();
	}
	
	public boolean podeEscolherColuna() {
	    return partida != null
	        && !partida.isEncerrada()
	        && partida.todosEscolheramConselheiro()
	        && partida.getTurnoAtual() == null;
	}

	public boolean isTurnoEmAndamento() {
	    return partida != null && partida.getTurnoAtual() != null;
	}

	public boolean isPartidaEncerrada() {
	    return partida != null && partida.isEncerrada();
	}

	public String getNomePersonagemAtual() {
	    verificarPartidaIniciada();

	    return partida.getJogadorAtual()
	                  .getTabuleiroJogador()
	                  .getPersonagem()
	                  .getNome();
	}

	public int getFichasAtivacaoJogadorAtual() {
	    verificarPartidaIniciada();

	    return partida.getJogadorAtual()
	                  .getTabuleiroJogador()
	                  .getFichasAtivacaoDisponiveis();
	}

	public boolean isColunaJogadorAtualAtivada(int coluna) {
	    verificarPartidaIniciada();

	    TabuleiroJogador tabuleiro =
	        partida.getJogadorAtual().getTabuleiroJogador();

	    switch (coluna) {
	        case 0:
	            return tabuleiro.colunaEstaAtivada(Regiao.CHINA);
	        case 1:
	            return tabuleiro.colunaEstaAtivada(Regiao.PERSIA);
	        case 2:
	            return tabuleiro.colunaEstaAtivada(Regiao.RUSSIA);
	        case 3:
	            return tabuleiro.colunaBrancaEstaAtivada();
	        default:
	            throw new IllegalArgumentException("Coluna inválida.");
	    }
	}

	public boolean ativarColunaJogadorAtual(int coluna) {
	    verificarPartidaIniciada();

	    boolean ativou;

	    switch (coluna) {
	        case 0:
	            ativou = partida.iniciarTurno(Regiao.CHINA);
	            break;
	        case 1:
	            ativou = partida.iniciarTurno(Regiao.PERSIA);
	            break;
	        case 2:
	            ativou = partida.iniciarTurno(Regiao.RUSSIA);
	            break;
	        case 3:
	            ativou = partida.iniciarTurnoColunaBranca();
	            break;
	        default:
	            throw new IllegalArgumentException("Coluna inválida.");
	    }

	    if (ativou) {
	        notificarObservadores();
	    }

	    return ativou;
	}

	public boolean terminarTurnoJogadorAtual() {
	    verificarPartidaIniciada();

	    boolean terminou = partida.TerminarTurno();

	    if (terminou) {
	        notificarObservadores();
	    }

	    return terminou;
	}

	public int getMoedasJogadorAtual() {
	    verificarPartidaIniciada();
	    return partida.getJogadorAtual()
	                  .getQuantidadeTributo(TipoTributo.MOEDA);
	}

	public int getEspadasJogadorAtual() {
	    verificarPartidaIniciada();
	    return partida.getJogadorAtual()
	                  .getQuantidadeTributo(TipoTributo.ESPADA);
	}

	public int getYurtsEstoqueJogadorAtual() {
	    verificarPartidaIniciada();
	    return partida.getJogadorAtual().getYurtsDisponiveis();
	}

	public int getTributosYurtJogadorAtual() {
	    verificarPartidaIniciada();
	    return partida.getJogadorAtual()
	                  .getQuantidadeTributo(TipoTributo.YURT);
	}
	
	public boolean todosEscolheramConselheiro() {
	    verificarPartidaIniciada();
	    return partida.todosEscolheramConselheiro();
	}

    @Override
    public String getNomeConselheiroSecretoJogadorAtual() {
        verificarPartidaIniciada();
        ConselheiroSecreto conselheiro = partida.getJogadorAtual().getConselheiroSecreto();
        return conselheiro == null ? null : conselheiro.getNome();
    }

	public ArrayList<String> getOpcoesConselheiroJogadorAtual() {
	    verificarPartidaIniciada();

	    ArrayList<String> nomes = new ArrayList<>();
	    Jogador jogador = partida.getJogadorAtual();

	    if (!jogador.jaEscolheuConselheiro()) {
	        nomes.add(jogador.getOpcaoConselheiro(0).getNome());
	        nomes.add(jogador.getOpcaoConselheiro(1).getNome());
	    }

	    return nomes;
	}
	
	public Cor getCorJogadorAtual() {
	    verificarPartidaIniciada();

	    return partida.getJogadorAtual()
	                  .getTabuleiroJogador()
	                  .getPersonagem()
	                  .getCor();
	}

    @Override
    public ArrayList<String> getConselheirosAbertos() {
        ArrayList<String> nomes = new ArrayList<>();
        if (partida == null) {
            for (int i = 0; i < 4; i++) {
                nomes.add(null);
            }
            return nomes;
        }
        for (ConselheiroAberto carta : partida.getConselheirosAbertos()) {
            // Conserva a posição vazia, sem revelar uma carta substituta.
            nomes.add(carta.isCumprida() ? null : carta.getNome());
        }
        return nomes;
    }

    @Override
    public HashMap<String, Integer> getPlacarVotos() {
        HashMap<String, Integer> placar = new HashMap<>();
        if (partida != null) {
            for (Jogador jogador : partida.getJogadores()) {
                String nome = jogador.getTabuleiroJogador().getPersonagem().getNome();
                placar.put(nome, jogador.getVotos());
            }
        }
        return placar;
    }

    @Override
    public int getVotosFichaNeutra() {
        return partida == null ? 0 : partida.getVotosFichaNeutra();
    }
    
    @Override
    public boolean isFimDisparado() {
        return partida != null && partida.isFimDisparado();
    }
    
    @Override
    public ArrayList<String> getNomesVencedores() {

        ArrayList<String> nomes = new ArrayList<>();

        if (partida == null || !partida.isEncerrada()) {
            return nomes;
        }

        for (Jogador jogador : partida.calcularVencedores()) {

            String nome = jogador.getTabuleiroJogador()
                                 .getPersonagem()
                                 .getNome();

            nomes.add(nome);
        }

        return nomes;
    }

}

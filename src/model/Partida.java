package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Partida  {
	private ArrayList<Jogador> jogadores;
	private TabuleiroPrincipal tabuleiroPrincipal;
	
	private Turno turnoAtual;
	private int indiceJogadorAtual;
	
	private int votosFichaNeutra;
	private int limiteVotos;
	
	private boolean encerrada;
	private boolean fimDisparado;
	private int rodadasExtras;
	private int indiceJogadorInicialFim;
	private int indicePrimeiroJogador = 0;
	private int turnosRestantesAposFim;
	
	private final HashMap<Peao, HashSet<Parada>> paradasPercorridas = new HashMap<>();
	private final HashMap<Cidade, Integer> tesourosTomadosNoTurno = new HashMap<>();
	private Peao peaoAtivo; 
	private boolean acaoRestritaFeita; 
	private int melhoriasGratisPendentes; 

	
	Partida(ArrayList<Cor> coresEscolhidas) {
		//O jogo é de 2 a 5 jogadores 
	    if (coresEscolhidas == null || coresEscolhidas.size() < 2 || coresEscolhidas.size() > 5) {
	        throw new IllegalArgumentException( "A partida deve ter de 2 a 5 jogadores." );
	    }

	    for (int i = 0; i < coresEscolhidas.size(); i++) {
	        if (coresEscolhidas.get(i) == null || coresEscolhidas.indexOf(coresEscolhidas.get(i)) != i) {
	            throw new IllegalArgumentException("As cores devem ser válidas e diferentes.");
	        }
	    }
	    
	    this.jogadores = new ArrayList<>();
	    this.indiceJogadorAtual = 0;
	    this.votosFichaNeutra = 0;
	    this.limiteVotos = obterLimiteVotos(coresEscolhidas.size());
	    this.encerrada = false;
	    this.fimDisparado = false;
	    this.rodadasExtras = 0;
	    criarJogadores(coresEscolhidas);
	    distribuirMoedasInicias();
	    criarTabuleiroPrincipal(); 
	    
	    
	}
	
	
	//limite  quantos votos a ficha neutra precisa alcançar para disparar o fim da partida
	private int obterLimiteVotos(int quantidadeJogadores) {
	    switch (quantidadeJogadores) {
	        case 2:
	        	return 10;
	        case 3: 
	        	return 14;
	        case 4: 
	        	return 16;
	        case 5: 
	        	return 17;
	        default:
	            throw new IllegalArgumentException( "Quantidade de jogadores inválida.");
	    }
	}


	private ArrayList<ConselheiroSecreto> sortearConselheiros( ArrayList<TipoConselheiroSecreto> baralho) {

	    ArrayList<ConselheiroSecreto> opcoes = new ArrayList<>();
		
	    opcoes.add(new ConselheiroSecreto(baralho.remove(0)));
	    opcoes.add(new ConselheiroSecreto(baralho.remove(0)));
	    return opcoes;
	}
	
	
	//Personagem associado com a cor escolhida 
	
	private Personagem criarPersonagem(Cor cor) {
		switch(cor) {
			case VERDE:
		        return new Altani();
		    case VERMELHO:
		        return new Chagatai();
		    case CINZA:
		        return new Jochi();
		    case AZUL:
		        return new Ogedei();
		    case AMARELO:
		        return new Tolui();
		    default:
		        throw new IllegalArgumentException("Cor inválida");
		}	
	}
	
	private void criarJogadores(ArrayList<Cor> coresEscolhidas) {
		
		 ArrayList<TipoConselheiroSecreto> baralho = new ArrayList<>();

	    for (TipoConselheiroSecreto tipo : TipoConselheiroSecreto.values()) {
	        baralho.add(tipo);
	    }

	    java.util.Collections.shuffle(baralho);
	    
	    
		for (Cor cor : coresEscolhidas) {

	        Personagem personagem = criarPersonagem(cor);

	        TabuleiroJogador tabuleiroJogador =new TabuleiroJogador(personagem);
	       
	        ArrayList<Peao> peoes = new ArrayList<>();

	        int quantidadePeoes = 1;

	        if (personagem instanceof Altani) {
	            quantidadePeoes = 2;
	        }

	        for (int i = 0; i < quantidadePeoes; i++) {
	            peoes.add(new Peao());
	        }

	        ArrayList<ConselheiroSecreto> conselheiros = sortearConselheiros(baralho);

	        
	        Jogador jogador = new Jogador(
	                peoes,
	                conselheiros,
	                tabuleiroJogador
	        );

	        
	        for (Peao peao : peoes) {
	            peao.setJogador(jogador);
	        }

	  
	        jogadores.add(jogador);
	    }
		
		
	}
	
	private  void distribuirMoedasInicias() {
	    for (int i = 0; i < jogadores.size(); i++) {
	        int moedasIniciais = (i < 2) ? 1 : 2;

	        jogadores.get(i).adicionarTributo(TipoTributo.MOEDA,moedasIniciais);
	    }
	}
	
	private void criarTabuleiroPrincipal() {

	    HashMap<Peao, Jogador> mapaPeoes = new HashMap<>();

	    for (Jogador jogador : jogadores) {
	        for (int i = 0; i < jogador.getQuantidadePeoes(); i++) {
	            Peao peao = jogador.getPeao(i);
	            mapaPeoes.put(peao, jogador);
	        }
	    }

	    tabuleiroPrincipal = new TabuleiroPrincipal(mapaPeoes);
	}
	
	Jogador getJogadorAtual() {
		if(jogadores.isEmpty()) {
			return null; 
		}
		
		return jogadores.get(indiceJogadorAtual); 
	}
	
	void avancarJogador() {
		if (jogadores.isEmpty() || encerrada) {
	        return;
	    }

	    indiceJogadorAtual =(indiceJogadorAtual + 1) % jogadores.size();
		
	}
	
	
	//TURNO 
	
	
	boolean escolherConselheiroJOgadorAtual(int indice) {
		if(encerrada || jogadores.isEmpty() || turnoAtual != null) {
			return false; 
		}
		Jogador jogador = getJogadorAtual();

	    return jogador.escolherConselheiro(indice);
		
	}
	
	
	
	 boolean todosEscolheramConselheiro() {
	    for (Jogador jogador : jogadores) {
	        if (!jogador.jaEscolheuConselheiro()) {
	            return false;
	        }
	    }

	    return true;
	}
	 

	
	boolean iniciarTurno(Regiao regiao) {
		if(encerrada || jogadores.isEmpty() || turnoAtual!= null || !todosEscolheramConselheiro() || regiao == null) {
			return false; 
		}
		
		Jogador jogador = getJogadorAtual();
		
		HashMap<Enum<?>, Integer> resultado = jogador.getTabuleiroJogador().ativarColuna(regiao);

	    return abrirTurno(jogador, resultado);
		
		
		
	}
	
	boolean iniciarTurnoColunaBranca() {
		if(encerrada || jogadores.isEmpty() || turnoAtual!= null || !todosEscolheramConselheiro()) {
			return false; 
		}
		
		Jogador jogador = getJogadorAtual();
		
		HashMap<Enum<?>, Integer> resultado = jogador.getTabuleiroJogador().ativarColunaBranca();

	    return abrirTurno(jogador, resultado);
		
		
	}


	private boolean abrirTurno(Jogador jogador, HashMap<Enum<?>, Integer> resultadoColuna) {
	
	    if (resultadoColuna == null) {
	        return false;
	    }
	
	    turnoAtual = new Turno(jogador, resultadoColuna);
	
	    paradasPercorridas.clear();
	    tesourosTomadosNoTurno.clear();
	    acaoRestritaFeita = false;
	    melhoriasGratisPendentes = 0;
	    peaoAtivo = jogador.getPeao(0);
	
	    for (int i = 0; i < jogador.getQuantidadePeoes(); i++) {
	        Peao peao = jogador.getPeao(i);
	        HashSet<Parada> paradas = new HashSet<>();
	        Parada posicao = peao.getPosicao();
	
	        if (posicao != null) {
	            paradas.add(posicao);
	            turnoAtual.adicionarProvinciasVisitadas(new HashSet<Provincia>(posicao.getProvincias().values()));
	        }
	
	        paradasPercorridas.put(peao, paradas);
	    }
	
	    return true;
	}

	
	
	
	
	boolean TerminarTurno() {
	    if (turnoAtual == null) {
	    	return false;
	    	
	    }
	    	
	    if (turnoAtual.isKhanPendente()) {
	    	return false;
	    }
	    
	    VerificaFimDisparado(); 

	    turnoAtual = null;
	    avancarJogador();

	    if (fimDisparado) {
	        if (rodadasExtras == 0 && indiceJogadorAtual == indicePrimeiroJogador) {

	            rodadasExtras = 1;
	            turnosRestantesAposFim = jogadores.size();

	        } else if (rodadasExtras == 1) {
	            turnosRestantesAposFim--;

	            if (turnosRestantesAposFim <= 0) {
	                encerrada = true;
	            }
	        }
	    }

	    return true;
	}
	
	boolean concluirAcaoKhan(ProvinciaKhanID provincia, TipoTributo tributo1, TipoTributo tributo2) {

	    if (encerrada || turnoAtual == null) {
	        return false;
	    }

	    if (!turnoAtual.isKhanPendente()) {
	        return false;
	    }

	    if (provincia == null || tributo1 == null || tributo2 == null) {
	        return false;
	    }
	    
	    if (provincia == tabuleiroPrincipal.getKhan()) {
	        return false;
	    }

	    tabuleiroPrincipal.ativarProvinciaKhan(provincia, tributo1, tributo2);

	    turnoAtual.concluirAcaoKhan();

	    return true;
	}
	
	boolean concluirAcaoKhanMelhorias() {
	    if (encerrada || turnoAtual == null || !turnoAtual.isKhanPendente()) {
	        return false;
	    }

	    tabuleiroPrincipal.ativarMelhoriasKhan();
	    turnoAtual.concluirAcaoKhan();

	    return true;
	}
	
	

	Turno getTurnoAtual() {
		return turnoAtual; 
	}
		
	private void VerificaFimDisparado() {
		if(fimDisparado) {
			return; 
		}
		
		if(votosFichaNeutra >= limiteVotos|| tabuleiroPrincipal.todasCidadesConquistadas()) {
			fimDisparado = true;  
			indiceJogadorInicialFim = indiceJogadorAtual;
		}
	}
	
	boolean registrarVoto(Jogador jogador, int quantidade) {
		if (encerrada || quantidade <= 0
		|| jogador == null || !jogadores.contains(jogador)) {
		return false;
		}

		jogador.adicionarVotos(quantidade);
		votosFichaNeutra += quantidade;
		VerificaFimDisparado();

		return true;

	}

	void adicionarFichaNeutra(int qtd) {
		registrarVoto(getJogadorAtual(), qtd);
	}
	

	boolean isFimDisparado() {
	    return fimDisparado;
	}
	
	boolean isEncerrada() {
	    return encerrada;
	}
	
	

	boolean moverPeao(Peao peao, Parada destino) {
	    if (encerrada || turnoAtual == null || peao == null || destino == null) {
	        return false;
	    }
	
	    
	    if (peao.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }
	    
	    Parada origem = peao.getPosicao();
	
	    if (origem == null || !origem.getVizinhos().containsValue(destino)) {
	        return false;
	    }
	
	    if (turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO) <= 0) {
	        return false;
	    }
	    
	    if (!peao.getPosicao().getVizinhos().containsValue(destino)) {
	        return false;
	    }
	    
	    if (turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO) <= 0) {
	        return false;
	    }
	    //VERIFICAR SE É VALIDO NO MANUAL
	    
	    if (!destino.isMoveble()) {
	        return false;
	    }
	
	    HashSet<Provincia> provinciasVisitadas = tabuleiroPrincipal.moverPeao(peao, destino);
	    

	    if (!turnoAtual.usarAcao(TipoAcao.MOVIMENTO)) {
	        return false;
	    }

	    turnoAtual.adicionarProvinciasVisitadas(provinciasVisitadas);
	    paradasPercorridas.get(peao).add(destino);

	    return true;
	}
	
	boolean pegarTributo(Provincia provincia) {
	    if (encerrada || turnoAtual == null || provincia == null) {
	        return false;
	    }

	    if (!turnoAtual.visitouProvincia(provincia)
	            || provincia.getCargas() <= 0) {
	        return false;
	    }

	    if (turnoAtual.getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO) <= 0) {
	        return false;
	    }

	    TipoTributo tipo = provincia.descarregar(1);

	    if (tipo == null) {
	        return false;
	    }

	    if (!turnoAtual.usarAcao(TipoAcao.PEGAR_TRIBUTO)) {
	        provincia.carregar();
	        return false;
	    }

	    turnoAtual.getJogador().adicionarTributo(tipo, 1);

	    return true;
	}
	

	boolean construirYurt(Peao peao, Parada parada) {
	    if (encerrada || turnoAtual == null || peao == null || parada == null) {
	        return false;
	    }
	
	    if (peao.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }
	
	    HashSet<Parada> percorridas = paradasPercorridas.get(peao);
	
	    if (percorridas == null || !percorridas.contains(parada)) {
	        return false;
	    }
	
	    if (!parada.podeReceberYurt()) {
	        return false;
	    }
	
	    Jogador jogador = turnoAtual.getJogador();
	
	    if (!jogador.usarYurt()) {
	        return false;
	    }
	
	    parada.adicionarYurt(jogador);
	
	    return true;
	}
	
	boolean atacarCidade(Peao peao, Cidade cidade, TipoTesouro tesouro) {
	    if (encerrada || turnoAtual == null || peao == null || cidade == null || tesouro == null) {
	        return false;
	    }

	    if (peao.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }

	    HashSet<Parada> percorridas = paradasPercorridas.get(peao);

	    if (percorridas == null) {
	        return false;
	    }

	    boolean adjacente = false;

	    for (Parada parada : percorridas) {
	        if (parada.getCidade() == cidade) {
	            adjacente = true;
	            break;
	        }
	    }

	    if (!adjacente) {
	        return false;
	    }

	    int quantidadeTesouro = cidade.getTesouros().getOrDefault(tesouro, 0);

	    if (quantidadeTesouro <= 0) {
	        return false;
	    }

	    int tesourosAnteriores = tesourosTomadosNoTurno.getOrDefault(cidade, 0);
	    int custoEspadas = (tesourosAnteriores == 0) ? 1 : 2;

	    int espadasVirtuais =turnoAtual.getQuantidadeRecursoVirtual(TipoMelhoria.ESPADA_VIRTUAL);
	    int espadasNormais = turnoAtual.getJogador().getQuantidadeTributo(TipoTributo.ESPADA);

	    if (espadasVirtuais + espadasNormais < custoEspadas) {
	        return false;
	    }

	    // Gastar primeiro as espadas virtuais.
	    int gastarVirtual = Math.min(espadasVirtuais, custoEspadas);

	    for (int i = 0; i < gastarVirtual; i++) {
	        turnoAtual.usarRecursoVirtual(TipoMelhoria.ESPADA_VIRTUAL);
	    }

	    int gastarNormal = custoEspadas - gastarVirtual;

	    if (gastarNormal > 0 && !turnoAtual.getJogador().gastarTributo(TipoTributo.ESPADA, gastarNormal)) {
	        return false;
	    }

	    Jogador jogador = turnoAtual.getJogador();

	    cidade.atacar(jogador, tesouro);
	    jogador.adicionarTesouro(tesouro);

	    tesourosTomadosNoTurno.put(cidade, tesourosAnteriores + 1);

	    return true;
	}
	
	
	
	
	
	
	

}

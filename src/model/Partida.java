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

	
	
	//PARA UM JOGADOR, COMO FICARIA PARA VÁRIOS?
	private ArrayList<ConselheiroSecreto> sortearConselheiros() {
	    ArrayList<TipoConselheiroSecreto> tipos = new ArrayList<>();

	    for (TipoConselheiroSecreto tipo :TipoConselheiroSecreto.values()) {
	        tipos.add(tipo);
	    }

	    java.util.Collections.shuffle(tipos);

	    ArrayList<ConselheiroSecreto> opcoes = new ArrayList<>();

	    opcoes.add(new ConselheiroSecreto(tipos.get(0)));
	    opcoes.add(new ConselheiroSecreto(tipos.get(1)));

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

	        ArrayList<ConselheiroSecreto> conselheiros = sortearConselheiros();

	        
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
	
	void AvancarJogador() {
		if (jogadores.isEmpty() || encerrada) {
	        return;
	    }

	    indiceJogadorAtual =(indiceJogadorAtual + 1) % jogadores.size();
		
	}
	
	boolean iniciarTurno(Regiao regiao) {
		if(encerrada || jogadores.isEmpty() || turnoAtual!= null) {
			return false; 
		}
		
		Jogador jogador = getJogadorAtual();
		
		HashMap<Enum<?>, Integer> resultado = jogador.getTabuleiroJogador().ativarColuna(regiao);
		if (resultado == null) {
		        return false;
		    }

		turnoAtual = new Turno(jogador, resultado);

	    return true;
		
		
		
	}
	
	boolean iniciarTurnoColunaBranca() {
		if(encerrada || jogadores.isEmpty() || turnoAtual!= null) {
			return false; 
		}
		
		Jogador jogador = getJogadorAtual();
		
		HashMap<Enum<?>, Integer> resultado = jogador.getTabuleiroJogador().ativarColunaBranca();
		if (resultado == null) {
		        return false;
		    }

		turnoAtual = new Turno(jogador, resultado);

	    return true;
		
		
	}
	
	boolean TerminarTurno() {
	    if (turnoAtual == null) {
	    	return false;
	    	
	    }
	    	
	    if (turnoAtual.isKhanPendente()) {
	    	return false;
	    }

	    turnoAtual = null;
	    AvancarJogador();

	    if (fimDisparado) {
	        if (rodadasExtras == 0 &&
	            indiceJogadorAtual == indicePrimeiroJogador) {

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

	    tabuleiroPrincipal.ativarProvinciaKhan(provincia, tributo1, tributo2);

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
	
	void AdicionarFichaNeutra(int qtd) {
		if(qtd <= 0 || fimDisparado ||encerrada) {
			return; 
		}
		
		votosFichaNeutra += qtd; 
		VerificaFimDisparado(); 
	}
	

	boolean isFimDisparado() {
	    return fimDisparado;
	}
	
	boolean isEncerrada() {
	    return encerrada;
	}
	
	

	boolean moverPeao(Peao peao, Parada destino) {
	    if (encerrada || turnoAtual == null) {
	        return false;
	    }
	
	    if (peao == null || destino == null) {
	        return false;
	    }
	
	    if (peao.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }
	
	    if (peao.getPosicao() == null) {
	        return false;
	    }
	
	    if (turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO) <= 0) {
	        return false;
	    }
	    
	    if (!peao.getPosicao().getVizinhos().containsValue(destino)) {
	        return false;
	    }
	    
	    if (!turnoAtual.usarAcao(TipoAcao.MOVIMENTO)) {
	        return false;
	    }
	
	    HashSet<Provincia> provinciasVisitadas = tabuleiroPrincipal.moverPeao(peao, destino);

	    turnoAtual.adicionarProvinciasVisitadas(provinciasVisitadas);

	    return true;
	}

}

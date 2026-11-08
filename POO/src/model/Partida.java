package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private boolean skipped = false;
	private boolean hasToMove = false;
    private final HashMap<Integer, ConselheiroAberto> conselheirosAbertos = new HashMap<>();
	private KhanID ativandoKhan = null;
    private HashSet<ProvinciaID> selecaoKhan = new HashSet<ProvinciaID>();
	
	Partida(ArrayList<Cor> coresEscolhidas) {
        this(coresEscolhidas, sortearConselheirosAbertos());
    }

    // Também permite preparar uma partida determinística nos testes do Model.
    Partida(ArrayList<Cor> coresEscolhidas, List<TipoConselheiroAberto> cartasAbertas) {
		//O jogo é de 2 a 5 jogadores 
	    if (coresEscolhidas == null || coresEscolhidas.size() < 2 || coresEscolhidas.size() > 5) {
	        throw new IllegalArgumentException( "A partida deve ter de 2 a 5 jogadores." );
	    }

	    for (int i = 0; i < coresEscolhidas.size(); i++) {
	        if (coresEscolhidas.get(i) == null || coresEscolhidas.indexOf(coresEscolhidas.get(i)) != i) {
	            throw new IllegalArgumentException("As cores devem ser válidas e diferentes.");
	        }
	    }

        if (cartasAbertas == null || cartasAbertas.size() != 4
                || cartasAbertas.contains(null) || new HashSet<>(cartasAbertas).size() != 4) {
            throw new IllegalArgumentException("A partida deve ter quatro conselheiros abertos distintos.");
        }
        for (int i = 0; i < cartasAbertas.size(); i++) {
            conselheirosAbertos.put(i, new ConselheiroAberto(cartasAbertas.get(i)));
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

    private static ArrayList<TipoConselheiroAberto> sortearConselheirosAbertos() {
        ArrayList<TipoConselheiroAberto> baralho = new ArrayList<>();
        java.util.Collections.addAll(baralho, TipoConselheiroAberto.values());
        java.util.Collections.shuffle(baralho);
        return new ArrayList<>(baralho.subList(0, 4));
    }

    ArrayList<ConselheiroAberto> getConselheirosAbertos() {
        ArrayList<ConselheiroAberto> cartas = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            cartas.add(conselheirosAbertos.get(i));
        }
        return cartas;
    }

    int getVotosFichaNeutra() {
        return votosFichaNeutra;
    }

    int verificarConselheirosAbertos() {
        if (encerrada || turnoAtual == null) {
            return 0;
        }
        Jogador jogador = turnoAtual.getJogador();
        int cumpridas = 0;
        for (ConselheiroAberto carta : getConselheirosAbertos()) {
            if (carta.demandaCumprida(jogador, tabuleiroPrincipal)
                    && registrarVoto(jogador, 1)) {
                carta.marcarCumprida(jogador);
                cumpridas++;
            }
        }
        return cumpridas;
    }

    // Ponto de integração para a entrega de tesouros que concede uma peça bônus.
    boolean receberPecaBonus(PecaBonus peca) {
        if (encerrada || turnoAtual == null
                || !turnoAtual.getJogador().adicionarPecaBonus(peca)) {
            return false;
        }
        verificarConselheirosAbertos();
        return true;
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

		    TabuleiroJogador tabuleiroJogador =
		            new TabuleiroJogador(personagem);

		    ArrayList<ConselheiroSecreto> conselheiros =
		            sortearConselheiros(baralho);

		    Jogador jogador = new Jogador(
		            conselheiros,
		            tabuleiroJogador
		    );

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
		//System.out.println(indiceJogadorAtual);
		if (jogadores.isEmpty() || encerrada) {
	        return;
	    }

	    indiceJogadorAtual =(indiceJogadorAtual + 1) % jogadores.size();
		
	}
	
	
	//TURNO 
	boolean escolherConselheiroJogadorAtual(int indice) {
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
		//System.out.println(jogador.getTabuleiroJogador().getPersonagem().getCor());

		//System.out.println("Turno iniciado");
		
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

	    //System.out.println("turno aberto");
	    verificarConselheirosAbertos();
	    return true;
	}

	boolean TerminarTurno() {
		//System.out.println("turno finalizado");
	    if (turnoAtual == null) {
	        return false;
	    }

	    if (turnoAtual.isKhanPendente() || hasToMove) {
	        return false;
	    }
	    verificarConselheirosAbertos();
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
	
	boolean concluirAcaoKhan(KhanID provincia) {

	    if (encerrada || turnoAtual == null) {
	        return false;
	    }

	    if (!turnoAtual.isKhanPendente()) {
	        return false;
	    }

	    if (provincia == null) {
	        return false;
	    }

	    tabuleiroPrincipal.ativarKhan(provincia, selecaoKhan);

	    turnoAtual.concluirAcaoKhan();
	    ativandoKhan = null;
	    selecaoKhan.clear();
	    verificarConselheirosAbertos();
	    return true;
	}
	
	Turno getTurnoAtual() {
		return turnoAtual; 
	}
		
	private void VerificaFimDisparado() {
	    if (fimDisparado) {
	        return;
	    }

	    if (votosFichaNeutra >= limiteVotos || tabuleiroPrincipal.todasCidadesConquistadas()) {
	        fimDisparado = true;
	        indiceJogadorInicialFim = indiceJogadorAtual;
	        indicePrimeiroJogador = indiceJogadorAtual;
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
	
	boolean moverPeao(ParadaID id) {
		Parada destino = tabuleiroPrincipal.getMapa().get(id);
		//System.out.println(peaoAtivo.getJogador().getTabuleiroJogador().getPersonagem().getCor());
	    if (encerrada || turnoAtual == null || peaoAtivo == null || destino == null) {
	        return false;
	    }

	    
	    if (peaoAtivo.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }

	    
	    Parada origem = peaoAtivo.getPosicao();

	    if (origem == null || !origem.getVizinhos().containsValue(destino)) {
	        return false;
	    }
	    //System.out.println(turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO));

	    if (turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO) <= 0) {
	        return false;
	    }

	    
	    if (!destino.isMoveble()) {
	    	if(tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO)-1).isEmpty())
	    		return false;
	    	else hasToMove = true;
	    }else hasToMove = false;

	    HashSet<Provincia> provinciasVisitadas =
	            tabuleiroPrincipal.moverPeao(peaoAtivo, destino);
	    //System.out.println(peaoAtivo.getPosicao().getId());
	    if(skipped || !destino.getYurt().contains(peaoAtivo.getJogador())) {
	    	skipped = false;
	    	if(!turnoAtual.usarAcao(TipoAcao.MOVIMENTO)) return false;
	    }
	    else skipped = true;

	    turnoAtual.adicionarProvinciasVisitadas(provinciasVisitadas);
	    paradasPercorridas.get(peaoAtivo).add(destino);
	    verificarConselheirosAbertos();
	    return true;
	}
	
	boolean pegarTributo(ProvinciaID id) {
		Provincia provincia = tabuleiroPrincipal.getProvincias().get(id);
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

	    verificarConselheirosAbertos();
	    return true;
	}
	
	boolean selecionarTributo(ProvinciaID id) {
		if(ativandoKhan == null) pegarTributo(id);
		else {
			selecaoKhan.add(id);
			if(selecaoKhan.size()>= 2) {
				concluirAcaoKhan(ativandoKhan);
			}
		}
		return true;
	}
	
	//verificar se um peão específico tem permissão para realizar uma ação naquele turno
	private boolean peaoPodeAgir(Peao peao){
			if (turnoAtual == null || peao == null || peao.getJogador() != turnoAtual.getJogador()) {
		        return false;
		    }
			
			//Altani tem dois peões e apenas um dos dois possa realizar certas ações restritas no mesmo turno
			boolean altani = turnoAtual.getJogador().getTabuleiroJogador().getPersonagem() instanceof Altani;
			
			return !altani || peao == peaoAtivo;
			
	}
	
	//escolher qual peao do Altani usar 
	boolean escolherPeaoAtivo(Peao peao) {
		if(encerrada || turnoAtual == null || acaoRestritaFeita || peao == null || peao.getJogador() != turnoAtual.getJogador()) {
			return false; 
		}
		peaoAtivo = peao; 
		return true; 
	
	}
	
	boolean construirYurt(ParadaID id) {
		int i = 0;
		//System.out.println(i++);
		Parada parada = tabuleiroPrincipal.getMapa().get(id);
	    if (encerrada || turnoAtual == null || peaoAtivo == null || parada == null || !peaoPodeAgir(peaoAtivo)) {
	        return false;
	    }
	    //System.out.println(i++);
	    if (peaoAtivo.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }
	    //System.out.println(i++);

	    HashSet<Parada> percorridas = paradasPercorridas.get(peaoAtivo);

	    if (percorridas == null || !percorridas.contains(parada)) {
	        return false;
	    }

	    //System.out.println(i++);
	    if (!parada.podeReceberYurt()) {
	        return false;
	    }

	    //System.out.println(i++);
	    Jogador jogador = turnoAtual.getJogador();

	    if (!jogador.usarYurt()) {
	        return false;
	    }

	    //System.out.println(i++);
	    tabuleiroPrincipal.adicionarYurt(jogador, parada);
	    acaoRestritaFeita = true;

	    verificarConselheirosAbertos();
	    return true;
	}

	boolean atacarCidade(CidadeID cidadeId, int index) {
		Peao peao = peaoAtivo;
		Cidade cidade = tabuleiroPrincipal.getCidade(cidadeId);
		TipoTesouro tesouro = cidade.getTesouros().get(index);
		if (encerrada || turnoAtual == null || cidade == null || tesouro == null || !peaoPodeAgir(peao)) {
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

	    int quantidadeTesouro = cidade.getTesouros().size();

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

	    tabuleiroPrincipal.atacarCidade(cidadeId, jogador, tesouro);
	    jogador.adicionarTesouro(tesouro);

	    tesourosTomadosNoTurno.put(cidade, tesourosAnteriores + 1);
	    
	    acaoRestritaFeita = true;

	    verificarConselheirosAbertos();
	    return true;
	}
	
	//FIM DA PARTIDA
	
	// Compara os votos acumulados; em caso de empate, vence quem tiver
	// mais tesouros não entregues.
	// Se o empate persistir, retorna todos os jogadores empatados.
	// O desempate por distância até Karakorum ainda não está implementado.
	// Os votos finais de influência regional e das cartas secretas
	// precisam ser contabilizados antes de chamar este método.
	
	List<Jogador> calcularVencedores() {
	    if (!encerrada) {
	        throw new IllegalStateException(
	            "A partida ainda não terminou." );
	    }

	    ArrayList<Jogador> melhores = new ArrayList<>();

	    for (Jogador jogador : jogadores) {
	        if (melhores.isEmpty()) {
	            melhores.add(jogador);
	            continue;
	        }

	        int comparacao = comparar(jogador, melhores.get(0));

	        if (comparacao > 0) {
	            melhores.clear();
	            melhores.add(jogador);
	        } else if (comparacao == 0) {
	            melhores.add(jogador);
	        }
	    }

	    return melhores;
	}

	// Retorna um valor positivo se a for melhor que b,
	// negativo se b for melhor que a e zero em caso de empate.
	private int comparar(Jogador a, Jogador b) {
	    if (a.getVotos() != b.getVotos()) {
	        return Integer.compare(a.getVotos(), b.getVotos());
	    }

	    return Integer.compare(totalTesouros(a), totalTesouros(b));
	}

	// Soma os tesouros que o jogador ainda possui.
	private int totalTesouros(Jogador jogador) {
	    int total = 0;

	    for (TipoTesouro tipo : TipoTesouro.values()) {
	        total += jogador.getQuantidadeTesouro(tipo);
	    }

	    return total;
	}
	
	HashSet<ProvinciaID> getProvinciasKhan(){
		if(ativandoKhan!=null)
			return tabuleiroPrincipal.getProvinicasKhan(ativandoKhan);			
		return null;
	}
	
	void selecionarKhan(KhanID id) {
		if(turnoAtual == null||!turnoAtual.isKhanPendente()) return;
		if(id==KhanID.M) {
			concluirAcaoKhan(id);
			return;
		}
		ativandoKhan = id;
		selecaoKhan.clear();
	}
	
	HashMap<ParadaID, ArrayList<Cor>> getYurts(){
		return tabuleiroPrincipal.getYurts();
	}
	
	ArrayList<Jogador> getJogadores(){
		return jogadores;
	}
	
	HashMap<ProvinciaID, Integer> getCargasProvincias(){
		if(ativandoKhan == null) return tabuleiroPrincipal.getCargasProvincias();
		HashMap<ProvinciaID, Integer> ret = new HashMap<ProvinciaID, Integer>();
		for(ProvinciaID id: ProvinciaID.values())
			ret.put(id, 0);
		for(ProvinciaID pid: getProvinciasKhan()) {
			ret.put(pid, 1);
		}
		return ret;
	}
	
	HashSet<ParadaID> getAlcancaveisTotais(){
		HashSet<ParadaID> ret = new HashSet<ParadaID>();
		if(ativandoKhan != null) return ret;
		int passos = turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO);
		if(skipped) passos--;
		for(Parada p: tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, passos).keySet()) ret.add(p.getId());
		return ret;
		
	}
	
	HashSet<ParadaID> getVizinhosPossiveis(){
		HashSet<ParadaID> ret = new HashSet<ParadaID>();
		if(ativandoKhan != null) return ret;
		if(turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO)<=0) return ret;
		for(Parada viz: peaoAtivo.getPosicao().getVizinhos().values()) {
			if(!tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, viz, turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO)-1).isEmpty()) ret.add(viz.getId());
		}
		return ret;
	}
	
	HashSet<ProvinciaID> getProvincias(){
		if(ativandoKhan != null) {
			return selecaoKhan;
		}
		HashSet<ProvinciaID> ret = new HashSet<ProvinciaID>();
		if(turnoAtual.getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO)<=0) return ret;
		for(Provincia p: turnoAtual.getProvinciasVisitadas())
			ret.add(p.getId());
		return ret;
	}
	
	HashSet<KhanID> getKhanSet() {
		HashSet<KhanID> ret = new HashSet<KhanID>();
		if(turnoAtual == null || !turnoAtual.isKhanPendente()) return ret;
		for(KhanID id: KhanID.values()) {
			if(id!=tabuleiroPrincipal.getKhan()) ret.add(id);
		}
		return ret;
	}
	
	KhanID getKhan() {
		return tabuleiroPrincipal.getKhan();
	}
	
	boolean getAtivacaoKhan() {
		return ativandoKhan != null;
	}
	
	KhanID getKhanSelecionado() {
		return ativandoKhan;
	}
	
	HashMap<CidadeID, ArrayList<TipoTesouro>> getCidades(){
		HashMap<CidadeID, ArrayList<TipoTesouro>> ret = new HashMap<>();
		if(paradasPercorridas.get(peaoAtivo) == null) return ret;
		for(CidadeID id: CidadeID.values()) {
			Cidade c = tabuleiroPrincipal.getCidade(id);
			if(c.getId() == CidadeID.KARAKORUM||c.getTesouros().isEmpty()) continue;
			ret.put(c.getId(), c.getTesouros());
		}
		return ret;
	}
	
	HashMap<CidadeID, Integer> getCidadesAtacaveis(){
		HashMap<CidadeID, Integer> ret = new HashMap<>();
		if(paradasPercorridas.get(peaoAtivo) == null) return ret;
		for(Parada p: paradasPercorridas.get(peaoAtivo)) {
			Cidade c = p.getCidade();
			if(ret.containsKey(c.getId())||c.getId()==CidadeID.KARAKORUM||c.getTesouros().size()<=0) continue;
			ret.put(c.getId(), c.getTesouros().size());
		}
		return ret;
	}
	
	HashMap<ParadaID, TipoBonus> getBonus(){
		return tabuleiroPrincipal.getBonus();
	}
	
	HashMap<CidadeID, Cor> getCidadesConquistadas(){
		HashMap<CidadeID, Cor> ret = new HashMap<>();
		if(paradasPercorridas.get(peaoAtivo) == null) return ret;
		for(CidadeID id: CidadeID.values()) {
			Cidade c = tabuleiroPrincipal.getCidade(id);
			if(c.getId() == CidadeID.KARAKORUM||c.getConquistador() == null) continue;
			ret.put(c.getId(), c.getConquistador().getTabuleiroJogador().getPersonagem().getCor());
		}
		return ret;
	}
}

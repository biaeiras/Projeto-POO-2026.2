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
	private final int indicePrimeiroJogador = 0;
	private int turnosRestantesAposFim;
	private boolean pontuacaoFinalCalculada;
	
	private final HashMap<Peao, HashSet<Parada>> paradasPercorridas = new HashMap<>();
	private final HashMap<Cidade, Integer> tesourosTomadosNoTurno = new HashMap<>();
	private Peao peaoAtivo; 
	private boolean acaoRestritaFeita; 
	private int melhoriasGratisPendentes; 
	private boolean skipped = false;
	private boolean hasToMove = false;
    private KhanID ativandoKhan;
    private final HashSet<ProvinciaID> selecaoKhan = new HashSet<>();
    private final HashMap<Integer, ConselheiroAberto> conselheirosAbertos = new HashMap<>();
	
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
        ativandoKhan = null;
        selecaoKhan.clear();
	
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
	                contabilizarPontuacaoFinal();
	            }
	        }
	    }
	    
	    return true;
	}
	
    // Mantém as entradas do Model existentes, delegando à seleção validada.
    boolean concluirAcaoKhan(ProvinciaKhanID provincia, TipoTributo tributo1, TipoTributo tributo2) {
        if (provincia == null || tributo1 == null || tributo2 == null
                || tributo1 == TipoTributo.VAZIO || tributo2 == TipoTributo.VAZIO || tributo1 == tributo2) return false;
        KhanID destino = KhanID.valueOf(provincia.name());
        if (!selecionarKhan(destino)) return false;
        return selecionarTributo(ProvinciaID.valueOf(provincia.name() + "_" + tributo1.name()))
                && selecionarTributo(ProvinciaID.valueOf(provincia.name() + "_" + tributo2.name()));
    }

    boolean concluirAcaoKhanMelhorias() {
        return selecionarKhan(KhanID.M);
    }

    boolean selecionarKhan(KhanID id) {
        if (encerrada || turnoAtual == null || !getKhans().contains(id)) return false;
        ativandoKhan = id;
        selecaoKhan.clear();
        return id != KhanID.M || concluirAcaoKhan(id);
    }

    private boolean concluirAcaoKhan(KhanID id) {
        if (encerrada || turnoAtual == null || !turnoAtual.isKhanPendente()
                || !tabuleiroPrincipal.ativarKhan(id, new HashSet<>(selecaoKhan))) return false;
        turnoAtual.concluirAcaoKhan();
        ativandoKhan = null;
        selecaoKhan.clear();
        verificarConselheirosAbertos();
        return true;
    }

    boolean selecionarTributo(ProvinciaID id) {
        if (encerrada || turnoAtual == null) return false;
        if (ativandoKhan == null) return pegarTributo(id);
        if (!tabuleiroPrincipal.getProvinicasKhan(ativandoKhan).contains(id)
                || !selecaoKhan.add(id)) return false;
        if (selecaoKhan.size() == 2) return concluirAcaoKhan(ativandoKhan);
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
        if (encerrada || turnoAtual == null || ativandoKhan != null || peaoAtivo == null || destino == null) {
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
        if (encerrada || turnoAtual == null || ativandoKhan != null || provincia == null) {
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

        boolean usarVirtual = turnoAtual.getQuantidadeRecursoVirtual(TipoMelhoria.YURT_VIRTUAL) > 0;
        if (!jogador.usarYurt(usarVirtual)) return false;
        if (usarVirtual) turnoAtual.usarRecursoVirtual(TipoMelhoria.YURT_VIRTUAL);

	    //System.out.println(i++);
	    tabuleiroPrincipal.adicionarYurt(jogador, parada);
	    acaoRestritaFeita = true;

	    verificarConselheirosAbertos();
	    return true;
	}

    ArrayList<ParadaID> getLocaisEntregaDisponiveis() {
        ArrayList<ParadaID> locais = new ArrayList<>();
        if (encerrada || turnoAtual == null || ativandoKhan != null || peaoAtivo == null
                || !peaoPodeAgir(peaoAtivo)) {
            return locais;
        }
        Parada atual = peaoAtivo.getPosicao();
        if (!hasToMove && atual != null && atual.ehConselheiro()) {
            locais.add(atual.getId());
        }
        return locais;
    }

    HashMap<TipoTesouro, Integer> getExigenciaEntrega(ParadaID id) {
        Parada parada = tabuleiroPrincipal.getMapa().get(id);
        return parada == null ? new HashMap<>() : parada.getExigenciaConselheiro();
    }

    int getVotosEntrega(ParadaID id) {
        Parada parada = tabuleiroPrincipal.getMapa().get(id);
        return parada == null ? 0 : parada.getVotosEntrega();
    }

    TipoBonus getBonusEntrega(ParadaID id) {
        Parada parada = tabuleiroPrincipal.getMapa().get(id);
        return parada == null ? null : parada.getBonus();
    }

    private TipoMelhoria tipoTesouroVirtual(TipoTesouro tipo) {
        return TipoMelhoria.valueOf(tipo.name() + "_VIRTUAL");
    }

    int getQuantidadeTributoVirtual(TipoTributo tipo) {
        if (encerrada || turnoAtual == null || tipo == null || tipo == TipoTributo.VAZIO) return 0;
        return turnoAtual.getQuantidadeRecursoVirtual(TipoMelhoria.valueOf(tipo.name() + "_VIRTUAL"));
    }

    int getQuantidadeTesouroVirtual(TipoTesouro tipo) {
        return encerrada || turnoAtual == null ? 0
                : turnoAtual.getQuantidadeRecursoVirtual(tipoTesouroVirtual(tipo));
    }

    boolean entregarTesouros(ParadaID id, HashMap<TipoTesouro, Integer> entrega) {
        if (!getLocaisEntregaDisponiveis().contains(id)) {
            return false;
        }
        Parada parada = tabuleiroPrincipal.getMapa().get(id);
        if (!parada.aceitaEntrega(entrega)) {
            return false;
        }
        Jogador jogador = turnoAtual.getJogador();
        TipoBonus bonus = parada.getBonus();
        if (bonus != null && jogador.possuiPecaBonus(bonus)) {
            return false;
        }
        HashMap<TipoTesouro, Integer> gastosReais = new HashMap<>();
        HashMap<TipoTesouro, Integer> gastosVirtuais = new HashMap<>();
        // Valida toda a entrega antes de consumir qualquer recurso.
        for (TipoTesouro tipo : entrega.keySet()) {
            int virtual = Math.min(entrega.get(tipo), getQuantidadeTesouroVirtual(tipo));
            int real = entrega.get(tipo) - virtual;
            if (jogador.getQuantidadeTesouro(tipo) < real) {
                return false;
            }
            if (real > 0) gastosReais.put(tipo, real);
            gastosVirtuais.put(tipo, virtual);
        }
        if (!gastosReais.isEmpty() && !jogador.gastarTesouros(gastosReais)) {
            return false;
        }
        for (TipoTesouro tipo : gastosVirtuais.keySet()) {
            for (int i = 0; i < gastosVirtuais.get(tipo); i++) {
                turnoAtual.usarRecursoVirtual(tipoTesouroVirtual(tipo));
            }
        }
        registrarVoto(jogador, parada.getVotosEntrega());
        if (bonus != null) {
            PecaBonus peca = new PecaBonus(bonus);
            jogador.adicionarPecaBonus(peca);
            parada.setBonus(null);
            // Esta peça tem efeito imediato; as demais são guardadas para uso.
            if (bonus == TipoBonus.VOTO_EXTRA) {
                registrarVoto(jogador, 1);
                peca.usar();
            }
        }
        acaoRestritaFeita = true;
        verificarConselheirosAbertos();
        return true;
    }

    boolean atacarCidade(CidadeID id, int indiceTesouro) {
        if (encerrada || turnoAtual == null || ativandoKhan != null || id == null) return false;
        Cidade cidade = tabuleiroPrincipal.getCidade(id);
        if (cidade == null || id == CidadeID.KARAKORUM || indiceTesouro < 0
                || indiceTesouro >= cidade.getTesouros().size()) return false;
        return atacarCidade(peaoAtivo, cidade, cidade.getTesouros().get(indiceTesouro));
    }

	boolean atacarCidade(Peao peao, Cidade cidade, TipoTesouro tesouro) {
        if (encerrada || turnoAtual == null || ativandoKhan != null || cidade == null
                || cidade.getId() == CidadeID.KARAKORUM || tesouro == null || !peaoPodeAgir(peao)) {
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

        int quantidadeTesouro = java.util.Collections.frequency(cidade.getTesouros(), tesouro);

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

        if (!tabuleiroPrincipal.atacarCidade(cidade.getId(), jogador, tesouro)) {
            return false;
        }
	    jogador.adicionarTesouro(tesouro);

	    tesourosTomadosNoTurno.put(cidade, tesourosAnteriores + 1);
	    
	    acaoRestritaFeita = true;

	    verificarConselheirosAbertos();
	    return true;
	}
	
	//FIM DA PARTIDA
	
    // Votos finais não avançam a ficha neutra nem disparam novos turnos.
    private void contabilizarPontuacaoFinal() {
        if (!encerrada || pontuacaoFinalCalculada) return;
        HashMap<Jogador, Integer> pontos = new HashMap<>();
        for (Jogador jogador : jogadores) pontos.put(jogador, 0);
        for (Regiao regiao : Regiao.values()) {
            HashMap<Jogador, Integer> yurts = new HashMap<>();
            int primeiro = 0;
            int segundo = 0;
            for (Jogador jogador : jogadores) {
                int quantidade = tabuleiroPrincipal.getQuantidadeYurts(jogador, regiao);
                yurts.put(jogador, quantidade);
                if (quantidade > primeiro) {
                    segundo = primeiro;
                    primeiro = quantidade;
                } else if (quantidade < primeiro) {
                    segundo = Math.max(segundo, quantidade);
                }
            }
            int empatadosPrimeiro = 0;
            for (int quantidade : yurts.values()) if (quantidade == primeiro) empatadosPrimeiro++;
            for (Jogador jogador : jogadores) {
                int quantidade = yurts.get(jogador);
                if (quantidade == 0) continue;
                int melhorias = Math.min(4, jogador.getTabuleiroJogador().getQuantidadeMelhorias(regiao));
                int votos = 0;
                if (quantidade == primeiro) {
                    votos = Math.max(1, melhorias);
                } else if (empatadosPrimeiro == 1 && quantidade == segundo) {
                    votos = (melhorias + 1) / 2;
                }
                pontos.put(jogador, pontos.get(jogador) + votos);
            }
        }
        for (Jogador jogador : jogadores) {
            ConselheiroSecreto carta = jogador.getConselheiroSecreto();
            int votos = pontos.get(jogador)
                    + (carta == null ? 0 : carta.calcularVotos(jogador, tabuleiroPrincipal));
            if (votos > 0) jogador.adicionarVotos(votos);
        }
        pontuacaoFinalCalculada = true;
    }
	
	List<Jogador> calcularVencedores() {
	    if (!encerrada) {
	        throw new IllegalStateException(
	            "A partida ainda não terminou." );
	    }

        contabilizarPontuacaoFinal();

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

        int tesouros = Integer.compare(totalTesouros(a), totalTesouros(b));
        if (tesouros != 0) return tesouros;
        return Integer.compare(tabuleiroPrincipal.getDistanciaKarakorum(b),
                tabuleiroPrincipal.getDistanciaKarakorum(a));
	}

	// Soma os tesouros que o jogador ainda possui.
	private int totalTesouros(Jogador jogador) {
	    int total = 0;

	    for (TipoTesouro tipo : TipoTesouro.values()) {
	        total += jogador.getQuantidadeTesouro(tipo);
	    }

	    return total;
	}
	
    HashSet<KhanID> getKhans() {
        HashSet<KhanID> destinos = new HashSet<>();
        if (!encerrada && turnoAtual != null && turnoAtual.isKhanPendente()) {
            for (KhanID id : KhanID.values()) if (id != tabuleiroPrincipal.getKhan()) destinos.add(id);
        }
        return destinos;
    }

    KhanID getKhan() { return tabuleiroPrincipal.getKhan(); }

    boolean getAtivacaoKhan() { return ativandoKhan != null; }
    KhanID getKhanSelecionado() { return ativandoKhan; }

    HashMap<CidadeID, ArrayList<TipoTesouro>> getCidades() {
        HashMap<CidadeID, ArrayList<TipoTesouro>> cidades = new HashMap<>();
        for (CidadeID id : CidadeID.values()) {
            Cidade cidade = tabuleiroPrincipal.getCidade(id);
            if (id != CidadeID.KARAKORUM && !cidade.getTesouros().isEmpty()) {
                cidades.put(id, new ArrayList<>(cidade.getTesouros()));
            }
        }
        return cidades;
    }

    HashMap<CidadeID, Integer> getCidadesAtacaveis() {
        HashMap<CidadeID, Integer> cidades = new HashMap<>();
        if (encerrada || turnoAtual == null || ativandoKhan != null || peaoAtivo == null) return cidades;
        HashSet<Parada> percorridas = paradasPercorridas.get(peaoAtivo);
        if (percorridas == null) return cidades;
        for (Parada parada : percorridas) {
            Cidade cidade = parada.getCidade();
            if (cidade != null && cidade.getId() != CidadeID.KARAKORUM
                    && !cidade.getTesouros().isEmpty()) {
                cidades.put(cidade.getId(), cidade.getTesouros().size());
            }
        }
        return cidades;
    }

    HashMap<CidadeID, Cor> getCidadesConquistadas() {
        HashMap<CidadeID, Cor> conquistadas = new HashMap<>();
        for (CidadeID id : CidadeID.values()) {
            Jogador conquistador = tabuleiroPrincipal.getCidade(id).getConquistador();
            if (id != CidadeID.KARAKORUM && conquistador != null) {
                conquistadas.put(id, conquistador.getTabuleiroJogador().getPersonagem().getCor());
            }
        }
        return conquistadas;
    }

    HashMap<ParadaID, TipoBonus> getBonus() { return tabuleiroPrincipal.getBonus(); }

	HashMap<ParadaID, ArrayList<Cor>> getYurts(){
		return tabuleiroPrincipal.getYurts();
	}
	
	ArrayList<Jogador> getJogadores(){
		return jogadores;
	}
	
	HashMap<ProvinciaID, Integer> getCargasProvincias(){
        if (ativandoKhan == null) return tabuleiroPrincipal.getCargasProvincias();
        HashMap<ProvinciaID, Integer> cargas = new HashMap<>();
        for (ProvinciaID id : ProvinciaID.values()) {
            cargas.put(id, tabuleiroPrincipal.getProvinicasKhan(ativandoKhan).contains(id) ? 1 : 0);
        }
        return cargas;
	}
	
	HashSet<ParadaID> getAlcancaveisTotais(){
		HashSet<ParadaID> ret = new HashSet<ParadaID>();
        if (turnoAtual == null || ativandoKhan != null || encerrada) return ret;
		int passos = turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO);
		if(skipped) passos--;
		for(Parada p: tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, passos).keySet()) ret.add(p.getId());
		return ret;
		
	}
	
	HashSet<ParadaID> getVizinhosPossiveis(){
		HashSet<ParadaID> ret = new HashSet<ParadaID>();
        if (turnoAtual == null || ativandoKhan != null || encerrada) return ret;
		if(turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO)<=0) return ret;
		for(Parada viz: peaoAtivo.getPosicao().getVizinhos().values()) {
			if(!tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, viz, turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO)-1).isEmpty()) ret.add(viz.getId());
		}
		return ret;
	}
	
	HashSet<ProvinciaID> getProvincias(){
		HashSet<ProvinciaID> ret = new HashSet<ProvinciaID>();
        if (turnoAtual == null || encerrada) return ret;
        if (ativandoKhan != null) return new HashSet<>(selecaoKhan);
		if(turnoAtual.getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO)<=0) return ret;
		for(Provincia p: turnoAtual.getProvinciasVisitadas())
			ret.add(p.getId());
		return ret;
	}
}

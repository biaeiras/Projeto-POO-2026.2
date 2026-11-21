package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Partida  {
    private static final int[] VOTOS_PRIMEIRO = {1, 1, 2, 2, 3};
    private static final int[] VOTOS_SEGUNDO = {0, 1, 1, 2, 2};
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
    private final ArrayList<TipoTesouro> tesourosBonusPendentes = new ArrayList<>();
    private Melhoria melhoriaCompraPendente;
    private int origemMelhoriaCompra = -1;
    private boolean melhoriaGratisPendente;
    private final HashMap<Jogador, ArrayList<Melhoria>> melhoriasIniciais = new HashMap<>();
	private boolean hasToMove = false;
    private final HashMap<Integer, ConselheiroAberto> conselheirosAbertos = new HashMap<>();
	
 // Construtor usado exclusivamente na restauração de uma partida salva.
    Partida() {
        this.jogadores = new ArrayList<>();
    }
    
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
        colocarYurtsIniciais();
        sortearMelhoriasIniciais();
	    
	    
	}

    private void sortearMelhoriasIniciais() {
        for (Jogador jogador : jogadores) {
            int quantidade = jogador.getTabuleiroJogador().getPersonagem().getQuantidadeOpcoesMelhoriaInicial();
            if (quantidade == 0) continue;
            ArrayList<Melhoria> opcoes = new ArrayList<>();
            for (int i = 0; i < quantidade; i++) {
                Melhoria melhoria = tabuleiroPrincipal.comprarMelhoriaDaPilha();
                if (melhoria == null) throw new IllegalStateException("Faltam melhorias para a preparação.");
                opcoes.add(melhoria);
            }
            melhoriasIniciais.put(jogador, opcoes);
        }
    }

    boolean isEscolhaMelhoriaInicialPendente() {
        Jogador jogador = getJogadorAtual();
        return !encerrada && turnoAtual == null && melhoriasIniciais.containsKey(jogador)
                && jogador.jaEscolheuConselheiro();
    }

    ArrayList<Melhoria> getOpcoesMelhoriaInicial() {
        return isEscolhaMelhoriaInicialPendente()
                ? new ArrayList<>(melhoriasIniciais.get(getJogadorAtual())) : new ArrayList<Melhoria>();
    }

    boolean escolherMelhoriaInicial(int indice) {
        ArrayList<Melhoria> opcoes = getOpcoesMelhoriaInicial();
        if (indice < 0 || indice >= opcoes.size()) return false;
        if (!getJogadorAtual().getTabuleiroJogador().adicionarMelhoriaBranca(opcoes.get(indice))) return false;
        for (int i = 0; i < opcoes.size(); i++) {
            if (i != indice) tabuleiroPrincipal.devolverMelhoriaAPilha(opcoes.get(i));
        }
        melhoriasIniciais.remove(getJogadorAtual());
        return true;
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

    boolean possuiHabilidadeAtivavelJogadorAtual() {
        Jogador jogador = getJogadorAtual();
        return jogador != null && jogador.getTabuleiroJogador().getPersonagem().possuiHabilidadeAtivavel();
    }

    boolean podeAtivarHabilidadeJogadorAtual() {
        if (encerrada || turnoAtual == null || isSelecaoAcaoPendente()) {
            return false;
        }
        Personagem personagem = turnoAtual.getJogador().getTabuleiroJogador().getPersonagem();
        return personagem.podeAtivarHabilidade(turnoAtual);
    }

    boolean ativarHabilidadeJogadorAtual() {
        if (!podeAtivarHabilidadeJogadorAtual()) {
            return false;
        }
        Personagem personagem = turnoAtual.getJogador().getTabuleiroJogador().getPersonagem();
        boolean ativou = personagem.ativarHabilidade(turnoAtual);
        if (ativou) {
            verificarConselheirosAbertos();
        }
        return ativou;
    }

    boolean isSelecaoAcaoPendente() {
        return melhoriaGratisPendente || isSelecaoTesouroHabilidadePendente()
                || isEscolhaColunaMelhoriaPendente() || isEscolhaTesouroBonusPendente();
    }

    boolean isSelecaoTesouroHabilidadePendente() {
        return !encerrada && turnoAtual != null && turnoAtual.isSelecaoTesouroHabilidadePendente();
    }

    boolean cancelarSelecaoTesouroHabilidade() {
        return isSelecaoTesouroHabilidadePendente() && turnoAtual.cancelarSelecaoTesouroHabilidade();
    }

    HashMap<CidadeID, Integer> getCidadesSelecionaveisHabilidade() {
        HashMap<CidadeID, Integer> cidades = new HashMap<>();
        if (!isSelecaoTesouroHabilidadePendente()) return cidades;
        for (CidadeID id : CidadeID.values()) {
            Cidade cidade = tabuleiroPrincipal.getCidade(id);
            if (id != CidadeID.KARAKORUM && cidade.getConquistador() == null
                    && cidade.getContadorDeTesouros() >= 2) {
                cidades.put(id, cidade.getContadorDeTesouros());
            }
        }
        return cidades;
    }

    boolean escolherTesouroHabilidade(CidadeID id, int indiceTesouro) {
        if (!getCidadesSelecionaveisHabilidade().containsKey(id)) return false;
        Cidade cidade = tabuleiroPrincipal.getCidade(id);
        if (indiceTesouro < 0 || indiceTesouro >= cidade.getContadorDeTesouros()) return false;
        Jogador jogador = turnoAtual.getJogador();
        boolean pagou;
        if (turnoAtual.getQuantidadeRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL) > 0) {
            pagou = turnoAtual.usarRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL);
        } else {
            pagou = jogador.gastarTributo(TipoTributo.MOEDA, 1);
        }
        if (!pagou) return false;
        TipoTesouro tesouro = cidade.getTesouros().remove(indiceTesouro);
        jogador.adicionarTesouro(tesouro);
        turnoAtual.concluirSelecaoTesouroHabilidade();
        verificarConselheirosAbertos();
        return true;
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
    boolean isEscolhaTesouroBonusPendente() {
        return !tesourosBonusPendentes.isEmpty();
    }

    ArrayList<TipoTesouro> getTesourosBonusPendentes() {
        return new ArrayList<>(tesourosBonusPendentes);
    }

    boolean podeUsarPecaBonus(int indice) {
        if (encerrada || turnoAtual == null || isEscolhaPeaoPendente() || isSelecaoAcaoPendente()) return false;
        ArrayList<TipoBonus> ordem = getJogadorAtual().getOrdemPecasBonus();
        if (indice < 0 || indice >= ordem.size()) return false;
        TipoBonus tipo = ordem.get(indice);
        return tipo != TipoBonus.VOTO_EXTRA && !getJogadorAtual().isPecaBonusUsada(tipo);
    }

    boolean usarPecaBonus(int indice) {
        if (!podeUsarPecaBonus(indice)) return false;
        Jogador jogador = getJogadorAtual();
        TipoBonus tipo = jogador.getOrdemPecasBonus().get(indice);
        switch (tipo) {
            case DUAS_ESPADAS: jogador.adicionarTributo(TipoTributo.ESPADA, 2); break;
            case DUAS_MOEDAS: jogador.adicionarTributo(TipoTributo.MOEDA, 2); break;
            case DOIS_YURTS: jogador.adicionarTributo(TipoTributo.YURT, 2); break;
            case MOVIMENTOS_EXTRAS: turnoAtual.adicionarAcoesBonus(3, 1); break;
            case PEGAR_TRIBUTOS_EXTRAS: turnoAtual.adicionarAcoesBonus(0, 2); break;
            case ESCOLHER_TESOURO:
                ArrayList<TipoTesouro> opcoes = tabuleiroPrincipal.retirarTesourosBonus();
                if (opcoes.size() != 3) return false;
                tesourosBonusPendentes.addAll(opcoes);
                break;
            default: return false;
        }
        jogador.getPecasBonus().get(tipo).usar();
        verificarConselheirosAbertos();
        return true;
    }

    boolean escolherTesouroBonus(int indice) {
        if (encerrada || turnoAtual == null || indice < 0 || indice >= tesourosBonusPendentes.size()) return false;
        getJogadorAtual().adicionarTesouro(tesourosBonusPendentes.get(indice));
        tesourosBonusPendentes.clear();
        verificarConselheirosAbertos();
        return true;
    }

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
	
    private void colocarYurtsIniciais() {
        for (Jogador jogador : jogadores) {
            Personagem personagem = jogador.getTabuleiroJogador().getPersonagem();
            if (personagem.getPosicaoYurtInicial() != null
                    && !tabuleiroPrincipal.colocarYurtInicial(jogador)) {
                throw new IllegalStateException("Não foi possível colocar o yurt inicial de " + personagem.getNome());
            }
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
        if (!melhoriasIniciais.isEmpty()) return false;
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
	    melhoriaGratisPendente = false;
	    peaoAtivo = null;
        if (!(jogador.getTabuleiroJogador().getPersonagem() instanceof Altani)) {
            peaoAtivo = jogador.getPeao(0);
            registrarPosicaoInicialPeaoAtivo();
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

	    if (isEscolhaPeaoPendente() || isSelecaoAcaoPendente() || turnoAtual.isKhanPendente() || hasToMove) {
	        return false;
	    }
	    verificarConselheirosAbertos();
	    VerificaFimDisparado();

        TabuleiroJogador tabuleiro = turnoAtual.getJogador().getTabuleiroJogador();
        if (tabuleiro.colunaBrancaEstaAtivada()) {
            tabuleiro.recuperarFichasAtivacao();
        }

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
	
    boolean selecionarKhan(KhanID id) {
        if (!getKhans().contains(id) || !tabuleiroPrincipal.ativarKhan(id, turnoAtual.getJogador())) {
            return false;
        }
        turnoAtual.concluirAcaoKhan();
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
        if (encerrada || turnoAtual == null || isSelecaoAcaoPendente() || peaoAtivo == null || destino == null) {
	        return false;
	    }

	    
	    if (peaoAtivo.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }

	    
	    Parada origem = peaoAtivo.getPosicao();

        if (origem == null || turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO) <= 0) {
            return false;
        }
        ArrayList<Parada> caminho = tabuleiroPrincipal.getCaminhoDeUmMovimento(peaoAtivo, destino);
        if (caminho.isEmpty()) return false;
        boolean ocupado = !destino.isMoveble();
        if (ocupado) {
            int restantes = turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO) - 1;
            if (tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, destino, restantes).isEmpty()) {
                return false;
            }
        }
        if (!turnoAtual.usarAcao(TipoAcao.MOVIMENTO)) return false;
        HashSet<Provincia> provinciasVisitadas = tabuleiroPrincipal.moverPeao(peaoAtivo, destino);
        hasToMove = ocupado;
        // As paradas com yurts puladas no caminho não são registradas como visitadas.

	    turnoAtual.adicionarProvinciasVisitadas(provinciasVisitadas);
	    paradasPercorridas.get(peaoAtivo).add(destino);
	    verificarConselheirosAbertos();
	    return true;
	}
	
	boolean pegarTributo(ProvinciaID id) {
		Provincia provincia = tabuleiroPrincipal.getProvincias().get(id);
        if (encerrada || turnoAtual == null || isSelecaoAcaoPendente() || provincia == null) {
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
		if (!isEscolhaPeaoPendente() || peao == null || peao.getPosicao() == null
                || peao.getJogador() != turnoAtual.getJogador()) {
			return false; 
		}
        Jogador jogador = turnoAtual.getJogador();
        for (int i = 0; i < jogador.getQuantidadePeoes(); i++) {
            if (jogador.getPeao(i) == peao) {
                peaoAtivo = peao;
                registrarPosicaoInicialPeaoAtivo();
                return true;
            }
        }
        return false;
	
	}

    boolean isEscolhaPeaoPendente() {
        return !encerrada && turnoAtual != null && peaoAtivo == null
                && turnoAtual.getJogador().getTabuleiroJogador().getPersonagem() instanceof Altani;
    }

    private void registrarPosicaoInicialPeaoAtivo() {
        HashSet<Parada> paradas = new HashSet<>();
        Parada posicao = peaoAtivo.getPosicao();
        if (posicao != null) {
            paradas.add(posicao);
            turnoAtual.adicionarProvinciasVisitadas(new HashSet<Provincia>(posicao.getProvincias().values()));
        }
        paradasPercorridas.put(peaoAtivo, paradas);
    }
	
    private boolean podeConstruirYurtAdjacente(Parada destino) {
        if (peaoAtivo == null || peaoAtivo.getPosicao() == null || destino == null) {
            return false;
        }
        Personagem personagem = peaoAtivo.getJogador().getTabuleiroJogador().getPersonagem();
        return personagem.permiteConstruirYurtAdjacente()
                && peaoAtivo.getPosicao().getVizinhos().containsValue(destino);
    }

	boolean construirYurt(ParadaID id) {
		int i = 0;
		//System.out.println(i++);
		Parada parada = tabuleiroPrincipal.getMapa().get(id);
	    if (encerrada || turnoAtual == null || isSelecaoAcaoPendente() || peaoAtivo == null || parada == null || !peaoPodeAgir(peaoAtivo)) {
	        return false;
	    }
	    //System.out.println(i++);
	    if (peaoAtivo.getJogador() != turnoAtual.getJogador()) {
	        return false;
	    }
	    //System.out.println(i++);

	    HashSet<Parada> percorridas = paradasPercorridas.get(peaoAtivo);

	    if ((percorridas == null || !percorridas.contains(parada))
                && !podeConstruirYurtAdjacente(parada)) {
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
        if (encerrada || turnoAtual == null || isSelecaoAcaoPendente() || peaoAtivo == null
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
        if (encerrada || turnoAtual == null || isSelecaoAcaoPendente() || id == null) return false;
        Cidade cidade = tabuleiroPrincipal.getCidade(id);
        if (cidade == null || id == CidadeID.KARAKORUM || indiceTesouro < 0
                || indiceTesouro >= cidade.getTesouros().size()) return false;
        return atacarCidade(peaoAtivo, cidade, cidade.getTesouros().get(indiceTesouro));
    }

	boolean atacarCidade(Peao peao, Cidade cidade, TipoTesouro tesouro) {
        if (encerrada || turnoAtual == null || isSelecaoAcaoPendente() || cidade == null
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

    boolean isEscolhaColunaMelhoriaPendente() {
        return melhoriaCompraPendente != null;
    }

    ArrayList<Integer> getColunasDisponiveisMelhoria() {
        ArrayList<Integer> colunas = new ArrayList<>();
        if (!isEscolhaColunaMelhoriaPendente()) return colunas;
        for (int i = 0; i < 3; i++) {
            if (getJogadorAtual().getTabuleiroJogador().podeReceberMelhoriaComprada(regiaoColunaMelhoria(i))) colunas.add(i);
        }
        return colunas;
    }

    private Regiao regiaoColunaMelhoria(int coluna) {
        return coluna == 0 ? Regiao.CHINA : coluna == 1 ? Regiao.PERSIA : coluna == 2 ? Regiao.RUSSIA : null;
    }

    Melhoria getMelhoriaCompraPendente() {
        return melhoriaCompraPendente;
    }

    boolean escolherColunaMelhoria(int coluna) {
        if (!getColunasDisponiveisMelhoria().contains(coluna)) return false;
        TabuleiroJogador tabuleiro = getJogadorAtual().getTabuleiroJogador();
        int linhasAntes = tabuleiro.getQuantidadeLinhasCompletas();
        if (!tabuleiro.adicionarMelhoriaComprada(regiaoColunaMelhoria(coluna), melhoriaCompraPendente)) return false;
        retirarMelhoria(origemMelhoriaCompra);
        pagarMelhoria();
        melhoriaCompraPendente = null;
        origemMelhoriaCompra = -1;
        verificarRecompensaPorLinha(tabuleiro, linhasAntes);
        verificarConselheirosAbertos();
        return true;
    }

    private boolean iniciarEscolhaColunaMelhoria(Melhoria melhoria, int origem) {
        if (!getJogadorAtual().getTabuleiroJogador().getPersonagem().ignoraCorMelhoria()) return false;
        melhoriaCompraPendente = melhoria;
        origemMelhoriaCompra = origem;
        return true;
    }

    boolean isMelhoriaGratisPendente() {
        return melhoriaGratisPendente;
    }

    private void verificarRecompensaPorLinha(TabuleiroJogador tabuleiro, int linhasAntes) {
        int linhas = tabuleiro.getQuantidadeLinhasCompletas();
        int linhasSemRecompensa = tabuleiro.getPersonagem().getQuantidadeOpcoesMelhoriaInicial() > 0 ? 1 : 0;
        melhoriaGratisPendente = linhas > linhasAntes && linhas > linhasSemRecompensa
                && tabuleiro.podeReceberMelhoriaBranca()
                && (tabuleiroPrincipal.consultarMelhoriaDaPilha() != null
                    || tabuleiroPrincipal.temMelhoriaDisponivel());
    }

    boolean comprarMelhoriaTabuleiro(int indice) {
        return adquirirMelhoria(tabuleiroPrincipal.consultarMelhoria(indice), indice);
    }

    private boolean adquirirMelhoria(Melhoria melhoria, int origem) {
        if (melhoria == null) return false;
        TabuleiroJogador tabuleiro = getJogadorAtual().getTabuleiroJogador();
        if (melhoriaGratisPendente) {
            if (!tabuleiro.adicionarMelhoriaBranca(melhoria)) return false;
            retirarMelhoria(origem);
            melhoriaGratisPendente = false;
        } else {
            if (!podeComprarMelhoria(melhoria)) return false;
            if (iniciarEscolhaColunaMelhoria(melhoria, origem)) return true;
            int linhasAntes = tabuleiro.getQuantidadeLinhasCompletas();
            if (!tabuleiro.adicionarMelhoria(melhoria)) return false;
            retirarMelhoria(origem);
            pagarMelhoria();
            verificarRecompensaPorLinha(tabuleiro, linhasAntes);
        }
        verificarConselheirosAbertos();
        return true;
    }

    private void retirarMelhoria(int origem) {
        if (origem < 0) tabuleiroPrincipal.comprarMelhoriaDaPilha();
        else tabuleiroPrincipal.comprarMelhoria(origem);
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
                int quantidade = tabuleiroPrincipal.getQuantidadeYurtsIncluindoConquistas(jogador, regiao);
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
                    votos = VOTOS_PRIMEIRO[melhorias];
                } else if (empatadosPrimeiro == 1 && quantidade == segundo) {
                    votos = VOTOS_SEGUNDO[melhorias];
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
        if (!encerrada && turnoAtual != null && !isEscolhaPeaoPendente() && !isSelecaoAcaoPendente() && turnoAtual.isKhanPendente()) {
            for (KhanID id : KhanID.values()) if (id != tabuleiroPrincipal.getKhan()) destinos.add(id);
        }
        return destinos;
    }

    KhanID getKhan() { return tabuleiroPrincipal.getKhan(); }

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
        if (encerrada || turnoAtual == null || isSelecaoAcaoPendente() || peaoAtivo == null) return cidades;
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
	
    HashMap<ProvinciaID, Integer> getCargasProvincias() {
        return tabuleiroPrincipal.getCargasProvincias();
    }

	HashSet<ParadaID> getAlcancaveisTotais(){
		HashSet<ParadaID> ret = new HashSet<ParadaID>();
        if (turnoAtual == null || peaoAtivo == null || isSelecaoAcaoPendente() || encerrada) return ret;
		int passos = turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO);
		for(Parada p: tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, passos).keySet()) ret.add(p.getId());
		return ret;
		
	}
	
    HashSet<ParadaID> getVizinhosPossiveis() {
        HashSet<ParadaID> destinos = new HashSet<>();
        if (turnoAtual == null || peaoAtivo == null || isSelecaoAcaoPendente() || encerrada) return destinos;
        int movimentos = turnoAtual.getQuantidadeAcao(TipoAcao.MOVIMENTO);
        if (movimentos <= 0) return destinos;
        for (Parada parada : tabuleiroPrincipal.getMovimentosPossiveis(peaoAtivo, 1).keySet()) {
            destinos.add(parada.getId());
        }
        // Uma parada ocupada só pode ser atravessada se ainda houver como continuar.
        for (Parada vizinha : peaoAtivo.getPosicao().getVizinhos().values()) {
            if (!vizinha.isMoveble() && !tabuleiroPrincipal.getMovimentosPossiveis(
                    peaoAtivo, vizinha, movimentos - 1).isEmpty()) destinos.add(vizinha.getId());
        }
        return destinos;
    }

	HashSet<ProvinciaID> getProvincias(){

		HashSet<ProvinciaID> ret = new HashSet<ProvinciaID>();
        if (turnoAtual == null || encerrada || isSelecaoAcaoPendente()) return ret;
		if(turnoAtual.getQuantidadeAcao(TipoAcao.PEGAR_TRIBUTO)<=0) return ret;
		for(Provincia p: turnoAtual.getProvinciasVisitadas())
			ret.add(p.getId());
		return ret;
	}
	
    boolean comprarMelhoriaDaPilha() {
        return adquirirMelhoria(tabuleiroPrincipal.consultarMelhoriaDaPilha(), -1);
    }

    private boolean podePagarMelhoria() {
        return !encerrada && turnoAtual != null && !isEscolhaPeaoPendente()
                && !isSelecaoAcaoPendente()
                && getJogadorAtual().getQuantidadeTributo(TipoTributo.MOEDA)
                    + turnoAtual.getQuantidadeRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL) >= 2;
    }

    private boolean podeComprarMelhoria(Melhoria melhoria) {
        if (!podePagarMelhoria() || melhoria == null) return false;
        TabuleiroJogador tabuleiro = getJogadorAtual().getTabuleiroJogador();
        if (!tabuleiro.getPersonagem().ignoraCorMelhoria()) {
            return tabuleiro.podeReceberMelhoriaComprada(melhoria.getRegiao());
        }
        for (int i = 0; i < 3; i++) {
            if (tabuleiro.podeReceberMelhoriaComprada(regiaoColunaMelhoria(i))) return true;
        }
        return false;
    }

    private void pagarMelhoria() {
        int virtuais = Math.min(2, turnoAtual.getQuantidadeRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL));
        for (int i = 0; i < virtuais; i++) turnoAtual.usarRecursoVirtual(TipoMelhoria.MOEDA_VIRTUAL);
        if (virtuais < 2) getJogadorAtual().gastarTributo(TipoTributo.MOEDA, 2 - virtuais);
    }

    boolean isCompravel() {
        Melhoria topo = tabuleiroPrincipal.consultarMelhoriaDaPilha();
        return topo != null && (melhoriaGratisPendente || podeComprarMelhoria(topo));
    }

	ArrayList<Melhoria> getMelhorias(){
		return tabuleiroPrincipal.getMelhorias();
	}
	
	
	void adicionarJogador(Jogador jogador) {
	    jogadores.add(jogador);
	}
	

	TabuleiroPrincipal getTabuleiroPrincipal() {
	    return tabuleiroPrincipal;
	}

	int getIndiceJogadorAtual() {
	    return indiceJogadorAtual;
	}

	int getLimiteVotos() {
	    return limiteVotos;
	}


	int getRodadasExtras() {
	    return rodadasExtras;
	}

	int getTurnosRestantesAposFim() {
	    return turnosRestantesAposFim;
	}

	boolean isPontuacaoFinalCalculada() {
	    return pontuacaoFinalCalculada;
	}
	
	void restaurarEstadoGeral(
	        int indiceJogador,
	        int votos,
	        boolean partidaEncerrada,
	        boolean fimDisparado,
	        int rodadasExtras,
	        int turnosRestantes,
	        boolean pontuacaoCalculada) {

	    if (indiceJogador < 0 || indiceJogador >= jogadores.size()|| votos < 0 || rodadasExtras < 0 || turnosRestantes < 0) {
	        throw new IllegalArgumentException("Estado da partida inválido.");
	    }

	    this.indiceJogadorAtual = indiceJogador;
	    this.votosFichaNeutra = votos;
	    this.encerrada = partidaEncerrada;
	    this.fimDisparado = fimDisparado;
	    this.rodadasExtras = rodadasExtras;
	    this.turnosRestantesAposFim = turnosRestantes;
	    this.pontuacaoFinalCalculada = pontuacaoCalculada;
	}
	
	void restaurarLimiteVotos(int limite) {
	    if (limite <= 0) {
	        throw new IllegalArgumentException("Limite de votos inválido.");
	    }
	    this.limiteVotos = limite;
	}
	
	void restaurarTabuleiroPrincipal(TabuleiroPrincipal tabuleiro) {
	    if (tabuleiro == null) {
	        throw new IllegalArgumentException("Tabuleiro inválido.");
	    }

	    this.tabuleiroPrincipal = tabuleiro;
	}
	
	void restaurarConselheirosAbertos( ArrayList<TipoConselheiroAberto> tipos, ArrayList<Jogador> cumpridosPor) {

	    if (tipos == null || cumpridosPor == null
	            || tipos.size() != 4 || cumpridosPor.size() != 4) {
	        throw new IllegalArgumentException("Dados dos conselheiros abertos inválidos.");
	    }

	    for (int i = 0; i < 4; i++) {
	        TipoConselheiroAberto tipo = tipos.get(i);
	        Jogador jogador = cumpridosPor.get(i);

	        if (tipo == null || tipos.indexOf(tipo) != i) {
	            throw new IllegalArgumentException("Conselheiro aberto inválido ou repetido.");
	        }

	        if (jogador != null && !jogadores.contains(jogador)) {
	            throw new IllegalArgumentException("Jogador do conselheiro não pertence à partida.");
	        }
	    }

	    conselheirosAbertos.clear();

	    for (int i = 0; i < 4; i++) {
	        ConselheiroAberto carta = new ConselheiroAberto(tipos.get(i));

	        if (cumpridosPor.get(i) != null) {
	            carta.restaurarCumprimento(cumpridosPor.get(i));
	        }

	        conselheirosAbertos.put(i, carta);
	    }
	}
	
	// Disponibiliza a criação do personagem para a restauração,
	// sem expor diretamente o método interno da partida.
	Personagem criarPersonagemParaRestauracao(Cor cor) {
	    return criarPersonagem(cor);
	}
	
}

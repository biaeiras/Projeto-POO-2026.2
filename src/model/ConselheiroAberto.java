package model;

class ConselheiroAberto extends CartaConselheiro {
	private final TipoConselheiroAberto tipo;
	private Jogador cumpridaPor;
	 
	ConselheiroAberto(TipoConselheiroAberto tipo) {
	        if (tipo == null) {
	            throw new IllegalArgumentException("O tipo de conselheiro deve ser informado.");
	        }
	        this.tipo = tipo;
	 }
	
	@Override
	String getNome() {
	    return tipo.name();
	 }
	 
	TipoConselheiroAberto getTipo() { 
		 return tipo;
	 }
	 
	boolean isCumprida() {
		return cumpridaPor != null;
	 }
	
	Jogador getCumpridaPor() {
	 	// quando um jogador consegue cumprir a carta ganha 1 voto  
	    return cumpridaPor;
	}

    boolean demandaCumprida(Jogador jogador, TabuleiroPrincipal tabuleiro) {
        if (jogador == null || tabuleiro == null || isCumprida()) {
            return false;
        }

        TabuleiroJogador cartela = jogador.getTabuleiroJogador();
        switch (tipo) {
            case QUATRO_ESPADAS:
                // Recursos virtuais não são peças de tributo possuídas.
                return jogador.getQuantidadeTributo(TipoTributo.ESPADA) >= 4;
            case SEIS_YURTS:
                return tabuleiro.getQuantidadeYurts(jogador) >= 6;
            case DEZ_YURTS:
                return tabuleiro.getQuantidadeYurts(jogador) >= 10;
            case QUATRO_TESOUROS:
                return jogador.getTotalTesouros() >= 4;
            case CINCO_TESOUROS_DIFERENTES:
                return jogador.getQuantidadeTiposTesouro() >= 5;
            case UM_YURT_POR_REGIAO:
                return possuiYurtsEmTodasRegioes(jogador, tabuleiro, 1);
            case DOIS_YURTS_POR_REGIAO:
                return possuiYurtsEmTodasRegioes(jogador, tabuleiro, 2);
            case DUAS_PECAS_BONUS:
                return jogador.getQuantidadePecasBonus() >= 2;
            case TRES_PECAS_BONUS:
                return jogador.getQuantidadePecasBonus() >= 3;
            case CONQUISTAR_DUAS_CIDADES:
                return tabuleiro.getQuantidadeCidadesConquistadas(jogador) >= 2;
            case CONQUISTAR_TRES_CIDADES:
                return tabuleiro.getQuantidadeCidadesConquistadas(jogador) >= 3;
            case LINHA_DE_MELHORIAS:
                return cartela.getQuantidadeLinhasCompletas() >= 1;
            case DUAS_COLUNAS_COM_DUAS_MELHORIAS:
                return contarColunasComMelhorias(cartela, 2) >= 2;
            case QUATRO_MELHORIAS_NA_COLUNA:
                return contarColunasComMelhorias(cartela, 4) >= 1;
            default:
                throw new IllegalStateException("Demanda não implementada: " + tipo);
        }
    }

    private boolean possuiYurtsEmTodasRegioes(
            Jogador jogador, TabuleiroPrincipal tabuleiro, int minimo) {
        for (Regiao regiao : Regiao.values()) {
            if (tabuleiro.getQuantidadeYurts(jogador, regiao) < minimo) {
                return false;
            }
        }
        return true;
    }

    private int contarColunasComMelhorias(TabuleiroJogador cartela, int minimo) {
        int quantidade = cartela.getMelhoriasColunaBranca().size() >= minimo ? 1 : 0;
        for (Regiao regiao : Regiao.values()) {
            if (cartela.getQuantidadeMelhorias(regiao) >= minimo) {
                quantidade++;
            }
        }
        return quantidade;
    }
	 
	void marcarCumprida(Jogador jogador) {
		if( jogador == null) {
			throw new IllegalStateException("Jogador não pode ser nulo"); 
	}
	
	if (isCumprida()) {
	    throw new IllegalStateException("Carta já foi cumprida por outro jogador");
		}
		cumpridaPor = jogador;
		//assim que essa carta for cumprida ela sai do jogo 
	 }	
}

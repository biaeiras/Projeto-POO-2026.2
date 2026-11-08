package model;

import java.util.ArrayList;
import java.util.HashMap;

class ConselheiroSecreto extends CartaConselheiro {
	private final TipoConselheiroSecreto tipo;
	
	ConselheiroSecreto(TipoConselheiroSecreto tipo) {
        this.tipo = tipo;
	}
	
	@Override
	String getNome() {
	    return tipo.name();
	 }
	
	TipoConselheiroSecreto getTipo() {
	    return tipo;
	}

    // Somente melhorias instaladas: ações básicas não são peças de melhoria.
    int calcularVotos(Jogador jogador, TabuleiroPrincipal mapa) {
        TabuleiroJogador cartela = jogador.getTabuleiroJogador();
        ArrayList<Melhoria> melhorias = new ArrayList<>(cartela.getMelhoriasColunaBranca());
        for (Regiao regiao : Regiao.values()) melhorias.addAll(cartela.getMelhorias(regiao));
        HashMap<TipoMelhoria, Integer> simbolos = new HashMap<>();
        for (Melhoria melhoria : melhorias) {
            simbolos.put(melhoria.getTipo(), simbolos.getOrDefault(melhoria.getTipo(), 0) + 1);
        }
        switch (tipo) {
            case VIAJANTE:
                return votosPorPatamares(mapa.getMaiorRedeYurts(jogador), 3, 6, 8);
            case SOLDADO:
                return votosPorPatamares(melhorias.size(), 4, 7, 10);
            case COLECIONADOR:
                return votosPorPatamares(simbolos.size(), 4, 6, 7);
            case SACERDOTE:
                int pares = 0;
                for (int quantidade : simbolos.values()) pares += quantidade / 2;
                return votosPorPatamares(pares, 1, 2, 4);
            case GOVERNADOR:
                int regioes = 0;
                for (Regiao regiao : Regiao.values()) {
                    if (mapa.getQuantidadeYurtsGovernador(jogador, regiao) >= 2
                            && cartela.getQuantidadeMelhorias(regiao) >= 2) regioes++;
                }
                return votosPorPatamares(regioes, 1, 2, 3);
            case BAJULADOR:
                return votosPorPatamares(mapa.getQuantidadeProvinciasKhanComYurts(jogador, 2), 2, 3, 4);
            case PREFEITO:
                return votosPorPatamares(mapa.getQuantidadeCidadesComYurtsAdjacentes(jogador, 2), 2, 3, 4);
            case CIDADAO:
                return votosPorPatamares(mapa.getQuantidadeCidadesComYurtsAdjacentes(jogador, 1), 3, 6, 8);
            case CAVALEIRO:
                int acoes = simbolos.getOrDefault(TipoMelhoria.MOVIMENTO_EXTRA, 0)
                        + simbolos.getOrDefault(TipoMelhoria.PEGAR_TRIBUTO_EXTRA, 0);
                return votosPorPatamares(acoes, 2, 4, 5);
            case MENSAGEIRO:
                return votosPorPatamares(mapa.getQuantidadeParadasDuplasComYurts(jogador), 2, 4, 6);
            default:
                throw new IllegalStateException("Conselheiro secreto desconhecido: " + tipo);
        }
    }

    private int votosPorPatamares(int quantidade, int primeiro, int segundo, int terceiro) {
        if (quantidade >= terceiro) return 3;
        if (quantidade >= segundo) return 2;
        return quantidade >= primeiro ? 1 : 0;
    }

}

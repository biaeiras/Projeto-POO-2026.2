package model;

import java.util.ArrayList;
import java.util.HashMap;

class TabuleiroJogador {
	
	private static class Coluna {

		private final Regiao regiao;
		private ArrayList<Melhoria> melhorias;
		private HashMap<TipoAcao, Integer> acoesBase;
	    private boolean ativada;

	    Coluna(Regiao regiao, HashMap<TipoAcao, Integer> acoesBase) {
	        this.regiao = regiao;
	        this.melhorias = new ArrayList<>();
	        this.acoesBase = new HashMap<>(acoesBase);
	        this.ativada = false;
	    }

	    boolean estaAtivada() {
	        return ativada;
	    }

	    void ativar() {
	        ativada = true;
	    }

	    void desativar() {
	        ativada = false;
	    }

	    boolean adicionarMelhoria(Melhoria melhoria) {

	        if (melhoria == null || melhorias.size() >= 5) {
	            return false;
	        }

	        // Coluna branca aceita melhorias de qualquer região
	        if (regiao == null || melhoria.getRegiao() == regiao) {
	            melhorias.add(melhoria);
	            return true;
	        }

	        return false;
	    }
	    
	    HashMap<Enum<?>, Integer> getAcoesEMelhorias() {

	        HashMap<Enum<?>, Integer> resultado = new HashMap<>();

	        // Adiciona as ações básicas
	        resultado.putAll(acoesBase);

	        // Adiciona as melhorias
	        for (Melhoria melhoria : melhorias) {

	            TipoMelhoria tipo = melhoria.getTipo();

	            resultado.put(
	                tipo,
	                resultado.getOrDefault(tipo, 0) + 1
	            );
	        }

	        return resultado;
	    }

	    int getQuantidadeMelhorias() {
	        return melhorias.size();
	    }
	    
	}

    private Personagem personagem;
    private HashMap<Regiao, Coluna> colunasRegiao;
    private Coluna colunaBranca;
    private int fichasAtivacaoDisponiveis;

    private static final int TOTAL_FICHAS_ATIVACAO = 4;

    TabuleiroJogador(Personagem personagem) {

        this.personagem = personagem;

        this.colunasRegiao = new HashMap<>();

        for (Regiao regiao : Regiao.values()) {

            HashMap<TipoAcao, Integer> acoesBase =
                    personagem.getAcoesBase(regiao);

            colunasRegiao.put(
                regiao,
                new Coluna(regiao, acoesBase)
            );
        }

        this.colunaBranca = new Coluna(
            null,
            personagem.getAcoesBase(null)
        );

        this.fichasAtivacaoDisponiveis = TOTAL_FICHAS_ATIVACAO;
    }

    Personagem getPersonagem() {
        return personagem;
    }

    int getFichasAtivacaoDisponiveis() {
        return fichasAtivacaoDisponiveis;
    }

    HashMap<Enum<?>, Integer> ativarColuna(Regiao regiao) {

        Coluna coluna = colunasRegiao.get(regiao);

        if (coluna == null ||
            fichasAtivacaoDisponiveis <= 0 ||
            coluna.estaAtivada()) {

            return null;
        }

        coluna.ativar();
        fichasAtivacaoDisponiveis--;

        return coluna.getAcoesEMelhorias();
    }

    HashMap<Enum<?>, Integer> ativarColunaBranca() {

        if (fichasAtivacaoDisponiveis <= 0 ||
            colunaBranca.estaAtivada()) {

            return null;
        }

        colunaBranca.ativar();
        fichasAtivacaoDisponiveis--;

        HashMap<Enum<?>, Integer> resultado =
                colunaBranca.getAcoesEMelhorias();

        recuperarFichasAtivacao();

        return resultado;
    }

    void recuperarFichasAtivacao() {
        for (Coluna coluna : colunasRegiao.values()) {
            coluna.desativar();
        }

        colunaBranca.desativar();

        fichasAtivacaoDisponiveis = TOTAL_FICHAS_ATIVACAO;
    }

    boolean colunaEstaAtivada(Regiao regiao) {
        Coluna coluna = colunasRegiao.get(regiao);

        if (coluna == null) {
            return false;
        }

        return coluna.estaAtivada();
    }

    boolean colunaBrancaEstaAtivada() {
        return colunaBranca.estaAtivada();
    }

    boolean adicionarMelhoria(Regiao regiao, Melhoria melhoria) {
        Coluna coluna = colunasRegiao.get(regiao);

        if (coluna == null || melhoria == null) {
            return false;
        }

        return coluna.adicionarMelhoria(melhoria);
    }

    boolean adicionarMelhoriaBranca(Melhoria melhoria) {
        if (melhoria == null) {
            return false;
        }

        return colunaBranca.adicionarMelhoria(melhoria);
    }

    int getQuantidadeMelhorias(Regiao regiao) {
        Coluna coluna = colunasRegiao.get(regiao);

        if (coluna == null) {
            return 0;
        }

        return coluna.getQuantidadeMelhorias();
    }

    int getQuantidadeLinhasCompletas() {
        int melhoriasRussia =
                colunasRegiao.get(Regiao.RUSSIA).getQuantidadeMelhorias();

        int melhoriasChina =
                colunasRegiao.get(Regiao.CHINA).getQuantidadeMelhorias();

        int melhoriasPersia =
                colunasRegiao.get(Regiao.PERSIA).getQuantidadeMelhorias();

        return Math.min(
                melhoriasRussia,
                Math.min(melhoriasChina, melhoriasPersia)
        );
    }
}
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FachadaJogo {

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
        
        partida.escolherConselheiroJogadorAtual(0);
		partida.avancarJogador();
		partida.escolherConselheiroJogadorAtual(0);
		partida.avancarJogador();
		partida.iniciarTurno(Regiao.RUSSIA);
    }

    // Associa a partida criada à fachada
    void definirPartida(Partida partida) {

        if (partida == null) {
            throw new IllegalArgumentException(
                "A partida não pode ser nula."
            );
        }

        this.partida = partida;
    }

    // Consulta se existe uma partida
    public boolean existePartida() {
        return partida != null;
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
		partida.moverPeao(id);
		return;
	}
	
	public void pegarTributo(ProvinciaID id) {
		partida.pegarTributo(id);
		return;
	}
	
	public HashMap<ProvinciaID, Integer> getCargasProvincias() {
		return partida.getCargasProvincias();
	}
	
	public void construirYurt(ParadaID id) {
		partida.construirYurt(id);
		return;
	}
	
	public void avancarTurno() {
		partida.TerminarTurno();
		partida.iniciarTurno(Regiao.PERSIA);
		return;
	}
	
	public HashMap<ParadaID, ArrayList<Cor>> getYurts(){
		return partida.getYurts();
	}
	
	public HashMap<ParadaID, ArrayList<Cor>> getPosicoesPeoes(){
		HashMap<ParadaID, ArrayList<Cor>> ret = new HashMap<ParadaID, ArrayList<Cor>>();
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
		return partida.getAlcancaveisTotais();
	}
	
	public HashSet<ParadaID> getVizinhosPossiveis(){
		return partida.getVizinhosPossiveis();
	}
	
	public HashSet<ProvinciaID> getProvincias(){
		return partida.getProvincias();
	}

}

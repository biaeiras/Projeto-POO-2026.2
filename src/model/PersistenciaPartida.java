
package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

class PersistenciaPartida {

    private PersistenciaPartida() {
    }

    static void salvarPartida(Partida partida, File arquivo) throws IOException {

        if (partida == null || arquivo == null) {
            throw new IllegalArgumentException(
                    "Partida ou arquivo inválido."
            );
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {

            TabuleiroPrincipal tabuleiro = partida.getTabuleiroPrincipal();

            escrever(writer, "VERSAO", "1");
            
            escrever(writer, "JOGADOR_ATUAL",partida.getIndiceJogadorAtual());
            
            escrever(writer, "LIMITE_VOTOS",partida.getLimiteVotos());
            
            escrever(writer, "VOTOS_FICHA_NEUTRA", partida.getVotosFichaNeutra());
            
            escrever(writer, "ENCERRADA",partida.isEncerrada());

            escrever(writer, "FIM_DISPARADO", partida.isFimDisparado());

            escrever(writer, "RODADAS_EXTRAS", partida.getRodadasExtras());

            escrever(writer, "TURNOS_RESTANTES",partida.getTurnosRestantesAposFim());

            escrever(writer, "PONTUACAO_FINAL_CALCULADA",partida.isPontuacaoFinalCalculada());
            
            ArrayList<Jogador> jogadores = partida.getJogadores();

            escrever(writer, "QUANTIDADE_JOGADORES",jogadores.size());
            
         // Conselheiros abertos e estado de cumprimento
            ArrayList<ConselheiroAberto> conselheiros = partida.getConselheirosAbertos();

            for (int i = 0; i < conselheiros.size(); i++) {
                ConselheiroAberto conselheiro = conselheiros.get(i);

                escrever(writer, "CONSELHEIRO_ABERTO_" + i,conselheiro.getTipo().name());

                Jogador cumpridoPor = conselheiro.getCumpridaPor();

                escrever(writer, "CONSELHEIRO_ABERTO_CUMPRIDO_POR_" + i,cumpridoPor == null ? "NENHUM" : cumpridoPor.getTabuleiroJogador() .getPersonagem().getCor().name());
            }

            // Dados dos jogadores
            for (int i = 0; i < jogadores.size(); i++) {

                Jogador jogador = jogadores.get(i);
                String prefixo = "JOGADOR_" + i + "_";

                Cor cor = jogador.getTabuleiroJogador()
                        .getPersonagem().getCor();

                escrever(writer, prefixo + "COR", cor.name());
                escrever(writer, prefixo + "YURTS",jogador.getYurtsDisponiveis());
                escrever(writer, prefixo + "VOTOS",jogador.getVotos());

                ConselheiroSecreto conselheiro = jogador.getConselheiroSecreto();

                escrever(writer, prefixo + "CONSELHEIRO",conselheiro == null? "NENHUM" : conselheiro.getTipo().name());

                escrever(writer, prefixo + "OPCOES_CONSELHEIRO",juntarConselheiros(jogador.getOpcoesConselheiro()));

                for (TipoTributo tipo : TipoTributo.values()) {
                    escrever(writer,prefixo + "TRIBUTO_" + tipo.name(),jogador.getQuantidadeTributo(tipo));
                }

                for (TipoTesouro tipo : TipoTesouro.values()) {
                    escrever(writer, prefixo + "TESOURO_" + tipo.name(),jogador.getQuantidadeTesouro(tipo));
                }

                // Posições dos peões
                escrever(writer, prefixo + "QUANTIDADE_PEOES",
                        jogador.getQuantidadePeoes());

                for (int p = 0; p < jogador.getQuantidadePeoes(); p++) {
                    Parada posicao = jogador.getPeao(p).getPosicao();

                    escrever(writer, prefixo + "PEAO_" + p, posicao == null ? "NENHUMA": posicao.getId().name());
                }

                // Melhorias do tabuleiro individual
                TabuleiroJogador tabuleiroJogador =jogador.getTabuleiroJogador();

                for (Regiao regiao : Regiao.values()) {
                    escrever(writer,prefixo + "MELHORIAS_" + regiao.name(),juntarMelhorias(tabuleiroJogador.exportarMelhoriasRegiao().get(regiao)));
                }

                escrever(writer, prefixo + "MELHORIAS_BRANCAS", juntarMelhorias( tabuleiroJogador.exportarMelhoriasBrancas()));
                
             // Estado das ativações das colunas
                escrever(writer, prefixo + "FICHAS_ATIVACAO",tabuleiroJogador.getFichasAtivacaoDisponiveis());

                for (Regiao regiao : Regiao.values()) {
                    escrever(writer,  prefixo + "COLUNA_ATIVADA_" + regiao.name(),tabuleiroJogador.colunaEstaAtivada(regiao));
                }

                escrever(writer, prefixo + "COLUNA_BRANCA_ATIVADA",tabuleiroJogador.colunaBrancaEstaAtivada());
                
             // Peças bônus do jogador
                ArrayList<TipoBonus> pecasBonus = jogador.getOrdemPecasBonus();

                escrever(writer, prefixo + "QUANTIDADE_PECAS_BONUS", pecasBonus.size());

                for (int b = 0; b < pecasBonus.size(); b++) {
                    TipoBonus tipo = pecasBonus.get(b);

                    escrever(writer, prefixo + "PECA_BONUS_" + b, tipo.name());

                    escrever(writer,prefixo + "PECA_BONUS_USADA_" + b,jogador.isPecaBonusUsada(tipo));
                }
            }

            // Estado das províncias
            for (Map.Entry<ProvinciaID, Integer> entrada :
                    tabuleiro.getCargasProvincias().entrySet()) {

                escrever(writer,"PROVINCIA_" + entrada.getKey().name(),entrada.getValue());
            }

            // Estado das cidades
            for (CidadeID id : CidadeID.values()) {

                Cidade cidade = tabuleiro.getCidade(id);

                escrever(writer,"CIDADE_" + id.name() + "_TESOUROS",juntarTesouros(cidade.getTesouros()));

                Jogador conquistador = cidade.getConquistador();

                escrever(writer,"CIDADE_" + id.name() + "_CONQUISTADOR",conquistador == null ? "NENHUM": conquistador.getTabuleiroJogador().getPersonagem().getCor().name());
            }

            // Pilhas restantes
            escrever(writer, "PILHA_CIDADES", juntarEnums(tabuleiro.getPilhaCidades()));

            escrever(writer, "PILHA_TESOUROS",juntarEnums(tabuleiro.getPilhaTesouros()));

            escrever(writer, "PILHA_MELHORIAS",juntarMelhorias(tabuleiro.getPilhaMelhorias()));

            // Melhorias disponíveis no tabuleiro principal
            escrever(writer, "MELHORIAS_TABULEIRO", juntarMelhorias(tabuleiro.getMelhorias()));

            // Khan
            escrever(writer, "KHAN",tabuleiro.getKhan() == null? "NENHUM" : tabuleiro.getKhan().name());

            // Yurts colocadas no mapa
            for (Map.Entry<ParadaID, ArrayList<Cor>> entrada :
                    tabuleiro.getYurts().entrySet()) {

                escrever(writer, "YURTS_" + entrada.getKey().name(),juntarEnums(entrada.getValue()));
            }

            // Bônus ainda presentes nas paradas
            for (Map.Entry<ParadaID, TipoBonus> entrada :
                    tabuleiro.getBonus().entrySet()) {

                escrever(writer, "BONUS_" + entrada.getKey().name(),entrada.getValue().name());
            }
        }
    }

    private static void escrever(BufferedWriter writer, String chave, Object valor)  throws IOException {

        writer.write(chave + "=" + valor);
        writer.newLine();
    }

    private static String juntarEnums(
            Iterable<? extends Enum<?>> valores) {

        StringBuilder resultado = new StringBuilder();

        for (Enum<?> valor : valores) {
            if (resultado.length() > 0) {
                resultado.append(",");
            }

            resultado.append(valor.name());
        }

        return resultado.toString();
    }

    private static String juntarTesouros(
            ArrayList<TipoTesouro> tesouros) {
        return juntarEnums(tesouros);
    }

    private static String juntarConselheiros(
            ArrayList<ConselheiroSecreto> conselheiros) {

        StringBuilder resultado = new StringBuilder();

        for (ConselheiroSecreto conselheiro : conselheiros) {
            if (resultado.length() > 0) {
                resultado.append(",");
            }

            resultado.append(conselheiro.getTipo().name());
        }

        return resultado.toString();
    }

    private static String juntarMelhorias(
            Iterable<Melhoria> melhorias) {

        StringBuilder resultado = new StringBuilder();

        for (Melhoria melhoria : melhorias) {

            if (resultado.length() > 0) {
                resultado.append(",");
            }

            if (melhoria == null) {
                resultado.append("VAZIO");
                continue;
            }

            resultado.append(melhoria.getTipo().name())
                    .append(":")
                    .append(melhoria.getRegiao() == null
                            ? "BRANCA"
                            : melhoria.getRegiao().name());
        }

        return resultado.toString();
    }
    
    
    private static Jogador restaurarJogador( HashMap<String, String> dados, Partida partida, int indice) throws IOException {

        String prefixo = "JOGADOR_" + indice + "_";

        // Recupera a cor e cria o personagem correspondente.
        Cor cor = obterEnum( obter(dados, prefixo + "COR"),Cor.class );

        Personagem personagem = partida.criarPersonagemParaRestauracao(cor);

        TabuleiroJogador tabuleiroJogador =new TabuleiroJogador(personagem);

        // Recupera as duas opções de conselheiro secreto.
        String[] tipos = obter( dados, prefixo + "OPCOES_CONSELHEIRO").split(",", -1);

        if (tipos.length != 2) {
            throw new IOException("Quantidade inválida de conselheiros do jogador " + indice );
        }

        ArrayList<ConselheiroSecreto> opcoes =new ArrayList<>();

        for (String tipo : tipos) {
            opcoes.add(new ConselheiroSecreto(obterEnum(tipo, TipoConselheiroSecreto.class) ));
        }

        Jogador jogador = new Jogador(opcoes, tabuleiroJogador);

        // Recupera os dados básicos do jogador.
        String conselheiroSalvo = obter(dados, prefixo + "CONSELHEIRO");

        TipoConselheiroSecreto conselheiro = null;

        if (!conselheiroSalvo.equals("NENHUM")) {
            conselheiro = obterEnum(conselheiroSalvo,TipoConselheiroSecreto.class );
        }

        jogador.restaurarEstado( obterInteiro(dados, prefixo + "YURTS"),obterInteiro(dados, prefixo + "VOTOS"),conselheiro );

        // Restaura os tributos.
        HashMap<TipoTributo, Integer> tributos = new HashMap<>();

        for (TipoTributo tipo : TipoTributo.values()) {
            tributos.put(tipo, obterInteiro(dados, prefixo + "TRIBUTO_" + tipo.name() ));
        }

        // Restaura os tesouros.
        HashMap<TipoTesouro, Integer> tesouros = new HashMap<>();

        for (TipoTesouro tipo : TipoTesouro.values()) {
            tesouros.put(tipo, obterInteiro(dados, prefixo + "TESOURO_" + tipo.name()));
        }

        jogador.restaurarInventarios(tributos, tesouros);

        // Restaura as peças bônus e seu estado de utilização.
        int quantidadeBonus = obterInteiro(dados, prefixo + "QUANTIDADE_PECAS_BONUS");

        if (quantidadeBonus < 0) {
        	throw new IOException("Quantidade de peças bônus inválida.");
        }

        ArrayList<TipoBonus> tiposBonus = new ArrayList<>();
        ArrayList<Boolean> bonusUsados = new ArrayList<>();

        for (int i = 0; i < quantidadeBonus; i++) {
        	tiposBonus.add(obterEnum(obter(dados, prefixo + "PECA_BONUS_" + i),TipoBonus.class));

        	bonusUsados.add(obterBooleano(dados, prefixo + "PECA_BONUS_USADA_" + i));
        }

        jogador.restaurarPecasBonus(tiposBonus, bonusUsados);

	     // Restaura as ativações das colunas.
	     HashMap<Regiao, Boolean> colunasAtivadas = new HashMap<>();

	     for (Regiao regiao : Regiao.values()) {
	         colunasAtivadas.put( regiao,obterBooleano( dados,prefixo + "COLUNA_ATIVADA_" + regiao.name())
	         );
	     }

	     tabuleiroJogador.restaurarAtivacoes(obterInteiro(dados, prefixo + "FICHAS_ATIVACAO"),colunasAtivadas,obterBooleano(dados, prefixo + "COLUNA_BRANCA_ATIVADA"));

    	// Restaura as melhorias de cada região.
    	HashMap<Regiao, ArrayList<Melhoria>> melhoriasRegiao = new HashMap<>();

    	for (Regiao regiao : Regiao.values()) {
    	    melhoriasRegiao.put(regiao,restaurarListaMelhorias(obter(dados, prefixo + "MELHORIAS_" + regiao.name())));
    	}

	    	// Restaura as melhorias da coluna branca.
    	ArrayList<Melhoria> melhoriasBrancas =restaurarListaMelhorias( obter(dados, prefixo + "MELHORIAS_BRANCAS"));

    	tabuleiroJogador.restaurarMelhorias( melhoriasRegiao, melhoriasBrancas);

    	return jogador;
	    
    }
    
    private static ArrayList<Melhoria> restaurarListaMelhorias( String valor) throws IOException {

        ArrayList<Melhoria> melhorias = new ArrayList<>();

        if (valor.isEmpty()) {
            return melhorias;
        }

        for (String item : valor.split(",", -1)) {

        	if (item.equals("VAZIO")) {
        	    melhorias.add(null);
        	    continue;
        	}

            String[] partes = item.split(":", -1);

            if (partes.length != 2) {
                throw new IOException("Melhoria inválida: " + item);
            }

            TipoMelhoria tipo = obterEnum(
                    partes[0], TipoMelhoria.class
            );

            Regiao regiao = partes[1].equals("BRANCA") ? null : obterEnum(partes[1], Regiao.class);

            melhorias.add(new Melhoria(tipo, regiao));
        }

        return melhorias;
    }
    
    //Lê a quantidade de jogadores salva, cria uma partida vazia, reconstrói cada jogador usando o método restaurarJogador e adiciona os jogadores à nova partida
    
    private static Partida restaurarJogadores( HashMap<String, String> dados) throws IOException {

        int quantidade = obterInteiro(dados, "QUANTIDADE_JOGADORES");

        if (quantidade < 2 || quantidade > 5) {
            throw new IOException("Quantidade de jogadores inválida.");
        }

        Partida partida = new Partida();

        for (int i = 0; i < quantidade; i++) {
            Jogador jogador = restaurarJogador(dados, partida, i);

            partida.adicionarJogador(jogador);
        }

        // Associa cada peão ao seu respectivo jogador.
        HashMap<Peao, Jogador> peoes = new HashMap<>();

        for (Jogador jogador : partida.getJogadores()) {
            for (Peao peao : jogador.getPeoes()) {
                peoes.put(peao, jogador);
            }
        }

        // Cria a estrutura inicial do tabuleiro principal.
        TabuleiroPrincipal tabuleiro = new TabuleiroPrincipal(peoes,new Stack<Melhoria>() );

        partida.restaurarTabuleiroPrincipal(tabuleiro);
        
        //  Restaura a pilha de cidades.
        Stack<CidadeID> cidadesSalvas = new Stack<>();

        String cidades = obter(dados, "PILHA_CIDADES");

        if (!cidades.isEmpty()) {
            for (String id : cidades.split(",", -1)) {
                cidadesSalvas.push(
                        obterEnum(id, CidadeID.class)
                );
            }
        }

        // Restaura a pilha de tesouros.
        Stack<TipoTesouro> tesourosSalvos = new Stack<>();

        String valorPilhaTesouros = obter(dados, "PILHA_TESOUROS");

        if (!valorPilhaTesouros.isEmpty()) {
            for (String tipo : valorPilhaTesouros.split(",", -1)) {
                tesourosSalvos.push( obterEnum(tipo, TipoTesouro.class));
            }
        }

        tabuleiro.restaurarPilhas(cidadesSalvas, tesourosSalvos);
        
        // Restaura as melhorias disponíveis no tabuleiro principal.
        ArrayList<Melhoria> melhoriasTabuleiro =restaurarListaMelhorias( obter(dados, "MELHORIAS_TABULEIRO") );

        // Restaura a pilha de melhorias.
        ArrayList<Melhoria> listaPilhaMelhorias = restaurarListaMelhorias(obter(dados, "PILHA_MELHORIAS"));

        Stack<Melhoria> pilhaMelhorias = new Stack<>();
        pilhaMelhorias.addAll(listaPilhaMelhorias);

        tabuleiro.restaurarMelhorias( melhoriasTabuleiro, pilhaMelhorias);
        
     // Restaura as yurts colocadas no mapa.
        HashMap<ParadaID, ArrayList<Cor>> yurtsSalvas = new HashMap<>();

        for (ParadaID id : ParadaID.values()) {
            String valor = dados.get("YURTS_" + id.name());

            if (valor != null) {
                ArrayList<Cor> cores = new ArrayList<>();

                if (!valor.isEmpty()) {
                    for (String cor : valor.split(",", -1)) {
                        cores.add(obterEnum(cor, Cor.class));
                    }
                }

                yurtsSalvas.put(id, cores);
            }
        }

        // Restaura os bônus ainda presentes nas paradas.
        HashMap<ParadaID, TipoBonus> bonusSalvos = new HashMap<>();

        for (ParadaID id : ParadaID.values()) {
            String valor = dados.get("BONUS_" + id.name());

            if (valor != null) {
                bonusSalvos.put(id, obterEnum(valor, TipoBonus.class));
            }
        }

        // Recupera a posição do Khan.
        String khanSalvo = obter(dados, "KHAN");
        KhanID khan = khanSalvo.equals("NENHUM")? null : obterEnum(khanSalvo, KhanID.class);

        // Aplica o estado recuperado ao mapa.
        tabuleiro.restaurarEstadoMapa( yurtsSalvas,bonusSalvos,khan, partida.getJogadores());
        
     // Restaura os conselheiros abertos e quem já os cumpriu.
        ArrayList<TipoConselheiroAberto> tiposConselheiros = new ArrayList<>();

        ArrayList<Jogador> cumpridosPor = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            String tipo = obter(dados, "CONSELHEIRO_ABERTO_" + i);

            tiposConselheiros.add( obterEnum(tipo, TipoConselheiroAberto.class));

            String cor = obter( dados, "CONSELHEIRO_ABERTO_CUMPRIDO_POR_" + i);

            Jogador cumpridoPor = null;

            if (!cor.equals("NENHUM")) {
                Cor corJogador = obterEnum(cor, Cor.class);

                for (Jogador jogador : partida.getJogadores()) {
                    if (jogador.getTabuleiroJogador().getPersonagem().getCor() == corJogador) {
                        cumpridoPor = jogador;
                        break;
                    }
                }

                if (cumpridoPor == null) {
                    throw new IOException("Jogador do conselheiro não encontrado.");
                }
            }

            cumpridosPor.add(cumpridoPor);
        }

        partida.restaurarConselheirosAbertos(tiposConselheiros, cumpridosPor );
        
        // Restaura o estado geral da partida.
        partida.restaurarLimiteVotos(obterInteiro(dados, "LIMITE_VOTOS"));

        partida.restaurarEstadoGeral(
            obterInteiro(dados, "JOGADOR_ATUAL"),
            obterInteiro(dados, "VOTOS_FICHA_NEUTRA"),
            obterBooleano(dados, "ENCERRADA"),
            obterBooleano(dados, "FIM_DISPARADO"),
            obterInteiro(dados, "RODADAS_EXTRAS"),
            obterInteiro(dados, "TURNOS_RESTANTES"),
            obterBooleano(dados, "PONTUACAO_FINAL_CALCULADA")
        );
        

	     // Restaura a posição dos peões de cada jogador.
	     for (int i = 0; i < quantidade; i++) {
	         Jogador jogador = partida.getJogadores().get(i);
	
	         String prefixo = "JOGADOR_" + i + "_";
	
	         int quantidadePeoes = obterInteiro( dados, prefixo + "QUANTIDADE_PEOES");
	
	         if (quantidadePeoes != jogador.getQuantidadePeoes()) {
	             throw new IOException("Quantidade de peões inválida para o jogador " + i);
	         }
	
	         for (int p = 0; p < quantidadePeoes; p++) {
	             String posicaoSalva = obter(dados, prefixo + "PEAO_" + p
	             );
	
	             if (!posicaoSalva.equals("NENHUMA")) {
	                 ParadaID id = obterEnum(posicaoSalva, ParadaID.class );
	
	                 Parada parada = tabuleiro.getParada(id);
	
	                 if (parada == null) {
	                     throw new IOException("Posição de peão inválida: " + id);
	                 }
	
	                 jogador.restaurarPosicaoPeao(p, parada);
	             }
	         }
	     }
	     
	  // Restaura as cargas das províncias.
	     for (Map.Entry<ProvinciaID, Provincia> entrada :tabuleiro.getProvincias().entrySet()) {

	         int cargas = obterInteiro( dados, "PROVINCIA_" + entrada.getKey().name());

	         entrada.getValue().restaurarCargas(cargas);
	     }
	     
	  // Restaura o estado das cidades.
	     for (Map.Entry<CidadeID, Cidade> entrada : tabuleiro.getCidades().entrySet()) {

	         CidadeID id = entrada.getKey();
	         String prefixoCidade = "CIDADE_" + id.name() + "_";

	         // Recupera os tesouros ainda disponíveis.
	         String valorTesouros = obter( dados, prefixoCidade + "TESOUROS");

	         ArrayList<TipoTesouro> tesouros = new ArrayList<>();

	         if (!valorTesouros.isEmpty()) {
	             for (String tipo : valorTesouros.split(",", -1)) {
	                 tesouros.add(obterEnum(tipo, TipoTesouro.class));
	             }
	         }

	         // Recupera o jogador que conquistou a cidade.
	         String corSalva = obter(  dados, prefixoCidade + "CONQUISTADOR");

	         Jogador conquistador = null;

	         if (!corSalva.equals("NENHUM")) {
	             Cor cor = obterEnum(corSalva, Cor.class);

	             for (Jogador jogador : partida.getJogadores()) {
	                 if (jogador.getTabuleiroJogador().getPersonagem().getCor() == cor) {
	                     conquistador = jogador;
	                     break;
	                 }
	             }

	             if (conquistador == null) {
	                 throw new IOException(
	                         "Conquistador não encontrado para a cidade " + id
	                 );
	             }
	         }

	         entrada.getValue().restaurarEstado(tesouros, conquistador);
	     }

	     return partida;

       
    }
    
    private static HashMap<String, String> lerDados(File arquivo) throws IOException {

        if (arquivo == null || !arquivo.isFile()) {
            throw new IllegalArgumentException("Arquivo de partida inválido.");
        }

        HashMap<String, String> dados = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int numeroLinha = 0;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;

                if (linha.trim().isEmpty()) {
                    continue;
                }

                int separador = linha.indexOf('=');

                if (separador <= 0) {
                    throw new IOException( "Linha inválida no arquivo: " + numeroLinha );
                }

                String chave = linha.substring(0, separador).trim();
                String valor = linha.substring(separador + 1).trim();

                if (dados.containsKey(chave)) {
                    throw new IOException("Chave duplicada: " + chave);
                }

                dados.put(chave, valor);
            }
        }

        if (!"1".equals(dados.get("VERSAO"))) {
            throw new IOException("Versão do arquivo incompatível.");
        }

        return dados;
    }

    private static String obter(HashMap<String, String> dados, String chave) throws IOException {

        String valor = dados.get(chave);

        if (valor == null) {
            throw new IOException("Campo ausente no arquivo: " + chave);
        }

        return valor;
    }

    private static int obterInteiro( HashMap<String, String> dados, String chave) throws IOException {

        try {
            return Integer.parseInt(obter(dados, chave));
        } catch (NumberFormatException e) {
            throw new IOException("Valor inteiro inválido: " + chave, e);
        }
    }

    private static boolean obterBooleano(HashMap<String, String> dados, String chave) throws IOException {

        String valor = obter(dados, chave);

        if (!valor.equals("true") && !valor.equals("false")) {
            throw new IOException("Valor booleano inválido: " + chave);
        }

        return Boolean.parseBoolean(valor);
    }

    private static <T extends Enum<T>> T obterEnum(String valor, Class<T> tipo) throws IOException {

        try {
            return Enum.valueOf(tipo, valor);
        } catch (IllegalArgumentException e) {
            throw new IOException("Valor inválido para " + tipo.getSimpleName() + ": " + valor, e);
        }
    }
    
    Partida carregarPartida(File arquivo) throws IOException {
        HashMap<String, String> dados = lerDados(arquivo);
        return restaurarJogadores(dados);
    }
    
}

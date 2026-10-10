# Projeto-POO-2026.2

# Herdeiros de Khan — Model

## Classes implementadas

### `Melhoria`
Representa uma melhoria que pode ser adquirida e utilizada durante a partida.

A classe armazena:
- `TipoMelhoria`: tipo da melhoria.
- `CorMelhoria`: cor associada à melhoria.
- `TipoTesouro`: tipo de tesouro virtual, quando aplicável.
- `EstadoMelhoria`: estado atual da melhoria.

Os estados definidos são:
- `RECEM_COMPRADA`: melhoria comprada no turno atual, que ainda não pode ser utilizada.
- `PRONTA`: melhoria disponível para utilização.
- `USADA`: melhoria que já foi utilizada.

A classe possui métodos para consultar seus atributos, verificar se pode ser utilizada, liberar sua utilização e marcar seu uso.

### `PecaBonus`
Representa uma peça de bônus do jogo.

Armazena o tipo de bônus e se a peça já foi utilizada.

### `CartaConselheiro`
Classe abstrata que representa uma carta de conselheiro. Define o método `getNome()`, implementado pelas classes concretas.

### `ConselheiroAberto`
Representa uma carta de conselheiro aberto.

Armazena o tipo da carta e registra o jogador que a cumpriu. O gerenciamento da coleção de cartas e a atribuição do voto devem ser realizados pela classe responsável pelo controle da partida.

### `ConselheiroSecreto`
Representa uma carta de conselheiro secreto.

Armazena o tipo do conselheiro e permite consultar sua identificação.

## Enum

- `TipoMelhoria`: tipos de melhorias disponíveis no jogo.
- `CorMelhoria`: cores das melhorias.
- `EstadoMelhoria`: estados possíveis de uma melhoria.
- `TipoTesouro`: tipos de tesouro.
- `TipoBonus`: tipos de bônus.
- `TipoConselheiroAberto`: tipos de conselheiros abertos.
- `TipoConselheiroSecreto`: tipos de conselheiros secretos.


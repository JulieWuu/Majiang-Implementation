package Player;

import Game.Tile;

import java.util.Optional;

public interface Strategy {
    void assignPlayer(Player player);

    Optional<Tile[]> inspect(Tile drawnTile);

    Tile pick(Tile[] tilesInPool);

    Optional<Tile[]> flag(Tile freeTile);
}

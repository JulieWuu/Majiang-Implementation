package Player;

import Game.Tile;

import java.util.Optional;

public class StrategyHongKong implements Strategy {
    private Player player;

    @Override
    public void assignPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Optional<Tile[]> inspect(Tile drawnTile) {
        return Optional.empty();
    }

    @Override
    public Tile pick(Tile[] tilesInPool) {
        return this.player.getCurrentTiles()[(int) (Math.random() * this.player.getCurrentTiles().length)];
    }

    @Override
    public Optional<Tile[]> flag(Tile freeTile) {
        return Optional.empty();
    }
}

package Player;

import java.util.*;

import Game.Tile;
import Game.TileSet;
import Utility.TypeConversion;

public class Player {

    // tiles on hand
    private final ArrayList<Tile> currentTiles;
    // flowers and tiles used to Eat or Pong
    private final ArrayList<Tile> publicTiles = new ArrayList<>();
    // AmGang tiles indicator - one instance for each AmGang
    private final ArrayList<Tile> amGangTiles = new ArrayList<>();

    private final Strategy strategy;
    private int position;

    public boolean Pong = false;
    public boolean Eat = false;
    public boolean Gang = false;
    public boolean Hu = false;

    // for QuanZhou style
    public boolean IdleKing = false;        // auto-checked and flagged
    public boolean DoubleIdle = false;
    public boolean TripleKings = false;

    public Player(Tile[] initialTiles, Strategy strategy) {
        this.currentTiles = new ArrayList<>();
        Collections.addAll(this.currentTiles, initialTiles);
        this.strategy = strategy;
        this.strategy.assignPlayer(this);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public Tile[] getCurrentTiles() {
        return TileSet.sorted(TypeConversion.toArray(this.currentTiles));
    }

    public Tile[] getPublicTiles() {
        return TypeConversion.toArray(this.publicTiles);
    }

    public Tile[] getAmGangTiles() {
        return TypeConversion.toArray(this.amGangTiles);
    }

    public Optional<Tile[]> getInspected(Tile drawnTile) {
        return this.strategy.inspect(drawnTile);
    }

    public Tile getPicked(Tile[] tilesInPool) {
        return this.strategy.pick(tilesInPool);
    }

    public Optional<Tile[]> getFlagged(Tile freeTile) {
        return this.strategy.flag(freeTile);
    }

    public void addTile(Tile t) {
        this.currentTiles.add(t);
    }

    public void removeTile(Tile t) {
        this.currentTiles.remove(t);
    }

    public void publicise(Tile t) {
        this.publicTiles.add(t);
    }

    public void registerAmGang(Tile t) {
        this.amGangTiles.add(t);
    }
}

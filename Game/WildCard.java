package Game;

public class WildCard extends Tile {

    private final Tile tile;

    protected WildCard(Tile t) {
        // type and value both 0 for King specifically
        super(0, 0);
        // can access the original tile
        this.tile = t;
    }

    public Tile getOriginal() {
        return this.tile;
    }

    @Override
    public String toString() {
        return this.tile.toString();
    }
}

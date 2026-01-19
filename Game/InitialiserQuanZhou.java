package Game;

import Player.Player;
import Utility.TypeConversion;

import java.util.ArrayList;

public class InitialiserQuanZhou implements Initialiser {

    protected final int playerPosition = (int) (Math.random() * 4); // 0-3

    private static Tile[] tileSet;
    private final Tile wildCard;

    protected InitialiserQuanZhou() {
        tileSet = TileSet.getTiles();
        this.wildCard = TileSet.draw(tileSet);
    }

    public void setWildCard() {
        System.out.println("The King tile is " + this.wildCard);

        ArrayList<Tile> intermediateTiles = TypeConversion.toAList(tileSet);
        // only 3 Kings available for all players
        intermediateTiles.remove(this.wildCard);
        tileSet = TypeConversion.toArray(intermediateTiles);

        if (this.wildCard.getType() == 2) {
            if (this.wildCard.getValue() <= 4) {
                for (int i = 136; i < 143; i++) {
                    if (tileSet[i].getValue() <= 4) {
                        tileSet[i] = new WildCard(tileSet[i]);
                    }
                }
            }
            else {
                for (int i = 136; i < 143; i++) {
                    if (tileSet[i].getValue() > 4) {
                        tileSet[i] = new WildCard(tileSet[i]);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < 143; i++) {
                if (TileSet.equivalent(this.wildCard, tileSet[i])) {
                    tileSet[i] = new WildCard(tileSet[i]);
                }
            }
        }
    }

    public Tile getWildCard() {
        return this.wildCard;
    }

    /// only for testing purposes
    public Tile[] getTileSet() {
        return tileSet;
    }

    @Override
    public int getPlayerPosition() {
        return this.playerPosition;
    }

    @Override
    public Tile[][] shuffle() {
        Tile[][] shuffledSets = new Tile[5][16];

        Tile[][] iteration1 = TileSet.extract(tileSet, 16);
        shuffledSets[0] = TileSet.sorted(iteration1[0]);    // player 1 set

        Tile[][] iteration2 = TileSet.extract(iteration1[1], 16);
        shuffledSets[1] = TileSet.sorted(iteration2[0]);    // player 2 set

        Tile[][] iteration3 = TileSet.extract(iteration2[1], 16);
        shuffledSets[2] = TileSet.sorted(iteration3[0]);    // player 3 set

        Tile[][] iteration4 = TileSet.extract(iteration3[1], 16);
        shuffledSets[3] = TileSet.sorted(iteration4[0]);    // player 4 set

        shuffledSets[4] = TileSet.sorted(iteration4[1]);    // rest of the tileset

        return shuffledSets;
    }

    @Override
    public boolean checkHu(Player player) {
        return TileSet.checkHu(player.getCurrentTiles());
    }
}

package Game;

import Player.Player;
import Utility.TypeConversion;

import java.util.ArrayList;
import java.util.Arrays;

public class InitialiserHongKong implements Initialiser {

    protected final int playerPosition = (int) (Math.random() * 4); // 0-3

    @Override
    public int getPlayerPosition() {
        return this.playerPosition;
    }

    @Override
    public Tile[][] shuffle() {
        Tile[][] shuffledSets = new Tile[5][13];

        Tile[][] iteration1 = TileSet.extract(TileSet.getTiles(), 13);
        shuffledSets[0] = TileSet.sorted(iteration1[0]);    // player 1 set

        Tile[][] iteration2 = TileSet.extract(iteration1[1], 13);
        shuffledSets[1] = TileSet.sorted(iteration2[0]);    // player 2 set

        Tile[][] iteration3 = TileSet.extract(iteration2[1], 13);
        shuffledSets[2] = TileSet.sorted(iteration3[0]);    // player 3 set

        Tile[][] iteration4 = TileSet.extract(iteration3[1], 13);
        shuffledSets[3] = TileSet.sorted(iteration4[0]);    // player 4 set

        shuffledSets[4] = TileSet.sorted(iteration4[1]);    // rest of the tileset

        return shuffledSets;
    }

    @Override
    // only check private tiles of player
    public boolean checkHu(Player player) {
        Tile[] privateTiles = player.getCurrentTiles();
        Tile[] publicTiles = player.getPublicTiles();

        // +4 ensures positivity (4-7), -playerPosition calculates differences, %4 casts down (0-3), +1 normalises (1-4)
        int position = (player.getPosition() + 4 - playerPosition) % 4 + 1;

        if (TileSet.check13Yao(privateTiles)) {
            return true;
        }
        else {
            if (TileSet.checkHu(privateTiles)) {
                ArrayList<Tile> totalTiles = TypeConversion.toAList(privateTiles);
                totalTiles.addAll(TypeConversion.toAList(publicTiles));

                return getFan(TypeConversion.toArray(totalTiles), player.getAmGangTiles(), position) >= 3;
            }
            else {
                return false;
            }
        }
    }

    // should only be used on Hu sets! throws UnsupportedOperationException unchecked
    // checks both private and public tiles of player
    private int getFan(Tile[] tiles, Tile[] amGangTiles, int position) {
        int Fan = 0;
        Tile[] sortedTiles = TileSet.sorted(tiles);
        System.out.println("Sorted tiles: " + Arrays.toString(sortedTiles));

        for (Tile t : amGangTiles) {
            if ((t.getType() == 1 && t.getValue() == position)
                || (t.getType() == 2 && t.getValue() % 4 == position)) {
                Fan++;
            }
        }

        // first while (true) deals with characters, loop until there are no more characters
        while (true) {
            if (sortedTiles[0].getType() == 1) {

                // Gang should be the first to check because Pong is a subset of Gang
                if (TileSet.checkGang(sortedTiles[0], sortedTiles[1], sortedTiles[2], sortedTiles[3])) {
                    // same position: Fan + 1, else remove the Gang
                    if (sortedTiles[0].getValue() == position) {
                        Fan++;
                    }

                    ArrayList<Tile> newTiles = TypeConversion.toAList(sortedTiles);
                    for (int i = 0; i < 4; i++) {
                        newTiles.remove(sortedTiles[i]);
                    }
                    sortedTiles = TypeConversion.toArray(newTiles);

                } else if (TileSet.checkPong(sortedTiles[0], sortedTiles[1], sortedTiles[2])) {
                    // same position: Fan + 1, else remove the Pong
                    if (sortedTiles[0].getValue() == position) {
                        Fan++;
                    }

                    ArrayList<Tile> newTiles = TypeConversion.toAList(sortedTiles);
                    for (int i = 0; i < 3; i++) {
                        newTiles.remove(sortedTiles[i]);
                    }
                    sortedTiles = TypeConversion.toArray(newTiles);

                } else if (sortedTiles[0].getValue() == sortedTiles[1].getValue()) {
                    ArrayList<Tile> newTiles = TypeConversion.toAList(sortedTiles);
                    for (int i = 0; i < 2; i++) {
                        newTiles.remove(sortedTiles[i]);
                    }
                    sortedTiles = TypeConversion.toArray(newTiles);

                } else
                    throw new UnsupportedOperationException("Fan cannot be calculated: given set has unpaired Character.");
            }
            else {
                break;
            }
        }

        // second while (true) deals with flowers, loop until there are no more flowers
        while (true) {
            if (sortedTiles[0].getType() == 2) {
                ArrayList<Tile> newTiles = TypeConversion.toAList(sortedTiles);

                for (Tile t : sortedTiles) {
                    if (t.getType() == 2) {

                        // clear out all flowers
                        newTiles.remove(t);

                        // +3 (or -1) to (4-7), %4 becomes (0-3), +1 normalises to (1-4)
                        if ((t.getValue() + 3) % 4 + 1 == position) {
                            Fan++;
                        }
                    }
                }
                sortedTiles = TypeConversion.toArray(newTiles);
            }
            else {
                break;
            }
        }

        // at this point only S/T/W tiles are left
        int type = sortedTiles[0].getType();
        boolean agree = true;

        for (Tile t : amGangTiles) {
            if (!(t.getType() == type)) {
                agree = false;
                break;
            }
        }

        if (agree) {
            for (Tile t : sortedTiles) {
                if (!(t.getType() == type)) {
                    agree = false;
                    break;
                }
            }
        }

        // if all tiles are of the same type, Fan += 3
        if (agree) {
            Fan += 3;
        }

        // if not, check for DuiDuiHu
        if (!agree) {
            boolean DuiDuiHu = true;
            boolean hasPair = false;

            int index = 0;

            // while (true) loop through all tiles and check if they all make Pongs and one Pair
            while (true) {
                // has already checked every tile
                if (index >= sortedTiles.length) {
                    break;
                }
                else {
                    // need to check Pong first because Pair is a subset of Pong
                    if (TileSet.checkPong(sortedTiles[index], sortedTiles[index + 1], sortedTiles[index + 2])) {
                        // skip the next 2 tiles because they already form a Pong
                        index += 3;
                    } else {
                        if (!hasPair && TileSet.equivalent(sortedTiles[index], sortedTiles[index + 1])) {
                            hasPair = true;
                            index += 2;
                        } else {
                            // either there is already a Pair or the tiles do not form a Pair: this is not DuiDuiHu
                            DuiDuiHu = false;
                            break;
                        }
                    }
                }
            }

            if (DuiDuiHu) {
                Fan += 3;
            }
        }

        System.out.println("Fan: " + Fan);
        return Fan;
    }
}

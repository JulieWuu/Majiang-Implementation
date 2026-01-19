package Game;

import Utility.TypeConversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class TileSet {
    public static TileSet tileSet = new TileSet();

    // array of all tiles
    private final Tile[] tiles;

    private TileSet() {
        this.tiles = new Tile[144];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 9; j++) {
                if (i < 4) {
                    this.tiles[9 * i + j] = new Tile(3, j + 1);
                } else if (i < 8) {
                    this.tiles[9 * i + j] = new Tile(4, j + 1);
                } else {
                    this.tiles[9 * i + j] = new Tile(5, j + 1);
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 7; j++) {
                this.tiles[108 + 7 * i + j] = new Tile(1, j + 1);
            }
        }
        for (int i = 0; i < 8; i++) {
            this.tiles[136 + i] = new Tile(2, i + 1);
        }
    }

    public static Tile[] getTiles() {
        return tileSet.tiles;
    }

    public static TileSet getTileSet() {
        return tileSet;
    }

    public static Tile[][] extract(Tile[] set, int num) {
        Tile[][] result = {new Tile[0], new Tile[0]};
        if (num > set.length) {
            System.out.println("Failure occurred: trying to extract " + (num - set.length) + " more tiles than there are available.");
        } else {
            result[0] = new Tile[num];
            result[1] = new Tile[set.length - num];
            Set<Integer> indices = new Random().ints(0, set.length)
                    .distinct().limit(num).boxed().collect(Collectors.toSet());

            int extractedCount = 0;
            int restCount = 0;
            for (int i = 0; i < set.length; i++) {
                if (indices.contains(i)) {
                    result[0][extractedCount] = set[i];
                    extractedCount++;
                } else {
                    result[1][restCount] = set[i];
                    restCount++;
                }
            }
        }
        return result;
    }

    public static Tile draw(Tile[] tiles) {
        return tiles[(int) (Math.random() * (tiles.length))];
    }

    public static Tile[] sorted(Tile[] tiles) {
        Arrays.sort(tiles);
        return tiles;
    }

    public static boolean equivalent(Tile t1, Tile t2) {
        return t1.getType() == t2.getType() && t1.getValue() == t2.getValue();
    }

    public static boolean checkPong(Tile t1, Tile t2, Tile t3) {
        if (t1.isKing()) {
            return equivalent(t2, t3);
        }
        else if (t2.isKing()) {
            return equivalent(t1, t3);
        }
        else if (t3.isKing()) {
            return equivalent(t1, t2);
        }
        else {
            return equivalent(t1, t2) && equivalent(t2, t3);
        }
    }

    public static boolean checkGang(Tile t1, Tile t2, Tile t3, Tile t4) {
        return equivalent(t1, t2) && equivalent(t2, t3) && equivalent(t3, t4);
    }

    public static boolean checkEat(Tile t1, Tile t2, Tile t3) {
        Tile[] sortedTiles = sorted(new Tile[]{t1, t2, t3});
        // King will always be sorted to the front
        if (sortedTiles[0].isKing()) {
            return sortedTiles[1].getType() == sortedTiles[2].getType()
                    && (sortedTiles[1].getValue() + 1 == sortedTiles[2].getValue() ||
                        sortedTiles[1].getValue() + 2 == sortedTiles[2].getValue());
        }
        else {
            // exclude characters and flowers
            return !(sortedTiles[0].getType() == 1 || sortedTiles[0].getType() == 2)
                    && (sortedTiles[0].getType() == sortedTiles[1].getType() && sortedTiles[1].getType() == sortedTiles[2].getType())
                    && (sortedTiles[0].getValue() + 1 == sortedTiles[1].getValue() && sortedTiles[1].getValue() + 1 == sortedTiles[2].getValue());
        }
    }

    public static boolean check13Yao(Tile[] tiles) {
        ArrayList<Tile> sortedTiles = TypeConversion.toAList(sorted(tiles));

        for (int i = 0; i < tiles.length - 1; i++) {
            if (tiles[i].getType() == tiles[i+1].getType() && tiles[i].getValue() == tiles[i+1].getValue()) {
                sortedTiles.remove(tiles[i]);
                break;
            }
        }

        Tile[] fixedTiles = TypeConversion.toArray(sortedTiles);
        return (fixedTiles.length == 13
                && (fixedTiles[0].getType() == 1 && fixedTiles[0].getValue() == 1)
                && (fixedTiles[1].getType() == 1 && fixedTiles[1].getValue() == 2)
                && (fixedTiles[2].getType() == 1 && fixedTiles[2].getValue() == 3)
                && (fixedTiles[3].getType() == 1 && fixedTiles[3].getValue() == 4)
                && (fixedTiles[4].getType() == 1 && fixedTiles[4].getValue() == 5)
                && (fixedTiles[5].getType() == 1 && fixedTiles[5].getValue() == 6)
                && (fixedTiles[6].getType() == 1 && fixedTiles[6].getValue() == 7)
                && (fixedTiles[7].getType() == 4 && fixedTiles[7].getValue() == 1)
                && (fixedTiles[8].getType() == 4 && fixedTiles[8].getValue() == 9)
                && (fixedTiles[9].getType() == 5 && fixedTiles[9].getValue() == 1)
                && (fixedTiles[10].getType() == 5 && fixedTiles[10].getValue() == 9)
                && (fixedTiles[11].getType() == 3 && fixedTiles[11].getValue() == 1)
                && (fixedTiles[12].getType() == 3 && fixedTiles[12].getValue() == 9));
    }

    public static boolean checkHu(Tile[] tiles) {
        // tiles contain only private tiles of the player
        Tile[] sortedTiles = sorted(tiles);

        for (Tile t : sortedTiles) {
            // if any flowers are present, it is not a Hu set
            if (t.getType() == 2) {
                return false;
            }
        }

        // can treat characters the same way as W/S/T
        boolean Hu = false;
        for (Tile[] potentialPair : reduce3pattern(sortedTiles)) {
            if (potentialPair.length == 2) {
                if (potentialPair[0].isKing() || potentialPair[1].isKing()) {
                    Hu = true;
                    break;
                }
                else if (equivalent(potentialPair[0], potentialPair[1])) {
                    Hu = true;
                    break;
                }
            }
        }
        return Hu;
    }

    // check on normal on-hand set (after playing one tile)
    public static boolean checkIdleKing(Tile[] tiles) {
        Tile[] sortedTiles = sorted(tiles);
        if (sortedTiles[0].isKing()) {
            ArrayList<Tile> copyTiles = TypeConversion.toAList(sortedTiles);
            copyTiles.remove(sortedTiles[0]);
            for (Tile[] restTiles : reduce3pattern(TypeConversion.toArray(copyTiles))) {
                if (restTiles.length == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // check on already-Hued set (after taking in one tile)
    public static boolean checkDoubleIdle(Tile[] tiles) {
        Tile[] sortedTiles = sorted(tiles);
        if (sortedTiles[0].isKing() && sortedTiles[1].isKing()) {
            ArrayList<Tile> copyTiles = TypeConversion.toAList(sortedTiles);
            copyTiles.remove(sortedTiles[0]);
            copyTiles.remove(sortedTiles[1]);
            for (Tile[] restTiles : reduce3pattern(TypeConversion.toArray(copyTiles))) {
                if (restTiles.length == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkTripleKings(Tile[] tiles) {
        Tile[] sortedTiles = sorted(tiles);
        return (sortedTiles[0].isKing() && sortedTiles[1].isKing() && sortedTiles[2].isKing());
    }

    // for W/S/T, check both Pong and Eat patterns
    private static ArrayList<Tile[]> reduce3pattern(Tile[] tiles) {
        ArrayList<Tile[]> candidates = new ArrayList<>();

        boolean update = false;

        // for every three different tiles: if Eat or Pong, generate reduced tiles with them being removed
        for (int i = 0; i < tiles.length - 2; i++) {
            for (int j = i + 1; j < tiles.length - 1; j++) {
                for (int k = j + 1; k < tiles.length; k++) {
                    if ((checkPong(tiles[i], tiles[j], tiles[k])) ||
                            (checkEat(tiles[i], tiles[j], tiles[k]))) {
                        update = true;
                        ArrayList<Tile> restTiles = TypeConversion.toAList(tiles);
                        restTiles.remove(tiles[i]);
                        restTiles.remove(tiles[j]);
                        restTiles.remove(tiles[k]);
                        candidates.addAll(reduce3pattern(TypeConversion.toArray(restTiles)));
                    }
                }
            }
        }
        if (!update) {
            candidates.add(tiles);
        }

        // return the sets with equal minimum length
        int minLength = tiles.length;
        ArrayList<Tile[]> result = new ArrayList<>();
        for (Tile[] candidate : candidates) {
            if (candidate.length < minLength) {
                result.clear();
                result.add(candidate);
                minLength = candidate.length;
            }
            else if (candidate.length == minLength) {
                result.add(candidate);
            }
        }
        return result;
    }
}

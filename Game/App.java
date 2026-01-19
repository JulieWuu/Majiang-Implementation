package Game;

import Player.Player;
import Player.StrategyHuman;
import Utility.TypeConversion;

import java.util.ArrayList;

public class App {
    static void main(String[] args) {
        if (args.length == 0) {
            Game game = new Game(new InitialiserHongKong());
            game.run();
        }

        else {
            if (args[0].equals("testQZ")) {
                InitialiserQuanZhou initialiser = new InitialiserQuanZhou();
                initialiser.setWildCard();

                Tile[] privateTiles = new Tile[13];
                Tile[] set = initialiser.getTileSet();
                privateTiles[0] = set[1];
                privateTiles[1] = set[2];
                privateTiles[2] = set[3];
                privateTiles[3] = set[112];
                privateTiles[4] = set[119];
                privateTiles[5] = set[126];
                privateTiles[6] = set[47];
                privateTiles[7] = set[26];
                privateTiles[8] = set[56];
                privateTiles[9] = set[57];
                privateTiles[10] = set[58];
                privateTiles[11] = set[110];
                privateTiles[12] = set[117];

                Player testPlayer = new Player(privateTiles, new StrategyHuman());
                testPlayer.setPosition(1);

                testPlayer.publicise(set[118]);
                testPlayer.publicise(set[125]);
                testPlayer.publicise(set[132]);
                testPlayer.publicise(set[140]);

                ArrayList<Tile> totalTiles = TypeConversion.toAList(privateTiles);
                totalTiles.addAll(TypeConversion.toAList(testPlayer.getPublicTiles()));

                System.out.println("InitialiserPosition = " + initialiser.playerPosition);
                System.out.println("Player position = " + testPlayer.getPosition());
                System.out.println("Total tiles: " + totalTiles);
                System.out.println("IdleKing = " + TileSet.checkIdleKing(testPlayer.getCurrentTiles()));
            }

            else if (args[0].equals("testHK")) {
                InitialiserHongKong initialiser = new InitialiserHongKong();

                Tile[] privateTiles = new Tile[11];
                Tile[] set = TileSet.getTiles();
                privateTiles[0] = set[1];
                privateTiles[1] = set[2];
                privateTiles[2] = set[3];
                privateTiles[3] = set[112];
                privateTiles[4] = set[119];
                privateTiles[5] = set[81];
                privateTiles[6] = set[90];
                privateTiles[7] = set[99];
                privateTiles[8] = set[56];
                privateTiles[9] = set[57];
                privateTiles[10] = set[58];

                Player testPlayer = new Player(privateTiles, new StrategyHuman());
                testPlayer.setPosition(1);

                testPlayer.publicise(set[118]);
                testPlayer.publicise(set[125]);
                testPlayer.publicise(set[132]);
                testPlayer.publicise(set[140]);

                ArrayList<Tile> totalTiles = TypeConversion.toAList(privateTiles);
                totalTiles.addAll(TypeConversion.toAList(testPlayer.getPublicTiles()));

                System.out.println("InitialiserPosition = " + initialiser.playerPosition);
                System.out.println("Player position = " + testPlayer.getPosition());
                System.out.println("Total tiles: " + totalTiles);
                System.out.println("Hu = " + initialiser.checkHu(testPlayer));
            }

        }
    }
}

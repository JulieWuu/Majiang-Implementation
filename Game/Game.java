package Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.LinkedHashMap;

import Player.Player;
import Player.StrategyHuman;
import Player.StrategyHongKong;
import Utility.TypeConversion;

public class Game {
    private boolean gameOver = false;
    private boolean lastRound = false;      // for QuanZhou style DoubleIdle and last round
    private final Initialiser initialiser;

    private int counter = 0;    // the position of player who has just played a tile, 0-3, same as players index
    private final Player[] players = new Player[4];
    private ArrayList<Tile> newTiles = new ArrayList<>();
    private final ArrayList<Tile> tilesInPool = new ArrayList<>();

    public Game(Initialiser initialiser) {
        this.initialiser = initialiser;
    }

    private void initialise() {
        this.counter = this.initialiser.getPlayerPosition();

        if (this.initialiser instanceof InitialiserQuanZhou) {
            ((InitialiserQuanZhou) this.initialiser).setWildCard();
        }

        Tile[][] shuffledSets = this.initialiser.shuffle();

        for (int i = 0; i < 4; i++) {
            this.players[i] = new Player(shuffledSets[i], new StrategyHuman());
            this.players[i].setPosition(i);     // positions are 0-3
        }

        this.newTiles = TypeConversion.toAList(shuffledSets[4]);

        for (Player player : this.players) {
            supply(player);
            System.out.println("Player " + player.getPosition() + ": " + Arrays.toString(player.getCurrentTiles()));
        }
    }

    // at the start of round, check AmGang and get supply or claim Hu on a new tile, or continue with normal playing
    private void inspect(Player player) {
        if (newTiles.isEmpty()) {
            System.out.println("\n\nYou have run out of new tiles, this game is a tie.");
            gameOver = true;
        }

        else {
            Tile drawnTile = TileSet.draw(TypeConversion.toArray(newTiles));
            newTiles.remove(drawnTile);

            if (drawnTile.getType() == 2) {
                System.out.println("Player " + player.getPosition() + " has flower " + drawnTile);
                player.publicise(drawnTile);
                System.out.println("System has replaced 1 flower for player " + player.getPosition());
                inspect(player);
            }
            else {
                Optional<Tile[]> inspection = player.getInspected(drawnTile);
                if (player.DoubleIdle) {
                    player.addTile(drawnTile);
                    System.out.println("\nPlayer " + player.getPosition() + " has won by Double Idle!");
                    System.out.println(Arrays.toString(player.getCurrentTiles()));
                    this.gameOver = true;
                }
                if (player.TripleKings) {
                    player.addTile(drawnTile);
                    if (TileSet.checkTripleKings(player.getCurrentTiles())) {
                        System.out.println("\nPlayer " + player.getPosition() + " has won by Triple Kings!");
                        System.out.println(Arrays.toString(player.getCurrentTiles()));
                        this.gameOver = true;
                    }
                    else {
                        System.out.println("\nPlayer " + player.getPosition() + " claimed that they have Triple Kings, but they do not. They shall face harsh punishment.");
                        player.TripleKings = false;
                    }
                }
                else if (player.Hu) {
                    player.addTile(drawnTile);
                    if (player.IdleKing) {
                        System.out.println("\nPlayer " + player.getPosition() + " has won by Idle King!");
                        System.out.println(Arrays.toString(player.getCurrentTiles()));
                        this.gameOver = true;
                    }
                    else {
                        if (this.initialiser.checkHu(player)) {
                            System.out.println("\nPlayer " + player.getPosition() + " has won!");
                            System.out.println(Arrays.toString(player.getCurrentTiles()));
                            this.gameOver = true;
                        } else {
                            System.out.println("\nPlayer " + player.getPosition() + " claimed that they have won, but they did not. They shall face harsh punishment.");
                            player.Hu = false;
                        }
                    }
                }
                else {
                    if (inspection.isPresent()) {
                        // extra Gang, Tile[1]
                        if (inspection.get().length == 1) {
                            player.publicise(drawnTile);
                            inspect(player);
                        }

                        // AmGang, Tile[4]
                        else {
                            // only one instance is needed for one AmGang
                            player.registerAmGang(inspection.get()[0]);

                            // removing tiles are independent of adding drawn tile: can AmGang 4 old tiles
                            player.addTile(drawnTile);
                            for (Tile t : inspection.get()) {
                                player.removeTile(t);
                            }
                            player.Gang = false;
                            inspect(player);
                        }
                    }
                    // no Hu flag, no inspection result (for AmGang), indicates normal playing
                    else {
                        player.addTile(drawnTile);
                    }
                }
            }
        }
    }

    // replace Flowers with normal tiles
    private void supply(Player player) {
        int flowerCount = 0;
        for (Tile t : player.getCurrentTiles()) {
            if (t.getType() == 2) {
                System.out.println("Player " + player.getPosition() + " has flower " + t);
                player.publicise(t);
                player.removeTile(t);
                flowerCount++;

                Tile supply = TileSet.draw(TypeConversion.toArray(newTiles));
                player.addTile(supply);
                newTiles.remove(supply);
            }
        }
        if (flowerCount > 0) {
            System.out.println("System has replaced " + flowerCount + " flower(s) for player " + player.getPosition());
            supply(player);
        }
    }

    // no drawing of new tiles, play a tile from your current tile set
    private Tile play (Player player) {
        Tile pickedTile = player.getPicked(TypeConversion.toArray(tilesInPool));
        player.removeTile(pickedTile);
        tilesInPool.add(pickedTile);

        if (pickedTile.isKing()) {
            if (player.DoubleIdle) {
                System.out.println("Player " + player.getPosition() + " has entered Double Idle state. This will be the last round of game.");
                System.out.println("\n\n\nPlayer " + player.getPosition() + " has played: " + pickedTile);
                return pickedTile;
            }
            else {
                System.out.println("\n\n\nPlayer " + player.getPosition() + " has played the King by accident! " + pickedTile);
                return ((WildCard) pickedTile).getOriginal();
            }
        }
        else {
            if (TileSet.checkIdleKing(player.getCurrentTiles())) {
                System.out.println("Your set of tiles satisfies the Idle King state, you can Hu when you draw the next tile.");
                player.IdleKing = true;
            }
            else {
                player.IdleKing = false;
            }
            System.out.println("\n\n\nPlayer " + player.getPosition() + " has played: " + pickedTile);
            return pickedTile;
        }
    }

    // gathers all flags to a LinkedHashMap, allocate to Eat/Pong/Gang/Hu, marks the end of round
    private void flagAllocate(Tile freeTile) {

        // LinkedHashMap maintains insertion order: always check earlier players first
        LinkedHashMap<Integer, Optional<Tile[]>> flagMap = new LinkedHashMap<>();
        for (int i = 0; i < 3; i++) {
            // System.out.println("\n\nYou are Player " + (this.counter + i + 1) % 4 + ".");
            flagMap.put((this.counter + i + 1) % 4, this.players[(this.counter + i + 1) % 4].getFlagged(freeTile));
        }

        boolean eatRequest = false;

        // check for Hu flags - priority over any other flags
        for (int pos : flagMap.keySet()) {
            Player player = this.players[pos];

            if (player.Hu) {
                player.addTile(freeTile);
                if (this.initialiser.checkHu(player)) {
                    System.out.println("\nPlayer " + player.getPosition() + " has won! They have tiles:\n");
                    System.out.println(Arrays.toString(player.getCurrentTiles()));
                    this.gameOver = true;
                } else {
                    player.removeTile(freeTile);
                    System.out.println("\nPlayer " + player.getPosition() + " claimed that they have won, but they did not. They shall face harsh punishment.");
                    player.Hu = false;
                }
            }
        }

        // check for Gang/Pong or Eat flags, the first two take immediate effects
        for (int pos : flagMap.keySet()) {
            Player player = this.players[pos];

            if (flagMap.get(pos).isPresent()) {
                if (player.Gang) {
                    Tile[] targetedTiles = flagMap.get(pos).get();
                    player.publicise(freeTile);
                    tilesInPool.remove(freeTile);
                    for (Tile t : targetedTiles) {
                        player.publicise(t);
                        player.removeTile(t);
                    }
                    System.out.println("\n\nPlayer " + player.getPosition() + " has Ganged " + freeTile + ".\n\n");
                    eatRequest = false;
                    player.Gang = false;
                    this.counter = pos;
                    inspect(player);
                    flagAllocate(play(player));
                    break;
                }
                else if (player.Pong) {
                    Tile[] targetedTiles = flagMap.get(pos).get();
                    player.publicise(freeTile);
                    tilesInPool.remove(freeTile);
                    for (Tile t : targetedTiles) {
                        player.publicise(t);
                        player.removeTile(t);
                    }
                    System.out.println("\n\nPlayer " + player.getPosition() + " has Ponged " + freeTile + ".\n\n");
                    eatRequest = false;
                    player.Pong = false;
                    this.counter = pos;
                    flagAllocate(play(player));
                    break;
                }
                else if (player.Eat) {
                    if (pos == (this.counter + 1) % 4) {
                        eatRequest = true;
                    }
                    else {
                        System.out.println("\nPlayer " + pos + " wants to eat while they cannot. The game continues.");
                    }
                }
                else {
                    System.out.println("Something strange happened: player " + pos + " has put out some tiles but did not specify their action.");
                }
            }
        }

        // check Eat request eligibility and applies effects
        if (eatRequest) {
            int pos = (this.counter + 1) % 4;   // position of the player who is allowed to Eat
            Player player = this.players[pos];   // player who is allowed to Eat

            if (flagMap.get(pos).isPresent()) {
                Tile[] targetedTiles = flagMap.get(pos).get();
                player.publicise(freeTile);
                tilesInPool.remove(freeTile);
                for (Tile t : targetedTiles) {
                    player.publicise(t);
                    player.removeTile(t);
                }
                System.out.println("\n\nPlayer " + player.getPosition() + " has Eaten " + freeTile + ".\n\n");
                player.Eat = false;
                this.counter = pos;
                flagAllocate(play(player));
            }
            else {
                System.out.println("Something strange happened: player " + pos + " has put out Eat request but did not put out any tiles.");
            }
        }
    }

    public void run() {
        this.initialise();
        System.out.println("\n\n\nGame will start with Player " + this.counter + " playing.\n\n");

        while (!gameOver) {
            System.out.println("\n\n\nCurrent public tiles:");
            if (this.initialiser instanceof InitialiserQuanZhou) {
                System.out.println("The King tile of this game is: " + ((InitialiserQuanZhou) this.initialiser).getWildCard());
            }
            System.out.println("Tiles in pool: " + this.tilesInPool);
            for (Player player : this.players) {
                Tile[] publicTiles = player.getPublicTiles();
                if (publicTiles.length != 0) {
                    System.out.println("Player " + player.getPosition() + " has public tiles: " + Arrays.toString(publicTiles));
                }
            }

            System.out.println("\n\n\nYou are Player " + this.counter + ". This is your turn of playing.");

            // need to check if player Hu (ZiMoo) before continuing to round
            inspect(this.players[this.counter]);

            if (!gameOver) {
                // TODO: last round
                if (lastRound) {
                    gameOver = true;
                }
                else {
                    Tile freeTile = play(this.players[this.counter]);
                    flagAllocate(freeTile);
                    this.counter = (this.counter + 1) % 4;
                }
            }
        }
    }
}

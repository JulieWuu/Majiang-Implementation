package Player;

import Game.Tile;
import Game.TileSet;
import Utility.Utilities;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class StrategyHuman implements Strategy {
    private Player player;

    @Override
    public void assignPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Optional<Tile[]> inspect(Tile drawnTile) {
        System.out.println("Tiles on your hand: " + Arrays.toString(this.player.getCurrentTiles()));
        System.out.println("New tile drawn: " + drawnTile);

        while (true) {
            try {
                if (this.player.IdleKing) {
                    System.out.println("Choose your action: 1 for AmGang, 2 for extra Gang, 3 for Hu (Idle King), 0 for play a tile: ");
                }
                else {
                    System.out.println("Choose your action: 1 for AmGang, 2 for extra Gang, 3 for Hu, 0 for play a tile: ");
                }
                System.out.println("Extras for QuanZhou style: 4 for Triple Kings, 5 for Double Idle.");

                int choice = new Scanner(System.in).nextInt();

                // do not AmGang or Hu, return empty
                if (choice == 0) {
                    return Optional.empty();
                }

                // check for Gang, return Tile[4]
                else if (choice == 1) {
                    System.out.println("Enter the indices of the four tiles that you wish to use to AmGang with, separate them by comma: ");
                    System.out.println("Use index 0 to represent the drawn tile.");
                    String rawCommand = new Scanner(System.in).nextLine();
                    rawCommand = rawCommand.replaceAll("\\s+", "");

                    try {
                        int[] indices = Arrays.stream(rawCommand.split(",")).mapToInt(Integer::parseInt).toArray();
                        if (indices.length != 4 || Utilities.hasRepeat(indices)) {
                            System.out.println("You must select 4 distinct tiles.");
                        }
                        else {
                            Tile[] candidateTiles = new Tile[4];
                            boolean valid = true;
                            for (int i = 0; i < 4; i++) {
                                if (indices[i] == 0) {
                                    candidateTiles[i] = drawnTile;
                                }
                                else if (indices[i] < 0 || indices[i] > this.player.getCurrentTiles().length) {
                                    System.out.println("The index you entered is out of bound.");
                                    valid = false;
                                    break;
                                } else {
                                    Tile candidate = this.player.getCurrentTiles()[indices[i] - 1];
                                    if (candidate.isKing()) {
                                        System.out.println("You cannot use King to AmGang.");
                                        valid = false;
                                        break;
                                    }
                                    candidateTiles[i] = candidate;
                                }
                            }
                            if (valid) {
                                if (TileSet.checkGang(candidateTiles[0], candidateTiles[1], candidateTiles[2], candidateTiles[3])) {
                                    this.player.Gang = true;
                                    return Optional.of(candidateTiles);
                                } else {
                                    System.out.println("The tiles you chose are not eligible for AmGang.");
                                }
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.println("You did not enter integer indices.");
                    }
                }

                // check for extra Gang, return Tile[1]
                else if (choice == 2) {
                    System.out.println("Enter 0 to confirm that you want to make extra Gang with the drawn tile.");

                    try {
                        int code = new Scanner(System.in).nextInt();
                        if (code == 0) {
                            Tile[] publicTiles = this.player.getPublicTiles();
                            for (int i = 0; i < publicTiles.length - 2; i++) {
                                // justify that the player has Ponged the drawn tile
                                if (TileSet.equivalent(publicTiles[i], drawnTile) &&
                                        TileSet.equivalent(publicTiles[i + 1], drawnTile) &&
                                        TileSet.equivalent(publicTiles[i + 2], drawnTile)) {
                                    return Optional.of(new Tile[]{drawnTile});
                                }
                            }
                            System.out.println("You cannot make extra Gang with this tile.");
                        }
                        else {
                            System.out.println("You have not entered the correct confirmation code.");
                        }
                    }
                    catch (NoSuchElementException e) {
                        System.out.println("You have not entered an integer.");
                    }
                }

                // DO NOT check for Hu, return empty but flag player.Hu
                else if (choice == 3) {
                    this.player.Hu = true;
                    return Optional.empty();
                }

                // Triple Kings for QuanZhou style, flag player.TripleKings
                else if (choice == 4) {
                    this.player.TripleKings = true;
                    return Optional.empty();
                }

                // checked Double Idle for QuanZhou style, flag player.DoubleIdle
                else if (choice == 5) {
                    this.player.addTile(drawnTile);
                    if (TileSet.checkDoubleIdle(this.player.getCurrentTiles())) {
                        this.player.DoubleIdle = true;
                        return Optional.empty();
                    }
                    else {
                        System.out.println("You do not have an Double Idle set of tiles.");
                        this.player.removeTile(drawnTile);
                    }
                }

                else {
                    System.out.println("You have not chosen a valid action.");
                }

            } catch (NoSuchElementException e) {
                System.out.println("You have not entered integers.");
            }
        }
    }

    @Override
    public Tile pick(Tile[] tilesInPool) {
        System.out.println("\n\n\nYou are player " + this.player.getPosition() + ".");
        System.out.println("Tiles that have been played: " + Arrays.toString(tilesInPool));
        System.out.println("Tiles on your hand: " + Arrays.toString(this.player.getCurrentTiles()));

        while (true) {

            // confirm to play a King card if DoubleIdle is flagged (justified in inspect)
            if (this.player.DoubleIdle) {
                System.out.println("Please enter 0 to play one of your Kings and claim your Double King state.");
                try {
                    int code = new Scanner((System.in)).nextInt();
                    if (code == 0) {
                        return this.player.getCurrentTiles()[0];
                    }
                    else {
                        System.out.println("You have not entered the confirmation code correctly.");
                    }
                }
                catch (NoSuchElementException e) {
                    System.out.println("You have not entered an integer.");
                }
            }

            else {
                try {
                    System.out.println("Please enter the index of the tile you want to play: ");
                    int index = new Scanner(System.in).nextInt();
                    if (index >= 1 && index <= this.player.getCurrentTiles().length) {
                        return this.player.getCurrentTiles()[index - 1];
                    }
                    else {
                        System.out.println("Please enter a valid index within the range 1-" + (this.player.getCurrentTiles().length));
                    }
                }
                catch (NoSuchElementException e) {
                    System.out.println("You have not entered an integer.");
                }
            }
        }
    }

    @Override
    // sets this.player status, return an array of Integers if Pong/Eat/Gang
    public Optional<Tile[]> flag(Tile freeTile) {
        System.out.println("\n\n\nYou are player " + this.player.getPosition());
        System.out.println(freeTile + " has been played. And your current tiles are:");
        System.out.println(Arrays.toString(this.player.getCurrentTiles()));

        while (true) {
            try {
                System.out.println("Choose your action: 1 for Pong, 2 for Eat, 3 for Gang, 4 for Hu, 0 for pass: ");
                int choice = new Scanner(System.in).nextInt();

                // most common one first, return empty
                if (choice == 0) {
                    return Optional.empty();
                }

                // check for Pong, return Tile[2], flag player.Pong
                else if (choice == 1) {
                    System.out.println("Enter the indices of the two tiles that you wish to use to Pong with, separate them by comma: ");

                    String rawCommand = new Scanner(System.in).nextLine();
                    rawCommand = rawCommand.replaceAll("\\s+", "");

                    try {
                        int[] indices = Arrays.stream(rawCommand.split(",")).mapToInt(Integer::parseInt).toArray();
                        if (indices.length != 2 || Utilities.hasRepeat(indices)) {
                            System.out.println("You must select 2 distinct tiles.");
                        }
                        else {
                            Tile[] candidateTiles = new Tile[2];
                            boolean valid = true;
                            for (int i = 0; i < 2; i++) {
                                if (indices[i] <= 0 || indices[i] > this.player.getCurrentTiles().length) {
                                    System.out.println("The index you entered is out of bound.");
                                    valid = false;
                                    break;
                                } else {
                                    Tile candidate = this.player.getCurrentTiles()[indices[i] - 1];
                                    if (candidate.isKing()) {
                                        System.out.println("You cannot use King to Pong.");
                                        valid = false;
                                        break;
                                    }
                                    candidateTiles[i] = candidate;
                                }
                            }
                            if (valid) {
                                if (TileSet.checkPong(freeTile, candidateTiles[0], candidateTiles[1])) {
                                    this.player.Pong = true;
                                    return Optional.of(candidateTiles);
                                } else {
                                    System.out.println("The tiles you chose are not eligible for Pong.");
                                }
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.println("You did not enter integer indices.");
                    }
                }

                // check for Eat, return Tile[2], flag player.Eat
                else if (choice == 2) {
                    System.out.println("Enter the indices of the two tiles that you wish to use to Eat with, separate them by comma: ");

                    String rawCommand = new Scanner(System.in).nextLine();
                    rawCommand = rawCommand.replaceAll("\\s+", "");

                    try {
                        int[] indices = Arrays.stream(rawCommand.split(",")).mapToInt(Integer::parseInt).toArray();
                        if (indices.length != 2 || Utilities.hasRepeat(indices)) {
                            System.out.println("You must select 2 distinct tiles.");
                        }
                        else {
                            Tile[] candidateTiles = new Tile[2];
                            boolean valid = true;
                            for (int i = 0; i < 2; i++) {
                                if (indices[i] <= 0 || indices[i] > this.player.getCurrentTiles().length) {
                                    System.out.println("The index you entered is out of bound.");
                                    valid = false;
                                    break;
                                }
                                else {
                                    Tile candidate = this.player.getCurrentTiles()[indices[i] - 1];
                                    if (candidate.isKing()) {
                                        System.out.println("You cannot use King to Eat.");
                                        valid = false;
                                        break;
                                    }
                                    candidateTiles[i] = candidate;
                                }
                            }
                            if (valid) {
                                if (TileSet.checkEat(freeTile, candidateTiles[0], candidateTiles[1])) {
                                    this.player.Eat = true;
                                    return Optional.of(candidateTiles);
                                } else {
                                    System.out.println("The tiles you chose are not eligible for Eat.");
                                }
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.println("You did not enter integer indices.");
                    }
                }

                // check for Gang, return Tile[3], flag player.Gang
                else if (choice == 3) {
                    System.out.println("Enter the indices of the three tiles that you wish to use to Gang with, separate them by comma: ");

                    String rawCommand = new Scanner(System.in).nextLine();
                    rawCommand = rawCommand.replaceAll("\\s+", "");

                    try {
                        int[] indices = Arrays.stream(rawCommand.split(",")).mapToInt(Integer::parseInt).toArray();
                        if (indices.length != 3 || Utilities.hasRepeat(indices)) {
                            System.out.println("You must select 3 distinct tiles.");
                        }
                        else {
                            Tile[] candidateTiles = new Tile[3];
                            boolean valid = true;
                            for (int i = 0; i < indices.length; i++) {
                                if (indices[i] <= 0 || indices[i] > this.player.getCurrentTiles().length) {
                                    System.out.println("The index you entered is out of bound.");
                                    valid = false;
                                    break;
                                }
                                else {
                                    Tile candidate = this.player.getCurrentTiles()[indices[i] - 1];
                                    if (candidate.isKing()) {
                                        System.out.println("You cannot use King to Gang.");
                                        valid = false;
                                        break;
                                    }
                                    candidateTiles[i] = candidate;
                                }
                            }
                            if (valid) {
                                if (TileSet.checkGang(freeTile, candidateTiles[0], candidateTiles[1], candidateTiles[2])) {
                                    this.player.Gang = true;
                                    return Optional.of(candidateTiles);
                                } else {
                                    System.out.println("The tiles you chose are not eligible for Gang.");
                                }
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.println("You did not enter integer indices.");
                    }
                }

                // DO NOT check for Hu, return empty but flag player.Hu
                else if (choice == 4) {
                    this.player.Hu = true;
                    return Optional.empty();
                }
                else {
                    System.out.println("Please enter a valid action number.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("You have not entered an integer.");
            }
        }
    }
}

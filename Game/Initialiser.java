package Game;

import Player.Player;

public interface Initialiser {
    int getPlayerPosition();

    Tile[][] shuffle();

    boolean checkHu(Player player);
}

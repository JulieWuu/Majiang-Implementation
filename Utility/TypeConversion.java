package Utility;

import Game.Tile;

import java.util.ArrayList;
import java.util.Collections;

public class TypeConversion {

    public static <E> ArrayList<E> toAList(E[] array) {
        ArrayList<E> result = new ArrayList<>();
        Collections.addAll(result, array);
        return result;
    }

    public static Tile[] toArray(ArrayList<Tile> aList) {
        Tile[] newArray = new Tile[aList.size()];
        return aList.toArray(newArray);
    }

}

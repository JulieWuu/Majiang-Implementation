package Game;

import java.util.regex.Pattern;

public class Tile implements Comparable<Tile> {

    private final Integer type;     // 1: character; 2: flower; 3: wan; 4: suo; 5: tong
    private final Integer value;    // up to 9 for W/S/T, up to 7 for C, up to 8 for F

    protected Tile(int type, int value) {
        if (type >= 3 && type <= 5) {    // Wan, Suo(Tiao), Tong
            if (value >= 1 && value <= 9) {
                this.type = type;
                this.value = value;
            } else {
                throw new IllegalArgumentException("Value out of bound for tile type Wan, Suo, Tong.");
            }
        }
        else if (type == 1) {   // Characters
            if (value >= 1 && value <= 7) {
                this.type = type;
                this.value = value;
            } else {
                throw new IllegalArgumentException("Value out of bound for tile type Character.");
            }
        }
        else if (type == 2) {   // Flowers
            if (value >= 1 && value <= 8) {
                this.type = type;
                this.value = value;
            } else {
                throw new IllegalArgumentException("Value out of bound for tile type Flower.");
            }
        }
        else if (type == 0) {   // The King
            if (value == 0) {
                this.type = type;
                this.value = value;
            } else {
                throw new IllegalArgumentException("Value out of bound for tile type King.");
            }
        }
        else {
            throw new IllegalArgumentException("Tile cannot be recognised.");
        }
    }

    /// normally not allowed, only for testing
    /*
    protected Tile(String name) {
        if (Pattern.matches("\\b\\d\\swan", name)) {
            this.value = Integer.parseInt(name.substring(0, 1));
            this.type = 3;
        }
        else if (Pattern.matches("\\b\\d\\stiao", name)) {
            this.value = Integer.parseInt(name.substring(0, 1));
            this.type = 4;
        }
        else if (Pattern.matches("\\b\\d\\stong", name)) {
            this.value = Integer.parseInt(name.substring(0, 1));
            this.type = 5;
        }
        else switch (name) {
            case "Dong" -> {
                this.type = 1;
                this.value = 1;
            }
            case "Xi" -> {
                this.type = 1;
                this.value = 2;
            }
            case "Nan" -> {
                this.type = 1;
                this.value = 3;
            }
            case "Bei" -> {
                this.type = 1;
                this.value = 4;
            }
            case "Zhong" -> {
                this.type = 1;
                this.value = 5;
            }
            case "Fa" -> {
                this.type = 1;
                this.value = 6;
            }
            case "Ban" -> {
                this.type = 1;
                this.value = 7;
            }
            case "Spring" -> {
                this.type = 2;
                this.value = 1;
            }
            case "Summer" -> {
                this.type = 2;
                this.value = 2;
            }
            case "Autumn" -> {
                this.type = 2;
                this.value = 3;
            }
            case "Winter" -> {
                this.type = 2;
                this.value = 4;
            }
            case "Plum" -> {
                this.type = 2;
                this.value = 5;
            }
            case "Orchid" -> {
                this.type = 2;
                this.value = 6;
            }
            case "Chrysanthemum" -> {
                this.type = 2;
                this.value = 7;
            }
            case "Bamboo" -> {
                this.type = 2;
                this.value = 8;
            }
            case "King" -> {
                this.type = 0;
                this.value = 0;
            }
            default -> throw new IllegalArgumentException("Cannot recognise this tile.");
        }
    }
    */

    @Override
    public String toString() {
        switch (this.type) {
            case 3 -> {
                return this.value + " wan";
            }
            case 4 -> {
                return this.value + " tiao";
            }
            case 5 -> {
                return this.value + " tong";
            }
            case 1 -> {
                if (this.value == 1) {
                    return "Dong";
                } else if (this.value == 2) {
                    return "Nan";
                } else if (this.value == 3) {
                    return "Xi";
                } else if (this.value == 4) {
                    return "Bei";
                } else if (this.value == 5) {
                    return "Zhong";
                } else if (this.value == 6) {
                    return "Fa";
                } else if (this.value == 7) {
                    return "Ban";
                }
            }
            case 2 -> {
                if (this.value == 1) {
                    return "Spring";
                } else if (this.value == 2) {
                    return "Summer";
                } else if (this.value == 3) {
                    return "Autumn";
                } else if (this.value == 4) {
                    return "Winter";
                } else if (this.value == 5) {
                    return "Plum";
                } else if (this.value == 6) {
                    return "Orchid";
                } else if (this.value == 7) {
                    return "Chrysanthemum";
                } else if (this.value == 8) {
                    return "Bamboo";
                }
            }
            default -> {
                return "Cannot recognise this tile.";
            }
        }
        return "";
    }

    public int getType() {
        return this.type;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isKing() {
        return this.type == 0 && this.value == 0;
    }

    @Override
    public int compareTo(Tile o) {
        if (this.type.compareTo(o.type) == 0) {
            return this.value.compareTo(o.value);
        }
        else {
            return this.type.compareTo(o.type);
        }
    }

}

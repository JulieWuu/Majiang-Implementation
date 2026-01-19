package Utility;

public class Utilities {

    public static boolean hasRepeat(int[] arr) {
        boolean repeat = false;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] == arr[j]) {
                    repeat = true;
                    break;
                }
            }
        }
        return repeat;
    }
}

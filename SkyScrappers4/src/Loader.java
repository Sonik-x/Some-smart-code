import java.util.ArrayList;

public class Loader {

    static Scrapper[][] build = new Scrapper[4][4];
    static int[] theClues = new int[16];
    static int[][] allCombinations = null;
    static Scrapper[][] tryBuild = new Scrapper[4][4];

    public static void main(String[] args) {


        int[] testClues = new int[]{0, 0, 1, 2,
                0, 2, 0, 0,
                0, 3, 0, 0,
                0, 1, 0, 0};
        int[][] scrappers = solvePuzzle(testClues);
        for (int[] raw : scrappers) {
            System.out.println();
            for (int n : raw) {
                System.out.print(n + " ");
            }
        }


    }

    public static class Scrapper {
        int height;
        boolean isFixed;

        public Scrapper() {
        }

        public Scrapper(int h, boolean isFixed) {
            height = h;
            this.isFixed = isFixed;
        }
    }

    static int[][] solvePuzzle(int[] clues) {
        allCombinations = createAllCombinations();
        theClues = clues;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                build[i][j] = new Scrapper();
            }
        }

        int[][] result = new int[4][4];
        int tryIt = 0;
        while (!isSolved()) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 4; j++) {
                    sortHorisontal(j);
                    sortVertical(j);
                }

            }
            trySolve(tryIt++);
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = build[i][j].height;
            }
        }
        return result;
    }

    static void trySolve(int index) {
        if (index == 0) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    tryBuild[i][j] = new Scrapper();
                }
            }
        }
        cleanUp(0);
        tryPutLine(index);
    }

    static void tryPutLine(int index) {
        int clue1 = theClues[15 - index];
        int clue2 = theClues[index + 4];

        ArrayList<int[]> options = allPossiblePlacements(clue1, clue2);

        Scrapper scrapper;
        for (int i = 0; i < 4; i++) {
            scrapper = build[index][i];
            if (scrapper.isFixed) {
                for (int j = 0; j < options.size(); j++) {
                    if (options.get(j)[i] != scrapper.height) {
                        options.remove(j--);
                    }
                }

            }
        }

        for (int j = 0; j < options.size(); j++) {
            placeTryHorisontal(index, options.get(j), true);
            if (!tryIsOk()) {
                options.remove(j--);
                cleanUp(index);
            }
        }

        if (options.size() == 1) {
            placeLineFinalHorisontal(index, options.get(0));
            cleanUp(index + 1);
            return;
        }

        for (int j = 0; j < options.size(); j++) {
            placeTryHorisontal(index, options.get(j), true);
            ArrayList<int[]> options2 = allPossiblePlacements(theClues[14 - index], theClues[index + 5]);
            for (int i = 0; i < options2.size(); i++) {
                placeTryHorisontal(index + 1, options2.get(i), true);
                if (!tryIsOk()) {
                    options2.remove(i--);
                    cleanUp(index + 1);
                }
            }
            if (options2.size() == 0) {
                options.remove(j--);
                cleanUp(index);
            }

        }

        if (options.size() == 1) {
            placeLineFinalHorisontal(index, options.get(0));
            return;
        }

    }

    static boolean tryIsOk() {
        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < 4; k++) {
                int o = tryBuild[k][i].height;
                if (o == 0) {
                    continue;
                }
                for (int j = k + 1; j < 4; j++) {
                    if (tryBuild[j][i].height == o) {
                        return false;
                    }
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                boolean y = trySortVertical(j);
                boolean z = trySortHorisontal(j);
                if (!(y && z)) {
                    return false;
                }
            }
        }
        return true;
    }

    static void cleanUp(int i) {
        for (; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tryBuild[i][j].height = build[i][j].height;
                tryBuild[i][j].isFixed = build[i][j].isFixed;
            }
        }
    }

    static boolean trySortVertical(int index) {
        int clue1 = theClues[index];
        int clue2 = theClues[11 - index];

        ArrayList<int[]> options = allPossiblePlacements(clue1, clue2);

        Scrapper scrapper;
        for (int i = 0; i < 4; i++) {
            scrapper = tryBuild[i][index];
            if (scrapper.height != 0) {
                for (int j = 0; j < options.size(); j++) {
                    if (options.get(j)[i] != scrapper.height) {
                        options.remove(j--);
                    }
                }

            }
        }

        if (options.size() == 1) {
            placeTryVertical(index, options.get(0), true);
            return true;
        }

        if (options.size() == 0) {
            return false;
        }
        checkTryPosVertical(options, index);

        return true;
    }

    static boolean trySortHorisontal(int index) {
        int clue1 = theClues[15 - index];
        int clue2 = theClues[index + 4];

        ArrayList<int[]> options = allPossiblePlacements(clue1, clue2);

        Scrapper scrapper;
        for (int i = 0; i < 4; i++) {
            scrapper = tryBuild[index][i];
            if (scrapper.height != 0) {
                for (int j = 0; j < options.size(); j++) {
                    if (options.get(j)[i] != scrapper.height) {
                        options.remove(j--);
                    }
                }

            }
        }

        if (options.size() == 0) {
            return false;
        }

        if (options.size() == 1) {
            placeTryHorisontal(index, options.get(0), true);
            return true;
        }
        checkTryPosHorisontal(options, index);

        return true;
    }

    static void checkTryPosVertical(ArrayList<int[]> options, int index) {
        for (int i = 0; i < 4; i++) {
            int o = 0;
            boolean canFix = true;
            for (int[] op : options) {
                if (o == 0) {
                    o = op[i];
                    continue;
                }
                if (op[i] != o) {
                    canFix = false;
                }

            }
            if (canFix) {
                tryBuild[i][index] = new Scrapper(o, true);
            }
        }
    }

    static void checkTryPosHorisontal(ArrayList<int[]> options, int index) {
        for (int i = 0; i < 4; i++) {
            int o = 0;
            boolean canFix = true;
            for (int[] op : options) {
                if (o == 0) {
                    o = op[i];
                    continue;
                }
                if (op[i] != o) {
                    canFix = false;
                }

            }
            if (canFix) {
                tryBuild[index][i] = new Scrapper(o, true);
            }
        }
    }

    static void placeTryHorisontal(int index, int[] street, boolean isFixed) {
        for (int i = 0; i < 4; i++) {
            tryBuild[index][i] = new Scrapper(street[i], isFixed);
        }

    }

    static void placeTryVertical(int index, int[] street, boolean isFixed) {
        for (int i = 0; i < 4; i++) {
            tryBuild[i][index] = new Scrapper(street[i], isFixed);
        }

    }

    static boolean isSolved() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (!build[i][j].isFixed) {
                    return false;
                }
            }
        }

        return true;
    }

    static void sortVertical(int index) {
        int clue1 = theClues[index];
        int clue2 = theClues[11 - index];

        ArrayList<int[]> options = allPossiblePlacements(clue1, clue2);
        if (options.size() == 1) {
            placeLineFinalVertical(index, options.get(0));
        }

        Scrapper scrapper;
        for (int i = 0; i < 4; i++) {
            scrapper = build[i][index];
            if (scrapper.isFixed) {
                for (int j = 0; j < options.size(); j++) {
                    if (options.get(j)[i] != scrapper.height) {
                        options.remove(j--);
                    }
                }

            }
        }

        if (options.size() == 1) {
            placeLineFinalVertical(index, options.get(0));
        }

        checkFixPosVertical(options, index);
    }

    static void sortHorisontal(int index) {
        int clue1 = theClues[15 - index];
        int clue2 = theClues[index + 4];

        ArrayList<int[]> options = allPossiblePlacements(clue1, clue2);
        if (options.size() == 1) {
            placeLineFinalHorisontal(index, options.get(0));
        }

        Scrapper scrapper;
        for (int i = 0; i < 4; i++) {
            scrapper = build[index][i];
            if (scrapper.isFixed) {

                for (int j = 0; j < options.size(); j++) {
                    if (options.get(j)[i] != scrapper.height) {
                        options.remove(j--);
                    }
                }

            }
        }

        if (options.size() == 1) {
            placeLineFinalHorisontal(index, options.get(0));
        }

        checkFixPosHorisontal(options, index);

    }

    static void checkFixPosHorisontal(ArrayList<int[]> options, int index) {
        for (int i = 0; i < 4; i++) {
            int o = 0;
            boolean canFix = true;
            for (int[] op : options) {
                if (o == 0) {
                    o = op[i];
                    continue;
                }
                if (op[i] != o) {
                    canFix = false;
                }

            }
            if (canFix) {
                build[index][i] = new Scrapper(o, true);
            }
        }
    }

    static void checkFixPosVertical(ArrayList<int[]> options, int index) {
        for (int i = 0; i < 4; i++) {
            int o = 0;
            boolean canFix = true;
            for (int[] op : options) {
                if (o == 0) {
                    o = op[i];
                    continue;
                }
                if (op[i] != o) {
                    canFix = false;
                }

            }
            if (canFix) {
                build[i][index] = new Scrapper(o, true);
            }
        }
    }


    static void placeLineFinalVertical(int index, int[] street) {
        for (int i = 0; i < 4; i++) {
            build[i][index] = new Scrapper(street[i], true);
        }

    }

    static void placeLineFinalHorisontal(int index, int[] street) {
        for (int i = 0; i < 4; i++) {
            build[index][i] = new Scrapper(street[i], true);
        }

    }

    static ArrayList<int[]> allPossiblePlacements(int clue1, int clue2) {
        ArrayList<int[]> result = new ArrayList<>();
        if (clue1 == 0 && clue2 == 0) {
            for (int[] option : allCombinations) {
                result.add(option);
            }
            return result;
        }
        if (clue1 == 0) {
            for (int[] option : allCombinations) {
                if (calculateClueReverse(option) == clue2) {
                    result.add(option);
                }
            }
            return result;
        }

        if (clue2 == 0) {
            for (int[] option : allCombinations) {
                if (calculateClue(option) == clue1) {
                    result.add(option);
                }
            }
            return result;
        }

        for (int[] option : allCombinations) {
            if (calculateClue(option) == clue1 && calculateClueReverse(option) == clue2) {
                result.add(option);
            }
        }

        return result;
    }

    static int calculateClue(int[] line) {
        int result = 0;
        int highest = 0;

        for (int i = 0; i < 4; i++) {
            if (line[i] > highest) {
                highest = line[i];
                result++;
            }
        }

        return result;
    }

    static int calculateClueReverse(int[] line) {
        int result = 0;
        int highest = 0;

        for (int i = 3; i >= 0; i--) {
            if (line[i] > highest) {
                highest = line[i];
                result++;
            }
        }


        return result;
    }


    static int[][] createAllCombinations() {
        int[][] result = new int[24][4];
        int lineIndex = 0;

        int[] line = new int[4];
        for (int i = 1; i <= 4; i++) {
            line[0] = i;
            int[] options = builOptions(i);
            for (int o : options) {
                line[1] = o;
                int[] optionss = builOptions(i, o);
                for (int k : optionss) {
                    line[2] = k;
                    line[3] = builOptions(i, o, k);
                    for (int j = 0; j < 4; j++) {
                        result[lineIndex][j] = line[j];
                    }
                    lineIndex++;
                }
            }

        }


        return result;
    }

    static int[] builOptions(int exclude) {
        int[] result = new int[3];
        int index = 0;
        for (int i = 1; i <= 4; i++) {
            if (i == exclude) continue;
            result[index] = i;
            index++;
        }
        return result;
    }

    static int[] builOptions(int exclude1, int exclude2) {
        int[] result = new int[2];
        int index = 0;
        for (int i = 1; i <= 4; i++) {
            if (i == exclude1 || i == exclude2) continue;
            result[index] = i;
            index++;
        }
        return result;
    }

    static int builOptions(int exclude1, int exclude2, int exclude3) {
        for (int i = 1; i <= 4; i++) {
            if (i == exclude1 || i == exclude2 || i == exclude3) continue;
            return i;
        }

        return 0;
    }
}

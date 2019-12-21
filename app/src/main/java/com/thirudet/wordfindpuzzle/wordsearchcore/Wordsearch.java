package com.thirudet.wordfindpuzzle.wordsearchcore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Wordsearch extends Puzzle {
    char[][] map_Randomize = null;

    public void generate(WordList words, int pSize) {
        if (RemovedList == null || this.RemovedList.size() == 0) {
            RemovedList = new WordMap(words, pSize);
        }
        FinishedList = new WordMap();
        FinishedList = TEST(RemovedList);
        populateWordMatrix(FinishedList);
    }

    public WordMap TEST(WordMap words) {
        WordMap temp = new WordMap();
        WordMap bad = new WordMap();
        try {
            temp.setBound(words.getBound());
            bad.setBound(temp.getBound());
        } catch (Exception e) {
            System.out.println("BOUND CANT BE EQUAL TO ZERO (aka board can't have zero size)");
        }
        int size = words.size();
        int count = 0;
        while (temp.size() < 12 && count < 1000) {
            Word w = randomizeWord(words, bad);
            if (w != null) {
                if (!checkWord(w, temp) || correctWordRandomly(w, temp) || correctBackup(w, temp)) {
                    temp.add(w);
                } else {
                    bad.add(w);
                }
            }
            count++;
        }
        words.add(bad);
        return temp;
    }

    private boolean correctWordRandomly(Word tempW, WordMap tempMap) {
        for (int i = 0; i < tempMap.size(); i++) {
            Word mapsWord = (Word) tempMap.get(i);
            for (int j = 0; j < mapsWord.size(); j++) {
                for (int k = 0; k < tempW.size(); k++) {
                    if (!getContinue()) {
                        return false;
                    }
                    if (tempW.getCharAt(k) == mapsWord.getCharAt(j)) {
                        correctByRandomDirection(tempW, mapsWord);
                        if (!checkWord(tempW, tempMap)) {
                            return true;
                        }
                        if (tempW.getDown()) {
                            tempW.setUp(true);
                        } else if (tempW.getUp()) {
                            tempW.setDown(true);
                        }
                        if (tempW.getRight()) {
                            tempW.setLeft(true);
                        } else if (tempW.getLeft()) {
                            tempW.setRight(true);
                        }
                        if (!checkWord(tempW, tempMap)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean correctBackup(Word tempW, WordMap tempMap) {
        if (correctByEmptySpace(tempW, findEmptySpace(tempMap))) {
            return true;
        }
        return false;
    }

    private boolean[][] findEmptySpace(WordMap tempMap) {
        int i;
        int j;
        boolean[][] boolMap = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{tempMap.getBound(), tempMap.getBound()});
        for (i = 0; i < boolMap.length; i++) {
            for (j = 0; j < boolMap[i].length; j++) {
                boolMap[i][j] = true;
            }
        }
        for (i = 0; i < tempMap.size(); i++) {
            Word tempW = (Word) tempMap.get(i);
            for (j = 0; j < tempW.size(); j++) {
                int y = tempW.getCharPosY(j);
                boolMap[y][tempW.getCharPosX(j)] = false;
            }
        }
        return boolMap;
    }

    private boolean correctByEmptySpace(Word tempW, boolean[][] tempBM) {
        int i;
        int j;
        ArrayList<ArrayList<ArrayList<Point>>> freeSpaceLR = new ArrayList();
        ArrayList<ArrayList<ArrayList<Point>>> freeSpaceUD = new ArrayList();
        ArrayList<ArrayList<ArrayList<Point>>> freeSpaceDR = new ArrayList();
        ArrayList<ArrayList<ArrayList<Point>>> freeSpaceDL = new ArrayList();
        int countX = 0;
        int countY = 0;
        freeSpaceLR.add(new ArrayList());
        ((ArrayList) freeSpaceLR.get(0)).add(new ArrayList());
        for (i = 0; i < tempBM.length; i++) {
            for (j = 0; j < tempBM[i].length; j++) {
                if (tempBM[i][j]) {
                    ((ArrayList) ((ArrayList) freeSpaceLR.get(countY)).get(countX)).add(new Point(j, i));
                } else {
                    ((ArrayList) freeSpaceLR.get(countY)).add(new ArrayList());
                    countX++;
                }
            }
            freeSpaceLR.add(new ArrayList());
            countY++;
            ((ArrayList) freeSpaceLR.get(countY)).add(new ArrayList());
            countX = 0;
        }
        if (!getContinue()) {
            return false;
        }
        countX = 0;
        countY = 0;
        freeSpaceUD.add(new ArrayList());
        (freeSpaceUD.get(0)).add(new ArrayList());
        for (i = 0; i < tempBM.length; i++) {
            for (j = 0; j < tempBM[i].length; j++) {
                if (tempBM[j][i]) {
                    ((ArrayList) ((ArrayList) freeSpaceUD.get(countY)).get(countX)).add(new Point(i, j));
                } else {
                    ( freeSpaceUD.get(countY)).add(new ArrayList());
                    countX++;
                }
            }
            freeSpaceUD.add(new ArrayList());
            countY++;
            ((ArrayList) freeSpaceUD.get(countY)).add(new ArrayList());
            countX = 0;
        }
        if (!getContinue()) {
            return false;
        }
        int column;
        int row = 0;
        countY = 0;
        countX = 0;
        freeSpaceDL.add(new ArrayList());
        ( freeSpaceDL.get(0)).add(new ArrayList());
        while (row < tempBM[0].length - 1) {
            int tX = row;
            int tY = 0;
            while (tX > -1 && tY < tempBM.length) {
                if (tempBM[tY][tX]) {
                    ((ArrayList) ((ArrayList) freeSpaceDL.get(countY)).get(countX)).add(new Point(tX, tY));
                } else {
                    (freeSpaceDL.get(countY)).add(new ArrayList());
                    countX++;
                }
                tX--;
                tY++;
            }
            freeSpaceDL.add(new ArrayList());
            countY++;
            (freeSpaceDL.get(countY)).add(new ArrayList());
            countX = 0;
            row++;
        }
        for (column = 0; column < tempBM.length; column++) {
            int tX = row;
            int tY = column;
            while (tX > -1 && tY < tempBM.length) {
                if (tempBM[tY][tX]) {
                    ((ArrayList) ((ArrayList) freeSpaceDL.get(countY)).get(countX)).add(new Point(tX, tY));
                } else {
                    (freeSpaceDL.get(countY)).add(new ArrayList());
                    countX++;
                }
                tX--;
                tY++;
            }
            freeSpaceDL.add(new ArrayList());
            countY++;
            (freeSpaceDL.get(countY)).add(new ArrayList());
            countX = 0;
        }
        if (!getContinue()) {
            return false;
        }
        row = tempBM[0].length;
        countY = 0;
        countX = 0;
        freeSpaceDR.add(new ArrayList());
        (freeSpaceDR.get(0)).add(new ArrayList());
        while (row > 0) {
            int tX = row;
            int tY = 0;
            while (tX < tempBM[0].length && tY < tempBM.length) {
                if (tempBM[tY][tX]) {
                    ((ArrayList) ((ArrayList) freeSpaceDR.get(countY)).get(countX)).add(new Point(tX, tY));
                } else {
                    (freeSpaceDR.get(countY)).add(new ArrayList());
                    countX++;
                }
                tX++;
                tY++;
            }
            freeSpaceDR.add(new ArrayList());
            countY++;
            (freeSpaceDR.get(countY)).add(new ArrayList());
            countX = 0;
            row--;
        }
        for (column = 0; column < tempBM.length; column++) {
            int tX = row;
            int tY = column;
            while (tX < tempBM[0].length && tY < tempBM.length) {
                if (tempBM[tY][tX]) {
                    ((ArrayList) ((ArrayList) freeSpaceDR.get(countY)).get(countX)).add(new Point(tX, tY));
                } else {
                    (freeSpaceDR.get(countY)).add(new ArrayList());
                    countX++;
                }
                tX++;
                tY++;
            }
            freeSpaceDR.add(new ArrayList());
            countY++;
            (freeSpaceDR.get(countY)).add(new ArrayList());
            countX = 0;
        }
        if (!getContinue()) {
            return false;
        }
        boolean correct = false;
        while (!correct) {
            int randomUD_LR_DR_DL = this.mRandom.nextInt(4);
            if (randomUD_LR_DR_DL == 0) {
                for (i = 0; i < freeSpaceLR.size(); i++) {
                    for (j = 0; j < ((ArrayList) freeSpaceLR.get(i)).size(); j++) {
                        if (!getContinue()) {
                            return false;
                        }
                        if (((ArrayList) ((ArrayList) freeSpaceLR.get(i)).get(j)).size() >= tempW.size()) {
                            correct = true;
                            int tX = ((Point) ((ArrayList) ((ArrayList) freeSpaceLR.get(i)).get(j)).get(0)).getX();
                            int tY = ((Point) ((ArrayList) ((ArrayList) freeSpaceLR.get(i)).get(j)).get(0)).getY();
                            tempW.setRight(true);
                            tempW.setDown(false);
                            tempW.setUp(false);
                            tempW.setFirstCharPos(tX, tY);
                            break;
                        }
                    }
                }
                continue;
            } else if (randomUD_LR_DR_DL == 1) {
                for (i = 0; i < freeSpaceUD.size(); i++) {
                    j = 0;
                    while (j < ( freeSpaceUD.get(i)).size()) {
                        if (((ArrayList) ((ArrayList) freeSpaceUD.get(i)).get(j)).size() < tempW.size()) {
                            j++;
                        } else if (!getContinue()) {
                            return false;
                        } else {
                            correct = true;
                            int tX = ((Point) ((ArrayList) ((ArrayList) freeSpaceUD.get(i)).get(j)).get(0)).getX();
                            int tY = ((Point) ((ArrayList) ((ArrayList) freeSpaceUD.get(i)).get(j)).get(0)).getY();
                            tempW.setDown(true);
                            tempW.setRight(false);
                            tempW.setLeft(false);
                            tempW.setFirstCharPos(tX, tY);
                        }
                    }
                }
                continue;
            } else if (randomUD_LR_DR_DL == 2) {
                for (i = 0; i < freeSpaceDR.size(); i++) {
                    j = 0;
                    while (j < ( freeSpaceDR.get(i)).size()) {
                        if (((ArrayList) ((ArrayList) freeSpaceDR.get(i)).get(j)).size() < tempW.size()) {
                            j++;
                        } else if (!getContinue()) {
                            return false;
                        } else {
                            correct = true;
                            int tX = ((Point) ((ArrayList) ((ArrayList) freeSpaceDR.get(i)).get(j)).get(0)).getX();
                            int tY = ((Point) ((ArrayList) ((ArrayList) freeSpaceDR.get(i)).get(j)).get(0)).getY();
                            tempW.setRight(true);
                            tempW.setDown(true);
                            tempW.setFirstCharPos(tX, tY);
                        }
                    }
                }
                continue;
            } else {
                for (i = 0; i < freeSpaceDL.size(); i++) {
                    j = 0;
                    while (j < ( freeSpaceDL.get(i)).size()) {
                        if (((ArrayList) ((ArrayList) freeSpaceDL.get(i)).get(j)).size() < tempW.size()) {
                            j++;
                        } else if (!getContinue()) {
                            return false;
                        } else {
                            correct = true;
                            int tX = ((Point) ((ArrayList) ((ArrayList) freeSpaceDL.get(i)).get(j)).get(0)).getX();
                            int tY = ((Point) ((ArrayList) ((ArrayList) freeSpaceDL.get(i)).get(j)).get(0)).getY();
                            tempW.setLeft(true);
                            tempW.setDown(true);
                            tempW.setFirstCharPos(tX, tY);
                        }
                    }
                }
                continue;
            }
        }
        return true;
    }

    private void correctByRandomDirection(Word tempA, Word tempB) {
        while (true) {
            if (tempA.getDown() != tempB.getDown() && tempA.getUp() != tempB.getUp()) {
                return;
            }
            if (tempA.getRight() == tempB.getRight() || tempA.getLeft() == tempB.getLeft()) {
                tempA.setDown(this.mRandom.nextBoolean());
                tempA.setUp(this.mRandom.nextBoolean());
                tempA.setLeft(this.mRandom.nextBoolean());
                tempA.setRight(this.mRandom.nextBoolean());
            } else {
                return;
            }
        }
    }

    public char[][] getMatrixRandomize() {
        return this.map_Randomize;
    }

    protected void populateWordMatrix(WordMap tempMap) {
        super.populateWordMatrix(tempMap);
        char[][] map_R = super.getMatrixSolution();
        this.map_Randomize = (char[][]) Array.newInstance(Character.TYPE, new int[]{tempMap.getBound(), tempMap.getBound()});
        for (int i = 0; i < this.map_Randomize.length; i++) {
            for (int j = 0; j < this.map_Randomize[i].length; j++) {
                if (map_R[i][j] == '~') {
                    this.map_Randomize[i][j] = (char) (this.mRandom.nextInt(24) + 97);
                } else {
                    this.map_Randomize[i][j] = map_R[i][j];
                }
            }
        }
    }

    public void populateWordMatrix(char[][] tempMap) {
        this.map_Randomize = (char[][]) Array.newInstance(Character.TYPE, new int[]{tempMap.length, tempMap[0].length});
        for (int i = 0; i < tempMap.length; i++) {
            for (int j = 0; j < tempMap[i].length; j++) {
                this.map_Randomize[i][j] = tempMap[i][j];
            }
        }
    }

    public boolean checkWord(Word tempW, WordMap tempMap) {
        for (int i = 0; i < tempMap.size(); i++) {
            if (tempW.checkCollision((Word) tempMap.get(i))) {
                return true;
            }
        }
        if (tempW.checkBounds(tempMap.getBound())) {
            return true;
        }
        return false;
    }

    private Word randomizeWord(WordMap tempMap, WordMap badMap) {
        Word tempW = super.randomizeWord(tempMap, tempMap.size());
        if (!check_correctBounds(tempW, tempMap)) {
            return tempW;
        }
        badMap.add(tempW);
        return null;
    }

    public Model.PuzzleType getPuzzleType() {
        return Model.PuzzleType.WORDSEARCH;
    }
}

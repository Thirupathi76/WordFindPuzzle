package com.thirudet.wordfindpuzzle.wordsearchcore;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.concurrent.Semaphore;

public abstract class Puzzle {
    WordMap FinishedList = new WordMap();
    WordMap RemovedList = new WordMap();

    Semaphore semaphore = new Semaphore(1);
    boolean m_continue = false;
    private char[][] map = null;
    Semaphore mutex = new Semaphore(1);
    protected Random mRandom = new Random();

    protected class PuzzleStop implements Runnable {
        Puzzle m_puz;

        PuzzleStop(Puzzle temp) {
            m_puz = temp;
            setContinue(true);
        }

        public void run() {
            long start = System.currentTimeMillis();
            while (m_puz.getContinue()) {
                if (System.currentTimeMillis() - start > 2000) {
                    m_puz.setContinue(false);
                }
            }
        }
    }

    public abstract void generate(WordList wordList, int i);

    public abstract char[][] getMatrixRandomize();

    public abstract Model.PuzzleType getPuzzleType();

    public int size() {
        return FinishedList.size();
    }

    public void setContinue(boolean pBool) {
        try {
            mutex.acquire();
            semaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("aquire semaphore error..");
            System.exit(0);
        }
        m_continue = pBool;
        semaphore.release();
        mutex.release();
    }

    public boolean getContinue() {
        try {
            mutex.acquire();
            semaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("aquire semaphore error..");
            System.exit(0);
        }
        boolean moveOn = m_continue;
        semaphore.release();
        mutex.release();
        return moveOn;
    }

    public char[][] getMatrixSolution() {
        return map;
    }

    public WordMap getWordsUsed() {
        return FinishedList;
    }

    public void setWordsUsed(WordMap words) {
        FinishedList = words;
    }

    public WordMap getWordsNotUsed() {
        return RemovedList;
    }

    protected void populateWordMatrix(WordMap tempMap) {
        int i;
        map = (char[][]) Array.newInstance(Character.TYPE, new int[]{tempMap.getBound(), tempMap.getBound()});
        for (i = 0; i < map.length; i++) {
            int j;
            for (j = 0; j < map[i].length; j++) {
                map[i][j] = '~';
            }
        }
        for (i = 0; i < tempMap.size(); i++) {
            Word tempW = tempMap.get(i);
            for (int j = 0; j < tempW.size(); j++) {
                int y = tempW.getCharPosY(j);
                map[y][tempW.getCharPosX(j)] = tempW.getCharAt(j);
            }
        }
    }

    public boolean check_correctBounds(Word tempW, WordMap tempMap) {

        if (tempW.checkBounds(tempMap.getBound()) && tempW.moveBounds(tempMap.getBound())) {
            return true;
        }
        return false;
    }

    protected Word randomizeWord(WordMap tempMap, int end) {
        Word tempW = tempMap.remove(mRandom.nextInt(end));
        tempW.setFirstCharPos(mRandom.nextInt(tempMap.getBound()), mRandom.nextInt(tempMap.getBound()));
        int rLR = mRandom.nextInt(3);
        int rUD = mRandom.nextInt(3);
        if (rUD == 0) {
            tempW.setUp(true);
        } else if (rUD == 1) {
            tempW.setDown(true);
        } else if (rUD == 2) {
            tempW.setUp(false);
            tempW.setDown(false);
        }
        if (rLR == 0) {
            tempW.setLeft(true);
        } else if (rLR == 1) {
            tempW.setRight(true);
        } else if (rLR == 2) {
            tempW.setLeft(false);
            tempW.setRight(false);
        }
        return tempW;
    }

    public void populateWordMatrix(char[][] tempMap) {
        map = (char[][]) Array.newInstance(Character.TYPE, new int[]{tempMap.length, tempMap[0].length});
        for (int i = 0; i < tempMap.length; i++) {
            for (int j = 0; j < tempMap[i].length; j++) {
                map[i][j] = tempMap[i][j];
            }
        }
    }

    public void populateSolutionMatrix(char[][] tempMap) {
        map = (char[][]) Array.newInstance(Character.TYPE, new int[]{tempMap.length, tempMap[0].length});
        for (int i = 0; i < tempMap.length; i++) {
            for (int j = 0; j < tempMap[i].length; j++) {
                map[i][j] = tempMap[i][j];
            }
        }
    }
}

package com.thirudet.wordfindpuzzle.wordsearchcore;

import java.util.ArrayList;

public class Crossword extends Puzzle {
    public void generate(WordList words, int psize) {
        this.FinishedList.clear();
        WordMap mList = new WordMap(words, psize, false);
        WordMap SolvingPuzzle = new WordMap(false);
        try {
            this.FinishedList.setBound(psize);
            SolvingPuzzle.setBound(psize);
        } catch (Exception e) {
            System.out.println("Bound is < 10");
            System.exit(0);
        }
        new Thread(new PuzzleStop(this)).start();
        for (int size = mList.size(); size > 0; size--) {
            Word random = randomizeWord(mList, size);
            random.setFirstCharPos(0, 0);
            SolvingPuzzle.add(random.clone());
            if (rec(mList.clone(), SolvingPuzzle)) {
                break;
            }
            SolvingPuzzle.remove(0);
            mList.add(random);
        }
        setContinue(false);
        this.FinishedList.TranslatePositionalStateOfWordsToTheConditionOfBeingNotNegative();
        populateWordMatrix(this.FinishedList);
    }

    public ArrayList<WordIntersection> getPossibleIntersections(WordMap tempWM, Word tempW) {
        ArrayList<WordIntersection> pointList = new ArrayList();
        for (int i = 0; i < tempWM.size(); i++) {
            Word tempP = (Word) tempWM.get(i);
            for (int j = 0; j < tempP.size(); j++) {
                for (int k = 0; k < tempW.size(); k++) {
                    if (tempP.getCharAt(j) == tempW.getCharAt(k)) {
                        pointList.add(new WordIntersection(i, j, k));
                    }
                }
            }
        }
        return pointList;
    }

    public boolean checkSetWordWorks(WordMap B, Word random, WordIntersection randomIntersection) {
        boolean z;
        Word tempWordB = (Word) B.get(randomIntersection.getWordIndex());
        int bX = tempWordB.getCharPosX(randomIntersection.getWordAPosition());
        int bY = tempWordB.getCharPosY(randomIntersection.getWordAPosition());
        random.setDown(!tempWordB.getDown());
        if (tempWordB.getRight()) {
            z = false;
        } else {
            z = true;
        }
        random.setRight(z);
        if (random.getDown()) {
            bY -= randomIntersection.getWordBPosition();
        } else {
            bX -= randomIntersection.getWordBPosition();
        }
        random.setFirstCharPos(bX, bY);
        if (random.checkCollision(B) || checkBuntingWords(random, B) || checkOverlappingWords(random, B)) {
            return false;
        }
        return true;
    }

    private boolean checkOverlappingWords(Word temp, WordMap tempList) {
        int i = 0;
        while (i < tempList.size()) {
            if (temp.overlap((Word) tempList.get(i)) && (temp.getDown() == ((Word) tempList.get(i)).getDown() || temp.getRight() == ((Word) tempList.get(i)).getRight())) {
                return true;
            }
            i++;
        }
        return false;
    }

    private boolean checkBuntingWords(Word temp, WordMap tempList) {
        int wordSmallestX = temp.getSmallestX();
        int wordLargestX = temp.getLargestX();
        int wordSmallestY = temp.getSmallestY();
        int wordLargestY = temp.getLargestY();
        for (int i = 0; i < tempList.size(); i++) {
            Word compareWord = (Word) tempList.get(i);
            if (!temp.overlap(compareWord) && wordSmallestX <= compareWord.getLargestX() + 1 && wordLargestX + 1 >= compareWord.getSmallestX() && wordSmallestY <= compareWord.getLargestY() + 1 && wordLargestY + 1 >= compareWord.getSmallestY()) {
                return true;
            }
        }
        return false;
    }

    private boolean rec(WordMap A, WordMap B) {
        if (this.FinishedList.size() < B.size()) {
            this.FinishedList = B.clone();
            this.RemovedList = A.clone();
        }
        int Size = A.size();
        boolean moveOn = getContinue();
        if (Size <= 0 || !moveOn) {
            return true;
        }
        while (Size > 0) {
            Word random = randomizeWord(A, Size);
            ArrayList<WordIntersection> possibleIntersections = getPossibleIntersections(B, random);
            for (int PIASize = possibleIntersections.size(); PIASize > 0; PIASize--) {
                if (checkSetWordWorks(B, random, randomizeIntersections(possibleIntersections, PIASize))) {
                    B.add(random.clone());
                    if (checkCrosswordBound(B) && rec(A.clone(), B)) {
                        return true;
                    }
                    B.remove(B.size() - 1);
                }
            }
            A.add(random);
            Size--;
        }
        return false;
    }

    private boolean checkCrosswordBound(WordMap temp) {
        int LargestX = temp.getLargestX();
        int SmallestX = temp.getSmallestX();
        int LargestY = temp.getLargestY();
        int SmallestY = temp.getSmallestY();
        int bound = temp.getBound();
        if (LargestX - SmallestX >= bound || LargestY - SmallestY >= bound) {
            return false;
        }
        return true;
    }

    protected Word randomizeWord(WordMap tempMap, int end) {
        return (Word) tempMap.remove(this.mRandom.nextInt(end));
    }

    private WordIntersection randomizeIntersections(ArrayList<WordIntersection> tempALP, int end) {
        return (WordIntersection) tempALP.remove(this.mRandom.nextInt(end));
    }

    public char[][] getMatrixRandomize() {
        return super.getMatrixSolution();
    }

    public Model.PuzzleType getPuzzleType() {
        return Model.PuzzleType.CROSSWORD;
    }
}

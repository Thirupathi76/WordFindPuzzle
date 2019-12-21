package com.thirudet.wordfindpuzzle.wordsearchcore;

import java.util.ArrayList;
import java.util.Iterator;

public class WordMap extends ArrayList<Word> {
    int m_bound;
    boolean m_checkNeg;
    Point m_indexLargest;
    Point m_indexSmallest;
    Point m_largest;
    Point m_smallest;

    WordMap(WordList temp, int bounds, boolean checkNeg) {
        this.m_largest = null;
        this.m_smallest = null;
        this.m_indexLargest = null;
        this.m_indexSmallest = null;
        this.m_bound = 1;
        this.m_checkNeg = true;
        initalize();
        try {
            setBound(bounds);
        } catch (Exception e) {
            System.out.println("Set bounds error.");
            System.exit(0);
        }
        this.m_checkNeg = checkNeg;
        add(temp);
    }

    WordMap(WordList temp, int bounds) {
        this.m_largest = null;
        this.m_smallest = null;
        this.m_indexLargest = null;
        this.m_indexSmallest = null;
        this.m_bound = 1;
        this.m_checkNeg = true;
        initalize();
        this.m_bound = bounds;
        add(temp);
    }

    WordMap(WordList temp) {
        this.m_largest = null;
        this.m_smallest = null;
        this.m_indexLargest = null;
        this.m_indexSmallest = null;
        this.m_bound = 1;
        this.m_checkNeg = true;
        initalize();
        add(temp);
    }

    WordMap(boolean checkNeg) {
        this();
        this.m_checkNeg = checkNeg;
    }

    WordMap() {
        this.m_largest = null;
        this.m_smallest = null;
        this.m_indexLargest = null;
        this.m_indexSmallest = null;
        this.m_bound = 1;
        this.m_checkNeg = true;
        initalize();
    }

    private void initalize() {
        this.m_bound = 10;
        this.m_largest = new Point(0, 0);
        this.m_smallest = new Point(0, 0);
        this.m_indexLargest = new Point(-1, -1);
        this.m_indexSmallest = new Point(-1, -1);
    }

    private void checkAllForLargest() {
        this.m_largest.setX(((Word) get(0)).getLargestX());
        this.m_largest.setY(((Word) get(0)).getLargestY());
        for (int i = 1; i < size(); i++) {
            checkLargest((Word) get(i), i);
        }
    }

    private void checkAllForSmallest() {
        this.m_smallest.setX(((Word) get(0)).getSmallestX());
        this.m_smallest.setY(((Word) get(0)).getSmallestY());
        for (int i = 1; i < size(); i++) {
            checkSmallest((Word) get(i), i);
        }
    }

    private boolean add(WordList temp) {
        for (int i = 0; i < temp.size(); i++) {
            add(new Word((String) temp.get(i), this.m_checkNeg));
        }
        return true;
    }

    public WordMap clone() {
        WordMap temp = (WordMap) super.clone();
        temp.m_bound = this.m_bound;
        temp.m_checkNeg = this.m_checkNeg;
        temp.m_indexLargest = this.m_indexLargest.clone();
        temp.m_indexSmallest = this.m_indexSmallest.clone();
        temp.m_largest = this.m_largest.clone();
        return temp;
    }

    private void addOffset(Point hPoint) {
        Iterator it = iterator();
        while (it.hasNext()) {
            Word feWord = (Word) it.next();
            feWord.setFirstCharPos(feWord.getCharPosX(0) - hPoint.getX(), feWord.getCharPosY(0) - hPoint.getY());
        }
    }

    public void TranslatePositionalStateOfWordsToTheConditionOfBeingNotNegative() {
        Point tP = new Point();
        tP.setX(getSmallestX());
        tP.setY(getSmallestY());
        if (tP.getX() < 0 || tP.getY() < 0) {
            addOffset(tP);
        }
    }

    public boolean add(WordMap temp) {
        for (int i = 0; i < temp.size(); i++) {
            add(new Word((Word) temp.get(i)));
        }
        return true;
    }

    public int getBound() {
        return this.m_bound;
    }

    public void setBound(int tempB) throws Exception {
        if (tempB >= 8) {
            this.m_bound = tempB;
            return;
        }
        throw new Exception();
    }

    public int getLongestWord() {
        if (size() <= 0) {
            return -1;
        }
        int lrg = ((Word) get(0)).size();
        for (int i = 1; i < size(); i++) {
            if (lrg < ((Word) get(i)).size()) {
                lrg = ((Word) get(i)).size();
            }
        }
        return lrg;
    }

    public int getLargestX() {
        checkAllForLargest();
        return this.m_largest.getX();
    }

    public int posLargestX() {
        checkAllForLargest();
        return this.m_indexLargest.getX();
    }

    public int getLargestY() {
        checkAllForLargest();
        return this.m_largest.getY();
    }

    public int posLargestY() {
        checkAllForLargest();
        return this.m_indexLargest.getY();
    }

    public int getSmallestX() {
        checkAllForSmallest();
        return this.m_smallest.getX();
    }

    public int posSmallestX() {
        checkAllForSmallest();
        return this.m_indexSmallest.getX();
    }

    public int getSmallestY() {
        checkAllForSmallest();
        return this.m_smallest.getY();
    }

    public int posSmallestY() {
        checkAllForSmallest();
        return this.m_indexSmallest.getY();
    }

    public WordList toWordList() {
        WordList list = new WordList();
        Iterator it = iterator();
        while (it.hasNext()) {
            list.add(((Word) it.next()).toString());
        }
        return list;
    }

    private void checkLargest(Word temp, int index) {
        if (temp.getLargestX() > this.m_largest.getX()) {
            this.m_largest.setX(temp.getLargestX());
            this.m_indexLargest.setX(index);
        }
        if (temp.getLargestY() > this.m_largest.getY()) {
            this.m_largest.setY(temp.getLargestY());
            this.m_indexLargest.setY(index);
        }
    }

    private void checkSmallest(Word temp, int index) {
        if (temp.getSmallestX() < this.m_smallest.getX()) {
            this.m_smallest.setX(temp.getSmallestX());
            this.m_indexSmallest.setX(index);
        }
        if (temp.getSmallestY() < this.m_smallest.getY()) {
            this.m_smallest.setY(temp.getSmallestY());
            this.m_indexSmallest.setY(index);
        }
    }
}

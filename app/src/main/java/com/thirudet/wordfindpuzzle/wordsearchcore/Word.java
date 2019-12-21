package com.thirudet.wordfindpuzzle.wordsearchcore;

import java.util.Iterator;

public class Word implements Cloneable {
    public static final int num_direction = 5;
    private boolean m_checkPosNeg;
    private boolean m_down;
    private Point m_largest;
    private boolean m_left;
    private char[] m_letters;
    private Point m_offset;
    private Point[] m_pos;
    private boolean m_right;
    private Point m_smallest;
    private boolean m_up;

    Word(String temp, boolean checkPos) {
        this(temp);
        this.m_checkPosNeg = checkPos;
    }

    Word(String temp) {
        this.m_letters = null;
        this.m_pos = null;
        this.m_offset = null;
        this.m_checkPosNeg = true;
        initalizePoints(temp.length());
        this.m_offset = new Point();
        this.m_largest = new Point();
        this.m_smallest = new Point();
        setString(temp);
        setRight(true);
    }

    Word(Word tempW) {
        int i;
        this.m_letters = null;
        this.m_pos = null;
        this.m_offset = null;
        this.m_checkPosNeg = true;
        initalizePoints(tempW.m_pos.length);
        this.m_offset = new Point();
        this.m_largest = new Point();
        this.m_smallest = new Point();
        this.m_offset.set(tempW.m_offset);
        this.m_largest.set(tempW.m_largest);
        this.m_smallest.set(tempW.m_smallest);
        this.m_letters = new char[tempW.m_letters.length];
        for (i = 0; i < this.m_letters.length; i++) {
            this.m_letters[i] = tempW.m_letters[i];
        }
        for (i = 0; i < this.m_pos.length; i++) {
            this.m_pos[i].set(tempW.m_pos[i]);
        }
        this.m_left = tempW.m_left;
        this.m_right = tempW.m_right;
        this.m_up = tempW.m_up;
        this.m_down = tempW.m_down;
        this.m_checkPosNeg = tempW.m_checkPosNeg;
    }

    private void initalizePoints(int tempS) {
        this.m_pos = new Point[tempS];
        for (int i = 0; i < this.m_pos.length; i++) {
            this.m_pos[i] = new Point();
        }
    }

    public final int getCharPosX(int c) {
        return this.m_pos[c].getX();
    }

    public final int getCharPosY(int c) {
        return this.m_pos[c].getY();
    }

    public int getLargestX() {
        return this.m_largest.getX();
    }

    public int getLargestY() {
        return this.m_largest.getY();
    }

    public int getSmallestY() {
        return this.m_smallest.getY();
    }

    public int getSmallestX() {
        return this.m_smallest.getX();
    }

    public boolean getUp() {
        return this.m_up;
    }

    public boolean getDown() {
        return this.m_down;
    }

    public boolean getRight() {
        return this.m_right;
    }

    public boolean getLeft() {
        return this.m_left;
    }

    public int size() {
        return this.m_letters.length;
    }

    public char[] getString() {
        return this.m_letters;
    }

    public String toString() {
        return String.copyValueOf(this.m_letters);
    }

    public char getCharAt(int index) {
        return this.m_letters[index];
    }

    public void setFirstCharPos(int chX, int chY) {
        this.m_pos[0].setX(chX);
        this.m_pos[0].setY(chY);
        resetPos();
    }

    public void setUp(boolean temp) {
        this.m_up = temp;
        if (this.m_up) {
            this.m_offset.setY(-1);
            this.m_down = false;
        } else if (!this.m_down) {
            this.m_offset.setY(0);
        }
        CheckDirection();
        resetPos();
    }

    public void setDown(boolean temp) {
        this.m_down = temp;
        if (this.m_down) {
            this.m_offset.setY(1);
            this.m_up = false;
        } else if (!this.m_up) {
            this.m_offset.setY(0);
        }
        CheckDirection();
        resetPos();
    }

    public void setRight(boolean temp) {
        this.m_right = temp;
        if (this.m_right) {
            this.m_offset.setX(1);
            this.m_left = false;
        } else if (!this.m_left) {
            this.m_offset.setX(0);
        }
        CheckDirection();
        resetPos();
    }

    public void setLeft(boolean temp) {
        this.m_left = temp;
        if (this.m_left) {
            this.m_offset.setX(-1);
            this.m_right = false;
        } else if (!this.m_right) {
            this.m_offset.setX(0);
        }
        CheckDirection();
        resetPos();
    }

    public Word clone() {
        try {
            Word temp = (Word) super.clone();
            temp.m_pos = new Point[this.m_pos.length];
            for (int i = 0; i < this.m_pos.length; i++) {
                temp.m_pos[i] = this.m_pos[i].clone();
            }
            temp.m_offset = this.m_offset.clone();
            temp.m_smallest = this.m_smallest.clone();
            temp.m_largest = this.m_largest.clone();
            return temp;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone Not supported");
        }
    }

    public void setString(String temp) {
        this.m_letters = (char[]) temp.toCharArray().clone();
        initalizePoints(this.m_letters.length);
    }

    public void resetPos() {
        if (this.m_letters.length != this.m_pos.length) {
            System.out.println("LETTER SIZE DOESN'T MATCH POS!!");
            System.exit(0);
            return;
        }
        int i;
        for (i = 1; i < this.m_letters.length; i++) {
            this.m_pos[i].setX(this.m_pos[i - 1].getX() + this.m_offset.getX());
        }
        for (i = 1; i < this.m_letters.length; i++) {
            this.m_pos[i].setY(this.m_pos[i - 1].getY() + this.m_offset.getY());
        }
        if (this.m_checkPosNeg) {
            checkNegatives();
        }
        ObtainGreatest();
        ObtainSmallest();
    }

    private void checkNegatives() {
        int i;
        int leastX = 0;
        int leastY = 0;
        for (i = 0; i < this.m_pos.length; i++) {
            int gX = this.m_pos[i].getX();
            int gY = this.m_pos[i].getY();
            if (gX < 0 && gX < leastX) {
                leastX = gX;
            }
            if (gY < 0 && gY < leastY) {
                leastY = gY;
            }
        }
        for (i = 0; i < this.m_pos.length; i++) {
            this.m_pos[i].setX(this.m_pos[i].getX() - leastX);
            this.m_pos[i].setY(this.m_pos[i].getY() - leastY);
        }
    }

    private void ObtainGreatest() {
        if (this.m_right) {
            this.m_largest.setX(this.m_pos[this.m_pos.length - 1].getX());
        } else {
            this.m_largest.setX(this.m_pos[0].getX());
        }
        if (this.m_down) {
            this.m_largest.setY(this.m_pos[this.m_pos.length - 1].getY());
        } else {
            this.m_largest.setY(this.m_pos[0].getY());
        }
    }

    private void ObtainSmallest() {
        if (this.m_left) {
            this.m_smallest.setX(this.m_pos[this.m_pos.length - 1].getX());
        } else {
            this.m_smallest.setX(this.m_pos[0].getX());
        }
        if (this.m_up) {
            this.m_smallest.setY(this.m_pos[this.m_pos.length - 1].getY());
        } else {
            this.m_smallest.setY(this.m_pos[0].getY());
        }
    }

    private boolean CheckDirection() {
        if (this.m_left || this.m_right || this.m_up || this.m_down) {
            return true;
        }
        setRight(true);
       /* if (1 != null) {
            return false;
        }*/
        return true;
    }

    public boolean overlap(Word tempW) {
        if (tempW != null) {
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < tempW.size(); j++) {
                    if (comparePos(i, tempW, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkCollision(WordMap tempM) {
        Iterator it = tempM.iterator();
        while (it.hasNext()) {
            if (checkCollision((Word) it.next())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkCollision(Word tempW) {
        if (tempW != null) {
            return checkLettersPos(tempW);
        }
        return false;
    }

    private boolean checkLettersPos(Word tempW) {
        if (tempW == null) {
            return false;
        }
        boolean rtn = true;
        int index = 0;
        while (rtn && index < size()) {
            for (int j = 0; j < tempW.size() && rtn; j++) {
                if (comparePos(index, tempW, j)) {
                    rtn = compareChar(index, tempW, j);
                }
            }
            index++;
        }
        if (rtn) {
            return false;
        }
        return true;
    }

    private boolean comparePos(int mytempI, Word tempW, int tempI) {
        if (tempW != null && tempW.getCharPosX(tempI) == getCharPosX(mytempI) && tempW.getCharPosY(tempI) == getCharPosY(mytempI)) {
            return true;
        }
        return false;
    }

    private boolean compareChar(int mytempI, Word tempW, int tempI) {
        if (tempW == null || tempW.getCharAt(tempI) != getCharAt(mytempI)) {
            return false;
        }
        return true;
    }

    public boolean checkBounds(int bound) {
        if (getLargestX() < bound && getLargestY() < bound) {
            return false;
        }
        return true;
    }

    public boolean moveBounds(int bound) {
        int ChangeX = getCharPosX(0);
        int ChangeY = getCharPosY(0);
        int LargestX = getLargestX();
        int LargestY = getLargestY();
        if (bound <= getLargestX()) {
            ChangeX = (ChangeX - (LargestX - bound)) - 1;
        }
        if (bound <= getLargestY()) {
            ChangeY = (ChangeY - (LargestY - bound)) - 1;
        }
        setFirstCharPos(ChangeX, ChangeY);
        ObtainGreatest();
        ObtainSmallest();
        return checkBounds(bound);
    }
}

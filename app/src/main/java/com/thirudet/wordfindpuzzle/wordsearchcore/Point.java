package com.thirudet.wordfindpuzzle.wordsearchcore;

public class Point implements Cloneable {
    /* renamed from: x */
    private int scaX;
    /* renamed from: y */
        private int scaY;

    Point(int tx, int ty) {
        this.scaX = tx;
        this.scaY = ty;
    }

    Point() {
        this.scaX = 0;
        this.scaY = 0;
    }

    void setX(int temp) {
        this.scaX = temp;
    }

    void setY(int temp) {
        this.scaY = temp;
    }

    int getX() {
        return this.scaX;
    }

    int getY() {
        return this.scaY;
    }

    void set(Point tempP) {
        this.scaX = tempP.getX();
        this.scaY = tempP.getY();
    }

    public Point clone() {
        try {
            return (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone will not support");
        }
    }
}

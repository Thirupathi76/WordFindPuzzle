package com.thirudet.wordfindpuzzle.wordsearchcore;

public class WordIntersection {
    private int m_wordAPosition = 0;
    private int m_wordBPosition = 0;
    private int m_wordIndex = 0;

    WordIntersection(int wordIndex, int wordAPos, int wordBPos) {
        this.m_wordIndex = wordIndex;
        this.m_wordAPosition = wordAPos;
        this.m_wordBPosition = wordBPos;
    }

    public int getWordIndex() {
        return this.m_wordIndex;
    }

    public void setWordIndex(int index) {
        this.m_wordIndex = index;
    }

    public int getWordAPosition() {
        return this.m_wordAPosition;
    }

    public void setWordAPosition(int pos) {
        this.m_wordAPosition = pos;
    }

    public int getWordBPosition() {
        return this.m_wordBPosition;
    }

    public void setWordBPosition(int pos) {
        this.m_wordBPosition = pos;
    }

    public WordIntersection clone() {
        this.m_wordIndex = m_wordIndex;
        this.m_wordAPosition = m_wordAPosition;
        this.m_wordBPosition = m_wordBPosition;
        return null;
    }
}

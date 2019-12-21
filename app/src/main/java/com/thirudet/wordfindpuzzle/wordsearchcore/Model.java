package com.thirudet.wordfindpuzzle.wordsearchcore;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Model {
    private WordList m_list;
    private Puzzle m_puzzle;

    public enum PuzzleType {
        CROSSWORD,
        WORDSEARCH
    }

    public Model() {

        m_list = new WordList();
        choosePuzzle(PuzzleType.WORDSEARCH);
    }

    public void choosePuzzle(PuzzleType puzType) {
        if (puzType == PuzzleType.CROSSWORD) {
            m_puzzle = new Crossword();
        } else if (puzType == PuzzleType.WORDSEARCH) {
            m_puzzle = new Wordsearch();
        }
    }

    public PuzzleType getPuzzleType() {
        return m_puzzle.getPuzzleType();
    }

    public char[][] getMatrixSolution() {
        return this.m_puzzle.getMatrixSolution();
    }

    public boolean add(String temp) {
        return this.m_list.add(temp);
    }

    public void remove(String temp) {
        this.m_list.remove(temp);
    }

    public void removeAll() {
        this.m_list.clear();
    }

    public void savePuzzle(String temp) throws IOException {
        FileHandler.savePuzzleText(temp, this.m_puzzle);
    }

    public void loadPuzzle(InputStream temp) throws IOException {
        this.m_puzzle = FileHandler.loadPuzzleText(temp);
        this.m_list = this.m_puzzle.getWordsUsed().toWordList();
    }

    public void loadWordList(InputStream temp) throws IOException {
        m_list = FileHandler.loadWordList(temp);
        Log.e("List data chars", m_list.toString());
    }

    public void saveWordList(String temp) throws IOException {
        FileHandler.saveWordList(temp, this.m_list);
    }


    public void export(String fileName, boolean isSolution) throws IOException {
        if (this.m_puzzle.getPuzzleType() == PuzzleType.CROSSWORD) {
            if (isSolution) {
                FileHandler.exportCrosswordSolution(fileName, this.m_puzzle.getMatrixSolution());
            } else {
                FileHandler.exportCrossword(fileName, this.m_puzzle.getMatrixSolution(), this.m_puzzle.getWordsUsed().toWordList());
            }
        } else if (isSolution) {
            FileHandler.exportWordSearchSolution(fileName, this.m_puzzle.getMatrixSolution());
        } else {
            FileHandler.exportWordSearch(fileName, this.m_puzzle.getMatrixRandomize(), this.m_puzzle.getWordsUsed().toWordList());
        }
    }

    public void generate(int size) {
        this.m_puzzle.generate(this.m_list, size);
    }

    public void generate() {
        m_puzzle.generate(this.m_list, 10);
    }

    public char[][] getMatrix() {
        return m_puzzle.getMatrixRandomize();
    }

    public String[] getwordList() {
        return (String[]) this.m_list.toArray(new String[this.m_list.size()]);
    }

    public int size() {
        return this.m_puzzle.size();
    }

    public ArrayList<String> getWordsNotUsed() {
        return this.m_puzzle.getWordsNotUsed().toWordList();
    }

    public WordMap GetFinishedList() {
        return this.m_puzzle.FinishedList;
    }
}

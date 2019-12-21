package com.thirudet.wordfindpuzzle.wordsearchcore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class WordList extends ArrayList<String> {
    private static ArrayList<String> m_profanityList;

    public WordList() {
        if (m_profanityList == null) {
            m_profanityList = FileHandler.loadProfanityList();
        }
    }

    public boolean add(String wordLine) {
        if (wordLine.length() < 3 || wordLine.length() > 10) {
            return false;
        }
        super.add(wordLine);
        return true;
    }

    public void add(WordList addend) {
        Iterator it = addend.iterator();
        while (it.hasNext()) {
            add((String) it.next());
        }
    }

    private boolean isLegalWord(String word) {
        return true;
    }

    public boolean remove(String wordLine) {
        boolean wordRemoved = false;
        Scanner strScan = new Scanner(wordLine.toLowerCase());
        while (strScan.hasNext()) {
            if (super.remove(strScan.next())) {
                wordRemoved = true;
            }
        }
        return wordRemoved;
    }
}

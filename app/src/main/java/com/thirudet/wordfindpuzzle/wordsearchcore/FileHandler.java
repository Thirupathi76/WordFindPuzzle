package com.thirudet.wordfindpuzzle.wordsearchcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class FileHandler {
    public static ArrayList<String> loadProfanityList() {
        return new ArrayList();
    }

    public static WordList loadWordList(InputStream inputStream) throws IOException {
        WordList words = new WordList();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "ISO-8859-9"));
        String line = br.readLine();
        while (line != null) {
            if (line.length() >= 3 && line.length() <= 10) {
                words.add(line);
            }
            line = br.readLine();
        }
        inputStream.close();
        return words;
    }

    public static void saveWordList(String fileName, WordList words) throws IOException {
        FileWriter writer = createFileWriter(fileName, "word list");
        writeWordList(writer, words);
        writer.close();
    }

    public static Puzzle loadPuzzleText(InputStream fileName) throws IOException {
        Scanner fileScan = createFileScanner(fileName, "word list");
        WordList words = new WordList();
        try {
            Puzzle resultPuzzle;
            if (fileScan.next().equals("CROSSWORD")) {
                resultPuzzle = new Crossword();
            } else {
                resultPuzzle = new Wordsearch();
            }
            int size = fileScan.nextInt();
            fileScan.nextLine();
            resultPuzzle.populateWordMatrix(readPuzzleMatrix(fileScan, size));
            if (resultPuzzle.getPuzzleType() == Model.PuzzleType.WORDSEARCH) {
                resultPuzzle.populateSolutionMatrix(readPuzzleMatrix(fileScan, size));
            }
            resultPuzzle.setWordsUsed(new WordMap(readWordList(fileScan)));
            fileScan.close();
            return resultPuzzle;
        } catch (Exception e) {
            throw new IOException("Could not read the puzzle from the file.");
        }
    }

    public static void savePuzzleText(String fileName, Puzzle puzzle) throws IOException {
        FileWriter writer = createFileWriter(fileName, "puzzle");
        try {
            if (puzzle.getPuzzleType() == Model.PuzzleType.CROSSWORD) {
                writer.write("CROSSWORD\n");
            } else {
                writer.write("WORDSEARCH\n");
            }
            char[][] puzzleArray = puzzle.getMatrixRandomize();
            writer.write(new StringBuilder(String.valueOf(String.valueOf(puzzleArray.length))).append('\n').toString());
            writePuzzleMatrix(writer, puzzleArray);
            if (puzzle.getPuzzleType() == Model.PuzzleType.WORDSEARCH) {
                writePuzzleMatrix(writer, puzzle.getMatrixSolution());
            }
            writeWordList(writer, puzzle.getWordsUsed().toWordList());
            writer.close();
        } catch (Exception e) {
            throw new IOException("Could not write the puzzle to the file.");
        }
    }

    public static void exportCrossword(String fileName, char[][] puzzleMatrix, WordList words) throws IOException {
        FileWriter writer = createFileWriter(fileName, "HTML");
        try {
            writeHTMLCrossword(writer, puzzleMatrix, false);
            writer.write("<h4>Words:</h4>\n");
            Iterator it = words.iterator();
            while (it.hasNext()) {
                writer.write(new StringBuilder(String.valueOf((String) it.next())).append("<br>\n").toString());
            }
            writer.write("</html>\n");
            writer.close();
        } catch (Exception e) {
            throw new IOException("Could not write the HTML.");
        }
    }

    public static void exportCrosswordSolution(String fileName, char[][] puzzleMatrix) throws IOException {
        FileWriter writer = createFileWriter(fileName, "HTML");
        try {
            writeHTMLCrossword(writer, puzzleMatrix, true);
            writer.write("</html>\n");
            writer.close();
        } catch (Exception e) {
            throw new IOException("Could not write the HTML.");
        }
    }

    public static void exportWordSearch(String fileName, char[][] puzzleMatrix, WordList words) throws IOException {
        FileWriter writer = createFileWriter(fileName, "HTML");
        try {
            writer.write("<html>\n<head>\n");
            writer.write("<title>Yalpha Generated Word Search</title>\n");
            writer.write("<style type=\"text/css\">\n");
            writer.write("td.wordSearch {\n");
            writer.write("  border: 0px none;\n");
            writer.write("  text-align: center;\n");
            writer.write("  width: 30px;\n");
            writer.write("  height: 30px;\n");
            writer.write("}\ntable.wordSearch {\n");
            writer.write("  border: 2px solid black;\n");
            writer.write("}\n</style>\n<body>\n<h3>Word Search</h3>\n");
            writeHTMLWordSearch(writer, puzzleMatrix);
            writer.write("<h4>Words:</h4>\n");
            Iterator it = words.iterator();
            while (it.hasNext()) {
                writer.write(new StringBuilder(String.valueOf((String) it.next())).append("<br>\n").toString());
            }
            writer.write("</html>\n");
            writer.close();
        } catch (Exception e) {
            throw new IOException("Could not write the HTML.");
        }
    }

    public static void exportWordSearchSolution(String fileName, char[][] puzzleMatrix) throws IOException {
        FileWriter writer = createFileWriter(fileName, "HTML");
        try {
            writer.write("<html>\n<head>\n");
            writer.write("<title>Yalpha Generated Word Search Solution</title>\n");
            writer.write("<style type=\"text/css\">\n");
            writer.write("td.wordSearch {\n");
            writer.write("  border: 1px solid black;\n");
            writer.write("  text-align: center;\n");
            writer.write("  width: 30px;\n");
            writer.write("  height: 30px;\n");
            writer.write("}\ntable.wordSearch {\n");
            writer.write("  border: 1px solid black;\n");
            writer.write("}\n</style>\n<body>\n<h3>Word Search Solution</h3>\n");
            writeHTMLWordSearch(writer, puzzleMatrix);
            writer.write("</html>\n");
            writer.close();
        } catch (Exception e) {
            throw new IOException("Could not write the HTML.");
        }
    }

    private static WordList readWordList(Scanner fileScan) throws IOException {
        WordList words = new WordList();
        while (fileScan.hasNext()) {
            try {
                words.add(fileScan.next());
            } catch (Exception e) {
                throw new IOException("Could not read the word list from the file.");
            }
        }
        return words;
    }

    private static void writeWordList(FileWriter writer, WordList words) throws IOException {
        int i = 0;
        while (i < words.size()) {
            try {
                writer.write(new StringBuilder(String.valueOf((String) words.get(i))).append('\n').toString());
                i++;
            } catch (Exception e) {
                throw new IOException("Could not write the word list to file.");
            }
        }
    }

    private static char[][] readPuzzleMatrix(Scanner fileScan, int size) throws IOException {
        char[][] puzzleArray = (char[][]) Array.newInstance(Character.TYPE, new int[]{size, size});
        for (int i = 0; i < size; i++) {
            String line = fileScan.nextLine();
            for (int j = 0; j < size; j++) {
                puzzleArray[i][j] = line.charAt(j * 2);
            }
        }
        return puzzleArray;
    }

    private static void writePuzzleMatrix(FileWriter writer, char[][] puzzleArray) throws IOException {
        for (int i = 0; i < puzzleArray.length; i++) {
            for (char write : puzzleArray[i]) {
                writer.write(write);
                writer.write(32);
            }
            writer.write(10);
        }
    }

    private static void writeHTMLCrossword(FileWriter writer, char[][] puzzleMatrix, boolean isSolution) throws IOException {
        int col;
        String titlePart = isSolution ? "Solution" : "";
        writer.write("<html>\n<head>\n");
        writer.write("<title>Yalpha Generated Crossword " + titlePart + "</title>\n");
        writer.write("<style type=\"text/css\">\n");
        writer.write("table.crossword { ");
        writer.write("border: 0px none;");
        writer.write(" }\ntd.puzzleCell {\n");
        writer.write("  text-align: center;\n");
        writer.write("  width: 30px;\n");
        writer.write("  height: 30px;\n");
        writer.write("}\ntd.borderCellX {\n");
        writer.write("  border: 0px none;\n");
        writer.write("  width: 5px;\n");
        writer.write("}\ntd.borderCellY {\n");
        writer.write("  border: 0px none;\n");
        writer.write("  height: 5px;\n");
        writer.write("}\ntd.open { ");
        writer.write("border: 0px none;");
        writer.write(" }\ntd.closed { ");
        writer.write("border: 1px solid black;");
        writer.write(" }\ntd.BT { ");
        writer.write("border-top: 1px solid black;");
        writer.write(" }\ntd.BL { ");
        writer.write("border-left: 1px solid black;");
        writer.write(" }\ntd.BB { ");
        writer.write("border-bottom: 1px solid black;");
        writer.write(" }\ntd.BR { ");
        writer.write("border-right: 1px solid black;");
        writer.write(" }\n</style>\n<body>\n<h3>Crossword " + titlePart + "</h3>\n");
        writer.write("<table class=\"crossword\" cellspacing=\"0\" cellpadding=\"5\">\n");
        int height = puzzleMatrix.length;
        int width = puzzleMatrix.length;
        int lastRow = height - 1;
        int lastCol = width - 1;
        writer.write("<tr>\n<td class=\"borderCellY\">&nbsp;</td>");
        for (char c : puzzleMatrix[0]) {
            writer.write("<td class=\"borderCellY");
            if (c != '~') {
                writer.write(" BB");
            }
            writer.write("\">&nbsp;</td>");
        }
        writer.write("\n</tr>\n");
        int row = 0;
        while (row < puzzleMatrix.length) {
            writer.write("<tr>\n");
            writer.write("<td class=\"borderCellX ");
            if (puzzleMatrix[row][0] != '~') {
                writer.write(" BR");
            }
            writer.write("\">&nbsp;</td>");
            col = 0;
            while (col < height) {
                char c2 = puzzleMatrix[row][col];
                if (c2 == '~') {
                    writer.write("<td class=\"puzzleCell open");
                    if (col > 0 && puzzleMatrix[row][col - 1] != '~') {
                        writer.write(" BL");
                    }
                    if (row > 0 && puzzleMatrix[row - 1][col] != '~') {
                        writer.write(" BT");
                    }
                    if (col < lastCol && puzzleMatrix[row][col + 1] != '~') {
                        writer.write(" BR");
                    }
                    if (row < lastRow && puzzleMatrix[row + 1][col] != '~') {
                        writer.write(" BB");
                    }
                    writer.write("\">&nbsp;</td>");
                } else {
                    writer.write("<td class=\"puzzleCell closed\">");
                    if (isSolution) {
                        writer.write(c2);
                    } else {
                        writer.write("&nbsp;");
                    }
                    writer.write("</td>");
                }
                col++;
            }
            writer.write("<td class=\"borderCellX ");
            if (puzzleMatrix[row][lastCol] != '~') {
                writer.write(" BL");
            }
            writer.write("\">&nbsp;</td>");
            writer.write("\n</tr>\n");
            row++;
        }
        writer.write("<tr>\n<td class=\"borderCellY\">&nbsp;</td>");
        for (col = 0; col < width; col++) {
            writer.write("<td class=\"borderCellY");
            if (puzzleMatrix[lastRow][col] != '~') {
                writer.write(" BT");
            }
            writer.write("\">&nbsp;</td>");
        }
        writer.write("\n</tr>\n");
        writer.write("</table>\n");
    }

    private static void writeHTMLWordSearch(FileWriter writer, char[][] puzzleMatrix) throws IOException {
        int width = puzzleMatrix[0].length;
        writer.write("<table class=\"wordSearch\" cellspacing=\"0\" cellpadding=\"5\">\n");
        for (char[] cArr : puzzleMatrix) {
            writer.write("<tr>\n");
            for (int col = 0; col < width; col++) {
                writer.write("<td class=\"wordSearch\">");
                char c = cArr[col];
                if (c == '~') {
                    writer.write("&nbsp;");
                } else {
                    writer.write(c);
                }
                writer.write("</td>");
            }
            writer.write("\n</tr>\n");
        }
        writer.write("</table>\n");
    }

    private static Scanner createFileScanner(InputStream inputStream, String type) throws IOException {
        try {
            return new Scanner(inputStream);
        } catch (Exception e) {
            throw new IOException("Could not open the " + type + " file.");
        }
    }

    private static FileWriter createFileWriter(String fileName, String type) throws IOException {
        try {
            return new FileWriter(new File(fileName));
        } catch (Exception e) {
            throw new IOException("Could not create the " + type + " file.");
        }
    }
}

package com.thirudet.wordfindpuzzle;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.thirudet.wordfindpuzzle.wordsearchcore.WordMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class GameView extends RelativeLayout {

    String SCORE_Str;
    String TIME_Str;
    private Runnable UpdateTimeTask = new TimerTaskListener();
    String WORDS_Str;
    private MainActivity activity;
    private ArrayList<Line> allLines;
    private float cellHeight;
    private float cellWidth;
    private int currentTime = 0;
    private ArrayList<String> foundWords;
    private GridView gridView;
    private int height;
    private boolean isGameFinished = false;
    private Line lastLine;
    private ArrayAdapter<String> listAdapter;
    private Locale locale;
    private Handler mHandler;
    private int numColumns;
    private int numRows;
    private int oldColumn = -1;
    private int oldRow = -1;
    private int point = 0;
    private TextView pointText;
    private int startColumn;
    private int startRow;
    private char[][] table;
    private Paint textPaint;
    private TextView timeText;
    private float topMargin;
    boolean touchDown = false;
    private int width;
    private WordMap wordMap;
    private TextView wordTitleText;
    private final ArrayList<String> wordList;


    class TimerTaskListener implements Runnable {
        TimerTaskListener() {
        }

        public void run() {
            if (isGameFinished) {
                mHandler.removeCallbacks(UpdateTimeTask);
/*                new GameView(activity, table,
                        numRows,
                        numColumns,
                        wordMap);
                return;*/
            }
            GameView gameView = GameView.this;
            gameView.currentTime = gameView.currentTime + 1;
            timeText.setText(new StringBuilder(String.valueOf(TIME_Str)).append(": ").append(CalculateTime(currentTime)).toString());
            mHandler.postDelayed(UpdateTimeTask, 1000);
            invalidate();
        }
    }

    public GameView(Context context, char[][] table, int numRows, int numColumns, WordMap wordMap) {
        super(context);
        activity = (MainActivity) context;
        this.table = table;

        this.numRows = numRows;
        this.numColumns = numColumns;
        this.wordMap = wordMap;
        allLines = new ArrayList();
        foundWords = new ArrayList();
        locale = new Locale("en", "IN");
        Locale.setDefault(locale);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.pattern));
        bitmapDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
        setBackgroundDrawable(bitmapDrawable);
        setWillNotDraw(false);

        TIME_Str = context.getString(R.string.time);
        SCORE_Str = context.getString(R.string.score);
        WORDS_Str = context.getString(R.string.words);

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        topMargin = (float) (((double) (height - width)) / 2.0d);

        cellWidth = ((float) width) / ((float) numColumns);
        cellHeight = ((float) width) / ((float) numRows);

        Log.e("Top margin at 10x10 ", String.valueOf(topMargin) + " \nCell W " + cellHeight + " \nCell H " + cellHeight);
        textPaint = new Paint(1);
        textPaint.setStyle(Style.STROKE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(22.0f * getContext().getResources().getDisplayMetrics().density);
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setTextAlign(Align.CENTER);

        String[] words = new String[0];
        gridView = new GridView(activity);
        gridView.setNumColumns(3);
        wordList = new ArrayList();
        wordList.addAll(Arrays.asList(words));

        listAdapter = new ArrayAdapter(activity, R.layout.simple_row, wordList);

        for (Object aWordMap : wordMap) {
            listAdapter.add((aWordMap).toString().toUpperCase(locale));
        }

        gridView.setAdapter(listAdapter);
        int wordTitleHeight = (int) (((double) topMargin) * 0.15d);

        LayoutParams gridViewParams = new LayoutParams(width, ((int) topMargin - wordTitleHeight));//- wordTitleHeight
        gridViewParams.topMargin = 8;
        gridViewParams.leftMargin = 0;
        gridViewParams.rightMargin = 8;
        gridViewParams.leftMargin = 8;
        Log.e("Word list size H : ", String.valueOf(wordTitleHeight) + " \nTop M : " + topMargin + " \n");
        gridView.setPadding(0, 6, 0, 6);

        gridView.setBackgroundResource(R.drawable.round_corner_stroke);
        addView(gridView, gridViewParams);

        /*LayoutParams layoutParams = new LayoutParams(width, wordTitleHeight);
        layoutParams.topMargin = 8;
        layoutParams.leftMargin = 0;
        wordTitleText = new TextView(activity);
        wordTitleText.setText(WORDS_Str);
        layoutParams.rightMargin = 8;
        layoutParams.leftMargin = 8;
        wordTitleText.setBackgroundColor(Color.argb(100, 32, 178, 170));
        wordTitleText.setGravity(17);
        wordTitleText.setTypeface(Typeface.DEFAULT_BOLD);
        addView(wordTitleText, layoutParams);*/


        LayoutParams timeTextParams = new LayoutParams(width / 2, (int) (((double) topMargin) * 0.25d));
        timeTextParams.topMargin = (int) (((float) height) - (topMargin - 10));
        timeTextParams.leftMargin = 0;
        timeText = new TextView(activity);
        timeText.setText(TIME_Str + " : 0:00");
        timeText.setPadding(timeText.getPaddingLeft() + 30, timeText.getPaddingTop(), timeText.getPaddingRight(), timeText.getPaddingBottom());
        timeText.setBackgroundColor(Color.argb(100, 32, 178, 170));
        timeText.setGravity(19);
        timeText.setTypeface(Typeface.DEFAULT_BOLD);
        addView(timeText, timeTextParams);
        Log.e("Time size H : ", String.valueOf(wordTitleHeight) + " \nTop M : " + timeTextParams.topMargin + " \n w/2 : " + width);

        LayoutParams pointTextParams = new LayoutParams(width / 2, (int) (((double) topMargin) * 0.25d));
        pointTextParams.topMargin = (int) (((float) height) - (topMargin - 10));
        pointTextParams.leftMargin = width / 2;
        pointText = new TextView(activity);
        pointText.setText(SCORE_Str + " : 0");
        pointText.setPadding(pointText.getPaddingLeft(), pointText.getPaddingTop(), pointText.getPaddingRight() + 30, pointText.getPaddingBottom());
        pointText.setBackgroundColor(Color.argb(100, 32, 178, 170));
        pointText.setGravity(21);
        pointText.setTypeface(Typeface.DEFAULT_BOLD);
        addView(pointText, pointTextParams);


        Log.e("Score size H : ", String.valueOf(wordTitleHeight) + " \nTop M : " + pointTextParams.topMargin + " \n w/2 : " + width);

        LayoutParams addParams = new LayoutParams( LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        addParams.addRule(ALIGN_PARENT_BOTTOM);
        addParams.addRule(CENTER_HORIZONTAL);
        LinearLayout adLayout = new LinearLayout(activity);
        adLayout.setBackgroundColor(Color.RED);

        AdView adView = new AdView(activity);
        adView.setAdUnitId(context.getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.BANNER);

        adView.loadAd(new AdRequest.Builder().build());
        addView(adView, addParams);
//        addView(adLayout, addParams);

        mHandler = new Handler();
        mHandler.removeCallbacks(UpdateTimeTask);
        mHandler.postDelayed(UpdateTimeTask, 1000);
    }

    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                String textToDraw = String.valueOf(table[i][j]).toUpperCase(locale);// new StringBuilder(String.valueOf(table[i][j])).toString().toUpperCase(locale);
//                Log.e("textToDraw ", textToDraw);
                Rect bounds = new Rect();
                textPaint.getTextBounds(textToDraw, 0, textToDraw.length(), bounds);
                canvas.drawText(textToDraw, (((float) j) * cellWidth) + (cellWidth / 2.0f), ((topMargin + (((float) i) * cellHeight)) + (cellHeight / 2.0f)) + ((float) ((bounds.bottom - bounds.top) / 2)), textPaint);
            }
        }
        Iterator it = allLines.iterator();
        while (it.hasNext()) {
            ((Line) it.next()).Draw(canvas, cellWidth, cellHeight, topMargin);
        }
        if (lastLine != null) {
            lastLine.Draw(canvas, cellWidth, cellHeight, topMargin);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isGameFinished) {
            int eventaction = event.getAction();
            int X = (int) event.getX();
            int row = GetRow((int) event.getY());
            int column = GetColumn(X);
            if (row < 0 || column < 0 || row >= numRows || column >= numColumns) {
                lastLine = null;
                invalidate();
                return true;
            } else if (touchDown && oldRow == row && oldColumn == column) {
                invalidate();
                return true;
            } else {
                switch (eventaction) {
                    case 0:
                        startRow = row;
                        startColumn = column;
                        touchDown = true;
                        break;
                    case 1:
                        if (lastLine != null && CheckLineValid(startRow, startColumn, lastLine.endRow, lastLine.endColumn)) {
                            String result = CheckWordExists(startRow, startColumn, lastLine.endRow, lastLine.endColumn);
                            Log.e("Result drawn", result);
                            if (!result.equals("")) {
                                boolean alreadyFound = false;
                                if (listAdapter.getPosition(result.toUpperCase(locale)) < 0) {
                                    alreadyFound = true;
                                }
                                if (!alreadyFound) {
                                    allLines.add(GetLine(startRow, startColumn, row, column, false));
                                    foundWords.add(result);
                                    point = (int) (((double) point) + ((((double) result.length()) * (1.0d / ((double) ((currentTime / 15) + 1)))) * 100.0d));
                                    pointText.setText(SCORE_Str + ": " + point);
                                    listAdapter.remove(result.toUpperCase(locale));
                                    if (listAdapter.isEmpty()) {
                                        isGameFinished = true;
                                        activity.SubmitScore(point, CalculateTime(currentTime), wordList);
                                    }
                                }
                            }
                        }
                        lastLine = null;
                        touchDown = false;
                        oldRow = -1;
                        oldColumn = -1;
                        break;
                    case 2:
                        if (!CheckLineValid(startRow, startColumn, row, column)) {
                            lastLine = null;
                            break;
                        }
                        lastLine = GetLine(startRow, startColumn, row, column, true);
                        break;
                }
                invalidate();
            }
        } else {
            new GameView(activity, table,
                    numRows,
                    numColumns,
                    wordMap);
//            new activity.StartGameTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            Toast.makeText(activity, "Game Finished", Toast.LENGTH_SHORT).show();

        }
        return true;
    }

    private Line GetLine(int startRow, int startColumn, int endRow, int endColumn, boolean isLastLine) {
        int startX = startColumn;
        int startY = startRow;
        int endX = endColumn;
        int endY = endRow;
        int X = 0;
        int Y = 0;
        double angle = (Math.atan2((double) (endY - startY), (double) (endX - startX)) * 180.0d) / 3.141592653589793d;
        if ((angle >= -22.5d && angle < 22.5d) || ((angle >= 157.5d && angle <= 180.0d) || (angle >= -180.0d && angle < -157.5d))) {
            X = endX;
            Y = startY;
        } else if ((angle >= 22.5d && angle < 45.0d) || (angle >= -157.5d && angle < -135.0d)) {
            X = endX;
            Y = startY - (startX - endX);
        } else if ((angle >= 45.0d && angle < 67.5d) || (angle >= -135.0d && angle < -112.5d)) {
            X = startX + (endY - startY);
            Y = endY;
        } else if ((angle >= 67.5d && angle < 112.5d) || (angle >= -112.5d && angle < -67.5d)) {
            X = startX;
            Y = endY;
        } else if ((angle >= 112.5d && angle < 135.0d) || (angle >= -67.5d && angle < -45.0d)) {
            X = startX - (endY - startY);
            Y = endY;
        } else if ((angle >= 135.0d && angle < 157.5d) || (angle >= -45.0d && angle < -22.5d)) {
            X = endX;
            Y = startY + (startX - endX);
        }

        return new Line(activity, startRow, startColumn, Y, X, isLastLine);
    }

    private int GetRow(int Y) {
        return (int) ((((float) Y) - topMargin) / cellHeight);
    }

    private int GetColumn(int X) {
        return (int) (((float) X) / cellWidth);
    }

    private boolean CheckLineValid(int startRow, int startColumn, int endRow, int endColumn) {
        Line line = GetLine(startRow, startColumn, endRow, endColumn, true);
        /*if (line == null) {
            return false;
        }*/
        if (line.startRow < 0 || line.startColumn < 0 || line.endRow < 0 || line.endColumn < 0) {
            return false;
        }
        if (line.startRow >= numRows || line.endRow >= numRows || line.startColumn >= numColumns || line.endColumn >= numColumns) {
            return false;
        }
        return true;
    }

    private String CheckWordExists(int startRow, int startColumn, int endRow, int endColumn) {
        int rowDiff = startRow - endRow;
        int columnDiff = startColumn - endColumn;
        if (Math.abs(rowDiff) == Math.abs(columnDiff) && Math.abs(rowDiff) <= 1) {
            return "";
        }
        if (rowDiff == 0 && Math.abs(columnDiff) <= 1) {
            return "";
        }
        if (columnDiff == 0 && Math.abs(rowDiff) <= 1) {
            return "";
        }
        int i;
        String temp = "";
        String tempReverse = "";
        if (Math.abs(rowDiff) == Math.abs(columnDiff)) {
            int j;
            int j2;
            if (startRow > endRow && startColumn > endColumn) {
                i = endRow;
                j = endColumn;
                while (i <= startRow) {
                    j2 = j + 1;
                    temp = new StringBuilder(String.valueOf(temp)).append(table[i][j]).toString();
                    i++;
                    j = j2;
                }
            } else if (startRow < endRow && startColumn < endColumn) {
                i = startRow;
                j = startColumn;
                while (i <= endRow) {
                    j2 = j + 1;
                    temp = new StringBuilder(String.valueOf(temp)).append(table[i][j]).toString();
                    i++;
                    j = j2;
                }
            } else if (startRow > endRow && startColumn < endColumn) {
                i = endRow;
                j = endColumn;
                while (i <= startRow) {
                    j2 = j - 1;
                    temp = new StringBuilder(String.valueOf(temp)).append(table[i][j]).toString();
                    i++;
                    j = j2;
                }
            } else if (startRow < endRow && startColumn > endColumn) {
                i = startRow;
                j = startColumn;
                while (i <= endRow) {
                    j2 = j - 1;
                    temp = new StringBuilder(String.valueOf(temp)).append(table[i][j]).toString();
                    i++;
                    j = j2;
                }
            }
        } else if (rowDiff == 0) {
            if (startColumn > endColumn) {
                for (i = endColumn; i <= startColumn; i++) {
                    temp = new StringBuilder(String.valueOf(temp)).append(table[startRow][i]).toString();
                }
            } else {
                for (i = startColumn; i <= endColumn; i++) {
                    temp = new StringBuilder(String.valueOf(temp)).append(table[startRow][i]).toString();
                }
            }
        } else if (columnDiff == 0) {
            if (startRow > endRow) {
                for (i = endRow; i <= startRow; i++) {
                    temp = new StringBuilder(String.valueOf(temp)).append(table[i][startColumn]).toString();
                }
            } else {
                for (i = startRow; i <= endRow; i++) {
                    temp = new StringBuilder(String.valueOf(temp)).append(table[i][startColumn]).toString();
                }
            }
        }
        for (i = temp.length() - 1; i >= 0; i--) {
            tempReverse = new StringBuilder(String.valueOf(tempReverse)).append(temp.charAt(i)).toString();
        }
        Iterator it = wordMap.iterator();
        ArrayList<String> list = new ArrayList<>();
        list = wordMap.toWordList();
        Log.e("list word", list.get(0));
        if (list.contains(temp))
            return temp;
        else if (list.contains(tempReverse))
            return tempReverse;
        /*while (it.hasNext()) {
//            Word word = (Word) it.next();
            if (!it.next().equals(temp)) {
                if (it.next().equals(tempReverse)) {
                }
            }
            return (String) it.next();
        }*/
        /*while (it.hasNext()) {
            Word word = (Word) it.next();
            if (!word.toString().equals(temp)) {
                if (word.toString().equals(tempReverse)) {
                }
            }
            return word.toString();
        }*/
        return "";
    }

    public void RemoveHandler() {
        mHandler.removeCallbacks(UpdateTimeTask);
    }

    public String CalculateTime(int seconds) {
        int minute = seconds / 60;
        int second = seconds % 60;
        if (second < 10) {
            return new StringBuilder(String.valueOf(minute)).append(":0").append(second).toString();
        }
        return new StringBuilder(String.valueOf(minute)).append(":").append(second).toString();
    }
}

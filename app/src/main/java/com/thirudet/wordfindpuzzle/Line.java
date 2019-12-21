package com.thirudet.wordfindpuzzle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.support.v4.view.MotionEventCompat;
import java.util.ArrayList;

public class Line {
    private int color;
    private float dEndColumn;
    private float dEndRow;
    private float dStartCol;
    private float dStartRow;
    public int endColumn;
    private int endColumnIncrement = 0;
    public int endRow;
    private int endRowIncrement = 0;
    private ArrayList<Integer> lineColors;
    private Paint linePaint;
    public int startColumn;
    private int startColumnIncrement = 0;
    public int startRow;
    private int startRowIncrement = 0;

    public Line(Context context, int startRow, int startColumn, int endRow, int endColumn, boolean isLastLine) {
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.endRow = endRow;
        this.endColumn = endColumn;
        if (startRow == endRow) {
            this.dStartRow = ((float) startRow) + 0.5f;
            this.dStartCol = (float) startColumn;
            this.dEndRow = ((float) endRow) + 0.5f;
            this.dEndColumn = ((float) endColumn) + 1.0f;
            if (startColumn > endColumn) {
                this.dStartCol = ((float) startColumn) + 1.0f;
                this.dEndColumn = (float) endColumn;
            }
        } else if (startColumn == endColumn) {
            this.dStartRow = (float) startRow;
            this.dStartCol = ((float) startColumn) + 0.5f;
            this.dEndRow = ((float) endRow) + 1.0f;
            this.dEndColumn = ((float) endColumn) + 0.5f;
            if (startRow > endRow) {
                this.dStartRow = ((float) startRow) + 1.0f;
                this.dEndRow = (float) endRow;
            }
        } else {
            if (startRow > endRow) {
                this.startRowIncrement = 1;
                this.endRowIncrement = 0;
            } else {
                this.startRowIncrement = 0;
                this.endRowIncrement = 1;
            }
            if (startColumn > endColumn) {
                this.startColumnIncrement = 1;
                this.endColumnIncrement = 0;
            } else {
                this.startColumnIncrement = 0;
                this.endColumnIncrement = 1;
            }
            this.dStartRow = (float) (this.startRowIncrement + startRow);
            this.dStartCol = (float) (this.startColumnIncrement + startColumn);
            this.dEndRow = (float) (this.endRowIncrement + endRow);
            this.dEndColumn = (float) (this.endColumnIncrement + endColumn);
        }
        if (isLastLine) {
            this.color = Color.argb(100, 0, MotionEventCompat.ACTION_MASK, 0);
        } else {
            this.color = MainActivity.GetRandomColor();
        }
        float densityMultiplier = context.getResources().getDisplayMetrics().density;
        this.linePaint = new Paint(1);
        this.linePaint.setStyle(Style.FILL);
        this.linePaint.setColor(this.color);
        if (isLastLine) {
            this.linePaint.setStrokeWidth(32.0f * densityMultiplier);
        } else {
            this.linePaint.setStrokeWidth(24.0f * densityMultiplier);
        }
        this.linePaint.setAntiAlias(true);
        this.linePaint.setDither(true);
        this.linePaint.setTextAlign(Align.CENTER);
    }

    public void Draw(Canvas canvas, float cellWidth, float cellHeight, float topMargin) {
        canvas.drawLine(this.dStartCol * cellWidth, topMargin + (this.dStartRow * cellHeight), this.dEndColumn * cellWidth, topMargin + (this.dEndRow * cellHeight), this.linePaint);
    }
}

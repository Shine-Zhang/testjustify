package com.example.zs.mttextview;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/19 0019.
 */

public class RightAlignTextView extends TextView {


    private int maxWidth;

    /**
     * 用于测量字符宽度
     */
    private TextPaint paint = new TextPaint();

    /**
     * 已绘的行中最宽的一行的宽度
     */
    private float lineWidthMax = -1;

    /**
     * 只有一行时的宽度
     */
    private int oneLineWidth = -1;

    //行距
    private float lineSpacing;
    private int lineSpacingDP = 5;

    private float SPACE_WIDTH = paint.measureText(" ");

    private List<String> wordList = new LinkedList<>();
    public RightAlignTextView(Context context) {
        super(context);
    }

    public RightAlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RightAlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0, height = 0;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                width = widthSize;
                break;
            default:
                break;
        }
        if (maxWidth > 0)
            width = Math.max(width, maxWidth);

        paint.setTextSize(this.getTextSize());
        int realHeight = measureContentHeight((int) width);

        //如果实际行宽少于预定的宽度，减少行宽以使其内容横向居中
        int leftPadding = getCompoundPaddingLeft();
        int rightPadding = getCompoundPaddingRight();
        width = Math.max(width, (int) lineWidthMax + leftPadding + rightPadding);

        if (oneLineWidth > -1) {
            width = oneLineWidth;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = realHeight;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = realHeight;
                break;
            default:
                break;
        }

        height += getCompoundPaddingTop() + getCompoundPaddingBottom();

        setMeasuredDimension(width, height);
    }

    private int measureContentHeight(int width) {
        int drawWidth;
        int drawHight;
        int totalHight = 0;

        return totalHight;
    }

    private  List<String> lineBreak(List<String> words, Paint paint, float contentWidth){

        List<String> lineList = new LinkedList<>();
        List<String> lineWords = new LinkedList<>();
        String myText="";
        float lineAcc = 0;
        boolean isLineHead = true;
        float tmpAcc;
        for(int i = 0; i < words.size() ; i++){
            String increment;
            if(isLineHead) {
                isLineHead = false;
                increment = words.get(i);
            } else {
                increment = " " + words.get(i);
            }
            float increLength = paint.measureText(increment);
            tmpAcc = lineAcc + increLength;
            if (tmpAcc <= contentWidth) {
                lineWords.add(increment);
                lineAcc = tmpAcc;
            } else{
                int totalSpacesToInsert = (int)((contentWidth-lineAcc / SPACE_WIDTH));
                lineList.add(justifyLine(lineWords, totalSpacesToInsert));
                i--;
                lineWords = new LinkedList<>();
                lineAcc = 0;
                isLineHead = true;
            }
        }
        lineList.add(myText);
        return lineList;
    }

    private  String justifyLine(List<String> lineWords,int totalSpacesToInsert){
        if(lineWords.size() == 0) {
            return "";
        }

        String toAppend="";
        int insertSpace = totalSpacesToInsert;
        while(insertSpace > 0){
            toAppend = toAppend + " ";
            insertSpace--;
        }

        if(totalSpacesToInsert > 0) {
            lineWords.set(0,toAppend);
        }
        return mergeNewString(lineWords);
    }

    private String mergeNewString (List<String> words) {
        StringBuilder line = new StringBuilder();
        for(String word : words) {
            line.append(word);
        }
        return line.toString();
    }

    public void setAlignText(String text) {
        wordList.clear();
        wordList.addAll(Arrays.asList(text.split("\\s")));
        requestLayout();
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

}

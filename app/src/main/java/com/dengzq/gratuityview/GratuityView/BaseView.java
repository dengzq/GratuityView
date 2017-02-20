package com.dengzq.gratuityview.GratuityView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Company: tsingning
 * Created by dengzq
 * Created time: 2017/2/14
 * Package_name: com.dengzq.dengzqtestapp.widget.RewardView
 * Description : 仿虎扑体育的打赏控件
 */

public class BaseView extends View {
    private static final String TAG = "BaseView";
    private int   TEXT_MARGIN;                     //文本间距
    private Paint mCirclePaint;                    //背景画笔
    private Paint mTextPaint;                      //文本画笔
    public int    textColor   = 0xFFFFFFFF;        //文字颜色
    public int    circleColor = 0xFF43CD80;        //圆形颜色
    public int    textSize    = 16;                //文字大小
    public String fstText     = "";                //第一段文本
    public String secText     = "";                //第二段文本
    private boolean isNewline;                     //是否换行
    private boolean isRotate;                      //是否旋转
    private Context mContext;                      //上下文
    private Rect    fstRect;                       //第一段文本矩形
    private Rect    secRect;                       //第二段文本矩形

    public BaseView(Context context) {
        super(context);
        init(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mCirclePaint = new Paint();
        mCirclePaint.setColor(circleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);

        fstRect = new Rect();
        secRect = new Rect();
        //设置文本间距
        TEXT_MARGIN = (int) (mTextPaint.getTextSize() / 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, Math.min(getMeasuredHeight(), getMeasuredWidth()) / 2, mCirclePaint);

        if (isRotate) {
            canvas.save();
            canvas.rotate(90, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
            drawText(canvas);
            canvas.restore();
        } else drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (isNewline) drawMulptiText(canvas);
        else drawOnelineText(canvas);
    }

    private void drawOnelineText(Canvas canvas) {
        mTextPaint.getTextBounds(fstText, 0, fstText.length(), fstRect);
        canvas.drawText(fstText, getMeasuredWidth() / 2 - fstRect.width() / 2, getMeasuredHeight() / 2 + fstRect.height() / 2, mTextPaint);
    }

    private void drawMulptiText(Canvas canvas) {
        mTextPaint.getTextBounds(fstText, 0, fstText.length(), fstRect);
        mTextPaint.getTextBounds(secText, 0, secText.length(), secRect);
        canvas.drawText(fstText, getMeasuredWidth() / 2 - fstRect.width() / 2, getMeasuredHeight() / 2 - TEXT_MARGIN, mTextPaint);
        canvas.drawText(secText, getMeasuredWidth() / 2 - secRect.width() / 2, getMeasuredHeight() / 2 + TEXT_MARGIN + secRect.height(), mTextPaint);
    }


    //----------------- 提供给外部的方法 -----------------------//

    /**
     * 设置文本颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        this.textColor = color;
        mTextPaint.setColor(textColor);
        invalidate();
    }

    /**
     * 设置背景颜色
     *
     * @param color
     */
    public void setCircleColor(int color) {
        this.circleColor = color;
        mCirclePaint.setColor(circleColor);
        invalidate();
    }

    /**
     * 设置文本大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
        mTextPaint.setTextSize(UIUtils.sp2px(mContext, textSize));
        invalidate();
    }


    /**
     * 设置单行文本
     *
     * @param text
     */
    public void setText(String text) {
        this.fstText = text;
        invalidate();
    }

    /**
     * 设置双行文本
     *
     * @param fstText
     * @param secText
     */
    public void setText(String fstText, String secText) {
        this.fstText = fstText;
        this.secText = secText;
        isNewline = true;
        invalidate();
    }

    /**
     * 绘制子View时候进行旋转
     *
     * @param isRotate
     */
    public void isRotate(boolean isRotate) {
        this.isRotate = isRotate;
    }

}

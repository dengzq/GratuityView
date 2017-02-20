package com.dengzq.gratuityview.GratuityView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.dengzq.gratuityview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Company: tsingning
 * Created by dengzq
 * Created time: 2017/2/14
 * Package_name: com.dengzq.dengzqtestapp.widget.RewardView
 * Description : 虎扑打赏控件
 */

public class GratuityView extends RelativeLayout {
    private static final String               TAG                  = "RewardView";
    private static       int                  AT_MOST              = 100;                  //最大值模式
    private static       int                  EXACTLY              = 101;                  //适合模式
    private              SparseArray<Integer> mTextSizeList        = new SparseArray<>();  //childView文本大小
    private              SparseArray<Integer> mColorBackgroundlist = new SparseArray<>();  //childView颜色背景
    private              SparseArray<Integer> mColorList           = new SparseArray<>();  //childView颜色集合
    private              SparseArray<String>  mStringList          = new SparseArray<>();  //childView文本集合
    private              List<BaseView>       mRewardViewList      = new ArrayList<>();    //childView集合
    private              ValueAnimator        expandAnimation      = new ValueAnimator();  //展开动画
    private              ValueAnimator        collapseAnimation    = new ValueAnimator();  //收缩动画
    private OnItemClickListener mOnItemClickListener;                             //childView点击事件
    private int                 mWidthMode;                                       //宽度模式
    private Paint               backgroundPaint;                                  //背景画笔
    private Paint               backAlphaPaint;                                   //混合模式画笔
    private boolean             added;                                            //是否添加过baseView
    private boolean             expand;                                           //是否展开
    private Context             mContext;                                         //上下文
    private RectF               fstRectF;                                         //背景矩形
    private int                 mCurrentValue;                                    //动画当前值
    private int                 radius;                                           //基圆半径
    private int                 AnimLength;                                       //动画总长度
    private boolean             animated;                                         //是否开启动画
    private boolean             clickEnable;                                      //childView是否可点击
    private int                 size;                                             //基圆大小
    private int                 mWidth;                                           //实际的View的需要宽度
    private Paint  mPaint  = new Paint();                                         //图层绘制画笔
    private String fstText = "";                                                  //第一段文本
    private String secText = "";                                                  //第二段文本
    private boolean isNewLine;                                                    //是否换行
    private Handler mHandler;                                                     //监听自动收缩
    //自定义属性
    private int     mBaseTextSize;                                                //基圆文本大小
    private int     mBaseTextColor;                                               //基圆文本颜色
    private int     mBasegroundColor;                                             //基圆背景颜色
    private int     mChildTextSize;                                               //子文本大小
    private int     mChildTextColor;                                              //子文本颜色
    private int     mCHildgroundColor;                                            //子背景颜色
    private int     mAnimaBackground;                                             //动画背景色
    private int     mAnimDuration;                                                //动画时长
    private int     mContainedCount;                                              //能够容纳的子View数目
    private int     mCollapseDelay;                                               //自动收缩延时
    private boolean mAutoCollapse;                                                //是否自动收缩


    public GratuityView(Context context) {
        this(context, null);
    }

    public GratuityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GratuityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GratuityView);
        mBaseTextSize = typedArray.getInt(R.styleable.GratuityView_baseTextSize, 14);
        mBaseTextColor = typedArray.getColor(R.styleable.GratuityView_baseTextColor, 0xFFFFFFFF);
        mBasegroundColor = typedArray.getColor(R.styleable.GratuityView_basegroundColor, 0xFF43CD80);
        mChildTextSize = typedArray.getInt(R.styleable.GratuityView_childTextSize, 10);
        mChildTextColor = typedArray.getColor(R.styleable.GratuityView_childTextColor, 0xFFFFFFFF);
        mCHildgroundColor = typedArray.getColor(R.styleable.GratuityView_childgroundColor, 0xFF43CD80);
        mAnimDuration = typedArray.getInt(R.styleable.GratuityView_animationDuration, 400);
        mAnimaBackground = typedArray.getColor(R.styleable.GratuityView_animationBackground, 0xFFD4D4D4);
        mContainedCount = typedArray.getInt(R.styleable.GratuityView_childCount, 3);
        mCollapseDelay = typedArray.getInt(R.styleable.GratuityView_collapseDelay, 2000);
        mAutoCollapse = typedArray.getBoolean(R.styleable.GratuityView_autoCollapse, true);
        typedArray.recycle();

        mContext = context;
        backgroundPaint = new Paint();
        backgroundPaint.setColor(mAnimaBackground);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        backAlphaPaint = new Paint();
        backAlphaPaint.setAntiAlias(true);
        backAlphaPaint.setColor(0x000000);
        backAlphaPaint.setStyle(Paint.Style.FILL);
        backAlphaPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        fstRectF = new RectF();

        setWillNotDraw(false);
        //禁止硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, backgroundPaint);
        //实例化handler
        mHandler = new Handler();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        addBaseView(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        int       left   = 0;
        final int right  = getMeasuredWidth();
        final int top    = 0;
        final int bottom = getMeasuredHeight();

        if (!animated) return;
        if (expand) {
            //绘制弧形
            if (mWidthMode == AT_MOST) {
                int diff = getMeasuredWidth() - (mContainedCount + 1) * size;
                //当前宽度刚好或者不够，此时所取宽度均是 getMeasureWidth;
                fstRectF.left = diff + AnimLength - mCurrentValue;
                fstRectF.right = diff + AnimLength - mCurrentValue + radius * 2;
                fstRectF.top = top;
                fstRectF.bottom = bottom;
                canvas.drawArc(fstRectF, 90, 180, true, backgroundPaint);
                //绘制矩形
                canvas.drawRect(diff + AnimLength - mCurrentValue + radius, top, right - radius, bottom, backgroundPaint);
            } else {
                //宽度过长
                int diff    = getMeasuredWidth() - mWidth;
                int layerId = canvas.saveLayer(diff, top, right, bottom, mPaint, Canvas.ALL_SAVE_FLAG);
                //绘制弧形
                fstRectF.left = diff + AnimLength - mCurrentValue;
                fstRectF.right = diff + AnimLength - mCurrentValue + radius * 2;
                fstRectF.top = top;
                fstRectF.bottom = bottom;
                canvas.drawArc(fstRectF, 90, 180, true, backgroundPaint);
                //绘制矩形
                canvas.drawRect(diff + mWidth - radius - mCurrentValue, top, right - radius, bottom, backgroundPaint);
                //绘制xformode源图像
                fstRectF.left = right - size;
                fstRectF.right = right;
                fstRectF.top = top;
                fstRectF.bottom = bottom;
                canvas.drawArc(fstRectF, 90, 180, true, backAlphaPaint);
                canvas.restoreToCount(layerId);
            }
        } else {
            if (mWidthMode == AT_MOST) {
                int diff = getMeasuredWidth() - (mContainedCount + 1) * size;
                fstRectF.left = diff + mCurrentValue;
                fstRectF.right = diff + mCurrentValue + 2 * radius;
                fstRectF.top = top;
                fstRectF.bottom = bottom;
                canvas.drawArc(fstRectF, 90, 180, true, backgroundPaint);

                canvas.drawRect(diff + radius + mCurrentValue, top, right - radius, bottom, backgroundPaint);
            } else {
                //宽度过长
                int diff    = getMeasuredWidth() - mWidth;
                int layerId = canvas.saveLayer(diff, top, right, bottom, mPaint, Canvas.ALL_SAVE_FLAG);
                //绘制弧形
                fstRectF.left = diff + mCurrentValue;
                fstRectF.right = diff + mCurrentValue + 2 * radius;
                fstRectF.top = top;
                fstRectF.bottom = bottom;
                canvas.drawArc(fstRectF, 90, 180, true, backgroundPaint);
                //绘制矩形
                canvas.drawRect(diff + radius + mCurrentValue, top, right - radius, bottom, backgroundPaint);
                //绘制xformode源图像
                fstRectF.left = right - size;
                fstRectF.right = right;
                fstRectF.top = top;
                fstRectF.bottom = bottom;
                canvas.drawArc(fstRectF, 90, 180, true, backAlphaPaint);
                canvas.restoreToCount(layerId);
            }
        }
    }

    private void addBaseView(int widthSize, int heightSize) {
        if (added || widthSize < 1 || heightSize < 1) return;
        added = !added;
        size = Math.min(getMeasuredHeight(), getMeasuredWidth());
        radius = size / 2;
        if (getMeasuredWidth() < (mContainedCount + 1) * size)
            mWidthMode = AT_MOST;
        else
            mWidthMode = EXACTLY;
        mWidth = Math.min(getMeasuredWidth(), (mContainedCount + 1) * size);

        BaseView                    baseView = new BaseView(mContext);
        LayoutParams params   = new LayoutParams(size, size);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        baseView.setLayoutParams(params);
        if (isNewLine)
            baseView.setText(fstText, secText);
        else baseView.setText(fstText);
        baseView.setTextSize(mBaseTextSize);
        baseView.setTextColor(mBaseTextColor);
        baseView.setCircleColor(mBasegroundColor);
        addView(baseView);
        baseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expand)
                    startExpandAnimation();
                else
                    startCollapseAnimation();
            }
        });

    }

    private void addRewardView() {
        for (int i = 0; i < mContainedCount; i++) {
            int                         rewardSize = (int) (2 * 1.0f / 3 * Math.min(getMeasuredHeight(), getMeasuredWidth()));
            BaseView                    rewardView = new BaseView(mContext);
            LayoutParams params     = new LayoutParams(rewardSize, rewardSize);
            params.rightMargin = radius - rewardSize / 2;
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            rewardView.setLayoutParams(params);
            if (mTextSizeList.get(i) != null)
                rewardView.setTextSize(mTextSizeList.get(i));
            else rewardView.setTextSize(mChildTextSize);
            if (mColorList.get(i) != null)
                rewardView.setTextColor(mColorList.get(i));
            else rewardView.setTextColor(mChildTextColor);
            if (mColorBackgroundlist.get(i) != null)
                rewardView.setCircleColor(mColorBackgroundlist.get(i));
            else rewardView.setCircleColor(mCHildgroundColor);
            if (!TextUtils.isEmpty(mStringList.get(i)))
                rewardView.setText(mStringList.get(i));
            else rewardView.setText("");
            rewardView.isRotate(true);
            addView(rewardView);
            mRewardViewList.add(rewardView);

            final int index = i;
            rewardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null && clickEnable)
                        mOnItemClickListener.onItemClick(index);
                }
            });
        }
    }

    private void startExpandAnimation() {
        if (collapseAnimation != null && collapseAnimation.isRunning()) {
            return;
        }
        expand = !expand;
        animated = true;
        clickEnable = false;
        if (mWidthMode == AT_MOST)
            AnimLength = (mContainedCount + 1) * size - radius * 2;
        else
            AnimLength = mWidth - radius * 2;      //总运动长度
        expandAnimation = ValueAnimator.ofInt(AnimLength);
        expandAnimation.setDuration(mAnimDuration);
        expandAnimation.start();
        expandAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        expandAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentValue = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        if (mRewardViewList.size() < 1)
            addRewardView();
        startChildExpandAnimation();

        //默认2s后自动收缩
        if (mAutoCollapse)
            mHandler.postDelayed(checkExpandRunnable, mCollapseDelay + mAnimDuration);
    }

    private void startCollapseAnimation() {
        if (expandAnimation != null && expandAnimation.isRunning()) {
            return;
        }
        expand = !expand;
        clickEnable = false;
        animated = true;
        collapseAnimation = ValueAnimator.ofInt(AnimLength);
        collapseAnimation.setDuration(mAnimDuration);
        collapseAnimation.start();
        collapseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        collapseAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentValue = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        startChildCollapseAnimation();

        //移除收缩监听
        if (mAutoCollapse)
            mHandler.removeCallbacks(checkExpandRunnable);
    }

    private void startChildExpandAnimation() {
        for (int i = 0; i < mContainedCount; i++) {
            final BaseView view = mRewardViewList.get(i);
            view.setVisibility(VISIBLE);
            final int      desX           = size * (mContainedCount - i);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, -desX);
            objectAnimator.setDuration(mAnimDuration);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    value = Math.abs(value);
                    //淡出
                    if (value <= radius * 1.0f * 33 / 20) {
                        float alpha = value * 1.0f / radius - 0.65f;
                        view.setAlpha(alpha);
                    } else view.setAlpha(1.0f);
                    //旋转
                    float ratation = -value * 1.0f / desX * 90;
                    view.setRotation(ratation);
                }
            });
            if (i == mContainedCount - 1)
                objectAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        clickEnable = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
        }
    }

    private void startChildCollapseAnimation() {
        for (int i = 0; i < mContainedCount; i++) {
            final BaseView view           = mRewardViewList.get(i);
            final int      desX           = size * (mContainedCount - i);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", -desX, 0);
            objectAnimator.setDuration(mAnimDuration);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.start();
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    value = Math.abs(value);
                    if (value <= radius * 1.0f * 33 / 20) {
                        float alpha = value * 1.0f / radius - 0.65f;
                        view.setAlpha(alpha);
                    }
                    float ratation = -value * 1.0f / desX * 90;
                    view.setRotation(ratation);
                }
            });
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    clickEnable = false;
                    view.setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }

    //监听自动收缩的runnable
    Runnable checkExpandRunnable = new Runnable() {

        @Override
        public void run() {
            if (expand && mAutoCollapse) {
                startCollapseAnimation();
            }
        }
    };


    //---------------------    提供给外部的方法 ------------------//

    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    /**
     * 设置点击监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 该方法暂时废弃
     * 获取child view对象
     *
     * @param index
     * @return
     */
    private BaseView getChildView(int index) {
        BaseView baseView = null;
        if (index < mRewardViewList.size()) {
            baseView = mRewardViewList.get(index);
        }
        return baseView;
    }

    /**
     * 设置单行文本
     *
     * @param fstText
     */
    public void setBaseText(String fstText) {
        this.fstText = fstText;
    }

    /**
     * 设置双行文本
     *
     * @param fstText
     * @param secText
     */
    public void setBaseText(String fstText, String secText) {
        this.fstText = fstText;
        this.secText = secText;
        isNewLine = true;
    }

    /**
     * 设置子View文本
     *
     * @param stringList
     */
    public void setChildText(List<String> stringList) {
        for (int i = 0; i < stringList.size(); i++) {
            mStringList.put(i, stringList.get(i));
        }
    }


    /**
     * 设置子View文本
     *
     * @param text
     * @param index
     */
    public void setChildText(String text, int index) {
        if (index < mRewardViewList.size()) {
            BaseView baseView = mRewardViewList.get(index);
            baseView.fstText = text;
            baseView.setText(text);
        }
        mStringList.put(index, text);
    }

    //该方法暂时废弃
    private void setChildText(String fstText, String secText, int index) {
        if (index < mRewardViewList.size()) {
            BaseView baseView = mRewardViewList.get(index);
            baseView.fstText = fstText;
            baseView.secText = secText;
            baseView.setText(fstText, secText);
        }
    }

    /**
     * 设置子view颜色
     *
     * @param color
     * @param index
     */
    public void setChildTextColor(int color, int index) {
        if (index < mRewardViewList.size()) {
            BaseView baseView = mRewardViewList.get(index);
            baseView.textColor = color;
            baseView.setTextColor(color);
        }
        mColorList.put(index, color);
    }

    /**
     * 设置子view背景颜色
     *
     * @param color
     * @param index
     */
    public void setChildgroundColor(int color, int index) {
        if (index < mRewardViewList.size()) {
            BaseView baseView = mRewardViewList.get(index);
            baseView.circleColor = color;
            baseView.setCircleColor(color);
        }
        mColorBackgroundlist.put(index, color);
    }

    /**
     * 设置文本大小
     *
     * @param textSize
     * @param index
     */
    public void setChildTextSize(int textSize, int index) {
        if (index < mRewardViewList.size()) {
            BaseView baseView = mRewardViewList.get(index);
            baseView.textSize = textSize;
            baseView.setTextSize(textSize);
        }
        mTextSizeList.put(index, textSize);
    }

    public void setBaseTextColor(int color) {
        this.mBaseTextColor = color;
    }

    public void setBaseTextSize(int size) {
        this.mBaseTextSize = size;
    }

    public void setBasegroundColor(int color) {
        this.mBasegroundColor = color;
    }

    public void setChildTextColor(int color) {
        this.mChildTextColor = color;
    }

    public void setChildTextSize(int size) {
        this.mChildTextSize = size;
    }

    public void setCHildgroundColor(int color) {
        this.mCHildgroundColor = color;
    }

    public void setAnimaBackground(int color) {
        this.mAnimaBackground = color;
    }

    public void setAnimDuration(int duration) {
        this.mAnimDuration = duration;
    }

    public void setChildCount(int childCount) {
        this.mContainedCount = childCount;
    }

    public void setCollapseDelay(int delay) {
        this.mCollapseDelay = delay;
    }

    public void setAutoCollapse(boolean autoCollapse) {
        this.mAutoCollapse = autoCollapse;
    }
}



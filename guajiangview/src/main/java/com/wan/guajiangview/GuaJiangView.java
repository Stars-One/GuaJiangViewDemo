package com.wan.guajiangview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by xen on 2018/8/20 0020.
 */

public class GuaJiangView extends View {
    /**
     * 文字内容
     */
    private String text;
    /**
     * 文字颜色，文字大小
     */
    private int textColor,textSize;
    /**
     * 信息层图片
     */
    private Bitmap background;
    /**
     * 遮盖层图片
     */
    private Bitmap cover;
    /**
     * 选择信息层是显示文字还是图片,默认显示文字
     */
    private boolean isDrawText;
    /**
     * 擦除效果的宽度
     */
    private int paintSize;
    /**
     * 达到多少阈值清除遮盖层,默认百分之60
     */
    private int clearFlag;
    /**
     * 文字背景矩形大小
     */
    private Rect mBackground;
    /**
     * 文字画笔
     */
    private Paint messagePaint = new Paint();
    /**
     * 用来接收drawable
     */
    private Drawable backgroundDrawable,coverDrawable;
    /**
     * 绘制线条的Paint,即用户手指绘制Path
     */
    private Paint mOutterPaint = new Paint();
    /**
     * 记录用户绘制的Path
     */
    private Path mPath = new Path();
    /**
     * 内存中创建的Canvas
     */
    private Canvas mCanvas;
    /**
     * mCanvas绘制内容在其上
     */
    private Bitmap mBitmap;
    /**
     * x坐标
     */
    private int mLastX;
    /**
     * y坐标
     */
    private int mLastY;
    /**
     * 是否清除
     */
    private volatile boolean isClear = false;
    public GuaJiangView(Context context) {
        super(context);
    }

    public GuaJiangView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.GuaJiangView);
        text = ta.getString(R.styleable.GuaJiangView_text);
        textColor = ta.getColor(R.styleable.GuaJiangView_textColor,0);
        textSize = ta.getInteger(R.styleable.GuaJiangView_textSize,16);
        coverDrawable = ta.getDrawable(R.styleable.GuaJiangView_cover);
        isDrawText = ta.getBoolean(R.styleable.GuaJiangView_isDrawText,true);
        clearFlag = ta.getInteger(R.styleable.GuaJiangView_clearFlag,60);
        if (!isDrawText){
            backgroundDrawable = ta.getDrawable(R.styleable.GuaJiangView_messageBackground);
        }
        paintSize = ta.getInteger(R.styleable.GuaJiangView_PaintSize,10);
        ta.recycle();


    }


    /**
     * drawable转换为bitmap
     * @param drawable 需要转换的drawble
     * @param width 宽
     * @param height 高
     * @return 返回bitmap
     */
    public Bitmap drawableToBitmap(Drawable drawable, int width, int height) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public GuaJiangView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width =getMeasuredWidth();
        int height = getMeasuredHeight();

        // 初始化bitmap
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//以获得的宽高创建一个32位的bitmap
        mCanvas = new Canvas(mBitmap);//以bitmap创建了一个画布

        if (isDrawText){
            mBackground = new Rect();

            messagePaint.setAntiAlias(true);
            messagePaint.setStyle(Paint.Style.STROKE);
            messagePaint.getTextBounds(text,0,text.length(),mBackground);
            messagePaint.setTextSize(textSize);
            messagePaint.setColor(textColor);//设置文字颜色
            Log.d(TAG, "onMeasure: if");
        }else{
            background =drawableToBitmap(backgroundDrawable,width,height);//转换为bitmap
            background = Bitmap.createScaledBitmap(background,width,height,true);//对bitmap进行缩放
        }
        cover = drawableToBitmap(coverDrawable,width,height);//转换为bitmap
        cover = Bitmap.createScaledBitmap(cover,width,height,true);//对bitmap进行缩放
        // 设置画笔
        mOutterPaint.setAntiAlias(true);//使用抗锯齿功能，会消耗较大资源，绘制图形速度会变慢
        mOutterPaint.setDither(true);//图像抖动处理,会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mOutterPaint.setStrokeJoin(Paint.Join.ROUND);//圆角，平滑
        mOutterPaint.setStrokeCap(Paint.Cap.ROUND); //圆角
        mOutterPaint.setStrokeWidth(paintSize); // 设置画笔宽度

        mCanvas.drawBitmap(cover,0,0,null);//将遮盖层画到内存中的画布上

    }

    private void drawPath() {
        Log.d(TAG, "drawPath: ");
        mOutterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mCanvas.drawPath(mPath, mOutterPaint);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isDrawText){
            canvas.drawText(text,mBitmap.getWidth()/2-mBackground.width()/2,getMeasuredHeight()/2+mBackground.height()/2,messagePaint);
        }else{
            canvas.drawBitmap(background,0,0,null);
        }
        if (!isClear){
            drawPath();
            canvas.drawBitmap(mBitmap,0,0,null);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //当手指按到屏幕上的时候，Path路径之中就使用moveto方法，移动到手指当前位置，invalidate刷新View,回调onDraw方法,（还没有画出来）
        //之后，手指移动，action是处于ACTION_MOVE的状态，Path路径使用lineto方法（画直线），
        // 同时，将x，y坐标进行了更新，invalidate刷新View,回调onDraw方法，canvas通过drawpath使用画笔将path画了出来，之后如果用户没有抬起手指，则继续循环ACTION_MOVE中的步骤

        int action = event.getAction();
        int x = (int) event.getX();//获得x坐标
        int y = (int) event.getY();//获得y坐标
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX, mLastY);//之后回调onDraw方法canvas将path
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);//之后回调onDraw方法时canvas画直线到（x，y）该点
                mLastX = x;//更新x坐标
                mLastY = y;//更新y坐标
                break;
            case MotionEvent.ACTION_UP:
                new Thread(mRunnable).start();
            default:break;
        }
        invalidate();//刷新View，回调onDraw方法
        Log.d(TAG, "onTouchEvent: invalidate");
        return true;

    }

    /**
     * 实现擦除遮盖层百分之60则清除遮盖层
     */
    private Runnable mRunnable = new Runnable() {
        int[] pixels;

        @Override
        public void run() {

            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();

            float wipeArea = 0;//擦除像素点计数，初始为0
            float totalArea = w * h;//全部的像素点

            pixels = new int[w * h];
            /**
             * pixels   接收位图颜色值的数组
             * offset   写入到pixels[]中的第一个像素索引值
             * stride   pixels[]中的行间距个数值(必须大于等于位图宽度)。可以为负数
             * x     　从位图中读取的第一个像素的x坐标值。
             * y      从位图中读取的第一个像素的y坐标值
             * width  　　从每一行中读取的像素宽度
             * height 　　　读取的行数
             */
            Bitmap b = mBitmap;
            b.getPixels(pixels, 0, w, 0, 0, w, h);

            //for循环查找用户擦除的像素点，为0则是擦除，wipeArea+1
            for (int i = 0; i < totalArea; i++) {
                if (pixels[i] == 0) {
                    wipeArea++;
                }
            }

            if (wipeArea > 0 && totalArea > 0) {
                int percent = (int) (wipeArea * 100 / totalArea);//计算比例
                if (percent > clearFlag) {
                    isClear = true;//isClear是之前声明的全局变量，
                    postInvalidate();//子进程中调用此方法重绘View
                }
            }

        }
    };

    /**
     * 设置文字内容
     * @param text 文字内容
     */
    public void setText(String text){
        this.text = text;
    }

    /**
     * 设置文字颜色
     * @param textColor 文字颜色
     */
    public void setTextColor(int textColor){
        this.textColor = textColor;
    }

    /**
     * 设置文字大小
     * @param textSize 文字大小
     */
    public void setTextSize(int textSize){
        this.textSize = textSize;
    }

    /**
     * 设置擦除的宽度
     * @param paintSize 擦除宽度
     */
    public void  setPaintSize(int paintSize){
        this.paintSize = paintSize;
    }

    /**
     * 设置清除效果的阈值
     * @param clearFlag
     */
    public void setClearFlag(int clearFlag){
        this.clearFlag = clearFlag;
    }
    public interface onGuaCompleteListener{
        void complete();
    }
    private onGuaCompleteListener mlistener;

    public void setGuaCompleteListener(onGuaCompleteListener mlistener) {
        this.mlistener = mlistener;
    }

}

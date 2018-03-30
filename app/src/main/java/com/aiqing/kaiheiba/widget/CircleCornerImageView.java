package com.aiqing.kaiheiba.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.aiqing.kaiheiba.utils.DensityUtil;


/**
 * Created by huxq17 on 2015/12/30.
 */
@SuppressLint("AppCompatCustomView")
public class CircleCornerImageView extends ImageView {
    private Paint paint;
    private int roundWidth = 10;
    private int roundHeight = 10;
    private Paint belowPaint;
    /**
     * 要显示圆角的角 依次是左，上，右，下
     * 0 | 1
     * -----
     * 2 | 3
     * 默认是上下左右都圆角显示
     */
    private int[] corners = {0, 1, 2, 3};

    public CircleCornerImageView(Context context) {
        super(context);
        init(context, null);
    }
    public void setRound(int round){
        roundWidth=roundHeight=round;
    }

    private void init(Context context, AttributeSet attrs) {
        roundWidth = DensityUtil.dip2px(getContext(), roundWidth);
        roundHeight = DensityUtil.dip2px(getContext(), roundHeight);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        belowPaint = new Paint();
        belowPaint.setAntiAlias(true);
        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), belowPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        //依次给需要圆角的角画圆角
        for (int i = 0; i < corners.length; i++) {
            switch (i) {
                case 0:
                    drawLeftUp(canvas);
                    break;
                case 1:
                    drawRightUp(canvas);
                    break;
                case 2:
                    drawLiftDown(canvas);
                    break;
                case 3:
                    drawRightDown(canvas);
                    break;
            }
        }
        canvas.restore();
    }

    /**
     * 设置要显示圆角的角 依次是左，上，右，下
     * 0 | 1
     * -----
     * 2 | 3
     */
    public void showCorner(int[] corners) {
        this.corners = corners;
    }

    private void drawLeftUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, roundHeight);
        path.lineTo(0, 0);
        path.lineTo(roundWidth, 0);
        path.arcTo(new RectF(0, 0, roundWidth * 2, roundHeight * 2),
                -90,
                -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLiftDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - roundHeight);
        path.lineTo(0, getHeight());
        path.lineTo(roundWidth, getHeight());
        path.arcTo(new RectF(0,getHeight() - roundHeight * 2,0 + roundWidth * 2,getHeight()),90,90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightDown(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - roundWidth, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - roundHeight);
        path.arcTo(new RectF(
                getWidth() - roundWidth * 2,
                getHeight() - roundHeight * 2,
                getWidth(),
                getHeight()), 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightUp(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - roundWidth, 0);
        path.arcTo(new RectF(getWidth() - roundWidth * 2,0,getWidth(),0 + roundHeight * 2),-90,90);
        path.close();
        canvas.drawPath(path, paint);
    }

}

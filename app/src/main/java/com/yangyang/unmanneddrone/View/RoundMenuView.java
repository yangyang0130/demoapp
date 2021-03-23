package com.yangyang.unmanneddrone.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.yangyang.unmanneddrone.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoundMenuView extends View {

    /**
     * 变量
     */
    private int coreX;//中心点的坐标X
    private int coreY;//中心点的坐标Y

    private List<RoundMenu> roundMenus;//菜单列表

    private boolean isCoreMenu = false;//是否有中心按钮
    private int coreMenuColor;//中心按钮的默认背景--最好不要透明色
    private int coreMenuStrokeColor;//中心按钮描边颜色
    private int coreMenuStrokeSize;//中心按钮描边粗细
    private int coreMenuSelectColor;//中心按钮选中时的背景颜色
    private Bitmap coreBitmap;//OK图片
    private OnClickListener onCoreClickListener;//中心按钮的点击回调

    private float deviationDegree;//偏移角度
    private int onClickState = -2;//-2是无点击，-1是点击中心圆，其他是点击菜单
    private int roundRadius;//中心圆的半径
    private double radiusDistance;//半径的长度比（中心圆半径=大圆半径*radiusDistance）
    private long touchTime;//按下时间，抬起的时候判定一下，超过300毫秒算点击
    private boolean mEnableClick = true;
    private boolean isPermission = false;
    private int rotate_flag = -1;//正在旋转的方向 -1代表没有旋转  //1左, 2上, 3右, 4下
    private Toast toast;
    private String clickEvent;

    public void setClickEvent(String clickEvent){
        this.clickEvent = clickEvent;
    }

    public String getClickEvent(){
        return clickEvent;
    }

    public void setPermission(boolean permission) {
        isPermission = permission;
    }

    public boolean ismEnableClick() {
        return mEnableClick;
    }

    public void setmEnableClick(boolean mEnableClick) {
        this.mEnableClick = mEnableClick;
    }

    public void setRotateFlag(int flag) {
        rotate_flag = flag;
        invalidate();
    }

    public int getRotateFlag() {
        return rotate_flag;
    }

    public RoundMenuView(Context context) {
        super(context);
        initView();
    }

    public RoundMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RoundMenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        coreX = getWidth() / 2;
        coreY = getHeight() / 2;
        roundRadius = (int) (getWidth() / 2 * radiusDistance);//计算中心圆圈半径

        int padding = dp2px(10f);
        int paddoutside = dp2px(12f);

        RectF rect = new RectF(padding, padding, getWidth() - padding, getHeight() - padding);

        if (roundMenus != null && roundMenus.size() > 0) {
            float sweepAngle = 360 / roundMenus.size();//每个弧形的角度
            deviationDegree = sweepAngle / 2;//其实的偏移角度，如果4个扇形的时候是X形状，而非+,设为0试试就知道什么意思了
            for (int i = 0; i < roundMenus.size(); i++) {
                RoundMenu roundMenu = roundMenus.get(i);
                //填充
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                if (onClickState == i) {
                    //选中
                    paint.setColor(roundMenu.selectSolidColor);
                } else {
                    //未选中
                    paint.setColor(roundMenu.solidColor);
                }
//               画点击效果
                canvas.drawArc(rect, deviationDegree + (i * sweepAngle), sweepAngle, true, paint);
                //画扇形描边
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStrokeWidth(roundMenu.strokeSize);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(roundMenu.strokeColor);
                canvas.drawArc(rect, deviationDegree + (i * sweepAngle), sweepAngle, roundMenu.useCenter, paint);
                paint = new Paint();
                paint.setStrokeWidth(dp2px(7f));
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.parseColor("#3B3C41"));
                //画大圆
                canvas.drawArc(rect, 0, 360, false, paint);
                //画扇形图案
                Matrix matrix = new Matrix();
                matrix.postTranslate((float) ((coreX + getWidth() / 2 * roundMenu.iconDistance) - (roundMenu.icon.getWidth() / 2)), coreY - (roundMenu.icon.getHeight() / 2));
                if (rotate_flag == i) {
                    matrix.postTranslate(-dp2px(10), -dp2px(10));
                    matrix.postRotate(((i + 1) * sweepAngle), coreX, coreY);
                    canvas.drawBitmap(roundMenu.stop_icon, matrix, null);
                } else {
                    matrix.postRotate(((i + 1) * sweepAngle), coreX, coreY);
                    canvas.drawBitmap(roundMenu.icon, matrix, null);
                }
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(dp2px(2f));
                canvas.drawArc(rect, 0, 360, false, paint);
            }
        }
        //画中心圆圈
        if (isCoreMenu) {
            //填充
            RectF rect1 = new RectF(coreX - roundRadius, coreY - roundRadius, coreX + roundRadius, coreY + roundRadius);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            if (onClickState == -1) {
                paint.setColor(coreMenuSelectColor);
            } else {
                paint.setColor(coreMenuColor);
            }
            canvas.drawArc(rect1, 0, 360, true, paint);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(dp2px(7));
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawArc(rect1, 0, 360, true, paint);
            //画描边
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(coreMenuStrokeSize);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(coreMenuStrokeColor);
            canvas.drawArc(rect1, 0, 360, true, paint);
            if (coreBitmap != null) {
                //画中心圆圈的“OK”图标
                canvas.drawBitmap(coreBitmap, coreX - coreBitmap.getWidth() / 2, coreY - coreBitmap.getHeight() / 2, null);//在 0，0坐标开始画入src
            }
        }
    }
    public boolean onTouchEvent(MotionEvent event) {
        OnClickListener onClickListener = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchTime = new Date().getTime();
                float textX = event.getX();
                float textY = event.getY();
                int distanceLine = (int) getDisForTwoSpot(coreX, coreY, textX, textY);//距离中心点之间的直线距离
                if (distanceLine <= roundRadius) {
                    //点击的是中心圆；按下点到中心点的距离小于中心园半径，那就是点击中心园了
                    onClickState = -1;
                    onClickListener = onCoreClickListener;
                } else if (distanceLine <= getWidth() / 2) {
                    //点击的是某个扇形；按下点到中心点的距离大于中心圆半径小于大圆半径，那就是点击某个扇形了
                    float sweepAngle = 360 / roundMenus.size();//每个弧形的角度
                    int angle = getRotationBetweenLines(coreX, coreY, textX, textY);
                    //这个angle的角度是从正Y轴开始，而我们的扇形是从正X轴开始，再加上偏移角度，所以需要计算一下
                    angle = (angle + 360 - 90 - (int) deviationDegree) % 360;
                    onClickState = (int) (angle / sweepAngle);//根据角度得出点击的是那个扇形
                    onClickListener = roundMenus.get(onClickState).onClickListener;
                } else {
                    //点击了外面
                    onClickState = -2;
                }
                if (onClickListener != null){
                    clickEvent = "down";
                    onClickListener.onClick(this);
                }
                if (rotate_flag == -1 || (rotate_flag != -1 && rotate_flag == onClickState)) {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (onClickState == -1) {
                    onClickListener = onCoreClickListener;
                } else if (onClickState >= 0 && onClickState < roundMenus.size()) {
                    onClickListener = roundMenus.get(onClickState).onClickListener;
                }
                if (onClickListener != null) {
                    clickEvent = "up";
                    onClickListener.onClick(this);
                }
                onClickState = -2;
                invalidate();
                break;
        }
        return true;
    }
    /**
     * 添加菜单
     *
     * @param roundMenu
     */
    public void addRoundMenu(RoundMenu roundMenu) {
        if (roundMenu == null) {
            return;
        }
        if (roundMenus == null) {
            roundMenus = new ArrayList<RoundMenu>();
        }
        roundMenus.add(roundMenu);
        invalidate();
    }

    /**
     * 添加中心菜单按钮
     *
     * @param coreMenuColor
     * @param coreMenuSelectColor
     * @param onClickListener
     */
    public void setCoreMenu(int coreMenuColor, int coreMenuSelectColor, int coreMenuStrokeColor,
                            int coreMenuStrokeSize, double radiusDistance, Bitmap bitmap, OnClickListener onClickListener) {
        isCoreMenu = true;
        this.coreMenuColor = coreMenuColor;
        this.coreMenuSelectColor = coreMenuSelectColor;
        this.coreMenuStrokeColor = coreMenuStrokeColor;
        this.coreMenuStrokeSize = coreMenuStrokeSize;
        this.radiusDistance = radiusDistance;
        coreBitmap = bitmap;
        this.onCoreClickListener = onClickListener;
        invalidate();
    }

    /**
     * 获取两条线的夹角
     *
     * @param centerX
     * @param centerY
     * @param xInView
     * @param yInView
     * @return
     */
    public static int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;

        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) //第二象限
        {
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }
        return (int) rotation;
    }

    /**
     * 求两个点之间的距离
     *
     * @return
     */
    public static double getDisForTwoSpot(float x1, float y1, float x2, float y2) {
        float width, height;
        if (x1 > x2) {
            width = x1 - x2;
        } else {
            width = x2 - x1;
        }

        if (y1 > y2) {
            height = y2 - y1;
        } else {
            height = y2 - y1;
        }
        return Math.sqrt((width * width) + (height * height));
    }

    /**
     * 扇形的对象类
     */
    public static class RoundMenu {
        public boolean useCenter = true;//扇形是否画连接中心点的直线
        public int solidColor = Color.WHITE;//背景颜色,默认透明
        public int selectSolidColor = Color.WHITE;//背景颜色,默认透明
        public int strokeColor = 0xFFBFC0C2;//描边颜色,默认透明
        public int strokeSize = 2;//描边的宽度,默认1
        public Bitmap icon;//菜单的图片
        public Bitmap stop_icon;//菜单的图片正在操作
        public OnClickListener onClickListener;//点击监听
        public double iconDistance = 0.6;//图标距离中心点的距离
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    private void initView() {

        RoundMenuView.RoundMenu roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = Color.GRAY;
        roundMenu.icon = drawable2Bitmap(getResources().getDrawable(R.mipmap.right));
        roundMenu.stop_icon = drawable2Bitmap2(getResources().getDrawable(R.drawable.ic_left_map));
        roundMenu.onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        addRoundMenu(roundMenu);

        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = Color.GRAY;
        roundMenu.icon = drawable2Bitmap(getResources().getDrawable(R.mipmap.right));
        roundMenu.stop_icon = drawable2Bitmap2(getResources().getDrawable(R.drawable.ic_left_map));
        roundMenu.onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        addRoundMenu(roundMenu);

        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = Color.GRAY;
        roundMenu.icon = drawable2Bitmap(getResources().getDrawable(R.mipmap.right));
        roundMenu.stop_icon = drawable2Bitmap2(getResources().getDrawable(R.drawable.ic_left_map));
        roundMenu.onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        addRoundMenu(roundMenu);

        roundMenu = new RoundMenuView.RoundMenu();
        roundMenu.selectSolidColor = Color.GRAY;
        roundMenu.icon = drawable2Bitmap(getResources().getDrawable(R.mipmap.right));
        roundMenu.stop_icon = drawable2Bitmap2(getResources().getDrawable(R.drawable.ic_left_map));
        roundMenu.onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        addRoundMenu(roundMenu);

        setCoreMenu(Color.parseColor("#FFFFFFFF"), Color.parseColor("#FFFFFFFF"), Color.parseColor("#FFBFC0C2")
                ,2,0.33, drawable2Bitmap(getResources().getDrawable(R.drawable.shape_dian)), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    Toast.makeText(PreviewActivity.this, "点击了中心圆圈", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Drawable to bitmap.
     *
     * @param drawable The drawable.
     * @return bitmap
     */
    private static Bitmap drawable2Bitmap(final Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    /**
     * Drawable to bitmap.
     *
     * @param drawable The drawable.
     * @return bitmap
     */
    private static Bitmap drawable2Bitmap2(final Drawable drawable) {
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(dp2px(25),
                dp2px(25),
                drawable.getOpacity() != PixelFormat.OPAQUE
                        ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
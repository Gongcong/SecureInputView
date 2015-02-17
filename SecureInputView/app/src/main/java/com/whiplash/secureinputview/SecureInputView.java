package com.whiplash.secureinputview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by gongcong on 15-2-17.
 */
public class SecureInputView extends EditText implements View.OnFocusChangeListener {

    private int textLength;

    private boolean displayPassWord = false;
    private int borderColorNormal;
    private int borderColorFocus;
    private int lineColorNormal;
    private int lineColorFocus;
    private float borderWidth;
    private float borderRadius;

    private int passwordLength;
    private int passwordColor;
    private float passwordWidth;
    private float passwordRadius;

    private Paint passwordPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint borderPaint = new Paint(ANTI_ALIAS_FLAG);

    private final int defaultSplitLineWidth = 1;

    private boolean hasFocus = false;

    public SecureInputView(Context context) {
        super(context);
        init(context, null);
    }

    public SecureInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SecureInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final Resources res = getResources();

        final int defaultBorderColor = res.getColor(R.color.default_ev_border_color_normal);
        final int defaultBorderColorFocus = res.getColor(R.color.default_ev_border_color_focus);
        final int defaultLineColor = res.getColor(R.color.default_divider_line_color_normal);
        final int defaultLineColorFocus = res.getColor(R.color.default_divider_line_color_focus);
        final float defaultBorderWidth = res.getDimension(R.dimen.default_ev_border_width);
        final float defaultBorderRadius = res.getDimension(R.dimen.default_ev_border_radius);

        final int defaultPasswordLength = res.getInteger(R.integer.default_ev_password_length);
        final int defaultPasswordColor = res.getColor(R.color.default_ev_password_color);
        final float defaultPasswordWidth = res.getDimension(R.dimen.default_ev_password_width);
        final float defaultPasswordRadius = res.getDimension(R.dimen.default_ev_password_radius);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SecureInputView, 0, 0);
        try {
            displayPassWord = a.getBoolean(R.styleable.SecureInputView_displayPassWord, false);
            borderColorNormal = a.getColor(R.styleable.SecureInputView_borderColor, defaultBorderColor);
            borderColorFocus = a.getColor(R.styleable.SecureInputView_borderColorFocus, defaultBorderColorFocus);
            lineColorNormal = a.getColor(R.styleable.SecureInputView_borderColor, defaultLineColor);
            lineColorFocus = a.getColor(R.styleable.SecureInputView_borderColorFocus, defaultLineColorFocus);
            borderWidth = a.getDimension(R.styleable.SecureInputView_borderWidth, defaultBorderWidth);
            borderRadius = a.getDimension(R.styleable.SecureInputView_borderRadius, defaultBorderRadius);
            passwordLength = a.getInt(R.styleable.SecureInputView_passwordLength, defaultPasswordLength);
            passwordColor = a.getColor(R.styleable.SecureInputView_passwordColor, defaultPasswordColor);
            passwordWidth = a.getDimension(R.styleable.SecureInputView_passwordWidth, defaultPasswordWidth);
            passwordRadius = a.getDimension(R.styleable.SecureInputView_passwordRadius, defaultPasswordRadius);
        } finally {
            a.recycle();
        }
        borderPaint.setColor(borderColorFocus);
        passwordPaint.setStrokeWidth(passwordWidth);
        passwordPaint.setStyle(Paint.Style.FILL);
        passwordPaint.setColor(passwordColor);
        setOnFocusChangeListener(this);
    }

    private RectF rectOut, rectIn;
    int width, height;

    private void initRect() {
        width = getWidth();
        height = getHeight();
        rectOut = new RectF(0, 0, width, height);
        rectIn = new RectF(rectOut.left + borderWidth, rectOut.top + borderWidth,
                rectOut.right - borderWidth, rectOut.bottom - borderWidth);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        initRect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 外边框
        borderPaint.setColor(hasFocus ? borderColorFocus : borderColorNormal);
        canvas.drawRoundRect(rectOut, borderRadius, borderRadius, borderPaint);
        // 内容区
        borderPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectIn, borderRadius, borderRadius, borderPaint);
        // 分割线
        borderPaint.setColor(hasFocus ? lineColorFocus : lineColorNormal);
        borderPaint.setStrokeWidth(defaultSplitLineWidth);
        for (int i = 1; i < passwordLength; i++) {
            float x = width * i / passwordLength;
            canvas.drawLine(x, 0, x, height, borderPaint);
        }
        // 密码
        float cx, cy = height / 2;
        float half = width / passwordLength / 2;
        for (int i = 0; i < textLength; i++) {
            passwordPaint.setTextSize(50);
            if (!displayPassWord) {
                cx = width * i / passwordLength + half;
                canvas.drawCircle(cx, cy, passwordWidth, passwordPaint);
            } else {
                char singlePassWord = getText().toString().charAt(i);
                //TODO 水平不在最中间，需要重新算起始位置
                cx = width * i / passwordLength + half;
                cy = (canvas.getHeight() / 2) - ((passwordPaint.descent() + passwordPaint.ascent()) / 2);
                canvas.drawText(String.valueOf(singlePassWord), cx, cy, passwordPaint);
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.textLength = text.toString().length();
        invalidate();
    }

    public void cleanInput() {
        getText().clear();
        invalidate();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public void isDisplayPassWord(boolean flag) {
        displayPassWord = flag;
    }

}

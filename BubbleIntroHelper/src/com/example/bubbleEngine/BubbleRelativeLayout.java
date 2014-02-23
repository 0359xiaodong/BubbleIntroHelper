package com.example.bubbleEngine;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.example.bubbleintrohelper.R;

public class BubbleRelativeLayout extends RelativeLayout {

	public static interface BubbleLayoutChangeListener {
		void onConfigurationChanged(final Configuration newConfig);
	}

	public static enum BubbleLegOrientation {
		TOP, LEFT, RIGHT, BOTTOM, NONE
	};

	public static float PADDING = 30;
	public static float LEG_HALF_BASE = 30;
	public static float STROKE_WIDTH = 2f;
	public static float CORNER_RADIUS = 8f;
	public static int SHADOW_COLOR = Color.argb(100, 0, 0, 0);
	public static float MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;

	private Paint mFillPaint = null;
	private final Path mPath = new Path();
	private final Path mBubbleLegPrototype = new Path();
	private final Paint mPaint = new Paint(Paint.DITHER_FLAG);
	private BubbleLayoutChangeListener mBubbleLayoutChangeListener = null;

	private float mBubbleLegOffset = 0.75f;
	private BubbleLegOrientation mBubbleOrientation = BubbleLegOrientation.LEFT;

	public BubbleRelativeLayout(Context context) {
		this(context, null);
	}

	public BubbleRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public BubbleRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public void setBubbleLayoutChangeListener(final BubbleLayoutChangeListener bubbleLayoutChangeListener) {
		mBubbleLayoutChangeListener = bubbleLayoutChangeListener;
	}

	private int dpToPx(float dp) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		return (int) ((dp * displayMetrics.density) + 0.5);
	}

	private void init(final Context context, final AttributeSet attrs) {

		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.bubble_intro_helper);

			try {

				PADDING = typedArray.getDimensionPixelSize(R.styleable.bubble_intro_helper_padding, dpToPx(18));
				SHADOW_COLOR = typedArray.getInt(R.styleable.bubble_intro_helper_shadowColor, Color.argb(100, 0, 0, 0));
				LEG_HALF_BASE = typedArray.getDimensionPixelSize(R.styleable.bubble_intro_helper_halfBaseOfLeg, dpToPx(18));
				MIN_LEG_DISTANCE = PADDING + LEG_HALF_BASE;
				STROKE_WIDTH = typedArray.getFloat(R.styleable.bubble_intro_helper_strokeWidth, 2f);
				CORNER_RADIUS = typedArray.getFloat(R.styleable.bubble_intro_helper_cornerRadius, 8f);

			} finally {

				if (typedArray != null) {
					typedArray.recycle();
				}
			}
		}

		mPaint.setColor(SHADOW_COLOR);
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeCap(Cap.BUTT);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(STROKE_WIDTH);
		mPaint.setStrokeJoin(Paint.Join.MITER);
		mPaint.setPathEffect(new CornerPathEffect(CORNER_RADIUS));

		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
		}

		mFillPaint = new Paint(mPaint);
		mFillPaint.setColor(Color.WHITE);
		mFillPaint.setShader(new LinearGradient(100f, 0f, 100f, 200f, Color.WHITE, Color.WHITE, TileMode.CLAMP));

		if (Build.VERSION.SDK_INT >= 11) {
			setLayerType(LAYER_TYPE_SOFTWARE, mFillPaint);
		}
		mPaint.setShadowLayer(2f, 2F, 5F, SHADOW_COLOR);

		renderBubbleLegPrototype();

		setPadding((int) PADDING, (int) PADDING, (int) PADDING, (int) PADDING);

	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (mBubbleLayoutChangeListener != null) {
			mBubbleLayoutChangeListener.onConfigurationChanged(newConfig);
		}

	}

	private void renderBubbleLegPrototype() {

		mBubbleLegPrototype.moveTo(0, 0);
		mBubbleLegPrototype.lineTo(PADDING * 3, -PADDING);
		mBubbleLegPrototype.lineTo(PADDING * 3, PADDING);
		mBubbleLegPrototype.close();

	}

	public void setBubbleParams(final BubbleLegOrientation bubbleOrientation, final float bubbleOffset) {

		mBubbleLegOffset = bubbleOffset;
		mBubbleOrientation = bubbleOrientation;

	}

	private Matrix renderBubbleLegMatrix(final float width, final float height) {

		final float offset = Math.max(mBubbleLegOffset, MIN_LEG_DISTANCE);

		float dstX = 0;
		float dstY = Math.min(offset, height - MIN_LEG_DISTANCE);
		;
		final Matrix matrix = new Matrix();

		switch (mBubbleOrientation) {

		case TOP:
			dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
			dstY = 0;
			matrix.postRotate(90);
			break;

		case RIGHT:
			dstX = width;
			dstY = Math.min(offset, height - MIN_LEG_DISTANCE);
			matrix.postRotate(180);
			break;

		case BOTTOM:
			dstX = Math.min(offset, width - MIN_LEG_DISTANCE);
			;
			dstY = height;
			matrix.postRotate(270);
			break;

		}

		matrix.postTranslate(dstX, dstY);
		return matrix;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		final float width = canvas.getWidth();
		final float height = canvas.getHeight();

		mPath.rewind();
		mPath.addRoundRect(new RectF(PADDING, PADDING, width - PADDING, height - PADDING), CORNER_RADIUS, CORNER_RADIUS, Direction.CW);
		mPath.addPath(mBubbleLegPrototype, renderBubbleLegMatrix(width, height));

		canvas.drawPath(mPath, mPaint);
		canvas.scale((width - STROKE_WIDTH) / width, (height - STROKE_WIDTH) / height, width / 2f, height / 2f);

		canvas.drawPath(mPath, mFillPaint);
	}
}

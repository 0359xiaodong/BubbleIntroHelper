package com.example.bubbleEngine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bubbleEngine.AlertDialogFragment.AlertDialogFragmentListener;
import com.example.bubbleEngine.BubbleRelativeLayout.BubbleLayoutChangeListener;
import com.example.bubbleEngine.BubbleRelativeLayout.BubbleLegOrientation;
import com.example.bubbleintrohelper.R;

public class HelpPopupWindow extends PopupWindow implements BubbleLayoutChangeListener {

	private static final String PRIVATE_PREF = "WhereAmI";
	private static final String VERSION_KEY = "version_number";

	private float mTopPos = 0;
	private float mLeftPos = 0;
	private float mRightPos = 0;
	private float mBottomPos = 0;
	private float mXoffsetPercentage = 50f;
	private float mYoffsetPercentage = 50f;

	private View mParent = null;
	private Integer mID = HelpPopupManager.INVALID_ID_VALUE;
	private Integer mForVersionCode = HelpPopupManager.INVALID_ID_VALUE;
	private BubbleRelativeLayout mBubbleRelativeLayout = null;

	private View.OnClickListener mOnNotifyNextClick = null;
	private View.OnClickListener mOnNotifyCancelClick = null;
	private PopupWindow.OnDismissListener mOnNotifyDismissListener = null;

	private PopupWindow.OnDismissListener mOnDismissListener = new OnDismissListener() {

		@Override
		public void onDismiss() {

			if (mOnNotifyDismissListener != null) {
				mOnNotifyDismissListener.onDismiss();
			}
		}
	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mID == null) ? 0 : mID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HelpPopupWindow other = (HelpPopupWindow) obj;
		if (mID == null) {
			if (other.mID != null)
				return false;
		} else if (!mID.equals(other.mID))
			return false;
		return true;
	}

	private View.OnClickListener mOnNextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			mOnNotifyDismissListener = null;

			dismiss();

			if (mOnNotifyNextClick != null) {
				mOnNotifyNextClick.onClick(v);
			}
		}
	};

	private View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {

		@Override
		public void onClick(final View v) {

			ActionBarActivity activity = (ActionBarActivity) getContentView().getContext();

			new AlertDialogFragment(R.string.alert_title_info, R.string.alert_cancel_help, new AlertDialogFragmentListener() {

				@Override
				public void onPositiveClick() {
					mOnNotifyDismissListener = null;

					dismiss();

					if (mOnNotifyCancelClick != null) {
						mOnNotifyCancelClick.onClick(v);
					}
				}

				@Override
				public void onNegativeClick() {
					// TODO Auto-generated method stub

				}
			}).showDialog(activity.getSupportFragmentManager());
		}
	};

	public void setOnNotifyCancelClick(View.OnClickListener mOnNotifyCancelClick) {
		this.mOnNotifyCancelClick = mOnNotifyCancelClick;
	}

	public void setOnNotifyDismissListener(PopupWindow.OnDismissListener mOnNotifyDismissListener) {
		this.mOnNotifyDismissListener = mOnNotifyDismissListener;
	}

	public void setOnNotifyNextClick(View.OnClickListener mOnNotifyNextClick) {
		this.mOnNotifyNextClick = mOnNotifyNextClick;
	}

	public HelpPopupWindow(final int forVersionCode, final int ID, final View parent, final String content) {

		this(forVersionCode, ID, parent, 50f, 50f, content);

	}

	public HelpPopupWindow(final int forVersionCode, final int ID, final View parent, final float xOffsetPercentage, final float yOffsetPercentage, final String content) {
		super(parent.getContext());

		mID = ID;
		mParent = parent;
		mForVersionCode = forVersionCode;
		mXoffsetPercentage = xOffsetPercentage;
		mYoffsetPercentage = yOffsetPercentage;

		final LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View contentView = inflater.inflate(R.layout.help_popup_window, null);
		setContentView(contentView);

		setFocusable(true);
		setOutsideTouchable(false);
		setClippingEnabled(false);

		setBackgroundDrawable(new ColorDrawable(0));
		setAnimationStyle(R.style.AnimationPopup);

		final Button buttonNext = (Button) contentView.findViewById(R.id.buttonNext);
		buttonNext.setOnClickListener(mOnNextClickListener);

		final Button buttonCancel = (Button) contentView.findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(mOnCancelClickListener);

		setTouchInterceptor(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				if (motionEvent.getAction() == KeyEvent.ACTION_UP) {
					return false;
				}

				final View touchAllowedControls[] = new View[] { buttonNext, buttonCancel };

				for (final View touchView : touchAllowedControls) {
					Rect rect = new Rect();
					int location[] = new int[2];

					touchView.getHitRect(rect);
					touchView.getLocationOnScreen(location);
					rect.offsetTo(location[0], location[1]);

					if (rect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
						return false;
					}
				}

				return true;
			}
		});

		setOnDismissListener(mOnDismissListener);

		final TextView textViewContent = (TextView) contentView.findViewById(R.id.txtContent);
		textViewContent.setText(content);

		mBubbleRelativeLayout = (BubbleRelativeLayout) contentView;
		mBubbleRelativeLayout.setBubbleLayoutChangeListener(this);

	}

	public void setParent(final View parent) {
		mParent = parent;
	}

	public View getParent() {
		return mParent;
	}

	public Integer getID() {
		return mID;
	}

	public Integer getForVersionCode() {
		return mForVersionCode;
	}

	public boolean isShownAlready(final boolean writeToFile) {

		final SharedPreferences sharedPref = mParent.getContext().getSharedPreferences(PRIVATE_PREF, Context.MODE_PRIVATE);

		int storedVersionCode;

		try {
			storedVersionCode = sharedPref.getInt(mID.toString(), HelpPopupManager.INVALID_VERSION_CODE_VALUE);
		
		} catch (Exception ex) {
			
			final Editor editor = sharedPref.edit();
			editor.remove(mID.toString());
			editor.commit();
			
			storedVersionCode = HelpPopupManager.INVALID_VERSION_CODE_VALUE;
		}

		if (storedVersionCode != HelpPopupManager.INVALID_VERSION_CODE_VALUE && storedVersionCode >= mForVersionCode) {
			return true;
		}

		if (writeToFile) {
			final Editor editor = sharedPref.edit();
			editor.putInt(mID.toString(), mForVersionCode);
			editor.commit();
		}
		return false;

	}

	public void show() {

		if (isShownAlready(true)) {
			return;
		}
		showOrUpdatePopup(false);
	}

	private void showOrUpdatePopup(final boolean update) {

		final DisplayMetrics dispMetrics = new DisplayMetrics();

		final WindowManager wm = (WindowManager) mParent.getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dispMetrics);

		final RelativeLayout contentView = (RelativeLayout) getContentView();
		contentView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

		final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(dispMetrics.widthPixels, MeasureSpec.AT_MOST);
		final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(dispMetrics.heightPixels, MeasureSpec.AT_MOST);

		contentView.measure(widthMeasureSpec, heightMeasureSpec);

		final float bubbleWidth = contentView.getMeasuredWidth();
		final float bubbleHeight = contentView.getMeasuredHeight();

		setWindowLayoutMode(1, 1);

		setWidth((int) bubbleWidth);
		setHeight((int) bubbleHeight + 10);

		final int[] absolutePosition = new int[2];
		mParent.getLocationOnScreen(absolutePosition);

		final float xOff = mParent.getWidth() * mXoffsetPercentage / 100f;
		final float yOff = mParent.getHeight() * mYoffsetPercentage / 100f;

		mLeftPos = (float) (absolutePosition[0] + xOff);
		mRightPos = (float) (dispMetrics.widthPixels - mLeftPos);
		mTopPos = (float) (absolutePosition[1] + mParent.getHeight() - yOff);
		mBottomPos = (float) (dispMetrics.heightPixels - mTopPos);

		final BubbleLegOrientation bubbleOrientation = resolveBubbleLegOrientation(bubbleWidth - BubbleRelativeLayout.PADDING, bubbleHeight - BubbleRelativeLayout.PADDING,
				dispMetrics);

		final float bubbleLegOffset = computeBubbleLegOffset(dispMetrics, bubbleOrientation, bubbleWidth, bubbleHeight);

		computePinPointAndShow(update, bubbleOrientation, bubbleLegOffset, bubbleWidth, bubbleHeight);
	}

	private BubbleLegOrientation resolveBubbleLegOrientation(final float bubbleWidth, final float bubbleHeight, final DisplayMetrics dispMetrics) {

		final boolean isMinTopBottomLegDistance = Math.min(mTopPos, mBottomPos) > BubbleRelativeLayout.MIN_LEG_DISTANCE;
		final boolean isMinLeftRightLegDistance = Math.min(mLeftPos, mRightPos) > BubbleRelativeLayout.MIN_LEG_DISTANCE;

		BubbleLegOrientation bubbleOrientation = (dispMetrics.widthPixels > (bubbleWidth + mLeftPos)) && isMinTopBottomLegDistance ? BubbleLegOrientation.LEFT
				: BubbleLegOrientation.RIGHT;

		if (bubbleOrientation == BubbleLegOrientation.RIGHT) {

			bubbleOrientation = (bubbleWidth < mLeftPos) && isMinTopBottomLegDistance ? bubbleOrientation : BubbleLegOrientation.BOTTOM;

			if (bubbleOrientation == BubbleLegOrientation.BOTTOM) {

				bubbleOrientation = (bubbleHeight < mTopPos) && isMinLeftRightLegDistance ? bubbleOrientation : BubbleLegOrientation.TOP;

				if (bubbleOrientation == BubbleLegOrientation.TOP) {

					bubbleOrientation = (dispMetrics.heightPixels > (bubbleHeight + mTopPos)) && isMinLeftRightLegDistance ? bubbleOrientation : BubbleLegOrientation.NONE;
				}
			}
		}

		return bubbleOrientation;
	}

	private float computeBubbleLegOffset(final DisplayMetrics dispMetrics, final BubbleLegOrientation bubbleLegOrientation, final float bubbleWidth, final float bubbleHeight) {

		if (bubbleLegOrientation == BubbleLegOrientation.BOTTOM || bubbleLegOrientation == bubbleLegOrientation.TOP) {

			if (mLeftPos < mRightPos) {

				return Math.min(mLeftPos, bubbleWidth / 2f);

			} else {

				return Math.max(bubbleWidth - mRightPos, bubbleWidth / 2f);
			}

		}

		if (mTopPos < mBottomPos) {

			return Math.min(mTopPos, bubbleHeight / 2f);

		} else {

			return Math.max(bubbleHeight - mBottomPos, bubbleHeight / 2f);
		}

	}

	private void computePinPointAndShow(final boolean update, final BubbleLegOrientation bubbleLegOrientation, final float legOffset, final float bubbleWidth,
			final float bubbleHeight) {

		mBubbleRelativeLayout.setBubbleParams(bubbleLegOrientation, legOffset);

		float xOff = mParent.getWidth() * mXoffsetPercentage / 100f;
		float yOff = -mParent.getHeight() * mYoffsetPercentage / 100f;

		if (bubbleLegOrientation == BubbleLegOrientation.TOP) {

			xOff -= legOffset;
			yOff += 0;

		} else if (bubbleLegOrientation == BubbleLegOrientation.BOTTOM) {

			xOff -= legOffset;
			yOff -= bubbleHeight;

		} else if (bubbleLegOrientation == BubbleLegOrientation.LEFT) {

			xOff += 0;
			yOff -= legOffset;

		} else if (bubbleLegOrientation == BubbleLegOrientation.RIGHT) {

			xOff -= bubbleWidth;
			yOff -= legOffset;
		}

		if (update) {
			getContentView().invalidate();
			update(mParent, (int) xOff, (int) yOff, (int) bubbleWidth, (int) bubbleHeight);

		} else {
			showAsDropDown(mParent, (int) xOff, (int) yOff);

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				showOrUpdatePopup(true);
			}
		}, 100);
	}
}

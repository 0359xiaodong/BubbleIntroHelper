package com.example.bubbleintrohelper;

import com.example.bubbleEngine.HelpPopupManager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {

	private static final int SCROLL_ENDED_UPDATION_INTERVAL = 250;
	private Runnable scrollEndedRunnable = new Runnable() {

		@Override
		public void run() {
			HelpPopupManager.startShowing();
		}
	};

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScrollView(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		removeCallbacks(scrollEndedRunnable);
		postDelayed(scrollEndedRunnable, SCROLL_ENDED_UPDATION_INTERVAL);

	}

}

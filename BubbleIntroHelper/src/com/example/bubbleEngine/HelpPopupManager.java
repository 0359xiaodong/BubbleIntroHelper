package com.example.bubbleEngine;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.PopupWindow;

public final class HelpPopupManager {

	public static int INVALID_ID_VALUE = 0;
	public static int INVALID_VERSION_CODE_VALUE = -1;

	private static boolean mIsEnabled = false;
	private static volatile HelpPopupWindow mCurrentHelpPopup = null;

	private static SortedMap<Integer, HelpPopupWindow> mHelpPopups = Collections.synchronizedSortedMap(new TreeMap<Integer, HelpPopupWindow>());

	private static OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					startShowing();
				}
			}, 500);

		}
	};

	private static PopupWindow.OnDismissListener mOnDismissListener = new PopupWindow.OnDismissListener() {

		@Override
		public void onDismiss() {

			removeCurrentPopup();
			startShowing();
		}
	};

	private static View.OnClickListener mOnNextClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			removeCurrentPopup();
			startShowing();
		}
	};

	private static View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};

	private static void removeCurrentPopup() {

		if (mCurrentHelpPopup != null) {

			mHelpPopups.remove(mCurrentHelpPopup.getID());
			mCurrentHelpPopup = null;
		}

	}

	/**
	 * Assign popup window to the specific parent view. Once the window is shown it will not be shown in the future anymore. 
	 * To provide popup's showing again one must clear app's storage data.
	 * 
	 * 
	 * @param forVersionCode   App version code for which popup will be shown
	 * @param ID   An ID of a popup window
	 * @param parent   A parent view to which this popup window will belong
	 * @param content   Context
	 * @return
	 */
	public static synchronized HelpPopupWindow addHelpPopupWindow(final int forVersionCode, final int ID,
			final View parent, final String content) {

		if (ID == HelpPopupManager.INVALID_ID_VALUE) {
			return null;

		}

		HelpPopupWindow popupWindow = mHelpPopups.get(ID);

		if (popupWindow != null) {

			// reinitialize parent
			if (!popupWindow.getParent().equals(parent)) {

				parent.getViewTreeObserver().addOnGlobalLayoutListener(
						mOnGlobalLayoutListener);
				popupWindow.setParent(parent);

			}

			return popupWindow.isShownAlready(false) ? null : popupWindow;

		} else {

			parent.getViewTreeObserver().addOnGlobalLayoutListener(
					mOnGlobalLayoutListener);
			popupWindow = new HelpPopupWindow(forVersionCode, ID, parent, content);

			if (popupWindow.isShownAlready(false)) {
				return null;
			}
		}

		popupWindow.setOnNotifyCancelClick(mOnCancelClickListener);
		popupWindow.setOnNotifyNextClick(mOnNextClickListener);
		popupWindow.setOnNotifyDismissListener(mOnDismissListener);

		mHelpPopups.put(ID, popupWindow);
		return popupWindow;
	}

	public static void setEnabled(final boolean enabled) {

		mIsEnabled = enabled;

	}

	public static synchronized void startShowing() {

		if (!mIsEnabled || mCurrentHelpPopup != null) {
			return;
		}

		final Iterator<Entry<Integer, HelpPopupWindow>> iterator = mHelpPopups
				.entrySet().iterator();

		while (iterator.hasNext()) {

			final HelpPopupWindow tempPopupWindow = (HelpPopupWindow) iterator
					.next().getValue();

			final Rect rootHitRect = new Rect();
			tempPopupWindow.getParent().getRootView().getHitRect(rootHitRect);

			if (tempPopupWindow.getParent().isShown()
					&& tempPopupWindow.getParent().getLocalVisibleRect(
							rootHitRect)) {

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					
					tempPopupWindow
							.getParent()
							.getViewTreeObserver()
							.removeOnGlobalLayoutListener(
									mOnGlobalLayoutListener);
					
				} else {
					
					tempPopupWindow
							.getParent()
							.getViewTreeObserver()
							.removeGlobalOnLayoutListener(
									mOnGlobalLayoutListener);
					

				}

				tempPopupWindow.show();
				mCurrentHelpPopup = tempPopupWindow;

				break;
			}
		}
	}
	
}

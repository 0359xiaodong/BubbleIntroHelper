package com.example.bubbleEngine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.util.Linkify;
import android.view.Gravity;
import android.widget.TextView;

import com.example.bubbleintrohelper.R;

public class AlertDialogFragment extends DialogFragment {

	public static interface AlertDialogFragmentListener {
		public void onPositiveClick();

		public void onNegativeClick();
	}

	private String mMessage = null;
	private int mTitleResId =   -1;
	private int mMessageResId = -1;
	
	private AlertDialogFragmentListener mAlertDialogFragmentListener;

	public AlertDialogFragment(final int titleResID, final int messageResId, final AlertDialogFragmentListener alertDialogFragmentListener) {
		super();
		setRetainInstance(true);

		mTitleResId = titleResID;
		mMessageResId = messageResId;
		mAlertDialogFragmentListener = alertDialogFragmentListener;

	}

	public AlertDialogFragment(final int titleResID, final String message) {
		super();
		setRetainInstance(true);

		mTitleResId = titleResID;
		mMessage = message;

	}

	public AlertDialogFragment(final int titleResID, final int messageResId) {
		super();
		setRetainInstance(true);

		mTitleResId = titleResID;
		mMessageResId = messageResId;

	}
	
	public AlertDialogFragment() {
		super();
		setRetainInstance(true);
	}

	public void showDialog(final FragmentManager fragmentManager) {

		Fragment prev = fragmentManager.findFragmentByTag("alertDialog");
		FragmentTransaction ft = fragmentManager.beginTransaction();

		if (prev != null) {
			ft.remove(prev);
		}

		ft.addToBackStack(null);
		show(ft, "alertDialog");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Dialog dialog;

		TextView txtContent = new TextView(getActivity());
		txtContent.setGravity(Gravity.CENTER);
		txtContent.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Base_CompactMenu_Dialog);
		txtContent.setText(mMessage != null ? mMessageResId : mMessageResId);		
		Linkify.addLinks(txtContent, Linkify.ALL);

		if (mAlertDialogFragmentListener == null) {
			dialog = new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle(mTitleResId).setView(txtContent).setPositiveButton(android.R.string.ok, null)
					.create();

		} else {
			dialog = new AlertDialog.Builder(getActivity()).setIcon(R.drawable.ic_launcher).setTitle(mTitleResId).setView(txtContent)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							mAlertDialogFragmentListener.onPositiveClick();
						}
					}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							mAlertDialogFragmentListener.onNegativeClick();
						}
					}).create();
		}

		return dialog;
	}

}

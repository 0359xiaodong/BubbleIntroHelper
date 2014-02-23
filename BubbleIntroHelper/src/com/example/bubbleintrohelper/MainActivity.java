package com.example.bubbleintrohelper;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.bubbleEngine.HelpPopupManager;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		TextView tvSmallTest = (TextView)findViewById(R.id.textViewSmallTest);
		HelpPopupManager.addHelpPopupWindow(1, 1, tvSmallTest, "This is small text");

		TextView tvLargeTest = (TextView)findViewById(R.id.textViewLargeTest);
		HelpPopupManager.addHelpPopupWindow(1, 2, tvLargeTest, "This is large text");

		RadioButton radioButtonTest = (RadioButton)findViewById(R.id.radioButtonTest);
		HelpPopupManager.addHelpPopupWindow(1, 3, radioButtonTest, "This is radio button test");
		
		TextView buttonTest = (Button)findViewById(R.id.buttonTest);
		HelpPopupManager.addHelpPopupWindow(1, 4, buttonTest, "This is button text");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		HelpPopupManager.setEnabled(true);
		HelpPopupManager.startShowing();
		
		return true;
	}

}

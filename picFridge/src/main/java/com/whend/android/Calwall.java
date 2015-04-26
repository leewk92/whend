package com.whend.android;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;



public class Calwall extends FragmentActivity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_calwall);
		//CalendarView a = new CalendarView();
	}

	
/*	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}*/

}
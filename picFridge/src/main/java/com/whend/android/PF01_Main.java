package com.whend.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

public class PF01_Main extends FragmentActivity {
	FragmentTabHost tabHost;
	private static final String TAB1 = "Calendar";
	private static final String TAB2 = "CalFeed";
	private static final String TAB3 = "CalList";
	
	 private void hideKeyboard(){

			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	           
		 setContentView(R.layout.activity_pf01_tmpmain);
		 
	        
	        tabHost = (FragmentTabHost)findViewById(R.id.tabhost);
	        tabHost.setup(this, getSupportFragmentManager(),R.id.realtabcontent);
	        
	         // 그림이 짤려서 높이 조절 코드 추가
	        
	        tabHost.addTab(tabHost.newTabSpec(TAB1).setIndicator("", getApplicationContext().getResources().getDrawable(R.drawable.tab1_selector)), CTCalendarViewFragment.class ,null);
	        tabHost.addTab(tabHost.newTabSpec(TAB2).setIndicator("", getApplicationContext().getResources().getDrawable(R.drawable.tab2_selector)), PF03_Calfeed.class ,null);
	        tabHost.addTab(tabHost.newTabSpec(TAB3).setIndicator("", getApplicationContext().getResources().getDrawable(R.drawable.tab3_selector)), PF04_Callist.class ,null);
	   
	       
	        for (int tab = 0; tab < tabHost.getTabWidget().getChildCount(); ++tab) {
	        	Log.d("tab Height", "" + tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height);
	        	tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
	        	Log.d("tab Height", "" + tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height);
	        	}
	        
	     tabHost.setCurrentTab(1);
    }

}


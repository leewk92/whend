package com.whend.android;

import android.app.Activity;

public class CalendarViewFragment extends AbstractActivityFragment {
	 
	   @Override
	   public Class<? extends Activity> getActivityClass() {
	      return CalendarView.class;
	   }
	}

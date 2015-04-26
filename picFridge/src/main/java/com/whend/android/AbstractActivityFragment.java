package com.whend.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;

public abstract class AbstractActivityFragment extends LocalActivityManagerFragment {
	 
private final static String ACTIVITY_TAG = "hosted";
 
@Override
   public View onCreateView(LayoutInflater inflater,
                  ViewGroup container,
                  Bundle savedInstanceState) {
      Intent intent = new Intent(getActivity(), getActivityClass());
 
      final Window window = getLocalActivityManager().startActivity(ACTIVITY_TAG, intent);
      final View windowDecor = (window != null) ? window.getDecorView() : null;
 
      if (windowDecor != null) {
         ViewParent parent = windowDecor.getParent();
         if (parent != null) {
            // detach the child view from its parent
            ViewGroup v = (ViewGroup) parent;
            v.removeView(windowDecor);
         }
 
         windowDecor.setVisibility(View.VISIBLE);
         windowDecor.setFocusableInTouchMode(true);
         if (windowDecor instanceof ViewGroup) {
            ((ViewGroup) windowDecor)
               .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
         }
      }
      return windowDecor;
   }
 
   public Activity getHostedActivity() {
      return getLocalActivityManager().getActivity(ACTIVITY_TAG);
   }
 
   /* 추상클래스를 상속하는 클래스에서 wrapping하기 원하는 Activity의 클래스 정보를 넘겨주는 메소드를 구현한다. */
   protected abstract Class<? extends Activity> getActivityClass();
}

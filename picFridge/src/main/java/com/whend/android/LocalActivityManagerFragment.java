package com.whend.android;

import android.app.Fragment;
import android.app.LocalActivityManager;
import android.os.Bundle;

/**
 * LocalActivityManager를 가진 Fragment를 만듭니다.
 */
public class LocalActivityManagerFragment extends Fragment {
 
   // LocalActivityManager가 관리하는 Activity를 식별하는 키 입니다.
   private static final String KEY_STATE_BUNDLE = "MY_ACTIVITY_KEY";
 
   private LocalActivityManager mManager;
 
   protected LocalActivityManager getLocalActivityManager() {
      return mManager;
   }
 
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Bundle state = null;
      if (savedInstanceState != null) {
         state = savedInstanceState
                    .getBundle(KEY_STATE_BUNDLE);
      }
      mManager =
         new LocalActivityManager(getActivity(), true);
      mManager.dispatchCreate(state);
   }
 
   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putBundle(KEY_STATE_BUNDLE,
         mManager.saveInstanceState());
   }
 
   @Override
   public void onResume() {
      super.onResume();
      mManager.dispatchResume();
   }
 
   @Override
   public void onPause() {
      super.onPause();
      mManager.dispatchPause(getActivity().isFinishing());
   }
 
   @Override
   public void onStop() {
      super.onStop();
      mManager.dispatchStop();
   }
 
   @Override
   public void onDestroy() {
      super.onDestroy();
      mManager.dispatchDestroy(getActivity().isFinishing());
   }
}

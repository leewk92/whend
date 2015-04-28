package com.whend.android;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.whend.android.Calfeed_Adapter.AsyncAddDeleteFeedfromWall;
import com.whend.android.PF04_Callist.SynchronizeDatabaseToCallist;
import com.whend.android.Start.AttemptLogin;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;


public class PF03_Calfeed extends Fragment implements OnRefreshListener{
    public static int HaveToSync = 1;
    public static ArrayList<Calfeed> Calfeed_list = new ArrayList<Calfeed>();
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    public int no_itemlog;
    public SQLiteDatabase db;
    public View rootView = null;
    SwipeListView swipelistview = null;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ProgressDialog loadingDialog;
    public int scrollY=0;
    public int scrollX=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        
        Tracker t = ((ApplicationController)getActivity().getApplication()).getTracker(ApplicationController.TrackerName.APP_TRACKER);
        t.setScreenName("onPF03_Calfeed");
        t.send(new HitBuilders.AppViewBuilder().build());

        // getActivity().setContentView(R.layout.frag_pf03__calfeed);
    }
	
/*	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		   //inflater.inflate(R.menu.pf03__itemlog, menu);
		    ((PF01_Main)getActivity()).setActionBarTitle(" Cal-feed");
		}*/

    @Override
    public void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this.getView().getContext()).reportActivityStart(this.getActivity());
    }

    @Override
    public void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this.getView().getContext()).reportActivityStop(this.getActivity());
    }


    @Override
    public void onPause(){

        super.onPause();

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

    }


    public static PF03_Calfeed newInstance(){
        PF03_Calfeed fragment = new PF03_Calfeed();
        return fragment;
    }

    private void hideKeyboard(){

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.frag_pf03__calfeed, container, false);
        if(HaveToSync ==1){
            new SynchronizeDatabaseToWall().execute();
        }
    	
   /* 	LinearLayout mainLayout = (LinearLayout) rootView.findViewById(R.id.pf03_root);
    	mainLayout.setOnClickListener(new View.OnClickListener() {
    	            @Override
    	            public void onClick(View v) {
 //   	                hideKeyboard();
    	            }
    	        });
    */


        final EditText calfeed_find = (EditText) rootView.findViewById(R.id.calfeed_find);
        ImageView calfeed_find_btn = (ImageView) rootView.findViewById(R.id.calfeed_find_btn);

        calfeed_find.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                String search = calfeed_find.getText().toString();

                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    new SynchronizeDatabaseToWall(search).execute();
                }

                return false;
            }
        });

        calfeed_find.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(calfeed_find.getWindowToken(), 0);
                }
            }});

        calfeed_find_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String search = calfeed_find.getText().toString();
                //Toast.makeText(getActivity(), search, 1000).show();

                new SynchronizeDatabaseToWall(search).execute();
            }
        });


        // As we're using a ListFragment we create a PullToRefreshLayout manually
 //       mPullToRefreshLayout = new PullToRefreshLayout(rootView.getContext());

        // We can now setup the PullToRefreshLayout



        // 	ListView listview = (ListView)rootView.findViewById(R.id.listView2);
        //		listview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list));


        swipelistview=(SwipeListView)rootView.findViewById(R.id.example_swipe_lv_list);
 	/*	swipelistview.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
//				hideKeyboard();
				return false;
			}
 			
 		});*/
        swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {

            public boolean isRight;
            @Override
            public void onClickFrontView(int position) {

                Log.d("swipe", String.format("onClickFrontView %d", position));

                //swipelistview.openAnimate(position); //when you touch front view it will open

            }
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d - right %s ", position, action, String.valueOf(right)));
                isRight = right;

            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }


            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));

                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                Log.d("swipe", String.format("dismiss right %s", String.valueOf(isRight)));

                if(isRight == true){

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("schedule_key", String.valueOf(Calfeed_list.get(reverseSortedPositions[0]).getEventPrimary())));
                    params.add(new BasicNameValuePair("add_delete","0"));
                    new AsyncAddDeleteFeedfromWall(params,getActivity()).execute();
                    Calfeed_list.remove(reverseSortedPositions[0]);
                    //  swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.custom_row,Calfeed_list) );
                }


            }

        });
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
        //	swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
        swipelistview.setOffsetLeft(convertDpToPixel(0f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(0f)); // right side offset
        swipelistview.setAnimationTime(50); // Animation time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress
        swipelistview.setSwipeCloseAllItemsWhenMoveList(true);

        //  swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list) );
        swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.custom_row,Calfeed_list) );

        return rootView;
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.log_refresh :

                //refresh_list(rootView, db);

                Toast toast = Toast.makeText(getActivity(), "Itemlog Refreshed", Toast.LENGTH_SHORT);
                toast.show();

                break;

        }
        return true;
    }


    void Loading(String cont){
        loadingDialog = ProgressDialog.show(getActivity(), null,cont,true);
        Log.d("Loading Dialog","Dialog");

        final Thread thread = new Thread(new Runnable(){
            public void run(){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    handler.sendEmptyMessage(0);
                }
            }

        });

        thread.start();
        rootView.setTranslationY(scrollY);

    }
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            loadingDialog.dismiss();
            Log.d("Loading Dialog","DialogDismiss");
        }
    };
		
		/*
	private void refresh_list(View rootView, SQLiteDatabase db) {
		// TODO Auto-generated method stub
    	//startDownload();
    	Calfeed_list = new ArrayList<Calfeed>();
    	
    	no_itemlog = 0;
 

        	ListView listview = (ListView)rootView.findViewById(R.id.listView2);
    		listview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list));
    		
     		//results.close();
        }*/

    class SynchronizeDatabaseToWall extends AsyncTask<String, String, String> {

        private String search = null;
        private String searchReq_url = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_calfeed_search.php";
        private ProgressDialog downloadDialog;

        public SynchronizeDatabaseToWall(String search){
            this.search = search;
            // this.url =
        }

        public SynchronizeDatabaseToWall(){
            //this.url =
        }

        public void downloadLoading(String cont){
            downloadDialog = new ProgressDialog(getActivity());
            downloadDialog.setMessage(cont);
            downloadDialog.show();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Calfeed_list = new ArrayList<Calfeed>();
            Log.d("preexecute","yes");
            if(search == null || search.equals("")){
                downloadLoading("일정을 가져옵니다...");
            }else{
                downloadLoading("검색중입니다...");
            }

        }

        private void syncWallFromWeb() throws JSONException{
            AppPrefs appPrefs = new AppPrefs(getActivity());
            String user_id = appPrefs.getUser_id();
            String password = appPrefs.getUser_password();
            JSONParser.user_id = user_id;
            JSONParser.password = password;
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("user_id", user_id));
            param.add(new BasicNameValuePair("password", password));

            String imageUrl = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_";
            String tmpUrl_company, tmpUrl_content;

            JSONObject wallReq = JSONParser.makeHttpRequest("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_calfeed_data.php","POST",param);
            Calfeed tmp = null;
            for(int i=0; i<wallReq.getInt("size"); i++){
                String page_icon = wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
                Long start = wallReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_starttime");
                Long end = wallReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_endtime");
                String page_id = wallReq.getJSONObject(String.valueOf(i)).getString("page_id");
                String schedule_forpage_id = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_id");
                String schedule_forpage_photo = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_photo");
                String schedule_forpage_memo = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_memo");
                int schedule_forpage_primary = wallReq.getJSONObject(String.valueOf(i)).getInt("schedule_forpage_primary");
                int follower_count = wallReq.getJSONObject(String.valueOf(i)).getInt("follower_count");
                int page_primary = wallReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
                int schedule_forpage_allday = wallReq.getJSONObject(String.valueOf(i)).getInt("schedule_forpage_allday");
                //	int abletoremove = wallReq.getJSONObject(String.valueOf(i)).getInt("abletoremove");
                //	int abletoadd = wallReq.getJSONObject(String.valueOf(i)).getInt("abletoadd");
                //	String page_master = wallReq.getJSONObject(String.valueOf(i)).getString("page_master");
                Date date_start=new Date(start);
                Date date_end=new Date(end);

                //			Log.d("master",wallReq.getJSONObject(String.valueOf(i)).getString("page_master"));
                Log.d("following",wallReq.getJSONObject(String.valueOf(i)).getString("following"));

                Log.d("schedule_forpage_id",schedule_forpage_id);
                boolean is_inCalendar=false;
                tmpUrl_company = imageUrl + page_icon;
                tmpUrl_content = imageUrl + schedule_forpage_photo;
                if(end == 0){
                    end = start + 3600*1000;
                }

                if(wallReq.getJSONObject(String.valueOf(i)).getInt("following") == 1){
                    is_inCalendar = true;
                    Log.d("isincalendar","true");
                }
                else{
                    is_inCalendar = false;
                    Log.d("isincalendar","false");

                }
                String path_company = tmpUrl_company.toString().substring(tmpUrl_company.toString().lastIndexOf('/'), tmpUrl_company.toString().length());
                String path_content = tmpUrl_content.toString().substring(tmpUrl_content.toString().lastIndexOf('/'), tmpUrl_content.toString().length());

                Log.d("path_content", path_content);

                new Image_DownloadFileAsync(getActivity()).execute(tmpUrl_company, "1", "1");
                new Image_DownloadFileAsync(getActivity()).execute(tmpUrl_content, "1", "1");

                //Calfeed??follow_num怨?is_incalendar 異붽?
                tmp = new Calfeed(page_id,schedule_forpage_id , start, end, schedule_forpage_memo, "0", path_company, path_content, follower_count ,is_inCalendar, schedule_forpage_primary, page_primary);
                tmp.setAllday(schedule_forpage_allday);
			/*	if(page_master != "null"){	//?ш린?쒕뒗 留덉뒪???꾩슂?놁쓬
					tmp.setIsMaster(true);
					Log.d("master","master");
				}else{
					tmp.setIsMaster(false);
				}*/

                Calfeed_list.add(tmp);
            }

        }

        private void syncWallFromWeb_search() throws JSONException{
            AppPrefs appPrefs = new AppPrefs(getActivity());
            String user_id = appPrefs.getUser_id();
            String password = appPrefs.getUser_password();
            JSONParser.user_id = user_id;
            JSONParser.password = password;

            String imageUrl = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_";
            String tmpUrl_company, tmpUrl_content;

            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("user_id", user_id));
            param.add(new BasicNameValuePair("password", password));
            try {
                param.add(new BasicNameValuePair("search",URLEncoder.encode(search, "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JSONObject searchReq = JSONParser.makeHttpRequest(searchReq_url,"POST", param);

            Calfeed_list.clear();
            Calfeed tmp = null;
            for(int i=0; i<searchReq.getInt("size"); i++){
                String page_icon = searchReq.getJSONObject(String.valueOf(i)).getString("page_icon");
                Long start = searchReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_starttime");
                Long end = searchReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_endtime");
                String page_id = searchReq.getJSONObject(String.valueOf(i)).getString("page_id");
                String schedule_forpage_id = searchReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_id");
                String schedule_forpage_photo = searchReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_photo");
                String schedule_forpage_memo = searchReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_memo");
                int schedule_forpage_primary = searchReq.getJSONObject(String.valueOf(i)).getInt("schedule_forpage_primary");
                int follower_count = searchReq.getJSONObject(String.valueOf(i)).getInt("follow_num");
                int page_primary = searchReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
                int schedule_forpage_allday = searchReq.getJSONObject(String.valueOf(i)).getInt("schedule_forpage_allday");
                //	int abletoremove = searchReq.getJSONObject(String.valueOf(i)).getInt("abletoremove");
                //	int abletoadd = searchReq.getJSONObject(String.valueOf(i)).getInt("abletoadd");
                Date date_start=new Date(start);
                Date date_end=new Date(end);
                //	Log.d("master",searchReq.getJSONObject(String.valueOf(i)).getString("page_master"));
                //		Log.d("following",searchReq.getJSONObject(String.valueOf(i)).getString("following"));
                Log.d("schedule_forpage_id",schedule_forpage_id);
                boolean is_inCalendar=false;
                tmpUrl_company = imageUrl + page_icon;
                tmpUrl_content = imageUrl + schedule_forpage_photo;
                if(end == 0){
                    end = start + 3600*1000;
                }
				
				/*if(searchReq.getJSONObject(String.valueOf(i)).getInt("following") == 1){
					is_inCalendar = true;
				Log.d("isincalendar","true");
				}
				else{ 
					is_inCalendar = false;
					Log.d("isincalendar","false");
					
				}*/
                String path_company = tmpUrl_company.toString().substring(tmpUrl_company.toString().lastIndexOf('/'), tmpUrl_company.toString().length());
                String path_content = tmpUrl_content.toString().substring(tmpUrl_content.toString().lastIndexOf('/'), tmpUrl_content.toString().length());

                Log.d("path_content", path_content);

                new Image_DownloadFileAsync(getActivity()).execute(tmpUrl_company, "1", "1");
                new Image_DownloadFileAsync(getActivity()).execute(tmpUrl_content, "1", "1");

                //Calfeed??follow_num怨?is_incalendar 異붽?
                tmp = new Calfeed(page_id,schedule_forpage_id , start, end, schedule_forpage_memo, "0", path_company, path_content, follower_count ,is_inCalendar, schedule_forpage_primary, page_primary);
                tmp.setAllday(schedule_forpage_allday);

                Calfeed_list.add(tmp);
            }

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.d("doing","yes");
            try {
                if(search == null || search.equals("")){
                    syncWallFromWeb();
                }else{
                    syncWallFromWeb_search();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            //pDialog.dismiss();
            Log.d("postexecute","yes");
            //       ListView listview = (ListView)rootView.findViewById(R.id.listView2);
            //		listview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list));
            HaveToSync = 1;
            search = null;

            if (downloadDialog != null)
            {
                downloadDialog.dismiss();
            }


            swipelistview=(SwipeListView)rootView.findViewById(R.id.example_swipe_lv_list);
            swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list) );
            swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {

                public boolean isRight;
                @Override
                public void onClickFrontView(int position) {

                    Log.d("swipe", String.format("onClickFrontView %d", position));

                    //swipelistview.openAnimate(position); //when you touch front view it will open

                }
                @Override
                public void onOpened(int position, boolean toRight) {
                }

                @Override
                public void onClosed(int position, boolean fromRight) {
                }

                @Override
                public void onListChanged() {
                }

                @Override
                public void onMove(int position, float x) {
                }

                @Override
                public void onStartOpen(int position, int action, boolean right) {
                    Log.d("swipe", String.format("onStartOpen %d - action %d - right %s ", position, action, String.valueOf(right)));
                    isRight = right;

                }

                @Override
                public void onStartClose(int position, boolean right) {
                    Log.d("swipe", String.format("onStartClose %d", position));
                }


                @Override
                public void onClickBackView(int position) {
                    Log.d("swipe", String.format("onClickBackView %d", position));

                    swipelistview.closeAnimate(position);//when you touch back view it will close
                }

                @Override
                public void onDismiss(int[] reverseSortedPositions) {
                    Log.d("swipe", String.format("dismiss right %s", String.valueOf(isRight)));

                    if(isRight == true){//?ㅻⅨ履쎌쑝濡?諛湲?: ?쇰뱶?덈낫湲?

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("schedule_key", String.valueOf(Calfeed_list.get(reverseSortedPositions[0]).getEventPrimary())));
                        params.add(new BasicNameValuePair("add_delete","0"));

                        Calfeed_list.remove(reverseSortedPositions[0]);

                        swipelistview=(SwipeListView)rootView.findViewById(R.id.example_swipe_lv_list);
                        swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list) );


                        new AsyncAddDeleteFeedfromWall(params,getActivity()).execute();
         //               new SynchronizeDatabaseToWall().execute();

                        Loading("월에서 일정을 지우는 중입니다");


                        //  swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.custom_row,Calfeed_list) );

                    }else if(isRight == false){//?쇱そ?쇰줈 諛湲?: 罹섎┛?붿뿉 ?ｊ린

                        ContentValues values = new ContentValues();

                        values.put(Events.DTSTART, Calfeed_list.get(reverseSortedPositions[0]).getStart());
                        values.put(Events.HAS_ALARM, 0);
                        values.put(Events.DTEND, Calfeed_list.get(reverseSortedPositions[0]).getEnd());
                        values.put(Events.EVENT_COLOR, Color.BLUE);
                        values.put(Events.TITLE, Calfeed_list.get(reverseSortedPositions[0]).getItems());
                        values.put(Events.DESCRIPTION, Calfeed_list.get(reverseSortedPositions[0]).getPlace());		//硫붾え濡?諛붽퓭?쇳븿
                        values.put(Events.CALENDAR_ID, 98723198); //my_calendar ID
                        values.put(Events.EVENT_TIMEZONE, "UTC");
                        values.put(Events._ID,1000000000-Calfeed_list.get(reverseSortedPositions[0]).getEventPrimary() );		//?뱀떆?섍껸移좉퉴遊?. 洹몃윺?쇱??놁?留?
                        values.put(Events.ALL_DAY, Calfeed_list.get(reverseSortedPositions[0]).getAllday());		// ?닿쾬???앹꽦?먮줈 媛?몄???.

                        getActivity().getContentResolver().insert(Events.CONTENT_URI, values);
                        //           Account my_account = new Account("WhendCalendar","com.whend.demo.account.DEMOACCOUNT");
                        //	    	Uri creationUri = asSyncAdapter(Events.CONTENT_URI, my_account.name, my_account.type);
                        //	    	Uri calendarData = mContext.getContentResolver().insert(creationUri, values);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();

                        params.add(new BasicNameValuePair("schedule_key", String.valueOf(Calfeed_list.get(reverseSortedPositions[0]).getEventPrimary())));
                        params.add(new BasicNameValuePair("add_delete","1"));

                        Calfeed_list.remove(reverseSortedPositions[0]);
                        swipelistview=(SwipeListView)rootView.findViewById(R.id.example_swipe_lv_list);
                        swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list) );


                        new AsyncAddDeleteFeedfromWall(params,getActivity()).execute();
         //               new SynchronizeDatabaseToWall().execute();
                        Loading("캘린더에 일정을 넣는 중입니다");
                    }	
                   
                    
                    swipelistview.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {

                        @Override
                        public void onScrollChanged() {

                            scrollX = swipelistview.getScrollX(); //for horizontalScrollView
                            scrollY = swipelistview.getScrollY(); //for verticalScrollView
                            Log.d("scrollY",String.valueOf(scrollY));
                            Log.d("scrollX",String.valueOf(scrollX));
                                 
                            
                            //DO SOMETHING WITH THE SCROLL COORDINATES

                        }
                    });
                    
                    
                    
                }

            });
            swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
            //	swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
            swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions
            swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
            swipelistview.setOffsetLeft(convertDpToPixel(0f)); // left side offset
            swipelistview.setOffsetRight(convertDpToPixel(0f)); // right side offset
            swipelistview.setAnimationTime(10); // Animation time
            swipelistview.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress
            swipelistview.setSwipeCloseAllItemsWhenMoveList(true);

            //  swipelistview.setAdapter(new Calfeed_Adapter(getActivity(),R.layout.list_calfeed_row,Calfeed_list) );



        }

    }

    private Uri asSyncAdapter(Uri uri, String account, String accountType)
    {
        return uri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").appendQueryParameter(Calendars.ACCOUNT_NAME,account).appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build();

    }

    class AsyncAddDeleteFeedfromWall extends AsyncTask<String, String, String> {
        private Activity a;
        private List<NameValuePair> params;
        public AsyncAddDeleteFeedfromWall(List<NameValuePair> _params, Activity ac){
            this.params = _params;
            this.a = ac;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... param) {
            // TODO Auto-generated method stub

            // getting product details by making HTTP request
            AppPrefs appPrefs = new AppPrefs(a.getApplicationContext());
            String user_id = appPrefs.getUser_id();
            String password = appPrefs.getUser_password();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("password", password));

            JSONObject json = JSONParser.makeHttpRequest(
                    "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_calfeed_add.php", "POST", params);
            try {
                Log.d("wall following",json.getString("success"));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            //pDialog.dismiss();


        }
    }

    @Override
    public void onRefreshStarted(View view) {
        // TODO Auto-generated method stub

    }
}
	
	
	
	
	


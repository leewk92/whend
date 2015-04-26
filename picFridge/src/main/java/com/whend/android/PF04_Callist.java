package com.whend.android;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.whend.android.CTCalendarViewFragment;
import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
 
public class PF04_Callist extends Fragment {
	
	private int TAKE_FROM_CAMERA = 1;
	private int TAKE_FROM_GALLERY = 2;
	private int CROP_FROM_CAMERA = 3;
	private int REQ_CODE_SELECT_IMAGE = 100;
	private Uri mImageCaptureUri;

    private int NEW_CALENDAR_ADD = 1;
    private int NEW_PERSONAL_CALENDAR_ADD = 2;
    private boolean NEW_CALENDAR = false;
    private boolean NEW_PERSONAL_CALENDAR = false;
	
    AlertDialog.Builder builder;
    AlertDialog alertdialog;
    static AlertDialog ad;
    
	public static ArrayList<Following_Feed> Following_Feed_list = new ArrayList<Following_Feed>();  
	public static ArrayList<Following_Feed> Likely_Following_Feed_list = new ArrayList<Following_Feed>();
	public static ArrayList<Following_Feed> Personal_Following_Feed_list = new ArrayList<Following_Feed>();
	public static ArrayList<Calfeed> Calfeed_Dialog_list = new ArrayList<Calfeed>();
	
    public static int HaveToSync = 1;

	public View rootView = null;


    OnTouchListener disable_scroll = new ListView.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                v.getParent().requestDisallowInterceptTouchEvent(false);
                
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                v.getParent().requestDisallowInterceptTouchEvent(false);
            	
                break;
            case MotionEvent.ACTION_MOVE:
            	v.getParent().requestDisallowInterceptTouchEvent(true);
                
            }

            // Handle HorizontalScrollView touch events.
            v.onTouchEvent(event);
            return true;
        }
    }; 
    
    @Override
	public void onPause(){

		super.onPause();
	
		getActivity().getWindow().setSoftInputMode(
			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
			);
	}
    
    private void hideKeyboard(){

		InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	}
    
    @Override    
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	    Tracker t = ((ApplicationController)getActivity().getApplication()).getTracker(ApplicationController.TrackerName.APP_TRACKER);
	    t.setScreenName("onPF04_Callist");
	    t.send(new HitBuilders.AppViewBuilder().build());
	}
	
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
    
    
    public static PF04_Callist newInstance(){
    	PF04_Callist fragment = new PF04_Callist();
    	return fragment;
    }
    
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		   //inflater.inflate(R.menu.pf04__default, menu);
		
		}
	
    @SuppressLint("ClickableViewAccessibility")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	
    	rootView = inflater.inflate(R.layout.frag_pf04__callist, container, false);
    	
    	LinearLayout mainLayout = (LinearLayout) rootView.findViewById(R.id.pf04_root);
    	mainLayout.setOnClickListener(new View.OnClickListener() {
    	            @Override
    	            public void onClick(View v) {
    	                hideKeyboard();
    	            }
    	        });
    	
    	final TextView calfeed_find = (TextView) rootView.findViewById(R.id.callist_find);
    	ImageView calfeed_find_btn = (ImageView) rootView.findViewById(R.id.callist_find_btn);
    	ImageView newcalendar_add = (ImageView) rootView.findViewById(R.id.callist_newcalendar_add);
    	
    	
    	calfeed_find_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String search = calfeed_find.getText().toString();
				//Toast.makeText(getActivity(), search, 1000).show();
				
				new SynchronizeDatabaseToCallist(search).execute();
			}
		});
		    	
		calfeed_find.setOnKeyListener(new View.OnKeyListener() {
					
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				String search = calfeed_find.getText().toString();
				
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					new SynchronizeDatabaseToCallist(search).execute();
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
    	
    	newcalendar_add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Context mContext = getActivity().getApplicationContext();
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
				
				final View layout = inflater.inflate(R.layout.new_calendar_add,(ViewGroup) getActivity().findViewById(R.id.layout_root));
				
				ImageView new_calendar_image = (ImageView) layout.findViewById(R.id.mycalendar_image);
				Button new_calendar_btn = (Button) layout.findViewById(R.id.new_calendar_btn);
				final EditText new_calendar_name = (EditText) layout.findViewById(R.id.new_calendar_name);
				final EditText new_calendar_memo = (EditText) layout.findViewById(R.id.new_calendar_memo);
				//final CheckBox new_calendar_share = (CheckBox) layout.findViewById(R.id.new_calendar_share);
				
				AlertDialog.Builder aDialog = new AlertDialog.Builder(getActivity());
				
				aDialog.setView(layout);
				
				aDialog.setPositiveButton("캘린더 추가", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
		               				
						String cal_name = new_calendar_name.getText().toString();
						String cal_memo = new_calendar_memo.getText().toString();
						//String img_path = ????;
						//boolean check_share = new_calendar_share.isChecked();
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();	 
						AppPrefs appPrefs = new AppPrefs(getActivity().getApplicationContext());
				        String user_id = appPrefs.getUser_id();
				        String password = appPrefs.getUser_password(); 
				        
				        params.add(new BasicNameValuePair("user_id",user_id));
				        params.add(new BasicNameValuePair("password",password));
				        Log.d("upload user_id",user_id);
				        Log.d("upload password",password);
				        Log.d("upload cal_name",cal_name);
				        Log.d("upload cal_memo",cal_memo);
				        
				        try {
				        	params.add(new BasicNameValuePair("calendar_name", URLEncoder.encode(cal_name, "UTF-8")));
							params.add(new BasicNameValuePair("calendar_text", URLEncoder.encode(cal_memo, "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//Toast.makeText(getActivity(), ""+check_share, 1000).show();
						new PhotoRegister(params, "").execute();
                        list_refresh(NEW_CALENDAR_ADD);
						
						//String img_name = ???;
						
					}
				}); 
				
				aDialog.setNegativeButton("안하기", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				
				final AlertDialog ad = aDialog.create();

				
				new_calendar_btn.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_PICK);
						
						if(new_calendar_name.getText().toString() != null){
							intent.putExtra("calendar_name", new_calendar_name.getText().toString());
							Log.d("hi",new_calendar_name.getText().toString());
						}
						
						if(new_calendar_memo.getText().toString() != null)
						intent.putExtra("calendar_memo", new_calendar_memo.getText().toString());
						
						/*if (new_calendar_share.isChecked())
							intent.putExtra("calendar_share", true);
						else
							intent.putExtra("calendar_share", true);		//나중에 false로 바꾸도록하자 . 원경*/
						
						intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
						intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
						
						ad.dismiss();
						
					}
				});
				

				
				ad.show();
				
			}
		});
    	
    	
    	
    	if(HaveToSync ==1){
    		new SynchronizeDatabaseToCallist().execute();
    	}
    	

    	HorizontalListView listview1 = (HorizontalListView)rootView.findViewById(R.id.follow_listview);
 		listview1.setAdapter(new Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Following_Feed_list));

 		
 		
 		HorizontalListView listview2 = (HorizontalListView)rootView.findViewById(R.id.likely_follow_listview);
 		listview2.setAdapter(new Likely_Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Likely_Following_Feed_list));

 		HorizontalListView listview3 = (HorizontalListView)rootView.findViewById(R.id.personal_follow_listview);
 		listview3.setAdapter(new Personal_Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Personal_Following_Feed_list));

 		listview1.setOnTouchListener(disable_scroll);
 		listview2.setOnTouchListener(disable_scroll);
 		listview3.setOnTouchListener(disable_scroll);
 		
 		listview1.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				// TODO Auto-generated method stub
				return false;
			}
		});
 		
		listview2.setOnTouchListener(new View.OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						hideKeyboard();
						// TODO Auto-generated method stub
						return false;
					}
				});
		 		
		
		listview3.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				// TODO Auto-generated method stub
				return false;
			}
		});
			
 		
			
        return rootView;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != getActivity().RESULT_OK)
			return;
			
		Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.new_calendar_add,(ViewGroup) getActivity().findViewById(R.id.layout_root));
		
		ImageView new_calendar_image = (ImageView) layout.findViewById(R.id.mycalendar_image);
		Button new_calendar_btn = (Button) layout.findViewById(R.id.new_calendar_btn);
		final EditText new_calendar_name = (EditText) layout.findViewById(R.id.new_calendar_name);
		final EditText new_calendar_memo = (EditText) layout.findViewById(R.id.new_calendar_memo);
		//final CheckBox new_calendar_share = (CheckBox) layout.findViewById(R.id.new_calendar_share);
		
		
		//Log.d("Extras",data.getExtras().getString("calendar_name"));
		//new_calendar_name.setText(data.getExtras().getString("calendar_name"));
		//new_calendar_memo.setText(data.getExtras().getString("calendar_memo"));
		//new_calendar_share.setChecked(data.getExtras().getBoolean("calendar_share"));
		
		
		AlertDialog.Builder aDialog = new AlertDialog.Builder(getActivity());
		
		aDialog.setView(layout);
		
		
		new_calendar_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK);
				
				if(new_calendar_name.getText().toString() != null)
					intent.putExtra("calendar_name", new_calendar_name.getText().toString());
				
				if(new_calendar_memo.getText().toString() != null)
				intent.putExtra("calendar_memo", new_calendar_memo.getText().toString());
				
				/*if (new_calendar_share.isChecked())
					intent.putExtra("calendar_share", true);
				else
					intent.putExtra("calendar_share", true);			//후에 false로 바꾸기. 원경*/
				
				intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
				startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
				
			}
		});

		
		if(requestCode == TAKE_FROM_GALLERY){
				mImageCaptureUri = data.getData(); // Get data from selected photo
		}else if (requestCode == TAKE_FROM_CAMERA){
			 if( data != null ){
				 mImageCaptureUri = data.getData();
		        }
		}else if(requestCode == REQ_CODE_SELECT_IMAGE ){
			mImageCaptureUri = data.getData();
		}
		
		try {
			Bitmap bm = Images.Media.getBitmap(getActivity().getContentResolver(), mImageCaptureUri);
			
			Cursor c = getActivity().getContentResolver().query(Uri.parse(mImageCaptureUri.toString()),null,null,null,null);
			c.moveToNext();
			final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
			
			int height = bm.getHeight();
			int width = bm.getWidth();
			
			Bitmap resized = null;
			
			if (height > width){
				while (height > 250) { // If the size is too big, this will resize 
					resized = Bitmap.createScaledBitmap(bm, (width * 250) / height, 250, true);
					height = resized.getHeight();
					width = resized.getWidth();
					}
			}else{
				while (width > 250) { // If the size is too big, this will resize 
					resized = Bitmap.createScaledBitmap(bm, 250, height * 250 / width, true);
					height = resized.getHeight();
					width = resized.getWidth();
					}
			}
			if (resized != null)
				new_calendar_image.setImageBitmap(resized);
			else
				new_calendar_image.setImageBitmap(bm);
				
			
			aDialog.setPositiveButton("캘린더 추가", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
	               							
					String cal_name = new_calendar_name.getText().toString();
					String cal_memo = new_calendar_memo.getText().toString();
					//String img_path = ????;
					//boolean check_share = new_calendar_share.isChecked();
					
					List<NameValuePair> params = new ArrayList<NameValuePair>();	 
					AppPrefs appPrefs = new AppPrefs(getActivity().getApplicationContext());
			        String user_id = appPrefs.getUser_id();
			        String password = appPrefs.getUser_password(); 
			        
			        params.add(new BasicNameValuePair("user_id",user_id));
			        params.add(new BasicNameValuePair("password",password));
			        Log.d("upload user_id",user_id);
			        Log.d("upload password",password);
			        Log.d("upload cal_name",cal_name);
			        Log.d("upload cal_memo",cal_memo);
			        
			        try {
			        	params.add(new BasicNameValuePair("calendar_name", URLEncoder.encode(cal_name, "UTF-8")));
						params.add(new BasicNameValuePair("calendar_text", URLEncoder.encode(cal_memo, "UTF-8")));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//Toast.makeText(getActivity(), ""+check_share, 1000).show();
					new PhotoRegister(params, absolutePath).execute();
                    NEW_CALENDAR = true;
                    list_refresh(NEW_CALENDAR_ADD);
					
					//String img_name = ???;
					
				}
			}); 
			
			aDialog.setNegativeButton("안하기", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			
			AlertDialog ad = aDialog.create();
			ad.show();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    
	    
	class SynchronizeDatabaseToCallist extends AsyncTask<String, String, String> {
				
		private String search = null;
		private String wallReq_url = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_following.php";
		private String recReq_url = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_recommend2.php";
		private String searchReq_url = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_search.php";

        private ProgressDialog downloadDialog;

		public SynchronizeDatabaseToCallist(String search){
		    	this.search = search;
		    	// this.url = 
	    }
	    
	    public SynchronizeDatabaseToCallist(){
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
            Following_Feed_list = new ArrayList<Following_Feed>();
            Likely_Following_Feed_list = new ArrayList<Following_Feed>();
            Personal_Following_Feed_list = new ArrayList<Following_Feed>();
            Log.d("Callist_preexecute", "yes");
            if (NEW_CALENDAR) {
                downloadLoading("새로운 캘린더를 추가합니다...");
                NEW_CALENDAR = false;
            } else if (NEW_PERSONAL_CALENDAR) {
                downloadLoading("개인 맞춤 캘린더를 추가합니다...");
                NEW_PERSONAL_CALENDAR = false;
            } else if (search == null || search.equals(""))
                downloadLoading("캘린더를 불러옵니다...");
            else
                downloadLoading("검색중입니다...");

	        
	    }
	    
		 private void syncCallistFromWeb() throws JSONException{
			 AppPrefs appPrefs = new AppPrefs(getActivity());
	        String user_id = appPrefs.getUser_id();
	        String password = appPrefs.getUser_password(); 
	        JSONParser.user_id = user_id;
	        JSONParser.password = password;
	        List<NameValuePair> param = new ArrayList<NameValuePair>();	        
	        param.add(new BasicNameValuePair("user_id", user_id));
	        param.add(new BasicNameValuePair("password", password));
	        String imageUrl = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_";
	        String tmpUrl;
	        
			JSONObject wallReq = JSONParser.makeHttpRequest("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_following.php","POST",param);
			Following_Feed tmp = null;
			for(int i=0; i<wallReq.getInt("size"); i++){
				
				String page_key = wallReq.getJSONObject(String.valueOf(i)).getString("page_key");
				String page_id = wallReq.getJSONObject(String.valueOf(i)).getString("page_id");
				String tmpfollowing = wallReq.getJSONObject(String.valueOf(i)).getString("following");
				String discription = wallReq.getJSONObject(String.valueOf(i)).getString("page_text");
				String page_icon = wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				int page_primary = wallReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
				int follow_num = wallReq.getJSONObject(String.valueOf(i)).getInt("follow_num");
				String page_master = wallReq.getJSONObject(String.valueOf(i)).getString("page_master");
				
				tmpUrl = imageUrl + page_icon;
				Log.d("Callist_tmpUrl",tmpUrl);
				String path = tmpUrl.toString().substring(tmpUrl.toString().lastIndexOf('/'), tmpUrl.toString().length());
				Log.d("path",path);
				new Image_DownloadFileAsync(getActivity()).execute(tmpUrl, "1", "1");
				Log.d("master",wallReq.getJSONObject(String.valueOf(i)).getString("page_master"));
				Log.d("following",wallReq.getJSONObject(String.valueOf(i)).getString("following"));
				

				tmp = new Following_Feed(page_id, discription, path,page_key, page_primary, follow_num);		//여기에 icon address 넣으면 됨
				
				if(tmpfollowing.compareTo("0")==0){
					Log.d("following", "false, true");
					tmp.setIsCalFollow(false);
					tmp.setIsWallFollow(true);					
				}else if(tmpfollowing.compareTo("1")==0){
					Log.d("following", "true, false");
					tmp.setIsCalFollow(true);
					tmp.setIsWallFollow(false);
				}else{
					Log.d("following", "false, false");
					tmp.setIsCalFollow(false);
					tmp.setIsWallFollow(false);
				}
				
				if(page_master != "null"){
					tmp.setIsMaster(true);
					Log.d("master","master");
				}else{
					tmp.setIsMaster(false);
				}
				
				Following_Feed_list.add(tmp);
			}
			
			
	        
			JSONObject likely_wallReq = JSONParser.makeHttpRequest("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_recommend2.php","POST",param);
			Following_Feed likely_tmp = null;
			Log.d("recommend size",String.valueOf(likely_wallReq.getInt("size")));
			for(int i=0; i<likely_wallReq.getInt("size"); i++){
				
				String page_key = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_key");
				String page_id = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_id");
				String tmpfollowing = likely_wallReq.getJSONObject(String.valueOf(i)).getString("following");
				String discription = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_text");
				String page_icon = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				int page_primary = likely_wallReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
				int follow_num = likely_wallReq.getJSONObject(String.valueOf(i)).getInt("follow_num");
				String page_master = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_master");
				
				tmpUrl = imageUrl + page_icon;
				Log.d("Callist_tmpUrl",tmpUrl);
				String path = tmpUrl.toString().substring(tmpUrl.toString().lastIndexOf('/'), tmpUrl.toString().length());
				Log.d("Callist_path",path);
				new Image_DownloadFileAsync(getActivity()).execute(tmpUrl, "1", "1");
				
				Log.d("master_likely",likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_master"));
				Log.d("following",likely_wallReq.getJSONObject(String.valueOf(i)).getString("following"));
				
				
				likely_tmp = new Following_Feed(page_id, discription, path, page_key, page_primary, follow_num);
				//여기에 icon address 넣으면 됨
				if(tmpfollowing.compareTo("0")==0){
					likely_tmp.setIsWallFollow(true);
					likely_tmp.setIsCalFollow(false);
				}else if(tmpfollowing.compareTo("1")==0){
					likely_tmp.setIsCalFollow(true);
					likely_tmp.setIsWallFollow(false);
				}else{
					likely_tmp.setIsCalFollow(false);
					likely_tmp.setIsWallFollow(false);
				}
								
				if(page_master != "null"){
					likely_tmp.setIsMaster(true);
					Log.d("master_likely","master");
				}else{
					likely_tmp.setIsMaster(false);
				}
				Likely_Following_Feed_list.add(likely_tmp);
			}
			    
		 
			JSONObject personal_wallReq = JSONParser.makeHttpRequest("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_special.php","POST",param);
			Following_Feed personal_tmp = null;
			Log.d("personal size",String.valueOf(personal_wallReq.getInt("size")));
			for(int i=0; i<personal_wallReq.getInt("size"); i++){
				
				String page_key = personal_wallReq.getJSONObject(String.valueOf(i)).getString("page_key");
				String page_id = personal_wallReq.getJSONObject(String.valueOf(i)).getString("page_id");
				String tmpfollowing = personal_wallReq.getJSONObject(String.valueOf(i)).getString("following");
				String discription = personal_wallReq.getJSONObject(String.valueOf(i)).getString("page_text");
				String page_icon = personal_wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				int page_primary = personal_wallReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
				int follow_num = personal_wallReq.getJSONObject(String.valueOf(i)).getInt("follow_num");
				String page_master = personal_wallReq.getJSONObject(String.valueOf(i)).getString("page_master");
				
				tmpUrl = imageUrl + page_icon;
				Log.d("Callist_tmpUrl",tmpUrl);
				String path = tmpUrl.toString().substring(tmpUrl.toString().lastIndexOf('/'), tmpUrl.toString().length());
				Log.d("Callist_path",path);
				new Image_DownloadFileAsync(getActivity()).execute(tmpUrl, "1", "1");
				
				Log.d("personal master",personal_wallReq.getJSONObject(String.valueOf(i)).getString("page_master"));
				Log.d("personal following",personal_wallReq.getJSONObject(String.valueOf(i)).getString("following"));
				
				
				personal_tmp = new Following_Feed(page_id, discription, path, page_key, page_primary,  follow_num);
				//여기에 icon address 넣으면 됨
				if(tmpfollowing.compareTo("0")==0){
					personal_tmp.setIsWallFollow(true);
					personal_tmp.setIsCalFollow(false);
				}else if(tmpfollowing.compareTo("1")==0){
					personal_tmp.setIsCalFollow(true);
					personal_tmp.setIsWallFollow(false);
				}else{
					personal_tmp.setIsCalFollow(false);
					personal_tmp.setIsWallFollow(false);
				}
								
				if(page_master != "null"){
					personal_tmp.setIsMaster(true);
					Log.d("master","master");
				}else{
					personal_tmp.setIsMaster(false);
				}
				Personal_Following_Feed_list.add(personal_tmp);
			}
				    	
	    }
		 
		 private void syncCallistFromWeb_search() throws JSONException{
			 AppPrefs appPrefs = new AppPrefs(getActivity());
	        String user_id = appPrefs.getUser_id();
	        String password = appPrefs.getUser_password(); 
	        JSONParser.user_id = user_id;
	        JSONParser.password = password;
	        
	        String imageUrl = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_";
	        String tmpUrl;
	        
	        List<NameValuePair> param = new ArrayList<NameValuePair>();	        
	        param.add(new BasicNameValuePair("user_id", user_id));
	        param.add(new BasicNameValuePair("password", password));
	        try {
				param.add(new BasicNameValuePair("search",URLEncoder.encode(search, "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        JSONObject searchReq = JSONParser.makeHttpRequest(searchReq_url, "POST", param);
		//	JSONObject wallReq = JSONParser.getJSONFromUrl("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_following.php");
			Following_Feed tmp = null;
			Following_Feed_list.clear();
			
			for(int i=0; i<searchReq.getInt("size"); i++){
				

				String page_key = searchReq.getJSONObject(String.valueOf(i)).getString("page_key");
				String page_id = searchReq.getJSONObject(String.valueOf(i)).getString("page_id");
				String tmpfollowing = searchReq.getJSONObject(String.valueOf(i)).getString("following");
				String discription = searchReq.getJSONObject(String.valueOf(i)).getString("page_text");
				String page_icon = searchReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				int page_primary = searchReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
				int follow_num = searchReq.getJSONObject(String.valueOf(i)).getInt("follow_num");
				String page_master = searchReq.getJSONObject(String.valueOf(i)).getString("page_master");
				
				tmpUrl = imageUrl + page_icon;
				Log.d("Callist_tmpUrl",tmpUrl);
				String path = tmpUrl.toString().substring(tmpUrl.toString().lastIndexOf('/'), tmpUrl.toString().length());
				Log.d("path",path);
				new Image_DownloadFileAsync(getActivity()).execute(tmpUrl, "1", "1");
				
				Log.d("master",searchReq.getJSONObject(String.valueOf(i)).getString("page_master"));
				Log.d("following",searchReq.getJSONObject(String.valueOf(i)).getString("following"));
		
				tmp = new Following_Feed(page_id, discription, path,page_key, page_primary, follow_num);		//여기에 icon address 넣으면 됨
				if(tmpfollowing.compareTo("0")==0){
					tmp.setIsWallFollow(true);
					tmp.setIsCalFollow(false);
				}else if(tmpfollowing.compareTo("1")==0){
					tmp.setIsCalFollow(true);
					tmp.setIsWallFollow(false);
				}else{
					tmp.setIsCalFollow(false);
					tmp.setIsWallFollow(false);
				}
				
				if(page_master != "null"){
					tmp.setIsMaster(true);
					Log.d("master","master");
				}else{
					tmp.setIsMaster(false);
				}
				Following_Feed_list.add(tmp);
			}
			
			
	        JSONObject likely_wallReq = JSONParser.makeHttpRequest("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_recommend2.php","POST",param);
			Following_Feed likely_tmp = null;
			Likely_Following_Feed_list.clear();
			Log.d("recommend size",String.valueOf(likely_wallReq.getInt("size")));
			for(int i=0; i<likely_wallReq.getInt("size"); i++){
				
				String page_key = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_key");
				String page_id = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_id");
				String tmpfollowing = likely_wallReq.getJSONObject(String.valueOf(i)).getString("following");
				String discription = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_text");
				String page_icon = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				int page_primary = likely_wallReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
				int follow_num = likely_wallReq.getJSONObject(String.valueOf(i)).getInt("follow_num");
				String page_master = likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_master");
				
				tmpUrl = imageUrl + page_icon;
				Log.d("Callist_tmpUrl",tmpUrl);
				String path = tmpUrl.toString().substring(tmpUrl.toString().lastIndexOf('/'), tmpUrl.toString().length());
				Log.d("Callist_path",path);
				new Image_DownloadFileAsync(getActivity()).execute(tmpUrl, "1", "1");

				Log.d("master_likely",likely_wallReq.getJSONObject(String.valueOf(i)).getString("page_master"));
				Log.d("following",likely_wallReq.getJSONObject(String.valueOf(i)).getString("following"));
		
				
				likely_tmp = new Following_Feed(page_id, discription, path, page_key, page_primary, follow_num);
				//여기에 icon address 넣으면 됨
				if(tmpfollowing.compareTo("0")==0){
					likely_tmp.setIsWallFollow(true);
					likely_tmp.setIsCalFollow(false);
				}else if(tmpfollowing.compareTo("1")==0){
					likely_tmp.setIsCalFollow(true);
					likely_tmp.setIsWallFollow(false);
				}else{
					likely_tmp.setIsCalFollow(false);
					likely_tmp.setIsWallFollow(false);
				}
				
				
				if(page_master != "null"){
					likely_tmp.setIsMaster(true);
					Log.d("master_likely","master");
				}else{
					likely_tmp.setIsMaster(false);
					Log.d("notMaster","notMaster");
				}
				Likely_Following_Feed_list.add(likely_tmp);
				}
				    	
	    }
		 
		 
		 
		 
		 @Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				Log.d("Callist_doing","yes");
				try {
					if(search == null || search.equals("")){
						syncCallistFromWeb();
	//					TextView follow_confirm = (TextView) rootView.findViewById(R.id.follow_confirm);		//이렇게 만들고싶음 준삐
	//					follow_confirm.setText("나의 팔로잉 피드");
					}else{
						syncCallistFromWeb_search();
	//					TextView follow_confirm = (TextView) rootView.findViewById(R.id.follow_confirm);		//이거해주셈 준삐
	//					follow_confirm.setText("\"" + search + "\"로 검색한 피드");
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


                if (downloadDialog != null)
                {
                    downloadDialog.dismiss();
                }
				
	            Log.d("Callist_postexecute","yes");
	        	HorizontalListView listview1 = (HorizontalListView)rootView.findViewById(R.id.follow_listview);
	     		listview1.setAdapter(new Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Following_Feed_list));
	     		
	     		HorizontalListView listview2 = (HorizontalListView)rootView.findViewById(R.id.likely_follow_listview);
	     		listview2.setAdapter(new Likely_Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Likely_Following_Feed_list));
	    	
	     		HorizontalListView listview3 = (HorizontalListView)rootView.findViewById(R.id.personal_follow_listview);
	     		listview3.setAdapter(new Personal_Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Personal_Following_Feed_list));

	       		
	     		listview1.setOnTouchListener(disable_scroll);
	     		listview2.setOnTouchListener(disable_scroll);
	     		listview3.setOnTouchListener(disable_scroll);
	    
	     		
	     		Log.d("Callist_ListviewEnd","yes");
	     		HaveToSync = 1;
	     		search = null;
		    }


		}
    

	public class PhotoRegister extends AsyncTask<String, Integer, String>{
		
		private List<NameValuePair> params;
		String path;
		PhotoRegister( List<NameValuePair> _params, String path){
			this.params = _params;
			this.path = path;
		}
		
		@Override
		protected String doInBackground(String... urls) {
			String content = executeClient();
			return content;
		}	
	
		@Override
		protected void onPostExecute(String result) {
			Log.d("upload finish", "finish");
				
		}

		// 실제 전송하는 부분
		public String executeClient() {
			try {	
			URL url = new URL("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_calendar_create.php");
			String boundary = "SpecificString";
			URLConnection con = url.openConnection();
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			con.setDoOutput(true);
					
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			
			for(int i=0; i<params.size(); i++){
				wr.writeBytes("\r\n--" + boundary + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\"" + params.get(i).getName() + "\"\r\n\r\n" + params.get(i).getValue());
				Log.d("upload..","upload...");
			}			
			/*			
			wr.writeBytes("\r\n--" + boundary + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"msg\"\r\n\r\n" + commentPutEdit.getText().toString());
			wr.writeBytes("\r\n--" + boundary + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"suid\"\r\n\r\n" + clickSuid);
			wr.writeBytes("\r\n--" + boundary + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"uuid\"\r\n\r\n" + uuid);*/
				
				
			if(path != ""){	
				wr.writeBytes("\r\n--" + boundary + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n");
				wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
					
				Log.d("upload path", path);
				FileInputStream fileInputStream = new FileInputStream(path);
				int bytesAvailable = fileInputStream.available();
				int maxBufferSize = 1024;
				int bufferSize = Math.min(bytesAvailable, maxBufferSize);
				byte[] buffer = new byte[bufferSize];
				
				int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				while (bytesRead > 0)
				{
					// Upload file part(s)
					DataOutputStream dataWrite = new DataOutputStream(con.getOutputStream());
					dataWrite.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available(); 
					bufferSize = Math.min(bytesAvailable, maxBufferSize); 
					bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
				} 
				fileInputStream.close();
			}
			wr.writeBytes("\r\n--" + boundary + "--\r\n");
			wr.flush();
			DataInputStream is = new DataInputStream(con.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            Log.d("upload debug" , sb.toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		return null;
		}


			
	}
	
    
    public int getCount() {
		return Following_Feed_list.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

    public void list_refresh(int param){

        if(param == NEW_CALENDAR_ADD)
                NEW_CALENDAR = true;

         else if(param == NEW_PERSONAL_CALENDAR_ADD)
                NEW_PERSONAL_CALENDAR = true;


            new SynchronizeDatabaseToCallist().execute();


        HorizontalListView listview1 = (HorizontalListView)rootView.findViewById(R.id.follow_listview);
        listview1.setAdapter(new Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Following_Feed_list));


        HorizontalListView listview2 = (HorizontalListView)rootView.findViewById(R.id.likely_follow_listview);
        listview2.setAdapter(new Likely_Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Likely_Following_Feed_list));

        HorizontalListView listview3 = (HorizontalListView)rootView.findViewById(R.id.personal_follow_listview);
        listview3.setAdapter(new Personal_Following_Feed_Adapter(getActivity(),R.layout.frag_pf04__callist,Personal_Following_Feed_list));

        listview1.setOnTouchListener(disable_scroll);
        listview2.setOnTouchListener(disable_scroll);
        listview3.setOnTouchListener(disable_scroll);
    }


}
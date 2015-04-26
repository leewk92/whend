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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.devsmart.android.ui.HorizontalListView;
import com.whend.android.PF04_Callist.PhotoRegister;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.provider.CalendarContract.Calendars;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Following_Feed_Adapter extends ArrayAdapter<Following_Feed>{
	private ArrayList<Following_Feed> Following_list;
	private Activity activity;
	public static ArrayList<Calfeed> Calfeed_list = new ArrayList<Calfeed>();
	private int TAKE_FROM_CAMERA = 1;
	private int TAKE_FROM_GALLERY = 2;
	private int CROP_FROM_CAMERA = 3;
	private int REQ_CODE_SELECT_IMAGE = 100;
	String cachepath;
	private Uri asSyncAdapter(Uri uri, String account, String accountType) 
    { 
    	return uri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").appendQueryParameter(Calendars.ACCOUNT_NAME,account).appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build(); 
    	
    } 
	public Following_Feed_Adapter(Activity a, int textViewResourceId, ArrayList<Following_Feed> lists){
		super(a, textViewResourceId, lists);
		this.Following_list = lists;
		this.activity = a;
	}
	
	public static class ViewIds{
		public LinearLayout image_company;
		public TextView company_name;
		public TextView follow_num;
		public ToggleButton btn_cal_follow;
		public ToggleButton btn_wall_follow;
		public ImageView company_info;
	}
	
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		final ViewIds Ids;
		
		if(v==null){
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_followers_row, null);
			Ids = new ViewIds();
			
			Ids.image_company = (LinearLayout)v.findViewById(R.id.list_company_icon);
			Ids.company_info = (ImageView) v.findViewById(R.id.list_info);
			Ids.company_name = (TextView) v.findViewById(R.id.list_company_name);
			Ids.follow_num = (TextView) v.findViewById(R.id.list_follow_num);
			Ids.btn_cal_follow = (ToggleButton) v.findViewById(R.id.list_cal_follow);
			Ids.btn_wall_follow = (ToggleButton) v.findViewById(R.id.list_wall_follow);
			
			
					
			v.setTag(Ids);
		}else
			Ids = (ViewIds)v.getTag();
		
		final Following_Feed i = Following_list.get(position);
		
		if(i != null){
			
			
			//BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.yonsei);

			//Bitmap bitmap = drawable.getBitmap();
			
			Ids.company_name.setText(i.getCompany());
			Ids.follow_num.setText("팔로잉 : "+i.getFollowNum()+"명");
			
			
			if(i.isWallFollow())
				Ids.btn_wall_follow.setChecked(true);
			else
				Ids.btn_wall_follow.setChecked(false);
			
			
			if(i.isCalFollow())
				Ids.btn_cal_follow.setChecked(true);
			else
				Ids.btn_cal_follow.setChecked(false);
			
			
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			cachepath = this.getContext().getCacheDir().getPath()+ "/whend_icon";
//			Bitmap bm = BitmapFactory.decodeFile("/sdcard/whend"+i.getIcon(), options);
			Bitmap bm = BitmapFactory.decodeFile(cachepath+i.getIcon(), options);
			int height, width;
			
		//	Log.d("real","/sdcard/whend"+i.getIcon());
			Log.d("real",cachepath+i.getIcon());
			
			if(bm != null){				
				BitmapDrawable ob = new BitmapDrawable(activity.getResources(), bm);
				
				if (Build.VERSION.SDK_INT >= 16)
					Ids.image_company.setBackground(ob);
				else
					Ids.image_company.setBackgroundDrawable(ob);
				
				
			}else{
				Ids.image_company.setBackgroundResource(R.drawable.calfeed_default_image);
			}
		
			
			/// 버튼클릭리스너
			
			Button.OnClickListener mClickListener_wallfollow = new View.OnClickListener() {
								
				public void onClick(View v) {
				
					if(i.isCalFollow()==true && i.isWallFollow()==false){
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						try {Log.d("button: cal_follow to wall_follow",i.getPagekey());
							params.add(new BasicNameValuePair("page_key", URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.add(new BasicNameValuePair("page_primary", String.valueOf(i.getPagePrimary())));
						params.add(new BasicNameValuePair("wall_following","0"));
						try {
							params.add(new BasicNameValuePair("page_key",URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                new AsyncFollow(params,activity).execute();
						
		              //내장캘린더에서도 지우기
		                Account my_account = new Account("WhendCalendar", "com.whend.demo.account.DEMOACCOUNT");
		                String selection = "((" + Calendars._ID + " = ?) AND (" 
		                        + Calendars.ACCOUNT_TYPE + " = ?))";
						String[] selectionArgs = new String[] { String.valueOf(i.getPagePrimary()), "com.whend.demo.account.DEMOACCOUNT"}; 
						Uri creationUri = asSyncAdapter(Calendars.CONTENT_URI, my_account.name, my_account.type); 
				        v.getContext().getContentResolver().delete(creationUri, selection , selectionArgs);  

				        i.setIsCalFollow(false);
		                i.setIsWallFollow(true);
		                Ids.btn_cal_follow.setChecked(false);
		                Ids.btn_wall_follow.setChecked(true);
					}
					else if( i.isCalFollow()==false && i.isWallFollow()==true ){
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						try {Log.d("button: wall_follow to not_follow",i.getPagekey());
							params.add(new BasicNameValuePair("page_key", URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.add(new BasicNameValuePair("page_primary", String.valueOf(i.getPagePrimary())));
						params.add(new BasicNameValuePair("wall_following","2"));
						try {
							params.add(new BasicNameValuePair("page_key",URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                new AsyncFollow(params,activity).execute();
						
		                i.setIsCalFollow(false);
		                i.setIsWallFollow(false);
		                Ids.btn_cal_follow.setChecked(false);
		                Ids.btn_wall_follow.setChecked(false);
						
					}
					else if(i.isCalFollow()==false && i.isWallFollow()==false ){
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						try {Log.d("button: not follow to wall_follow",i.getPagekey());
							params.add(new BasicNameValuePair("page_key", URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.add(new BasicNameValuePair("page_primary", String.valueOf(i.getPagePrimary())));
						params.add(new BasicNameValuePair("wall_following","0"));
						try {
							params.add(new BasicNameValuePair("page_key",URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                new AsyncFollow(params,activity).execute();
						
		                i.setIsCalFollow(false);
		                i.setIsWallFollow(true);
		                Ids.btn_cal_follow.setChecked(false);
		                Ids.btn_wall_follow.setChecked(true);
					
					}
	
					
				}
			};
			
			Button.OnClickListener mClickListener_calfollow = new View.OnClickListener() {
				public void onClick(View v) {
					if(i.isCalFollow()==true && i.isWallFollow()==false){
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						try {Log.d("button: cal_follow to not_follow",i.getPagekey());
							params.add(new BasicNameValuePair("page_key", URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.add(new BasicNameValuePair("page_primary", String.valueOf(i.getPagePrimary())));
						params.add(new BasicNameValuePair("wall_following","2"));
						try {
							params.add(new BasicNameValuePair("page_key",URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                new AsyncFollow(params,activity).execute();
		                
		              //내장캘린더에서도 지우기
		                Account my_account = new Account("WhendCalendar", "com.whend.demo.account.DEMOACCOUNT");
		                String selection = "((" + Calendars._ID + " = ?) AND (" 
		                        + Calendars.ACCOUNT_TYPE + " = ?))";
						String[] selectionArgs = new String[] { String.valueOf(i.getPagePrimary()), "com.whend.demo.account.DEMOACCOUNT"}; 
						Uri creationUri = asSyncAdapter(Calendars.CONTENT_URI, my_account.name, my_account.type); 
				        v.getContext().getContentResolver().delete(creationUri, selection , selectionArgs);    
				        
				        i.setIsCalFollow(false);
		                i.setIsWallFollow(false);
		                Ids.btn_cal_follow.setChecked(false);
		                Ids.btn_wall_follow.setChecked(false);
					}
					else if( i.isCalFollow()==false && i.isWallFollow()==true ){
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						try {Log.d("button: wall_follow to cal_follow",i.getPagekey());
							params.add(new BasicNameValuePair("page_key", URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.add(new BasicNameValuePair("page_primary", String.valueOf(i.getPagePrimary())));
						params.add(new BasicNameValuePair("wall_following","1"));
						try {
							params.add(new BasicNameValuePair("page_key",URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                new AsyncFollow(params,activity).execute();
		                
		                i.setIsCalFollow(true);
		                i.setIsWallFollow(false);
		                Ids.btn_cal_follow.setChecked(true);
		                Ids.btn_wall_follow.setChecked(false);
						
					}
					else if(i.isCalFollow()==false && i.isWallFollow()==false ){
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						try {Log.d("button: not follow to cal_follow",i.getPagekey());
							params.add(new BasicNameValuePair("page_key", URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.add(new BasicNameValuePair("page_primary", String.valueOf(i.getPagePrimary())));
						params.add(new BasicNameValuePair("wall_following","1"));
						try {
							params.add(new BasicNameValuePair("page_key",URLEncoder.encode(i.getPagekey(), "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                new AsyncFollow(params,activity).execute();
						
		                
		                i.setIsCalFollow(true);
		                i.setIsWallFollow(false);
		                Ids.btn_cal_follow.setChecked(true);
		                Ids.btn_wall_follow.setChecked(false);
					
					}
				
				}
			};
			
			
			Ids.btn_wall_follow.setOnClickListener(mClickListener_wallfollow);
			Ids.btn_cal_follow.setOnClickListener(mClickListener_calfollow);
			Ids.company_info.setOnClickListener(new View.OnClickListener(){
				
				@Override
				public void onClick(View v) {
					ImageView cal_img = null;
					TextView cal_name = null;
					TextView cal_memo = null;
					Button cal_sched_add = null;
					View layout = null;
			    	
					
					Context mContext = activity.getApplicationContext();
					LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
					
					layout =  inflater.inflate(R.layout.calendar_dialog,(ViewGroup) activity.findViewById(R.id.lay_root));
					
					
					cal_img = (ImageView) layout.findViewById(R.id.calendar_image);
					cal_name = (TextView) layout.findViewById(R.id.calendar_name);
					cal_memo = (TextView) layout.findViewById(R.id.calendar_memo);
					cal_sched_add = (Button) layout.findViewById(R.id.calendar_schedule_add);
							
					if(i.isMaster()){
						Log.d("i'm master",i.getPagekey());
						cal_sched_add.setOnClickListener(new View.OnClickListener() {
						JSONObject schedule_data = new JSONObject();
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//일정추가 dialog 띄우기
								
				//				Intent intent=new Intent(activity,Calendar_Sched_Add.class);
								Log.d("feed_primary before", String.valueOf(i.getPagePrimary()));
								Intent intent2 = new Intent(activity, Calendar_Sched_Add.class);
								intent2.putExtra("primarykey", i.getPagePrimary());
				                activity.startActivity(intent2);
							}
						});
					}
					else
						cal_sched_add.setVisibility(View.GONE);

					
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 1;
					
	//				Bitmap bm = BitmapFactory.decodeFile("/sdcard/whend"+i.getIcon(), options);
					Bitmap bm = BitmapFactory.decodeFile(cachepath+i.getIcon(), options);
								
//					Log.d("real","/sdcard/whend"+i.getIcon());
					Log.d("real",cachepath+i.getIcon());
	
					if(bm != null){				
							
							cal_img.setImageBitmap(bm);
						
					}else{
						cal_img.setImageResource(R.drawable.calfeed_default_image);
					}
					
					cal_name.setText(i.getCompany());
					cal_memo.setText(i.getGroup());
					
	
					
					ListView listview = (ListView)layout.findViewById(R.id.listView3);
					
			 		listview.setAdapter(new Calfeed_forDialog_Adapter(activity ,R.layout.calendar_dialog_row, Calfeed_list));

									
					AlertDialog.Builder aDialog = new AlertDialog.Builder(activity);
					AlertDialog ad = aDialog.create();
		
					
					new SynchronizeDatabaseToCalinfo(String.valueOf(i.getPagePrimary()), layout, aDialog).execute();		
					
					
					//aDialog.setView(layout);
					//AlertDialog ad = aDialog.create();
					//ad.show();					
				}
			});
			
			
			
		}
			
		return v;
		
	}
	
	

	class AsyncFollow extends AsyncTask<String, String, String> {
		private Activity a;
		private List<NameValuePair> params;
		public AsyncFollow(List<NameValuePair> _params, Activity ac){
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
                       "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_callist_calendar_follow.php", "POST", params);
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
    
	class SynchronizeDatabaseToCalinfo extends AsyncTask<String, String, String> {
	
		private View layout=null;
		AlertDialog.Builder aDialog;
		private String primary = null;
		
		public SynchronizeDatabaseToCalinfo(String primary, View layout, AlertDialog.Builder aDialog){
	    	this.primary = primary;
	    	this.layout = layout;
	    	this.aDialog = aDialog;
	    	// this.url = 
	    }
	    
	    public SynchronizeDatabaseToCalinfo(){
	    	//this.url = 
	    }
	  
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        Calfeed_list = new ArrayList<Calfeed>();
	        Log.d("preexecute","yes");
	        
	    }
	    
		 private void syncCalinfoListFromWeb() throws JSONException{
			 AppPrefs appPrefs = new AppPrefs(getContext());
	        String user_id = appPrefs.getUser_id();
	        String password = appPrefs.getUser_password(); 
	        
	        JSONParser.user_id = user_id;
	        JSONParser.password = password;
	        List<NameValuePair> param = new ArrayList<NameValuePair>();	        
	        param.add(new BasicNameValuePair("user_id", user_id));
	        param.add(new BasicNameValuePair("password", password));
	        param.add(new BasicNameValuePair("primary", primary));
	        
	        String imageUrl = "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_";
	        String tmpUrl_company, tmpUrl_content;
	        
			JSONObject wallReq = JSONParser.makeHttpRequest("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_calendar_info.php","POST",param);
			Calfeed tmp = null;
			Log.d("iter size", String.valueOf(wallReq.getInt("size")));
			Log.d("iter size", String.valueOf(wallReq.getInt("primary")));
			
			for(int i=0; i<wallReq.getInt("size"); i++){
	//			String page_icon = wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				Long start = wallReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_starttime");
				Long end = wallReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_endtime");
	//			String page_id = wallReq.getJSONObject(String.valueOf(i)).getString("page_id");
				String schedule_forpage_id = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_id");
				String schedule_forpage_photo = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_photo");
				String schedule_forpage_memo = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_memo");
				int schedule_forpage_primary = wallReq.getJSONObject(String.valueOf(i)).getInt("schedule_forpage_primary");
				int follower_count = wallReq.getJSONObject(String.valueOf(i)).getInt("follower_count");
				String page_master = wallReq.getJSONObject(String.valueOf(i)).getString("page_master");
	//			int page_primary = wallReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
			//	int abletoremove = wallReq.getJSONObject(String.valueOf(i)).getInt("abletoremove");
			//	int abletoadd = wallReq.getJSONObject(String.valueOf(i)).getInt("abletoadd");
				int tmpallday = wallReq.getJSONObject(String.valueOf(i)).getInt("schedule_forpage_allday");
					
				Date date_start=new Date(start);
				Date date_end=new Date(end);

				
				
				Log.d("schedule_forpage_id",schedule_forpage_id);
				boolean is_inCalendar=false;
				//tmpUrl_company = imageUrl + page_icon;
				tmpUrl_content = imageUrl + schedule_forpage_photo;
				if(end == 0){
					end = start + 3600*1000;
				}
				
				if(wallReq.getJSONObject(String.valueOf(i)).getInt("follow") == 1){
					is_inCalendar = true;
				Log.d("isincalendar","true");
				}
				else{ 
					is_inCalendar = false;
					Log.d("isincalendar","false");
					
				}
			//	String path_company = tmpUrl_company.toString().substring(tmpUrl_company.toString().lastIndexOf('/'), tmpUrl_company.toString().length());
				String path_content = tmpUrl_content.toString().substring(tmpUrl_content.toString().lastIndexOf('/'), tmpUrl_content.toString().length());

				Log.d("path_content", path_content);
				
		//		new Image_DownloadFileAsync(activity).execute(tmpUrl_company, "1", "1");
				new Image_DownloadFileAsync(activity).execute(tmpUrl_content, "1", "1");

				//Calfeed에 follow_num과 is_incalendar 추가
				tmp = new Calfeed("",schedule_forpage_id , start, end, schedule_forpage_memo, "0", "null", path_content, follower_count ,is_inCalendar, schedule_forpage_primary, Integer.parseInt(primary));
				Log.d("pagemastervalue",page_master);
				if(page_master.compareTo("1")==0){//유저가 이 일정이 속한 페이지의 마스터라는 뜻.
					tmp.setIsMaster(true);
					Log.d("master","master");
				}else{
					tmp.setIsMaster(false);
				}

				tmp.setAllday(tmpallday);
								
				Calfeed_list.add(tmp);
			}
	    	
	    }
		 @Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				Log.d("doing","yes");
				try {
				//	Loading();
					syncCalinfoListFromWeb();
						
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
	            ListView listview = (ListView)layout.findViewById(R.id.listView3);
				listview.setAdapter(new Calfeed_forDialog_Adapter(activity ,R.layout.calendar_dialog_row, Calfeed_list));
				aDialog.setView(layout);
				AlertDialog ad = aDialog.create();
				ad.show();
	     		primary = null;
			}
			
	/*		void Loading(){		
				loadingDialog = ProgressDialog.show(activity, null, "새로고침 중입니다",true);
				Log.d("Loading Dialog","Dialog");
				
				final Thread thread = new Thread(new Runnable(){
					public void run(){
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							handler.sendEmptyMessage(0);
						}
					}
						
				});
				
				thread.start();

				
			}
			private Handler handler = new Handler(){
				public void handleMessage(Message msg){
					loadingDialog.dismiss();
					Log.d("Loading Dialog","DialogDismiss");
				}
			};
			*/
			
	}

	

}

package com.whend.android;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import com.whend.android.Following_Feed_Adapter.AsyncFollow;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Calfeed_Adapter extends ArrayAdapter<Calfeed>{
	private ArrayList<Calfeed> Callfeed_list;
	private Activity activity;
	String cachepath;
	
	public Calfeed_Adapter(Activity a, int textViewResourceId, ArrayList<Calfeed> lists){
		super(a, textViewResourceId, lists);
		this.Callfeed_list = lists;
		this.activity = a;
	}
	
	public static class ViewIds{
		public TextView startdate;
		public TextView enddate;
		public TextView company;
		public TextView item;
		public TextView place;
		public TextView hum;
		public ImageView image_company;
		public ImageView image_content;
		public TextView follow_num;
		public Button btn_remove_from_wall;
		public Button btn_incalendar;
		public String index;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		ViewIds Ids;
		
		if(v==null){
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//v = vi.inflate(R.layout.list_calfeed_row, null);
			v = vi.inflate(R.layout.custom_row, null);
			Ids = new ViewIds();
			
			Ids.image_company = (ImageView)v.findViewById(R.id.company_image);
			Ids.image_content = (ImageView)v.findViewById(R.id.feed_image);
			Ids.item = (TextView)v.findViewById(R.id.feed_item);
			Ids.company = (TextView)v.findViewById(R.id.feed_company);
			Ids.startdate = (TextView)v.findViewById(R.id.feed_startdate);
			Ids.enddate = (TextView)v.findViewById(R.id.feed_enddate);
			Ids.place = (TextView)v.findViewById(R.id.feed_place);
			Ids.follow_num = (TextView)v.findViewById(R.id.num_following);
	//		Ids.btn_remove_from_wall = (Button)v.findViewById(R.id.feed_remove_wall);
	//		Ids.btn_incalendar = (Button)v.findViewById(R.id.feed_incalendar);

			v.setTag(Ids);
		}else
			Ids = (ViewIds)v.getTag();
		
		final Calfeed il = Callfeed_list.get(position);

		
		// �� ���� �����ϴ� �κ�
		if(il != null){
			Ids.item.setText(il.getItems());
			Ids.startdate.setText(il.getDateStart());
			Ids.enddate.setText(il.getDateEnd());
			Ids.place.setText(il.getPlace());
			Ids.company.setText(il.getCompany());
			Ids.follow_num.setText("팔로잉 "+ il.getFollowNumber() + "명");
			
			
			
	/*		Ids.btn_remove_from_wall.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Context mContext = activity.getApplicationContext();
					LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
					
					final View layout = inflater.inflate(R.layout.calfeed_dialog_removefeed,(ViewGroup) activity.findViewById(R.id.layout_root));


					AlertDialog.Builder aDialog = new AlertDialog.Builder(activity);
					aDialog.setView(layout);
					
					aDialog.setPositiveButton("보지 않기", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							
							
						}
					}); // 팔로우 클릭을 했을때
					
					aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							

						}
					});
					
					AlertDialog ad = aDialog.create();
					ad.show();
					
				}
			});
			
			if(il.isIncalendar()){
				Ids.btn_incalendar.setText(R.string.incalendar);
				Ids.btn_incalendar.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
				
					Context mContext = activity.getApplicationContext();
					LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
					
					final View layout = inflater.inflate(R.layout.calfeed_dialog_remove,(ViewGroup) activity.findViewById(R.id.layout_root));


					AlertDialog.Builder aDialog = new AlertDialog.Builder(activity);
					aDialog.setView(layout);
					
					aDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							
							params.add(new BasicNameValuePair("schedule_key", String.valueOf(il.getEventPrimary())));
							params.add(new BasicNameValuePair("add_delete","0"));
			                new AsyncAddDeleteFeedfromWall(params,activity).execute();
							
							
						}
					}); // 팔로우 클릭을 했을때
					
					aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							

						}
					});
					
					AlertDialog ad = aDialog.create();
					ad.show();
					
				} }) ;
			}			
			else{
				Ids.btn_incalendar.setText(R.string.caladd);
				Ids.btn_incalendar.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {

						final Context mContext = activity.getApplicationContext();
						LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
						
						final View layout = inflater.inflate(R.layout.calfeed_dialog_add,(ViewGroup) activity.findViewById(R.id.layout_root));


						AlertDialog.Builder aDialog = new AlertDialog.Builder(activity);
						aDialog.setView(layout);
						
						aDialog.setPositiveButton("추가", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								
								ContentValues values = new ContentValues();
						        
						        values.put(Events.DTSTART, il.getStart()); 
						        values.put(Events.HAS_ALARM, 0); 
						        values.put(Events.DTEND, il.getEnd()); 
						        values.put(Events.EVENT_COLOR, Color.BLUE); 
						        values.put(Events.TITLE, il.getItems()); 
						        values.put(Events.DESCRIPTION, il.getCompany());		//메모로 바꿔야함 
						        values.put(Events.CALENDAR_ID, 98723198); //my_calendar ID
						        values.put(Events.EVENT_TIMEZONE, "UTC"); 
						    	values.put(Events._ID,1000000000-il.getEventPrimary() );		//혹시나겹칠까봐.. 그럴일은없지만.
						    	values.put(Events.ALL_DAY, 0);		// 이것도 생성자로 가져와야..
						    	
						    	mContext.getContentResolver().insert(Events.CONTENT_URI, values);
					 //           Account my_account = new Account("WhendCalendar","com.whend.demo.account.DEMOACCOUNT");						    	
					//	    	Uri creationUri = asSyncAdapter(Events.CONTENT_URI, my_account.name, my_account.type); 
					//	    	Uri calendarData = mContext.getContentResolver().insert(creationUri, values); 
						    	 
						    	List<NameValuePair> params = new ArrayList<NameValuePair>();
								
								params.add(new BasicNameValuePair("schedule_key", String.valueOf(il.getEventPrimary())));
								params.add(new BasicNameValuePair("add_delete","1"));
				                new AsyncAddDeleteFeedfromWall(params,activity).execute();
								
							}
						}); // 팔로우 클릭을 했을때
						
						aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								

							}
						});
						
						AlertDialog ad = aDialog.create();
						ad.show();
						
					} }) ;
			}*/
			

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			cachepath = activity.getCacheDir().getPath() + "/whend_icon";
//			Bitmap bm = BitmapFactory.decodeFile("/sdcard/whend"+il.getIconCompany(), options);
//			Bitmap bm2 = BitmapFactory.decodeFile("/sdcard/whend"+il.getIconContent(), options);
			Bitmap bm = BitmapFactory.decodeFile(cachepath+il.getIconCompany(), options);
			Bitmap bm2 = BitmapFactory.decodeFile(cachepath+il.getIconContent(), options);
		
//			Log.d("real","/sdcard/whend"+il.getIconCompany());
//			Log.d("real2","/sdcard/whend"+il.getIconContent());
			Log.d("real",cachepath+il.getIconCompany());
			Log.d("real2",cachepath+il.getIconContent());
			
			
			if(bm != null)
					Ids.image_company.setImageBitmap(bm);
			else
				Ids.image_company.setImageResource(R.drawable.calfeed_default_image);


		
			if(bm2 != null)
				Ids.image_content.setImageBitmap(bm2);
			else		
				Ids.image_content.setImageResource(R.drawable.calfeed_default_image);
			

		}
		
		

        
		return v;
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
    
	
	
}

package com.whend.android;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Calfeed_forDialog_Adapter extends ArrayAdapter<Calfeed>{
	private ArrayList<Calfeed> Callfeed_list;
	private Activity activity;
	String cachepath;
	public Calfeed_forDialog_Adapter(Activity a, int textViewResourceId, ArrayList<Calfeed> lists){
		super(a, textViewResourceId, lists);
		this.Callfeed_list = lists;
		this.activity = a;
	}
	
	public static class ViewIds{
		public TextView startdate;
		public TextView enddate;
		public TextView item;
		public TextView place;
		public TextView hum;
		public ImageView image_content;
		public Button schedule_change;
		public String index;
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		ViewIds Ids;
		
		if(v==null){
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calendar_dialog_row, null);
			Ids = new ViewIds();
			
			Ids.image_content = (ImageView)v.findViewById(R.id.calendar_dialog_image);
			Ids.item = (TextView)v.findViewById(R.id.calendar_dialog_item);
			Ids.startdate = (TextView)v.findViewById(R.id.calendar_dialog_startdate);
			Ids.enddate = (TextView)v.findViewById(R.id.calendar_dialog_enddate);
			Ids.place = (TextView)v.findViewById(R.id.calendar_dialog_place);
			Ids.schedule_change = (Button) v.findViewById(R.id.calendar_dialog_schedule_change);

			
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
			
			if(il.isMaster()){
				
				
              
				Ids.schedule_change.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					//	Intent intent=new Intent(activity,Calendar_Sched_Modify.class);
						Intent intent2 = new Intent(activity, Calendar_Sched_Modify.class);
						intent2.putExtra("primarykey", il.getEventPrimary());
						intent2.putExtra("Company", il.getCompany());
						intent2.putExtra("Start", il.getStart());
						intent2.putExtra("End", il.getEnd());
						intent2.putExtra("Allday", il.getAllday());
						intent2.putExtra("IconContent", il.getIconContent());
						intent2.putExtra("Title", il.getItems());
						intent2.putExtra("Memo",il.getPlace());
						
		                activity.startActivity(intent2);
					}
				});
				
			}else{
				Ids.schedule_change.setVisibility(View.GONE);
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			cachepath = activity.getCacheDir().getPath()+ "/whend_icon";

			//			Bitmap bm2 = BitmapFactory.decodeFile("/sdcard/whend"+il.getIconContent(), options);
			Bitmap bm2 = BitmapFactory.decodeFile(cachepath+il.getIconContent(), options);

			
//			Log.d("real2","/sdcard/whend"+il.getIconContent());
			Log.d("real2",cachepath+il.getIconContent());
			

		
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
	
	
	
	
}

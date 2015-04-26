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
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.whend.android.PF04_Callist.PhotoRegister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class Calendar_Sched_Add extends Activity {
	
	private int TAKE_FROM_CAMERA = 1;
	private int TAKE_FROM_GALLERY = 2;
	private int CROP_FROM_CAMERA = 3;
	private int REQ_CODE_SELECT_IMAGE = 100;
	private Uri mImageCaptureUri;
	private int pagePrimary;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar__sched__add);
		
		final ImageView schedule_image = (ImageView) findViewById(R.id.schedule_image);
		final Button schedule_image_btn = (Button) findViewById(R.id.schedule_image_btn);
		final EditText title = (EditText) findViewById(R.id.title);
		final EditText memo = (EditText) findViewById(R.id.memo);
		final CheckBox allday = (CheckBox) findViewById(R.id.allday);
		final Spinner calendar_spinner = (Spinner) findViewById(R.id.calendar_spinner);
		final TextView text_spinner = (TextView) findViewById(R.id.text_spinner);
		final TimePicker starttime = (TimePicker)findViewById(R.id.starttime);
		final TimePicker endtime = (TimePicker)findViewById(R.id.endtime);
		final DatePicker startdate = (DatePicker)findViewById(R.id.startdate);
		final DatePicker enddate = (DatePicker) findViewById(R.id.enddate);
		final TextView text_starttime = (TextView) findViewById(R.id.text_starttime);
		final TextView text_endtime = (TextView) findViewById(R.id.text_endtime);
		final Button cancel = (Button) findViewById(R.id.cancel);
		final Button submit = (Button) findViewById(R.id.submit);
		
		

		Bundle extras = getIntent().getExtras();
		pagePrimary = extras.getInt("primarykey");
		
		final int tmppagePrimary = pagePrimary;
		

		allday.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(
					CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				
				if(isChecked){
					text_starttime.setVisibility(text_starttime.GONE);
					text_endtime.setVisibility(text_endtime.GONE);
					starttime.setVisibility(starttime.GONE);
					endtime.setVisibility(endtime.GONE);
				}
				else{
					text_starttime.setVisibility(text_starttime.VISIBLE);
					text_endtime.setVisibility(text_endtime.VISIBLE);
					starttime.setVisibility(starttime.VISIBLE);
					endtime.setVisibility(endtime.VISIBLE);
				}
				
			}
										
			
		});
		
		submit.setOnClickListener(new View.OnClickListener(){

			
			int startYear;
			int startMonth;
			int endYear;
			int endMonth;
			int startHour;
			int endHour;
			int startMinute;
			int endMinute;
			int endDay;
			int startDay;
			String schedule_title;
			String schedule_memo;
			boolean schedule_allday;
			long starttime_milli;
			long endtime_milli;
			
			@Override
			public void onClick(View v) {
				schedule_allday = allday.isChecked();
				if(schedule_allday){	//종일일정이므로 시간 필요없음. 날짜만필요
					startYear = startdate.getYear();
					startMonth = startdate.getMonth();
					startDay = startdate.getDayOfMonth();
					endYear = enddate.getYear();
					endMonth = enddate.getMonth();
					endDay = enddate.getDayOfMonth();
					Calendar cstart = Calendar.getInstance();
					cstart.set(startYear, startMonth, startDay,0,0,0);		//이부분은 중요
					starttime_milli = (cstart.getTimeInMillis()/1000)*1000;
					Calendar cend = Calendar.getInstance();
					cend.set(endYear, endMonth, endDay,0,0,0);				//이부분은 중요
					endtime_milli = (cend.getTimeInMillis()/1000)*1000;
																
				}
				else{
					startYear = startdate.getYear();
					startMonth = startdate.getMonth();
					startDay = startdate.getDayOfMonth();
					startHour = starttime.getCurrentHour();
					startMinute = starttime.getCurrentMinute();
					endYear = enddate.getYear();
					endMonth = enddate.getMonth();
					endDay = enddate.getDayOfMonth();
					endHour = endtime.getCurrentHour();
					endMinute = endtime.getCurrentMinute();
															 												
					Calendar cstart = Calendar.getInstance();
					cstart.set(startYear, startMonth, startDay, startHour, startMinute,0);
					starttime_milli = (cstart.getTimeInMillis()/1000)*1000;
					Calendar cend = Calendar.getInstance();
					cend.set(endYear, endMonth, endDay, endHour, endMinute,0);
					endtime_milli = (cend.getTimeInMillis())/1000*1000;
				}
				
				schedule_title = title.getText().toString();
				schedule_memo = memo.getText().toString();
				
				AppPrefs appPrefs = new AppPrefs(v.getContext());
			    String user_id = appPrefs.getUser_id();
			    String password = appPrefs.getUser_password(); 
				List<NameValuePair> param = new ArrayList<NameValuePair>();	        
		        param.add(new BasicNameValuePair("user_id", user_id));
		        param.add(new BasicNameValuePair("password", password));
		        try {
					param.add(new BasicNameValuePair("Event_name", URLEncoder.encode(schedule_title, "UTF-8")));
					param.add(new BasicNameValuePair("text", URLEncoder.encode(schedule_memo, "UTF-8")));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}								        
		        Log.d("tmpcalfeed_primary",String.valueOf(tmppagePrimary));
		        Log.d("calfeed_primary",String.valueOf(pagePrimary));
		        
		        param.add(new BasicNameValuePair("calfeed_primary", String.valueOf(tmppagePrimary)));
		        param.add(new BasicNameValuePair("check", String.valueOf(schedule_allday)));
		        param.add(new BasicNameValuePair("timepicker1", String.valueOf(starttime_milli)));
		        param.add(new BasicNameValuePair("timepicker2", String.valueOf(endtime_milli)));
		        param.add(new BasicNameValuePair("picker1", String.valueOf(starttime_milli)));
		        param.add(new BasicNameValuePair("picker2", String.valueOf(endtime_milli)));
		        appPrefs.setPrimary(0);
		        new PhotoRegister(param, "").execute();
		        finish();
		        
			}			
			
		
			
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		schedule_image_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_PICK);
				
				intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
				intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);

				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calendar__sched__add, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	   public void onActivityResult(int requestCode, int resultCode, Intent data){
			super.onActivityResult(requestCode, resultCode, data);
			
			if(resultCode != RESULT_OK)
				return;
			
			final ImageView schedule_image = (ImageView) findViewById(R.id.schedule_image);
			final Button schedule_image_btn = (Button) findViewById(R.id.schedule_image_btn);
			final EditText title = (EditText) findViewById(R.id.title);
			final EditText memo = (EditText) findViewById(R.id.memo);
			final CheckBox allday = (CheckBox) findViewById(R.id.allday);
			final Spinner calendar_spinner = (Spinner) findViewById(R.id.calendar_spinner);
			final TextView text_spinner = (TextView) findViewById(R.id.text_spinner);
			final TimePicker starttime = (TimePicker)findViewById(R.id.starttime);
			final TimePicker endtime = (TimePicker)findViewById(R.id.endtime);
			final DatePicker startdate = (DatePicker)findViewById(R.id.startdate);
			final DatePicker enddate = (DatePicker) findViewById(R.id.enddate);
			final TextView text_starttime = (TextView) findViewById(R.id.text_starttime);
			final TextView text_endtime = (TextView) findViewById(R.id.text_endtime);
			final Button cancel = (Button) findViewById(R.id.cancel);
			final Button submit = (Button) findViewById(R.id.submit);
			
			
			
			
			

			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});


			
			
			schedule_image_btn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_PICK);
					
					intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
					intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
				Bitmap bm = Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
				
				Cursor c = getContentResolver().query(Uri.parse(mImageCaptureUri.toString()),null,null,null,null);
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
					schedule_image.setImageBitmap(resized);
				else
					schedule_image.setImageBitmap(bm);
				
				submit.setOnClickListener(new View.OnClickListener(){
					int startYear;
					int startMonth;
					int endYear;
					int endMonth;
					int startHour;
					int endHour;
					int startMinute;
					int endMinute;
					int endDay;
					int startDay;
					String schedule_title;
					String schedule_memo;
					boolean schedule_allday;
					long starttime_milli;
					long endtime_milli;
					@Override
					public void onClick(View v) {
		               							
						schedule_allday = allday.isChecked();
						if(schedule_allday){	//종일일정이므로 시간 필요없음. 날짜만필요
							startYear = startdate.getYear();
							startMonth = startdate.getMonth();
							startDay = startdate.getDayOfMonth();
							endYear = enddate.getYear();
							endMonth = enddate.getMonth();
							endDay = enddate.getDayOfMonth();
							Calendar cstart = Calendar.getInstance();
							cstart.set(startYear, startMonth, startDay,0,0,0);		//이부분은 중요
							starttime_milli = (cstart.getTimeInMillis()/1000)*1000;
							Calendar cend = Calendar.getInstance();
							cend.set(endYear, endMonth, endDay,0,0,0);				//이부분은 중요
							endtime_milli = (cend.getTimeInMillis()/1000)*1000;
																		
						}
						else{
							startYear = startdate.getYear();
							startMonth = startdate.getMonth();
							startDay = startdate.getDayOfMonth();
							startHour = starttime.getCurrentHour();
							startMinute = starttime.getCurrentMinute();
							endYear = enddate.getYear();
							endMonth = enddate.getMonth();
							endDay = enddate.getDayOfMonth();
							endHour = endtime.getCurrentHour();
							endMinute = endtime.getCurrentMinute();
																	 												
							Calendar cstart = Calendar.getInstance();
							cstart.set(startYear, startMonth, startDay, startHour, startMinute,0);
							starttime_milli = (cstart.getTimeInMillis()/1000)*1000;
							Calendar cend = Calendar.getInstance();
							cend.set(endYear, endMonth, endDay, endHour, endMinute,0);
							endtime_milli = (cend.getTimeInMillis()/1000)*1000;
						}
						
						schedule_title = title.getText().toString();
						schedule_memo = memo.getText().toString();
						
						AppPrefs appPrefs = new AppPrefs(v.getContext());
					    String user_id = appPrefs.getUser_id();
					    String password = appPrefs.getUser_password(); 
						List<NameValuePair> param = new ArrayList<NameValuePair>();	        
				        param.add(new BasicNameValuePair("user_id", user_id));
				        param.add(new BasicNameValuePair("password", password));
				        try {
							param.add(new BasicNameValuePair("Event_name", URLEncoder.encode(schedule_title, "UTF-8")));
							param.add(new BasicNameValuePair("text", URLEncoder.encode(schedule_memo, "UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}								        
				        param.add(new BasicNameValuePair("calfeed_primary", String.valueOf(pagePrimary)));
				        param.add(new BasicNameValuePair("check", String.valueOf(schedule_allday)));
				        param.add(new BasicNameValuePair("timepicker1", String.valueOf(starttime_milli)));
				        param.add(new BasicNameValuePair("timepicker2", String.valueOf(endtime_milli)));
				        param.add(new BasicNameValuePair("picker1", String.valueOf(starttime_milli)));
				        param.add(new BasicNameValuePair("picker2", String.valueOf(endtime_milli)));
				      	        
				        
				       
						new PhotoRegister(param, absolutePath).execute();
						
						
						finish();
		                
						//String img_name = ???;
						
					}
				}); 

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				URL url = new URL("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_calfeed_create.php");
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
}



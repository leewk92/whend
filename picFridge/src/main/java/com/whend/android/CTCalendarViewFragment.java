package com.whend.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


// 원본 : http://funpython.com/blog/59?category=2
// 수정 : 한수댁
public class CTCalendarViewFragment extends Fragment implements OnClickListener, OnItemSelectedListener {
    static public int HaveToSync = 1;  
    private Calendar rightNow;
    private GregorianCalendar gCal;
    private int iYear = 0;
    private int iMonth = 0;  
 
    private int startDayOfweek = 0;
    private int maxDay = 0;
    private int oneday_width =0;
    private int oneday_height =0;
    
    static final public ArrayList<String> calstringarr = new ArrayList<String>();
    static final public ArrayList<Integer> calprimaryarr = new ArrayList<Integer>();
    int calprimary_add;
    ArrayList<String> daylist; //일자 목록을 가지고 있는다. 1,2,3,4,.... 28?30?31? 
    ArrayList<String> actlist; //일자에 해당하는 활동내용을 가지고 있는다
 
    TextView aDateTxt;
    View rootview;
    private int dayCnt;
    private int mSelect = -1;
    static public JSONObject caljson = new JSONObject();
    
    public static CTCalendarViewFragment newInstance(){
    	CTCalendarViewFragment fragment = new CTCalendarViewFragment();
    	return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	    Tracker t = ((ApplicationController)getActivity().getApplication()).getTracker(ApplicationController.TrackerName.APP_TRACKER);
	    t.setScreenName("on CTCalendarViewFragment");
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
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState){
    	rootview= inflater.inflate(R.layout.calendarview, parent,false);
    	try {
			initialize();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return rootview;
    }
    
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        if(HaveToSync == 1){
        	//new SynchronizeInnerCalToDatabase().execute();			
        	
        	new SynchronizeDatabaseToCalendar().execute();
       /* 	try {
				initialize();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        }super.onActivityCreated(savedInstanceState);
    }
    /*
        
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		   //inflater.inflate(R.menu.pf03__itemlog, menu);
		    ((PF01_Main)getActivity()).setActionBarTitle(" Calendar");
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.refresh :
	    		
                //refresh_list(rootView, db);
                
                Toast toast = Toast.makeText(getActivity(), "Item Refreshed", Toast.LENGTH_SHORT); 
                toast.show(); 
                
                break;
                
	    }
	    return true;
    }
    
    
    protected void initialize() throws JSONException{
    	//getActivity().setContentView(R.layout.calendarview);
        syncDeviceCalendar(); 
        rightNow = Calendar.getInstance();
        gCal = new GregorianCalendar();
        iYear = rightNow.get(Calendar.YEAR);
        iMonth = rightNow.get(Calendar.MONTH);
         
        Button btnMPrev = (Button)rootview.findViewById(R.id.btn_calendar_prevmonth);
        btnMPrev.setOnClickListener(this);
        Button btnMNext = (Button)rootview.findViewById(R.id.btn_calendar_nextmonth);
        btnMNext.setOnClickListener(this);
 
        //btnMPrev.setText("이전");
        //btnMNext.setText("다음");
 
        aDateTxt = (TextView)rootview.findViewById(R.id.CalendarMonthTxt);
 
        makeCalendardata(iYear, iMonth);
    }
 
 
  //달력의 일자를 표시한다. 
    private void printDate(String thisYear, String thisMonth)
    {
 
        if(thisMonth.length() == 1) {
            aDateTxt.setText(String.valueOf(thisYear) + "." + "0"+ thisMonth);
        }
        else{
            aDateTxt.setText(String.valueOf(thisYear) + "." + thisMonth);
        }
    }
 
  //달력에 표시할 일자를 배열에 넣어 구성한다. 
    private void makeCalendardata(int thisYear, int thisMonth)
    {
        printDate(String.valueOf(thisYear),String.valueOf(thisMonth+1));
         
        rightNow.set(thisYear, thisMonth, 1);
        gCal.set(thisYear, thisMonth, 1);
        startDayOfweek = rightNow.get(Calendar.DAY_OF_WEEK);
 
 
        maxDay = gCal.getActualMaximum ((Calendar.DAY_OF_MONTH));
        if(daylist==null)daylist = new ArrayList<String>();
        daylist.clear();
 
        if(actlist==null)actlist = new ArrayList<String>();
        actlist.clear();
 
        daylist.add("일");actlist.add("");
        daylist.add("월");actlist.add("");
        daylist.add("화");actlist.add("");
        daylist.add("수");actlist.add("");
        daylist.add("목");actlist.add("");
        daylist.add("금");actlist.add("");
        daylist.add("토");actlist.add("");
 
        if(startDayOfweek != 1) {
            gCal.set(thisYear, thisMonth-1, 1);
            int prevMonthMaximumDay = (gCal.getActualMaximum((Calendar.DAY_OF_MONTH))+2);
            for(int i=startDayOfweek;i>1;i--){
                daylist.add(Integer.toString(prevMonthMaximumDay-i));
                actlist.add("p");
            }
        }
 
        for(int i=1;i<=maxDay;i++) //일자를 넣는다.
        {
            daylist.add(Integer.toString(i));
            actlist.add("");
        }
 
 
        int dayDummy = (startDayOfweek-1)+maxDay;
        if(dayDummy >35)
        {
            dayDummy = 42 - dayDummy;
        }else{
            dayDummy = 35 - dayDummy;
        }
        
      //자투리..그러니까 빈칸을 넣어 달력 모양을 이쁘게 만들어 준다.
        if(dayDummy != 0)
        {
            for(int i=1;i<=dayDummy;i++) 
            {
                daylist.add(Integer.toString(i));
                actlist.add("n");
            }
        }
 
        makeCalendar();
    }
 
 
 
    private void makeCalendar()
    {
        final Oneday[] oneday = new Oneday[daylist.size()];
        final Calendar today = Calendar.getInstance();
        TableLayout tl =(TableLayout)rootview.findViewById(R.id.tl_calendar_monthly);
        tl.removeAllViews();
 
        dayCnt = 0;
        int maxRow = ((daylist.size() > 42)? 7:6);
        int maxColumn = 7;
        
 
        oneday_width = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
        oneday_height =getActivity().getWindow().getWindowManager().getDefaultDisplay().getHeight();
 
        oneday_height = ((((oneday_height >= oneday_width)?oneday_height:oneday_width) - tl.getTop()) / (maxRow+1)+10);	//revised, original : -10이 뒤에 붙어있었음.
        oneday_width = (oneday_width / maxColumn)+1;
        Log.d("oneday_height", String.valueOf(oneday_height));
        Log.d("oneday_width", String.valueOf(oneday_width));
        
 
        int daylistsize =daylist.size()-1;
//        syncDeviceCalendar();
        for(int i=1;i<=maxRow;i++ )
        {
            TableRow tr = new TableRow(getActivity());
            tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            for(int j=1;j<=maxColumn;j++)
            {
                //calender_oneday를 생성해 내용을 넣는다. 
                oneday[dayCnt] = new Oneday(getActivity().getApplicationContext());
 
                //요일별 색상 정하기 
                if((dayCnt % 7) == 0){
                    oneday[dayCnt].setTextDayColor(Color.RED);
                } else if((dayCnt % 7) == 6){
                    oneday[dayCnt].setTextDayColor(Color.GRAY);
                } else {
                    oneday[dayCnt].setTextDayColor(Color.BLACK);
                }
                 
                //요일 표시줄 설정
                if(dayCnt >= 0 && dayCnt < 7)
                {
                    oneday[dayCnt].setBgDayPaint(Color.WHITE); //배경색상 
                    oneday[dayCnt].setTextDayTopPadding(oneday_height/8); //일자표시 할때 top padding: original 8
                    oneday[dayCnt].setTextDayLeftPadding(oneday_width*7/8);
                    oneday[dayCnt].setTextDayColor(Color.DKGRAY); //일자의 글씨 색상 
                    oneday[dayCnt].setTextDaySize(oneday_height/8); //일자의 글씨크기 
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,2)); //일자 컨트롤 크기 
                    oneday[dayCnt].isToday = false;
                     
                }else{
                     
                    oneday[dayCnt].isToday = false;
                    oneday[dayCnt].setDayOfWeek(dayCnt%7 + 1);
                    oneday[dayCnt].setDay(Integer.valueOf(daylist.get(dayCnt)).intValue());
                    oneday[dayCnt].setTextActcntSize(oneday_height/8);		//original : 14
                    oneday[dayCnt].setTextActcntColor(Color.DKGRAY);
                    oneday[dayCnt].setTextActcntTopPadding(oneday_height/8);			//original : 18, -oneday_height/2+oneday_height/8*3
                    oneday[dayCnt].setBgSelectedDayPaint(Color.rgb(37, 204, 204));
                    oneday[dayCnt].setBgTodayPaint(Color.LTGRAY);
                    oneday[dayCnt].setBgActcntPaint(Color.rgb(251, 247, 210));
                    oneday[dayCnt].setLayoutParams(new LayoutParams(oneday_width,oneday_height));
                     
                    //이전 달 블럭 표시
                    if(actlist.get(dayCnt).equals("p")){
                         oneday[dayCnt].setTextDaySize(oneday_height/10);
                         actlist.set(dayCnt, "");
                         oneday[dayCnt].setTextDayTopPadding(-4);
                          
                         if(iMonth - 1 < Calendar.JANUARY){
                             oneday[dayCnt].setMonth(Calendar.DECEMBER);
                             oneday[dayCnt].setYear(iYear - 1);
                         }  else {
                             oneday[dayCnt].setMonth(iMonth - 1);
                             oneday[dayCnt].setYear(iYear);
                         }
                     
                    // 다음 달 블럭 표시
                    } else if(actlist.get(dayCnt).equals("n")){
                        oneday[dayCnt].setTextDaySize(oneday_height/10);
                        actlist.set(dayCnt, "");
                        oneday[dayCnt].setTextDayTopPadding(-4);
                        if(iMonth + 1 > Calendar.DECEMBER){
                            oneday[dayCnt].setMonth(Calendar.JANUARY);
                            oneday[dayCnt].setYear(iYear + 1);
                        }  else {
                            oneday[dayCnt].setMonth(iMonth + 1);
                            oneday[dayCnt].setYear(iYear);
                        }
                    // 현재 달 블력 표시
                    }else{
                        oneday[dayCnt].setTextDaySize(oneday_height/8);
                        oneday[dayCnt].setYear(iYear);
                        oneday[dayCnt].setMonth(iMonth);
                        
                        //oneday[dayCnt].setBgActcntPaint(Color.YELLOW);
                        //오늘 표시
                        if(oneday[dayCnt].getDay() == today.get(Calendar.DAY_OF_MONTH)
                                && oneday[dayCnt].getMonth() == today.get(Calendar.MONTH)
                                && oneday[dayCnt].getYear() == today.get(Calendar.YEAR)){
                             
                            oneday[dayCnt].isToday = true;
                          //  actlist.set(dayCnt,"오늘");
                            oneday[dayCnt].invalidate();
                            mSelect = dayCnt;
                        }
                        
              
	                    for(int key=0; key<mDay.size(); key++){
                        	if(oneday[dayCnt].getDay() == mDay.get(key)
	                        		&&oneday[dayCnt].getMonth() == mMonth.get(key)
	                        		&&oneday[dayCnt].getYear() == mYear.get(key)){
	                        	
	                        	oneday[dayCnt].textActCnts.add(title.get(key).toString());
	                        	oneday[dayCnt].bgActcntPaints.add(new Paint(Color.YELLOW));
	          //              	Log.d("include",title.get(key).toString());  
	                        	
	                        }
	                    }
                    }
                     
 
                    oneday[dayCnt].setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            //Toast.makeText(context, iYear+"-"+iMonth+"-"+oneday[v.getId()].getTextDay(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
 
                    oneday[dayCnt].setOnTouchListener(new OnTouchListener() {
 
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                             
                            if(oneday[v.getId()].getTextDay() != "" && event.getAction() == MotionEvent.ACTION_UP)
                            {
                                if(mSelect != -1){
                                    oneday[mSelect].setSelected(false);
                                    oneday[mSelect].invalidate();
                                }
                                oneday[v.getId()].setSelected(true);
                                oneday[v.getId()].invalidate();
                                mSelect = v.getId();
                                 
                                //Log.d("hahaha", oneday[mSelect].getMonth()+"-"+ oneday[mSelect].getDay());
                                 
                                onTouched(oneday[mSelect]);
                            }
                            return false;
                        }                        
                    });
                }
                
                oneday[dayCnt].setTextDayTopPadding(-oneday_height/2+oneday_height/8); 		//revised Original: 없었음
                oneday[dayCnt].setTextDayLeftPadding(-oneday_width/2+oneday_width/8);		//revised Original: 없었음
                oneday[dayCnt].setTextDay(daylist.get(dayCnt).toString()); //요일,일자 넣기 
                oneday[dayCnt].setTextActCnt(actlist.get(dayCnt).toString());//활동내용 넣기 
                oneday[dayCnt].setId(dayCnt); //생성한 객체를 구별할수 있는 id넣기 
                oneday[dayCnt].invalidate();
                tr.addView(oneday[dayCnt]);
 
                if(daylistsize != dayCnt)
                {
                    dayCnt++;
                }else{
                    break;
                }
            }
            tl.addView(tr,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
 
        }
    }
     
    
    
    
    public static final String[] CALENDAR_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	    Calendars.OWNER_ACCOUNT,                  // 3
	    Calendars.NAME
	};
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	public static final String[] EVENT_PROJECTION =  new String[]{ 
		Events._ID,  
        Events.DTSTART,  
        Events.DTEND,  
        Events.RRULE,  
        Events.TITLE,
        Events.DESCRIPTION,
        Events.EVENT_TIMEZONE,
        Events.EVENT_END_TIMEZONE,
        Events.DURATION,
        Events.ALL_DAY,
        Events.RDATE,
        Events.AVAILABILITY
    }; 
	//for calendars
	List<Long> calID = new ArrayList<Long>();
	List<Object> displayName = new ArrayList<Object>();
	List<Object> accountName = new ArrayList<Object>();
	List<Object> ownerName = new ArrayList<Object>();
	//for events
	List<Object> eventID = new ArrayList<Object>();
	List<Object> dtstart = new ArrayList<Object>();
	List<Object> dtend = new ArrayList<Object>();
	List<Object> rrule = new ArrayList<Object>();
	List<Object> title = new ArrayList<Object>();
	List<Integer> mYear = new ArrayList<Integer>();
	List<Integer> mMonth = new ArrayList<Integer>();
	List<Integer> mDay = new ArrayList<Integer>();
	
	
    private void syncDeviceCalendar() throws JSONException{
    	
    	calID.clear();
    	displayName.clear();
    	accountName.clear();
    	ownerName.clear();
    	eventID.clear();
    	dtstart.clear();
    	dtend.clear();
    	rrule.clear();
    	title.clear();
    	mYear.clear();
    	mMonth.clear();
    	mDay.clear();
    	
    	
    	
    	Cursor cur = null;
    	ContentResolver cr = getActivity().getContentResolver();
    	Uri uri = Calendars.CONTENT_URI;   
    //	String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
    //	                        + Calendars.ACCOUNT_TYPE + " = ?))";
    //	String[] selectionArgs = new String[]{"sampleuser@gmail.com", "com.google"}; 
    	// Submit the query and get a Cursor object back. 
   // 	cur = cr.query(uri, CALENDAR_PROJECTION, null, null, null);
    	cur = cr.query(uri, CALENDAR_PROJECTION, Calendars.VISIBLE + " = 1", null, null);
    	// Use the cursor to step through the returned records
    	
    	
    	
    	while (cur.moveToNext()) {
    	    	      
    	    // Get the field values
    	    calID.add(cur.getLong(PROJECTION_ID_INDEX));		
    	    displayName.add(cur.getString(PROJECTION_DISPLAY_NAME_INDEX));	
    	    accountName.add(cur.getString(PROJECTION_ACCOUNT_NAME_INDEX));
    	    ownerName.add(cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX));
    	    
    //	    Log.d("calid",String.valueOf(cur.getLong(PROJECTION_ID_INDEX)));
    	    
    	    
    	   
    	    Uri uri2 = Events.CONTENT_URI;
    	    ContentResolver cr2 = getActivity().getContentResolver();
    	    Cursor cur2 = cr2.query(uri2, EVENT_PROJECTION, Events.CALENDAR_ID+" ="+cur.getLong(PROJECTION_ID_INDEX),null, null);
    	   
    	    
        	JSONObject evtpropjson = null;
        	Calendar calendar = null;
        	while (cur2.moveToNext()) {
      	        evtpropjson = new JSONObject();
        	    // Get the field values
        		eventID.add(cur2.getLong(0));
        		dtstart.add(cur2.getLong(1));
        		dtend.add(cur2.getLong(2));
        		rrule.add(cur2.getString(3));
        		title.add(cur2.getString(4));
       // 	   	Log.d("title",cur2.getString(4));        	   
        		calendar = Calendar.getInstance();
        	    calendar.setTimeInMillis(cur2.getLong(1));
        	    Calendar endCal = Calendar.getInstance();
        	    endCal.setTimeInMillis(cur2.getLong(2));
        	    
        	   
        	    
        	    mYear.add(calendar.get(Calendar.YEAR));
        	    mMonth.add(calendar.get(Calendar.MONTH));		
        	    mDay.add(calendar.get(Calendar.DAY_OF_MONTH));
        //	    Log.d("mYear",String.valueOf(calendar.get(Calendar.YEAR)));	
        //	    Log.d("mMonth",String.valueOf(calendar.get(Calendar.MONTH)));	
        //	    Log.d("mDay",String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        	    
        	}
        	
    	}
    	
    }
     
/*
	class SynchronizeInnerCalToDatabase extends AsyncTask<String, String, String> {
		

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            caljson = new JSONObject();
            try {
            	Log.d("preexecute","yes");
            	
				this.syncDeviceCalendar();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        }
        
		 private void syncDeviceCalendar() throws JSONException{
		    	
			
	    	calID.clear();
	    	displayName.clear();
	    	accountName.clear();
	    	ownerName.clear();
	    	eventID.clear();
	    	dtstart.clear();
	    	dtend.clear();
	    	rrule.clear();
	    	title.clear();
	    	mYear.clear();
	    	mMonth.clear();
	    	mDay.clear();
	   
	    	
	    	Cursor cur = null;
	    	ContentResolver cr = getActivity().getContentResolver();
	    	Uri uri = Calendars.CONTENT_URI; 
	    //	String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
	    //	                        + Calendars.ACCOUNT_TYPE + " = ?))";
	    //	String[] selectionArgs = new String[]{"sampleuser@gmail.com", "com.google"}; 
	    	// Submit the query and get a Cursor object back. 
	    	cur = cr.query(uri, CALENDAR_PROJECTION, Calendars.VISIBLE + " = 1", null, null);
	    	// Use the cursor to step through the returned records
	    	
	    	JSONObject calpropjson = null;
	    	
	    	while (cur.moveToNext()) {
	    	    calpropjson = new JSONObject();  	      
	    	    // Get the field values
	    	    calID.add(cur.getLong(PROJECTION_ID_INDEX));		
	    	    displayName.add(cur.getString(PROJECTION_DISPLAY_NAME_INDEX));	
	    	    accountName.add(cur.getString(PROJECTION_ACCOUNT_NAME_INDEX));
	    	    ownerName.add(cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX));
	    	    
	    	
	    	
				calpropjson.put("CALENDAR_DISPLAY_NAME",cur.getString(PROJECTION_DISPLAY_NAME_INDEX));
				calpropjson.put("ACCOUNT_NAME",cur.getString(PROJECTION_ACCOUNT_NAME_INDEX));
				calpropjson.put("OWNER_ACCOUNT",cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX));
				calpropjson.put("CALENDAR_ID",cur.getString(PROJECTION_ID_INDEX));
			
				
				
	    	    
	    	   
	    	    Uri uri2 = Events.CONTENT_URI;
	    	    ContentResolver cr2 = getActivity().getContentResolver();
	    	    Cursor cur2 = cr2.query(uri2, EVENT_PROJECTION, Events.CALENDAR_ID+" ="+cur.getLong(PROJECTION_ID_INDEX),null, null);
	        	JSONObject evtpropjson = null;
	        	Calendar calendar = null;
	        	while (cur2.moveToNext()) {
	      	        evtpropjson = new JSONObject();
	        	    // Get the field values
	        		eventID.add(cur2.getLong(0));
	        		dtstart.add(cur2.getLong(1));
	        		dtend.add(cur2.getLong(2));
	        		rrule.add(cur2.getString(3));
	        		title.add(cur2.getString(4));
	       // 	   	Log.d("title",cur2.getString(4));        	   
	        		calendar = Calendar.getInstance();
	        	    calendar.setTimeInMillis(cur2.getLong(1));
	        	    
	        	    mYear.add(calendar.get(Calendar.YEAR));
	        	    mMonth.add(calendar.get(Calendar.MONTH));		
	        	    mDay.add(calendar.get(Calendar.DAY_OF_MONTH));
	        //	    Log.d("mYear",String.valueOf(calendar.get(Calendar.YEAR)));	
	        //	    Log.d("mMonth",String.valueOf(calendar.get(Calendar.MONTH)));	
	        //	    Log.d("mDay",String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
	        	    
	        	    evtpropjson.put("_ID", String.valueOf(cur2.getLong(0)));
					evtpropjson.put("DTSTART", String.valueOf(cur2.getLong(1)));
					evtpropjson.put("DTEND", String.valueOf(cur2.getLong(2)));
		        	evtpropjson.put("RRULE", cur2.getString(3));
		        	evtpropjson.put("TITLE", cur2.getString(4));
		        	evtpropjson.put("DESCRIPTION", cur2.getString(5));
		        	evtpropjson.put("EVENT_TIMEZONE", cur2.getString(6));
		        	evtpropjson.put("EVENT_END_TIMEZONE", cur2.getString(7));
		        	evtpropjson.put("DURATION", cur2.getString(8));
		        	evtpropjson.put("ALL_DAY",String.valueOf(cur2.getInt(9)));
		        	evtpropjson.put("RDATE", cur2.getString(10));
		        	evtpropjson.put("AVAILABILITY", String.valueOf(cur2.getInt(11)));
		        	    
					
	        	    calpropjson.accumulate("events",evtpropjson);
	        	}
	        	caljson.accumulate("calendar",calpropjson);
	    	}
	    	
	    }
	
				
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d("doing","yes");
			
			JSONObject test = new JSONObject();
			try {
				test.put("ho", "hi");
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			//			
	    	 JSONObject calReq;
	    	 Log.d("caljson",caljson.toString());
			try {
				
				AppPrefs appPrefs = new AppPrefs(getActivity());
		        String user_id = appPrefs.getUser_id();
		        String password = appPrefs.getUser_password(); 
		        JSONParser.user_id = user_id;
		        JSONParser.password = password;
				calReq = JSONParser.sendJSONtoURL("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/redirect_forandroid.php",caljson);
				Log.d("calReq",calReq.getString("success"));
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
	            makeCalendar();
				HaveToSync = 1;
	    }
		
		
		
		
		
	}*/
    
    /**
     * 숫자를 2자리 문자로 변환, 2 -> 02
     * @param value
     * @return
     */
    protected String doubleString(int value)
    {
        String temp;
 
        if(value < 10){
            temp = "0"+ String.valueOf(value);
             
        }else {
            temp = String.valueOf(value);
        }
        return temp;
    }
 
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
        case R.id.btn_calendar_nextmonth:
            if(iMonth == 11)
            {
                iYear = iYear + 1;
                iMonth = 0;
            }
            else
            {
                iMonth = iMonth + 1;
            }
            makeCalendardata(iYear,iMonth);
            break;
        case R.id.btn_calendar_prevmonth:
            if(iMonth == 0)
            {
                iYear = iYear - 1;
                iMonth = 11;
            }else{
                iMonth = iMonth - 1;
            }
            makeCalendardata(iYear,iMonth);
            break;
        }
    }
     
    //For spinner defined below.
    @Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
    	calprimary_add = calprimaryarr.get(position); 
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}
	
    /**
     * 서브 클래스에서 오버라이드 해서 터치한 날짜 입력 받기
     * @param oneday
     */
    protected void onTouched(Oneday oneday){
        /* 
    	Log.d("touched","t");
    	Context mContext = getActivity().getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
		
		final View layout = inflater.inflate(R.layout.schedule_make_dialog,(ViewGroup) getActivity().findViewById(R.id.layout_root));
		
//		ImageView new_calendar_image = (ImageView) layout.findViewById(R.id.mycalendar_image);
		
		final EditText title = (EditText) layout.findViewById(R.id.title);
		final EditText memo = (EditText) layout.findViewById(R.id.memo);
		final CheckBox allday = (CheckBox) layout.findViewById(R.id.allday);
		final Spinner calendar_spinner = (Spinner) layout.findViewById(R.id.calendar_spinner);
		final TimePicker starttime = (TimePicker)layout.findViewById(R.id.starttime);
		final TimePicker endtime = (TimePicker)layout.findViewById(R.id.endtime);
		final int year = oneday.getYear();
		final int month = oneday.getMonth();
		final int day = oneday.getDay();
		final int startHour;
		final int startMinute;
		final int endHour;
		final int endMinute;
		
		starttime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute){
				startHour = hourOfDay;		
				startMinute = minute;
			}
		});
		endtime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute){
				endHour = hourOfDay;		
				endMinute = minute;
			}
		});
		
		calendar_spinner.setOnItemSelectedListener(this);
			
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, calstringarr);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		calendar_spinner.setAdapter(adapter);
		
		
		AlertDialog.Builder aDialog = new AlertDialog.Builder(getActivity());
		
		aDialog.setView(layout);
		
		aDialog.setPositiveButton("일정 추가", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				Calendar cstart = Calendar.getInstance();
				cstart.set(year, month, day, startHour, startMinute);
				Calendar cend = Calendar.getInstance();
				cend.set(year, month, day, endHour, endMinute);
				
				List<NameValuePair> param = new ArrayList<NameValuePair>();	        
		        param.add(new BasicNameValuePair("user_id", user_id));
		        param.add(new BasicNameValuePair("password", password));
		        
				title.getText().toString()
				memo.getText().toString()
				allday.isChecked()
								
				cstart.getTimeInMillis()
				cend.getTimeInMillis()
			}
		}); 
		
		aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		final AlertDialog ad = aDialog.create();
    	ad.show();*/
    	
    }
     
    /**
     * 해당 일이 기준일 범위 안에 있는지 검사
     * @param test 검사할 날짜
     * @param basis 기준 날짜
     * @param during 기간(일)
     * @return
     */
    protected boolean isInside(Oneday test, Oneday basis, int during){
        Calendar calbasis = Calendar.getInstance();
        calbasis.set(basis.getYear(), basis.getMonth(), basis.getDay());
        calbasis.add(Calendar.DAY_OF_MONTH, during);
         
        Calendar caltest = Calendar.getInstance();
        caltest.set(test.getYear(), test.getMonth(), test.getDay());
         
        if(caltest.getTimeInMillis() < calbasis.getTimeInMillis()){
            return true;
        }
        return false;
    }
     
    /**
     *오늘 달력으로 이동 
     */
    public void gotoToday(){
        final Calendar today = Calendar.getInstance();
        iYear = today.get(Calendar.YEAR);
        iMonth = today.get(Calendar.MONTH);
        makeCalendardata(today.get(Calendar.YEAR),today.get(Calendar.MONTH));
    }
    
    
    
    
    
    
    
    
    class SynchronizeDatabaseToCalendar extends AsyncTask<String, String, String> {
		Uri synccr;
		Account my_account;
        @Override
        protected void onPreExecute() {
            
            /* 	  public static final String[] CALENDAR_PROJECTION = new String[] {
		    Calendars._ID,                           // 0
		    Calendars.ACCOUNT_NAME,                  // 1
		    Calendars.CALENDAR_DISPLAY_NAME,         // 2
		    Calendars.OWNER_ACCOUNT                  // 3
		};
         */
		 	my_account = new Account("WhendCalendar", "com.whend.demo.account.DEMOACCOUNT");
	//	 	createCalendar(getActivity(),my_account);
		
		 	super.onPreExecute();           
        }
        Uri createCalendar(Context mContext, Account account, String cal_name, int cal_id, String cal_pagekey) 
        { 
       // 	Log.d("createCalendar","whywhye");
        	  final ContentValues v = new ContentValues(); 
              v.put(CalendarContract.Calendars.NAME,cal_pagekey);		//이걸로판별
              v.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, cal_name);                  
	          v.put(CalendarContract.Calendars.ACCOUNT_NAME, account.name);       
	          v.put(CalendarContract.Calendars.ACCOUNT_TYPE, account.type); 
	          v.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.WHITE); 
	          v.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,Calendars.CAL_ACCESS_OWNER);         
	          v.put(CalendarContract.Calendars.OWNER_ACCOUNT, account.name); 
	          v.put(CalendarContract.Calendars._ID,cal_id);// u can give any id there and use same id any where u need to create event   
	          v.put(Calendars.SYNC_EVENTS, 1); 
	          v.put(Calendars.VISIBLE, 1); 
	          Uri creationUri = asSyncAdapter(Calendars.CONTENT_URI, account.name, account.type); 
	          Uri calendarData = mContext.getContentResolver().insert(creationUri, v); 
	          return creationUri;
	    //      long id = Long.parseLong(calendarData.getLastPathSegment()); 
         } 
       
        private Uri asSyncAdapter(Uri uri, String account, String accountType) 
        { 
        	return uri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").appendQueryParameter(Calendars.ACCOUNT_NAME,account).appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build(); 
        	
        } 

        
		
		private void syncDBCalendar() throws JSONException{
		    	
			
	/*    	calID.clear();
	    	displayName.clear();
	    	accountName.clear();
	    	ownerName.clear();
	    	eventID.clear();
	    	dtstart.clear();
	    	dtend.clear();
	    	rrule.clear();
	    	title.clear();
	    	mYear.clear();
	    	mMonth.clear();
	    	mDay.clear();*/				//잠시
	    	
	    	
	    	
	    	   	
	    	
	    	AppPrefs appPrefs = new AppPrefs(getActivity());
	        String user_id = appPrefs.getUser_id();
	        String password = appPrefs.getUser_password(); 
	        JSONParser.user_id = user_id;
	        JSONParser.password = password;
	        
	        
	        JSONObject callistReq = JSONParser.getJSONFromUrl("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_data_callist.php");
			calstringarr.clear();
			calstringarr.add("MyCalendar");
			calprimaryarr.clear();
			calprimaryarr.add(98723198);
			
	        for(int i=0; i<callistReq.getInt("size"); i++){
				
				String page_key = callistReq.getJSONObject(String.valueOf(i)).getString("page_key");
				int page_primary = callistReq.getJSONObject(String.valueOf(i)).getInt("page_primary");
				String page_id = callistReq.getJSONObject(String.valueOf(i)).getString("page_id");
			/*	try {
					createCalendar(getActivity(),my_account,page_id,page_primary, URLEncoder.encode(page_key, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
//				Log.d("addcallistpagekey",page_key.replaceAll("[\\W&&[^\\\\]]+ ",""));
				
				calstringarr.add(page_id);
				calprimaryarr.add(page_primary);
				createCalendar(getActivity(),my_account,page_id,page_primary,page_key.replaceAll("[\\W&&[^\\\\]]+ ",""));
			}
			createCalendar(getActivity(),my_account,"MyCalendar",98723198,"_My_Calendar_");
	        
	//        Log.d("syncsync","wow");
			JSONObject wallReq = JSONParser.getJSONFromUrl("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_data_page.php");
			Calfeed tmp = null;
			
			for(int i=0; i<wallReq.getInt("size"); i++){
				//String page_icon = wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				Long start = wallReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_starttime");
				Long end = wallReq.getJSONObject(String.valueOf(i)).getLong("schedule_forpage_endtime");
				int schedule_forpage_primary = wallReq.getJSONObject(String.valueOf(i)).getInt("schedule_forpage_primary");
				String schedule_forpage_id = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_id");
				//String schedule_forpage_photo = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_photo");
				String schedule_forpage_memo = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_memo");
				String event_timezone = "UTC"; 
//"schedule_forpage_event_timezone");			
				String event_end_timezone = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_event_end_timezone");			
				String Rrule = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_Rrule");			
				String Rdate = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_Rdate");			
				String event_location = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_place");			
				String schedule_forpage_allday = wallReq.getJSONObject(String.valueOf(i)).getString("schedule_forpage_allday");			
				String page_key_tmp = wallReq.getJSONObject(String.valueOf(i)).getString("page_key");
				Long page_primary = wallReq.getJSONObject(String.valueOf(i)).getLong("page_primary");
				
				String urlpage_key=null;
			/*	try {
					urlpage_key = URLEncoder.encode(page_key, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				urlpage_key = page_key_tmp.replaceAll("[\\W&&[^\\\\]]+ ","");
				
	//			int follower_count = wallReq.getJSONObject(String.valueOf(i)).getInt("follower_count");
	//			int abletoremove = wallReq.getJSONObject(String.valueOf(i)).getInt("abletoremove");
	//			int abletoadd = wallReq.getJSONObject(String.valueOf(i)).getInt("abletoadd");
				if(end == 0){
					end = start + 3600*1000;
				}
				
				 //       	
				ContentValues values = new ContentValues();
		        
		        values.put(Events.DTSTART, start); 
		        values.put(Events.HAS_ALARM, 0); 
		        values.put(Events.DTEND, end); 
		        values.put(Events.EVENT_COLOR, Color.BLUE); 
		        values.put(Events.TITLE, schedule_forpage_id); 
		        values.put(Events.DESCRIPTION, schedule_forpage_memo); 
		        values.put(Events.CALENDAR_ID, page_primary); 
		        values.put(Events.EVENT_TIMEZONE, event_timezone); 
		    	values.put(Events._ID,schedule_forpage_primary+10000 );		//혹시나겹칠까봐.. 그럴일은없지만.
		    	values.put(Events.ALL_DAY,schedule_forpage_allday);
//		    	Log.d("inputdataName",schedule_forpage_id);
		    	
		    	Uri creationUri = asSyncAdapter(Events.CONTENT_URI, my_account.name, my_account.type); 
		    	Uri calendarData = getActivity().getContentResolver().insert(creationUri, values); 
		    	String[] selectiontmp = {String.valueOf(schedule_forpage_primary+10000)};
		    	getActivity().getContentResolver().update(creationUri, values, "_ID =?", selectiontmp);
		   // 	Uri uri = cr.insert(Events.CONTENT_URI, values);
//		    	Log.d("wheretogogogogo",String.valueOf(page_primary));
		    	//  long eventID = Long.parseLong(uri.getLastPathSegment()); 

	        	}
//			
			
			
			
	/*	
			JSONObject userReq = JSONParser.getJSONFromUrl("http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_data_user.php");
			
			
			for(int i=0; i<userReq.getInt("size"); i++){
				//String page_icon = wallReq.getJSONObject(String.valueOf(i)).getString("page_icon");
				Long start = userReq.getJSONObject(String.valueOf(i)).getLong("schedule_foruser_starttime");
				Long end = userReq.getJSONObject(String.valueOf(i)).getLong("schedule_foruser_endtime");
				int schedule_foruser_primary = userReq.getJSONObject(String.valueOf(i)).getInt("schedule_foruser_primary");
				String schedule_foruser_id = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_id");
				//String schedule_foruser_photo = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_photo");
				String schedule_foruser_memo = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_memo");
				String event_timezone = "UTC"; 
//"schedule_foruser_event_timezone");			
				String event_end_timezone = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_event_end_timezone");			
				String Rrule = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_Rrule");			
				String Rdate = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_Rdate");			
				String event_location = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_place");			
				String schedule_foruser_allday = userReq.getJSONObject(String.valueOf(i)).getString("schedule_foruser_allday");			
				//String page_key_tmp = userReq.getJSONObject(String.valueOf(i)).getString("page_key");
				String urlpage_key=null;
					
				urlpage_key = "_My_Calendar_";
	
				if(end == 0){
					end = start + 3600*1000;
				}
								        	
	        	int page_primary = 98723198;
				ContentValues values = new ContentValues();
		        
		        values.put(Events.DTSTART, start); 
		        values.put(Events.HAS_ALARM, 0); 
		        values.put(Events.DTEND, end); 
		        values.put(Events.EVENT_COLOR, Color.BLUE); 
		        values.put(Events.TITLE, schedule_foruser_id); 
		        values.put(Events.DESCRIPTION, schedule_foruser_memo); 
		        values.put(Events.CALENDAR_ID, page_primary); 
		        values.put(Events.EVENT_TIMEZONE, event_timezone); 
		    	values.put(Events._ID,1000000000-schedule_foruser_primary );		//혹시나겹칠까봐.. 그럴일은없지만.
		    	values.put(Events.ALL_DAY,schedule_foruser_allday);
	//	    	Log.d("inputdataName",schedule_foruser_id);
		    	
		    	Uri creationUri = asSyncAdapter(Events.CONTENT_URI, my_account.name, my_account.type); 
		    	Uri calendarData = getActivity().getContentResolver().insert(creationUri, values); 
		
	        	}*/
			
	    }
	
				
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
	//		Log.d("doingdoing","yes");
			try {
				syncDBCalendar();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
			return null;
		}
		
		protected void onPostExecute(String file_url) {
	            // dismiss the dialog once product deleted
	//            //pDialog.dismiss();
	            Log.d("postexecuteexecute","yes");
	            try {
	            	//syncDeviceCalendar();
					initialize();
	            	//makeCalendar();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HaveToSync = 1;
	    }
		
		
		
		
		
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

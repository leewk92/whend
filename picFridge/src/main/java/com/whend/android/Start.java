package com.whend.android;



import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
 













import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;










import com.facebook.LoginActivity;
import com.facebook.Session;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;


public class Start extends FragmentActivity implements OnClickListener {
	
	private static final String LOGIN_URL = "http://54.149.93.56/whend/login_forandroid.php";
	private EditText user, pass;
	private ProgressDialog pDialog;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	String user_str, pass_str, isfacebook;
//	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Button buttonLoginLogout;
	private boolean LoginSuccess = false;
	static int intentnum=0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*AppPrefs appPrefs = new AppPrefs(getBaseContext());
        appPrefs.setUser_password(null);
        appPrefs.setUser_id(null);*/
		AppPrefs appPrefs = new AppPrefs(getBaseContext());
		Log.d("mymy","mymuy");
		Log.d("my_id",appPrefs.getUser_id());
		Log.d("mymymy","mymuy");
		Log.d("mymymymy",String.valueOf(appPrefs.getUser_id()!=""));
		if(appPrefs.getUser_id()!="" && LoginSuccess == false)			//false
        {	
			AccountManager accountManager = AccountManager.get(getBaseContext()); //this is Activity
            Account account = new Account("WhendCalendar","com.whend.demo.account.DEMOACCOUNT");
            boolean success = accountManager.addAccountExplicitly(account,"password",null);
            if(success){
                Log.d("Account","Account created");
            }else{
                Log.d("Account","Account creation failed. Look at previous logs to investigate");
            }
            
			
        	Intent intent= new Intent(Start.this, PF01_Main.class);
			startActivity(intent);
			intentnum ++;
			this.finish();
			new AttemptLogin().cancel(true);
		//	this.onStop();
		
        }
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_start);
		
		TextView email = (TextView)findViewById(R.id.email);
		TextView password = (TextView)findViewById(R.id.password);
		
		user = (EditText)findViewById(R.id.user_id);
		pass = (EditText)findViewById(R.id.user_pwd);
		
		Button loginBtn = (Button) findViewById(R.id.submitBtnLogin);
		Button signupBtn = (Button) findViewById(R.id.submitBtnSignup);
		
	//	init();
	  //  dataInit();
	//    facebookInit(savedInstanceState);
	    
		//login.setOnClickListener(this);
		//signup.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		signupBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent= new Intent(Start.this, Signup.class);
				startActivity(intent);
				finish();

			}
		});
		
			       
	}
	
	@Override
	protected void onPause(){
		super.onPause();
//		Session.getActiveSession().addCallback(statusCallback);
	}
	
	
	public void onClick(View v) {  
		
		switch(v.getId()){
		case R.id.submitBtnLogin:
			user_str = user.getText().toString();
            pass_str = pass.getText().toString();
            isfacebook = "0";
           // String user_id = user.getText().toString() ; 
            //String user_pwd = pass.getText().toString() ; 
            new AttemptLogin().execute();
            break;
        default:
          	break;
          //  TextView resultField = ( TextView ) findViewById( R.id.get_result ) ; 
 /*
            try { 
           
                JSONArray ja = new JSONArray( sendData( user_id, user_pwd ) ); 
                JSONObject data = ja.getJSONObject(0);
                if (data.getString("result") == "true"){
                	Intent intent= new Intent(Start.this, Calwall.class);
        			startActivity(intent);
        			//this.finish();
                }
            } catch (ClientProtocolException e) { 
            // TODO Auto-generated catch block 
                e.printStackTrace(); 
            } catch (IOException e) { 
            // TODO Auto-generated catch block 
                e.printStackTrace(); 
            } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */
        }  
     }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
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
/*
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getTag().equals(1))
		{
			if (email.getText().equals("harry@gmail.com"))
			{
			if (password.getText().equals("1234")){
				Intent intent= new Intent(Start.this, Calwall.class);
			startActivity(intent);
			this.finish();}
			else
			{
				Toast.makeText(this, "��й�ȣ ����", Toast.LENGTH_LONG);
			}
			}
			else{
				Toast.makeText(this, "���̵� ����", Toast.LENGTH_LONG);
			}
			
		}
	}*/
	
	
	

	class AttemptLogin extends AsyncTask<String, String, String> {
		 
		 /**
         * Before starting background thread Show Progress Dialog
         * */
		boolean failure = false;
		public int NumOfPage = 0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Start.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            File file = new File("/sdcard/whend/");
            boolean result = file.mkdirs();
            //pDialog.show();
            //pDialog.dismiss();
            
        }
             
        

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
       //     String username = user.getText().toString();
       //     String password = pass.getText().toString();
            String username = user_str;
            String password = pass_str;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("isfacebook",isfacebook));
                Log.d("request!", "starting");
                Context mContext;
                mContext =  getApplicationContext();
                
          /*      AppPrefs appPrefs = new AppPrefs(mContext);
                appPrefs.setUser_password(password);
                appPrefs.setUser_id(username);*/
                                                
                // getting product details by making HTTP request
                JSONObject json = JSONParser.makeHttpRequest(
                       LOGIN_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt json", String.valueOf(json.getInt(TAG_SUCCESS)));

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	Log.d("Login Successful!", json.toString());
                	LoginSuccess = true;
                	AppPrefs appPrefs = new AppPrefs(mContext);
                    appPrefs.setUser_password(password);
                    appPrefs.setUser_id(username);
                	pDialog.dismiss();
                	finish();
    			
                //	return json.getString(TAG_SUCCESS);
                }else{
                	Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                	Intent i = new Intent(Start.this, Start.class);
                	pDialog.dismiss();
                	LoginSuccess = false;
                	//finish();
    				//startActivity(i);
                	return json.getString(TAG_SUCCESS);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

		}
		/**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            //pDialog.dismiss();
            if( LoginSuccess == true && intentnum==0){
            	Intent i = new Intent(Start.this, PF01_Main.class);
            	intentnum++;
            	AccountManager accountManager = AccountManager.get(getBaseContext()); //this is Activity
                Account account = new Account("WhendCalendar","com.whend.demo.account.DEMOACCOUNT");
                boolean success = accountManager.addAccountExplicitly(account,"password",null);
                if(success){
                    Log.d("Account","Account created");
                }else{
                    Log.d("Account","Account creation failed. Look at previous logs to investigate");
                }
            	startActivity(i);
            }
            else{
            	intentnum=0;
            	Toast.makeText(Start.this, "로그인에 실패하였습니다",Toast.LENGTH_LONG).show();
            }
            if (file_url != null){
            	Toast.makeText(Start.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

	}
	
/*	
	 private void init() {
	       buttonLoginLogout = (Button)findViewById(R.id.authButton);
	       buttonLoginLogout.setText("3초만에 페이스북 아이디로 로그인하기");
	 }

	  @SuppressLint("NewApi")
	  private void dataInit() {
	      //ActionBar Init
	      getActionBar().setDisplayShowHomeEnabled(false);
	      getActionBar().setTitle(R.string.buttonName);
	  }
	   
	   private void facebookInit(Bundle savedInstanceState) {
	       Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	       
	       Session session = Session.getActiveSession();
	       if (session == null) {
	           if (savedInstanceState != null) {
	               session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
	           }
	           if (session == null) {
	               session = new Session(this);
	           }
	           Session.setActiveSession(session);
	           if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
	        //       session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	           }
	       }
	       
	       updateView();
	   }

	    @Override
	       public void onStart() {
	           super.onStart();
	           Session.getActiveSession().addCallback(statusCallback);
	       }

	       @Override
	       public void onStop() {
	           super.onStop();
	           Session.getActiveSession().removeCallback(statusCallback);
	       }

	       @Override
	       public void onActivityResult(int requestCode, int resultCode, Intent data) {
	           super.onActivityResult(requestCode, resultCode, data);
	           Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	       }

	       @Override
	       protected void onSaveInstanceState(Bundle outState) {
	           super.onSaveInstanceState(outState);
	           Session session = Session.getActiveSession();
	           Session.saveSession(session, outState);
	       }

	       private void updateView() {
	           Session session = Session.getActiveSession();
	           if (session.isOpened()) {
	               buttonLoginLogout.setText("로그아웃");
	               buttonLoginLogout.setOnClickListener(new OnClickListener() {
	                   public void onClick(View view) { onClickLogout(); }
	               });
	               
	               new Request(
	                   session,
	                   "/me",
	                   null,
	                   HttpMethod.GET,
	                   new Request.Callback() {
	                       public void onCompleted(Response response) {
	                           // handle the result
	                    	   String rsp_str = response.getRawResponse();
	                    	   JSONObject jo;
							try {
								jo = new JSONObject(rsp_str);
								
								String FBemail = jo.getString("email");
								Log.v("responseJSON",response.getRawResponse());
		                    	   Log.v("responseParsedEMAIL",FBemail);
		                    	   user_str = "FB."+FBemail;
		                    	   pass_str = FBemail;
		                    	   isfacebook = "1";
		                    	   new AttemptLogin().execute();
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	                    	                		   
								
		                    	   
	                    	  	                    	   
	                    	   
	                       }
	                   }
	               ).executeAsync();      
	               
	               
	               
	               
	           } else {
	               buttonLoginLogout.setText("3초만에 페이스북 아이디로 로그인하기");
	               buttonLoginLogout.setOnClickListener(new OnClickListener() {
	                   public void onClick(View view) { onClickLogin(); }
	               });
	           }
	       }

	       private void onClickLogin() {
	           Session session = Session.getActiveSession();
	           if (!session.isOpened() && !session.isClosed()) {
	               session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	           } else {
	               Session.openActiveSession(this, true, statusCallback);
	               	               
	           }
	       }

	       private void onClickLogout() {
	           Session session = Session.getActiveSession();
	           if (!session.isClosed()) {
	               session.closeAndClearTokenInformation();
	           }
	       }

	       private class SessionStatusCallback implements Session.StatusCallback {
	           @Override
	           public void call(Session session, SessionState state, Exception exception) {
	               updateView();    
	               
	               
	           }
	       }
	      
	       
	*/
	
	
	
	
	/*
	
	
	
	
	
	
	
	
	
	
    private String sendData(String id, String pwd) throws ClientProtocolException, IOException { 
        // TODO Auto-generated method stub 
        HttpPost request = makeHttpPost( id, pwd, "http://54.149.93.56/mobileJQ2/login_forandroid.php" ) ; 
        // Get ����ϰ��
        //HttpGet request = makeHttpGet( id, pwd, "http://www.shop-wiz.com/android_post.php" ) ;
        HttpClient client = new DefaultHttpClient() ; 
        ResponseHandler<String> reshandler = new BasicResponseHandler() ;
        String result = client.execute( request, reshandler ) ; 
        return result ; 
    } 
 
    //Post ����ϰ��
    private HttpPost makeHttpPost(String user_id, String user_pwd, String url) { 
        // TODO Auto-generated method stub 
        HttpPost request = new HttpPost( url ) ; 
        Vector<NameValuePair> nameValue = new Vector<NameValuePair>() ; 
        nameValue.add( new BasicNameValuePair( "user_id", user_id ) ) ; 
        nameValue.add( new BasicNameValuePair( "psword", user_pwd ) ) ; 
        request.setEntity( makeEntity(nameValue) ) ; 
        return request ; 
    } 
     
    //Get ����ϰ��
    private HttpGet makeHttpGet(String user_id, String user_pwd, String url) { 
        // TODO Auto-generated method stub 
        Vector<NameValuePair> nameValue = new Vector<NameValuePair>() ; 
        nameValue.add( new BasicNameValuePair( "user_id", user_id ) ) ; 
        nameValue.add( new BasicNameValuePair( "user_pwd", user_pwd ) ) ;
           
        String my_url = url + "?" + URLEncodedUtils.format( nameValue, null) ; 
        HttpGet request = new HttpGet( my_url ) ; 
        return request ; 
    }
     
    private HttpEntity makeEntity( Vector<NameValuePair> nameValue ) { 
        HttpEntity result = null ; 
        try { 
        result = new UrlEncodedFormEntity( nameValue ) ; 
        } catch (UnsupportedEncodingException e) { 
        // TODO Auto-generated catch block 
        e.printStackTrace(); 
        } 
        return result ; 
    }
	
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
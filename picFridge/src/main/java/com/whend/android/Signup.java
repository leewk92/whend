package com.whend.android;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Signup extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		
		final EditText et_email1 = (EditText) findViewById(R.id.signup_email1);
		final EditText et_email2 = (EditText) findViewById(R.id.signup_email2);
		final EditText et_password = (EditText) findViewById(R.id.signup_password);
		final EditText et_name = (EditText) findViewById(R.id.signup_name);
		final TextView tv_signup = (TextView) findViewById(R.id.signup_textview);
		Button btn_signup = (Button) findViewById(R.id.signup_button);
		
		tv_signup.setText("@");
		
		
		
		LinearLayout mainLayout = (LinearLayout)findViewById(R.id.SignupLayout);
    	mainLayout.setOnClickListener(new View.OnClickListener() {
    	            @Override
    	            public void onClick(View v) {
    	                hideKeyboard();
    	            }
    	        });	
		
		
		
		
		btn_signup.setOnClickListener(new View.OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				
				boolean error = false;
				
				String email1 = null;
				String email2 = null;
				
				String email = null;
				String password = null;
				String name = null;
				
				email1 = et_email1.getText().toString();
				email2 = et_email2.getText().toString();
				password = et_password.getText().toString();
				name = et_name.getText().toString();
				
			
				Log.d("버튼클릭","버튼클릭");
				
				
				if (email1.equals("")){
					Log.d("에러","이메일에러");
					Toast.makeText(Signup.this , "email을 확인하세요", 1000);
					error = true;
				}
				
				if (email2.equals("")){
					Log.d("에러","이메일에러");
					Toast.makeText(Signup.this , "email을 확인하세요", 1000);
					error = true;
				}
				
				if(password.equals("")){
					Log.d("에러","패스워드에러");
					Toast.makeText(Signup.this , "패스워드를 확인하세요", 1000);
					error = true;
				}
				
				if(name.equals("")){
					Log.d("에러","이름에러");
					Toast.makeText(Signup.this , "이름을 확인하세요", 1000);
					error = true;
				}
				
				
				
				if(error)
					return;
				
				else{
					email = email1 + "@" + email2;
					// CreatUser.execute();					
					// Register.java 에 구현이 되어있는 것 같다.
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();	 
				params.add(new BasicNameValuePair("user_id", email));
		        params.add(new BasicNameValuePair("password", password));
		        params.add(new BasicNameValuePair("name", name));
		        new AsyncSignup(params).execute();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signup, menu);
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
	
	private void hideKeyboard(){

		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	}
	
	class AsyncSignup extends AsyncTask<String, String, String> {
		
		private List<NameValuePair> params;
		public AsyncSignup(List<NameValuePair> _params){
			this.params = _params;
			
		}
		    @Override
		    protected void onPreExecute() {
		       	super.onPreExecute();
		    }
		    
			  @Override
				protected String doInBackground(String... param) {
					// TODO Auto-generated method stub
					                                                             
	                // getting product details by making HTTP request
				
                
		        JSONObject json = JSONParser.makeHttpRequest(
                       "http://ec2-54-149-93-56.us-west-2.compute.amazonaws.com/whend/and_sign_up.php", "POST", params);
                try {
					Log.d("signup",json.getString("success"));
					
					if(json.getString("login").compareTo("1")==0){
						Start.intentnum=0;
				//		Toast.makeText(Signup.this, "회원가입에 성공하였습니다! 로그인 해주세요",Toast.LENGTH_LONG).show();
						Intent i = new Intent(Signup.this, Start.class);
						startActivity(i);
						
					}
			//		else 	Toast.makeText(Signup.this, "이미 가입된 아이디입니다", Toast.LENGTH_LONG).show();
					
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

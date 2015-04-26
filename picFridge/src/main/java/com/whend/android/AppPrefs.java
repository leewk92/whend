package com.whend.android;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 
public class AppPrefs {
 private static final String USER_PREFS = "com.example.picfridge";
 private SharedPreferences appSharedPrefs;
 private SharedPreferences.Editor prefsEditor;
 private String password = "password";
 private String user_id = "user_id";
 private String caljson_str = null;
 private String primary = "primary";
 
public AppPrefs(Context context){
 this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
 this.prefsEditor = appSharedPrefs.edit();
 }
public String getUser_id() {
 return appSharedPrefs.getString(user_id, "");
 }
 
public void setUser_id(String _user_id) {
 prefsEditor.putString(user_id, _user_id).commit();
}

public int getPrimary(){
	return appSharedPrefs.getInt(primary, 0);
}
public void setPrimary(int _primary){
	prefsEditor.putInt(primary,_primary).commit();
}

public String getUser_password() {
 return appSharedPrefs.getString(password, "");
 }
 
 public void setUser_password( String _password) {
 prefsEditor.putString(password, _password).commit();
 }
 
 public String getCaljson_str (){
	 return appSharedPrefs.getString(caljson_str,"");
 }
 
 public void setCaljson_str(String _caljson_str){
	 prefsEditor.putString(caljson_str, _caljson_str);
 }
 
 
 
}
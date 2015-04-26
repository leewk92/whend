package com.whend.android;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Image_DownloadFileAsync extends AsyncTask<String, String, String> {

//	private ProgressDialog mDlg;
	private Context mContext;
	String cachepath;
	public Image_DownloadFileAsync(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
//		mDlg = new ProgressDialog(mContext);
//		mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		mDlg.setMessage("Start");
//		mDlg.show();
//		File file = new File("/sdcard/whend/");
		cachepath = mContext.getCacheDir().getPath() + "/whend_icon";
		File file = new File(cachepath);
		boolean result = file.mkdirs();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {

		int count = 0;
		
		try {
			URL url = new URL(params[0].toString());
			
			String path = url.toString().substring(url.toString().lastIndexOf('/'), url.toString().length());
			
	//		Log.d("fileexist", path + " : " + String.valueOf(new File("/sdcard/whend"+path).exists()));
			Log.d("fileexist2", path + " : " + String.valueOf(new File(cachepath+path).exists()));
				
			//Log.d("isItFalse?",String.valueOf(path.compareTo("/and_null")));
						
			
//			if(path.compareTo("/and_null")!=0 && path.compareTo("/and_")!=0 && !new File("/sdcard/whend"+path).exists()){		//파일 존재하면 밑애꺼 안함.
			if(path.compareTo("/and_null")!=0 && path.compareTo("/and_")!=0 && !new File(cachepath+path).exists()){		//파일 존재하면 밑애꺼 안함.
						
			
				
				URLConnection conexion = url.openConnection();
				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();
				Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

				InputStream input = new BufferedInputStream(url.openStream());
				
//				OutputStream output = new FileOutputStream("/sdcard/whend"+path);
				OutputStream output = new FileOutputStream(cachepath+path);
				
//				Log.d("writepath","/sdcard/whend"+path);
				Log.d("writepath",cachepath+path);
				
				
				
				byte data[] = new byte[1024];
	
				long total = 0;
	
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				
				}
	
				output.flush();
				output.close();
				input.close();
			}
			// �۾��� ����Ǹ鼭 ȣ���ϸ� ȭ���� ���׷��̵带 ����ϰ� �ȴ�
			//publishProgress("progress", 1, "Task " + 1 + " number");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ������ ������ �����ϴ� ���� ������ ����� onProgressUpdate �� �Ķ���Ͱ� �ȴ�
		return null;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		if (progress[0].equals("progress")) {
//			mDlg.setProgress(Integer.parseInt(progress[1]));
//			mDlg.setMessage(progress[2]);
		} else if (progress[0].equals("max")) {
//			mDlg.setMax(Integer.parseInt(progress[1]));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPostExecute(String unused) {
//		mDlg.dismiss();
		//Toast.makeText(mContext, Integer.toString(result) + " total sum",
				//Toast.LENGTH_SHORT).show();
	}
}

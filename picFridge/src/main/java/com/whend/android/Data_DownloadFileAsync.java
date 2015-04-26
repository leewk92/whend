package com.whend.android;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Data_DownloadFileAsync extends AsyncTask<String, String, String> {

	private ProgressDialog mDlg;
	private Context mContext;

	public Data_DownloadFileAsync(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		mDlg = new ProgressDialog(mContext);
		mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDlg.setMessage("Start");
		mDlg.show();

		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {

		int count = 0;
		
		try {
			Thread.sleep(100);
			URL url = new URL(params[0].toString());
			URLConnection conexion = url.openConnection();
			conexion.connect();

			int lenghtOfFile = conexion.getContentLength();
			Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream("/sdcard/data.db");

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
			
			// �۾��� ����Ǹ鼭 ȣ���ϸ� ȭ���� ���׷��̵带 ����ϰ� �ȴ�
			//publishProgress("progress", 1, "Task " + 1 + " number");
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ������ ������ �����ϴ� ���� ������ ����� onProgressUpdate �� �Ķ���Ͱ� �ȴ�
		return null;
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		if (progress[0].equals("progress")) {
			mDlg.setProgress(Integer.parseInt(progress[1]));
			mDlg.setMessage(progress[2]);
		} else if (progress[0].equals("max")) {
			mDlg.setMax(Integer.parseInt(progress[1]));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPostExecute(String unused) {
		mDlg.dismiss();
		//Toast.makeText(mContext, Integer.toString(result) + " total sum",
				//Toast.LENGTH_SHORT).show();
	}
}

package com.ntj.GcmTest;

import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
	private static final String TAG = "MyGCM";
	private static final int MSG_MESSAGE = 0x0010; 
	private MyThread mThread = null;

	private void add(String newString) {
		LinkedList<String> list = new LinkedList<String>();
		SharedPreferences sp = getSharedPreferences("histories", MODE_PRIVATE);
		for (int i = 0; i < 20; i++) {
			String historyKey = "history" + i;
			String h = sp.getString(historyKey, "");
			if (h == null || h.isEmpty())
				continue;
			list.add(h);
		}

		// modify
		list.addFirst(newString);
		while (list.size() > 20)
			list.removeLast();

		// write back
		Editor edit = sp.edit();
		int i = 0;
		for (String h : list) {
			String historyKey = "history" + i++;
			edit.putString(historyKey, h);
		}
		edit.commit();
	}
	
	protected void processMessage(String input) {
		if ((input.startsWith("http://") || input.startsWith("https://")) &&
				Patterns.WEB_URL.matcher(input).matches()) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(input));
			// Start in non-activity
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				startActivity(intent);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		ClipboardManager cb = (ClipboardManager)
				getSystemService(Context.CLIPBOARD_SERVICE);
		cb.setPrimaryClip(ClipData.newPlainText(
				ClipDescription.MIMETYPE_TEXT_PLAIN, input));

		add(input);
	}
	
	class MyThread extends Thread {
		private Context mContext = null;
		private Handler mHandler = null;

		public MyThread(String s, Context context) {
			super(s);
			this.mContext = context;
		}

		public void setMessage(String message) {
			Message msg = Message.obtain(mHandler, MSG_MESSAGE, message);
			mHandler.sendMessage(msg);
		}
		
		@SuppressLint("HandlerLeak")
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					int what = msg.what;
					if (what == MSG_MESSAGE) {
						String input = (String) msg.obj;
						((MyGcmListenerService)mContext).processMessage(input);
					}
					getLooper().quit();
				}
			};
			Looper.loop();
			mHandler = null;
		}
	}

	public void onMessageReceived(String from, Bundle data) {
		String message = data.getString("message");
		// DEBUG Log.d(TAG, "From: " + from);
		// DEBUG Log.d(TAG, "Message: " + message);
		mThread = new MyThread("NTjGCM", this);
		mThread.start();
		synchronized (this) {
			try {
				// Wait the looper begin
				wait(10);
			} catch (InterruptedException e) {
			}
		}
		mThread.setMessage(message);
	}
}

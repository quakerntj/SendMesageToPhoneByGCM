package com.ntj.GcmTest;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import android.app.Activity;
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
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class MainActivity extends Activity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MyGCM";
    private static final int MSG_SHOW_TOKEN = 0x0010;
    private static final int MSG_LOAD_HISTORY = 0x0011;
    private static final int MSG_COPY_TO_CLIPBOARD = 0X0012;
    private static final int MSG_UPDATE_HISTORY = 0x0013;

    static class Item {
    	public LinearLayout linear;
    	public String history;
    };
    
    protected LinkedList<Item> mHistories;
    protected boolean mDirty = false;
    private boolean mTokenOkay = false;

    private class MyHandler extends Handler {
    	private WeakReference<MainActivity> mWeakContext;

    	MyHandler(MainActivity context) {
    		super();
			mWeakContext = new WeakReference<MainActivity>(context);
    	}

    	private LinearLayout getNewItem(Activity activity, String historyString) {
    		final LinearLayout l = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.history_button, null);
			Button btn;
			btn = (Button) l.findViewById(R.id.btnHistoryString);
			btn.setText(historyString);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity activity = mWeakContext.get();
					if (activity == null)
						return;
					Button btn = (Button) v;
					String historyString = btn.getText().toString();
					if (historyString == null)
						return;
					ClipboardManager cb = (ClipboardManager)
							activity.getSystemService(Context.CLIPBOARD_SERVICE);
					cb.setPrimaryClip(ClipData.newPlainText(
							ClipDescription.MIMETYPE_TEXT_PLAIN, historyString));
				}
			});
			btn = (Button) l.findViewById(R.id.btnRemove);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDirty = true;
					final int size = mHistories.size();
					for (int i = 0; i < size; i++) {
						Item item = mHistories.get(i);
						if (item.linear == l) {
							mHistories.remove(i);
							break;
						}
					}
			    	Message.obtain(mHandler, MSG_UPDATE_HISTORY).sendToTarget();
				}
			});
			return l;
    	}

    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		MainActivity activity = mWeakContext.get();
			if (activity == null)
				return;

    		if (msg.what == MSG_SHOW_TOKEN) {
    			final String token = (String) msg.obj;
    			Button code = (Button) activity.findViewById(R.id.textCode);
				code.setText("Token is:\n\n" + token + 
						"\n\nThis code is already copied to your clipboard." + 
						"  You can paste it in other text input.");

				ClipboardManager cb = (ClipboardManager)
						activity.getSystemService(Context.CLIPBOARD_SERVICE);
				cb.setPrimaryClip(ClipData.newPlainText(
						ClipDescription.MIMETYPE_TEXT_PLAIN, token));

				SharedPreferences sp = activity.getPreferences(MODE_PRIVATE);
				sp.edit().putString("token", token).commit();
				mTokenOkay = true;
    		} else if (msg.what == MSG_COPY_TO_CLIPBOARD) {
    			final String token = (String) msg.obj;
				ClipboardManager cb = (ClipboardManager)
						activity.getSystemService(Context.CLIPBOARD_SERVICE);
				cb.setPrimaryClip(ClipData.newPlainText(
						ClipDescription.MIMETYPE_TEXT_PLAIN, token));
    		} else if (msg.what == MSG_UPDATE_HISTORY) {
    			LinearLayout linearHistory = (LinearLayout) activity.findViewById(R.id.linearHistory);
    			View v = linearHistory.findViewById(R.id.textHistory);
    			linearHistory.removeAllViews();
    			linearHistory.addView(v);
    			for (Item item : mHistories) {
    				linearHistory.addView(item.linear);
    			}
    		} else if (msg.what == MSG_LOAD_HISTORY) {
    			mDirty = false;
    			SharedPreferences sp = activity.getSharedPreferences("histories", MODE_PRIVATE);
    			mHistories = new LinkedList<Item>();
				LinearLayout linearHistory = (LinearLayout) activity.findViewById(R.id.linearHistory);
				View v = linearHistory.findViewById(R.id.textHistory);
				linearHistory.removeAllViews();
				linearHistory.addView(v);

    			for (int i = 0; i < 20; i++) {
    				String historyKey = "history" + i;
    				String historyString = sp.getString(historyKey, "");
    				if (historyString == null || historyString.isEmpty())
    					continue;
    				Item item = new Item();
					LinearLayout l = getNewItem(activity, historyString);
					linearHistory.addView(l);

					item.history = historyString;
    				item.linear = l;
					mHistories.add(item);
    			}
    		}
    	}
    };

    private static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mHandler = new MyHandler(this);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

	private void emailAccess(String token) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String subject = "How to use GCMTest";
		String body = "Dear User,\n\nThis mail will show you how to use this in your desktop or other device.\n" +
				"At first, your token is in the following line:\n\n" + token + "\n\n";
		Uri data = Uri.parse("mailto:?subject=" + subject + "&body=" + body);
		intent.setData(data);
		startActivity(Intent.createChooser(intent, "Choose how to deliver this \"INFORMATION\" mail"));
	}

    public void onGetToken(View v) {
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
        String token = sp.getString("token", "");
		if (mTokenOkay) {
			emailAccess(token);
		} else {
	        if (!token.isEmpty())
	        	Message.obtain(mHandler, MSG_SHOW_TOKEN, token).sendToTarget();
        }
    }
    
    public void getToken() {
        final Activity context = this;
        HandlerThread t = new HandlerThread("GcmMain") {
        	@Override
        	protected void onLooperPrepared() {
		        if (!checkPlayServices())
		        	return;
				InstanceID iid = InstanceID.getInstance(context);
				// Log.i(TAG, "getId() = " + iid.getId());
				String token = "";
				try {
					token = iid.getToken("506066863990", GoogleCloudMessaging.INSTANCE_ID_SCOPE);
					// Log.i(TAG, "getToken() = " + token);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				Message.obtain(mHandler, MSG_SHOW_TOKEN, token).sendToTarget();
				quit();
        	}
        };
        t.start();
        mTokenOkay = true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();

    	Message.obtain(mHandler, MSG_LOAD_HISTORY).sendToTarget();
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        String token = sp.getString("token", "");
        if (token.isEmpty())
        	getToken();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	if (mDirty) {
    		SharedPreferences sp = getSharedPreferences("histories", MODE_PRIVATE);

    		// write back
    		Editor edit = sp.edit();
			edit.clear();
    		int i = 0;
    		for (Item item : mHistories) {
    			String historyKey = "history" + i++;
    			edit.putString(historyKey, item.history);
    		}
    		edit.commit();
    	}
    	mDirty = false;
    }
}

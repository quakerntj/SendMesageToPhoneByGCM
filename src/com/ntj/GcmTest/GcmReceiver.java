package com.ntj.GcmTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GcmReceiver extends BroadcastReceiver {
	private static final String TAG = "MyGCM";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive " + intent);
	}
}

package com.ntj.GcmTest;

import java.io.IOException;

import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends  InstanceIDListenerService {
	private static final String TAG = "MyGCM";
	@Override
	public void onTokenRefresh() {
		Log.d(TAG, "onTokenRefresh");
		InstanceID iid = InstanceID.getInstance(this);
		String token;
		try {
			token = iid.getToken("506066863990", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			Log.d(TAG, token);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

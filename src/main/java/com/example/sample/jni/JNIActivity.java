package com.example.sample.jni;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class JNIActivity extends Activity implements IJNICallback, OnClickListener  {
	private final String TAG = this.getClass().toString();

	public native int setUserCallBack(IJNICallback cb);
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Button btn = new Button(this);
		btn.setOnClickListener(this);
		this.setContentView(btn);
	}
	
	@Override
	public int callTest(int val, int val2) {
		return Log.d(TAG, "test = " + val + ", val2 = " + val2);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		setUserCallBack(this);
	}
}

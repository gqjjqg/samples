package com.example.sample;

import com.guo.android_extend.CustomOrientationDetector;
import com.guo.android_extend.controller.CenterController;
import com.guo.android_extend.widget.ExtImageView;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

public class ImageViewTestActivity extends Activity {
	@SuppressWarnings("unused")
	private final String TAG = this.getClass().toString();

	CustomOrientationDetector mODetector;
	ExtImageView eiv;
	Rect rect;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_imageview);
		
		mODetector = new CustomOrientationDetector(this);
		mODetector.enable();
		
		eiv = (ExtImageView) this.findViewById(R.id.imageView1);
		eiv.setBackgroundColor(Color.BLACK);
		eiv.setImageResource(R.drawable.ic_launcher);
		eiv.setImageCtrl(new CenterController(this, eiv));
		mODetector.addReceiver(eiv);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		rect = eiv.getDrawable().copyBounds();
		Log.d(TAG, "view w =" + eiv.getWidth() + ",h =" + eiv.getHeight());
		Log.d(TAG, "rect =" + rect.toString());
		super.onDestroy();
	}


}

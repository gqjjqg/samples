package com.guo.samples;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;

import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.widget.ExtOrientationDetector;
import com.guo.android_extend.widget.controller.ImageController;
import com.guo.android_extend.widget.ExtImageView;

public class ImageViewActivity extends Activity {

	private final String TAG = this.getClass().toString();

	String mFilePath = null;
	ExtOrientationDetector mODetector;
	ExtImageView eiv;
	Bitmap mBitmap;
	ImageConverter mImageConverter;
	Rect rect;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_imageview);
		
		mODetector = new ExtOrientationDetector(this);
		mODetector.enable();

		mImageConverter = new ImageConverter();
		
		eiv = (ExtImageView) this.findViewById(R.id.imageView1);
		eiv.setBackgroundColor(Color.BLACK);
		eiv.setImageCtrl(new ImageController(this, eiv));
		mODetector.addReceiver(eiv);
		
		//initial data.
		mFilePath = getIntent().getStringExtra("image");
		if (mFilePath == null) {
			Log.e(TAG, "getIntentData fail!");
			eiv.setImageResource(R.drawable.ic_launcher);
		} else {
			getIntent().getExtras().getString("image");
			mBitmap = loadImage(mFilePath);
			eiv.setImageBitmap(mBitmap);
		}

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		eiv.setImageBitmap(null);
		if (null != mBitmap) {
			mBitmap.recycle();
		}
		super.onDestroy();
	}

	private boolean getIntentData(Bundle bundle) {
		try {
			mFilePath = bundle.getString("imagePath");
			if (mFilePath == null || mFilePath.isEmpty()) {
				return false;
			}
			Log.i(TAG, "getIntentData:" + mFilePath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param path
	 * @return bitmap
	 */
	private Bitmap loadImage(String path) {
		Bitmap res;
		try {
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			
			BitmapFactory.Options op = new BitmapFactory.Options();    
	        op.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(path, op);
	        Log.d(TAG, "Image:" + op.outWidth + "X" + op.outHeight);
			
	        op.inSampleSize = 1;
	        op.inJustDecodeBounds = false;  
	        //op.inMutable = true;
	        res = BitmapFactory.decodeFile(path, op);
	        
	        //rotate and scale.
	        Matrix matrix = new Matrix();
        	if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
			    matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
			    matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
			    matrix.postRotate(270);
			}
        	Bitmap bmp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
        	Log.d(TAG, "check target Image:" + bmp.getWidth() + "X" + bmp.getHeight());

        	try {
				byte[] data = new byte[bmp.getWidth() * bmp.getHeight() * 3 / 2];
				mImageConverter.initial(bmp.getWidth(), bmp.getHeight(), ImageConverter.CP_PAF_NV12);
				if (!mImageConverter.convert(bmp, data)) {
					Log.e(TAG, "CONVERT FAIL!");
				}
				mImageConverter.destroy();
			} catch (Exception e) {
        		e.printStackTrace();
			}
			return bmp;
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
		return null;
	}
}

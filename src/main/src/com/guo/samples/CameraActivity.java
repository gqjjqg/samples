package com.guo.samples;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.guo.android_extend.tools.CameraHelper;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;

import java.util.List;

public class CameraActivity extends Activity implements OnCameraListener, View.OnTouchListener, Camera.AutoFocusCallback {
	private final String TAG = this.getClass().getSimpleName();
	
	private int mWidth, mHeight, mFormat;
	private CameraSurfaceView mSurfaceView;
	private CameraGLSurfaceView mGLSurfaceView;
	private Camera mCamera;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_camera);

		mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView1);
		mGLSurfaceView.setOnTouchListener(this);
		mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView1);
		mSurfaceView.setOnCameraListener(this);
		mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, false, 90);
		mSurfaceView.debug_print_fps(true, false);

		mWidth = 1280;
		mHeight = 720;
		mFormat = ImageFormat.NV21;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public Camera setupCamera() {
		// TODO Auto-generated method stub
		mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mWidth, mHeight);
			parameters.setPreviewFormat(mFormat);

			for( Camera.Size size : parameters.getSupportedPreviewSizes()) {
				Log.d(TAG, "SIZE:" + size.width + "x" + size.height);
			}
			for( Integer format : parameters.getSupportedPreviewFormats()) {
				Log.d(TAG, "FORMAT:" + format);
			}

			List<int[]> fps = parameters.getSupportedPreviewFpsRange();
			for(int[] count : fps) {
				Log.d(TAG, "T:");
				for (int data : count) {
					Log.d(TAG, "V=" + data);
				}
			}
			//parameters.setPreviewFpsRange(15000, 30000);
			//parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
			//parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			//parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
			//parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			//parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			//parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
			mCamera.setParameters(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mCamera != null) {
			mWidth = mCamera.getParameters().getPreviewSize().width;
			mHeight = mCamera.getParameters().getPreviewSize().height;
		}
		return mCamera;
	}
	
	@Override
	public void setupChanged(int format, int width, int height) {
		
	}

	@Override
	public boolean startPreviewLater() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPreview(byte[] data, int width, int height, int format, long timestamp) {

	}

	@Override
	public void onPreviewRender(byte[] data, int width, int height, int format, long timestamp) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		CameraHelper.touchFocus(mCamera, event, v, this);
		return false;
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		if (success) {
			Log.d(TAG, "Camera Focus SUCCESS!");
		}
	}
}

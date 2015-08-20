package com.guo.samples;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;

import com.guo.android_extend.GLES2Render;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView.OnCameraListener;
import com.guo.android_extend.widget.ExtGLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraActivity extends Activity implements OnCameraListener, Renderer {
	private final String TAG = this.getClass().getSimpleName();
	
	private int mWidth, mHeight, mFormat;
	private CameraSurfaceView mSurfaceView;
	private ExtGLSurfaceView mGLSurfaceView;
	private GLES2Render mGLES2Render;
	private byte[] mImageData;
	private Camera mCamera;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.camera);
		
		mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView1);  
		mSurfaceView.setOnCameraListener(this);
		mSurfaceView.showFPS(true);
		
		mGLSurfaceView = (ExtGLSurfaceView) findViewById(R.id.glsurfaceView1);
		mGLSurfaceView.setEGLContextClientVersion(2);
		mGLSurfaceView.setRenderer(this);
		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		mGLSurfaceView.setZOrderMediaOverlay(true);
		
		mImageData = null;
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
		mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(mWidth, mHeight);
			parameters.setPreviewFormat(mFormat);
			//parameters.setPreviewFpsRange(20000, 60000);
			//parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
			//parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
			//parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
			//parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			//parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
			//parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
			mCamera.setParameters(parameters);
		} catch (Exception e) {
			mWidth = mCamera.getParameters().getPreviewSize().width;
			mHeight = mCamera.getParameters().getPreviewSize().height;
			e.printStackTrace();
		}
		mGLSurfaceView.setAspectRatio((double)mWidth / (double)mHeight);
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
	public void onPreview(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		mImageData = data.clone();
		mGLSurfaceView.requestRender();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		if (mImageData != null) {
			mGLES2Render.render(mImageData, mWidth, mHeight);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		mGLES2Render.setViewPort(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		mGLES2Render = new GLES2Render(true, 0, ImageConverter.CP_PAF_NV21, false);
	}

}

package com.guo.samples;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.guo.android_extend.GLES2Render;
import com.guo.android_extend.device.VideoClient;
import com.guo.android_extend.image.ImageConverter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoActivity extends Activity implements Renderer, VideoClient.OnCameraListener {
	private final String TAG = this.getClass().getSimpleName();
	
	private int mWidth, mHeight, mFormat;
	private GLSurfaceView mGLSurfaceView;
	private GLES2Render mGLES2Render;
	private byte[] mImageData;

	private VideoClient mVideoClient;
	private ImageConverter mImageConverter;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.camera);
		
		mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceView1);
		mGLSurfaceView.setEGLContextClientVersion(2);
		mGLSurfaceView.setRenderer(this);
		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		mGLSurfaceView.setZOrderMediaOverlay(true);
		
		mImageData = null;
		mWidth = 1280;
		mHeight = 720;
		mFormat = ImageConverter.CP_MJPEG;
		
		if (mFormat == ImageConverter.CP_MJPEG) {
			mImageConverter = new ImageConverter();
			mImageConverter.initial(mWidth, mHeight, ImageConverter.CP_PAF_NV21);
		}
		try {
			mVideoClient = new VideoClient(new Handler(), 0);
			mVideoClient.setPreviewSize(mWidth, mHeight);
			mVideoClient.setPreviewFormat(mFormat);
			mVideoClient.setOnCameraListener(this);
			mVideoClient.startPreview();
			mVideoClient.start();
		} catch (Exception e) {
			e.printStackTrace();
			this.finish();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mVideoClient != null) {
			mVideoClient.stopPreview();
			mVideoClient.shutdown();
		}
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
		if (mFormat == ImageConverter.CP_MJPEG) {
			mGLES2Render = new GLES2Render(true, 0, ImageConverter.CP_PAF_NV21, false);
		} else {
			mGLES2Render = new GLES2Render(true, 0, mFormat, false);
		}
	}

	@Override
	public void onPreview(byte[] data, int size, int camera) {
		// TODO Auto-generated method stub
		if (mFormat == ImageConverter.CP_MJPEG) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, size);
			if (mImageConverter.convert(bitmap, data)) {
				mImageData = data.clone();
			} else {
				mImageData = null;
			}
		} else {
			mImageData = data.clone();
		}
		mGLSurfaceView.requestRender();
	}
	
}

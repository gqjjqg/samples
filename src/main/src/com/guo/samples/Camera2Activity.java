package com.guo.samples;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.guo.android_extend.widget.Camera2GLSurfaceView;
import com.guo.android_extend.widget.Camera2Manager;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Guo on 2018/1/23.
 */

public class Camera2Activity extends Activity implements View.OnTouchListener, Camera2GLSurfaceView.OnCameraListener, View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    private String[] ids = null;
    private int mWidth, mHeight, mFormat;
    private Camera2GLSurfaceView mGLSurfaceView;
    private boolean isPause;
    private Button mButton;
    private Button mButton1;

    /* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_camera2);
        mWidth = 960;
        mHeight = 720;
        mFormat = ImageFormat.YUV_420_888;
        //mFormat = ImageFormat.NV21;

        mGLSurfaceView = (Camera2GLSurfaceView) findViewById(R.id.glsurfaceView1);
        mGLSurfaceView.setOnTouchListener(this);
        mGLSurfaceView.setOnCameraListener(this);
        mGLSurfaceView.setRenderConfig(90, false);
        mGLSurfaceView.setImageConfig(mWidth, mHeight, mFormat);
        mGLSurfaceView.setAspectRatio(mHeight, mWidth);
        mGLSurfaceView.setAutoFitMax(true);
        mGLSurfaceView.debug_print_fps(true);

        mButton = (Button) findViewById(R.id.button5);
        mButton.setOnClickListener(this);
        mButton.setText("Front");

        mButton1 = (Button) findViewById(R.id.button6);
        mButton1.setOnClickListener(this);
        mButton1.setText("Stop");
        isPause = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public String[] chooseCamera(String[] cameras) {
        ids = new String[]{cameras[0]};
        return ids;
    }

    @Override
    public ImageReader setupPreview(String id, CameraCharacteristics sc, CaptureRequest.Builder builder) {
        List<CaptureRequest.Key<?>> listkey = sc.getAvailableCaptureRequestKeys();
        for (CaptureRequest.Key key : listkey) {
            Log.d(TAG, "Key:" + key.getName());
        }
        StreamConfigurationMap scm = sc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        boolean support = false;
        int[] formats = scm.getOutputFormats();
        for (int format : formats) {
            Log.d(TAG, "FORMAT:" + format);
            if (mFormat == format) {
                support = true;
            }
        }
        if (support == false) {
            Log.e(TAG, "not support format = " + mFormat);
            return null;
        }

        support = false;
        Size[] sizes = scm.getOutputSizes(mFormat);
        for (Size size : sizes) {
            Log.d(TAG, "SIZE:" + size.getWidth() + "x" + size.getHeight());
            if (size.getWidth() == mWidth && size.getHeight() == mHeight) {
                support = true;
            }
        }
        if (support == false) {
            Log.e(TAG, "not support size(WxH) = " + mWidth + "x" + mHeight);
            return null;
        }

        try {
            //builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
            //builder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON);
            builder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_OFF);
            builder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, CameraMetadata.CONTROL_AE_MODE_ON);
            //builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, DualCamera.this.lowerFpsRange);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ImageReader.newInstance(mWidth, mHeight, mFormat, 3);
    }

    @Override
    public Object onPreview(String id, byte[] data, int width, int height, int format, long timestamp) {
        return null;
    }

    @Override
    public void onCameraEvent(String id, int event) {
        if (id.equals(ids[0])) {
            if (event == Camera2GLSurfaceView.OnCameraListener.EVENT_FOCUS_OVER) {
                Log.d(TAG, "onCameraEvent camera locked 3a ok!");
            } else if ((event & Camera2GLSurfaceView.OnCameraListener.EVENT_CAMERA_ERROR) == Camera2GLSurfaceView.OnCameraListener.EVENT_CAMERA_ERROR) {
                Log.d(TAG, "error: 0, " + (event ^ Camera2GLSurfaceView.OnCameraListener.EVENT_CAMERA_ERROR));
            }
        } else if (id.equals(ids[1])) {
            if (event == Camera2GLSurfaceView.OnCameraListener.EVENT_FOCUS_OVER) {
                Log.d(TAG, "onCameraEvent camera locked 3a ok!");
            }else if ((event & Camera2GLSurfaceView.OnCameraListener.EVENT_CAMERA_ERROR) == Camera2GLSurfaceView.OnCameraListener.EVENT_CAMERA_ERROR) {
                Log.d(TAG, "error: 1, " + (event ^ Camera2GLSurfaceView.OnCameraListener.EVENT_CAMERA_ERROR));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button5) {

        } else if (v.getId() == R.id.button6) {
            if (isPause) {
                mGLSurfaceView.getCamera2Manager().startPreview();
                mButton1.setText("Stop");
            } else {
                mGLSurfaceView.getCamera2Manager().stopPreview();
                mButton1.setText("Start");
            }
            isPause = !isPause;
        }
    }
}

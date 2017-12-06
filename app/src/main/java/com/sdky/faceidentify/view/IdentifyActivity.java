package com.sdky.faceidentify.view;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.sdky.faceidentify.R;
import com.sdky.faceidentify.bean.IdentifyResultBean;
import com.sdky.faceidentify.bean.UserBean;
import com.sdky.faceidentify.utils.FaceUtils;
import com.sdky.faceidentify.utils.HttpCallBack;
import com.sdky.faceidentify.utils.JsonXmlUtils;
import com.sdky.faceidentify.widget.FaceView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 * <p>
 * Created by liurui on 2017/4/24.
 */
public class IdentifyActivity extends Activity implements SurfaceHolder.Callback, Camera.FaceDetectionListener {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.faceView)
    FaceView faceView;
    private Camera mCamera;
    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        ButterKnife.bind(this);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        openCamera();
    }

    private void openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//这就是前置摄像头，亲。
                    mCamera = Camera.open(i);
                }
            }
        }
        if (mCamera == null) {
            mCamera = Camera.open();
        }
        mCamera.setFaceDetectionListener(this);
        Log.d("TAG", "mCamera:" + mCamera);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mCamera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            List<Camera.Size> pictures = parameters.getSupportedPictureSizes();

            Camera.Size size = pictures.get(pictures.size() - 1);
            parameters.setPictureSize(size.width, size.height);

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();

            if (parameters.getMaxNumDetectedFaces() > 1) {
                mCamera.startFaceDetection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean identifying = false;

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        Toast.makeText(this, "faces.length:" + faces.length, Toast.LENGTH_SHORT).show();
        faceView.setFaces(faces);
        identify();
    }

    UserBean user;

    private void identify() {
        if (!identifying) {
            identifying = true;
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(final byte[] data, Camera camera) {
                    String path = savePicture(data);
                    Log.d("TAG", path);
                    user = new UserBean();
                    user.setImages(path);
                    FaceUtils.identify(user, call);
                    if (user.getUser_id() != null) {
                        mCamera.stopPreview();
                        mCamera.stopFaceDetection();
                        Toast.makeText(IdentifyActivity.this, "user_id = " + user.getUser_id(), Toast.LENGTH_SHORT).show();
                    } else {
                        identifying = false;
                    }
                }
            });
        }
    }

    private HttpCallBack call = new HttpCallBack() {
        @Override
        public void getResult(Object o) {
            String s = (String) o;
            Log.d("TAG", "result = " + s);
            IdentifyResultBean resultBean = null;
            try {
                resultBean = JsonXmlUtils.writeJsonToObject(IdentifyResultBean.class, s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            IdentifyResultBean.ResultsBean resultUser = resultBean.getResults().get(0);
            user.setUser_id(resultUser.getUid());
            user.setUser_info(resultUser.getUser_info());
            user.setLog_id(String.valueOf(resultBean.getLog_id()));
            Toast.makeText(IdentifyActivity.this, "识别成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getError(Object o) {
            Log.e("TAG", o.toString());
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopFaceDetection();
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public String savePicture(byte[] bytes) {
        try {
            String path = getFilesDir().getAbsolutePath() + "/share.png";
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

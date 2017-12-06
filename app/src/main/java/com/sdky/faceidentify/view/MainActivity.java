package com.sdky.faceidentify.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sdky.faceidentify.R;
import com.sdky.faceidentify.bean.UserBean;
import com.sdky.faceidentify.utils.FaceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sdky.faceidentify.R.id.identify;

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
 */
public class MainActivity extends Activity {

    @BindView(R.id.tv_log)
    TextView tvLog;
    private StringBuilder log = new StringBuilder("日志：");
    private String fileName;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvLog.setText(log);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                999);
    }

    @OnClick({R.id.take_photo, R.id.add, R.id.identify, R.id.identify_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.take_photo:
                takePhoto();
                break;
            case R.id.add:
                add();
                break;
            case identify:
                identify();
                break;
            case R.id.identify_page:
                startActivity(new Intent(MainActivity.this, IdentifyActivity.class));
                break;
        }
    }

    private void identify() {
        setLog("----开始识别-------");
        UserBean userBean = new UserBean();
        userBean.setImages(fileName);
//        FaceUtils.identify(userBean);
        setLog("user_id = " + userBean.getUser_id());
        setLog("user_info = " + userBean.getUser_info());
        setLog("----结束识别-------");
    }

    private void add() {
        setLog("----开始增加用户--------");
        UserBean userBean = new UserBean();
        userBean.setUser_id(String.valueOf(System.currentTimeMillis()));
        userBean.setUser_info("userName:" + new Random().nextInt(10000));
        userBean.setImages(fileName);
        FaceUtils.addUser(userBean);
        setLog("user_id = " + userBean.getUser_id());
        setLog("user_info = " + userBean.getUser_info());
        setLog("log_id = " + userBean.getLog_id());
        setLog("----结束增加用户--------");
    }

    private void takePhoto() {
        setLog("启动相机");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setLog("onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile",
                        "SD card is not avaiable/writeable right now.");
                setLog("SD card is not avaiable/writeable right now.");
                return;
            }
            String name = "" + System.currentTimeMillis() + ".jpg";
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

            FileOutputStream b = null;
            File file = new File("/sdcard/myImage/");
            file.mkdirs();// 创建文件夹
            fileName = "/sdcard/myImage/" + name;
            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                setLog("图片保存完成");
                setLog(fileName);
            } catch (FileNotFoundException e) {
                setLog("图片保存失败");
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setLog(String msg) {
        log = log.append(msg + "\n");
        handler.sendEmptyMessage(0);
    }


    public static void checkPermission(Activity context, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int status = 0;
            for (String s : permissions) {
                int i = ContextCompat.checkSelfPermission(context, s);
                if (i == PackageManager.PERMISSION_DENIED) {
                    status = PackageManager.PERMISSION_DENIED;
                    break;
                }
            }

            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (status != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，申请授权
                ActivityCompat.requestPermissions(context, permissions, requestCode);
            }
        }
    }

}

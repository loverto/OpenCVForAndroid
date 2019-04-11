package com.yinlongfei.opencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yinlongfei.ObjectDetectingView;
import com.yinlongfei.ObjectDetector;
import com.yinlongfei.listener.OnFaceDetectorListener;
import com.yinlongfei.listener.OnOpenCVLoadListener;
import com.yinlongfei.uitl.FaceUtil;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class ObjectDetectingActivity extends BaseActivity {

    private static final String TAG = "ObjectDetectingActivity";
    private ObjectDetectingView objectDetectingView;
    private ObjectDetector mFaceDetector;

    private static final String FACE1 = "face1";
    private static final String FACE2 = "face2";
    private static boolean isGettingFace = false;
    private Bitmap mBitmapFace1;
    private Bitmap mBitmapFace2;
    private String currentFile ;
    private ImageView mImageViewFace1;
    private ImageView mImageViewFace2;
    private TextView mCmpPic;
    private Button button;
    private double cmp;

    private Map<String,String> hashMap = new HashMap<>();



    private byte[] readFile(InputStream file) {
        // 需要读取的文件，参数是文件的路径名加文件名
        //if (file ==null) {
            // 以字节流方法读取文件

        InputStream fis = file;
            try {
                //fis = new FileInputStream(file);
                // 设置一个，每次 装载信息的容器
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // 开始读取数据
                int len = 0;// 每次读取到的数据的长度
                while ((len = fis.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                    // append方法往sb对象里面添加数据
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();//刷新缓冲区
                fis.close();
                outputStream.close();
                // 输出字符串
                return outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
//        } else {
//            System.out.println("文件不存在！");
//        }
//        return null;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_object_detecting);

        objectDetectingView = (ObjectDetectingView)findViewById(R.id.photograph_view);


        mImageViewFace1 = (ImageView) findViewById(R.id.imageView);
        mImageViewFace2 = (ImageView) findViewById(R.id.imageView2);
        mCmpPic = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGettingFace = true;
            }
        });



        objectDetectingView.setOnFaceDetectorListener(new OnFaceDetectorListener() {
            @Override
            public void onFace(Mat mat, Rect rect) {
//                if (isGettingFace) {
//                    if (null == mBitmapFace1 || null != mBitmapFace2) {
//                        mBitmapFace1 = null;
//                        mBitmapFace2 = null;
//
//                        // 保存人脸信息并显示
                        FaceUtil.saveImage(ObjectDetectingActivity.this, mat, rect, FACE1);
                        mBitmapFace1 = FaceUtil.getImage(ObjectDetectingActivity.this, FACE1);
//                        cmp = 0.0d;
//                    } else {

                        // 加载人脸

                        String[] list = {"tt.jpg"};//getAssets().list("image");
                        for (String file:list){
                            String absolutePath = file;//getFileStreamPath(file).getAbsolutePath();
                            currentFile = absolutePath;
                            // 计算相似度
                            // 比较人脸
                            long startTime = System.currentTimeMillis();
                            cmp = FaceUtil.compare(ObjectDetectingActivity.this, FACE1, file);
                            //Log.i(TAG, "onFace: 相似度 : " + cmp);
                            long endTime = System.currentTimeMillis();
                            // 播报语音
                            Log.i(TAG, "onFace: 相似度 : " + cmp+"本次比较花费时间："+(endTime-startTime));

                        }

//
//
//                        FaceUtil.saveImage(ObjectDetectingActivity.this, mat, rect, FACE2);
//                        mBitmapFace2 = FaceUtil.getImage(ObjectDetectingActivity.this, FACE2);
//
//                        // 计算相似度
//                        cmp = FaceUtil.compare(ObjectDetectingActivity.this, FACE1, FACE2);

//                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (null == mBitmapFace1) {
//                                mImageViewFace1.setImageResource(R.mipmap.ic_launcher);
//                            } else {
//                                mImageViewFace1.setImageBitmap(mBitmapFace1);
//                            }
//                            if (null == mBitmapFace2) {
//                                mImageViewFace2.setImageResource(R.mipmap.ic_launcher);
//                            } else {
//                                mImageViewFace2.setImageBitmap(mBitmapFace2);
//                            }
//                            mCmpPic.setText(String.format("相似度 :  %.2f", cmp) + "%");
                            // 界面展示原图片
                            File file = new File(currentFile);
                            if (file.exists()){
                                mImageViewFace2.setImageURI(Uri.fromFile(file));
                            }

                            mCmpPic.setText(String.format("相似度 :  %.2f", cmp) + "%");
                        }
                    });

//                    isGettingFace = false;
                }
//            }
        });


        objectDetectingView.setOnOpenCVLoadListener(new OnOpenCVLoadListener() {
            @Override
            public void onOpenCVLoadSuccess() {
                Toast.makeText(getApplicationContext(), "OpenCV 加载成功", Toast.LENGTH_SHORT).show();
                mFaceDetector = new ObjectDetector(getApplicationContext(), R.raw.lbpcascade_frontalface, 6, 0.2F, 0.2F, new Scalar(255, 0, 0, 255));
//                mFaceDetector.detectObject()
// 图片转换为Mat
                Mat mat = new Mat(400,400, CvType.CV_8UC1);
                InputStream open =null;
                try{
                   open = getAssets().open("image/yinlongfei.jpg");

                }catch (Exception e){
                    e.printStackTrace();
                }

//                File f = new File();
                byte [] bytes = readFile(open);
                mat.put(0,0, bytes);
                try{
                    open = getAssets().open("image/yinlongfei.jpg");

                }catch (Exception e){
                    e.printStackTrace();
                }
                // 获取图片宽高
                Bitmap bitmap = BitmapFactory.decodeStream(open);
                //BitmapFactory.Options options = new BitmapFactory.Options();
                //获取图片的宽高
                int mHeight = bitmap.getHeight();
                int mWidth = bitmap.getWidth();

                //灰化图片
                Mat gray = mat.submat(0, 400, 0, 400);
                // 从初始化的图片中找出特征图片
                Rect[] rects = mFaceDetector.detectObject(gray, new MatOfRect());
//        for (Rect rect: rects){
//
//        }
                if (rects.length>0){
                    Mat rgba = new Mat();
                    Imgproc.cvtColor(mat, rgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);
                    FaceUtil.saveImage(ObjectDetectingActivity.this,rgba,rects[0],"tt.jpg");
                    Log.i(TAG, "找到人脸，开始保存");
                }
                objectDetectingView.addDetector(mFaceDetector);
            }

            @Override
            public void onOpenCVLoadFail() {
                Toast.makeText(getApplicationContext(), "OpenCV 加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotInstallOpenCVManager() {
                showInstallDialog();
            }
        });

        objectDetectingView.loadOpenCV(getApplicationContext());
    }

    /**
     * 切换摄像头
     *
     * @param view view
     */
    public void swapCamera(View view) {
        objectDetectingView.swapCamera();
    }

}

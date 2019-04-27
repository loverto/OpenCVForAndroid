package com.yinlongfei.opencv;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yinlongfei.DnnFaceRecognitionView;
import com.yinlongfei.ObjectDetectingView;
import com.yinlongfei.ObjectDetector;
import com.yinlongfei.listener.OnFaceDetectorListener;
import com.yinlongfei.listener.OnOpenCVLoadListener;
import com.yinlongfei.uitl.FaceUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DnnActivity extends BaseActivity{


    private static final String TAG = "DnnActivity";
    private DnnFaceRecognitionView objectDetectingView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.dnn);

        objectDetectingView = (DnnFaceRecognitionView)findViewById(R.id.photograph_view);


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
                if (isGettingFace) {
                    if (null == mBitmapFace1 || null != mBitmapFace2) {
                        mBitmapFace1 = null;
                        mBitmapFace2 = null;

                        // 保存人脸信息并显示
                        FaceUtil.saveImage(DnnActivity.this, mat, rect, FACE1);
                        mBitmapFace1 = FaceUtil.getImage(DnnActivity.this, FACE1);
                        cmp = 0.0d;
                    } else {
                        FaceUtil.saveImage(DnnActivity.this, mat, rect, FACE2);
                        mBitmapFace2 = FaceUtil.getImage(DnnActivity.this, FACE2);

                        // 计算相似度
                        cmp = FaceUtil.compare(DnnActivity.this, FACE1, FACE2);
                        Log.i(TAG, "onFace: 相似度 : " + cmp);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null == mBitmapFace1) {
                                mImageViewFace1.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                mImageViewFace1.setImageBitmap(mBitmapFace1);
                            }
                            if (null == mBitmapFace2) {
                                mImageViewFace2.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                mImageViewFace2.setImageBitmap(mBitmapFace2);
                            }
                            mCmpPic.setText(String.format("相似度 :  %.2f", cmp) + "%");
                        }
                    });

                    isGettingFace = false;
                }
            }
        });


        objectDetectingView.setOnOpenCVLoadListener(new OnOpenCVLoadListener() {
            @Override
            public void onOpenCVLoadSuccess() {
                Toast.makeText(getApplicationContext(), "OpenCV 加载成功", Toast.LENGTH_SHORT).show();
                mFaceDetector = new ObjectDetector(getApplicationContext(), R.raw.lbpcascade_frontalface, 6, 0.2F, 0.2F, new Scalar(255, 0, 0, 255));
                objectDetectingView.addDetector(mFaceDetector);
                loadNetwork();
                objectDetectingView.setNet(net);

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


    // Initialize OpenCV manager.
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS: {
//                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    break;
//                }
//                default: {
//                    super.onManagerConnected(status);
//                    break;
//                }
//            }
//        }
//    };
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // 异步加载
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        // 设置布局文件
//        setContentView(R.layout.dnn);
//
//        // Set up camera listener.
//        // 处理视频展示
//        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.CameraView);
//        // 设置视频可用
//        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
//        // 设置相机视频监听
//        mOpenCvCameraView.setCvCameraViewListener(this);
//    }

    // Load a network.
    // 加载神经网络
    public void loadNetwork() {
        //摄像机view开始时执行获取分类

       // String proto = getPath("frozen_inference_graph_face.pb", this);
//        String proto = getPath("MobileNetSSD_deploy.prototxt", this);
//        String weights = getPath("MobileNetSSD_deploy.caffemodel", this);
        String proto = getPath("deploy.prototxt.txt", this);
        String weights = getPath("res10_300x300_ssd_iter_140000.caffemodel", this);
        // 获取模型权重
        //
//        String proto = getPath("face_landmark_68_model-shard1", this);
//        String weights = getPath("face_landmark_68_model-weights_manifest.json", this);
        // 设置从caffe中获取dnn识别模型
        net = Dnn.readNetFromCaffe(proto, weights);
        // 从tf中读取神经网络模型
        //net = Dnn.readNetFromTensorflow(proto);
        //net = Dnn.readNetFromTensorflow(proto,weights);
        //Dnn.read
        // 神经网络加载成功
        Log.i(TAG, "Network loaded successfully");
    }

//    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
//          final int IN_WIDTH = 300;
//        final int IN_HEIGHT = 300;
//        final float WH_RATIO = (float)IN_WIDTH / IN_HEIGHT;
//        final double IN_SCALE_FACTOR = 0.007843;
//        final double MEAN_VAL = 127.5;
//        final double THRESHOLD = 0.2;
//
//        // Get a new frame
//        // 获取新的帧
//        Mat frame = inputFrame.rgba();
//        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);
//
//        // Forward image through network.
//        // 把图片传输到网络
//        Mat blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR,
//                new Size(IN_WIDTH, IN_HEIGHT),
//                new Scalar(MEAN_VAL, MEAN_VAL, MEAN_VAL), /*swapRB*/false, /*crop*/false);
//        net.setInput(blob);
//        //
//        Mat detections = net.forward();
//
//        int cols = frame.cols();
//        int rows = frame.rows();
//
//        detections = detections.reshape(1, (int)detections.total() / 7);
//
//        for (int i = 0; i < detections.rows(); ++i) {
//            double confidence = detections.get(i, 2)[0];
//            if (confidence > THRESHOLD) {
//                int classId = (int)detections.get(i, 1)[0];
//
//                int left   = (int)(detections.get(i, 3)[0] * cols);
//                int top    = (int)(detections.get(i, 4)[0] * rows);
//                int right  = (int)(detections.get(i, 5)[0] * cols);
//                int bottom = (int)(detections.get(i, 6)[0] * rows);
//
//                // Draw rectangle around detected object.
//                //  给检测的对象画矩形框
//                Imgproc.rectangle(frame, new Point(left, top), new Point(right, bottom),
//                        new Scalar(0, 255, 0));
//                String label = classNames[classId] + ": " + confidence;
//                int[] baseLine = new int[1];
//                Size labelSize = Imgproc.getTextSize(label, Core.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);
//
//                // Draw background for label.
//                // 给标签画背景
//                Imgproc.rectangle(frame, new Point(left, top - labelSize.height),
//                        new Point(left + labelSize.width, top + baseLine[0]),
//                        new Scalar(255, 255, 255), Core.FILLED);
//                // Write class name and confidence.
//                // 添加类名和可信度
//                Imgproc.putText(frame, label, new Point(left, top),
//                        Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0));
//            }
//        }
//        return frame;
//    }

//    public void onCameraViewStopped() {}

    // Upload file to storage and return a path.

    /**
     * 从资源目录加载文件
     * @param file 文件名称
     * @param context 应用上下文
     * @return
     */
    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();

        BufferedInputStream inputStream = null;
        try {
            // 从加载文件
            //     * @中读取数据。
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            // 在存储中创建副本文件。
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // 返回一个可以通用方式读取的文件路径。
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }

//    private static final String TAG = "OpenCV/Sample/MobileNet";
//    private static final String[] classNames = {"background",
//            "aeroplane", "bicycle", "bird", "boat",
//            "bottle", "bus", "car", "cat", "chair",
//            "cow", "diningtable", "dog", "horse",
//            "motorbike", "person", "pottedplant",
//            "sheep", "sofa", "train", "tvmonitor"};

    private Net net;
//    private CameraBridgeViewBase mOpenCvCameraView;
}


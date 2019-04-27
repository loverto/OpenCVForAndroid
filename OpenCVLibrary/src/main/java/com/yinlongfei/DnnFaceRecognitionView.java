package com.yinlongfei;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.yinlongfei.listener.OnFaceDetectorListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Created by kqw on 2016/7/13.
 * RobotCameraView
 */
public class DnnFaceRecognitionView extends BaseCameraView {

    /**
     * 日志标记
     */
    private static final String TAG = "ObjectDetectingView";
    /**
     * 对象检测列表
     */
    private ArrayList<ObjectDetector> mObjectDetects;

    private MatOfRect mObject;

    private OnFaceDetectorListener onFaceDetectorListener;

    /**
     * 神经网络
     */
    private Net net;

    @Override
    public void onOpenCVLoadSuccess() {
        Log.i(TAG, "onOpenCVLoadSuccess: ");

        mObject = new MatOfRect();

        mObjectDetects = new ArrayList<>();
    }

    @Override
    public void onOpenCVLoadFail() {
        Log.i(TAG, "onOpenCVLoadFail: ");
    }

    public DnnFaceRecognitionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        final int IN_WIDTH = 300;
        final int IN_HEIGHT = 300;
        final float WH_RATIO = (float)IN_WIDTH / IN_HEIGHT;
        final double IN_SCALE_FACTOR = 0.007843;
        final double MEAN_VAL = 127.5;
        final double THRESHOLD = 0.2;

        // Get a new frame
        // 获取新的帧
        Mat frame = inputFrame.rgba();
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

        // Forward image through network.
        // 把图片传输到网络
        Mat blob = Dnn.blobFromImage(frame, IN_SCALE_FACTOR,
                new Size(IN_WIDTH, IN_HEIGHT),
                new Scalar(MEAN_VAL, MEAN_VAL, MEAN_VAL), /*swapRB*/false, /*crop*/false);
        net.setInput(blob);
        //
        Mat detections = net.forward();

        int cols = frame.cols();
        int rows = frame.rows();

        detections = detections.reshape(1, (int)detections.total() / 7);

        for (int i = 0; i < detections.rows(); ++i) {
            double confidence = detections.get(i, 2)[0];
            if (confidence > THRESHOLD) {
                //int classId = (int)detections.get(i, 1)[0];

                int left   = (int)(detections.get(i, 3)[0] * cols);
                int top    = (int)(detections.get(i, 4)[0] * rows);
                int right  = (int)(detections.get(i, 5)[0] * cols);
                int bottom = (int)(detections.get(i, 6)[0] * rows);

                // Draw rectangle around detected object.
                //  给检测的对象画矩形框
                Imgproc.rectangle(frame, new Point(left, top), new Point(right, bottom),
                        new Scalar(0, 255, 0));
                //String label = classNames[classId] + ": " + confidence;
//                int[] baseLine = new int[1];
                //Size labelSize = Imgproc.getTextSize(label, Core.FONT_HERSHEY_SIMPLEX, 0.5, 1, baseLine);

                // Draw background for label.
                // 给标签画背景
//                Imgproc.rectangle(frame, new Point(left, top - labelSize.height),
//                        new Point(left + labelSize.width, top + baseLine[0]),
//                        new Scalar(255, 255, 255), Core.FILLED);
                // Write class name and confidence.
                // 添加类名和可信度
                //Imgproc.putText(frame, label, new Point(left, top),
                 //       Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 0, 0));
            }
        }
        return frame;


//        for (ObjectDetector detector : mObjectDetects) {
//            // 检测目标
//            Rect[] object = detector.detectObject(mGray, mObject);
//            for (Rect rect : object) {
//                Imgproc.rectangle(mRgba, rect.tl(), rect.br(), detector.getRectColor(), 3);
//                if (null!=onFaceDetectorListener){
//                    onFaceDetectorListener.onFace(mRgba,rect);
//                }
//            }
//        }

        //return mRgba;
    }

    public void setNet(Net net){
        this.net = net;
    }

    /**
     * 添加检测器
     *
     * @param detector 检测器
     */
    public synchronized void addDetector(ObjectDetector detector) {
        if (!mObjectDetects.contains(detector)) {
            mObjectDetects.add(detector);
        }
    }

    /**
     * 移除检测器
     *
     * @param detector 检测器
     */
    public synchronized void removeDetector(ObjectDetector detector) {
        if (mObjectDetects.contains(detector)) {
            mObjectDetects.remove(detector);
        }
    }


    public void setOnFaceDetectorListener(OnFaceDetectorListener onFaceDetectorListener){
        this.onFaceDetectorListener = onFaceDetectorListener;
    }

}

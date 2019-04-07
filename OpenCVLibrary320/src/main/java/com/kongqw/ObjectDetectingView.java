package com.kongqw;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.kongqw.listener.OnFaceDetectorListener;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Created by kqw on 2016/7/13.
 * RobotCameraView
 */
public class ObjectDetectingView extends BaseCameraView {

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

    public ObjectDetectingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        for (ObjectDetector detector : mObjectDetects) {
            // 检测目标
            Rect[] object = detector.detectObject(mGray, mObject);
            for (Rect rect : object) {
                Imgproc.rectangle(mRgba, rect.tl(), rect.br(), detector.getRectColor(), 3);
                if (null!=onFaceDetectorListener){
                    onFaceDetectorListener.onFace(mRgba,rect);
                }
            }
        }

        return mRgba;
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

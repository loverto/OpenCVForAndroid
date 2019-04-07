package com.yinlongfei;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.yinlongfei.listener.OnFaceDetectorListener;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static org.opencv.objdetect.Objdetect.CASCADE_DO_ROUGH_SEARCH;
import static org.opencv.objdetect.Objdetect.CASCADE_FIND_BIGGEST_OBJECT;
import static org.opencv.objdetect.Objdetect.CASCADE_DO_CANNY_PRUNING;
import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;


public class CameraFaceDetectionView extends BaseCameraView {
    private static final String TAG = "RobotCameraView";
    private OnFaceDetectorListener mOnFaceDetectorListener;
    //private OnOpenCVInitListener mOnOpenCVInitListener;
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private CascadeClassifier mJavaDetector;

    private int mAbsoluteFaceSize = 0;
    // 脸部占屏幕多大面积的时候开始识别
    private static final float RELATIVE_FACE_SIZE = 0.2f;

    @Override
    public void onOpenCVLoadSuccess() {
        //isLoadSuccess = true;
    }

    @Override
    public void onOpenCVLoadFail() {
        Log.i(TAG, "onOpenCVLoadFail: ");
    }

    public CameraFaceDetectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // 子线程（非UI线程）
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * RELATIVE_FACE_SIZE) > 0) {
                mAbsoluteFaceSize = Math.round(height * RELATIVE_FACE_SIZE);
            }
        }

        if (mJavaDetector != null) {
            MatOfRect faces = new MatOfRect();
            mJavaDetector.detectMultiScale(mGray, // 要检查的灰度图像
                    faces, // 检测到的人脸
                    1.1, // 表示在前后两次相继的扫描中，搜索窗口的比例系数。默认为1.1即每次搜索窗口依次扩大10%;
                    10, // 默认是3 控制误检测，表示默认几次重叠检测到人脸，才认为人脸存在
                    CASCADE_FIND_BIGGEST_OBJECT // 返回一张最大的人脸（无效？）
                            | CASCADE_SCALE_IMAGE
                            | CASCADE_DO_ROUGH_SEARCH
                            | CASCADE_DO_CANNY_PRUNING, //CV_HAAR_DO_CANNY_PRUNING ,// CV_HAAR_SCALE_IMAGE, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
                    new Size(mGray.width(), mGray.height()));

            // 检测到人脸
            Rect[] facesArray = faces.toArray();
            for (Rect aFacesArray : facesArray) {
                Imgproc.rectangle(mRgba, aFacesArray.tl(), aFacesArray.br(), FACE_RECT_COLOR, 3);
                if (null != mOnFaceDetectorListener) {
                    mOnFaceDetectorListener.onFace(mRgba, aFacesArray);
                }
            }
        }
        return mRgba;
    }

    /**
     * 添加人脸识别额监听
     *
     * @param listener 回调接口
     */
    public void setOnFaceDetectorListener(OnFaceDetectorListener listener) {
        mOnFaceDetectorListener = listener;
    }

}

package kong.qingwei.opencv320;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kongqw.CameraFaceDetectionView;
import com.kongqw.ObjectDetectingView;
import com.kongqw.ObjectDetector;
import com.kongqw.listener.OnFaceDetectorListener;
import com.kongqw.listener.OnOpenCVLoadListener;
import com.kongqw.uitl.FaceUtil;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;


public class ObjectDetectingActivity extends BaseActivity {

    private static final String TAG = "ObjectDetectingActivity";
    private ObjectDetectingView objectDetectingView;
    private ObjectDetector mFaceDetector;
    private CameraFaceDetectionView cameraFaceDetectionView;

    private static final String FACE1 = "face1";
    private static final String FACE2 = "face2";
    private static boolean isGettingFace = false;
    private Bitmap mBitmapFace1;
    private Bitmap mBitmapFace2;
    private ImageView mImageViewFace1;
    private ImageView mImageViewFace2;
    private TextView mCmpPic;
    private double cmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_object_detecting);

        objectDetectingView = (ObjectDetectingView)findViewById(R.id.photograph_view);

        cameraFaceDetectionView = (CameraFaceDetectionView) findViewById(R.id.cfdv);

        cameraFaceDetectionView.setOnFaceDetectorListener(new OnFaceDetectorListener() {
            @Override
            public void onFace(Mat mat, Rect rect) {
                if (isGettingFace) {
                    if (null == mBitmapFace1 || null != mBitmapFace2) {
                        mBitmapFace1 = null;
                        mBitmapFace2 = null;

                        // 保存人脸信息并显示
                        FaceUtil.saveImage(ObjectDetectingActivity.this, mat, rect, FACE1);
                        mBitmapFace1 = FaceUtil.getImage(ObjectDetectingActivity.this, FACE1);
                        cmp = 0.0d;
                    } else {
                        FaceUtil.saveImage(ObjectDetectingActivity.this, mat, rect, FACE2);
                        mBitmapFace2 = FaceUtil.getImage(ObjectDetectingActivity.this, FACE2);

                        // 计算相似度
                        cmp = FaceUtil.compare(ObjectDetectingActivity.this, FACE1, FACE2);
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
        cameraFaceDetectionView.loadOpenCV(getApplicationContext());
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

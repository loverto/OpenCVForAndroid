package kong.qingwei.opencv320;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import java.io.File;
import java.io.IOException;


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
                //if (isGettingFace) {
                    // 保存人脸信息并显示
                    FaceUtil.saveImage(ObjectDetectingActivity.this, mat, rect, FACE1);
                    mBitmapFace1 = FaceUtil.getImage(ObjectDetectingActivity.this, FACE1);
                    try {
                        String[] list = getAssets().list("image");
                        for (String file:list){
                            String absolutePath = getFileStreamPath(file).getAbsolutePath();
                            currentFile = absolutePath;
                            // 计算相似度
                            cmp = FaceUtil.compareAbs(ObjectDetectingActivity.this, FACE1, file);
                            Log.i(TAG, "onFace: 相似度 : " + cmp);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null == mBitmapFace1) {
                                mImageViewFace1.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                mImageViewFace1.setImageBitmap(mBitmapFace1);
                            }

                            mImageViewFace2.setImageURI(Uri.fromFile(new File(currentFile)));

                            mCmpPic.setText(String.format("相似度 :  %.2f", cmp) + "%");
                        }
                    });
//
//                    isGettingFace = false;
//                }
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

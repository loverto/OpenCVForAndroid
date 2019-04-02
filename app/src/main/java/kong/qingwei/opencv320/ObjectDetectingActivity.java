package kong.qingwei.opencv320;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kongqw.ObjectDetectingView;
import com.kongqw.ObjectDetector;
import com.kongqw.listener.OnOpenCVLoadListener;

import org.opencv.core.Scalar;

public class ObjectDetectingActivity extends BaseActivity {

    private ObjectDetectingView objectDetectingView;
    private ObjectDetector mFaceDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_object_detecting);

        objectDetectingView = (ObjectDetectingView) findViewById(R.id.photograph_view);

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

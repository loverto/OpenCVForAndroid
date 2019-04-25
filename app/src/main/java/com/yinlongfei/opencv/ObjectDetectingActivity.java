package com.yinlongfei.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yinlongfei.ObjectDetectingView;
import com.yinlongfei.ObjectDetector;
import com.yinlongfei.listener.OnFaceDetectorListener;
import com.yinlongfei.listener.OnOpenCVLoadListener;
import com.yinlongfei.opencv.api.ServiceAPI;
import com.yinlongfei.opencv.entity.Photo;
import com.yinlongfei.opencv.entity.User;
import com.yinlongfei.uitl.FaceUtil;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


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
    private Photo currentPhoto;
    private ImageView mImageViewFace1;
    private ImageView mImageViewFace2;
    private TextView mCmpPic;
    private Button button;
    private double cmp;
    private  TextToSpeech textToSpeech;

    private Map<String, Photo> hashMap = new HashMap<>();
    private List<String> images = new ArrayList<>();
    private Map<String, Photo> cacheMap = new HashMap();




    private byte[] readFile(Context context, String filename) {
        // 需要读取的文件，参数是文件的路径名加文件名
        //if (file ==null) {
            // 以字节流方法读取文件

            try {
                InputStream file = context.getAssets().open(filename);
                //fis = new FileInputStream(file);
                // 设置一个，每次 装载信息的容器
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // 开始读取数据
                int len = 0;// 每次读取到的数据的长度
                while ((len = file.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                    // append方法往sb对象里面添加数据
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();//刷新缓冲区
                file.close();
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

    private Bitmap readImage(Context context,String filename){
        InputStream open =null;

        try{
            open = context.getAssets().open(filename);
            // 获取图片宽高
            Bitmap bitmap = BitmapFactory.decodeStream(open);
            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }


    private Observable<User> account;
    private Observable<List<Photo>> serverAPIPhoto;

    private void init(){
        //ServiceAPI serverAPI = App.getServerAPI();
        account = App.getServerAPI().getAccount();

        serverAPIPhoto = App.getServerAPI().getPhoto(0, 100000);


        account.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        Log.d(TAG, "获取帐号信息成功"+user.toString());
                        App.getAccountManager().setAccount(user);
                        System.out.println(user);

                    }
                }).observeOn(Schedulers.io())
                .flatMap(new Function<User, ObservableSource<List<Photo>>>() {
                             @Override
                             public ObservableSource<List<Photo>> apply(User user) throws Exception {
                                 return serverAPIPhoto;
                             }
                         })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Photo>>() {
                            @Override
                            public void accept(List<Photo> photos) throws Exception {
                                //遍历图片，缓存图片
                                for (int i = 0; i < photos.size(); i++) {
                                    Photo photo = photos.get(i);
                                    Log.i(TAG, photo.toString());
                                    String originUrl = photo.getOriginUrl();
                                    String url = "http://th.minio.boyuanziben.cn" + originUrl;
                                    Glide.with(ObjectDetectingActivity.this)
                                            .load(url)
                                            .into(new SimpleTarget<Drawable>() {
                                                @Override
                                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                    Log.i(TAG,"资源准备好了，请挥霍");
                                                }
                                            });
                                }
                                // 照片不为空，开始处理
                                if (hashMap.isEmpty()|| hashMap.size()==0){
                                    // 用文件存储照片信息
                                    String fileName = "image/yinlongfei.jpg";
                                    saveFeatuer(fileName);
                                    Photo photo = new Photo();
                                    photo.setOriginUrl(fileName);
                                    String replace = fileName.replace("/", "");
                                    String replace1 = replace.replace(".", "");
                                    String featureUrl = replace1 + "featuer";
                                    photo.setFeatureUrl(featureUrl);
                                    photo.setName("殷龙飞");
                                    hashMap.put(featureUrl, photo);
                                    images.add(featureUrl);
                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                // 请求接口异常
                                Log.i(TAG, "信息异常："+throwable.getMessage());
                            }
                        });



    }

    /**
     * 保存图片中的特征
     * @param fileName 图片路径
     */
    private void saveFeatuer(String fileName) {
        // 获取图片宽高
        Bitmap bitmap = readImage(ObjectDetectingActivity.this,fileName);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //获取图片的宽高
        int mHeight = bitmap.getHeight();
        int mWidth = bitmap.getWidth();

        byte[] jpegData = readFile(ObjectDetectingActivity.this, fileName);

        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        //灰化图片
        Mat gray = mat.submat(0, mHeight, 0, mWidth);
        // 从初始化的图片中找出特征图片
        MatOfRect object = new MatOfRect();
        Rect[] rects = mFaceDetector.detectObject(gray, object);

        if (rects.length>0){
            Rect rect = rects[0];
            Mat mRgba = Imgcodecs.imdecode(new MatOfByte(jpegData), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
            Imgproc.rectangle(mRgba, rect.tl(), rect.br(), mFaceDetector.getRectColor(), 3);
            String replace = fileName.replace("/", "");
            String replace1 = replace.replace(".", "");
            String featureUrl = replace1 + "featuer";
            FaceUtil.saveImage(ObjectDetectingActivity.this,mat, rect, featureUrl);
            Log.i(TAG, "找到人脸，开始保存");
        }
    }

    private void speakOut(String text) {
        if (textToSpeech!= null && !textToSpeech.isSpeaking()){
//            Bundle bundle = new Bundle();
//            bundle.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME,1.0f);
            //朗读，注意这里三个参数的added in API level 4   四个参数的added in API level 21
//            mTextToSpeech.speak(mBinding.editText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null);
            textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,"");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_object_detecting);
        objectDetectingView = (ObjectDetectingView)findViewById(R.id.photograph_view);

        TextToSpeech.OnInitListener onInitListener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 设置音调,1.0是常规
                    textToSpeech.setPitch(1.0f);
                    //设定语速 ，默认1.0正常语速
                    textToSpeech.setSpeechRate(1.0f);
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(ObjectDetectingActivity.this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        };
        textToSpeech = new TextToSpeech(this, onInitListener);


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

//                        // 保存人脸信息并显示
                        FaceUtil.saveImage(ObjectDetectingActivity.this, mat, rect, FACE1);
                        //FaceUtil.saveImage(ObjectDetectingActivity.this,mat,"test");
                        mBitmapFace1 = FaceUtil.getImage(ObjectDetectingActivity.this, FACE1);
                        for (String file: images){
                            Photo photo = hashMap.get(file);
                            currentPhoto = photo;
                            String featureUrl = photo.getFeatureUrl();
                            String absolutePath = ObjectDetectingActivity.this.getFilesDir().getPath() + featureUrl+".jpg";
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null == mBitmapFace1) {
                                mImageViewFace1.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                mImageViewFace1.setImageBitmap(mBitmapFace1);
                            }
//                            if (null == mBitmapFace2) {
//                                mImageViewFace2.setImageResource(R.mipmap.ic_launcher);
//                            } else {
//                                mImageViewFace2.setImageBitmap(mBitmapFace2);
//                            }
//                            mCmpPic.setText(String.format("相似度 :  %.2f", cmp) + "%");
                            // 界面展示原图片
//                            File file = new File(currentFile);
//                            if (file.exists()){


//
//
// }
                            if (cmp > 20){
                                cacheMap.put(currentPhoto.getOriginUrl(), currentPhoto);
                            }

                            if(cacheMap.get(currentPhoto.getOriginUrl())!=null){
                                String text = "欢迎亲爱的%s同学";
                                String welcome = String.format(text, currentPhoto.getName());
                                //speakOut(welcome);
                                String text1 = String.format("相似度 :  %.2f", cmp) + "%";
                                mCmpPic.setText(welcome+text1);
//                                Bitmap bitmap = BitmapFactory.decodeFile(currentFile);
                                Bitmap bitmap = readImage(ObjectDetectingActivity.this, currentPhoto.getOriginUrl());
                                mImageViewFace2.setImageBitmap(bitmap);
                            }

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
                init();
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

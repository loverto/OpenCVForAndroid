package com.yinlongfei.uitl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.CvHistogram;

import java.io.File;

import static org.bytedeco.javacpp.helper.opencv_imgproc.cvCalcHist;
import static org.bytedeco.javacpp.opencv_core.CV_HIST_ARRAY;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_COMP_CORREL;
import static org.bytedeco.javacpp.opencv_imgproc.CV_COMP_INTERSECT;
import static org.bytedeco.javacpp.opencv_imgproc.cvCompareHist;
import static org.bytedeco.javacpp.opencv_imgproc.cvNormalizeHist;

import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * Created by kqw on 2016/9/9.
 * FaceUtil
 */
public final class FaceUtil {

    private static final String TAG = "FaceUtil";

    private FaceUtil() {
    }


    public static boolean saveImage(Context context,Mat image,String filename){
        return imwrite(getFilePath(context,filename),image);
    }

    /**
     * 特征保存
     *
     * @param context  Context
     * @param image    Mat
     * @param rect     人脸信息
     * @param fileName 文件名字
     * @return 保存是否成功
     */
    public static boolean saveImage(Context context, Mat image, Rect rect, String fileName) {
        // 原图置灰
        Mat grayMat = new Mat();
        Imgproc.cvtColor(image, grayMat, Imgproc.COLOR_BGR2GRAY);
        // 把检测到的人脸重新定义大小后保存成文件
        Mat sub = grayMat.submat(rect);
        Mat mat = new Mat();
        Size size = new Size(100, 100);
        Imgproc.resize(sub, mat, size);
        Log.i(TAG, "文件保存路径"+fileName);
        return         imwrite(getFilePath(context, fileName), mat);
    }

    /**
     * 删除特征
     *
     * @param context  Context
     * @param fileName 特征文件
     * @return 是否删除成功
     */
    public static boolean deleteImage(Context context, String fileName) {
        // 文件名不能为空
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        // 文件路径不能为空
        String path = getFilePath(context, fileName);
        if (path != null) {
            File file = new File(path);
            return file.exists() && file.delete();
        } else {
            return false;
        }
    }

    /**
     * 提取特征
     *
     * @param context  Context
     * @param fileName 文件名
     * @return 特征图片
     */
    public static Bitmap getImage(Context context, String fileName) {
        String filePath = getFilePath(context, fileName);
        if (TextUtils.isEmpty(filePath)) {
            return null;
        } else {
            return BitmapFactory.decodeFile(filePath);
        }
    }

    /**
     * 特征对比
     *  1。分别用facedetect功能将两张图片中的人脸检测出来
     *
     *  2。将人脸部分的图片剪切出来，存到两张只有人脸的图片里。
     *
     *  3。将这两张人脸图片转换成单通道的图像
     *
     *  4。使用直方图比较这两张单通道的人脸图像，得出相似度。
     *
     * @param context   Context
     * @param fileName1 人脸特征
     * @param fileName2 人脸特征
     * @return 相似度
     */
    public static double compare(Context context, String fileName1, String fileName2) {
        try {
            // 获取第一章照片
            String pathFile1 = getFilePath(context, fileName1);
            // 获取第二章照片
            String pathFile2 = getFilePath(context, fileName2);
            // 用Opencv加载图片，灰色
            IplImage image1 = cvLoadImage(pathFile1, org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            // 用Opencv加载图片
            IplImage image2 = cvLoadImage(pathFile2, org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            if (null == image1 || null == image2) {
                return -1;
            }

            int l_bins = 256;
            int hist_size[] = {l_bins};
            float v_ranges[] = {0, 255};
            float ranges[][] = {v_ranges};

            IplImage imageArr1[] = {image1};
            IplImage imageArr2[] = {image2};
            // cv直方图
            CvHistogram Histogram1 = CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            // cv直方图
            CvHistogram Histogram2 = CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            // cv 计算直方图
            cvCalcHist(imageArr1, Histogram1, 0, null);
            cvCalcHist(imageArr2, Histogram2, 0, null);
            // cv 规范化直方图
            cvNormalizeHist(Histogram1, 100.0);
            cvNormalizeHist(Histogram2, 100.0);
            // 参考：http://blog.csdn.net/nicebooks/article/details/8175002
            double c1 = cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL) * 100;
            double c2 = cvCompareHist(Histogram1, Histogram2, CV_COMP_INTERSECT);
//            Log.i(TAG, "compare: ----------------------------");
//            Log.i(TAG, "compare: c1 = " + c1);
//            Log.i(TAG, "compare: c2 = " + c2);
//            Log.i(TAG, "compare: 平均值 = " + ((c1 + c2) / 2));
//            Log.i(TAG, "compare: ----------------------------");
            return (c1 + c2) / 2;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 特征对比
     *  1。分别用facedetect功能将两张图片中的人脸检测出来
     *
     *  2。将人脸部分的图片剪切出来，存到两张只有人脸的图片里。
     *
     *  3。将这两张人脸图片转换成单通道的图像
     *
     *  4。使用直方图比较这两张单通道的人脸图像，得出相似度。
     *
     * @param context   Context
     * @param absFileName 人脸特征
     * @param fileName2 人脸特征
     * @return 相似度
     */
    public static double compareAbs(Context context, String absFileName, String fileName2) {
        try {
//            // 获取第一章照片
//            String pathFile1 = getFilePath(context, absFileName);
            // 获取第二章照片
            String pathFile2 = getFilePath(context, fileName2);
            // 用Opencv加载图片，灰色
            IplImage image1 = cvLoadImage(absFileName, org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            // 用Opencv加载图片
            IplImage image2 = cvLoadImage(pathFile2, org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            if (null == image1 || null == image2) {
                return -1;
            }

            int l_bins = 256;
            int hist_size[] = {l_bins};
            float v_ranges[] = {0, 255};
            float ranges[][] = {v_ranges};

            IplImage imageArr1[] = {image1};
            IplImage imageArr2[] = {image2};
            // cv直方图
            CvHistogram Histogram1 = CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            // cv直方图
            CvHistogram Histogram2 = CvHistogram.create(1, hist_size, CV_HIST_ARRAY, ranges, 1);
            // cv 计算直方图
            cvCalcHist(imageArr1, Histogram1, 0, null);
            cvCalcHist(imageArr2, Histogram2, 0, null);
            // cv 规范化直方图
            cvNormalizeHist(Histogram1, 100.0);
            cvNormalizeHist(Histogram2, 100.0);
            // 参考：http://blog.csdn.net/nicebooks/article/details/8175002
            double c1 = cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL) * 100;
            double c2 = cvCompareHist(Histogram1, Histogram2, CV_COMP_INTERSECT);
//            Log.i(TAG, "compare: ----------------------------");
//            Log.i(TAG, "compare: c1 = " + c1);
//            Log.i(TAG, "compare: c2 = " + c2);
//            Log.i(TAG, "compare: 平均值 = " + ((c1 + c2) / 2));
//            Log.i(TAG, "compare: ----------------------------");
            return (c1 + c2) / 2;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取人脸特征路径
     *
     * @param fileName 人脸特征的图片的名字
     * @return 路径
     */
    private static String getFilePath(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        // 内存路径
        return context.getApplicationContext().getFilesDir().getPath() + fileName + ".jpg";
        // 内存卡路径 需要SD卡读取权限
        // return Environment.getExternalStorageDirectory() + "/FaceDetect/" + fileName + ".jpg";
    }
}
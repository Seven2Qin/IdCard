package com.seven.opencvdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.MORPH_CROSS;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    Button btnOrigin;
    Button btnGray;
    Button btnBinary;
    Button btnCanny;
    Button btnErode;
    Button btnCut;
    Button btnDist;

    Bitmap srcBitmap;
    Bitmap grayBitmap;
    Bitmap cannyBitmap;
    Bitmap binaryBitmap;
    Bitmap erodeBitmap;
    Bitmap cutBitmap;
    ImageView img;
    TextView tvResult;

    Mat rgbMat;
    Mat grayMat;
    Mat binaryMat;
    Mat cannyMat;
    Mat erodeMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView() {
        img = findViewById(R.id.img);
        tvResult = findViewById(R.id.tv_result);

        btnOrigin = findViewById(R.id.btn_origin);
        btnGray = findViewById(R.id.btn_gray);
        btnBinary = findViewById(R.id.btn_binary);
        btnCanny = findViewById(R.id.btn_canny);
        btnErode = findViewById(R.id.btn_erode);
        btnCut = findViewById(R.id.btn_cut);
        btnDist = findViewById(R.id.btn_dist);

        btnOrigin.setOnClickListener(this);
        btnGray.setOnClickListener(this);
        btnBinary.setOnClickListener(this);
        btnCanny.setOnClickListener(this);
        btnErode.setOnClickListener(this);
        btnCut.setOnClickListener(this);
        btnDist.setOnClickListener(this);

        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sfz1);

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (rgbMat == null) {
            rgbMat = new Mat();
        }
        if (grayMat == null) {
            grayMat = new Mat();
        }
        if (binaryMat == null) {
            binaryMat = new Mat();
        }
        if (cannyMat == null) {
            cannyMat = new Mat();
        }
        if (erodeMat == null) {
            erodeMat = new Mat();
        }

        //临时测试
        dealBitmap();

        switch (v.getId()) {
            case R.id.btn_origin:
                img.setImageBitmap(srcBitmap);
                break;
            case R.id.btn_gray:
                img2Gray();
                img.setImageBitmap(grayBitmap);
                break;
            case R.id.btn_binary:
                gray2Binary();
                img.setImageBitmap(binaryBitmap);
                break;
            case R.id.btn_canny:
                img2Canny();
                img.setImageBitmap(cannyBitmap);
                break;
            case R.id.btn_erode:
                erode();
                img.setImageBitmap(erodeBitmap);
                break;
            case R.id.btn_cut:
                cutImg();
                img.setImageBitmap(cutBitmap);
                break;
            case R.id.btn_dist:
                getResult(cutBitmap);
                break;
        }
    }

    private void dealBitmap() {

        if (grayBitmap == null) {
            img2Gray();
        }
        if (binaryBitmap == null) {
            gray2Binary();
        }
        if (cannyBitmap == null) {
            img2Canny();
        }
        if (erodeBitmap == null) {
            erode();
        }
        if (cutBitmap == null) {
            cutImg();
        }
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }

        }
    };

    //灰度化
    public void img2Gray() {
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
    }



    //二值化
    public void gray2Binary() {

        if (binaryBitmap != null) {
            return;
        }

        Imgproc.threshold(grayMat, binaryMat, 165, 255, Imgproc.THRESH_BINARY);//二值化
        binaryBitmap = Bitmap.createBitmap(binaryMat.cols(), binaryMat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(binaryMat, binaryBitmap);

//        //得到图形的宽度和长度
//        int width = grayBitmap.getWidth();
//        int height = grayBitmap.getHeight();
//        //创建二值化图像
//        binaryBitmap = grayBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        //依次循环，对图像的像素进行处理
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                //得到当前像素的值
//                int col = binaryBitmap.getPixel(i, j);
//                //得到alpha通道的值
//                int alpha = col & 0xFF000000;
//                //得到图像的像素RGB的值
//                int red = (col & 0x00FF0000) >> 16;
//                int green = (col & 0x0000FF00) >> 8;
//                int blue = (col & 0x000000FF);
//                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
//                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
//                //对图像进行二值化处理
//                if (gray <= 150) {
//                    gray = 0;
//                } else {
//                    gray = 255;
//                }
//                // 新的ARGB
//                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
//                //设置新图像的当前像素值
//                binaryBitmap.setPixel(i, j, newColor);
//            }
//        }
    }

    //边缘检测
    public void img2Canny() {
        cannyBitmap = Bitmap.createBitmap(grayBitmap.getWidth(), grayBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(grayBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.Canny(rgbMat, cannyMat, 80, 90);
        //Imgproc.cvtColor(grayMat, cannyMat, Imgproc.COLOR_GRAY2BGRA);//rgbMat to gray grayMat
        Utils.matToBitmap(cannyMat, cannyBitmap); //convert mat to bitmap
    }

    private void erode() {
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20));
        //腐蚀
        Imgproc.erode(binaryMat, erodeMat, element, new Point(-1, -1), 1);
        erodeBitmap = Bitmap.createBitmap(binaryMat.cols(), binaryMat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(erodeMat, erodeBitmap);
    }


    //裁剪
    private void cutImg() {

        Mat element = new Mat(20, 20, CV_8U, new Scalar(1));
        Imgproc.morphologyEx(erodeMat, element, MORPH_CROSS, element);//闭运算

        /**
         * 轮廓提取()
         */
        ArrayList<MatOfPoint> contoursList = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(element, contoursList, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);
        Mat resultMat = Mat.zeros(element.size(), CV_8U);
        Imgproc.drawContours(resultMat, contoursList, -1, new Scalar(255, 0, 255));
        Mat effective = new Mat(); //身份证位置
        //外包矩形区域
        double maxWidth = 0;
        MatOfPoint maxContour = null;
        for (int i = 0; i < contoursList.size(); i++) {
            Rect rect = Imgproc.boundingRect(contoursList.get(i));
            if (rect.width == srcBitmap.getWidth()) {//过滤原图的宽度影响
                continue;
            }
            double width = rect.width;
            if (width > maxWidth) {
                maxWidth = width;
                maxContour = contoursList.get(i);
            }
        }

        Rect rect = Imgproc.boundingRect(maxContour);
        //Imgproc.rectangle(resultMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 255), 1);
        effective = new Mat(grayMat, new Rect(rect.x - 4, rect.y - 4, rect.width + 8, rect.height + 8));


        if (effective.cols() > 0 && effective.rows() > 0) {
            cutBitmap = Bitmap.createBitmap(effective.cols(), effective.rows(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(effective, cutBitmap);
        }

    }

    /**
     * 对要识别的图像进行识别
     *
     * @param bmp 要识别的bitmap
     * @return
     */
    public String getResult(Bitmap bmp) {
        String result = "";
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(Environment.getExternalStorageDirectory() + "/tesseract", "eng");
        Bitmap bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        baseApi.setImage(bitmap);
//        baseApi.setVariable("tessedit_char_whitelist", "0123456789x");
        result = baseApi.getUTF8Text();
        Log.i("seven", "result:" + result);
        //result = result.replaceAll(" ", "");
//        if (result.equals("") || result.length() <= 16 || result.length() >= 20) { //允许4个字符的误差
//            result = null;
//        }
        baseApi.end();
        tvResult.setText(result);
        return result;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
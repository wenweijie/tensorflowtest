
package org.tensorflow.demo.fragment;

import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.tensorflow.demo.classifier.Classifier;
import org.tensorflow.demo.classifier.MnistClassifier;
import org.tensorflow.demo.classifier.TensorflowClassifier;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.media.Image;
import android.media.ImageReader;
import android.media.Image.Plane;
import android.media.ImageReader.OnImageAvailableListener;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Trace;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class ImageParserListener implements OnImageAvailableListener {
    private static final String TAG = "ImageParserListener";
    private static final Logger LOGGER = new Logger();

    private static final boolean SAVE_PREVIEW_BITMAP = true;

    // All Classifier Param
    // ModelNum is Model's number
    // 0 is TensorflowClassifier
    // 1 is MnistClassifier / If test 1 mean TensorflowClassifier2
    public class Model {
        public static final int TENSORFLOW_DEMO = 0;
        public static final int MNIST_DEMO = 1;
        public static final int OPENCV_DEMO = 2;
    }

    private TensorflowClassifier tfClassifier;
    private MnistClassifier mnistClassifier;
    

    private int ModelNum = 0;
    private int inputSize = 100;
    private int rotation = 0;

    private int previewWidth = 0;
    private int previewHeight = 0;
    private byte[][] yuvBytes;
    private int[] rgbBytes = null;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;

    private boolean computing = false;
    private Handler handler;
    private Integer sensorOrientation;
    private RecognitionScoreView mScoreView;
    private RectView mRectView;

    private Context mContext;

    public void initialize(int ModelNum, final AssetManager assetManager,
            final RecognitionScoreView mScoreView,final RectView mRectView, final Handler handler,
            final Integer sensorOrientation, final InputStream in, Context context
            ) {
        this.ModelNum = ModelNum;
        Assert.assertNotNull(sensorOrientation);
        this.mScoreView = mScoreView;
        this.mRectView=mRectView;
        this.handler = handler;
        this.sensorOrientation = sensorOrientation;
        this.mContext = context;
        FragmentActivity mFragmentActivity = (FragmentActivity)context;
        this.rotation = mFragmentActivity.getWindowManager().getDefaultDisplay().getRotation();

        switch (this.ModelNum) {
            case Model.TENSORFLOW_DEMO: {
                tfClassifier = new TensorflowClassifier();
                inputSize = 224;
                tfClassifier
                        .initializeTensorflow(
                                assetManager,
                                "file:///android_asset/tensorflow_inception_graph.pb",
                                "file:///android_asset/imagenet_comp_graph_label_strings.txt",
                                1001, 224, 117, 1, "input:0", "output:0");
                break;
            }
            case Model.MNIST_DEMO: {
                // There will be other model
                // But testing is used TensorflowClassifier
                mnistClassifier = new MnistClassifier();
                inputSize = 28;
                mnistClassifier.initializeMnist(assetManager,
                        "file:///android_asset/regen-graph-easy.pb", 10, inputSize);
                break;
            }
            case Model.OPENCV_DEMO: {
               
                break;
            }

            default:
                break;
        }

    }

    private void drawResizedBitmap(final Bitmap src, final Bitmap dst) {
        // check width = height
        Assert.assertEquals(dst.getWidth(), dst.getHeight());
        final float minDim = Math.min(src.getWidth(), src.getHeight());
        
        final Matrix matrix = new Matrix();

        // We only want the center square out of the original rectangle.
        final float translateX = -Math.max(0, (src.getWidth() - minDim) / 2);
        final float translateY = -Math.max(0, (src.getHeight() - minDim) / 2);
        matrix.preTranslate(translateX, translateY);
        
        
        final float scaleFactor = dst.getHeight() / minDim;
        matrix.postScale(scaleFactor, scaleFactor);

        Log.e(TAG+"tx", String.valueOf(translateX));//---  -80
        Log.e(TAG+"ty", String.valueOf(translateY));//---  0
        Log.e(TAG+"mindim", String.valueOf(minDim));//---- 320
        Log.e(TAG+"scaleFactor", String.valueOf(scaleFactor));//-- 0.7
        
        // Rotate around the center if necessary.
        if (sensorOrientation != 0) {
            matrix.postTranslate(-dst.getWidth() / 2.0f,
                    -dst.getHeight() / 2.0f);
            matrix.postRotate(sensorOrientation);
            matrix.postTranslate(dst.getWidth() / 2.0f, dst.getHeight() / 2.0f);
        }

        final Canvas canvas = new Canvas(dst);
        //matrix.
        canvas.drawBitmap(src, matrix, null);
        Log.e("ip-----srcw",String.valueOf(src.getWidth()));
        Log.e("ip-----srch",String.valueOf(src.getHeight()));
        Log.e("ip-----dstw",String.valueOf(dst.getWidth()));
        Log.e("ip-----dsth",String.valueOf(dst.getHeight()));
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        // TODO Auto-generated method stub
        Image image = null;
        try {
            image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            // No mutex needed as this method is not reentrant.
            if (computing) {
                image.close();
                return;
            }
            computing = true;

            Trace.beginSection("imageAvailable");

            final Plane[] planes = image.getPlanes();

            // Initialize the storage bitmaps once when the resolution is known.
            if (previewWidth != image.getWidth()
                    || previewHeight != image.getHeight()) {
                previewWidth = image.getWidth();
                previewHeight = image.getHeight();

                LOGGER.i("Initializing at size %dx%d", previewWidth,
                        previewHeight);
                rgbBytes = new int[previewWidth * previewHeight];
                rgbFrameBitmap = Bitmap.createBitmap(previewWidth,
                        previewHeight, Config.ARGB_8888);
                croppedBitmap = Bitmap.createBitmap(inputSize, inputSize,
                        Config.ARGB_8888);
                Log.e("--------INPUTSIZE-----", String.valueOf(inputSize));
                Log.e(TAG+"w", String.valueOf(image.getWidth()));
                Log.e(TAG+"h", String.valueOf(image.getHeight()));

                yuvBytes = new byte[planes.length][];
                for (int i = 0; i < planes.length; ++i) {
                    yuvBytes[i] = new byte[planes[i].getBuffer().capacity()];
                }
            }

            for (int i = 0; i < planes.length; ++i) {
                planes[i].getBuffer().get(yuvBytes[i]);
            }

            final int yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();
            ImageUtils.convertYUV420ToARGB8888(yuvBytes[0], yuvBytes[1],
                    yuvBytes[2], rgbBytes, previewWidth, previewHeight,
                    yRowStride, uvRowStride, uvPixelStride, false);

            image.close();
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            LOGGER.e(e, "Exception!");
            Trace.endSection();
            return;
        }

        rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth,
                previewHeight);

        drawResizedBitmap(rgbFrameBitmap, croppedBitmap);
        // For examining the actual TF input.

        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(rgbFrameBitmap);
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                switch (ModelNum) {
                    case Model.TENSORFLOW_DEMO: {
                        final List<Classifier.Recognition> results = tfClassifier
                                .recognizeImage(croppedBitmap);

                        LOGGER.v("%d results", results.size());

                        for (final Classifier.Recognition result : results) {
                            LOGGER.v("Result: " + result.getTitle());
                        }
                        mScoreView.setResults(results, ModelNum);
                        mRectView.setResults(results, previewWidth ,previewHeight);
                        break;
                    }
                    case Model.MNIST_DEMO: {
                        final List<Classifier.Recognition> results = mnistClassifier
                                .recognizeImage(croppedBitmap);

                        LOGGER.v("%d results", results.size());

                        for (final Classifier.Recognition result : results) {
                            LOGGER.v("Result: " + result.getTitle());
                        }
                        mScoreView.setResults(results, ModelNum);
                        break;
                    }
                    case Model.OPENCV_DEMO: {
                        
                       
                        break;
                    }
                    default:
                        break;
                }
                computing = false;
            }
        });

        Trace.endSection();
    }

    public void clean() {
        previewWidth = 0;
        previewHeight = 0;
        rgbBytes = null;
        rgbFrameBitmap = null;
        croppedBitmap = null;
        computing = false;
    }

    public void closeClassifier() {

    }
}

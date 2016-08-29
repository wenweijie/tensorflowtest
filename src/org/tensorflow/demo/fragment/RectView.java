
package org.tensorflow.demo.fragment;

import java.util.List;

import org.tensorflow.demo.classifier.Classifier.Recognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RectView extends View {

    private static final String TAG = "RectView";
    private List<Recognition> mResults;
    private Paint mPaint, mPaint2, mPaint3;
    private int mCameraWidth = 0, mCameraHeight = 0;
    private float mScaleW = 1.0f, mScaleH = 1.0f;
    private float mLeft = 1.0f;
    private float mRight = 1.0f;
    private float mTop = 1.0f;
    private float mBottom = 1.0f;
    private RectF mRectF;

    // override view should have 2 pare:context and set
    public RectView(final Context context, final AttributeSet set) {
        super(context, set);
        // set paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(3.0f);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setColor(Color.RED);
        mPaint2.setStrokeWidth(3.0f);
        mPaint2.setStyle(Paint.Style.STROKE);

        mPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint3.setColor(Color.GREEN);
        mPaint3.setStrokeWidth(3.0f);
        mPaint3.setStyle(Paint.Style.STROKE);

    }

    public void setResults(final List<Recognition> results, int camerawidth, int cameraheight) {
        mResults = results;
        mCameraWidth = camerawidth;
        mCameraHeight = cameraheight;
        postInvalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(final Canvas canvas) {
        if (mResults != null) {
            mScaleW = (float) getWidth() / (float) mCameraHeight; // 720/320
            mScaleH = (float) getHeight() / (float) mCameraWidth; // 1200/480          

            Log.e(TAG+"GW", String.valueOf(getWidth()));// -720
            Log.e(TAG+"GH", String.valueOf(getHeight()));// ---------1200
            Log.e(TAG+"CW", String.valueOf(mCameraWidth));// -480
            Log.e(TAG+"CH", String.valueOf(mCameraHeight));//-320
            Log.e(TAG, "--------------------------");

            // if (mResults.size() > 0) {
            // for (Recognition recog : mResults) {
            //
            // mLeft = recog.getLocation().top * mScaleW;
            // mRight = recog.getLocation().bottom * mScaleW;
            // mTop = (recog.getLocation().right == 0) ?
            // recog.getLocation().right
            // : (mCameraWidth - recog.getLocation().right) * mScaleH;
            // mBottom = (recog.getLocation().left == 0) ?
            // recog.getLocation().left
            // : (mCameraWidth - recog.getLocation().left) * mScaleH;
            //
            // mRectF = new RectF(mLeft, (mTop == 0) ? mTop : getHeight() -
            // mBottom, mRight,
            // (mBottom == 0) ? mBottom : getHeight() - mTop);
            //
            // canvas.drawRect(mRectF, mPaint);
            // }
            //
            // }
            if (mResults.size() > 0) {

                for (Recognition r : mResults) {
                    float lx=((float)getWidth()-mScaleW*224)/2;
                    float ly=((float)getHeight()-mScaleW*224)/2;
                    mRectF=new RectF(lx, ly, lx+mScaleW*224, ly+mScaleW*224);

                    canvas.drawRect(mRectF, mPaint);
                }

            }
            

        }
    }
}

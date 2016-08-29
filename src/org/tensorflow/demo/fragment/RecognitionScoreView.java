/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.demo.fragment;

import android.R.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import org.tensorflow.demo.classifier.Classifier.Recognition;

import java.util.List;

public class RecognitionScoreView extends View {
    private static final String TAG = "RecognitionScoreView";
    private static final float TEXT_SIZE_DIP = 24;
    private List<Recognition> results;
    private int modelNum;
    private final float textSizePx;
    private final Paint fgPaint;
    private final Paint bgPaint;
   

    public RecognitionScoreView(final Context context, final AttributeSet set) {
        super(context, set);

        textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        fgPaint = new Paint();
        fgPaint.setTextSize(textSizePx);

        bgPaint = new Paint();
        bgPaint.setColor(0xcc4285f4);

    }

    public void setResults(final List<Recognition> results) {
        this.results = results;
        this.modelNum = 0; // default is 0 : tensorflow demo
        postInvalidate();
    }

    public void setResults(final List<Recognition> results, final int modelNum) {
        this.results = results;
        this.modelNum = modelNum; // default is 0 : tensorflow demo
        postInvalidate();
    }

    public void setModelNum(final int modelNum) {
        this.modelNum = modelNum;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(final Canvas canvas) {
        final int x = 10;
        int y = (int) (fgPaint.getTextSize() * 1.5f);

        canvas.drawPaint(bgPaint);

        if (results != null) {
            switch (this.modelNum) {
                case 0: {
                    for (final Recognition recog : results) {
                        canvas.drawText(
                                recog.getTitle() + ": " + recog.getConfidence(), x,
                                y, fgPaint);
                        y += fgPaint.getTextSize() * 1.5f;
                    }
                    break;
                }
                case 1: {
                    Recognition recog = results.get(0);
                    canvas.drawText(recog.getTitle(), x, y, fgPaint);
                    y += fgPaint.getTextSize() * 1.5f;
                    break;
                }
                case 2: {
                    canvas.drawText(results.get(0).getTitle(), x, y, fgPaint);

                    break;
                }
                default:
                    break;
            }

        }
    }
}

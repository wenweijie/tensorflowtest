package org.tensorflow.demo.classifier;

import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Trace;

/**
 * JNI wrapper class for the MNIST Tensorflow native code.
 */
public class MnistClassifier implements Classifier {
	private static final String TAG = "MNISTClassifier";
		
	private int modelsize = 28;

	// jni native methods.
	public native int initializeMnist(AssetManager assetManager,
			String model, int numClasses, int inputSize);	
	
	public native int detectDigit(int[] pixels);

	static {
		System.loadLibrary("tensorflow_demo");
	}
	
	public int[] getPixelData(Bitmap bitmap) {
		// Get 28x28 pixel data from bitmap
		int[] pixels = new int[28 * 28];
		bitmap.getPixels(pixels, 0, 28, 0, 0, 28, 28);

		int[] retPixels = new int[pixels.length];
		for (int i = 0; i < pixels.length; ++i) {
			// Set 0 for white and 255 for black pixel
			retPixels[i] = pixels[i] & 0xff;
		}
		return retPixels;
	}

	@Override
	public List<Recognition> recognizeImage(Bitmap bitmap) {
		// Log this method so that it can be analyzed with systrace.
		Trace.beginSection("Recognize");
		final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
		int result = detectDigit(getPixelData(bitmap));
		recognitions.add(new Recognition(null, String.valueOf(result), 0.0f, null));
		
		Trace.endSection();
		return recognitions;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}

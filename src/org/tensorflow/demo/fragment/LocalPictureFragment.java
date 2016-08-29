package org.tensorflow.demo.fragment;

import java.util.List;

import org.tensorflow.demo.R;
import org.tensorflow.demo.classifier.*;
import org.tensorflow.demo.classifier.Classifier.Recognition;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class LocalPictureFragment extends Fragment {
    private static final String TAG="LocalPictureFragment";
	private static final int MNIST_NUM_CLASSES = 10;
	private static final int MNIST_INPUT_SIZE = 28;
	// private static final int MODEL_NUM = 2;

	private static final String MNIST_MODEL_FILE = "file:///android_asset/regen-graph-easy.pb";
	private static final MnistClassifier MNIST_CLASSIFIER = new MnistClassifier();

	private View mLocalpictureview;
	private Button mChoose, mCrop, mRecog;
	private ImageView mImageview, mImageview2;
	private TextView mResultview;
	private static String mPath = null;
	private boolean mIsVisible;

	public static LocalPictureFragment newInstance() {
		return new LocalPictureFragment();
	}

	/*
	 * @Override public void onActivityCreated(Bundle savedInstanceState) {
	 * super.onActivityCreated(savedInstanceState); }
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mLocalpictureview = inflater.inflate(R.layout.local_picture_fragment,
				container, false);
		initialize();

		mChoose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, 1);
			}
		});

		mCrop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mImageview.setDrawingCacheEnabled(true);
				Bitmap bp = Bitmap.createBitmap(mImageview.getDrawingCache());
				Bitmap dp = Bitmap.createBitmap(112, 112, Config.ARGB_8888);
				mImageview.setDrawingCacheEnabled(false);
				drawsize(bp, dp);
				mImageview2.setImageBitmap(dp);
			}
		});

		mRecog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPath != null) {
					Bitmap croppedBitmap = BitmapFactory.decodeFile(mPath);
					Log.e("SHIBIEsize",
							String.valueOf(croppedBitmap.getRowBytes()));
					final List<Classifier.Recognition> results = MNIST_CLASSIFIER
							.recognizeImage(croppedBitmap);
					Recognition recog = results.get(0);
					mResultview.setText("result£º " + recog.getTitle());
				}
			}
		});

		return mLocalpictureview;
	}

	public void initialize() {
		mChoose = (Button) mLocalpictureview.findViewById(R.id.choose);
		mCrop = (Button) mLocalpictureview.findViewById(R.id.crop);
		mRecog = (Button) mLocalpictureview.findViewById(R.id.recog);
		mImageview = (ImageView) mLocalpictureview.findViewById(R.id.iv);
		mImageview2 = (ImageView) mLocalpictureview.findViewById(R.id.iv2);
		mResultview = (TextView) mLocalpictureview.findViewById(R.id.result);
		MNIST_CLASSIFIER.initializeMnist(getActivity().getAssets(),
				MNIST_MODEL_FILE, MNIST_NUM_CLASSES, MNIST_INPUT_SIZE);
	}

	@Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            mIsVisible=true;
            Log.e(TAG, "--------------mIsVisible=true");
            onVisible();
        }else{
            mIsVisible=false;
            Log.e(TAG, "--------------mIsVisible=false");
            onInvisible();
        }
    }
    //This view can be seen
    public void onVisible(){
      // getActivity().Toast.makeText(getActivity(), "see11111111111", Toast.LENGTH_SHORT);
        Log.e(TAG, "--------------visible");
    }
    //This view cant be seen
    public void onInvisible(){
      //  Toast.makeText(getActivity(), "dis11111111111", Toast.LENGTH_SHORT);
        //onPause();
        //cameraDevice.close();
        Log.e(TAG, "--------------INvisible");
    }
    
	public void drawsize(Bitmap src, Bitmap dst) {
		final float minDim = Math.min(src.getWidth(), src.getHeight());

		final Matrix matrix = new Matrix();

		// We only want the center square out of the original rectangle.
		final float translateX = -Math.max(0,
				(src.getWidth() - dst.getWidth()) / 2);
		final float translateY = -Math.max(0,
				(src.getHeight() - dst.getHeight()) / 2);
		// matrix.preTranslate(translateX, translateY);

		final float scaleFactor = dst.getHeight() / minDim;

		matrix.setScale(1 / scaleFactor, 1 / scaleFactor);
		matrix.preTranslate(translateX, translateY);

		Canvas canvas = new Canvas(dst);
		canvas.drawBitmap(src, matrix, null);

	}

	@SuppressWarnings("static-access")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == 1) {
				Uri uri = data.getData();
				mPath = uri.getPath();
				if (uri.getScheme().toString().equalsIgnoreCase("content")) {
					Cursor cursor = getActivity().getContentResolver().query(
							uri, null, null, null, null);
					cursor.moveToFirst();
					mPath = cursor.getString(1);
					Log.e("---------uri2", mPath);
					cursor.close();
				}

				BitmapFactory.Options option = new BitmapFactory.Options();
				option.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(mPath, option);
				int h = option.outHeight;
				int w = option.outWidth;
				int be = 1;
				int tow = 240;
				int toh = 320;
				Log.e("----------hhhhhhhhh", String.valueOf(h));
				Log.e("----------wwwwwwwww", String.valueOf(w));
				if (h > toh || w > tow) {
					if (w / tow > h / toh && w > tow) {
						be = (int) w / tow;
					} else if (w / tow < h / toh && h > toh) {
						be = (int) h / toh;
					}
				}
				option.inSampleSize = be;
				option.inJustDecodeBounds = false;

				Bitmap b = BitmapFactory.decodeFile(mPath, option);

				mImageview.setImageBitmap(b);

			}
		}
	}

}

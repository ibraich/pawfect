package com.example.pawfect;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.appinterface.R;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

public class TensorFlowImageClassifier implements Classifier{
    private static final int MAX_RESULTS = 3;
    private static final float THRESHOLD = 0.1f;
    private static final String MODEL_FILE = "stripped.pb";
    private static final int inputSize = 299;
    private static final int imageMean = 128;
    private static final float imageStd = 128;
    private static final String outputName = "final_result";
    // Config values.
    private static final String inputName = "Mul";

    // Pre-allocated buffers.
    private Vector<String> labels = new Vector<String>();
    private int[] intValues;
    private float[] floatValues;
    private float[] outputs;
    private String[] outputNames;
    private TensorFlowInferenceInterface inferenceInterface;
    private Context context;

    List<Recognition> recognitions;


    public TensorFlowImageClassifier(Context context, Bitmap bitmap) {
        this.context = context;

        Collections.addAll(labels, context.getResources().getStringArray(R.array.breeds_array));
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);
        final Operation operation = inferenceInterface.graphOperation(outputName);
        final int numClasses = (int) operation.output(0).shape().size(1);

        outputNames = new String[]{outputName};
        intValues = new int[inputSize * inputSize];
        floatValues = new float[inputSize * inputSize * 3];
        outputs = new float[numClasses];
        recognitions = recognizeImage(bitmap);

    }
    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        bitmap = resizeCropAndRotate(bitmap, getOrientation(context, null));

        // Preprocess the image data from 0-255 int to normalized float based on the provided parameters.
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3 + 0] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
            floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            floatValues[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
        }

        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 3);

        inferenceInterface.run(outputNames);

        // Copy the output Tensor back into the output array.
        inferenceInterface.fetch(outputName, outputs);

        PriorityQueue<Recognition> pq =
                new PriorityQueue<Recognition>(
                        3,
                        (lhs, rhs) -> {
                            return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                        });

        for (int i = 0; i < outputs.length; ++i) {
            if (outputs[i] > THRESHOLD) {
                pq.add(
                        new Recognition(
                                "" + i, labels.size() > i ? labels.get(i) : "unknown", outputs[i], null));
            }
        }
        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        final int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }
        return recognitions;
    }

    private Bitmap resizeCropAndRotate(Bitmap originalImage, int orientation) {
        Bitmap result = Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.ARGB_8888);

        final float originalWidth = originalImage.getWidth();
        final float originalHeight = originalImage.getHeight();

        final Canvas canvas = new Canvas(result);

        final float scale = inputSize / originalWidth;

        final float xTranslation = 0.0f;
        final float yTranslation = (inputSize - originalHeight * scale) / 2.0f;

        final Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        final Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(originalImage, transformation, paint);


         // if the orientation is not 0 (or -1, which means we don't know), we have to do a rotation.

        if (orientation > 0) {
            final Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            result = Bitmap.createBitmap(result, 0, 0, inputSize,
                    inputSize, matrix, true);
        }

        return result;
    }

    public int getOrientation(Context context, Uri photoUri) {
        try (final Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null)
        ) {
            assert cursor != null;
            if (cursor.getCount() != 1) {
                cursor.close();
                return -1;
            }

            if (cursor.moveToFirst()) {
                final int r = cursor.getInt(0);
                cursor.close();
                return r;
            }

        } catch (Exception e) {
            return -1;
        }
        return -1;
    }


}

package tersh.tilttest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
/* from Orient OpMode */


public class TiltTest extends AppCompatActivity implements SensorEventListener {
    private Sensor mRotationVectorSensor;
    private SensorManager mSensorManager;
    private final float[] mRotationMatrix = new float[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, mRotationVectorSensor, 10000);


        // initialize the rotation matrix to identity
        mRotationMatrix[0] = 1;
        mRotationMatrix[4] = 1;
        mRotationMatrix[8] = 1;
        mRotationMatrix[12] = 1;


        setContentView(R.layout.activity_tilt_test);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // convert the rotation-vector to a 4x4 matrix. the matrix
            // is interpreted by Open GL as the inverse of the
            // rotation-vector, which is what we want.
            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
        }
        float[] orientation = new float[3];
       /* SensorManager
                .remapCoordinateSystem(mRotationMatrix,
                        SensorManager.AXIS_X, SensorManager.AXIS_Z,
                        mRotationMatrix);
                        */
        SensorManager.remapCoordinateSystem(mRotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mRotationMatrix);

        SensorManager.getOrientation(mRotationMatrix, orientation);
        float[] result = new float[3];
        Log.d("Orient", orientation[0] + " | " + orientation[1] + " | " + orientation[2]);
        result[0] = format(orientation[0]); //Yaw (azimuth)
        result[1] = format(orientation[1]); //Pitch
        result[2] = format(orientation[2]); //Roll
        //Log.d("Orient", result[0] + " | " + result[1] + " | " + result[2]);
        TextView tv = (TextView) findViewById(R.id.tv1);
        if (tv != null) {
            tv.setText(getResponseText((int) result[2]));
        } else {
            Log.d("Orient", "No TextView");
        }
    }

    private String getResponseText(int result) {
        String s = "";
        if (result < 0) {
            s += "LEFT";
        } else {
            s += "RIGHT";
        }
        for (int i = 0; i < Math.abs(result); i++) {
            s += "!";
        }
        return s;
    }

    private void convertToDegrees(float[] vector) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = Math.round(Math.toDegrees(vector[i]));
        }
    }

    private float format(float f) {
        return Math.round(Math.toDegrees((float) f));
        //return (float) f * 57.3f; // radians
    }

    private float[] rotationVectorAction(float[] values) {
        float[] result = new float[3];
        float vec[] = values;
        float quat[] = new float[4];
        float[] orientation = new float[3];
        SensorManager.getQuaternionFromVector(quat, vec);
        float[] rotMat = new float[9];
        SensorManager.getRotationMatrixFromVector(rotMat, quat);
        SensorManager.getOrientation(rotMat, orientation);
        result[0] = (float) orientation[0];
        result[1] = (float) orientation[1];
        result[2] = (float) orientation[2];
        return result;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not sure if needed, placeholder just in case
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tilt_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

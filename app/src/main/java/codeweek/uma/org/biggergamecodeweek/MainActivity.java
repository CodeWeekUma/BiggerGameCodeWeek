package codeweek.uma.org.biggergamecodeweek;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "Main Activity";
    Button _leftButton;
    Button _rightButton;
    TextView _scoreLabel;
    TextView _gameOver;

    int _left_value;
    int _right_value;
    int _score_value;
    float _last_orientation_measure;

    private SensorManager _sensorManager;
    private Sensor _sensor_gyro;
    private Sensor _sensor_magnetic;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _leftButton  = (Button)findViewById(R.id.left_btn);
        _rightButton = (Button)findViewById(R.id.right_btn);
        _scoreLabel  = (TextView)findViewById(R.id.score_label);
        _gameOver    = (TextView)findViewById(R.id.game_over_label);

        doGameCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _sensor_gyro = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _sensor_magnetic = _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        _sensorManager.registerListener(this, _sensor_gyro, SensorManager.SENSOR_DELAY_NORMAL);
        _sensorManager.registerListener(this, _sensor_magnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Don't receive any mbbore updates from either sensor.
        _sensorManager.unregisterListener(this);
    }
    private void doGameCycle(){

        _left_value  = (int)Math.round(Math.random()*1000);
        _right_value = (int)Math.round(Math.random()*1000);

        _scoreLabel.setText(_score_value+"");
        _leftButton.setText(_left_value+"");
        _rightButton.setText(_right_value+"");

        if(_score_value<0)
            _gameOver.setVisibility(View.VISIBLE);

    }

    public void handleGameClick(View v){
        if(_score_value>=0) {
            if (v.getId() == R.id.left_btn) {
                _score_value = _left_value > _right_value ? _score_value + 1 : _score_value - 1;
            } else if (v.getId() == R.id.right_btn) {
                _score_value = _left_value <= _right_value ? _score_value + 1 : _score_value - 1;
            }
            doGameCycle();
        }
    }

    public void handleSelectionClick(View v){

        Log.i(TAG,_last_orientation_measure+"");

        if(_last_orientation_measure < -1){
            _score_value = _left_value > _right_value ? _score_value + 1 : _score_value - 1;
            doGameCycle();
        }else if(_last_orientation_measure >1){
            _score_value = _left_value <= _right_value ? _score_value + 1 : _score_value - 1;
            doGameCycle();
        }
    }

    private void checkUserSelection(float value){
        if(value < -1){
            _score_value = _left_value > _right_value ? _score_value + 1 : _score_value - 1;
            doGameCycle();
        }else if(value >1){
            _score_value = _left_value <= _right_value ? _score_value + 1 : _score_value - 1;
            doGameCycle();
        }

    }
    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

        updateOrientationAngles();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        _sensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        _sensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.

       // Log.i("orientation",""+mOrientationAngles[2]);
        _last_orientation_measure = mOrientationAngles[2];
    }
}
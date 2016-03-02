package cs160.prog2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.widget.Toast;

public class MainActivity extends Activity {

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private String reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            reps = extras.getString("REPS");
            String[] repsList = extractReps(reps);

            final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
            pager.setAdapter(new GridPagerAdapter(this, getFragmentManager(), repsList));
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private String[] extractReps(String repsString) {
        return repsString.split("\\^");
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 2) {
                Toast toast = Toast.makeText(getApplicationContext(), "(40.712784,-74.005941)", Toast.LENGTH_SHORT);
                toast.show();

                if (reps != null) {
                    String[] repsList = extractReps(reps);
                    String temp = repsList[1];
                    repsList[1] = repsList[2];
                    repsList[2] = temp;

                    String updateReps = "^,40.712784,-74.005941";
                    Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
                    sendIntent.putExtra("REP", updateReps);
                    startService(sendIntent);

                    final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
                    pager.setAdapter(new GridPagerAdapter(getBaseContext(), getFragmentManager(), repsList));
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
}

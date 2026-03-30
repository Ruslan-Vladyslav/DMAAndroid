package com.example.lab5;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView tvX, tvY, tvZ, tvAngle, tvStatus;
    private TextView tvMax, tvAvg, tvTime, tvCount;

    private double maxAngle = 0;
    private double sumAngle = 0;
    private int count = 0;

    private long levelStartTime = 0;
    private long totalLevelTime = 0;
    private int levelCount = 0;
    private boolean wasLevel = false;

    private LineChart lineChart;
    private List<Entry> entries;
    private int graphCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvX = findViewById(R.id.tvX);
        tvY = findViewById(R.id.tvY);
        tvZ = findViewById(R.id.tvZ);
        tvAngle = findViewById(R.id.tvAngle);
        tvStatus = findViewById(R.id.tvStatus);
        tvMax = findViewById(R.id.tvMax);
        tvAvg = findViewById(R.id.tvAvg);
        tvTime = findViewById(R.id.tvTime);
        tvCount = findViewById(R.id.tvCount);

        lineChart = findViewById(R.id.lineChart);
        entries = new ArrayList<>();
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.setBackgroundColor(Color.parseColor("#121212"));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        tvX.setText(String.format("X: %.2f", x));
        tvY.setText(String.format("Y: %.2f", y));
        tvZ.setText(String.format("Z: %.2f", z));

        double angle = Math.toDegrees(Math.atan2(x, y));
        tvAngle.setText("Angle: " + (int) angle + "°");
        double angleAbs = Math.abs(angle);

        if (angleAbs > maxAngle) maxAngle = angleAbs;
        sumAngle += angleAbs;
        count++;
        double avg = sumAngle / count;

        boolean isLevel = Math.abs(x) < 1 && Math.abs(y) < 1;

        if (isLevel) {
            tvStatus.setText("LEVEL");
            tvStatus.setTextColor(Color.GREEN);
        } else {
            tvStatus.setText("NOT LEVEL");
            tvStatus.setTextColor(Color.RED);
        }

        if (isLevel && !wasLevel) {
            levelStartTime = System.currentTimeMillis();
            levelCount++;
        }

        if (!isLevel && wasLevel) {
            totalLevelTime += System.currentTimeMillis() - levelStartTime;
        }

        wasLevel = isLevel;

        tvMax.setText("Max angle: " + (int) maxAngle + "°");
        tvAvg.setText("Avg angle: " + (int) avg + "°");
        tvTime.setText("Level time: " + (totalLevelTime / 1000) + " sec");
        tvCount.setText("Times leveled: " + levelCount);

        entries.add(new Entry(graphCount++, (float) angle));
        LineDataSet dataSet = new LineDataSet(entries, "Angle of inclination");
        dataSet.setColor(Color.CYAN);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
package com.example.masato.pressurelogger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineDataSet
import android.R.attr.entries
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity(),SensorEventListener {
    private var sensorManager: SensorManager by Delegates.notNull<SensorManager>()
    private var pressure: Sensor by Delegates.notNull<Sensor>()

    private var senval=1020f

    private  var data=arrayListOf<Float>()
    private var filteredData= arrayListOf<Float>()
    private val dataN=10;

    fun updateData(datum: Float){
        filteredData.add(data.takeLast(dataN).average() as Float)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("senchanged","senchanged")
        if(event?.sensor?.type==Sensor.TYPE_PRESSURE) {
            senval = event.values.get(0)
        }
    }

    override fun onResume(){
        super.onResume()
        Log.d("onresume","onreesume")
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_UI)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager;

        pressure = sensorManager.getDefaultSensor(
            Sensor.TYPE_PRESSURE
        )


        val chart=findViewById<LineChart>(R.id.chart)

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);



        val dataObjects= listOf<Float>()

        var entries = ArrayList<Entry>();


        var i=0f;
        for (data in dataObjects) {
            // turn your data into Entry objects
            entries.add(Entry(i, data));
            i++
        }

        val dataSet = LineDataSet(entries, "Label")
        dataSet.setColor(Color.BLACK,255)


        val dataSets=ArrayList<ILineDataSet>()
        dataSets.add(dataSet)

        val lineData = LineData(dataSets)

        chart.data = lineData
        chart.invalidate()

        val handler = Handler()
        val runnable=object:Runnable{
            override fun run() {
                Log.d("MainActivity","update")
                //entries.add(Entry(entries.size.toFloat(), Math.random().toFloat()));
                entries.add(Entry(entries.size.toFloat(), senval));


                dataSet.notifyDataSetChanged()
                lineData.notifyDataChanged()
                chart.notifyDataSetChanged()

                chart.setVisibleXRangeMaximum(10f);
                //chart.setVisibleYRangeMaximum(2f,YAxis.AxisDependency.LEFT);
                chart.moveViewToX(entries.size - 11f);

                chart.invalidate()

                handler.postDelayed(this,1000)
            }
        }

        handler.post(runnable)
    }
}

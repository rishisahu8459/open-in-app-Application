package com.example.openinapp.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.demoretrofit.data.DataResponse
import com.example.demoretrofit.retrofit.RetroServiceInterface
import com.example.demoretrofit.retrofit.RetrofitInstance
import com.example.openinapp.R
import com.example.openinapp.adapter.MyFragmentAdapter
import com.github.mikephil.charting.charts.LineChart

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var  lineChart: LineChart
    private lateinit var tab: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var greeting: TextView
    private lateinit var adapter: MyFragmentAdapter
    private lateinit var totalClicksTextView:TextView
    private lateinit var todaysclicksTextView:TextView
    private lateinit var totallinksTextView:TextView
    private lateinit var topsourceTextView:TextView
    private lateinit var toplocationTextView:TextView
    private lateinit var besttimeTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        greeting = findViewById(R.id.txtGreeting)
        tab = findViewById(R.id.tabler)
        totalClicksTextView=findViewById(R.id.txtTotalClicks)
        totallinksTextView=findViewById(R.id.txtTotalLinks)
        todaysclicksTextView=findViewById(R.id.txtTodaysClicks)
        topsourceTextView=findViewById(R.id.txtTopSource)
        toplocationTextView=findViewById(R.id.txtTopLocation)
        besttimeTextView=findViewById(R.id.txtBestTime)
        viewPager2 = findViewById(R.id.viewrt)
        tab.addTab(tab.newTab().setText("Top Links"))
        tab.addTab(tab.newTab().setText("Recent Links"))
        val fragmentManager: FragmentManager = supportFragmentManager
        adapter = MyFragmentAdapter(fragmentManager, lifecycle)
        viewPager2.adapter = adapter
        lineChart = findViewById(R.id.chart1)
        val calendar: Calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("HH")
        val strDate: String = mdformat.format(calendar.time)
        val time = strDate.toInt()
        viewPager2.isUserInputEnabled = false
        viewPager2.setPageTransformer(null)
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.setCurrentItem(tab.position, false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tab.selectTab(tab.getTabAt(position))
            }
        })

        sharedPreferences = getSharedPreferences("com.example.openinapp.PREFERENCES", Context.MODE_PRIVATE)

        val tokenn ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI"




        saveTokenToSharedPreferences(tokenn)
        setGreeting(time)
        fetchData()
        fetchDataAndPlotChart()

            }




    private fun setGreeting(time: Int) {
        when {
            time in 13..16 -> greeting.setText("Good Afternoon")
            time < 12 -> greeting.setText("Good Morning")
            time > 17 -> greeting.setText("Good Evening")
        }
    }

    private fun fetchData() {
        val retrofit = RetrofitInstance.retrofit
        val apiService = retrofit.create(RetroServiceInterface::class.java)


        val token = sharedPreferences.getString("token", "")

        val call = apiService.getDashboardData("Bearer $token")
        call.enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                if (response.isSuccessful) {
                    val dataResponse = response.body()
                    if (dataResponse != null) {
                        val totalClicks = dataResponse.total_clicks
                        val todayClicks = dataResponse.today_clicks
                        val toplocation= dataResponse.top_location
                        val totallinks= dataResponse.total_links
                        val besttime= dataResponse.startTime
                        val topsource= dataResponse.top_source



                        totalClicksTextView.text = totalClicks.toString()
                        todaysclicksTextView.text = todayClicks.toString()
                        toplocationTextView.text = if (toplocation.isEmpty()) "N/A" else toplocation
                        topsourceTextView.text = if (topsource.isEmpty()) "N/A" else topsource
                        totallinksTextView.text=totallinks.toString()
                        besttimeTextView.text = if (besttime.isEmpty()) "N/A" else besttime

                    } else {

                    }
                } else {

                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {

            }
        })

}




    private fun fetchDataAndPlotChart() {
        val retrofit = RetrofitInstance.retrofit
        val apiService = retrofit.create(RetroServiceInterface::class.java)
        val sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")



        val call = apiService.getDashboardData("Bearer $token")
        call.enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                if (response.isSuccessful) {
                    val dataResponse = response.body()
                    if (dataResponse != null) {
                        val overallUrlChart = dataResponse.data.overall_url_chart
                        val entries = mutableListOf<Entry>()


                        if (overallUrlChart != null) {
                            overallUrlChart.forEach { (date, clickCount) ->
                                val floatDate = date.substringAfterLast('-').toFloat()
                                entries.add(Entry(floatDate, clickCount.toFloat()))
                            }
                        }


                        entries.sortBy { it.x }


                        val groupedEntries = groupDataByInterval(entries, 5)


                        val xAxis = lineChart.xAxis
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.valueFormatter = IndexAxisValueFormatter(
                            generateXAxisLabels(
                                groupedEntries,
                                overallUrlChart.keys.toList()
                            )
                        )
                        lineChart.setPinchZoom(false)
                        lineChart.isDragEnabled = false
                        lineChart.isScaleXEnabled = false
                        xAxis.granularity = 1f
                        xAxis.setDrawGridLines(false)
                        xAxis.labelRotationAngle = -45f
                        lineChart.xAxis.labelRotationAngle = 0f
                        lineChart.isDoubleTapToZoomEnabled = false
                        lineChart.description.text = "Overview"
                        lineChart.description.textColor = Color.BLACK

                        lineChart.description.xOffset = 0f
                        lineChart.description.yOffset = -20f






                        val dataSet = LineDataSet(groupedEntries, "Click Count")
                        dataSet.color = Color.parseColor("#0e6ffe")
                        dataSet.setCircleColor(Color.parseColor("#0e6ffe"))
                        dataSet.setDrawCircleHole(false)
                        dataSet.enableDashedLine(12f, 8f, 0f)
                        dataSet.lineWidth = 4f
                        val lineData = LineData(dataSet)
                        lineChart.data = lineData
                        lineChart.invalidate()
                    } else {

                    }
                } else {

                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {

            }
        })
    }

    private fun generateXAxisLabels(groupedEntries: List<Entry>, dates: List<String>): List<String> {
        val xValues = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()

        val interval = 5
        val totalDates = dates.size

        if (totalDates > 0) {
            val endIndex = totalDates - 1
            val startIndex = maxOf(endIndex - (interval * 6), 0)
            val step = maxOf((endIndex - startIndex) / 6, 1)

            for (i in startIndex..endIndex step step) {
                val date = dates[i]
                val parsedDate = dateFormat.parse(date)
                calendar.time = parsedDate ?: Date(0)
                val formattedDate = SimpleDateFormat("MMM dd", Locale.US).format(calendar.time)
                xValues.add(formattedDate)
            }
        }

        return xValues
    }
    private fun groupDataByInterval(entries: List<Entry>, interval: Int): List<Entry> {
        val groupedEntries = mutableListOf<Entry>()
        var sum = 0f
        var count = 0
        var groupIndex = 0

        entries.forEachIndexed { index, entry ->
            sum += entry.y
            count++

            if (count == interval || index == entries.size - 1) {
                val average = sum / count
                val groupEntry = Entry(groupIndex.toFloat(), average)
                groupedEntries.add(groupEntry)
                sum = 0f
                count = 0
                groupIndex++
            }
        }

        return groupedEntries
    }

    private fun saveTokenToSharedPreferences(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

}

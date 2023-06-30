package com.example.openinapp.Viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.demoretrofit.data.DataResponse
import com.example.demoretrofit.data.Links

import com.example.demoretrofit.retrofit.RetroServiceInterface
import com.example.demoretrofit.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

class ActivityViewModel : ViewModel() {


    private val _topLinks = MutableLiveData<List<Links>>()
    val topLinks: LiveData<List<Links>> get() = _topLinks

    // Function to handle the Retrofit response and extract the recentLinks
    fun handleResponse(response: DataResponse) {
        val data = response.data
        val links = data.top_links
        _topLinks.value = links
    }


   /* fun makeAPICall()
    {
       val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI"

        val call = RetrofitInstance.apiService.getDashboardData("Bearer $token")


        call.enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                if (response.isSuccessful) {

                    val dataResponse = response.body()
                    // Process the profileResponse object here
                    println(dataResponse?.message)

                    if (dataResponse != null) {
                        val status = dataResponse.status
                        val statusCode = dataResponse.statusCode
                        val message = dataResponse.message
                        val supportWhatsappNumber = dataResponse.support_whatsapp_number
                        val extraIncome = dataResponse.extra_income
                        val totalLinks = dataResponse.total_links
                        val totalClicks = dataResponse.total_clicks
                        val todayClicks = dataResponse.today_clicks
                        val topSource = dataResponse.top_source
                        val topLocation = dataResponse.top_location
                        val startTime = dataResponse.startTime
                        val linksCreatedToday = dataResponse.links_created_today
                        val appliedCampaign = dataResponse.applied_campaign
                        val data = dataResponse.data

                        // Access other properties as needed
                        val recentLinks = data.recent_links
                        val topLinks = data.top_links
                        val overallUrlChart = data.overall_url_chart




                    }

             }

                else {
                    // Handle the error response
                    println("API request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                // Handle failure
                println("API request failed: ${t.message}")
            }
        })



    }  */



}
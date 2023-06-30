package com.example.openinapp.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoretrofit.data.DataResponse
import com.example.demoretrofit.retrofit.RetroServiceInterface
import com.example.demoretrofit.retrofit.RetrofitInstance

import com.example.openinapp.R
import com.example.openinapp.Viewmodel.ActivityViewModel
import com.example.openinapp.adapter.TopLinkAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TopLinksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TopLinkAdapter
    private lateinit var viewModel: ActivityViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_top_links, container, false)

        sharedPreferences = requireContext().getSharedPreferences("com.example.openinapp.PREFERENCES", Context.MODE_PRIVATE)
        recyclerView = view.findViewById(R.id.toplinkRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        adapter = TopLinkAdapter()
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(requireActivity()).get(ActivityViewModel::class.java)



        val apiService = RetrofitInstance.retrofit.create(RetroServiceInterface::class.java)
        val token = sharedPreferences.getString("token", "")
       //val token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjU5MjcsImlhdCI6MTY3NDU1MDQ1MH0.dCkW0ox8tbjJA2GgUx2UEwNlbTZ7Rr38PVFJevYcXFI"
        val call = apiService.getDashboardData("Bearer $token")
        call.enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                if (response.isSuccessful) {
                    val dataResponse = response.body()
                    if (dataResponse != null) {
                        viewModel.handleResponse(dataResponse)
                        println(dataResponse.message)
                    }
                } else {
                    // Handle the error response
                    println("API request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                // Handle the network failure
                println("API request failed: ${t.message}")
            }
        })

        viewModel.topLinks.observe(viewLifecycleOwner, { links ->
            // Update the adapter with the recent links
            adapter.setTopLinks(links)
        })

        return view
    }
    }





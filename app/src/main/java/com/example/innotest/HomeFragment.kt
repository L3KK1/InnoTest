package com.example.innotest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), HeaderClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager


        loadFeaturedCollections()

        return view
    }

    private fun loadFeaturedCollections() {

        val call = PexelsApi.service.getFeaturedCollections(PexelsApi.API_KEY, 1, 7)
        call.enqueue(object : Callback<CollectionsResponse> {
            override fun onResponse(
                call: Call<CollectionsResponse>,
                response: Response<CollectionsResponse>
            ) {
                if (response.isSuccessful) {
                    val collections = response.body()?.collections
                    collections?.let {
                        val headers = it.map { collection -> collection.title }
                        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
                        val adapter = HeaderAdapter(headers, this@HomeFragment)
                        recyclerView.adapter = adapter

                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
                        recyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
                    }
                } else {
                    Log.e("API Error", "Response unsuccessful")
                    val errorBody = response.errorBody()?.string()
                    Log.e("API Error", "Error Body: $errorBody")
                }
            }

            override fun onFailure(call: Call<CollectionsResponse>, t: Throwable) {
                Log.e("API Error", "Failed to fetch data", t)
            }
        })
    }

    override fun onHeaderClicked(header: String) {
        // Обработка клика на заголовок
    }
}

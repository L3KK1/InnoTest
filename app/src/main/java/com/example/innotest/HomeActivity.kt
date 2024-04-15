package com.example.innotest

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    private val apiKey = "PmxjA47BhjHLAH2p8kyAnKeilRRuSVLZhvbvVNxvGTguGVViBrRTq6Oi"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.home_layout)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // Обработка нажатия на элемент "Home"
                    return@setOnItemSelectedListener true
                }
                R.id.menu_fav_home -> {
                    // Обработка нажатия на элемент "Favourite"
                    return@setOnItemSelectedListener true
                }

            }
            return@setOnItemSelectedListener false
        }
        //хуй щука  щщ


        //Get Featured Collections
        val call = PexelsApi.service.getFeaturedCollections(PexelsApi.API_KEY, 1, 7)
        call.enqueue(object : Callback<CollectionsResponse> {
            override fun onResponse(
                call: Call<CollectionsResponse>,
                response: Response<CollectionsResponse>
            )
             {
                if (response.isSuccessful) {
                    val collections = response.body()?.collections
                    collections?.let { // Проверяем, что collections не null
                        val headers = it.map { collection -> collection.title }
                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                        val layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
                        recyclerView.adapter = HeaderAdapter(it.map { collection -> collection.title }) // Установите адаптер здесь
                        recyclerView.layoutManager = layoutManager

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

    fun onHeaderClicked(header: String) {
        // Обработка клика на заголовок
        Toast.makeText(this, "Clicked on header: $header", Toast.LENGTH_SHORT).show()
    }


    fun makeApiCall() {

        val editSearch = findViewById<EditText>(R.id.searchbar)
        val inputSearch = editSearch.text.toString()
        val call = RetrofitInstance.api.searchPhotos(inputSearch, 30, 1, "Bearer $apiKey")

        call.enqueue(object : Callback<List<PhotoResponse>> {
            override fun onResponse(
                call: Call<List<PhotoResponse>>,
                response: Response<List<PhotoResponse>>
            ) {
                if (response.isSuccessful) {
                    val photos = response.body()
                    photos?.forEach { photo ->
                        println("Photo ID: ${photo.id}, Photographer: ${photo.photographer}, Source: ${photo.src.original}")
                    }
                } else {
                    println("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<PhotoResponse>>, t: Throwable) {
                println("Error: ${t.message}")
            }
        })
    }


}
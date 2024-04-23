package com.example.innotest

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity(), HeaderClickListener {
    private val apiKey = "PmxjA47BhjHLAH2p8kyAnKeilRRuSVLZhvbvVNxvGTguGVViBrRTq6Oi"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        //val firstMenuItem = bottomNavigationView.menu.findItem(R.id.menu_home)

        val indicatorView = findViewById<View>(R.id.indicator_view)

        //Log.d("MENU", "First menu item id: $firstMenuItem")
        bottomNavigationView.itemIconTintList = null
        fun moveIndicatorToMenuItem(item: MenuItem) {
            val selectedMenuItemView = bottomNavigationView.findViewById<View>(item.itemId)
            val parentView = selectedMenuItemView.parent as ViewGroup

            val indicatorParams = indicatorView.layoutParams as RelativeLayout.LayoutParams

            // Calculate the center position of the selected menu item
            val selectedItemCenterX = selectedMenuItemView.left + selectedMenuItemView.width / 2



            // Calculate the left margin of the indicator to center it under the selected menu item
            val indicatorLeftMargin = selectedItemCenterX + ((indicatorView.width / 3) * 2)

            // Set the top margin of the indicator to position it below the selected menu item
            val indicatorTopMargin = selectedMenuItemView.bottom - 20

            // Log the calculated coordinates
            Log.d("INDICATOR", "Selected item center X: $selectedItemCenterX")
            Log.d("INDICATOR", "Indicator left margin: $indicatorLeftMargin")
            Log.d("INDICATOR", "Indicator top margin: $indicatorTopMargin")

            // Set the calculated margins to the indicator layout params
            indicatorParams.leftMargin = indicatorLeftMargin
            indicatorParams.topMargin = indicatorTopMargin

            // Apply the layout params to the indicator view
            indicatorView.layoutParams = indicatorParams
        }
        // Wait for the layout to finish and then move the indicator
        val rootView = window.decorView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Вызываем метод установки индикатора для первого элемента меню
                val firstMenuItem = bottomNavigationView.menu.findItem(R.id.menu_home)
                moveIndicatorToMenuItem(firstMenuItem)

                // Remove listener
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })


        bottomNavigationView.setOnItemSelectedListener { item ->
        moveIndicatorToMenuItem(item)
            when (item.itemId) {
                R.id.menu_home -> {
                    // Обработка нажатия на элемент "Home"
                    moveIndicatorToMenuItem(item)
                    return@setOnItemSelectedListener true
                }
                R.id.menu_fav_home -> {
                    // Обработка нажатия на элемент "Favourite"
                    moveIndicatorToMenuItem(item)
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }




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
                        recyclerView.adapter = HeaderAdapter(it.map { collection -> collection.title }, this@HomeActivity) // Установите адаптер здесь
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

    override fun onHeaderClicked(header: String) {
        // Обработка клика на заголовок
        

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

    private fun Int.dp() = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun View.dp() = layoutParams.width.dp()

}
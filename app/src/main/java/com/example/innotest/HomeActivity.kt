package com.example.innotest

import BookmarksFragment
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View

import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity(), HeaderClickListener, PhotoClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, HomeFragment())
        fragmentTransaction.commit()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        //val firstMenuItem = bottomNavigationView.menu.findItem(R.id.menu_home)

        val indicatorView = findViewById<View>(R.id.indicator_view)

        //Log.d("MENU", "First menu item id: $firstMenuItem")
        bottomNavigationView.itemIconTintList = null
        fun moveIndicatorToMenuItem(item: MenuItem) {
            val selectedMenuItemView = bottomNavigationView.findViewById<View>(item.itemId)


            val indicatorParams = indicatorView.layoutParams as RelativeLayout.LayoutParams

            val selectedItemCenterX = selectedMenuItemView.left + selectedMenuItemView.width / 2
            val indicatorLeftMargin = selectedItemCenterX + ((indicatorView.width / 3) * 2)
            val indicatorTopMargin = selectedMenuItemView.bottom - 20

            // Log the calculated coordinates
            Log.d("INDICATOR", "Selected item center X: $selectedItemCenterX")
            Log.d("INDICATOR", "Indicator left margin: $indicatorLeftMargin")
            Log.d("INDICATOR", "Indicator top margin: $indicatorTopMargin")


            indicatorParams.leftMargin = indicatorLeftMargin
            indicatorParams.topMargin = indicatorTopMargin

            indicatorView.layoutParams = indicatorParams
        }
        // Wait for the layout to finish and then move the indicator
        val rootView = window.decorView.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

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
                    replaceFragment(HomeFragment())
                    moveIndicatorToMenuItem(item)
                    return@setOnItemSelectedListener true
                }

                R.id.menu_fav_home -> {
                    // Обработка нажатия на элемент "Favourite"
                    replaceFragment(BookmarksFragment())
                    moveIndicatorToMenuItem(item)
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onHeaderClicked(header: String) {
        // Обработка клика на заголовок
    }

    override fun onPhotoClicked(url: String, id: Int) {
        val fragment = DetailsFragment.newInstance(url, id)
        val homeFragment = supportFragmentManager.findFragmentByTag("HomeFragment")
        val indicatorView = findViewById<View>(R.id.indicator_view)
        indicatorView.visibility = View.GONE
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        if (homeFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.hide(homeFragment)
            transaction.commit()
        }

        replaceFragment(fragment)
    }

}
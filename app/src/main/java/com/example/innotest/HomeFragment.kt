package com.example.innotest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), HeaderClickListener {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager

        // Показать заголовки
        loadFeaturedCollections()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCuratedPhotos()
    }

    private fun loadCuratedPhotos() {
        // Показать индикатор загрузки
        showLoadingIndicator(true)

        val apiKey = PexelsApi.API_KEY
        val page = 1
        val perPage = 30

        PexelsApi.service.getCuratedPhotos(apiKey, perPage, page).enqueue(object : Callback<PhotosResponse> {
            override fun onResponse(call: Call<PhotosResponse>, response: Response<PhotosResponse>) {
                if (response.isSuccessful) {
                    val photos = response.body()?.photos
                    val photoUrls = mutableListOf<String>()
                    photos?.forEach {
                        photoUrls.add(it.src.medium)
                    }
                    val recyclerPhotoView = view?.findViewById<RecyclerView>(R.id.photoRecyclerView)
                    val photoLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    recyclerPhotoView?.layoutManager = photoLayoutManager
                    val photoAdapter = PhotoRecyclerAdapter(photoUrls)
                    recyclerPhotoView?.adapter = photoAdapter
                    showLoadingIndicator(false)
                } else {
                    // Обработка ошибки загрузки фотографий
                }
            }

            override fun onFailure(call: Call<PhotosResponse>, t: Throwable) {
                // Обработка ошибки загрузки фотографий
            }
        })
    }

    private fun loadFeaturedCollections() {
        val call = PexelsApi.service.getFeaturedCollections(PexelsApi.API_KEY, 1, 7)
        call.enqueue(object : Callback<CollectionsResponse> {
            override fun onResponse(call: Call<CollectionsResponse>, response: Response<CollectionsResponse>) {
                if (response.isSuccessful) {
                    val collections = response.body()?.collections
                    collections?.let {
                        val headers = it.map { collection -> collection.title }
                        val adapter = HeaderAdapter(headers, this@HomeFragment)
                        recyclerView.adapter = adapter

                        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
                        recyclerView.addItemDecoration(SpacesItemDecoration(spacingInPixels))
                    }
                } else {
                    // Обработка ошибки загрузки заголовков
                }
            }

            override fun onFailure(call: Call<CollectionsResponse>, t: Throwable) {
                // Обработка ошибки загрузки заголовков
            }
        })
    }

    private fun showLoadingIndicator(isLoading: Boolean) {
        // Отображение или скрытие индикатора загрузки
        val progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onHeaderClicked(header: String) {
        // Обработка клика на заголовок
    }
}

package com.example.innotest

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), HeaderClickListener, PhotoClickListener {

    private lateinit var context: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchbar: EditText
    private lateinit var recyclerPhotoView: RecyclerView
    private lateinit var clearButton : Button
    private lateinit var horizontalProgressBar: ProgressBar
    private lateinit var headers: List<String>
    private var photoClickListener: PhotoClickListener? = null
    private var selectedHeaderPosition: Int = RecyclerView.NO_POSITION

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        if (context is PhotoClickListener) {
            photoClickListener = context
        } else {
            throw RuntimeException("$context must implement PhotoClickListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        photoClickListener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager


       horizontalProgressBar = view.findViewById(R.id.progressBar)
        horizontalProgressBar.progress = 50

        recyclerPhotoView = view.findViewById(R.id.photoRecyclerView)
        val photoLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val photoGridManager = GridLayoutManager(context, 2)
        recyclerPhotoView.layoutManager = photoLayoutManager
        recyclerPhotoView.layoutManager = photoGridManager

        clearButton = view.findViewById(R.id.clear_button)
        clearButton.visibility = View.INVISIBLE

        searchbar = view.findViewById(R.id.searchbar)

        clearButton.setOnClickListener{
            searchbar.setText("")
        }
        searchbar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                    loadCuratedPhotos()
                } else {
                    clearButton.visibility = View.VISIBLE
                    searchPhotos(query)
                }

                // Убираем выделение заголовка после изменения текста в searchbar
                selectedHeaderPosition = RecyclerView.NO_POSITION

                // Обновляем внешний вид заголовков в RecyclerView
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // Показать заголовки
        loadFeaturedCollections()




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCuratedPhotos()
    }

    private fun searchPhotos(query: String) {
        showLoadingIndicator(true)
        val apiKey = PexelsApi.API_KEY
        val page = 1
        val perPage = 30

        PexelsApi.service.searchPhotosByQuery(apiKey, query, page, perPage).enqueue(object : Callback<PhotosResponse> {
            override fun onResponse(call: Call<PhotosResponse>, response: Response<PhotosResponse>) {
                if (response.isSuccessful) {
                    val photos = response.body()?.photos
                    Log.d("HomeFragment", "Received ${photos?.size} photos from server")
                    val photoUrls = photos?.map { it.src.medium } ?: emptyList()
                    val photoIds = photos?.map { it.id } ?: emptyList() // Получаем список идентификаторов фото

                    val photoAdapter = PhotoRecyclerAdapter(photoUrls, photoIds, this@HomeFragment) // Передаем и URL, и ID фото

                    recyclerPhotoView.adapter = photoAdapter
                    showLoadingIndicator(false)

                    // Добавить вызов notifyDataSetChanged() после установки нового адаптера
                    photoAdapter.notifyDataSetChanged()
                } else {
                    Log.e("HomeFragment", "Error: ${response.message()}")
                    Log.d("HomeFragment", "Debug message: Text entered - $query")

                    // Обработка ошибки загрузки фотографий
                }
            }


            override fun onFailure(call: Call<PhotosResponse>, t: Throwable) {
                // Обработка ошибки загрузки фотографий
                Log.e("HomeFragment", "Failed to make network request: ${t.message}")
            }
        })
    }

    private fun loadCuratedPhotos() {
        showLoadingIndicator(true)

        val apiKey = PexelsApi.API_KEY
        val page = 1
        val perPage = 30

        PexelsApi.service.getCuratedPhotos(apiKey, perPage, page).enqueue(object : Callback<PhotosResponse> {
            override fun onResponse(call: Call<PhotosResponse>, response: Response<PhotosResponse>) {
                if (response.isSuccessful) {
                    val photos = response.body()?.photos
                    val photoUrls = photos?.map { it.src.medium } ?: emptyList()
                    val photoIds = photos?.map { it.id } ?: emptyList() // Получаем список идентификаторов фото

                    val photoAdapter = PhotoRecyclerAdapter(photoUrls, photoIds, this@HomeFragment) // Передаем и URL, и ID фото
                    recyclerPhotoView.adapter = photoAdapter
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
                        headers = it.map { collection -> collection.title }
                        val adapter = HeaderAdapter(headers, this@HomeFragment)
                        recyclerView.adapter = adapter

                        val spacingInPixels = context.resources.getDimensionPixelSize(R.dimen.spacing)
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
        val progressBar = requireView().findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onHeaderClicked(header: String) {
        // Проверяем, содержится ли текст заголовка в тексте searchbar
        if (searchbar.text.toString() != header) {
            selectedHeaderPosition = RecyclerView.NO_POSITION
        }
        // Установка текста заголовка в searchbar
        searchbar.setText(header)
    }

    override fun onPhotoClicked(url: String, id: Int) {
        photoClickListener?.onPhotoClicked(url,id)

    }

}
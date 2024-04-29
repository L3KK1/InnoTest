package com.example.innotest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.viewModels

interface BackButtonClickListener {
    fun onBackButtonClicked()
}

class DetailsFragment: Fragment() {


    private var backButtonClickListener: BackButtonClickListener? = null
    private var photoUrl: String? = null
    private var photoId: Int? = null
    private var authorName: String? = null
    lateinit var photographer_name: TextView
    lateinit var downloadButton: ImageButton
    lateinit var bookmarkButton: ImageButton
    lateinit var backButton: ImageButton
    companion object {
        private const val ARG_PHOTO_URL = "photo_url"
        private const val ARG_PHOTO_ID = "photo_id"

        fun newInstance(photoUrl: String, photoId: Int): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putString(ARG_PHOTO_URL, photoUrl)
            args.putInt(ARG_PHOTO_ID, photoId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BackButtonClickListener) {
            backButtonClickListener = context
        } else {
            throw RuntimeException("$context must implement BackButtonClickListener")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            photoUrl = it.getString(ARG_PHOTO_URL)
            photoId = it.getInt(ARG_PHOTO_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.details_layout, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoImageView = view.findViewById<ImageView>(R.id.photo_view)
        photographer_name = view.findViewById(R.id.photographer_name)
        bookmarkButton = view.findViewById(R.id.bookmark_button)
        val db = context?.let { Room.databaseBuilder(it, AppDatabase::class.java, "image-database").build() }

        val imageDao = db!!.imageDao()

        backButton = view.findViewById(R.id.return_button)
        backButton.setOnClickListener{
            backButtonClickListener?.onBackButtonClicked()
        }

        photoUrl?.let { url ->
            bookmarkButton.setOnClickListener {
                Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val stream = ByteArrayOutputStream()
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val imageData: ByteArray = stream.toByteArray()
                            CoroutineScope(Dispatchers.IO).launch {
                                val imageEntity =
                                    photoId?.let { it1 -> ImageEntity(it1, url, imageData) }
                                if (imageEntity != null) {
                                    imageDao.insertImage(imageEntity)
                                }
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // Очистка загрузки
                        }
                    })
            }
        }

        val sharedViewModel: SharedViewModel by viewModels {
            SharedViewModelFactory(imageDao)
        }



        photoUrl?.let { url ->
             downloadButton = view.findViewById(R.id.download_button)
            downloadButton.setOnClickListener{
                val file = File(context?.externalCacheDir, "image.jpg")
            context?.let {
                Glide.with(it)
                    .asFile()
                    .load(url)
                    .into(object : CustomTarget<File>() {
                        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                            try {
                                val inputStream: InputStream = FileInputStream(resource)
                                val outputStream: OutputStream = FileOutputStream(file)
                                val buf = ByteArray(1024)
                                var len: Int
                                while (inputStream.read(buf).also { len = it } > 0) {
                                    outputStream.write(buf, 0, len)
                                }
                                inputStream.close()
                                outputStream.close()
                                // Файл успешно сохранен на устройстве
                                Toast.makeText(context, "Image downloaded successfully", Toast.LENGTH_SHORT).show()
                            } catch (e: IOException) {
                                e.printStackTrace()
                                Toast.makeText(context, "Failed to download image", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // Очистка загрузки
                        }
                    })
            }
        }

        }
        Log.d("DetailsFragment", "Photo ID: $photoId")
        photoUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .transform(RoundedCorners(50))
                .into(photoImageView)

            // Отправляем запрос к Pexels API для получения информации об авторе
            val call = photoId?.let { PexelsApi.service.getPhotoById(PexelsApi.API_KEY, it) }
            call?.enqueue(object : Callback<Photo> {
                override fun onResponse(call: Call<Photo>, response: Response<Photo>) {
                    if (response.isSuccessful) {
                        authorName = response.body()?.photographer
                        photographer_name.text = authorName
                        Log.d("DetailsFragment", "Author name received: $authorName")

                    } else {
                        val statusCode = response.code()
                        val errorMessage = response.errorBody()?.string()
                        Log.e("DetailsFragment", "Failed to get author name. Status code: $statusCode, Error message: $errorMessage")
                        Log.e("DetailsFragment", "Failed to get author name. Error message: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Photo>, t: Throwable) {
                    Log.e("DetailsFragment", "Error getting author name", t)
                }
            })
        }

    }

    fun getImageDao(): ImageDao {
        val db = context?.let {
            Room.databaseBuilder(it, AppDatabase::class.java, "image-database").build()
        }
            return db!!.imageDao()

    }

}

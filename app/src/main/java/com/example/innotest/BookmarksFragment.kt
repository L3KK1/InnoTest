import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.innotest.BookmarksPhotoAdapter
import com.example.innotest.DetailsFragment
import com.example.innotest.ImageDao
import com.example.innotest.PhotoRecyclerAdapter
import com.example.innotest.R
import com.example.innotest.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModelProvider

class BookmarksFragment : Fragment() {

    private lateinit var recyclerPhotoView: RecyclerView
    private lateinit var photoAdapter: BookmarksPhotoAdapter
    private lateinit var imageDao: ImageDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmarks, container, false)
        // Загрузка макета для фрагмента
        recyclerPhotoView = view.findViewById(R.id.bookmarks_photos)
        val photoLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val photoGridManager = GridLayoutManager(context, 2)
        recyclerPhotoView.layoutManager = photoLayoutManager
        recyclerPhotoView.layoutManager = photoGridManager


        val sharedViewModel: SharedViewModel by activityViewModels()
         imageDao = sharedViewModel.getImageDao()
        photoAdapter = BookmarksPhotoAdapter()
        recyclerPhotoView.adapter = photoAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        imageDao = sharedViewModel.getImageDao()
        photoAdapter = BookmarksPhotoAdapter()
        recyclerPhotoView.adapter = photoAdapter
        // Дополнительная логика, выполняемая после создания представления фрагмента
        // Получение списка фото из базы данных и установка их в адаптер
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val images = imageDao.getAllImages() // Получаем все изображения из базы данных
                withContext(Dispatchers.Main) {
                    // Обновляем UI на основе полученных данных
                    photoAdapter.submitList(images)
                }
                // Выводим в лог содержимое базы данных для проверки
                for (image in images) {
                    Log.d("BookmarksFragment", "Image ID: ${image.imageId}, Image URL: ${image.imageUrl}")
                }
            } catch (e: Exception) {
                // Выводим в лог информацию об ошибке при запросе к базе данных
                Log.e("BookmarksFragment", "Error fetching images from database: ${e.message}", e)
            }
        }
    }

}

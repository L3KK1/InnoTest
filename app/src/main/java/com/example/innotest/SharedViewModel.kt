package com.example.innotest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImageDaoImplementation : ImageDao {

    private val imageList = mutableListOf<ImageEntity>()
    override fun getAllImages(): List<ImageEntity> {
        return imageList
    }
    override fun insertImage(image: ImageEntity) {
        imageList.add(image)
    }
    override fun deleteImage(image: ImageEntity) {
        imageList.remove(image)
    }
}
class SharedViewModel(private val imageDao: ImageDao) : ViewModel() {

    constructor() : this(ImageDaoImplementation()) {

    }

    fun getImageDao(): ImageDao {
        return imageDao
    }
}

class SharedViewModelFactory(private val imageDao: ImageDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(imageDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.innotest

data class PhotoResponse(
    val id: Int,
    val photographer: String,
    val src: PhotoSource
)

data class PhotoSource(
    val original: String
)

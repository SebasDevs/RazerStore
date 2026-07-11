package com.example.AppRazer.domain.model

data class CarouselItem(
    val title: String,
    val imageUrl: String,
    val onClickAction: () -> Unit
)
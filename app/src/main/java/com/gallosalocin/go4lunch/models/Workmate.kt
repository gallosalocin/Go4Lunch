package com.gallosalocin.go4lunch.models

data class Workmate(
        var name: String? = null,
        var image: String? = null,
        var chosenRestaurantId: String? = null,
        var chosenRestaurantName: String? = null,
        var favorite: List<String>? = null,
)
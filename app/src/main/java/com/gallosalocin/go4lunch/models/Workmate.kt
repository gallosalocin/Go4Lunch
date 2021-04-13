package com.gallosalocin.go4lunch.models

data class Workmate (
        var name: String? = null,
        var image: String? = null,
        var chosenRestaurantId: String? = null,
        var chosenRestaurantName: String? = null,
        var favorite: List<String>? = null,
        )
//{
//
//
//    constructor(name: String?, image: String?, chosenRestaurantId: String?, chosenRestaurantName: String?, favorite: List<String>?) {
//        this.name = name
//        this.image = image
//        this.chosenRestaurantId = chosenRestaurantId
//        this.chosenRestaurantName = chosenRestaurantName
//        this.favorite = favorite
//    }
//
//    constructor()
//}
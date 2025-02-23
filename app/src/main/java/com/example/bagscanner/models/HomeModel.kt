package com.example.bagscanner.models
import com.example.bagscanner.enums.BagTypes

data class HomeModel(
    val detectedBagType: BagTypes = BagTypes.Unknown
)
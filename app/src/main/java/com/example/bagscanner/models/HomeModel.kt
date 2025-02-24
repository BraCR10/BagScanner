package com.example.bagscanner.models
import com.example.bagscanner.enums.BagTypes

class HomeModel(
    val detectedBagType: BagTypes = BagTypes.Unknown
)
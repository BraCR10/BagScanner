package com.example.bagscanner.models

// Project-specific imports
import com.example.bagscanner.enums.BagTypes


class HomeModel(
    val detectedBagType: BagTypes = BagTypes.Unknown
)
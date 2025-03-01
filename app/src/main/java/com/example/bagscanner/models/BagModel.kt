package com.example.bagscanner.models

// Project-specific imports
import com.example.bagscanner.enums.BagTypes


class BagModel(
    val detectedBagType: BagTypes = BagTypes.Unknown
)
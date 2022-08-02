package com.ala158.magicpantry.ui.receiptscanner

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReceiptScannerViewModel : ViewModel() {
    val userImage = MutableLiveData<Bitmap>()
}
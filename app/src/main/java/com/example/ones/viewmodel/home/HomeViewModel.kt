package com.example.ones.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ones.R
import com.example.ones.data.model.LatestEntry

class HomeViewModel : ViewModel() {

    private val _latestEntries = MutableLiveData<List<LatestEntry>>().apply {
        value = listOf(
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Food",
                date = "20 Feb 2024",
                amount = "+ $20 + Vat 0.5%",
                paymentInfo = "Google Pay"
            ),
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Uber",
                date = "13 Mar 2024",
                amount = "- $18 + Vat 0.8%",
                paymentInfo = "Cash"
            ),
            LatestEntry(
                iconResId = R.drawable.ic_shopping,
                title = "Shopping",
                date = "11 Mar 2024",
                amount = "- $400 + Vat 0.12%",
                paymentInfo = "Paytm"
            )
        )
    }
    val latestEntries: LiveData<List<LatestEntry>> = _latestEntries
}

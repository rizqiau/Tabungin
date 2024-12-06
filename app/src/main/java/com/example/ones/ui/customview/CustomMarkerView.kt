package com.example.ones.ui.customview

import android.content.Context
import android.widget.TextView
import com.example.ones.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private var tvData: TextView = findViewById(R.id.markerText)

    // Menampilkan nilai ketika bar diklik
    fun setDataValue(value: Number) {
        tvData.text = "$value"
    }

    // Refresh marker view saat data dipilih
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        tvData.text = "${e?.y}" // Menampilkan nilai y dari Entry
    }

    override fun getOffset(): MPPointF {
        // Mengatur posisi marker di atas bar
        return MPPointF(-width / 2f, -height.toFloat())
    }
}
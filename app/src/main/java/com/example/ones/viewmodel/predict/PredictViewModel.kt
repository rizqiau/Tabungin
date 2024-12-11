package com.example.ones.viewmodel.predict

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ones.data.remote.response.PredictResponse
import com.example.ones.data.repository.PredictRepository
import com.example.ones.data.model.Result
import com.example.ones.data.remote.request.PredictRequest
import com.example.ones.data.repository.SavingsRepository
import kotlinx.coroutines.launch

class PredictViewModel(
    private val predictRepository: PredictRepository,
    private val savingsRepository: SavingsRepository
) : ViewModel() {

    private val _predictResult = MutableLiveData<Result<PredictResponse>>()
    val predictResult: LiveData<Result<PredictResponse>> get() = _predictResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun predictSavings() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Ambil total additions dan reductions dari SavingsRepository
                val (totalAdditions, totalReductions) = savingsRepository.getTotalAdditionsAndReductions()

                // Buat request dengan data yang diperoleh
                val request = PredictRequest(
                    Pendapatan_Bulanan = totalAdditions,
                    Umur = 0, // Default value
                    Jumlah_Tanggungan = 0, // Default value
                    Sewa_Bulanan = 0, // Default value
                    Pembayaran_Pinjaman_Bulanan = 0, // Default value
                    Biaya_Asuransi_Bulanan = 0, // Default value
                    Biaya_Bahan_Makanan_Bulanan = 0, // Default value
                    Biaya_Transportasi_Bulanan = 0, // Default value
                    Biaya_Makan_Di_Luar_Bulanan = 0, // Default value
                    Biaya_Hiburan_Bulanan = 0, // Default value
                    Biaya_Utilitas_Bulanan = 0, // Default value
                    Biaya_Perawatan_Kesehatan_Bulanan = 0, // Default value
                    Biaya_Pendidikan_Bulanan = 0, // Default value
                    Biaya_Lain_Lain_Bulanan = totalReductions
                )

                Log.d("PredictRequest", "Request data: $request")
                val response = predictRepository.predictSavings(request)

                _predictResult.value = Result.Success(response)
                Log.d("PredictResponse", "Prediction successful: $response")
            } catch (e: Exception) {
                _predictResult.value = Result.Error(e.message ?: "Unknown error")
                Log.e("PredictViewModel", "Error during prediction: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

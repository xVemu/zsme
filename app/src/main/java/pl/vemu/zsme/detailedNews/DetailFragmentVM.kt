package pl.vemu.zsme.detailedNews

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation

class DetailFragmentVM(url: String) : ViewModel(), View.OnClickListener {
    private val detail = MutableLiveData<Detail?>()
    private val isRefreshing = MutableLiveData(false)
    fun getDetail(): LiveData<Detail?> {
        return detail
    }

    fun getIsRefreshing(): LiveData<Boolean> {
        return isRefreshing
    }

    override fun onCleared() {
        detail.value = null
        isRefreshing.value = false
    }

    override fun onClick(v: View) {
        if (detail.value == null) return
        val imagesArray = arrayOfNulls<String>(detail.value!!.images!!.size)
        detail.value!!.images?.toTypedArray()
        Navigation.findNavController(v).navigate(DetailFragmentDirections.actionDetailFragmentToGalleryFragment(imagesArray))
    }

    /*init {
        val finalUrl = if (!url.startsWith("http") && !url.startsWith("https")) application.getString(R.string.zsme_default_link) + url else url
        Thread {
            isRefreshing.postValue(true)
            try {
                val downloadedDetail = DetailRepo.INSTANCE.downloadText(finalUrl)
                        ?: throw IOException("Not found")
                detail.postValue(downloadedDetail)
            } catch (e: IOException) {
                e.printStackTrace()
                detail.postValue(Detail("Wystąpił błąd", null))
            } finally {
                isRefreshing.postValue(false)
            }
        }.start()
    }*/
}
package com.twincom.imagegenerateai

import android.net.Uri
import com.twincom.imagegenerateai.network.HttpClient
import com.twincom.imagegenerateai.utils.Cons.PREFERENCES_TOKEN
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainPresenter(private val view: MainView.View) : MainView.Presenter {
    private val mCompositeDisposable: CompositeDisposable?
    init {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun submitOpenAi(image: Uri, number: Int, size: String) {
        view.showLoading(true)
        val productImage = File(image.path)
        val imageRequest = productImage.asRequestBody("image/png".toMediaTypeOrNull())

        val imageParams = MultipartBody.Part.createFormData("image", productImage.name, imageRequest)
        val numberParams = number.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val sizeParams = size.toRequestBody("text/plain".toMediaTypeOrNull())

        val disposable = HttpClient.getInstance().getApi()!!.submitOpenAi(
            PREFERENCES_TOKEN, imageParams, numberParams, sizeParams
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when(it.isSuccessful) {
                    true -> {
                        it.body()?.let { response -> view.submitOpenAiSuccess(response) }
                        view.showLoading(false)
                    }

                    false -> {
                        view.showLoading(false)
                    }
                }
            }, {
                view.showLoading(false)
            })
        mCompositeDisposable?.add(disposable)
    }

    override fun subscribe() {}

    override fun unSubscribe() {
        mCompositeDisposable?.clear()
    }
}
package com.twincom.imagegenerateai

import android.net.Uri
import com.twincom.imagegenerateai.base.BasePresenter
import com.twincom.imagegenerateai.base.BaseView
import com.twincom.imagegenerateai.response.OpenAiResponse

interface MainView {
    interface View: BaseView {
        fun submitOpenAiSuccess(openAiResponse: OpenAiResponse)
    }

    interface Presenter: BasePresenter {
        fun submitOpenAi(image: Uri, number:Int, size:String)
    }
}
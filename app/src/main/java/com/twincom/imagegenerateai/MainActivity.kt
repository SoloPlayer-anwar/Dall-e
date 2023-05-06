package com.twincom.imagegenerateai

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.twincom.imagegenerateai.adapter.AdapterOpenAi
import com.twincom.imagegenerateai.databinding.ActivityMainBinding
import com.twincom.imagegenerateai.response.OpenAiResponse
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), MainView.View {
    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainView.Presenter
    private var loadingDialog: Dialog? = null
    private var filePath: Uri? = null

    private val starActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        when(activityResult.resultCode) {
            Activity.RESULT_OK -> {
                val fileDefault = activityResult.data?.data
                val bitmap = BitmapFactory.decodeStream(fileDefault.let {
                    it?.let { it1 -> contentResolver.openInputStream(it1) }
                })
                val outputStream = FileOutputStream(File(filesDir, "profile.png"))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,outputStream)
                outputStream.flush()
                outputStream.close()

                filePath = Uri.fromFile(File(filesDir, "profile.png"))
                Glide.with(this)
                    .load(fileDefault)
                    .placeholder(R.drawable.animasi)
                    .into(binding.imageEmpty)
            }

            ImagePicker.RESULT_ERROR -> {
                Snackbar.make(this, binding.constraint, ImagePicker.getError(activityResult.data), Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.green))
                    .show()
            }

            else -> {
                Snackbar.make(this, binding.constraint, "Photo di cancel", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.green))
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = MainPresenter(view = this)
        initLoading()

        binding.imageEmpty.setOnClickListener {
            ImagePicker.with(this)
                .cameraOnly()
                .cropSquare()
                .compress(1000)
                .maxResultSize(500,500)
                .createIntent {
                    starActivityForResult.launch(it)
                }
        }

        binding.ivPlus.setOnClickListener {
            var qty = binding.tvQuantity.text.toString().toInt()
            qty += 1
            binding.tvQuantity.text = qty.toString()

            if (qty > 3) {
                binding.ivPlus.isEnabled = false
                Snackbar.make(this, binding.constraint, "Maximal 4 quantity", Snackbar.LENGTH_SHORT)
                    .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.green))
                    .show()
            }
        }

        binding.ivMines.setOnClickListener {
            var qty = binding.tvQuantity.text.toString().toInt()
            if (qty > 1) qty -= 1
            binding.tvQuantity.text = qty.toString()

            if (qty < 4) {
                binding.ivPlus.isEnabled = true
            }
        }

        binding.chipCategory.setOnCheckedStateChangeListener { _, _ ->

            binding.chip256x.setOnClickListener {
                val sizeValue = binding.chip256x.text.toString()
                when(binding.chip256x.isCheckable) {
                    true -> binding.tvSizeValue.text = sizeValue
                    false -> binding.tvSizeValue.text = sizeValue
                }
            }

            binding.chip512x.setOnClickListener {
                val sizeValue = binding.chip512x.text.toString()
                when(binding.chip512x.isCheckable) {
                    true -> binding.tvSizeValue.text = sizeValue
                    false -> binding.tvSizeValue.text = sizeValue
                }
            }

            binding.chip1024x.setOnClickListener {
                val sizeValue = binding.chip1024x.text.toString()
                when(binding.chip1024x.isCheckable) {
                    true -> binding.tvSizeValue.text = sizeValue
                    false -> binding.tvSizeValue.text = sizeValue
                }
            }
        }

        binding.btnGenerate.setOnClickListener {
            val sizeImage = binding.tvSizeValue.text.toString()
            val quantity = binding.tvQuantity.text.toString()

            when {
                sizeImage == "kosong" -> {
                    Snackbar.make(this, binding.constraint, "silahkan pilih ukuran gambar", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.green))
                        .show()
                }

                filePath == null -> {
                    Snackbar.make(this, binding.constraint, "silahkan upload gambar", Snackbar.LENGTH_SHORT)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.green))
                        .show()
                }

                else -> {
                    presenter.submitOpenAi(
                        filePath!!, quantity.toInt(), sizeImage
                    )
                }
            }
        }

    }

    @SuppressLint("InflateParams")
    private fun initLoading() {
        loadingDialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.loading_dialog, null)

        loadingDialog?.let {
            it.setContentView(dialogView)
            it.setCancelable(false)
            it.setCanceledOnTouchOutside(false)
            it.window?.setBackgroundDrawableResource(R.color.tsp)
        }
    }

    override fun submitOpenAiSuccess(openAiResponse: OpenAiResponse) {
        val adapterOpenAi = AdapterOpenAi(openAiResponse.data)

        binding.LinearLayoutVis.visibility = if (openAiResponse.data.isEmpty()) View.VISIBLE else View.GONE

        binding.rvDall.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = adapterOpenAi
        }
    }

    override fun showLoading(loading: Boolean) {
        when(loading) {
            true -> loadingDialog?.show()
            false -> loadingDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        starActivityForResult.unregister()
        if (::presenter.isInitialized) {
            presenter.unSubscribe()
        }
        super.onDestroy()
    }
}
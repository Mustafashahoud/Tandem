package com.mustafa.tandem.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.util.*

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        url?.let {
            Glide.with(imageView.context)
                .load(it)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("setNativeLanguage")
    fun bindNativeLanguage(textView: TextView, natives: List<String>?) {
        if (!natives.isNullOrEmpty()) {
            textView.text = natives[0].toUpperCase(Locale.ROOT)
        }
    }

    @JvmStatic
    @BindingAdapter("setLearnLanguage")
    fun bindLearnLanguage(textView: TextView, learns: List<String>?) {
        if (!learns.isNullOrEmpty()) {
            textView.text = learns[0].toUpperCase(Locale.ROOT)
        }
    }

    @JvmStatic
    @BindingAdapter("visibilityByValue")
    fun visibilityByValue(textView: TextView, value: Int?) {
        value?.let {
            if (value > 0) {
                textView.visibility = View.VISIBLE
                textView.text = value.toString()
            } else {
                textView.visibility = View.INVISIBLE
            }
        }
    }

    @JvmStatic
    @BindingAdapter("visibilityByValueForNew")
    fun visibilityByValueForNew(imageView: ImageView, value: Int?) {
        value?.let {
            if (value == 0) {
                imageView.visibility = View.VISIBLE
            } else {
                imageView.visibility = View.INVISIBLE
            }
        }
    }
}
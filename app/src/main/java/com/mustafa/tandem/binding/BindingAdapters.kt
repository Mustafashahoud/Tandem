package com.mustafa.tandem.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mustafa.tandem.model.Member
import com.mustafa.tandem.model.Resource
import com.mustafa.tandem.model.Status
import java.util.*

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("showHideRetry")
    fun showHideRetry(view: View, resource: Resource<List<Member>>?) {
        resource?.let {
            view.visibility = if (resource.status == Status.ERROR) View.VISIBLE else View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("showHideProgressbar")
    fun showHideProgressbar(view: View, resource: Resource<List<Member>>?) {
        resource?.let {
            view.visibility = if (resource.status == Status.LOADING) View.VISIBLE else View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("snackBar")
    fun bindSnackBar(recyclerView: RecyclerView, resource: Resource<List<Member>>?) {
        resource?.let {
            if (resource.status == Status.ERROR) {
                Snackbar.make(recyclerView, resource.message!!, Snackbar.LENGTH_LONG).show()
            }
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
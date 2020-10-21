package com.mustafa.tandem.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mustafa.tandem.testing.OpenForTesting
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
@OpenForTesting
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {
    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, url: String?) {
        url?.let {
            val myUrl =
                "https://www.byrdie.com/thmb/4_qMkGKchnx8ThwW1NCiaDAZ790=/1067x800/smart/filters:no_upscale()/rihana-5178adbd22af42f9b6a745ad502c2c8e.jpg"
            val ujj =
                "https://ichef.bbci.co.uk/news/1024/cpsprodpb/C26C/production/_111927794_gettyimages-1192169655.jpg"
            Glide.with(fragment)
                .load(myUrl)
//                .apply(RequestOptions.bitmapTransform(RoundedCorners(100)))
                .into(imageView)
        }
    }
}
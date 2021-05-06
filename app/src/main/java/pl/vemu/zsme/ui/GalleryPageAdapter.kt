package pl.vemu.zsme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import com.github.chrisbanes.photoview.PhotoView

class GalleryPageAdapter(private val fragment: GalleryFragment, private val images: Array<String>) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = images.size

    override fun createFragment(position: Int): Fragment = GalleryPageFragment(fragment, images[position])

}

class GalleryPageFragment(private val fragment: GalleryFragment, private val image: String) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            PhotoView(context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                load(image)
                setOnClickListener { fragment.switchUiVisibility() }
            }
}
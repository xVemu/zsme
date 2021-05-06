package pl.vemu.zsme.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.vemu.zsme.R

class GalleryFragment : Fragment() {


    private lateinit var pager: ViewPager2
    private var isUiVisible = true

    private val args: GalleryFragmentArgs by navArgs()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        pager = ViewPager2(requireContext())
        pager.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT) //TODO remove?
        return pager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pager.adapter = GalleryPageAdapter(this, args.images)
        switchUiVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isUiVisible = false
        switchUiVisibility()
    }

    @Suppress("DEPRECATION")
    fun switchUiVisibility() {
        val decorView = requireActivity().window.decorView
        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        if (isUiVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decorView.windowInsetsController?.hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                decorView.windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
            } else {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }
            actionBar?.hide()
            bottomNav.visibility = View.GONE
            isUiVisible = false
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decorView.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                else 0
            }
            actionBar?.show()
            bottomNav.visibility = View.VISIBLE
            isUiVisible = true
        }
    }
}
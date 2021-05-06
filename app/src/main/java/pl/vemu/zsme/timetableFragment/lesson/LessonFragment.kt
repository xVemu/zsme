package pl.vemu.zsme.timetableFragment.lesson

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import pl.vemu.zsme.LessonFragmentVMFactory
import pl.vemu.zsme.R
import pl.vemu.zsme.databinding.FragmentTimetableBinding
import pl.vemu.zsme.timetableFragment.TimetableAdapter
import java.time.LocalDate
import java.util.*

class LessonFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!


    private val args: LessonFragmentArgs by navArgs()
    private val viewmodel: LessonFragmentVM by viewModels() {
        LessonFragmentVMFactory(args.url)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar: MaterialToolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.title = args.title
        val adapter = viewmodel.list.value?.let {
            TimetableAdapter(R.layout.item_lesson, it)
        }
        binding.viewPager.adapter = adapter
        binding.tabLayout.tabMode = TabLayout.MODE_AUTO
        val names = arrayOf(getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday), getString(R.string.thursday), getString(R.string.friday))
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab: TabLayout.Tab, position: Int -> tab.text = names[position] }.attach()
        viewmodel.list.observe(viewLifecycleOwner, { list ->
            adapter?.list = list
            adapter?.notifyDataSetChanged()
        })
        val day = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dayOfWeek = LocalDate.now().dayOfWeek.value
            if (dayOfWeek >= 6) 0 else dayOfWeek - 1
        } else {
            val dayOfWeek = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
            if (dayOfWeek == 1 or 7) 0 else dayOfWeek - 2
        }
        binding.viewPager.currentItem = day
    }
}
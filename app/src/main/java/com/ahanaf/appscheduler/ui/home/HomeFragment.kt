package com.ahanaf.appscheduler.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ahanaf.appscheduler.R
import com.ahanaf.appscheduler.databinding.FragmentHomeBinding
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ahanaf.appscheduler.models.ScheduleAppInfo
import com.ahanaf.appscheduler.ui.adapters.SchedulRecylerView


class HomeFragment : Fragment(), SchedulRecylerView.Interaction {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var schedulRecylerView: SchedulRecylerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)

        with(binding) {
            fab.setOnClickListener {
                gotoSchedule()
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            schedulRecylerView = SchedulRecylerView(this@HomeFragment)
            adapter = schedulRecylerView
        }

        viewModel.scheduleAppInfo.observe(viewLifecycleOwner, { scheduleAppInfo ->
            scheduleAppInfo?.let {
                schedulRecylerView.submitList(it)
            }
        })
    }

    private fun gotoSchedule() {
        navController.navigate(R.id.action_navigation_home_to_scheduleFragment)
    }

    private fun gotoScheduleToUpdate(item: ScheduleAppInfo) {
        if(this::navController.isInitialized) {
            val action = HomeFragmentDirections.actionNavigationHomeToScheduleFragment(item)
            navController.navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int, item: ScheduleAppInfo) {
        gotoScheduleToUpdate(item)
    }
}
package com.ahanaf.appscheduler.ui.allApps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahanaf.appscheduler.databinding.AllAppsFragmentBinding
import com.ahanaf.appscheduler.ui.adapters.AppsRecyclerView
import com.bumptech.glide.Glide

class AllAppsFragment : Fragment(), AppsRecyclerView.Interaction {

    companion object {
        fun newInstance() = AllAppsFragment()
    }

    private val viewModel: AllAppsViewModel by viewModels()
    private var _binding: AllAppsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var appsRecyclerView: AppsRecyclerView
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AllAppsFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        initRecyclerAdapter()
        openListOfApps()
    }

    private fun initRecyclerAdapter() {
        val requestManager = Glide.with(this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        appsRecyclerView = AppsRecyclerView(this, requestManager)
        binding.recyclerView.adapter = appsRecyclerView
    }

    private fun openListOfApps() {
        val packageManager = context?.packageManager
        val packages = packageManager?.getInstalledApplications(PackageManager.GET_META_DATA)
        val installedApplication = packages?.let { checkInstalledApps(it) }
        appsRecyclerView.submitList(installedApplication as List<ApplicationInfo>)
    }

    private fun checkInstalledApps(packages: MutableList<ApplicationInfo>): MutableList<ApplicationInfo> {
        val installedApps: MutableList<ApplicationInfo> = ArrayList()

        for (app in packages) {
            when {
                app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0 -> {
                    installedApps.add(app)
                }
                app.flags and ApplicationInfo.FLAG_SYSTEM != 0 -> {
                }
                else -> {
                    installedApps.add(app)
                }
            }
        }

        return installedApps
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(position: Int, item: ApplicationInfo) {
        gotoSchedule(item)
    }

    private fun gotoSchedule(appInfo: ApplicationInfo) {
        navController.previousBackStackEntry?.savedStateHandle?.set("appName", appInfo)
        navController.popBackStack()
    }
}
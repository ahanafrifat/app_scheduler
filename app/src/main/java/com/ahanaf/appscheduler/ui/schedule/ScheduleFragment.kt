package com.ahanaf.appscheduler.ui.schedule

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.ahanaf.appscheduler.R
import com.ahanaf.appscheduler.broadcastReceiver.AppBroadcastReceiver
import com.ahanaf.appscheduler.databinding.ScheduleFragmentBinding
import com.ahanaf.appscheduler.models.ScheduleAppInfo
import com.ahanaf.appscheduler.utils.AppUtils
import java.util.*

class ScheduleFragment : Fragment() {

    companion object {
        fun newInstance() = ScheduleFragment()
    }

    private val viewModel: ScheduleViewModel by viewModels()
    private var _binding: ScheduleFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val args: ScheduleFragmentArgs by navArgs()
    private lateinit var scheduleAppInfo: ScheduleAppInfo
    private var timeInMillis: Long? = null
    private lateinit var appName: String
    private lateinit var packageName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScheduleFragmentBinding.inflate(layoutInflater, container, false)

        setHasOptionsMenu(false)

        args.schedule?.let { scheduleApp ->

            this.scheduleAppInfo = ScheduleAppInfo(
                scheduleApp.name,
                scheduleApp.packageName,
                scheduleApp.time,
                scheduleApp.isAlarmActive,
                scheduleApp.scheduleId
            )
            this.scheduleAppInfo.id = scheduleApp.id
            setAppName(scheduleAppInfo)
            setDate(scheduleAppInfo.time!!)
            setHasOptionsMenu(true)

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)

        timeInMillis?.let {
            showDate(it)
        }

        with(binding) {
            selectTime.setOnClickListener {
                pickDateTime()
            }

            selectApp.setOnClickListener {
                gotoAllApps()
            }

            scheduleApp.setOnClickListener {
                if (appName.isNotEmpty() && packageName.isNotEmpty() && timeInMillis != null) {
                    if (!this@ScheduleFragment::scheduleAppInfo.isInitialized) {
                        createNewAlarm()
                    } else {
                        updateAlarm()
                    }
                }
            }
        }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<ApplicationInfo>("appName")
            ?.observe(viewLifecycleOwner) { applicationInfo ->
                applicationInfo?.let {
                    setAppName(applicationInfo)
                }
            }

        viewModel.message.observe(viewLifecycleOwner, { message ->
            message?.let {
                AppUtils.message(binding.root, it)
            }
        })

        viewModel.insertScheduleId.observe(viewLifecycleOwner, { insertScheduleId ->
            insertScheduleId?.let {
                setAlarmManager(appName, packageName, timeInMillis!!, it.toInt(), false)
            }
        })

        viewModel.isUpdateSuccessful.observe(viewLifecycleOwner, { isUpdateSuccessful ->
            isUpdateSuccessful?.let {
                if (it) {
                    setAlarmManager(
                        appName,
                        packageName, timeInMillis!!, scheduleAppInfo.id, false
                    )
                }
            }
        })
    }

    private fun createNewAlarm() {
        viewModel.saveSchedule(appName, packageName, timeInMillis!!)
    }

    private fun updateAlarm() {
        viewModel.updateSchedule(appName, packageName, timeInMillis!!, scheduleAppInfo)
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute, 0)
                setDate(pickedDateTime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    private fun setDate(pickedDateTime: Calendar) {
        this.timeInMillis = pickedDateTime.timeInMillis
        showDate(this.timeInMillis!!)

        if (this::scheduleAppInfo.isInitialized) {
            scheduleAppInfo.time = timeInMillis!!
        }
    }

    private fun setDate(pickedDateTime: Long) {
        this.timeInMillis = pickedDateTime
        showDate(this.timeInMillis!!)

        if (this::scheduleAppInfo.isInitialized) {
            scheduleAppInfo.time = timeInMillis!!
        }
    }

    private fun showDate(timeInMillis: Long) {
        binding.selectTime.text = AppUtils.millisLongToDateMonthYearTimeString(timeInMillis)
    }

    private fun setAppName(applicationInfo: ApplicationInfo) {
        val packageManager = context?.packageManager
        this.appName = packageManager?.let { applicationInfo.loadLabel(it).toString() }!!
        this.packageName = applicationInfo.packageName
        binding.selectApp.text = appName
    }

    private fun setAppName(scheduleAppInfo: ScheduleAppInfo) {
        this.appName = scheduleAppInfo.name!!
        this.packageName = scheduleAppInfo.packageName!!
        binding.selectApp.text = appName
    }

    private fun gotoAllApps() {
        navController.navigate(R.id.action_scheduleFragment_to_allAppsFragment)
    }

    private fun setAlarmManager(
        appName: String,
        packageName: String,
        time: Long,
        id: Int,
        isCancel: Boolean
    ) {
        val alarmnamager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AppBroadcastReceiver::class.java)
        intent.putExtra("app_name", appName)
        intent.putExtra("app_package_name", packageName)
        intent.action = "com.ahanaf.appscheduler"
        val pendingIntent =
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        if (!isCancel) {
            alarmnamager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        } else {
            alarmnamager.cancel(pendingIntent)
        }
        navController.popBackStack()
    }

    private fun alertDelete() {
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure?")
            setMessage("You can not undo this message")
            setPositiveButton("YES") { _, _ ->
                deleteAppInfo()
            }
            setNegativeButton("No") { _, _ ->

            }
        }.create().show()
    }

    private fun deleteAppInfo() {
        scheduleAppInfo.let {
            viewModel.deleteSchedule(it)
            setAlarmManager(it.name!!, it.packageName!!, it.time!!, it.id, true)
        }
        navController.popBackStack()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> alertDelete()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
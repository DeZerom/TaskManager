package com.example.taskmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.fragments.task_holders.project.ProjectFragmentDirections
import com.example.taskmanager.data.viewmodels.ProjectViewModel
import com.example.taskmanager.notifications.NotificationsBroadcastReceiver
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.*
import java.time.temporal.ChronoUnit
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters
import java.time.temporal.TemporalUnit

class MainActivity : AppCompatActivity() {
    private lateinit var mProjectViewModel: ProjectViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mProjects: List<Project>
    private lateinit var mProjectsMenuItemIds: Array<Int>
    private lateinit var mLastNavigatedProject: Project
    private val LOG_TAG = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tb = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(tb)

        val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navContr = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.settingsFragment,
            R.id.projectFragment, R.id.dayFragment, R.id.plannerFragment), drawerLayout)
        setupActionBarWithNavController(navContr, appBarConfiguration)
        navView.setupWithNavController(navContr)

        //menu items for projects
        mProjectViewModel = ViewModelProvider(this).get(ProjectViewModel::class.java)
        mProjectViewModel.allProjects.observe(this) {
            mProjects = it
            mProjectsMenuItemIds = Array(it.size) { 0 }
            val menu = navView.menu
            menu.removeGroup(R.id.projects_menu_group)

            var i = 1
            for((j, p: Project) in it.withIndex()) {
                //avoiding ids conflict in onOptionsItemSelected
                while (i == R.id.settingsFragment || i == R.id.homeFragment ||
                        i == R.id.editProjectFragment || i == R.id.addProjectFragment ||
                        i == R.id.dayFragment || i == R.id.plannerFragment) i++
                //adding MenuItem to the menu
                menu.add(R.id.projects_menu_group, i, NONE, p.name)
                //writing item id
                mProjectsMenuItemIds[j] = i++
            }
        }

        //hide editProject btn from drawer
        navView.menu.findItem(R.id.editProjectFragment).isVisible = false

        //listener for navigation from drawer
        navView.setNavigationItemSelectedListener(onNavigationItemSelectedListener)

        //alarm manager
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationsBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
            intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
            Duration.between(LocalTime.now(), LocalTime.MAX).toMillis(), pendingIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_main_menu, menu)

        //hide edit project button before accessing project fragment
        menu?.findItem(R.id.editProjectFragment)?.isVisible = false
        //hide home fragment button because it isn't supposed to be in overflow
        menu?.findItem(R.id.homeFragment)?.isVisible = false
        //hide today
        menu?.findItem(R.id.dayFragment)?.isVisible = false
        //hide planner
        menu?.findItem(R.id.plannerFragment)?.isVisible = false

        return true
    }

    //options menu (buttons in toolbar)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onNavigationItemSelectedListener.onNavigationItemSelected(item)
    }

    /**
     * Navigates to needed destination. Sets arguments if needed. Calls [navButtonsVisibilityHandler].
     * @see navButtonsVisibilityHandler
     */
    private val onNavigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener {
        val navController = findNavController(R.id.nav_host_fragment)
        var result = false

        when (it.itemId) {
            R.id.homeFragment -> {
                navController.navigate(R.id.homeFragment)
                result = true
            }
            R.id.settingsFragment -> {
                navController.navigate(R.id.settingsFragment)
                result = true
            }
            in mProjectsMenuItemIds -> {
                mLastNavigatedProject = mProjects[mProjectsMenuItemIds.indexOf(it.itemId)]
                val a = NavGraphDirections.actionGlobalProjectFragment(mLastNavigatedProject)
                navController.navigate(a)
                it.isChecked = true
                result = true
            }
            R.id.editProjectFragment -> {
                val a = ProjectFragmentDirections.actionProjectFragmentToEditProjectFragment(
                    mLastNavigatedProject)
                navController.navigate(a)
                result = true
            }
            R.id.dayFragment -> {
                navController.navigate(R.id.dayFragment)
                result = true
            }
            R.id.plannerFragment -> {
                navController.navigate(R.id.plannerFragment)
                result = true
            }
        }

        navButtonsVisibilityHandler(it.itemId)
        main_drawerLayout.closeDrawer(GravityCompat.START)

        return@OnNavigationItemSelectedListener result
    }

    /**
     * Sets visibility for [R.id.settingsFragment] and [R.id.editProjectFragment] depending on
     * destination id.
     * @param destinationId the Id of destination that is being accessed
     * @see MenuItem.setVisible
     * @see MenuItem.isVisible
     */
    private fun navButtonsVisibilityHandler(@IdRes destinationId: Int) {
        //get buttons
        val editProject = toolbar.menu.findItem(R.id.editProjectFragment)
        val settings = toolbar.menu.findItem(R.id.settingsFragment)

        //save old state
        val oldEditVisibility = editProject.isVisible
        val oldSettingsVisibility = settings.isVisible

        //set them invisible
        editProject.isVisible = false
        settings.isVisible = false

        //set them visible again if needed
        when (destinationId) {
            R.id.homeFragment -> {
                settings.isVisible = true
            }
            R.id.settingsFragment -> {/* do nothing */}
            in mProjectsMenuItemIds -> {
                editProject.isVisible = true
                settings.isVisible = true
            }
            R.id.editProjectFragment -> {
                settings.isVisible = true
            }
            R.id.dayFragment -> {
                settings.isVisible = true
            }
            R.id.plannerFragment -> {
                editProject.isVisible = false
                settings.isVisible = true
            }
            else -> {
                //use old state
                editProject.isVisible = oldEditVisibility
                settings.isVisible = oldSettingsVisibility
            }
        }
    }

    companion object {
        const val ALARM_REQUEST_CODE = 1001
    }
}
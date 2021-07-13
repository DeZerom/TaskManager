package com.example.taskmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.viewmodels.ProjectViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mProjectModel: ProjectViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mProjects: List<Project>
    private lateinit var mProjectsMenuItemIds: Array<Int>
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
            R.id.projectFragment), drawerLayout)
        setupActionBarWithNavController(navContr, appBarConfiguration)
        navView.setupWithNavController(navContr)

        //menu items for projects
        mProjectModel = ViewModelProvider(this).get(ProjectViewModel::class.java)
        mProjectModel.allProjects.observe(this) {
            mProjects = it
            mProjectsMenuItemIds = Array(it.size) { 0 }
            val menu = navView.menu
            menu.removeGroup(R.id.projects_menu_group)

            var i = 1
            for((j, p: Project) in it.withIndex()) {
                //avoiding ids conflict in onOptionsItemSelected
                while (i == R.id.settingsFragment || i == R.id.homeFragment) i++
                //adding MenuItem to the menu
                menu.add(R.id.projects_menu_group, i, NONE, p.name)
                //writing item id and incrementing it just after
                mProjectsMenuItemIds[j] = i++
            }
        }

        //listener for navigation
        navView.setNavigationItemSelectedListener {
            //navigate home fragment and settings fragment
            when(it.itemId) {
                R.id.homeFragment -> {
                    navContr.navigate(R.id.homeFragment)
                    return@setNavigationItemSelectedListener true
                }
                R.id.settingsFragment -> {
                    navContr.navigate(R.id.settingsFragment)
                    return@setNavigationItemSelectedListener true
                }
            }

            //navigation to projects pages
            if (mProjectsMenuItemIds.contains(it.itemId)) {
                val proj = mProjects[mProjectsMenuItemIds.indexOf(it.itemId)]
                val action = NavGraphDirections.actionGlobalProjectFragment(proj)
                toolbar.title = proj.name
                navContr.navigate(action)

                return@setNavigationItemSelectedListener true
            }

            return@setNavigationItemSelectedListener false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        when(item.itemId) {
            R.id.homeFragment -> {
                navController.navigate(R.id.homeFragment)
                return true
            }
            R.id.settingsFragment -> {
                navController.navigate(R.id.settingsFragment)
                return true
            }
        }

        return false
    }
}
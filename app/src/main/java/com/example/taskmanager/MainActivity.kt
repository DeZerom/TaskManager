package com.example.taskmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.core.view.contains
import androidx.core.view.iterator
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.data.project.ProjectViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var mProjectModel: ProjectViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var projects: List<Project>
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
            val menu = navView.menu
            menu.removeGroup(R.id.projects_menu_group)

            for(p: Project in it) {
                menu.add(R.id.projects_menu_group, NONE, NONE, p.name)
            }
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
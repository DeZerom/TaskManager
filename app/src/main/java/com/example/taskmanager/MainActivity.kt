package com.example.taskmanager

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgument
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.taskmanager.data.project.Project
import com.example.taskmanager.fragments.task_holders.project.ProjectFragmentArgs
import com.example.taskmanager.fragments.task_holders.project.ProjectFragmentDirections
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
            R.id.projectFragment, R.id.dayFragment), drawerLayout)
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
                while (i == R.id.settingsFragment || i == R.id.homeFragment ||
                    i == R.id.editProjectFragment) i++
                //adding MenuItem to the menu
                menu.add(R.id.projects_menu_group, i, NONE, p.name)
                //writing item id
                mProjectsMenuItemIds[j] = i++
            }
        }

        //listener for navigation
        //from drawer
        navView.setNavigationItemSelectedListener {
            //navigate home fragment and settings fragment
            if (tryToNavigateToMainDest(it)) {
                drawerLayout.closeDrawer(GravityCompat.START)
                return@setNavigationItemSelectedListener true
            }
            //todo tmp solution for navigating dayFragment
            if (it.itemId == R.id.dayFragment) {
                navContr.navigate(R.id.dayFragment)
            }
            //navigation to projectFragment
            if (mProjectsMenuItemIds.contains(it.itemId)) {
                val proj = mProjects[mProjectsMenuItemIds.indexOf(it.itemId)]
                val action = NavGraphDirections.actionGlobalProjectFragment(proj)

                //making edit project button visible
                //it will be hidden again when navigating to other fragments
                toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = true

                navContr.navigate(action)
                it.isChecked = true

                //hide drawer
                drawerLayout.closeDrawer(GravityCompat.START)
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
        //hide edit project button before accessing project fragment
        menu?.findItem(R.id.editProjectFragment)?.isVisible = false
        //hide home fragment button because it isn't supposed to be in overflow
        menu?.findItem(R.id.homeFragment)?.isVisible = false
        //hide editFragment button
        menu?.findItem(R.id.editProjectFragment)?.isVisible = false

        return true
    }

    //options menu (buttons in toolbar)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (tryToNavigateToMainDest(item)) return true
        val navContr = findNavController(R.id.nav_host_fragment)

        //navigating to editProjectFragment
        when (item.itemId) {
            //editProjectFragment
            R.id.editProjectFragment -> {
                if (navContr.currentDestination?.id == R.id.projectFragment) {
                    // TODO avoid this pornography with toolbar.title
                    //get Project instance that is being representing by current projectFragment
                    val projName =
                        toolbar.title.toString() //proj name has been written in title
                    val proj = mProjects.find { p ->
                        return@find p.name == projName
                    }

                    //navigating
                    proj?.let {
                        val a = ProjectFragmentDirections
                            .actionProjectFragmentToEditProjectFragment(proj)
                        navContr.navigate(a)

                        return true
                    }
                }
            }
        }

        return false
    }

    /**
     * Tries to navigate to [R.layout.home_fragment] or to [R.xml.root_preferences]
     * @return true if navigated successful, false otherwise
     */
    private fun tryToNavigateToMainDest(item: MenuItem): Boolean {
        val navContr = findNavController(R.id.nav_host_fragment)

        when(item.itemId) {
            R.id.homeFragment -> {
                //hide edit project button
                toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = false
                navContr.navigate(R.id.homeFragment)
                return true
            }
            R.id.settingsFragment -> {
                //hide edit project button
                toolbar?.menu?.findItem(R.id.editProjectFragment)?.isVisible = false
                navContr.navigate(R.id.settingsFragment)
                return true
            }

        }

        return false
    }
}
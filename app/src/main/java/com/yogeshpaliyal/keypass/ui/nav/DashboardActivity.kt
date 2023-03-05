package com.yogeshpaliyal.keypass.ui.nav

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.transition.MaterialElevationScale
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.databinding.ActivityDashboardBinding
import com.yogeshpaliyal.keypass.ui.addTOTP.AddTOTPActivity
import com.yogeshpaliyal.keypass.ui.detail.DetailActivity
import com.yogeshpaliyal.keypass.ui.generate.GeneratePasswordActivity
import com.yogeshpaliyal.keypass.ui.home.DashboardViewModel
import com.yogeshpaliyal.keypass.ui.home.HomeFragmentDirections
import com.yogeshpaliyal.keypass.ui.settings.MySettingsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity :
    AppCompatActivity(),
    Toolbar.OnMenuItemClickListener,
    NavController.OnDestinationChangedListener,
    NavigationAdapter.NavigationAdapterListener {
    lateinit var binding: ActivityDashboardBinding

    private val bottomNavDrawer: BottomNavDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as BottomNavDrawerFragment
    }

    private val mViewModel by viewModels<DashboardViewModel>()

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setSupportActionBar(binding.bottomAppBar)

        binding.lifecycleOwner = this
        binding.viewModel = mViewModel

        navController.addOnDestinationChangedListener(
            this@DashboardActivity
        )

        binding.btnAdd.setOnClickListener {
            currentNavigationFragment?.apply {
                exitTransition = MaterialElevationScale(false).apply {
                    duration =
                        resources.getInteger(R.integer.keypass_motion_duration_large).toLong()
                }
                reenterTransition = MaterialElevationScale(true).apply {
                    duration =
                        resources.getInteger(R.integer.keypass_motion_duration_large).toLong()
                }
            }

            DetailActivity.start(this)
        }

        bottomNavDrawer.apply {
            // addOnSlideAction(HalfClockwiseRotateSlideAction(binding.bottomAppBar))
            //  addOnSlideAction(AlphaSlideAction(binding.bottomAppBarTitle, true))
            addOnStateChangedAction(ShowHideFabStateAction(binding.btnAdd))
            addOnStateChangedAction(
                ChangeSettingsMenuStateAction { showSettings ->
                    // Toggle between the current destination's BAB menu and the menu which should
                    // be displayed when the BottomNavigationDrawer is open.
                    binding.bottomAppBar.replaceMenu(
                        if (showSettings) {
                            R.menu.bottom_app_bar_settings_menu
                        } else {
                            getBottomAppBarMenuForDestination()
                        }
                    )
                }
            )

            // addOnSandwichSlideAction(HalfCounterClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addNavigationListener(this@DashboardActivity)
        }

        // Set up the BottomAppBar menu
        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                bottomNavDrawer.toggle()
            }
            setOnMenuItemClickListener(this@DashboardActivity)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> {
                val settingDestination = MySettingsFragmentDirections.actionGlobalSettings()
                navController.navigate(settingDestination)
                bottomNavDrawer.close()
            }
        }
        return true
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

        binding.searchAppBar.isVisible = destination.id == R.id.homeFragment
        when (destination.id) {
            R.id.homeFragment -> {
                binding.btnAdd.isActivated = false
                setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            }
        }
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        // Swap the list of emails for the given mailbox
        // navigateToHome(item.titleRes, item.mailbox)
        when (item.id) {
            NavigationModel.GENERATE_PASSWORD -> {
                val intent = Intent(this, GeneratePasswordActivity::class.java)
                startActivity(intent)
            }
            NavigationModel.HOME -> {
                val args = HomeFragmentDirections.actionGlobalHomeFragment()
                navController.navigate(args)
            }
            NavigationModel.ADD_TOPT -> {
                AddTOTPActivity.start(this)
            }
        }
    }

    override fun onNavEmailFolderClicked(folder: NavigationModelItem.NavEmailFolder) {
        mViewModel.tag.postValue(folder.category)
        val destination = HomeFragmentDirections.actionGlobalHomeFragmentTag()
        navController.navigate(destination)
        bottomNavDrawer.close()
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            // Get a handle on the animator that hides the bottom app bar so we can wait to hide
            // the fab and bottom app bar until after it's exit animation finishes.
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator) {
                    if (isCanceled) return

                    // Hide the BottomAppBar to avoid it showing above the keyboard
                    // when composing a new email.
                    bottomAppBar.visibility = View.GONE
                    btnAdd.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {
                    isCanceled = true
                }
            })
        }
    }

    /**
     * Helper function which returns the menu which should be displayed for the current
     * destination.
     *
     * Used both when the destination has changed, centralizing destination-to-menu mapping, as
     * well as switching between the alternate menu used when the BottomNavigationDrawer is
     * open and closed.
     */
    @MenuRes
    private fun getBottomAppBarMenuForDestination(destination: NavDestination? = null): Int {
        val dest = destination ?: navController.currentDestination
        return when (dest?.id) {
            R.id.homeFragment -> R.menu.bottom_app_bar_settings_menu
            // R.id.emailFragment -> R.menu.bottom_app_bar_email_menu
            else -> R.menu.bottom_app_bar_settings_menu
        }
    }

    private fun setBottomAppBarForHome(@MenuRes menuRes: Int) {
        binding.run {
            btnAdd.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.replaceMenu(menuRes)
            // btnAdd.contentDescription = getString(R.string.fab_compose_email_content_description)
            // bottomAppBarTitle.visibility = View.VISIBLE
            bottomAppBar.performShow()
            btnAdd.show()
        }
    }
}

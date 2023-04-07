package ru.netology.nmedia.activity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.EditPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.util.SignOutDialogFragment
import ru.netology.nmedia.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
        }

        val authViewModel by viewModels<AuthViewModel>()

        var previousMenuProvider: MenuProvider? = null
        authViewModel.data.observe(this){
            previousMenuProvider?.let(::removeMenuProvider)

            addMenuProvider(object : MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)

                    menu.setGroupVisible(R.id.unauthorization, !authViewModel.authorized)
                    menu.setGroupVisible(R.id.authorized, authViewModel.authorized)
                }


                override fun onMenuItemSelected(menuItem: MenuItem): Boolean  =
                    when(menuItem.itemId){
                        R.id.signIn ->{
//                            val navHostFragment =
//                                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//                            val navController = navHostFragment.navController
//                            navController.navigate(R.id.action_feedFragment_to_signInFragment)
                            AppAuth.getInstance().setAuth(5, "x-token")
                            true
                        }
                        R.id.signUp ->{
                            val navHostFragment =
                                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                            val navController = navHostFragment.navController
                            navController.navigate(R.id.action_feedFragment_to_signUpFragment)
                            true
                        }
                        R.id.signOut -> {
                            SignOutDialogFragment().show(supportFragmentManager, getString(R.string.sign_out))
                            true
                        }else -> false
                    }

            }.also {
                previousMenuProvider = it
            })
        }

        checkGoogleApiAvailability()
    }
    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, "Google Api Unavailable", Toast.LENGTH_LONG).show()
        }
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
        }
    }
}

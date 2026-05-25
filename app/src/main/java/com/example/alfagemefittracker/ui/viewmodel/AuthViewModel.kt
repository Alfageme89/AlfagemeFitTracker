package com.example.alfagemefittracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(context: Context) : ViewModel() {

    private val account = Auth0(
        context.getString(com.example.alfagemefittracker.R.string.com_auth0_client_id),
        context.getString(com.example.alfagemefittracker.R.string.com_auth0_domain)
    )
    
    private val client = AuthenticationAPIClient(account)
    private val storage = SharedPreferencesStorage(context)
    private val credentialsManager = CredentialsManager(client, storage)

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(credentialsManager.hasValidCredentials())
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        if (isAuthenticated.value) {
            fetchUserProfile()
        }
    }

    fun login(context: Context) {
        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email")
            .start(context, object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    _errorMessage.value = error.message
                    _isAuthenticated.value = false
                }

                override fun onSuccess(result: Credentials) {
                    credentialsManager.saveCredentials(result)
                    _isAuthenticated.value = true
                    _errorMessage.value = null
                    fetchUserProfile()
                }
            })
    }

    fun logout(context: Context) {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(context, object : Callback<Void?, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    _errorMessage.value = error.message
                }

                override fun onSuccess(result: Void?) {
                    credentialsManager.clearCredentials()
                    _userProfile.value = null
                    _isAuthenticated.value = false
                }
            })
    }

    private fun fetchUserProfile() {
        credentialsManager.getCredentials(object : Callback<Credentials, CredentialsManagerException> {
            override fun onSuccess(result: Credentials) {
                val token = result.accessToken
                client.userInfo(token)
                    .start(object : Callback<UserProfile, AuthenticationException> {
                        override fun onSuccess(profile: UserProfile) {
                            _userProfile.value = profile
                        }
                        override fun onFailure(error: AuthenticationException) {
                            _errorMessage.value = "Error al cargar perfil"
                        }
                    })
            }
            override fun onFailure(error: CredentialsManagerException) {
                _isAuthenticated.value = false
            }
        })
    }
}

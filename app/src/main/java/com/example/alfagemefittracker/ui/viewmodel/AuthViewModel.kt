
package com.example.alfagemefittracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data class para guardar la información del perfil del usuario
data class UserProfile(
    val name: String?,
    val email: String?,
    val picture: String?
)

class AuthViewModel(private val account: Auth0) : ViewModel() {

    private val _credentials = MutableStateFlow<Credentials?>(null)
    val credentials = _credentials.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    fun login(context: Context) {
        WebAuthProvider.login(account)
            .withScheme("https")
            .withScope("openid profile email")
            .start(context, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    _credentials.value = result
                    // Decodificar el token para obtener el perfil
                    val jwt = JWT(result.idToken)
                    _userProfile.value = UserProfile(
                        name = jwt.getClaim("name").asString(),
                        email = jwt.getClaim("email").asString(),
                        picture = jwt.getClaim("picture").asString()
                    )
                }

                override fun onFailure(error: AuthenticationException) {
                    // Handle error
                }
            })
    }

    fun logout(context: Context) {
        WebAuthProvider.logout(account)
            .withScheme("https")
            .start(context, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    // Limpiar credenciales y perfil al cerrar sesión
                    _credentials.value = null
                    _userProfile.value = null
                }

                override fun onFailure(error: AuthenticationException) {
                    // Handle error
                }
            })
    }
}

class AuthViewModelFactory(private val account: Auth0) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(account) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

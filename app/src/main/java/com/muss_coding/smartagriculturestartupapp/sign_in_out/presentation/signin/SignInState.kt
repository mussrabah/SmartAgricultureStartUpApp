package com.muss_coding.smartagriculturestartupapp.sign_in_out.presentation.signin

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

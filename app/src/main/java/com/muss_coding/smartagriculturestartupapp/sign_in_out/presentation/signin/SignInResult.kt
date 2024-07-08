package com.muss_coding.smartagriculturestartupapp.sign_in_out.presentation.signin

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val userName: String?,
    val profilePictureUrl: String?
)

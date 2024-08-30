package com.manoj.base.core.common.sociallogin.googlelogin

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.manoj.base.BuildConfig
import com.manoj.base.core.common.singletonholder.SingletonHolder
import com.manoj.base.core.network.helper.apihelper.Result
import com.manoj.base.core.utils.dispatchers.DispatchersProvider
import com.manoj.base.core.utils.extension.toJson
import com.manoj.base.data.local.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import java.security.MessageDigest
import java.util.UUID

class GoogleSignInManager(
    private val context: Context,
    private val credentialManager: CredentialManager?,
    private val dispatchersProvider: DispatchersProvider,
    private val dataStoreManager: DataStoreManager?
) {
    data class GoogleSignInParams(
        val context: Context,
        val credentialManager: CredentialManager?,
        val dispatchersProvider: DispatchersProvider, val dataStoreManager: DataStoreManager?
    )

    companion object : SingletonHolder<GoogleSignInManager, GoogleSignInParams>({
        GoogleSignInManager(
            it.context,
            it.credentialManager,
            it.dispatchersProvider, it.dataStoreManager
        )
    })

    /**
     * Initiates the Google sign-in process and returns a Flow emitting SignInResult.
     * This approach uses Kotlin Flow to perform the sign-in operation asynchronously and emit results.
     */
    fun signInWithGoogle(): Flow<Result<UserData>> = flow {
        val googleSignRequest = createGoogleSignInRequest()

        try {
            val result = credentialManager?.getCredential(
                request = googleSignRequest,
                context = context
            )
            emit(handleSignIn(result))
        } catch (e: GetCredentialException) {
            emit(Result.Error("Unexpected type of credential: ${e.message}"))
        }
    }.onStart { emit(Result.Loading) }
        .flowOn(dispatchersProvider.getIO()) // Run on IO thread to avoid blocking the main thread

    private fun createGoogleSignInRequest(): GetCredentialRequest {
        val googleIdOption = createGoogleIdOption()
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private fun createGoogleIdOption(): GetGoogleIdOption {
        val hashedNonce = generateNonce()
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false).setRequestVerifiedPhoneNumber(true)
            .setServerClientId(BuildConfig.web_client_id)
            .setAutoSelectEnabled(false)
            .setNonce(hashedNonce)
            .build()
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    /**
     * Handles the sign-in result and returns a Result.
     */
    private suspend fun handleSignIn(result: GetCredentialResponse?): Result<UserData> {
        return when (val credential = result?.credential) {
            is CustomCredential -> handleCustomCredential(credential)
            else -> Result.Error("Unexpected type of credential")
        }
    }

    private suspend fun handleCustomCredential(credential: CustomCredential): Result<UserData> {
        return if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val user = UserData(
                    googleIdTokenCredential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID"),
                    googleIdTokenCredential.displayName,
                    googleIdTokenCredential.profilePictureUri.toString(),
                    googleIdTokenCredential.phoneNumber
                )
                dataStoreManager?.saveUser(user)
                dataStoreManager?.saveAccessToken(googleIdTokenCredential.toString())
                Result.Success(user)
            } catch (e: GoogleIdTokenParsingException) {
                Result.Error("Received an invalid Google ID token response: ${e.message}")
            }
        } else {
            Result.Error("Unrecognized custom credential type")
        }
    }

    data class UserData(
        val email: String?,
        val name: String?,
        val profilePictureUrl: String?,
        val phone: String?,
    )

    fun signOut(): Flow<Result<String>> = flow {
        try {
            val result =
                credentialManager?.clearCredentialState(createClearCredentialStateRequest())
            dataStoreManager?.clearUser()
            emit(Result.Success(result.toJson()))
        } catch (e: ClearCredentialException) {
            emit(Result.Error(e.message.toString()))
        }
    }.onStart { emit(Result.Loading) }
        .flowOn(dispatchersProvider.getIO())

    private fun createClearCredentialStateRequest(): ClearCredentialStateRequest =
        ClearCredentialStateRequest()

    fun getAddGoogleAccountIntent(): Intent {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        return intent
    }
}




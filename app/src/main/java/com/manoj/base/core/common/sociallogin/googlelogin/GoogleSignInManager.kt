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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import java.security.MessageDigest
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class GoogleSignInManager(
    private val context: Context,
    private val credentialManager: CredentialManager?,
    private val dispatchersProvider: DispatchersProvider
) {
    data class GoogleSignInParams(
        val context: Context,
        val credentialManager: CredentialManager?,
        val dispatchersProvider: DispatchersProvider
    )

    companion object : SingletonHolder<GoogleSignInManager, GoogleSignInParams>({
        GoogleSignInManager(
            it.context,
            it.credentialManager,
            it.dispatchersProvider
        )
    })

    /**
     * Initiates the Google sign-in process and returns a Flow emitting SignInResult.
     * This approach uses Kotlin Flow to perform the sign-in operation asynchronously and emit results.
     */
    fun signInWithGoogle(): Flow<Result<UserData>> = flow {
        val googleSignRequest = createGoogleSignInRequest()

        try {
            // Asynchronously request credentials using the CredentialManager
            val result = credentialManager?.getCredential(
                request = googleSignRequest,
                context = context
            )
            // Emit success if the sign-in is successful
            emit(handleSignIn(result))
        } catch (e: GetCredentialException) {
            // Emit failure if an exception is encountered
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
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.web_client_id)
            .setAutoSelectEnabled(true)
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
    private fun handleSignIn(result: GetCredentialResponse?): Result<UserData> {
        return when (val credential = result?.credential) {
            is CustomCredential -> handleCustomCredential(credential)
            else -> Result.Error("Unexpected type of credential")
        }
    }

    private fun handleCustomCredential(credential: CustomCredential): Result<UserData> {
        return if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.Success(
                    UserData(
                        googleIdTokenCredential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID"),
                        googleIdTokenCredential.displayName,
                        googleIdTokenCredential.profilePictureUri.toString(),
                        googleIdTokenCredential.idToken
                    )
                )
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
        val idToken: String?,
    )

    fun signOut(): Flow<Result<String>> = flow {
        try {
            val result =
                credentialManager?.clearCredentialState(createClearCredentialStateRequest())
            emit(Result.Success(result.toJson()))
        } catch (e: ClearCredentialException) {
            emit(Result.Error(e.message.toString()))
        }
    }.onStart { emit(Result.Loading) }
        .flowOn(dispatchersProvider.getIO())

    private fun createClearCredentialStateRequest(): ClearCredentialStateRequest =
        ClearCredentialStateRequest()

}




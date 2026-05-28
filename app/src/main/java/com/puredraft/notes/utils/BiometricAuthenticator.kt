package com.puredraft.notes.utils

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object BiometricAuthenticator {

    sealed class Result {
        object Success : Result()
        data class Error(val errCode: Int, val errString: CharSequence) : Result()
        object Failed : Result()
        object NotAvailable : Result()
    }

    suspend fun authenticate(activity: FragmentActivity, title: String = "Unlock Note", subtitle: String = "Confirm your identity to view this note."): Result {
        val biometricManager = BiometricManager.from(activity)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        
        if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
            return Result.NotAvailable
        }

        return suspendCancellableCoroutine { continuation ->
            val executor = ContextCompat.getMainExecutor(activity)
            val biometricPrompt = BiometricPrompt(
                activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (continuation.isActive) {
                            continuation.resume(Result.Error(errorCode, errString))
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        if (continuation.isActive) {
                            continuation.resume(Result.Success)
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // Don't resume on failure, it will prompt again or eventually error out.
                        // We only return Result.Failed if we want to immediately end it, but let the prompt handle retries.
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(promptInfo)
            
            continuation.invokeOnCancellation {
                biometricPrompt.cancelAuthentication()
            }
        }
    }
}

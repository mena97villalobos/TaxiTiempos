package com.mena97villalobos.taxitiempos.authentication

import androidx.biometric.BiometricPrompt

class BioAuthentication (
    private val authError: () -> Unit,
    private val authSuccess: () -> Unit,
    private val authFail: () -> Unit
) : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
        authError()
    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
        authSuccess()
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        authFail
    }
}
package com.example.proyectoseminario.data.preferences

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SessionManagerTest {

    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        sessionManager = SessionManager(context)
    }

    @Test
    fun `saveSession persiste datos y clearSession los borra`() = runTest {
        sessionManager.saveSession(1, "test@mail.com", "Test User")

        assertTrue(sessionManager.isLoggedIn.first())
        assertEquals("test@mail.com", sessionManager.currentUserEmail.first())
        assertEquals(1, sessionManager.currentUserId.first())

        sessionManager.clearSession()

        assertFalse(sessionManager.isLoggedIn.first())
        assertEquals(0, sessionManager.currentUserId.first())
    }

    @Test
    fun `onboarding se marca completado`() = runTest {
        assertFalse(sessionManager.hasCompletedOnboarding())

        sessionManager.setOnboardingCompleted()

        assertTrue(sessionManager.hasCompletedOnboarding())
    }
}

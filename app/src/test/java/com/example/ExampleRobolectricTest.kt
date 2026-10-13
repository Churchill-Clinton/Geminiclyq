package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.SocialRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches app name C`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("C", appName)
    }

    @Test
    fun `repository initializes with seed posts and toggles like`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repository = SocialRepository(db.socialDao())
        repository.ensureInitialized()

        // Initial posts should exist
        val posts = repository.getAllPostsFlow().first()
        assertTrue("Seed posts should not be empty", posts.isNotEmpty())

        val firstPost = posts.first()
        val originalLikes = firstPost.likes
        val originalLiked = firstPost.liked

        repository.toggleLikePost(firstPost.id)

        val updatedPosts = repository.getAllPostsFlow().first()
        val updatedPost = updatedPosts.first { it.id == firstPost.id }

        assertEquals(!originalLiked, updatedPost.liked)
        if (!originalLiked) {
            assertEquals(originalLikes + 1, updatedPost.likes)
        }
    }
}

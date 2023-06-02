package com.seftian.storyapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.seftian.storyapp.BuildConfig
import com.seftian.storyapp.data.local.UserDatabase
import com.seftian.storyapp.data.remote.NotesApi
import com.seftian.storyapp.di.token.TokenProvider
import com.seftian.storyapp.di.token.TokenProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesApi(tokenProvider: TokenProvider): NotesApi{

        val loggingInterceptor =
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

        val bearerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            if (originalRequest.url.encodedPath == "/login") {
                return@Interceptor chain.proceed(originalRequest)
            }

            if (originalRequest.url.encodedPath == "/register") {
                return@Interceptor chain.proceed(originalRequest)
            }

            val token = tokenProvider.getToken()
            val modifiedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(modifiedRequest)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(bearerInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(NotesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NotesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideTokenProvider(impl: TokenProviderImpl): TokenProvider = impl
}
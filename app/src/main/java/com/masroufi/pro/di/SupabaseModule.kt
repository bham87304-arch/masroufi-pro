package com.masroufi.pro.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    private const val SUPABASE_URL = "https://jtjlggbmiydgrcrlavvk.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imp0amxnZ2JtaXlkZ3JjcmxhdnZrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODk4Mjc4MzYsImV4cCI6MjEwNTQwMzgzNn0.FmNghgkh19AQPzq2IWNQVMnKiEwa14q9VJBDpCkN5UY"

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

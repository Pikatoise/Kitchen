package com.example.kitchen.supabase

import com.google.android.datatransport.runtime.dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @OptIn(SupabaseInternal::class)
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://gkeqyqnfnwgcbpgbnxkq.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdrZXF5cW5mbndnY2JwZ2JueGtxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTU5NDI3NjgsImV4cCI6MjAzMTUxODc2OH0.wkGX4ZbmEYC2A5ThtPJxe_f0xXpK0uFQ-7lP8n6hdPE"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseDatabase(): Postgrest {
        return provideSupabaseClient().postgrest
    }

    @Provides
    @Singleton
    fun provideSupabaseStorage(client: SupabaseClient): Storage {
        return client.storage
    }

}
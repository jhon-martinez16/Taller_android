package com.example.taller.ui

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage


object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://kfoezkkdctwepoiaijgb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtmb2V6a2tkY3R3ZXBvaWFpamdiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU5NDgwNTYsImV4cCI6MjA5MTUyNDA1Nn0.ilVUPFThiOkdqH_6gzk0jAmJ5ekCju8ER9ykBSzo_Yo"
    ){
        install(plugin = Auth)
        install(plugin = Postgrest)
        install(Storage)

    }
}
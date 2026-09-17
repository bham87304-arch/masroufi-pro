# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt
-keep,allowobfuscation,allowshrinking interface dagger.hilt.internal.GeneratedEntryPoint
-keep,allowobfuscation,allowshrinking interface dagger.hilt.internal.GeneratedComponentManager

# Supabase / Ktor
-keep class io.ktor.** { *; }
-keep class io.github.jan-tennert.supabase.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,allowinitialization,keepemptyclassmembers,allowobfuscation,allowoptimization class kotlinx.serialization.** {
    <methods>;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
    @kotlinx.serialization.Transient *;
}

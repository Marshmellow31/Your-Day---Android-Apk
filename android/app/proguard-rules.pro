# Firebase — keep model classes and annotations
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel *;
}

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# Gson (for Room converters)
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Your app data models
-keep class com.yourday.app.data.model.** { *; }

# Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Compose
-keep class androidx.compose.** { *; }

# Prevent stripping of companion objects
-keepclassmembers class ** {
    public static ** INSTANCE;
    public static ** Companion;
}

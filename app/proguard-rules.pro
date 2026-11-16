# Project specific ProGuard rules

# Keep Data & Domain Models
-keep class com.chibidoll.dressup.domain.model.** { *; }
-keep class com.chibidoll.dressup.data.local.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep class * extends androidx.room.Dao { *; }
-dontwarn androidx.room.paging.**

# Coil Image Loader
-dontwarn coil.**
-keep class coil.** { *; }

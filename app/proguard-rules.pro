# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep public class org.savit.savitauthenticator.network.KtorClient
-keep public class org.savit.savitauthenticator.network.*
-keep public class org.savit.savitauthenticator.userAccount.*
-keep public class org.savit.savitauthenticator.*
-keepnames class org.savit.savitauthenticator.** { *; }
-keep public class * extends android.arch.persistence.room.RoomDatabase
-dontwarn android.arch.persistence.room.paging.**
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder

#Sql
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }

-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

-keep class androidx.core.database.sqlite.** { *; }

# keep everything in this package from being renamed only
-keepnames class androidx.core.database.sqlite.** { *; }



# keep the class and specified members from being removed or renamed
-keep class androidx.sqlite.db.SupportSQLiteDatabaseKt { *; }

# keep the specified class members from being removed or renamed
# only if the class is preserved
-keepclassmembers class androidx.sqlite.db.SupportSQLiteDatabaseKt { *; }

# keep the class and specified members from being renamed only
-keepnames class androidx.sqlite.db.SupportSQLiteDatabaseKt { *; }

# keep the specified class members from being renamed only
-keepclassmembernames class androidx.sqlite.db.SupportSQLiteDatabaseKt { *; }

-keep class androidx.sqlite.db.SupportSQLiteQueryBuilder { *; }

# keep the specified class members from being removed or renamed
# only if the class is preserved
-keepclassmembers class androidx.sqlite.db.SupportSQLiteQueryBuilder { *; }

# keep the class and specified members from being renamed only
-keepnames class androidx.sqlite.db.SupportSQLiteQueryBuilder { *; }

# keep the specified class members from being renamed only
-keepclassmembernames class androidx.sqlite.db.SupportSQLiteQueryBuilder { *; }


-keep class io.ktor.server.netty.EngineMain
-keep class kotlin.reflect.jvm.internal.**
-keep class kotlin.text.RegexOption
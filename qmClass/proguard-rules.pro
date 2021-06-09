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

#指定压缩级别
-optimizationpasses 5

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
#保持泛型
-keepattributes Signature


#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

-keep class com.qm.qmclass.qmmanager.QMClassManager{ public <methods>; }

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

-keep class com.qm.qmclass.okhttp.** { *;}

#--------------------------2.第三方包-------------------------------

    -keep class com.tencent.** { *; }
    -keep class com.tencent.imsdk.** { *; }
    -keep class com.tencent.teduboard.** { *; }
    -keep class com.huawei.multimedia.liteav.** { *; }
    -keep class com.tencent.tiw.logger.** { *; }

    # FastJson 混淆代码
    -dontwarn com.alibaba.fastjson.**
    -keep class com.alibaba.fastjson.** { *; }
    -keepattributes Signature


    #Gson
    ##---------------Begin: proguard configuration for Gson  ----------
    # Gson uses generic type information stored in a class file when working with fields. Proguard
    # removes such information by default, so configure it to keep all of it.
    -keepattributes Signature

    # For using GSON @Expose annotation
    -keepattributes *Annotation*

    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
    -keep class com.google.gson.** { *; }

    # Application classes that will be serialized/deserialized over Gson
    -keep class com.qm.qmclass.model.** { *;}
    ##---------------End: proguard configuration for Gson  ----------

    ##Glide
    -dontwarn com.bumptech.glide.**
    -keep class com.bumptech.glide.**{*;}
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public class * extends com.bumptech.glide.AppGlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
      **[] $VALUES;
      public *;
    }

#-------------- okhttp3 start-------------

# OkHttp3
# ----------------------------okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#----------------------------- okio
-dontwarn okio.**
-keep class okio.**{*;}
#----------okhttp end--------------

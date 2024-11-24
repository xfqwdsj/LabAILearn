-dontoptimize
-dontobfuscate

-dontwarn kotlinx.**

-keepclasseswithmembers public class top.ltfan.labailearn.MainKt {
    public static void main(java.lang.String[]);
}
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }

# SpeechSuper SDK Android Java SDK Demo

This demo illustrates how to integrate the SpeechSuper Android SDK into your Android app for pronunciation assessment. Follow these steps to get started:

## Step 1: Configure Your Keys
1. Open the file `app/src/main/java/com/example/recorder/MainActivity.java`.
2. Insert your `appKey` and `secretKey` into the following lines:

   ```java
   public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

       private static String appKey = "Insert your appKey here";
       private static String secretKey = "Insert your secretKey here";
    ```

## Step 2: Customize Your Inputs
1. Open the file `app/src/main/java/com/example/recorder/TestActivity.java`.
2. Modify the input parameters according to your requirements in the `startSkegn` method:

```java
   public int startSkegn(String engineType, JSONObject requests, CallbackResult callbackResult)  {
      ...

      try {
            JSONObject params = new JSONObject();
            JSONObject appObj = new JSONObject();
            appObj.put("userId", "123");

            JSONObject audioObj = new JSONObject();
            audioObj.put("audioType", "wav");
            audioObj.put("sampleRate", 16000);
            audioObj.put("channel", 1);
            audioObj.put("sampleBytes", 2);
            params.put("audio", audioObj);

            JSONObject vad = new JSONObject();
            vad.put("seek",200);
            vad.put("ref_length",1000);
            params.put("vad",vad);

            params.put("request", requests);
      ...
   
```

## Additional Tips:

### 1. `build.gradle` Configuration

Ensure that your `build.gradle` file includes NDK and sourceSets configurations as follows:
```groovy

        android {
            ...
            defaultConfig {
                ...

                ndk {
                    abiFilters "armeabi", "armeabi-v7a", "arm64-v8a" , "x86"
                }
            }
            ...
            sourceSets {
                main {
                    jniLibs.srcDirs = ['libs']
                }
            }
        }

        dependencies {
            implementation fileTree(include: ['*.jar'], dir: 'libs')
           ...
        }
```
### 2. `proguard-rules.pro` Configuration

If you're using ProGuard, add the following rules to your `proguard-rules.pro` file to keep necessary classes and methods:

```proguard
...
-keep class com.speechsuper.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
```


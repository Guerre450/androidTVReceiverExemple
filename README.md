# Implementing the Android Tv receiver for communicating with a mobile device

this is a guide on how to implement the android tv native receiver from an empty view project in android studio.
## Step 1 : adding the libraries
- create a new empty view project in koltin on android studio
- go to Project Structure
- go to dependencies
- under "all dependencies", add a new dependency (library dependency)
  - add the following libraires:
    -   ``` 'com.google.android.gms:play-services-cast-tv:21.1.1' ```
    -    ```'com.google.android.gms:play-services-cast:22.0.0'```

# Step 2 : Make a subclass of ReceiverOptionsProvider and override getOptions
 - Make a new android package in your MainActivity.kt directory
 - Create a kotlin class called CastReceiverOptionsProvider
 - add the following code to the the CastReceiverOptionsProvider
```kotlin
class CastReceiverOptionsProvider : ReceiverOptionsProvider {
  override fun getOptions(context: Context): CastReceiverOptions {
    val namespaces : List<String> = listOf("urn:x-cast:<sender.Package.Here>")
    return CastReceiverOptions.Builder(context)
      .setStatusText("Cast Connect Codelab")
      .setCustomNamespaces(namespaces)
      .build()
  }
}
```
The above code overrides the ReceiverOptionsProvider to build the options 


# Step3.1 : make the app appear on google tv homescreen:
- Under the manifest xml componenet, add the following lines
```xml
  <uses-feature
  android:name="android.software.leanback"
  android:required="false" />
  <uses-feature android:name="android.hardware.touchscreen"
  android:required="false"/>
```
- In the launch intent, add the LEANBACK_LAUNCHER like in the example underneath:
```xml
<intent-filter>
      <action android:name="android.intent.action.MAIN" />
      <category android:name="android.intent.category.LAUNCHER" />
      <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
</intent-filter>
```
- in the app, add a banner property: you can use the one underneath:
``` xml
android:banner="@color/black"
```
- 
# Step 3 : Reference the options in the AndroidManifest.xml
- Inside the **application** component add the following:
```xml
  <meta-data
  android:name="com.google.android.gms.cast.tv.RECEIVER_OPTIONS_PROVIDER_CLASS_NAME"
  android:value="<REFERENCE.TO.CASTRECEIVEROPTIONSPROVIDER>" />
```
- Add the following intent to launch the library
``` xml
  <intent-filter>
  <action android:name="com.google.android.gms.cast.tv.action.LAUNCH" />
  <category android:name="android.intent.category.DEFAULT" />
  <action android:name="com.google.android.gms.cast.tv.action.LOAD" />
  </intent-filter>
```
  # Step 4, Starting and stopping the Receiver
- create a new kotlin class in the package we created earlier called **AppLifeCycleObserver**

- add the following code:
``` kotlin
class AppLifeCycleObserver : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        // App prepares to enter foreground.
        println("STARTED CASTING")
        CastReceiverContext.getInstance().start()
    }

    override fun onStop(owner: LifecycleOwner) {
        // App has moved to the background or has terminated.
        println("STOPPED CASTING")
        CastReceiverContext.getInstance().stop()
    }
}
```
This class will start the casting depending if the app is in foreground or not.

- Go back to the MainActivity.kt and add the following lines to the onCreate function:
```kotlin
//casting initiation
CastReceiverContext.initInstance(this)
ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeCycleObserver())
```

This will init the casting and start it.

# Step 5, Test if casting started correctly:
1. Connect you chromecast to your computer(enable the usb debug on your chromecast in the menu first or it won't boot)
2. Wait for the power error message to disappear
3. Say yes to usb debugging
4. Start the debugging process through Android Studio
5. When the app has launched, open the **logcat** in the option on the bottom left
6. Look for **STARTED CASTING** message in logs
7. If you see the message, everything is running fine

# Step 6, test sender app connection
## let's register the app and the chromecast
1. Open up the google cast sdk console : https://cast.google.com/publish/#/overview
2. Register your chrome cast device, 
here's a guide on how to get it : https://developers.google.com/cast/docs/registration#find_device_serial_number
in short, you have to cast your google sdk console tab.
3. Add a new application, select custom receiver
4. Put the name of your apps(I don't think this matter, but I always put the one used in android studio)
5. For the receiver URL, insert a fallback to a webreceiver, if you don't have any, you can put https://localhost
6. Now input the package named used by the application you made, **WARNING: IF YOU MAKE A MISTAKE HERE, YOUR RECEIVER WONT WORK**
8. Use the an existing sender app or create a new app
You can use this one if you don't have any : https://casttool.appspot.com/cactool/
In your sender app, you need to enable the option
for cast-connect or/and androidtv, here's an example:
9. 
``` kotlin
class CastOptionsProvider : OptionsProvider {
   companion object {
   const val CUSTOM_NAMESPACE = "urn:x-cast:com.example.myapplication"
   }
   override fun getCastOptions(context: Context): CastOptions {
   /** Following lines enable Cast Connect  */
   val launchOptions = LaunchOptions.Builder()
   .setAndroidReceiverCompatible(true)
   .build()
   val supportedNamespaces: MutableList<String> = ArrayList()
   supportedNamespaces.add(CUSTOM_NAMESPACE)
   return CastOptions.Builder ()
   .setLaunchOptions(launchOptions)
   //            .setSupportedNamespaces(supportedNamespaces)
   .setReceiverApplicationId("YOUR APPLICATION ID")
   .build()
   }

   override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
   return null
   }


}
```
8. After 15 minutes, reboot your chrome cast.
add the following code to the MainActivity class in your project:
``` kotlin
   private inner class EventCallback : CastReceiverContext.EventCallback() {
       override fun onSenderConnected(senderInfo: SenderInfo) {
           println("SENDER HAS BEEN CONNECTED")
           Toast.makeText(
               this@MainActivity,
               "Sender connected " + senderInfo.senderId,
               Toast.LENGTH_LONG
               ).show()
           }

        override fun onSenderDisconnected(eventInfo: SenderDisconnectedEventInfo) {
            println("SENDER HAS BEEN DISCONNECTED")
            Toast.makeText(
                this@MainActivity,
                "Sender disconnected " + eventInfo.senderInfo.senderId,
                Toast.LENGTH_LONG
            )
                .show()
        }
   }

```
9. Add the following line in the onCreate function after initializing the chrome cast object:

``` kotlin
   CastReceiverContext.getInstance().registerEventCallback(EventCallback())
```
10. Test your cast connection, if you see a notification which says you've been connect, Congratulation!



# (BONUS) Sending messages to your receiver
## In this section, we will display messages by the sender app to the screen
1. Create a new kotlin class called **TVCustomReceiverListener**
2. Insert the following content:
``` kotlin
class TVCustomReceiverListener : CastReceiverContext.MessageReceivedListener {
var mainMessage = mutableStateOf("android")

    override fun onMessageReceived(
        namespace: String, senderId: String?, message: String ) {
        mainMessage.value = message
        println(message)
    }
}
```
This class basically handles received messages.

3. Initiate the object, and use it's mutable value to replace the "android" in the default app
- Initiate the object in the MainActivity class
```kotlin 
var messageLisener : TVCustomReceiverListener = TVCustomReceiverListener()
```
- Change the value of the name in the greeting props:
```kotlin
Greeting(
    name = messageLisener.mainMessage.value,
    modifier = Modifier.padding(innerPadding)
)
```
- Add the callback to the listener in your MainActivity's onCreate function.
```kotlin
  CastReceiverContext.getInstance().setMessageReceivedListener("urn:x-cast:<YOUR.SENDER.APP.PACKAGE>", messageLisener)
```


































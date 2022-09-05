# privacy_check

### 国内Android市场隐私协议相关审核越来越严格,在隐私协议弹出之前,禁止调用与隐私协议相关的Api,比如ANDROID_ID,IMEI等,通常我们可以通过业务逻辑延后初始化一些已知的涉及隐私协议的SDK或者插件,但是部分插件或者SDK可能会在启动时候自动调用,这样往往的情况我们往往无法注意到,等到应用商店被拒之后也很难排查,于是基于[epic](https://github.com/tiann/epic),做了这个插件,无需侵入任何业务代码,并且不会带到线上,基本原理是读取配置文件(assets/privacy_methods.json),hookd对应的方法,当该方法调用的时候,控制台输出相关信息和调用栈,可比对应用商店被拒报告来查看一致。由于本人项目中暂时只遇到了ANDROID_ID被拒的情况,demo仅仅演示了获取ANDROID_ID的情况,可通过添加配置文件检查其它Api的获取


## Depend on it
Add this to your package's pubspec.yaml file:
```
dev_dependencies:
  privacy_check: 
  	git:
	 url:git://github.com/shingo/privacy_check
```
## Usage
#1、Add custom check configuration in assets/privacy_methods.json
```
[
  {
    "className": "android.provider.Settings$Secure",
    "methodName": "getString",
    "message": "获取ANDROID_ID",
    "filter": "android_id",
    "parameterTypes": [
      "android.content.ContentResolver",
      "java.lang.String"
    ]
  }
]
className: hook class name 
methodName: hook class method
message(Optional): just for print
filter(Optional): filter parameter value(It's useless)
parameterTypes(parameterTypes): hook class method parameter types

```
#2、 run you app and observe console output，or filter PrivacyMethodHook log
```
2022-09-05 15:16:11.260 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: start read privacy methods hook data from 'assets/privacy_methods.json
2022-09-05 15:16:11.260 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: privacy methods hook data =>[  {    "className": "android.provider.Settings$Secure",    "methodName": "getString",    "message": "获取ANDROID_ID",    "parameterTypes": [      "android.content.ContentResolver",      "java.lang.String"    ]  }]
2022-09-05 15:16:11.263 17777-18829/com.shingo.privacy_check_example E/AwareLog: AtomicFileUtils: readFileLines file not exist: android.util.AtomicFile@8c122a3
2022-09-05 15:16:11.688 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: ====================================================================================================================
2022-09-05 15:16:11.688 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: Hooked Message:获取ANDROID_ID
2022-09-05 15:16:11.688 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: Hooked Method:android.provider.Settings$Secure.getString
2022-09-05 15:16:11.688 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: Hooked Method Parameter:[android.app.ContextImpl$ApplicationContentResolver@841088c, android_id]
2022-09-05 15:16:11.689 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: Hooked Method Stack
    java.lang.Throwable
        at com.shingo.lib.epic.PrivacyMethodHookProvider$1.beforeHookedMethod(PrivacyMethodHookProvider.java:140)
        at de.robv.android.xposed.DexposedBridge.handleHookedArtMethod(DexposedBridge.java:229)
        at me.weishu.epic.art.entry.Entry64.onHookObject(Entry64.java:64)
        at me.weishu.epic.art.entry.Entry64.referenceBridge(Entry64.java:239)
        at plus.fluttercommunity.dev.android_id.AndroidIdPlugin.getAndroidId(AndroidIdPlugin.kt:45)
        at plus.fluttercommunity.dev.android_id.AndroidIdPlugin.onMethodCall(AndroidIdPlugin.kt:33)
        at io.flutter.plugin.common.MethodChannel$IncomingMethodCallHandler.onMessage(MethodChannel.java:262)
        at io.flutter.embedding.engine.dart.DartMessenger.invokeHandler(DartMessenger.java:295)
        at io.flutter.embedding.engine.dart.DartMessenger.lambda$dispatchMessageToQueue$0$io-flutter-embedding-engine-dart-DartMessenger(DartMessenger.java:319)
        at io.flutter.embedding.engine.dart.DartMessenger$$ExternalSyntheticLambda0.run(Unknown Source:12)
        at android.os.Handler.handleCallback(Handler.java:900)
        at android.os.Handler.dispatchMessage(Handler.java:103)
        at android.os.Looper.loop(Looper.java:219)
        at android.app.ActivityThread.main(ActivityThread.java:8668)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:513)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1109)
2022-09-05 15:16:11.689 17777-17777/com.shingo.privacy_check_example E/PrivacyMethodHook: ====================================================================================================================

```

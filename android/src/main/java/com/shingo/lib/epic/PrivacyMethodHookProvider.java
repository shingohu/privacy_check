package com.shingo.lib.epic;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shingo.lib.epic.model.PrivacyMethodHookData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;

public class PrivacyMethodHookProvider extends ContentProvider {

    private static String TAG = "PrivacyMethodHook";

    public void readPrivacyMethodHookData(Context context) {

        try {
            InputStream inputStream = null;
            Loge(TAG, "start read privacy methods hook data from 'assets/privacy_methods.json");
            inputStream = context.getResources().getAssets().open("privacy_methods.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            boolean read = true;

            while (read) {
                String line = reader.readLine();
                if (line == null) {
                    read = false;
                } else {
                    result.append(line);
                }
            }
            Loge(TAG, "privacy methods hook data =>" + result);
            JSONArray jo = new JSONArray(result.toString());
            List<PrivacyMethodHookData> list = new ArrayList<PrivacyMethodHookData>();

            for (int i = 0; i < jo.length(); i++) {
                List<Class> typeList = null;
                if (jo.getJSONObject(i).has("parameterTypes")) {
                    typeList = new ArrayList();
                    JSONArray typeArray = jo.getJSONObject(i).getJSONArray("parameterTypes");
                    for (int j = 0; j < typeArray.length(); j++) {
                        typeList.add(Class.forName(typeArray.getString(j)));
                    }
                }

                String className = jo.getJSONObject(i).getString("className");


                String methodName = null;
                if (jo.getJSONObject(i).has("methodName")) {
                    methodName = jo.getJSONObject(i).getString("methodName");
                }

                boolean isHookConstructor = false;
                if (jo.getJSONObject(i).has("hookConstructor")) {
                    isHookConstructor = jo.getJSONObject(i).getBoolean("hookConstructor");
                }


                String message = null;
                if (jo.getJSONObject(i).has("message")) {
                    message = jo.getJSONObject(i).getString("message");
                }

                String filter = null;
                if (jo.getJSONObject(i).has("filter")) {
                    filter = jo.getJSONObject(i).getString("filter");
                }


                list.add(new PrivacyMethodHookData(
                        className,
                        methodName,
                        isHookConstructor,
                        typeList,
                        message,
                        filter

                ));
            }

            for (int i = 0; i < list.size(); i++) {
                hookPrivacyMethod(list.get(i));
            }

        } catch (JSONException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void hookPrivacyMethod(PrivacyMethodHookData entity) {


        if (entity.className != null && !entity.className.isEmpty()) {
            String className = entity.className;
            String methodName = entity.methodName;
            boolean isHookConstructor = entity.isHookConstructor;
            String message = entity.message;
            String filter = entity.filter;
            List<Class> parameterTypes = entity.parameterTypes;

            XC_MethodHook hook = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Loge(TAG, "====================================================================================================================");
                    Loge(TAG, "Hooked Message:" + message);
                    Loge(TAG, "Hooked Method:" + className + "." + methodName);
                    String parameter = Arrays.toString(param.args);
                    Loge(TAG, "Hooked Method Parameter:" + parameter);
                    if (parameter != null && filter != null && parameter.contains(filter)) {
                        Loge(TAG, "Hooked Method Filter:" + filter);
                    }
                    Loge(TAG, "Hooked Method Stack", new Throwable());
                    Loge(TAG, "====================================================================================================================");
                }
            };
            try {
                Class<?> lintClass = Class.forName(className);
                if (isHookConstructor) {
                    DexposedBridge.hookAllConstructors(lintClass, hook);
                } else {
                    Object[] parameterTypesAndCallback;
                    if (parameterTypes != null && parameterTypes.size() > 0) {
                        parameterTypesAndCallback = new Object[parameterTypes.size() + 1];
                        for (int i = 0; i < parameterTypes.size(); i++) {
                            parameterTypesAndCallback[i] = parameterTypes.get(i);
                        }
                    } else {
                        parameterTypesAndCallback = new Object[1];
                    }
                    parameterTypesAndCallback[parameterTypesAndCallback.length - 1] = hook;
                    DexposedBridge.findAndHookMethod(lintClass, methodName, parameterTypesAndCallback);
                }
            } catch (Exception e) {
                Loge(TAG, "hookPrivacyMethod Exception:" + e);
            }
        }


    }


    private void Loge(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    private void Loge(String tag, String message, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message, throwable);
        }
    }


    @Override
    public boolean onCreate() {
        readPrivacyMethodHookData(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

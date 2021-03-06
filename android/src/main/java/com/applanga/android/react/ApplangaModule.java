package com.applanga.android.react;

import com.applanga.android.ApplangaCallback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;

import com.applanga.android.Applanga;


import java.util.HashMap;
import java.util.Map;

public class ApplangaModule extends ReactContextBaseJavaModule {
    private static String TAG = "applanga";
    
    public ApplangaModule(ReactApplicationContext context) {
        super(context);
        Applanga.init(context);
    }

    @Override
    public String getName() {
        return "Applanga";
    }
    
    @ReactMethod
    public void showDraftModeDialog(final Promise promise){
        try{
            Applanga.showDraftModeDialog(getCurrentActivity());
            promise.resolve(null);
        } catch (Exception error){
            promise.reject(error);
        }
    }

    @ReactMethod
    public void setLanguage(String lang, Promise promise){
        try {
            promise.resolve(Applanga.setLanguage(lang));          
        } catch(Exception error) {
            promise.reject(TAG, error.getMessage(), error);  
        }
    }

    @ReactMethod
    public void getString(String s, String d, Promise promise){
        try {
            promise.resolve(Applanga.getString(s, d));
        } catch(Exception error) {
            promise.reject(TAG, error.getMessage(), error);  
        }
    }

    @ReactMethod
    public void update(final Promise promise){
        try {
            Applanga.update(new ApplangaCallback() {
                @Override
                public void onLocalizeFinished(boolean succeeded) {
                    promise.resolve(succeeded);
                }
            });
        } catch(Exception error) {
            promise.reject(TAG, error.getMessage(), error);  
        }
    }
    
    @ReactMethod
    public void localizeMap(ReadableMap map, Promise promise){   
        try {
            promise.resolve(toWritableMap(Applanga.localizeMap(toHashMap(map))));
        } catch(Exception error) {
            promise.reject(TAG, error.getMessage(), error);  
        }
    }
    
    private static HashMap<String, HashMap<String, String>> toHashMap(ReadableMap map){
        ReadableMapKeySetIterator it = map.keySetIterator();
        HashMap<String, HashMap<String,String>> hashMap = new HashMap<>();
    
        while(it.hasNextKey()){
            String lang = it.nextKey();
            //Log.e("localizeMap", "lang = " + lang);
            ReadableMap langMap = map.getMap(lang);
            ReadableMapKeySetIterator langMapIt = langMap.keySetIterator();
            HashMap<String, String> langHashMap = new HashMap<>();
            while (langMapIt.hasNextKey()){
                String id = langMapIt.nextKey();
                //Log.e("localizeMap", "id = " + id);
                String value = langMap.getString(id);
                //Log.e("localizeMap", "value = " + value);
                langHashMap.put(id, value);
            }
            hashMap.put(lang, langHashMap);
        }
        return hashMap; 
    }
    
    private static WritableMap toWritableMap(HashMap<String, HashMap<String, String>> map) {       
        WritableMap writableMap = Arguments.createMap();
    
        for(Map.Entry<String, HashMap<String,String>> lang : map.entrySet()){
            WritableMap writableMapLang = Arguments.createMap();
            for(Map.Entry<String,String> string : map.get(lang.getKey()).entrySet()){
                writableMapLang.putString(string.getKey(), string.getValue());
            }
            writableMap.putMap(lang.getKey(), writableMapLang);
        }
        return writableMap;
    }
}
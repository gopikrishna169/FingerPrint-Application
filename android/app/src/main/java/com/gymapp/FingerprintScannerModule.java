package com.gymapp;

import android.widget.Toast;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.CAPTURED_DATA;
import com.nitgen.SDK.AndroidBSP.NBioBSPJNI.CAPTURE_CALLBACK;
import com.gymapp.FingerPrintSDK;


public class FingerprintScannerModule extends ReactContextBaseJavaModule {

    FingerPrintSDK fpSDK;
    public FingerprintScannerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        fpSDK = new FingerPrintSDK();
    }

    @Override
    public String getName() {
        return "FingerprintScanner";
    }

    @ReactMethod
    public void startFingerprintScan(Callback successCallback, Callback errorCallback) {
        fpSDK.init();
        try {
            if (fpSDK.getFingerPrintData() != null) {
                successCallback.invoke("Fingerprint captured successfully");
            } else {
                errorCallback.invoke("Fingerprint capture failed");
            }
        } catch (Exception e) {
            errorCallback.invoke("Exception: " + e.getMessage());
        }
    }


}

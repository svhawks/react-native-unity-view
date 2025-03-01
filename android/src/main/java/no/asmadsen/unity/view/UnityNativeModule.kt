package no.asmadsen.unity.view

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import kotlin.math.roundToLong

class UnityNativeModule(
    reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext), UnityEventListener {

    init {
        UnityUtils.addUnityEventListener(this)
    }

    override fun getName(): String = "UnityNativeModule"

    @ReactMethod
    fun isReady(promise: Promise) {
        promise.resolve(UnityPlayerManager.get(currentActivity) != null)
    }

    @ReactMethod
    fun createUnity(promise: Promise) {
        createUnityWithWarmupDurationMs(DEFAULT_WARMUP_DURATION_MS, promise)
    }

    @ReactMethod
    fun createUnityWithWarmupSeconds(warmupDurationMs: Double, promise: Promise) {
        createUnityWithWarmupDurationMs((warmupDurationMs * 1000).roundToLong(), promise)
    }

    private fun createUnityWithWarmupDurationMs(
        warmupDurationMs: Long,
        promise: Promise,
    ) {
        val activity = currentActivity
        if (activity == null) {
            promise.resolve(false)
        } else {
            UnityPlayerManager.acquire(activity, warmupDurationMs = warmupDurationMs) {
                promise.resolve(true)
            }
        }
    }

    @ReactMethod
    fun postMessage(gameObject: String?, methodName: String?, message: String?) {
        UnityUtils.postMessage(gameObject, methodName, message)
    }

    @ReactMethod
    fun pause() {
        // NOTE: This is a no-op; there should be no need to manually pause or resume
        // It *could* be implemented using:
        //   UnityPlayerManager.get(currentActivity)?.pause()
    }

    @ReactMethod
    fun resume() {
        // NOTE: This is a no-op; there should be no need to manually pause or resume
        // It *could* be implemented using:
        //   UnityPlayerManager.get(currentActivity)?.resume()
    }

    override fun onMessage(message: String) {
        val context: ReactContext = reactApplicationContext
        context.getJSModule(RCTDeviceEventEmitter::class.java).emit("onUnityMessage", message)
    }
}
package com.the_attic_level.dash.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.content.res.Resources
import android.net.Uri
import android.os.Looper
import android.text.Html
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.Insets
import com.the_attic_level.dash.sys.Logger
import com.the_attic_level.dash.sys.rest.NetworkUtil
import com.the_attic_level.dash.sys.sync.SyncHandler
import com.the_attic_level.dash.ui.activity.ActivityEvent
import com.the_attic_level.dash.ui.activity.ActivityResult
import com.the_attic_level.dash.ui.activity.InsetRequest
import com.the_attic_level.dash.ui.activity.PermissionResult
import com.the_attic_level.dash.ui.fragment.DashFragment
import com.the_attic_level.dash.ui.fragment.DashFragmentActivity
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicLong

class Dash
{
    companion object
    {
        // ------------------------------------------------------------
        // Unique Atomic ID's
        // ------------------------------------------------------------
        
        private val UNIQUE = AtomicLong()
        
        fun nextUniqueLong(): Long =
            UNIQUE.incrementAndGet()
        
        fun nextUniqueInt(): Int =
            UNIQUE.incrementAndGet().toInt()
        
        // ------------------------------------------------------------
        // Network
        // ------------------------------------------------------------
        
        val isNetworkAvailable: Boolean
            get() = NetworkUtil.isNetworkAvailable
        
        val isNetworkNotMetered: Boolean
            get() = NetworkUtil.isNetworkNotMetered
        
        // ------------------------------------------------------------
        // Thread Utils
        // ------------------------------------------------------------
        
        fun onMainThread(): Boolean =
            Looper.myLooper() == Looper.getMainLooper()
        
        fun ui(delay: Long=0, action: Runnable) =
            SyncHandler.ui(delay, action)
        
        fun async(delay: Long=0, runnable: Runnable) =
            SyncHandler.async(delay, runnable)
        
        fun thread(action: Runnable) =
            SyncHandler.thread(action)
        
        fun sleep(millis: Long) {
            if (millis > 0) {
                try {
                    Thread.sleep(millis)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        
        // ------------------------------------------------------------
        // Application / Resources
        // ------------------------------------------------------------
        
        val app: Application
            get() = DashApp.shared
        
        val packageName: String
            get() = DashApp.shared.packageName
        
        val packagePartsUri: Uri?
            get() = Uri.parse("package:" + this.packageName)
        
        val resources: Resources
            get() = DashApp.shared.resources
        
        fun color(id: Int): Int =
            ContextCompat.getColor(DashApp.shared, id)
        
        fun colors(id: Int): ColorStateList? =
            ResourcesCompat.getColorStateList(this.resources, id, null)
        
        fun string(id: Int): String =
            optString(id) ?: ""
        
        fun string(id: Int, vararg args: Any?): String =
            optString(id, args) ?: ""
        
        fun optString(id: Int): String? {
            return if (id != 0) this.resources.getString(id) else null
        }
        
        fun optString(id: Int, vararg args: Any?): String? {
            return if (id != 0) {
                if (args.isNotEmpty()) {
                    this.resources.getString(id, *args)
                } else this.resources.getString(id)
            } else null
        }
        
        @Suppress("DEPRECATION")
        fun html(id: Int): CharSequence? {
            val text = optString(id)
            return if (text != null) Html.fromHtml(text) else null
        }
        
        fun integer(id: Int): Int {
            return if (id != 0) this.resources.getInteger(id) else 0
        }
        
        fun bool(id: Int): Boolean {
            return id != 0 && this.resources.getBoolean(id)
        }
        
        fun <T> getService(cls: Class<T>): T? {
            return ContextCompat.getSystemService(DashApp.shared, cls)
        }
        
        // ------------------------------------------------------------
        // Activity Permissions
        // ------------------------------------------------------------
        
        fun hasPermissions(permissions: Array<String>): Boolean {
            for (permission in permissions) {
                if (!hasPermission(permission)) {
                    return false
                }
            }
            return true
        }
        
        fun hasPermission(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(DashApp.shared, permission) == PackageManager.PERMISSION_GRANTED
        }
        
        fun requestPermissions(permissions: Array<String>, callback: (DashActivity, PermissionResult) -> Unit): Int {
            return requestPermissions(permissions, object: PermissionResult.Receiver {
                override fun onResult(activity: DashActivity, result: PermissionResult) {
                    callback.invoke(activity, result)
                }
            })
        }
        
        fun requestPermissions(permissions: Array<String>, receiver: PermissionResult.Receiver): Int {
            val activity = this.currentActivity
            if (activity is DashActivity) {
                return activity.requestPermissions(permissions, receiver)
            }
            error("no activity to request permissions")
            return -1
        }
        
        fun shouldShowRequestPermissionRationale(permission: String): Boolean {
            val activity = this.currentActivity
            if (activity != null) {
                return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
            error("unable to check request permission rationale without activity")
            return false
        }
        
        // ------------------------------------------------------------
        // Activity Results
        // ------------------------------------------------------------
        
        fun resolveActivity(intent: Intent): ResolveInfo? {
            return DashApp.shared.packageManager.resolveActivity(intent, 0)
        }
        
        fun requestActivityResult(intent: Intent, callback: (DashActivity, ActivityResult) -> Unit) {
            requestActivityResult(object: ActivityResult.Receiver{
                override fun onResult(activity: DashActivity, result: ActivityResult) {
                    callback.invoke(activity, result)
                }
            }, intent)
        }
        
        fun requestActivityResult(receiver: ActivityResult.Receiver, intent: Intent): Int {
            val activity = this.currentActivity
            if (activity is DashActivity) {
                return activity.requestActivityResult(intent, receiver)
            }
            error("no activity to request for result")
            return -1
        }
        
        // ------------------------------------------------------------
        // Request Activity Events / System Bar Insets
        // ------------------------------------------------------------
        
        fun requestEvent(event: ActivityEvent, callback: (DashActivity, ActivityEvent) -> Unit) {
            requestEvent(event, object: ActivityEvent.Receiver {
                override fun onEvent(activity: DashActivity, event: ActivityEvent) {
                    callback.invoke(activity, event)
                }
            })
        }
        
        fun requestEvent(event: ActivityEvent, receiver: ActivityEvent.Receiver) {
            val activity = this.currentActivity
            if (activity is DashActivity) {
                activity.requestEvent(event, receiver)
            } else {
                error("no activity to request event: $event")
            }
        }
        
        /** Request the system bar insets for edge-to-edge layouts. */
        fun requestInsets(callback: (Insets) -> Unit) {
            requestInsets(object : InsetRequest.Receiver {
                override fun onInsets(systemBars: Insets) {
                    callback.invoke(systemBars)
                }
            })
        }
        
        /** Request the system bar insets for edge-to-edge layouts. */
        fun requestInsets(receiver: InsetRequest.Receiver) {
            val activity = this.currentActivity
            if (activity is DashActivity) {
                activity.requestInsets(receiver)
            } else {
                error("no activity to request insets")
            }
        }
        
        // ------------------------------------------------------------
        // Activity
        // ------------------------------------------------------------
        
        const val CLEAR_STACK = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        
        val currentActivity: Activity?
            get() = DashApp.shared.activity
        
        fun <T: Activity> start(cls: Class<T>, flags: Int = 0): Boolean {
            val activity = this.currentActivity
            return if (activity != null) {
                start(activity, Intent(activity, cls).setFlags(flags))
            } else {
                error("no activity to start ${cls.simpleName} from")
                false
            }
        }
        
        fun start(intent: Intent): Boolean {
            val activity = this.currentActivity
            return if (activity != null) {
                return start(activity, intent)
            } else {
                error("no activity to start intent from")
                false
            }
        }
        
        @Suppress("DEPRECATION")
        fun invokeBackPress() {
            this.currentActivity?.onBackPressed()
        }
        
        @Suppress("DEPRECATION")
        fun invokeBackPress(activity: Activity) {
            activity.onBackPressed()
        }
        
        // ------------------------------------------------------------
        // Activity (Private)
        // ------------------------------------------------------------
        
        fun start(activity: Activity, intent: Intent): Boolean {
            return if (this.onMainThread()) {
                try {
                    activity.startActivity(intent)
                    true
                } catch (e: Exception) {
                    error(e.message)
                    false
                }
            } else {
                error("intents can only start from main thread")
                false
            }
        }
        
        private fun error(msg: String?) {
            Logger.error(Dash::class, msg)
        }
        
        // ------------------------------------------------------------
        // Fragment
        // ------------------------------------------------------------
        
        val activeFragment: DashFragment?; get() {
            val activity = this.currentActivity
            if (activity is DashFragmentActivity) {
                return activity.activeFragment
            }
            return null
        }
        
        // ------------------------------------------------------------
        // JSON Utils
        // ------------------------------------------------------------
        
        fun toJSON(bytes: ByteArray?, charset: Charset = StandardCharsets.UTF_8): JSONObject? {
            return if (bytes != null && bytes.isNotEmpty()) {
                toJSON(String(bytes, charset))
            } else null
        }
        
        fun toJSON(value: String?): JSONObject? {
            if (!value.isNullOrEmpty()) {
                try {
                    return JSONObject(value)
                } catch (e: JSONException) {
                    error("unable to parse json: ${e.message}")
                }
            }
            return null
        }
        
        fun toBytes(json: JSONObject?): ByteArray? {
            return toBytes(json, StandardCharsets.UTF_8)
        }
        
        fun toBytes(json: JSONObject?, charset: Charset?): ByteArray? {
            return json?.toString()?.toByteArray(charset!!)
        }
    }
}
package com.example.datawedge_flutter_integration

import io.flutter.embedding.android.FlutterActivity
import android.content.*
import android.os.Bundle
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.EventChannel.StreamHandler
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import io.flutter.plugins.GeneratedPluginRegistrant
import android.util.Log

class MainActivity: FlutterActivity() {
    
    // Define the method channel and event channel for communication with Flutter
    private val COMMAND_CHANNEL = "com.example.datawedge_flutter_integration/command"
    private val SCAN_CHANNEL = "com.example.datawedge_flutter_integration/scan"
    
    // Constants for DataWedge profile configuration
    private val PROFILE_INTENT_ACTION = "com.example.datawedge_flutter_integration.SCAN"
    private val PROFILE_INTENT_BROADCAST = "2"

    // Instance of DWInterface for managing DataWedge commands
    private val dwInterface = DWInterface()
 
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
       
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        // Set up an EventChannel for receiving barcode scan events from DataWedge
        EventChannel(flutterEngine.dartExecutor, SCAN_CHANNEL).setStreamHandler(
                object : StreamHandler {
                    private var dataWedgeBroadcastReceiver: BroadcastReceiver? = null
                    
                    // Called when Flutter listens for events
                    override fun onListen(arguments: Any?, events: EventSink?) {
                        // Register the BroadcastReceiver to listen for scan events
                        dataWedgeBroadcastReceiver = createDataWedgeBroadcastReceiver(events)
                        val intentFilter = IntentFilter()
                        intentFilter.addAction(PROFILE_INTENT_ACTION)
                        intentFilter.addAction(DWInterface.DATAWEDGE_RETURN_ACTION)
                        intentFilter.addCategory(DWInterface.DATAWEDGE_RETURN_CATEGORY)
                        registerReceiver(
                                dataWedgeBroadcastReceiver, intentFilter)  
                    }

                    // Called when the listener is canceled
                    override fun onCancel(arguments: Any?) {
                   
                        unregisterReceiver(dataWedgeBroadcastReceiver)
                        dataWedgeBroadcastReceiver = null
                    }
                }
        )

        
        MethodChannel(flutterEngine.dartExecutor, COMMAND_CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "sendDataWedgeCommandStringParameter") {
                // Handle sending DataWedge commands with a string parameter
                val arguments = JSONObject(call.arguments.toString())
                val command: String = arguments.get("command") as String
                val parameter: String = arguments.get("parameter") as String
                dwInterface.sendCommandString(applicationContext, command, parameter)
            }
            else if (call.method == "createDataWedgeProfile") {
                // Handle creating a new DataWedge profile
                createDataWedgeProfile(call.arguments.toString())
            }
            else {
                result.notImplemented() // Method not implemented
            }
        }
    }

    // Creates a BroadcastReceiver to listen for scan data from DataWedge
    private fun createDataWedgeBroadcastReceiver(events: EventSink?): BroadcastReceiver? {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action.equals(PROFILE_INTENT_ACTION)) {
                    // A barcode has been scanned, extract the scan data
                    var scanData = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
                    scanData = scanData ?: "" // Default to an empty string if null

                    var symbology = intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_LABEL_TYPE)
                    symbology = symbology ?: "" // Default to an empty string if null

                    // Get the current timestamp for when the scan occurred
                    val date = Calendar.getInstance().time
                    val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                    val dateTimeString = df.format(date)

                    // Create a Scan object to hold the scan data
                    val currentScan = Scan(scanData, symbology, dateTimeString)

                    // Send the scan data back to Flutter as a JSON object
                    events?.success(currentScan.toJson())
                }
            }
        }
    }

    // Creates a DataWedge profile and configures it for the app
    private fun createDataWedgeProfile(profileName: String) {
        // Command to create a new DataWedge profile for the app
        dwInterface.sendCommandString(this, DWInterface.DATAWEDGE_SEND_CREATE_PROFILE, profileName)
        
        // Create a Bundle for the profile configuration
        val profileConfig = Bundle().apply {
            putString("PROFILE_NAME", profileName)
            putString("PROFILE_ENABLED", "true") // Enable the profile
            putString("CONFIG_MODE", "UPDATE")
        }

        // Configure barcode settings for the profile
        val barcodeConfig = Bundle().apply {
            putString("PLUGIN_NAME", "BARCODE")
            putString("RESET_CONFIG", "true") // Reset any previous configuration
            val barcodeProps = Bundle() // Add barcode properties here
            putBundle("PARAM_LIST", barcodeProps)
        }

        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)

        // Associate the profile with this app
        val appConfig = Bundle().apply {
            putString("PACKAGE_NAME", packageName)
            putStringArray("ACTIVITY_LIST", arrayOf("*")) // Apply to all activities
        }

        profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))

        // Send the profile configuration to DataWedge
        dwInterface.sendCommandBundle(this, DWInterface.DATAWEDGE_SEND_SET_CONFIG, profileConfig)

        // Configure the intent output for DataWedge to deliver scan results via broadcast
        profileConfig.remove("PLUGIN_CONFIG") // Remove previous plugin config
        val intentConfig = Bundle().apply {
            putString("PLUGIN_NAME", "INTENT")
            putString("RESET_CONFIG", "true")
            val intentProps = Bundle().apply {
                putString("intent_output_enabled", "true")
                putString("intent_action", PROFILE_INTENT_ACTION)
                putString("intent_delivery", PROFILE_INTENT_BROADCAST)  // Broadcast delivery
            }
            putBundle("PARAM_LIST", intentProps)
        }

        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)

        // Send the intent configuration to DataWedge
        dwInterface.sendCommandBundle(this, DWInterface.DATAWEDGE_SEND_SET_CONFIG, profileConfig)
    }
}

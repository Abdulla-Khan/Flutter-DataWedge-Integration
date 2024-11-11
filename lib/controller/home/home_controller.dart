import 'dart:convert';
import 'dart:developer';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

class HomeViewController extends GetxController {
  // MethodChannel for sending commands to DataWedge
  static const MethodChannel methodChannel =
      MethodChannel('com.example.datawedge_flutter_integration/command');

  // EventChannel for receiving scan results from DataWedge
  static const EventChannel scanChannel =
      EventChannel('com.example.datawedge_flutter_integration/scan');

  // Observable to hold the barcode scan text
  RxString barcodeScanText = "Tap Button to Start Scan".obs;

  @override
  void onInit() {
    // Listen for events from DataWedge scan results
    scanChannel.receiveBroadcastStream().listen(_onEvent, onError: _onError);

    // Create a DataWedge profile on initialization
    _createProfile("DataWedgeFlutterDemo");

    super.onInit();
  }

  // Sends a command to DataWedge via MethodChannel
  Future<void> _sendDataWedgeCommand(String command, String parameter) async {
    try {
      // Create a JSON string with command and parameter
      String argumentAsJson =
          jsonEncode({"command": command, "parameter": parameter});

      // Send the command to DataWedge using MethodChannel
      await methodChannel.invokeMethod(
          'sendDataWedgeCommandStringParameter', argumentAsJson);
    } on PlatformException catch (e) {
      // Handle PlatformException if there's an error communicating with DataWedge
      log("Error sending DataWedge command: ${e.message}");
    } catch (e) {
      // Handle other types of exceptions
      log("Unexpected error: $e");
    }
  }

  // Creates a DataWedge profile using MethodChannel
  Future<void> _createProfile(String profileName) async {
    try {
      // Send command to create the DataWedge profile
      await methodChannel.invokeMethod('createDataWedgeProfile', profileName);
    } on PlatformException catch (e) {
      // Handle errors if profile creation fails
      log("Error creating DataWedge profile: ${e.message}");
    } catch (e) {
      // Handle other exceptions
      log("Unexpected error: $e");
    }
  }

  // Callback function to handle successful scan events
  void _onEvent(event) {
    // Decode the event to get the barcode data
    Map barcodeScan = jsonDecode(event);

    // Update the barcodeScanText with the scan data
    barcodeScanText.value = barcodeScan['scanData'].toString();

    // Stop scanning after a successful scan
    stopScan();
  }

  // Callback function to handle errors during scanning
  void _onError(Object error) {
    // Display an error message if scanning fails
    barcodeScanText.value = "Error Scanning Barcode";

    // Stop scanning if there's an error
    stopScan();
  }

  // Starts the barcode scanning process by sending a start command to DataWedge
  void startScan() {
    // Update the UI to show scanning is in progress
    barcodeScanText.value = "Scanning";

    // Send command to DataWedge to start scanning
    _sendDataWedgeCommand(
        "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "START_SCANNING");
  }

  // Stops the barcode scanning process by sending a stop command to DataWedge
  void stopScan() {
    // Send command to DataWedge to stop scanning
    _sendDataWedgeCommand(
        "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "STOP_SCANNING");
  }
}

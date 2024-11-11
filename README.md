# DataWedge Flutter Integration

This project demonstrates the integration of Zebra DataWedge with a Flutter app for barcode scanning. It uses MethodChannels and EventChannels to communicate between Flutter and the native Android environment for controlling the barcode scanner and receiving scan results. Read complete article [here](https://medium.com/@abdullahkhan.smiu/integrating-datawedge-with-flutter-for-barcode-scanning-85797f52baee)

## Features

- **Barcode Scanning**: Trigger barcode scans using DataWedge and receive scan data in Flutter.
- **Start/Stop Scan**: Start and stop the scan from within Flutter.
- **Custom DataWedge Profile**: Create and configure a DataWedge profile programmatically.
- **Reactive UI**: Display scan results on the UI using `GetX` for state management.

## Requirements

- **Flutter 2.x or later**
- **Zebra Android device with Barcoe Scanner and DataWedge**

## Setup

1. Clone this repository:

   ```bash
   git clone https://https://github.com/Abdulla-Khan/Flutter-DataWedge-Integration
   ```

2. Navigate to the project directory:

   ```bash
   cd Flutter-DataWedge-Integration
   ```

3. Install the dependencies:

   ```bash
   flutter pub get
   ```

4. Set up the DataWedge Profile for your app:

   - The app creates a DataWedge profile during initialization. The profile is used to configure the barcode scanner and send scan results via Intent.

## How It Works

The Flutter app communicates with DataWedge via:

- **MethodChannel** (`com.example.datawedge_flutter_integration/command`): Used for sending commands to DataWedge, such as creating profiles and starting/stopping scans.
- **EventChannel** (`com.example.datawedge_flutter_integration/scan`): Used for receiving scan results from DataWedge.

### Key Components

- **HomeViewController**: Manages the interaction with DataWedge, including sending commands to start/stop scans and receiving scan results.
- **HomeView**: Displays the current scan status and result.
- **ControllerBinding**: Lazy initializes the `HomeViewController` using `GetX` for dependency injection.

## Usage

1. **Start Scan**: Tap the floating action button to trigger the barcode scanner.
2. **Display Scan Result**: Once a barcode is scanned, the result will be displayed on the screen.

## Example

```dart
// HomeViewController.dart
HomeViewController().startScan();
```

The app will show "Scanning" while it scans, and display the scanned barcode data once successful.

## Contributing

Feel free to fork the repository and create pull requests with improvements, bug fixes, or additional features.

### Note:

This app assumes that **DataWedge** is installed and configured on your Zebra Android device. DataWedge must be enabled to handle the barcode scanner settings.

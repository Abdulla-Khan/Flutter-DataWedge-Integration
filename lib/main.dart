import 'package:datawedge_flutter_integration/bindings/bindings.dart';
import 'package:flutter/material.dart';
import 'package:get/get_navigation/src/root/get_material_app.dart';
import 'views/home/home_view.dart';

void main() => runApp(const DataWedgeFlutterScanner());

class DataWedgeFlutterScanner extends StatelessWidget {
  const DataWedgeFlutterScanner({super.key});

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      initialBinding: ControllerBinding(),
      home: const HomeView(),
    );
  }
}

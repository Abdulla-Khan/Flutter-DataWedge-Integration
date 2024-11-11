import 'package:datawedge_flutter_integration/controller/home/home_controller.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class HomeView extends StatelessWidget {
  const HomeView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Flutter DataWedge Integration"),
        centerTitle: true, // Centers the title in the AppBar
      ),
      body: Center(
        child: Obx(
          // Obx widget listens to changes in the barcodeScanText value and updates the UI reactively
          () => Text(
            // Displaying the current value of barcodeScanText
            Get.find<HomeViewController>().barcodeScanText.value,
            style: const TextStyle(
              fontSize: 20,
            ),
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // Trigger the start scan action from HomeViewController
          Get.find<HomeViewController>().startScan();
        },
        child: const Icon(
          Icons.document_scanner_outlined,
          size: 26,
        ),
      ),
    );
  }
}

import 'package:datawedge_flutter_integration/controller/home/home_controller.dart';
import 'package:get/get.dart';

class ControllerBinding implements Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => HomeViewController());
  }
}

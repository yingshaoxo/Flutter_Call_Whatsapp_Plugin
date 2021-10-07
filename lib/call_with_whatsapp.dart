import 'dart:async';

import 'package:flutter/services.dart';

class CallWithWhatsapp {
  static const MethodChannel _channel = const MethodChannel('call_with_whatsapp');
  static Future<void> initiateCall(String number){
    Completer<void> completer = Completer();
    _channel.invokeMethod("callWithWhatsapp",{"number":number}).then((x){
      if(x == "SUCCESS") completer.complete();
      else completer.completeError(x);
    }).catchError((e){
      completer.completeError("ERROR");
    });
    return completer.future;
  }
  static Future<void> openInPlayStore(){
    Completer<void> completer = Completer();
    _channel.invokeMethod("openPlayStore").then((x){
      if(x == "SUCCESS") completer.complete();
      else completer.completeError(x);
    }).catchError((e){
      completer.completeError("ERROR");
    });
    return completer.future;
  }
  static Future<void> requestPermissions(){
    Completer<void> completer = Completer();
    _channel.invokeMethod("requestPermissions").then((x){
      if(x == "PERMISSION_GRANTED") completer.complete();
      else completer.completeError(x);
    }).catchError((e){
      completer.completeError("ERROR");
    });
    return completer.future;
  }
  static Future<void> createContact(String name,String number){
    Completer<void> completer = Completer();
    _channel.invokeMethod("createContact",{"name":name,"number":number}).then((x){
      if(x == "SUCCESS") completer.complete();
      else completer.completeError(x);
    }).catchError((e){
      completer.completeError("ERROR");
    });
    return completer.future;
  }
}

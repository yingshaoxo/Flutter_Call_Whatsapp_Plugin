import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:call_with_whatsapp/call_with_whatsapp.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  void _requestPermission() {
    CallWithWhatsapp.requestPermissions().then((x) {
      print("success");
    }).catchError((e) {
      print(e);
    });
  }

  void _openStore() {
    CallWithWhatsapp.openInPlayStore().then((x) {
      print("success");
    }).catchError((e) {
      print(e);
    });
  }

  void _initiateCall() {
    CallWithWhatsapp.initiateCall("+15812576428").then((x) {
      print("success");
    }).catchError((e) {
      print(e);
    });
  }

  void _createNewContact() {
    CallWithWhatsapp.createContact("AAAAA AAAA", "0111111").then((x) {
      print("success");
    }).catchError((e) {
      print(e);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: SafeArea(
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: <Widget>[
                RaisedButton(
                  onPressed: _requestPermission,
                  child: Text(
                    "Request permission",
                  ),
                ),
                RaisedButton(
                  onPressed: _openStore,
                  child: Text(
                    "Open In Playstore",
                  ),
                ),
                RaisedButton(
                  onPressed: _initiateCall,
                  child: Text(
                    "Initiate Call",
                  ),
                ),
                RaisedButton(
                  onPressed: _createNewContact,
                  child: Text(
                    "Create contact",
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

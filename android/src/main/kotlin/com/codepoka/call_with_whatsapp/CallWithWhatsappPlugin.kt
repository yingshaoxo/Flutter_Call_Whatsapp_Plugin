package com.codepoka.call_with_whatsapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class CallWithWhatsappPlugin(private val channel: MethodChannel, private val registrar: Registrar) : MethodCallHandler {
  private var _result: Result? = null

  init {
    registrar.addRequestPermissionsResultListener { requestCode, permissions, grantResults ->
        if(requestCode == 203){
          if(grantResults.any{ r -> r != PackageManager.PERMISSION_GRANTED }){
            //NOT GRANTED ALL
            _result?.success("PERMISSION_DENIED")
            _result = null
//            channel.invokeMethod("onRequestPermissionsResult","PERMISSION_DENIED")
          }
          else{
            //ALL GRANTED
            _result?.success("PERMISSION_GRANTED")
            _result = null
//            channel.invokeMethod("onRequestPermissionsResult","PERMISSION_GRANTED")
          }
        }

        return@addRequestPermissionsResultListener true
      }
    registrar.addActivityResultListener { requestCode, resultCode, data ->
      print(requestCode)
      //request code -1 && data != null
      if(requestCode == 202){
        //Contact creation result
        if(resultCode == -1 && data != null){
          //contact creation success
          _result?.success("SUCCESS")
          _result = null
//          channel.invokeMethod("onCreateContactResult","SUCCESS")
        } else{
          // contact not reated
          _result?.success("CANCELED")
          _result = null
//          channel.invokeMethod("onCreateContactResult","CANCELED")
        }
      }

      return@addActivityResultListener true
    }
  }
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "call_with_whatsapp")
      channel.setMethodCallHandler(CallWithWhatsappPlugin(channel,registrar))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
        "callWithWhatsapp" -> {
          result.success(initiateCall(call.argument<String>("number")!!))
        }
        "openPlayStore" -> {
          result.success(openPlaystore(registrar.activeContext(),"com.whatsapp"))
        }
        "requestPermissions" -> {
          _result = result
          requestPermissions()
        }
        "createContact" -> {
          _result = result
          createNewContact(call.argument<String>("name")!!,call.argument<String>("number")!!)
        }
        else -> {
          result.notImplemented()
        }
    }
  }

  private fun initiateCall(number: String):String{
    val contecxt = registrar.activity()
    val whatsappPack ="com.whatsapp"
    try {
      return if (isAppInstalled(contecxt,whatsappPack)) {
        if (isAppEnabled(contecxt,whatsappPack)) {
          if (checkPermissions()) {
            val contactId = contactIdByPhoneNumber(number)
            if (contactId != null && contactId.isNotEmpty()) {
              val whatsappId = hasWhatsapp(contactId)
              if (whatsappId != null && whatsappId.isNotEmpty()) {
                callWithWhatsapp(whatsappId)
                "SUCCESS"
              }else "NO_WHATSAPP"
            }else "NO_CONTACT"
          }else "PERMISSION_DENIED"
        }else "DISABLED"
      }else "NOT_INSTALLED"
    }catch (e:Exception){
      return "ERROR"
    }
  }

  private fun contactIdByPhoneNumber(number: String): String? {
    var contactId: String? = null
    val phoneNumber = number.replace("+", "").replace(" ", "")
    if (phoneNumber.isNotEmpty()) {
      val contentResolver = registrar.activity().contentResolver
      val uri = Uri.withAppendedPath(
              ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
              Uri.encode(phoneNumber)
      )
      val projection =
              arrayOf(ContactsContract.PhoneLookup._ID)
      val cursor =
              contentResolver.query(uri, projection, null, null, null)
      if (cursor != null) {
        while (cursor.moveToNext()) {
          contactId =
                  cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
        }
        cursor.close()
      }
    }
    return contactId
  }
  private fun hasWhatsapp(contactId:String):String? {
    var whatsappId:String? = null
    val projection = arrayOf(
            ContactsContract.Data._ID,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.MIMETYPE,
            "account_type"
    )
    val selection =
            ContactsContract.RawContacts.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " =? and account_type=?"
    val selectionArgs = arrayOf(
            contactId,
            "vnd.android.cursor.item/vnd.com.whatsapp.video.call",
            "com.whatsapp"
    )
    val cursor = registrar.activity().contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection, selection, selectionArgs,
            ContactsContract.Contacts.DISPLAY_NAME
    )

    //ID QUERY SECTION
    while (cursor!!.moveToNext()) {
      whatsappId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID)).toString()
    }
    return whatsappId
  }
  private fun callWithWhatsapp(profileId:String) {
    // CALL SECTION
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.setDataAndType(
            Uri.parse("content://com.android.contacts/data/$profileId"),
            "vnd.android.cursor.item/vnd.com.whatsapp.video.call"
    )
    intent.setPackage("com.whatsapp")
    registrar.activeContext().startActivity(intent)
  }

  private fun isAppInstalled(context: Context, packageName: String): Boolean {
    val pm: PackageManager = context.packageManager
    try {
      pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
      return true
    }
    catch (ignored: PackageManager.NameNotFoundException) {
    }

    return false
  }
  private fun isAppEnabled(context: Context, packageName: String): Boolean {
    var appStatus = false
    try {
      val ai = context.packageManager.getApplicationInfo(packageName, 0)
      appStatus = ai.enabled
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    }
    return appStatus
  }
  private fun checkPermissions():Boolean{
    val permission = ContextCompat.checkSelfPermission(registrar.activity(), Manifest.permission.READ_CONTACTS)
    val permission1 = ContextCompat.checkSelfPermission(registrar.activity(), Manifest.permission.CALL_PHONE)
    return !(permission != PackageManager.PERMISSION_GRANTED ||
            permission1 != PackageManager.PERMISSION_GRANTED)
  }

  private fun openPlaystore(myContext: Context, packageName: String):String {
    try {
      val marketUri =
              Uri.parse("market://details?id=$packageName")
      val myIntent = Intent(Intent.ACTION_VIEW, marketUri)
      myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      myContext.startActivity(myIntent)
      return "SUCCESS"
    }catch (_:Exception){}
    return "ERROR"
  }
  private fun requestPermissions(){
    try {
      ActivityCompat.requestPermissions(registrar.activity(),
              arrayOf(Manifest.permission.READ_CONTACTS,
                      Manifest.permission.CALL_PHONE
              ),203)
    } catch (e: Exception) {
      channel.invokeMethod("onRequestPermissionsResult","ERROR")
    }
  }
  private fun createNewContact(name:String, number:String){
    try {
      // Creates a new Intent to insert a contact
      val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
        type = ContactsContract.RawContacts.CONTENT_TYPE
      }
      intent.apply {
        putExtra(ContactsContract.Intents.Insert.NAME,name)
        putExtra(ContactsContract.Intents.Insert.PHONE,number)
      }
     registrar.activity().startActivityForResult (intent,202)
    }catch (e:Exception){
      channel.invokeMethod("onCreateContactResult","ERROR")
    }
  }
}

package org.urgero.ota;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class checkforupdate extends android.app.Service {
	public static class checkforupdate_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, checkforupdate.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static checkforupdate mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return checkforupdate.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "org.urgero.ota", "org.urgero.ota.checkforupdate");
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Service (checkforupdate) Create **");
        processBA.raiseEvent(null, "service_create");
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		handleStart(intent);
    }
    @Override
    public int onStartCommand(android.content.Intent intent, int flags, int startId) {
    	handleStart(intent);
		return android.app.Service.START_NOT_STICKY;
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (checkforupdate) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = new anywheresoftware.b4a.objects.IntentWrapper();
    			if (intent != null) {
    				if (intent.hasExtra("b4a_internal_intent"))
    					iw.setObject((android.content.Intent) intent.getParcelableExtra("b4a_internal_intent"));
    				else
    					iw.setObject(intent);
    			}
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
        BA.LogInfo("** Service (checkforupdate) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.NotificationWrapper _notification1 = null;
public static String _runningversion = "";
public static String _currentversion = "";
public static String _updateurl = "";
public static boolean _refreshdataflag = false;
public static String _rominfo = "";
public org.urgero.ota.main _main = null;
public org.urgero.ota.httputils _httputils = null;
public org.urgero.ota.httputilsservice _httputilsservice = null;
public static String  _checkme() throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub CheckME";
 //BA.debugLineNum = 52;BA.debugLine="HttpUtils.CallbackActivity = \"CheckForUpdate\" 'Current activity name.";
mostCurrent._httputils._callbackactivity = "CheckForUpdate";
 //BA.debugLineNum = 53;BA.debugLine="HttpUtils.CallbackJobDoneSub = \"JobDone\"";
mostCurrent._httputils._callbackjobdonesub = "JobDone";
 //BA.debugLineNum = 54;BA.debugLine="HttpUtils.Download(\"Job1\", UpdateURL & \"update.php?push=query\")";
mostCurrent._httputils._download(processBA,"Job1",_updateurl+"update.php?push=query");
 //BA.debugLineNum = 55;BA.debugLine="Log(\"Hit query\")";
anywheresoftware.b4a.keywords.Common.Log("Hit query");
 //BA.debugLineNum = 57;BA.debugLine="Notification1.Icon = \"icon\"";
_notification1.setIcon("icon");
 //BA.debugLineNum = 58;BA.debugLine="Notification1.Vibrate = False";
_notification1.setVibrate(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 59;BA.debugLine="Notification1.SetInfo(\"OpenOTA\", \"Checking for update...\", Main)";
_notification1.SetInfo(processBA,"OpenOTA","Checking for update...",(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 60;BA.debugLine="Notification1.Sound = False";
_notification1.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 61;BA.debugLine="Notification1.AutoCancel = True";
_notification1.setAutoCancel(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 62;BA.debugLine="Notification1.Notify(1)";
_notification1.Notify((int)(1));
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(String _job) throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub JobDone (Job As String)";
 //BA.debugLineNum = 65;BA.debugLine="Log(\"job done!\")";
anywheresoftware.b4a.keywords.Common.Log("job done!");
 //BA.debugLineNum = 66;BA.debugLine="If HttpUtils.IsSuccess(UpdateURL & \"update.php?push=query\") Then";
if (mostCurrent._httputils._issuccess(processBA,_updateurl+"update.php?push=query")) { 
 //BA.debugLineNum = 67;BA.debugLine="Log(\"InJobDone\")";
anywheresoftware.b4a.keywords.Common.Log("InJobDone");
 //BA.debugLineNum = 68;BA.debugLine="CurrentVersion = HttpUtils.GetString(UpdateURL & \"update.php?push=query\")";
_currentversion = mostCurrent._httputils._getstring(processBA,_updateurl+"update.php?push=query");
 //BA.debugLineNum = 69;BA.debugLine="If CurrentVersion = RunningVersion Then";
if ((_currentversion).equals(_runningversion)) { 
 //BA.debugLineNum = 70;BA.debugLine="Log(\"No Update Needed\")";
anywheresoftware.b4a.keywords.Common.Log("No Update Needed");
 //BA.debugLineNum = 71;BA.debugLine="Notification1.Cancel(1)";
_notification1.Cancel((int)(1));
 //BA.debugLineNum = 72;BA.debugLine="RomInfoCheck";
_rominfocheck();
 }else {
 //BA.debugLineNum = 74;BA.debugLine="Log(\"Update Needed\")";
anywheresoftware.b4a.keywords.Common.Log("Update Needed");
 //BA.debugLineNum = 76;BA.debugLine="Notification1.Icon = \"icon\"";
_notification1.setIcon("icon");
 //BA.debugLineNum = 77;BA.debugLine="Notification1.Vibrate = True";
_notification1.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 78;BA.debugLine="Notification1.SetInfo(\"OpenOTA\", \"There is an update: Version \" & CurrentVersion, Main)";
_notification1.SetInfo(processBA,"OpenOTA","There is an update: Version "+_currentversion,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 79;BA.debugLine="Notification1.Sound = False";
_notification1.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 80;BA.debugLine="Notification1.AutoCancel = True";
_notification1.setAutoCancel(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 81;BA.debugLine="Notification1.Notify(1)";
_notification1.Notify((int)(1));
 //BA.debugLineNum = 82;BA.debugLine="RomInfoCheck";
_rominfocheck();
 };
 }else {
 //BA.debugLineNum = 92;BA.debugLine="Log(\"failed http request\")";
anywheresoftware.b4a.keywords.Common.Log("failed http request");
 //BA.debugLineNum = 93;BA.debugLine="Notification1.Initialize";
_notification1.Initialize();
 //BA.debugLineNum = 94;BA.debugLine="Notification1.Icon = \"icon\"";
_notification1.setIcon("icon");
 //BA.debugLineNum = 95;BA.debugLine="Notification1.Vibrate = True";
_notification1.setVibrate(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 96;BA.debugLine="Notification1.SetInfo(\"OpenOTA\", \"Check Failed on Update!\", Main)";
_notification1.SetInfo(processBA,"OpenOTA","Check Failed on Update!",(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 97;BA.debugLine="Notification1.Sound = False";
_notification1.setSound(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 98;BA.debugLine="Notification1.AutoCancel = True";
_notification1.setAutoCancel(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 99;BA.debugLine="Notification1.Notify(1)";
_notification1.Notify((int)(1));
 };
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone2(String _job) throws Exception{
 //BA.debugLineNum = 103;BA.debugLine="Sub JobDone2 (Job As String)";
 //BA.debugLineNum = 104;BA.debugLine="Log(\"job done!\")";
anywheresoftware.b4a.keywords.Common.Log("job done!");
 //BA.debugLineNum = 105;BA.debugLine="If HttpUtils.IsSuccess(UpdateURL & \"update.php?push=rominfo\") Then";
if (mostCurrent._httputils._issuccess(processBA,_updateurl+"update.php?push=rominfo")) { 
 //BA.debugLineNum = 106;BA.debugLine="RomInfo = HttpUtils.GetString(UpdateURL & \"update.php?push=rominfo\")";
_rominfo = mostCurrent._httputils._getstring(processBA,_updateurl+"update.php?push=rominfo");
 //BA.debugLineNum = 107;BA.debugLine="If IsPaused(Main) Then";
if (anywheresoftware.b4a.keywords.Common.IsPaused(processBA,(Object)(mostCurrent._main.getObject()))) { 
 //BA.debugLineNum = 109;BA.debugLine="RefreshDataFlag = True";
_refreshdataflag = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 111;BA.debugLine="CallSub(Main, \"UpdateLabels\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)(mostCurrent._main.getObject()),"UpdateLabels");
 };
 };
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Dim Notification1 As Notification";
_notification1 = new anywheresoftware.b4a.objects.NotificationWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim RunningVersion As String";
_runningversion = "";
 //BA.debugLineNum = 10;BA.debugLine="Dim CurrentVersion As String";
_currentversion = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim UpdateURL As String";
_updateurl = "";
 //BA.debugLineNum = 12;BA.debugLine="Dim RefreshDataFlag As Boolean";
_refreshdataflag = false;
 //BA.debugLineNum = 13;BA.debugLine="Dim RomInfo As String";
_rominfo = "";
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _rominfocheck() throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub RomInfoCheck";
 //BA.debugLineNum = 47;BA.debugLine="HttpUtils.CallbackActivity = \"CheckForUpdate\" 'Current activity name.";
mostCurrent._httputils._callbackactivity = "CheckForUpdate";
 //BA.debugLineNum = 48;BA.debugLine="HttpUtils.CallbackJobDoneSub = \"JobDone2\"";
mostCurrent._httputils._callbackjobdonesub = "JobDone2";
 //BA.debugLineNum = 49;BA.debugLine="HttpUtils.Download(\"Job2\", UpdateURL & \"update.php?push=rominfo\")";
mostCurrent._httputils._download(processBA,"Job2",_updateurl+"update.php?push=rominfo");
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 16;BA.debugLine="Notification1.Initialize";
_notification1.Initialize();
 //BA.debugLineNum = 17;BA.debugLine="Notification1.Icon = \"icon\" 'use the application icon file for the notification";
_notification1.setIcon("icon");
 //BA.debugLineNum = 18;BA.debugLine="Notification1.Vibrate = False";
_notification1.setVibrate(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 115;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 116;BA.debugLine="Log(\"Service Closed\")";
anywheresoftware.b4a.keywords.Common.Log("Service Closed");
 //BA.debugLineNum = 117;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
String _command = "";
String _runner = "";
anywheresoftware.b4a.keywords.StringBuilderWrapper _stdout = null;
anywheresoftware.b4a.keywords.StringBuilderWrapper _stderr = null;
int _result = 0;
anywheresoftware.b4a.phone.Phone _ph = null;
 //BA.debugLineNum = 20;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 21;BA.debugLine="Log(\"Start Service\")";
anywheresoftware.b4a.keywords.Common.Log("Start Service");
 //BA.debugLineNum = 22;BA.debugLine="Dim Command, Runner As String";
_command = "";
_runner = "";
 //BA.debugLineNum = 23;BA.debugLine="Dim StdOut, StdErr As StringBuilder";
_stdout = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
_stderr = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 25;BA.debugLine="Dim Ph As Phone";
_ph = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 26;BA.debugLine="StdOut.Initialize";
_stdout.Initialize();
 //BA.debugLineNum = 27;BA.debugLine="StdErr.Initialize";
_stderr.Initialize();
 //BA.debugLineNum = 28;BA.debugLine="Result = Ph.Shell(\"cat /system/ota.prop\", Null, StdOut, StdErr)";
_result = _ph.Shell("cat /system/ota.prop",(String[])(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(_stdout.getObject()),(java.lang.StringBuilder)(_stderr.getObject()));
 //BA.debugLineNum = 29;BA.debugLine="Log(\"Command 1 Ran\")";
anywheresoftware.b4a.keywords.Common.Log("Command 1 Ran");
 //BA.debugLineNum = 31;BA.debugLine="RunningVersion = StdOut.ToString";
_runningversion = _stdout.ToString();
 //BA.debugLineNum = 32;BA.debugLine="Dim Command, Runner As String";
_command = "";
_runner = "";
 //BA.debugLineNum = 33;BA.debugLine="Dim StdOut, StdErr As StringBuilder";
_stdout = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
_stderr = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 35;BA.debugLine="Dim Ph As Phone";
_ph = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 36;BA.debugLine="StdOut.Initialize";
_stdout.Initialize();
 //BA.debugLineNum = 37;BA.debugLine="StdErr.Initialize";
_stderr.Initialize();
 //BA.debugLineNum = 38;BA.debugLine="Result = Ph.Shell(\"cat /system/ota.url.prop\", Null, StdOut, StdErr)";
_result = _ph.Shell("cat /system/ota.url.prop",(String[])(anywheresoftware.b4a.keywords.Common.Null),(java.lang.StringBuilder)(_stdout.getObject()),(java.lang.StringBuilder)(_stderr.getObject()));
 //BA.debugLineNum = 39;BA.debugLine="Log(\"Command 2 Ran\")";
anywheresoftware.b4a.keywords.Common.Log("Command 2 Ran");
 //BA.debugLineNum = 41;BA.debugLine="UpdateURL = StdOut.ToString";
_updateurl = _stdout.ToString();
 //BA.debugLineNum = 42;BA.debugLine="Main.UpdateURL = StdOut.ToString";
mostCurrent._main._updateurl = _stdout.ToString();
 //BA.debugLineNum = 43;BA.debugLine="CheckME";
_checkme();
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
}

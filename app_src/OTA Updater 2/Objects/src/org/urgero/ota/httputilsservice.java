package org.urgero.ota;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class httputilsservice extends android.app.Service {
	public static class httputilsservice_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
			android.content.Intent in = new android.content.Intent(context, httputilsservice.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
			context.startService(in);
		}

	}
    static httputilsservice mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return httputilsservice.class;
	}
	@Override
	public void onCreate() {
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "org.urgero.ota", "org.urgero.ota.httputilsservice");
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
        BA.LogInfo("** Service (httputilsservice) Create **");
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
    	BA.LogInfo("** Service (httputilsservice) Start **");
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
        BA.LogInfo("** Service (httputilsservice) Destroy **");
		processBA.raiseEvent(null, "service_destroy");
        processBA.service = null;
		mostCurrent = null;
		processBA.setActivityPaused(true);
	}
public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.http.HttpClientWrapper _hc = null;
public static int _task = 0;
public static int _countworking = 0;
public static int _finishtasks = 0;
public static int _maxtasks = 0;
public static anywheresoftware.b4a.objects.collections.Map _tasktorequest = null;
public static String _tempfolder = "";
public static boolean _post = false;
public static byte[] _postbytes = null;
public static anywheresoftware.b4a.objects.streams.File.InputStreamWrapper _postinputstream = null;
public static int _postlength = 0;
public static boolean _hcisinitialized = false;
public org.urgero.ota.main _main = null;
public org.urgero.ota.httputils _httputils = null;
public org.urgero.ota.checkforupdate _checkforupdate = null;
public static String  _handleerror(int _taskid,String _reason) throws Exception{
String _link = "";
 //BA.debugLineNum = 102;BA.debugLine="Sub HandleError(TaskId As Int, Reason As String)";
 //BA.debugLineNum = 103;BA.debugLine="Dim link As String";
_link = "";
 //BA.debugLineNum = 104;BA.debugLine="link = taskToRequest.Get(TaskId)";
_link = String.valueOf(_tasktorequest.Get((Object)(_taskid)));
 //BA.debugLineNum = 105;BA.debugLine="Log(\"Error. Url=\" & link & \" Message=\" & Reason)";
anywheresoftware.b4a.keywords.Common.Log("Error. Url="+_link+" Message="+_reason);
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return "";
}
public static String  _hc_responseerror(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,String _reason,int _statuscode,int _taskid) throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub hc_ResponseError (Response As HttpResponse, Reason As String, StatusCode As Int, TaskId As Int)";
 //BA.debugLineNum = 93;BA.debugLine="countWorking = countWorking - 1";
_countworking = (int)(_countworking-1);
 //BA.debugLineNum = 94;BA.debugLine="finishTasks = finishTasks + 1";
_finishtasks = (int)(_finishtasks+1);
 //BA.debugLineNum = 95;BA.debugLine="HandleError(TaskId, Reason)";
_handleerror(_taskid,_reason);
 //BA.debugLineNum = 96;BA.debugLine="If Response <> Null Then";
if (_response!= null) { 
 //BA.debugLineNum = 97;BA.debugLine="Log(Response.GetString(\"UTF8\"))";
anywheresoftware.b4a.keywords.Common.Log(_response.GetString("UTF8"));
 //BA.debugLineNum = 98;BA.debugLine="Response.Release";
_response.Release();
 };
 //BA.debugLineNum = 100;BA.debugLine="ProcessNextTask";
_processnexttask();
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
public static String  _hc_responsesuccess(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,int _taskid) throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub hc_ResponseSuccess (Response As HttpResponse, TaskId As Int)";
 //BA.debugLineNum = 75;BA.debugLine="Response.GetAsynchronously(\"response\", File.OpenOutput(TempFolder, TaskId, False), _ 		True, TaskId)";
_response.GetAsynchronously(processBA,"response",(java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.File.OpenOutput(_tempfolder,BA.NumberToString(_taskid),anywheresoftware.b4a.keywords.Common.False).getObject()),anywheresoftware.b4a.keywords.Common.True,_taskid);
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim hc As HttpClient";
_hc = new anywheresoftware.b4a.http.HttpClientWrapper();
 //BA.debugLineNum = 8;BA.debugLine="Dim task As Int";
_task = 0;
 //BA.debugLineNum = 9;BA.debugLine="Dim countWorking As Int";
_countworking = 0;
 //BA.debugLineNum = 10;BA.debugLine="Dim finishTasks As Int";
_finishtasks = 0;
 //BA.debugLineNum = 11;BA.debugLine="Dim maxTasks As Int";
_maxtasks = 0;
 //BA.debugLineNum = 12;BA.debugLine="maxTasks = 10";
_maxtasks = (int)(10);
 //BA.debugLineNum = 13;BA.debugLine="Dim taskToRequest As Map";
_tasktorequest = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 14;BA.debugLine="Dim TempFolder As String";
_tempfolder = "";
 //BA.debugLineNum = 15;BA.debugLine="Dim Post As Boolean";
_post = false;
 //BA.debugLineNum = 16;BA.debugLine="Dim PostBytes() As Byte";
_postbytes = new byte[(int)(0)];
;
 //BA.debugLineNum = 17;BA.debugLine="Dim PostInputStream As InputStream";
_postinputstream = new anywheresoftware.b4a.objects.streams.File.InputStreamWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim PostLength As Int";
_postlength = 0;
 //BA.debugLineNum = 19;BA.debugLine="Dim hcIsInitialized As Boolean";
_hcisinitialized = false;
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _processnexttask() throws Exception{
String _link = "";
anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper _req = null;
 //BA.debugLineNum = 40;BA.debugLine="Sub ProcessNextTask";
 //BA.debugLineNum = 41;BA.debugLine="If finishTasks >= HttpUtils.Tasks.Size Then";
if (_finishtasks>=mostCurrent._httputils._tasks.getSize()) { 
 //BA.debugLineNum = 42;BA.debugLine="HttpUtils.Working = False";
mostCurrent._httputils._working = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 43;BA.debugLine="HttpUtils.Complete = True";
mostCurrent._httputils._complete = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 45;BA.debugLine="If HttpUtils.CallbackJobDoneSub <> \"\" Then";
if ((mostCurrent._httputils._callbackjobdonesub).equals("") == false) { 
 //BA.debugLineNum = 46;BA.debugLine="CallSub2(HttpUtils.CallbackActivity, HttpUtils.CallbackJobDoneSub, HttpUtils.Job)";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._httputils._callbackactivity),mostCurrent._httputils._callbackjobdonesub,(Object)(mostCurrent._httputils._job));
 };
 //BA.debugLineNum = 49;BA.debugLine="If HttpUtils.Working = False Then";
if (mostCurrent._httputils._working==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 50;BA.debugLine="StopService(\"\")";
anywheresoftware.b4a.keywords.Common.StopService(processBA,(Object)(""));
 };
 //BA.debugLineNum = 52;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 54;BA.debugLine="If task >= HttpUtils.Tasks.Size Then Return";
if (_task>=mostCurrent._httputils._tasks.getSize()) { 
if (true) return "";};
 //BA.debugLineNum = 55;BA.debugLine="Dim link As String";
_link = "";
 //BA.debugLineNum = 56;BA.debugLine="link = HttpUtils.Tasks.Get(task)";
_link = String.valueOf(mostCurrent._httputils._tasks.Get(_task));
 //BA.debugLineNum = 57;BA.debugLine="Dim req As HttpRequest";
_req = new anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper();
 //BA.debugLineNum = 58;BA.debugLine="If Post Then";
if (_post) { 
 //BA.debugLineNum = 59;BA.debugLine="If PostInputStream.IsInitialized Then";
if (_postinputstream.IsInitialized()) { 
 //BA.debugLineNum = 60;BA.debugLine="req.InitializePost(link, PostInputStream, PostLength)";
_req.InitializePost(_link,(java.io.InputStream)(_postinputstream.getObject()),_postlength);
 }else {
 //BA.debugLineNum = 62;BA.debugLine="req.InitializePost2(link, PostBytes)";
_req.InitializePost2(_link,_postbytes);
 };
 }else {
 //BA.debugLineNum = 65;BA.debugLine="req.InitializeGet(link)";
_req.InitializeGet(_link);
 };
 //BA.debugLineNum = 68;BA.debugLine="countWorking = countWorking + 1";
_countworking = (int)(_countworking+1);
 //BA.debugLineNum = 69;BA.debugLine="taskToRequest.Put(task, link)";
_tasktorequest.Put((Object)(_task),(Object)(_link));
 //BA.debugLineNum = 70;BA.debugLine="hc.Execute(req, task)";
_hc.Execute(processBA,_req,_task);
 //BA.debugLineNum = 71;BA.debugLine="task = task + 1";
_task = (int)(_task+1);
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _response_streamfinish(boolean _success,int _taskid) throws Exception{
 //BA.debugLineNum = 78;BA.debugLine="Sub Response_StreamFinish (Success As Boolean, TaskId As Int)";
 //BA.debugLineNum = 79;BA.debugLine="finishTasks = finishTasks + 1";
_finishtasks = (int)(_finishtasks+1);
 //BA.debugLineNum = 80;BA.debugLine="countWorking = countWorking - 1";
_countworking = (int)(_countworking-1);
 //BA.debugLineNum = 81;BA.debugLine="If Success = False Then";
if (_success==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 82;BA.debugLine="HandleError(TaskId, LastException.Message)";
_handleerror(_taskid,anywheresoftware.b4a.keywords.Common.LastException(processBA).getMessage());
 }else {
 //BA.debugLineNum = 84;BA.debugLine="HttpUtils.SuccessfulUrls.Put(taskToRequest.Get(TaskId), TaskId)";
mostCurrent._httputils._successfulurls.Put(_tasktorequest.Get((Object)(_taskid)),(Object)(_taskid));
 //BA.debugLineNum = 86;BA.debugLine="If HttpUtils.CallbackUrlDoneSub <> \"\" Then";
if ((mostCurrent._httputils._callbackurldonesub).equals("") == false) { 
 //BA.debugLineNum = 87;BA.debugLine="CallSub2(HttpUtils.CallbackActivity, HttpUtils.CallbackUrlDoneSub, taskToRequest.Get(TaskId))";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._httputils._callbackactivity),mostCurrent._httputils._callbackurldonesub,_tasktorequest.Get((Object)(_taskid)));
 };
 };
 //BA.debugLineNum = 90;BA.debugLine="ProcessNextTask";
_processnexttask();
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 22;BA.debugLine="If TempFolder = \"\" Then TempFolder = File.DirInternalCache";
if ((_tempfolder).equals("")) { 
_tempfolder = anywheresoftware.b4a.keywords.Common.File.getDirInternalCache();};
 //BA.debugLineNum = 23;BA.debugLine="If hcIsInitialized = False Then";
if (_hcisinitialized==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 24;BA.debugLine="hc.Initialize(\"hc\")";
_hc.Initialize("hc");
 //BA.debugLineNum = 25;BA.debugLine="hcIsInitialized = True";
_hcisinitialized = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 107;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 108;BA.debugLine="HttpUtils.Working = False";
mostCurrent._httputils._working = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}
public static String  _service_start() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Service_Start";
 //BA.debugLineNum = 30;BA.debugLine="If HttpUtils.Tasks.IsInitialized = False Then Return";
if (mostCurrent._httputils._tasks.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
if (true) return "";};
 //BA.debugLineNum = 31;BA.debugLine="taskToRequest.Initialize";
_tasktorequest.Initialize();
 //BA.debugLineNum = 32;BA.debugLine="finishTasks = 0";
_finishtasks = (int)(0);
 //BA.debugLineNum = 33;BA.debugLine="task = 0";
_task = (int)(0);
 //BA.debugLineNum = 34;BA.debugLine="countWorking = 0";
_countworking = (int)(0);
 //BA.debugLineNum = 35;BA.debugLine="Do While task < HttpUtils.Tasks.Size";
while (_task<mostCurrent._httputils._tasks.getSize()) {
 //BA.debugLineNum = 36;BA.debugLine="ProcessNextTask";
_processnexttask();
 //BA.debugLineNum = 37;BA.debugLine="If countWorking >= maxTasks Then Exit";
if (_countworking>=_maxtasks) { 
if (true) break;};
 }
;
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
}

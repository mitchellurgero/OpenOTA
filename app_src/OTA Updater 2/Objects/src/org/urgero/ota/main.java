package org.urgero.ota;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "org.urgero.ota", "org.urgero.ota.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "org.urgero.ota", "org.urgero.ota.main");
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
		return true;
	}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true)
				return true;
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static String _updateurl = "";
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public org.urgero.ota.httputils _httputils = null;
public org.urgero.ota.httputilsservice _httputilsservice = null;
public org.urgero.ota.checkforupdate _checkforupdate = null;
public static String  _aboutota_click() throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub aboutOTA_Click";
 //BA.debugLineNum = 53;BA.debugLine="Msgbox(\"Developed and Tested by sandix of XDA Forums and AndroidForums\" & CRLF & \"Copyright © 2015 URGERO.ORG\",\"About OpenOTA\")";
anywheresoftware.b4a.keywords.Common.Msgbox("Developed and Tested by sandix of XDA Forums and AndroidForums"+anywheresoftware.b4a.keywords.Common.CRLF+"Copyright © 2015 URGERO.ORG","About OpenOTA",mostCurrent.activityBA);
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 30;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"MainLayout\")";
mostCurrent._activity.LoadLayout("MainLayout",mostCurrent.activityBA);
 //BA.debugLineNum = 33;BA.debugLine="Activity.Title = \"OpenOTA\"";
mostCurrent._activity.setTitle((Object)("OpenOTA"));
 //BA.debugLineNum = 34;BA.debugLine="StartService(\"CheckForUpdate\")";
anywheresoftware.b4a.keywords.Common.StartService(mostCurrent.activityBA,(Object)("CheckForUpdate"));
 //BA.debugLineNum = 35;BA.debugLine="Activity.AddMenuItem(\"About OpenOTA\",\"aboutOTA\")";
mostCurrent._activity.AddMenuItem("About OpenOTA","aboutOTA");
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 56;BA.debugLine="If CheckForUpdate.RefreshDataFlag = True Then";
if (mostCurrent._checkforupdate._refreshdataflag==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 57;BA.debugLine="CheckForUpdate.RefreshDataFlag = False";
mostCurrent._checkforupdate._refreshdataflag = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 58;BA.debugLine="UpdateLabels";
_updatelabels();
 };
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 68;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 69;BA.debugLine="StartService(\"CheckForUpdate\")";
anywheresoftware.b4a.keywords.Common.StartService(mostCurrent.activityBA,(Object)("CheckForUpdate"));
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 71;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 72;BA.debugLine="DownloadUpdate";
_downloadupdate();
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _button3_click() throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub Button3_Click";
 //BA.debugLineNum = 75;BA.debugLine="CheckForUpdate.Notification1.Cancel(1)";
mostCurrent._checkforupdate._notification1.Cancel((int)(1));
 //BA.debugLineNum = 76;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 77;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _downloadupdate() throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneIntents _p = null;
 //BA.debugLineNum = 64;BA.debugLine="Sub DownloadUpdate";
 //BA.debugLineNum = 65;BA.debugLine="Dim p As PhoneIntents";
_p = new anywheresoftware.b4a.phone.Phone.PhoneIntents();
 //BA.debugLineNum = 66;BA.debugLine="StartActivity(p.OpenBrowser(UpdateURL & \"update.php?push=DownloadUpdate\"))";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_p.OpenBrowser(_updateurl+"update.php?push=DownloadUpdate")));
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (processGlobalsRun == false) {
	    processGlobalsRun = true;
		try {
		        main._process_globals();
httputils._process_globals();
httputilsservice._process_globals();
checkforupdate._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim Button3 As Button";
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim UpdateURL As String";
_updateurl = "";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _recoveryboot_click() throws Exception{
String _command = "";
String _runner = "";
anywheresoftware.b4a.keywords.StringBuilderWrapper _stdout = null;
anywheresoftware.b4a.keywords.StringBuilderWrapper _stderr = null;
int _result = 0;
anywheresoftware.b4a.phone.Phone _ph = null;
 //BA.debugLineNum = 38;BA.debugLine="Sub recoveryBoot_Click";
 //BA.debugLineNum = 39;BA.debugLine="Dim Command, Runner As String";
_command = "";
_runner = "";
 //BA.debugLineNum = 40;BA.debugLine="Dim StdOut, StdErr As StringBuilder";
_stdout = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
_stderr = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 42;BA.debugLine="Dim Ph As Phone";
_ph = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 43;BA.debugLine="StdOut.Initialize";
_stdout.Initialize();
 //BA.debugLineNum = 44;BA.debugLine="StdErr.Initialize";
_stderr.Initialize();
 //BA.debugLineNum = 45;BA.debugLine="Runner = File.Combine(File.DirInternalCache, \"runner\")";
_runner = anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"runner");
 //BA.debugLineNum = 46;BA.debugLine="Command = File.Combine(File.DirInternalCache, \"command\")";
_command = anywheresoftware.b4a.keywords.Common.File.Combine(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"command");
 //BA.debugLineNum = 47;BA.debugLine="File.WriteString(File.DirInternalCache, \"runner\", \"su < \" & Command)";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"runner","su < "+_command);
 //BA.debugLineNum = 48;BA.debugLine="File.WriteString(File.DirInternalCache, \"command\", \"reboot recovery\" & CRLF & \"exit\") 'Any commands via crlf, and exit at end";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"command","reboot recovery"+anywheresoftware.b4a.keywords.Common.CRLF+"exit");
 //BA.debugLineNum = 49;BA.debugLine="Result = Ph.Shell(\"sh\", Array As String(Runner), StdOut, StdErr)";
_result = _ph.Shell("sh",new String[]{_runner},(java.lang.StringBuilder)(_stdout.getObject()),(java.lang.StringBuilder)(_stderr.getObject()));
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _updatelabels() throws Exception{
 //BA.debugLineNum = 79;BA.debugLine="Sub UpdateLabels";
 //BA.debugLineNum = 80;BA.debugLine="Label1.Text = \"Running Version: \" & CheckForUpdate.RunningVersion";
mostCurrent._label1.setText((Object)("Running Version: "+mostCurrent._checkforupdate._runningversion));
 //BA.debugLineNum = 81;BA.debugLine="Label2.Text = \"Version Available: \" & CheckForUpdate.CurrentVersion";
mostCurrent._label2.setText((Object)("Version Available: "+mostCurrent._checkforupdate._currentversion));
 //BA.debugLineNum = 82;BA.debugLine="Label3.Text = \"ROM Information: \" & CRLF & CheckForUpdate.RomInfo";
mostCurrent._label3.setText((Object)("ROM Information: "+anywheresoftware.b4a.keywords.Common.CRLF+mostCurrent._checkforupdate._rominfo));
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
return "";
}
}

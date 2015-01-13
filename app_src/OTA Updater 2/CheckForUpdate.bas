Type=Service
Version=2.71
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Dim Notification1 As Notification
	Dim RunningVersion As String
	Dim CurrentVersion As String
	Dim UpdateURL As String
	Dim RefreshDataFlag As Boolean 
	Dim RomInfo As String
End Sub
Sub Service_Create
    Notification1.Initialize
    Notification1.Icon = "icon" 'use the application icon file for the notification
    Notification1.Vibrate = False
End Sub
Sub Service_Start (StartingIntent As Intent)
Log("Start Service")
	Dim Command, Runner As String
	Dim StdOut, StdErr As StringBuilder
	Dim Result As Int
	Dim Ph As Phone
	StdOut.Initialize
	StdErr.Initialize
	Result = Ph.Shell("cat /system/ota.prop", Null, StdOut, StdErr)
	Log("Command 1 Ran")
	'Msgbox(StdOut.tostring, "")
	RunningVersion = StdOut.ToString
	Dim Command, Runner As String
Dim StdOut, StdErr As StringBuilder
	Dim Result As Int
	Dim Ph As Phone
	StdOut.Initialize
	StdErr.Initialize
	Result = Ph.Shell("cat /system/ota.url.prop", Null, StdOut, StdErr)
	Log("Command 2 Ran")
	'Msgbox(StdOut.tostring, "")
	UpdateURL = StdOut.ToString
	Main.UpdateURL = StdOut.ToString
	CheckME
	
End Sub
Sub RomInfoCheck
  HttpUtils.CallbackActivity = "CheckForUpdate" 'Current activity name.
 HttpUtils.CallbackJobDoneSub = "JobDone2"
 HttpUtils.Download("Job2", UpdateURL & "update.php?push=rominfo")
End Sub
Sub CheckME
 HttpUtils.CallbackActivity = "CheckForUpdate" 'Current activity name.
 HttpUtils.CallbackJobDoneSub = "JobDone"
 HttpUtils.Download("Job1", UpdateURL & "update.php?push=query")
 Log("Hit query")
 'Notification1.Initialize
Notification1.Icon = "icon" 
Notification1.Vibrate = False
Notification1.SetInfo("OpenOTA", "Checking for update...", Main)
Notification1.Sound = False
Notification1.AutoCancel = True
Notification1.Notify(1)
End Sub
Sub JobDone (Job As String)
Log("job done!")
 If HttpUtils.IsSuccess(UpdateURL & "update.php?push=query") Then
 Log("InJobDone")
  CurrentVersion = HttpUtils.GetString(UpdateURL & "update.php?push=query")
	If CurrentVersion = RunningVersion Then
	Log("No Update Needed")
		Notification1.Cancel(1)
		RomInfoCheck
	Else
		Log("Update Needed")
		'Notification1.Initialize
    	Notification1.Icon = "icon" 
    	Notification1.Vibrate = True
		Notification1.SetInfo("OpenOTA", "There is an update: Version " & CurrentVersion, Main)
    	Notification1.Sound = False
		Notification1.AutoCancel = True
		Notification1.Notify(1)
		 RomInfoCheck
		'If IsPaused(Main) Then
 			'StartActivity(Main)
 		'	RefreshDataFlag = True
		'Else
 	'		CallSub(Main, "UpdateLabels")
		'End If

	End If
Else
	Log("failed http request")
	Notification1.Initialize
    Notification1.Icon = "icon" 
    Notification1.Vibrate = True
	Notification1.SetInfo("OpenOTA", "Check Failed on Update!", Main)
    Notification1.Sound = False
	Notification1.AutoCancel = True
	Notification1.Notify(1)
 End If

End Sub
Sub JobDone2 (Job As String)
Log("job done!")
 If HttpUtils.IsSuccess(UpdateURL & "update.php?push=rominfo") Then
	RomInfo = HttpUtils.GetString(UpdateURL & "update.php?push=rominfo")
		If IsPaused(Main) Then
 			'StartActivity(Main)
 			RefreshDataFlag = True
		Else
 			CallSub(Main, "UpdateLabels")
		End If
 End If
End Sub
Sub Service_Destroy
Log("Service Closed")
End Sub

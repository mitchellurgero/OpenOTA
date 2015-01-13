Type=Service
Version=2.71
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

'Service module
Sub Process_Globals
    Dim HC As HttpClient
    'Activity is expected to set URL
    Dim URL As String
    Dim Target As OutputStream
    Dim JobStatus As Int
    Dim STATUS_NONE, STATUS_WORKING, STATUS_DONE As Int
    STATUS_NONE = 0
    STATUS_WORKING = 1
    STATUS_DONE = 2
    Dim DoneSuccessfully As Boolean
    Dim Notification1 As Notification
End Sub
Sub Service_Create
    HC.Initialize("HC")
    Notification1.Initialize
    Notification1.Icon = "icon" 'use the application icon file for the notification
    Notification1.Vibrate = False
End Sub

Sub Service_Start
    'URL and Target should be set by the calling module
    Dim request As HttpRequest
    request.InitializeGet(URL)
    HC.Execute(request, 1)
    JobStatus = STATUS_WORKING
    Notification1.SetInfo("OTA Update", "Downloading: " & URL, Main)
    Notification1.Sound = False
    'Make sure that the process is not killed during the download
    'This is important if the download is expected to be long.
    'This will also show the status bar notification
    Service.StartForeground(1, Notification1) 
End Sub

Sub HC_ResponseError (Reason As String, StatusCode As Int, TaskId As Int)
    ToastMessageShow("Error downloading file: " & Reason, True)
    DoneSuccessfully = False
    Finish
End Sub

Sub HC_ResponseSuccess (Response As HttpResponse, TaskId As Int)
    'Asynchronously download the stream
    Response.GetAsynchronously("Response", Target, True, TaskId)
End Sub

Sub Response_StreamFinish (Success As Boolean, TaskId As Int)
    If Success = False Then
        ToastMessageShow("Error downloading file: " & LastException.Message, True)
    Else
        ToastMessageShow("Download successfully.", False)
    End If
    DoneSuccessfully = Success
    Finish
End Sub

Sub Finish
    Log("Service finished downloading")
    JobStatus = STATUS_DONE
    'Notify the activity that the download has finished.
    'It will do nothing if the activity is currently paused.
    CallSub(Main, "FinishDownload")
    Service.StopForeground(1) 'Return the service to the "background" (also removes the ongoing notification)
    If IsPaused(Main) Then
        'The activity is paused. The user is probably busy with some other activity.
        'Notify the user that the download has finished
        Notification1.Sound = True
        Notification1.SetInfo("Download Service", "Download complete", Main)
        Notification1.AutoCancel = True
        Notification1.Notify(1)
    End If
End Sub
Sub Service_Destroy

End Sub

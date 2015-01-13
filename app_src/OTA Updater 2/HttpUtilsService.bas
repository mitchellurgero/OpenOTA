Type=Service
Version=2.71
@EndOfDesignText@
#Region Module Attributes
	#StartAtBoot: False
#End Region

'Version 1.04
Sub Process_Globals
	Dim hc As HttpClient
	Dim task As Int
	Dim countWorking As Int
	Dim finishTasks As Int
	Dim maxTasks As Int
	maxTasks = 10
	Dim taskToRequest As Map
	Dim TempFolder As String
	Dim Post As Boolean
	Dim PostBytes() As Byte
	Dim PostInputStream As InputStream
	Dim PostLength As Int
	Dim hcIsInitialized As Boolean
End Sub
Sub Service_Create
	If TempFolder = "" Then TempFolder = File.DirInternalCache
	If hcIsInitialized = False Then
		hc.Initialize("hc")
		hcIsInitialized = True
	End If
End Sub

Sub Service_Start
	If HttpUtils.Tasks.IsInitialized = False Then Return
	taskToRequest.Initialize
	finishTasks = 0
	task = 0
	countWorking = 0
	Do While task < HttpUtils.Tasks.Size
		ProcessNextTask
		If countWorking >= maxTasks Then Exit
	Loop
End Sub
Sub ProcessNextTask
	If finishTasks >= HttpUtils.Tasks.Size Then
		HttpUtils.Working = False
		HttpUtils.Complete = True
		'Raise job done event
		If HttpUtils.CallbackJobDoneSub <> "" Then
			CallSub2(HttpUtils.CallbackActivity, HttpUtils.CallbackJobDoneSub, HttpUtils.Job)
		End If
		'If no new job was started then we kill this service.
		If HttpUtils.Working = False Then
			StopService("")
		End If
		Return
	End If
	If task >= HttpUtils.Tasks.Size Then Return
	Dim link As String
	link = HttpUtils.Tasks.Get(task)
	Dim req As HttpRequest
	If Post Then
		If PostInputStream.IsInitialized Then
			req.InitializePost(link, PostInputStream, PostLength)
		Else
			req.InitializePost2(link, PostBytes)
		End If
	Else
		req.InitializeGet(link)
	End If
	'Here you can modify the request object if needed
	countWorking = countWorking + 1
	taskToRequest.Put(task, link)
	hc.Execute(req, task)
	task = task + 1
End Sub

Sub hc_ResponseSuccess (Response As HttpResponse, TaskId As Int)
	Response.GetAsynchronously("response", File.OpenOutput(TempFolder, TaskId, False), _
		True, TaskId)
End Sub
Sub Response_StreamFinish (Success As Boolean, TaskId As Int)
	finishTasks = finishTasks + 1
	countWorking = countWorking - 1
	If Success = False Then
		HandleError(TaskId, LastException.Message)
	Else
		HttpUtils.SuccessfulUrls.Put(taskToRequest.Get(TaskId), TaskId)
		'Raise URL done event
		If HttpUtils.CallbackUrlDoneSub <> "" Then
			CallSub2(HttpUtils.CallbackActivity, HttpUtils.CallbackUrlDoneSub, taskToRequest.Get(TaskId))
		End If
	End If
	ProcessNextTask
End Sub
Sub hc_ResponseError (Response As HttpResponse, Reason As String, StatusCode As Int, TaskId As Int)
	countWorking = countWorking - 1
	finishTasks = finishTasks + 1
	HandleError(TaskId, Reason)
	If Response <> Null Then
		Log(Response.GetString("UTF8"))
		Response.Release
	End If
	ProcessNextTask
End Sub
Sub HandleError(TaskId As Int, Reason As String)
	Dim link As String
	link = taskToRequest.Get(TaskId)
	Log("Error. Url=" & link & " Message=" & Reason)
End Sub
Sub Service_Destroy
	HttpUtils.Working = False
End Sub

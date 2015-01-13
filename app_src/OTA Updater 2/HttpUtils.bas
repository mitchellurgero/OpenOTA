Type=StaticCode
Version=2.71
@EndOfDesignText@
'Version 1.04
Sub Process_Globals
	Dim Tasks As List 'List of URLs to fetch.
	'A map that holds the successful links.
	'The links are stored as keys. The values are not used.
	Dim SuccessfulUrls As Map 
	Dim Working As Boolean 'True when a job is still running
	Dim Complete As Boolean 'True after a job has completer
	Dim Job As String 'Name of the current running Job
	Dim CallbackActivity As String 'Name of Activity that handles the callbacks.
	Dim CallbackJobDoneSub As String 'Name of the JobDone callback sub.
	Dim CallbackUrlDoneSub As String 'Name of the UrlDone callback sub.
End Sub
'Download a single resource (GET method).
Sub Download(JobName As String, URL As String)
	DownloadList(JobName, Array As String(URL))
End Sub
'Downloads a list of resources (GET method).
Sub DownloadList(JobName As String, URLs As List)
	If internalCheckIfCanStart(JobName) = False Then Return
	Tasks = URLs
	HttpUtilsService.Post = False
	StartService(HttpUtilsService)
End Sub
'Sends a POST request with the given string as the post data
Sub PostString(JobName As String, URL As String, Text As String)
	PostBytes(JobName, URL, Text.GetBytes("UTF8"))
End Sub
'Sends a POST request with the given file send as the post data.
Sub PostFile(JobName As String, URL As String, Dir As String, FileName As String)
	If internalCheckIfCanStart(JobName) = False Then Return
	Dim length As Int
	If Dir = File.DirAssets Then
		Log("Cannot send files from the assets folder.")
		Return
	End If
	length = File.Size(Dir, FileName)
	Dim In As InputStream
	In = File.OpenInput(Dir, FileName)
	If length < 1000000 Then '1mb
		'There are advantages for sending the file as bytes array. It allows the Http library to resend the data
		'if it failed in the first time.
		Dim out As OutputStream
		out.InitializeToBytesArray(length)
		File.Copy2(In, out)
		HttpUtilsService.PostInputStream = Null
		HttpUtilsService.PostBytes = out.ToBytesArray
	Else
		HttpUtilsService.PostInputStream = In
		HttpUtilsService.PostLength = length
	End If
	Tasks = Array As String(URL)
	HttpUtilsService.Post = True
	StartService(HttpUtilsService)
End Sub
'Sends a POST request with the given data as the post data.
Sub PostBytes(JobName As String, URL As String, Data() As Byte)
	If internalCheckIfCanStart(JobName) = False Then Return
	HttpUtilsService.PostInputStream = Null
	HttpUtilsService.PostBytes = Data
	Tasks = Array As String(URL)
	HttpUtilsService.Post = True
	StartService(HttpUtilsService)
End Sub
Sub internalCheckIfCanStart(JobName As String) As Boolean
	If Working Then
		Log("Already working. Request ignored (" & JobName & ")")
		Return False
	End If
	Log("Starting Job: " & JobName)
	Job = JobName
	Working = True
	Complete = False
	SuccessfulUrls.Initialize
	Return True
End Sub
Sub IsSuccess(URL As String) As Boolean
	Return SuccessfulUrls.ContainsKey(URL)
End Sub

'Get methods should be called only after the JobDone event or the UrlDone event.
Sub GetString(URL As String) As String
	If IsSuccess(URL) = False Then
		Log("Task not completed successfully.")
		Return ""
	End If
	Return File.GetText(HttpUtilsService.TempFolder, SuccessfulUrls.Get(URL))
End Sub

Sub GetBitmap(URL As String) As Bitmap
	Dim b As Bitmap
	If IsSuccess(URL) = False Then
		Log("Task not completed successfully.")
		Return b
	End If
	b = LoadBitmap(HttpUtilsService.TempFolder, SuccessfulUrls.Get(URL))
	Return b
End Sub

Sub GetInputStream(URL As String) As InputStream
	Dim In As InputStream
	If IsSuccess(URL) = False Then
		Log("Task not completed successfully.")
		Return In
	End If
	In = File.OpenInput(HttpUtilsService.TempFolder, SuccessfulUrls.Get(URL))
	Return In
End Sub
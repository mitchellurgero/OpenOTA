<?php
$CurrentVersion='7'; //Version Number.
$DownloadPath='http://domain.com/ota/path_to_update.zip'; // Path to the update.zip Download.
$BasicRomInfo='Some ROM Info here.'; // Limited to 250 Characters for now.
//
//DO NOT MODIFY ANYTHING PAST THIS POINT, UNLESS YOU KNOW WHAT YOU ARE DOING.
//
$GETCom = htmlspecialchars($_GET["push"]);
if ($GETCom == "DownloadUpdate") {
header( 'Location: '.$DownloadPath );
}
elseif ($GETCom == "query") {
echo $CurrentVersion;
}
elseif ($GETCom == "rominfo") {
echo $BasicRomInfo;
}
else {
echo 'You must input correct syntax!';
}

?>

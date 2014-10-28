<?php
$CurrentVersion='7';
$DownloadPath='http://service.urgero.org/fx3roms/download.php?file=AdvancedStock7V7.zip';
$BasicRomInfo='This is update addresses OTA Updater issues from version 6. This update is safe to "dirty flash"';
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
OpenOTA
=======

Open Source OTA Checker for Android (PHP/JAVA/BASIC)
This is a self hosted solution to getting Custom ROM OTA updates. This is a prototype and may change in the future.
Please report any bugs found.

How To Install
===============
1. Download master.zip and upload to a php server that can serve php and zip properly.
2. Insert the system folder into your roms system folder so it looks like:
          /system
                /ota.prop
                /ota.url.prop
                
3. Edit ota.prop and ota.url.prop to match your server and ROM.
                ota.url.prpo format is "http://domain.com/ota/" YOU WILL NEED A TRAILING SLASH
                ota.prop is just the version number of your ROM (E.G 1.2.3.4)(Technically speaking, if the Latest version is different from the currently running version, the app will prompt the user to download.)
4. edit update.php variables to match your latests ROM.
5. Test the application to make sure the update works, and enjoy.

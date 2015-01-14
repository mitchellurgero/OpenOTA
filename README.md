OpenOTA
=======

Open Source OTA Checker for Android (PHP/JAVA/BASIC)
This is a self hosted solution to getting Custom ROM OTA updates. This is a prototype and may change in the future.
Please report any bugs found.

How To Install/Configure for your ROM:
===============
1. Download master.zip and upload to a php server that can serve php and zip properly.
2. Insert the system folder into your roms system folder so it looks like:
          /system

                /ota.prop
                
                /ota.url.prop
                
3. Edit ota.prop and ota.url.prop to match your server and ROM.

                ota.url.prop format is "http://domain.com/ota/" YOU WILL NEED A TRAILING SLASH
                
                ota.prop is just the version number of the users CURRENT ROM, Meaning before the update. This number is the "running" version of the ROM where as update.php will have the "latest" or "current" version of the ROM. (Technically speaking, if the Latest version is different from the currently running version, the app will prompt the user to download.)
4. Add the following to the permissions part of updater-script:

          set_perm(0, 2000, 0755, "/system/ota.prop");
          
          set_perm(0, 2000, 0755, "/system/ota.url.prop");
          
5. edit update.php variables to match your latests ROM. Then upload update.php to your webserver(PHP 5.4+, Apache, NGINX, IIS, ETC)
6. Test the application to make sure the update works, and enjoy.

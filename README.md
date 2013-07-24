servdroid
=========

ServDroid is an small web server for the Android platform.

* Auto start options
* Can run under port 1024 (iptables and super user rights are required). Check the [wiki](https://github.com/joanpuigsanz/servdroid/wiki/Use-port-under-1024) for more information
* Only serves HTML pages (Servlets implementations will be considered for in future versions).
* Log of all requests are saved in the Android database. This information can be dumped to a text file.
* ServDroid can be configured to vibrate when a request is received.
* The 404 error page can be personalised.
Available on [Google Play store](https://play.google.com/store/apps/details?id=org.servDroid.web)

The version 0.2.4 and above allows the developer to manage the ServDroid service from an other application. [Here](https://github.com/joanpuigsanz/servdroid/wiki/How-to-bind-servDroid-service) you will find a little tutorial explaining the few steps needed to bind the service.

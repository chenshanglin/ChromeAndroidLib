ChromeAndroidLib
================

Use Chrome Android 33 as a lib to implement WebView.

ChromeLib:<br>
		Use chrome android 33 as a core lib to implement browser on Android. The primary work is:<br>
			1) adapter exported interface to implement WebView interface, use chrome as a WebViewProvider
			2) export other chrome browser functionality
		
Chrome code change list:<br>
		  1) add HawkBrowserTab.java implements TabBase.java 
			2) disable multiple windows support
		
ChromeLibTest:
		A test project for ChromeLib
(Eye-Fi) Auto Preview
--------------

This application was created (around the summer of 2010) as a simple project to help me learn about Java 7 NIO.

This is a simple application that monitors a directory and automatically opens the editor for any new files created in that directory.

Originally created to work with the Eye-Fi card, this application can monitor your Eye-fi upload directory and automatically preview images as they are updated.

Of course, this application an monitor any directory and launch the default application for any new files created.

There are probably many tools that do the same thing as this app, and are probably better at it.

autopreview.properties
-------
There are two properties you can set to control the behavior of Auto Preivew.

__base.dir__=[The directory tree to monitor]
The default value is base.dir=C:\\Users\\dalelotts\\Pictures\\Eye-Fi\\ - obvously you should change this to a directory that exists on your machine.

__sub.dir.format__[The sub directory format used by the Eye-Fi uploader]
The default value is sub.dir.format=YYYY\\MM\\dd - this means that Eye-Fi uploads the files to a custom date formatted directed.


Future Enhancements
-----
Automatically Detect Eye-Fi upload directory (Currently, you must specify the directory in autopreivew.properties.
Able to monitor multiple unrelated directories
Able to specify application to launch when new file is detected
UI to display during execution - this would also support changing the configuration

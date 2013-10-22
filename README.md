jdotxt
======

Copyright 2013 Christian M. Schmid

another open source cross-platform GUI for the todo.txt file format

#### Website

- [jdotxt](http://jdotxt.chschmid.com/), where you will find downloads for different operating systems and a quick guide video

#### Building jdotxt

to build jdotxt from its sources, you will need
- a Java Development Kit (JDK) Version 7 or higher http://www.oracle.com/technetwork/java/javase/downloads/index.html
- Apache Ant as build system http://ant.apache.org/

I use Ubuntu as a build system, simply run
- sudo apt-get install openjdk-7-jdk ant
to set up your build system.

To build jdotxt
1. Download the latest sources from github (e.g., "git clone https://github.com/chms/jdotxt.git")
2. Move into the directory (e.g., "cd jdotxt")
3. Run ant (i.e., "ant")

You can run the resulting jar file by executing

java -jar jar/jdotxt.jar

#### Third Party Code

jdotxt uses code and libraries from the following open source projects:

- [todo.txt-android](https://github.com/ginatrapani/todo.txt-android): jdotxt uses the same datastructures and IO functions that the official Android client uses.
- [Java Native Access (JNA)](https://github.com/twall/jna#readme): for fixing some Windows 7 taskbar issues.

#### Links

- [todo.txt](http://todotxt.com/) is a simple file format for managing your todos.
- [todo.txt-android](https://github.com/ginatrapani/todo.txt-android) an open source todo.txt Android client
- [Java Native Access (JNA)](https://github.com/twall/jna#readme) provides Java programs easy access to native shared libraries (DLLs on Windows)
- [jdotxt](http://jdotxt.chschmid.com/), well binaries and help for this very program.

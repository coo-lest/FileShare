# FileShare
COMP3015 Project 2020-21

**Group members**

Student ID | Name
---------- | -----
18200028 | Anmol DHAWAN
18250009 | CHEN Dezhi


## Running the program
**JDK used**: JDK 1.8  
Compile the source code with `javac FileShare.java`.   
Put the ***authorized_user*** and ***share_root*** file in the *working directory* 
before starting the program.  
Run the main class by invoking `java FileShare`.

## Notes for naughty users
The developers admit that this is not a robust program. Use with care!

Here are some known issues:
* **Upload/Download/NewFolder to a *File* (instead of a Folder) will cause problem**  
Just make sure you select a *folder* as the destination.  
This may also cause unpredicted problems in subsequent operations!  

* **Error may prompt when login to hosts in *another network***  
Due to some problem with the class `ObjectOutputStream`, bytes of an object may
alter when transferred across networks (according to Mandel). This can be solved
by adopting similar transmission protocol as that in the `Message` class, i.e.
serializing the object and transmitting bytes(not implemented due to time limitation).  

* **Files with the same name will be overwritten**  
The developer is also a victim of this when writing this documentation.

* **Folder with the same name will prevent uploading without any notification**  
... while the same situation for download triggers a prompt.  

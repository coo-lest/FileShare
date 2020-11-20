# FileShare
COMP3015 Project 2020-21
The basic functionality of the program has been adapted.
The GUI has been touched upon but testing seems to be problematic. The layout and the components have been made and the function calls have been defined. Replication needed.
setTitle(""); Not working
Cannot test login, is it becuase of discovery?
Step-by-step approach -- First page of GUI, if works, we can start the next page.

### Notes for naughty users
The developers admit that this is not a robust program. Use with care! Here are some known issues:
* **Error may prompt when login to hosts in *another network***  
Due to some problem with the class `ObjectOutputStream`, bytes of an object may
alter when transferred across networks (according to Mandel). This can be solved
by adopting similar transmission protocol as that in the `Message` class, i.e.
serializing the object and transmitting bytes(not implemented due to time limitation).  
* **Upload/Download/NewFolder to a *File* (instead of a Folder) will cause problem**  
Just make sure you select a *folder* as the destination.
* **Files with the same name will be overwritten**  
The developer is also a victim of this when writing this documentation.
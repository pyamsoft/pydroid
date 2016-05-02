# pydroid
PYDroid library for pyamsoft applications

## What is PYDroid

PYDroid is the **py**amsoft An**droid** library. It contains common 
functions and classes that are used in multiple pyamsoft projects.
It is an always changing, always actively developed library, and as 
such is not meant for use outside of pyamsoft projects.

**It is not written with public usage in mind** and will **not** be taking pull 
requests on GitHub or elsewhere for new features, unless it will result 
in less duplication of effort across other pyamsoft applications.

## PYDroid design guidelines

PYDroid is a small library, and as a result does not have very strict or  
very many design guidelines. Because the library is not meant for public  
use outside of pyamsoft applications, it will not necessarily adhere to  
common design guidelines or accepted practices. The few design guidelines  
are as follows:
+ All classes in the com.pyamsoft.pydroid.util package are meant to be  
statically accessed, meaning that there should be no functions which rely  
on an instance of the class.
+ All classes in the com.pyamsoft.pydroid.util package should have explicitly  
private constructors to prevent their initialization.
+ All classes in the com.pyamsoft.pydroid.base package are meant to be used  
as generic base classes, and are meant to be extended by the implementation.
+ All classes in the com.pyamsoft.pydroid.base package will have all undefined  
function implementations handled by abstract function calls.
+ All classes which do not fall into a category of a base class or a utility  
class will be placed into the com.pyamsoft.pydroid.misc package until a more  
specific home is decided.

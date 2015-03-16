# Introduction #

This project is created with Eclipse. The easiest way to check out a copy of the code is through the Eclipse IDE (http://www.eclipse.org/downloads/).

To set up with SVN, you need to first install the SVN plugin. After starting Eclipse, go to Help->About. Make sure you have a Juno Version of Eclipse.

If not, then install it. If you do then go to Help->Install new software. In the dropdown box labeled works with, select Juno - ... Then expand the collaboration option in the main area and select the box for all of the subversion packages. Install these and it will probably ask you to restart. When it restarts it will probably ask you to set up an adapter for SVN, just select one of the available adapters).

Next, File->New->project. There should be an SVN folder now, select Project from SVN and hit next. Fill out all of the information on the repository

url :“http://wordsimilarity.googlecode.com/svn/trunk/”
username: “wordsimilarity-read-only”

Then it will ask you to check it out as a project, select yes and continue.

Once this is complete, Eclipse will try to automatically build your code. Give it time to complete. In the lower right there will be a progress bar. If there are errors, the following are some common soluions.

To view the project setup. Right click on the project folder and select properties. If you are running on a Mac, you should always look at the Java Compiler tab first. They are always a generation of Java behind (1.6 now) so make sure the program isn't trying to run 1.7 (it will say 1.7 in a bunch of dropdown boxes). If it is 1.7 and you are on a Mac you can select the Enable project specific settings checkbox, and set the select boxes to 1.6.

The next place to look for errors is in the "Java Build Path" which is a choice on the side of the properties tab, just two above "Java compiler". In here, check the source tab first. It will indicate errors with red x's or something similar. But tell me where it is giving errors if there are any. The source should contain the folder that has the main code, as well as the location of the w2w program which must also be setup in eclipse for the other code I sent to work. Finally, check the libraries tab in the Java Build Path. These are the java libraries that you are linking to. They should all be included but if any of them are red, that means you are missing that library.
Now that the project is downloaded to Eclipse, you can run the project by selecting the appropriate main class. This will usually be src->gui->MainGUIExternal.
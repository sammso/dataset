	DataSet Library 1.0 Final
-------------------------------------------------------------
 I currently using this version of DataSet on my projects.
 I'm using DataSet on production.
 - Web applications
 - On my NetForm project http://netform.sohlman.com
 - DataTransfer applications.
 - Small Swing application

Changes 
------------------------------------------------------------- 
 Changes in Final
 - SQLColumn info has more information, see javadoc
 - Fixes on swing section
 - Another bug fix on SQLWriteEngine.getTableName()
 - new method DataSet.isModified() see Javadoc
 - Modified SQLTool example application

 Changes in RC 3
 - Row.setValueAt throws now ClassCastException, this helps to find errors on application 
   code.
 - New Methodd DataSet.setValue(..) 
   + to changing values to whole row at once.
 - Swing package developement
 - Swing example application on examples/datasetswing
   + does nothing important, but just one test case to swing connection.
 - To run build.xml you need also the change value of easylayout.jar property in build.xml 
   file
 Changes in RC 2
 - Bug fix on SQLWriteEngine.getTableName()
 Changes in RC 1
 - Row setValueAt checking class is now corrected. Before it used to use 
   class name now it is using Class.instanceOf() method which is working 
   more precise
 - RowInfo.getColumnClass(int) index is now 1 - size
 - Fixed bug on SQLWriteEngine.keyWordSearchIndexOf(..)

 Changes in Beta 6
 - Package structure fix. 
    + build.xml didn't put swingsrc to it's folder.
 - FileReadEngine fix on Timestamp range.
 
 Changes in Beta 5
 - Fixed bug on removeRow event generation
 - syncronizeFrom functionality has been rewritten. API has
   been changed. It is better now.
 - Row and DataSet getValueAt throws ArrayIndexOutOfBoundsException 
   if row or column is out of range.
 - New CVS and package structure.
   - build.xml to create newest version from CVS
   - swing functionality removed and transferred to another .jar
 - Write statements generation developed further
 - Swing developement.
 - Rename KeyAction ModifyAction also it is bounded to RowInfo
 
Before Beta 4 - Read from Earlier releases
 
Future Ideas
-------------------------------------------------------------
 - Tight SWT (Eclipse) integration
 - DataSet Taglib for JSP for reporting purposes
 - Connection to NetForm web application framework. 	
   http://netform.sohlman.com

Installation instructions:
-------------------------------------------------------------
 put dataset.jar from ./lib directory to your classpath
 if you are using com.sohlman.dataset.swing classes
 put also dataset-swing.jar to your classpath
 
Demo
-------------------------------------------------------------
 Go to ./lib and read SQLTool.ini file and do necesary 
 changes.

How to use
-------------------------------------------------------------
 See demo, javadocs and tutorials on 
 http://dataset.sohlman.com

Licence
-------------------------------------------------------------
 LGPL - Lesser General Public License

Author
-------------------------------------------------------------
 Sampsa Sohlman (http://sampsa.sohlman.com)
  - All comments are more than welcome

	DataSet Library 1.0 Beta 5
-------------------------------------------------------------
 I currently using this version of DataSet on my projects.
 I'm using DataSet on production.
 - Web applications
 - DataTransfer applications.
 - Small Swing application

Changes 
------------------------------------------------------------- 
 Before Beta 4 - Read from Earlier releases
 Beta 5
 - Fixed bug on removeRow event generation
 - syncronizeFrom functionality has been rewritten. API has
   been changed. It is better now.
 - Row and DataSet getValueAt throws ArrayIndexOutOfBoundsException 
   if row or column is out of range.
 - New CVS and package structure.
   - build.xml to create newest version from CVS
   - swing functionality removed and transferred to another .jar
 - Swing support removed it is now on own .jar file.
 - Write statements generation developed further
 - Swing developement.
 - Rename KeyAction ModifyAction also it is bounded to RowInfo
 
Future Ideas
-------------------------------------------------------------
 - Tight SWT (Eclipse) integration
 - DataSet Taglib for JSP for reporting purposes

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

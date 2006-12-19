# run_pv.r: runs PathVisio from R enviroment

# load library and set working directory
library("rJava")
setwd("D:/Work/BI/SVN")

# initialize jvm
.jinit(classpath = "D:/Work/BI/SVN/pathvisio_v2.jar; D:/Work/BI/SVN/lib/org.eclipse.jface.jar; D:/Work/BI/SVN/lib/swt-win32.jar; D:/Work/BI/SVN/lib/swt-win32-lib.jar")

# create window object
# gmmlVisionWindow window = new gmmlVisionWindow();
window <- .jcall("gmmlVision/GmmlVision", "LgmmlVision/GmmlVisionWindow;", "getWindow");

# Initiates some objects used by the program
# initiate()
.jcall("gmmlVision/GmmlVisionMain", "V", "initiate");

# pass control ot the program to the window
# window.setBlockOnOpen(true);
.jcall(window, "V", "setBlockOnOpen", TRUE);

# pass data file with map to use to the window so it will be shown when the window opens.
# window.setPWF("D:\\Work\\BI\\SVN\\testData\\test.gpml");
.jcall(window, "V", "setPWF", "D:\\Work\\BI\\data\\GpmlFiles\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml");

# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", "D:\\Work\\BI\\data\\Rn_39_34i.pgdb");

# open window 
# window.open();
.jcall(window, "I", "open");

setwd("D:/Work/BI/SVN/JR-Interface")











# run_pv.r: runs PathVisio from R environment

# load library and set working directory
library("rJava")
setwd("D:/Project BioInformatica/SVN")

# initialize jvm
# loading classpath's : pathvisio_v2.jar, jface.jar, swt-win32.jar, swt-win32-lib.jar
.jinit(classpath = "D:/Project BioInformatica/SVN/pathvisio_v2.jar; D:/Project BioInformatica/SVN/lib/org.eclipse.jface.jar; D:/Project BioInformatica/SVN/lib/swt-win32.jar; D:/Work/BI/SVN/lib/swt-win32-lib.jar")

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
.jcall(window, "V", "setPWF", "C:\\Documents and Settings\\s040778\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml");

# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", "C:\\Documents and Settings\\s040778\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb");

# Select data file with expression dataset
.jcall(window, "V", "setExName", "C:\\Documents and Settings\\s040778\\Pathvisio-Data\\expression datasets\\expr_genmapp_format.pgex");

# open window 
# window.open();
.jcall(window, "I", "open");

setwd("D:/Project BioInformatica/SVN/JR-Interface")











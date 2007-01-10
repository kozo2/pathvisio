# run_pv.r: runs PathVisio from R environment

openPathway <- function(y1) {

# load library and set working directory
library("rJava")
setwd("D:\\Project BioInformatica\\SVN")

# initialize jvm
# loading classpath's : pathvisio_v2.jar, jface.jar, swt-win32.jar, swt-win32-lib.jar
base <- "D:\\Project BioInformatica\\SVN\\lib\\"
classpath = paste (base, c(
	"swt-win32.jar",
	"pathvisio_v2.jar",
	"jdom.jar",
	"JRI.jar",
	"org.eclipse.core.commands.jar",
	"org.eclipse.equinox.common.jar",
	"org.eclipse.jface.jar",
	"derby.jar",
	"junit.jar",
	"BrowserLauncher.jar",
	"resources.jar",
	"R-resources.jar"
), sep="");

.jinit(classpath)

# create window object
# gmmlVisionWindow window = new gmmlVisionWindow();
window <- .jcall("gmmlVision/GmmlVision", "LgmmlVision/GmmlVisionWindow;", "getWindow");

# Initiates some objects used by the program
# initiate()
.jcall("gmmlVision/GmmlVisionMain", "V", "initiate");

# pass control ot the program to the window
# window.setBlockOnOpen(true);
.jcall(window, "V", "setBlockOnOpen", TRUE);

# pass data file with pathway to use to the window so it will be shown when the window opens.
# window.setPWF("D:\\Work\\BI\\SVN\\testData\\test.gpml");
.jcall(window, "V", "setPWF", "D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml");

# open window 
# window.open();
.jcall(window, "I", "open");
}


openGenDb <- function(y1,y2) {

# load library and set working directory
library("rJava")
setwd("D:/Project BioInformatica/SVN")

# initialize jvm
# loading classpath's : pathvisio_v2.jar, jface.jar, swt-win32.jar, swt-win32-lib.jar
base <- "D:\\Project BioInformatica\\SVN\\lib\\"
classpath = paste (base, c(
	"swt-win32.jar",
	"pathvisio_v2.jar",
	"jdom.jar",
	"JRI.jar",
	"org.eclipse.core.commands.jar",
	"org.eclipse.equinox.common.jar",
	"org.eclipse.jface.jar",
	"derby.jar",
	"junit.jar",
	"BrowserLauncher.jar",
	"resources.jar",
	"R-resources.jar"
), sep="");

.jinit(classpath)

# create window object
# gmmlVisionWindow window = new gmmlVisionWindow();
window <- .jcall("gmmlVision/GmmlVision", "LgmmlVision/GmmlVisionWindow;", "getWindow");

# Initiates some objects used by the program
# initiate()
.jcall("gmmlVision/GmmlVisionMain", "V", "initiate");

# pass control ot the program to the window
# window.setBlockOnOpen(true);
.jcall(window, "V", "setBlockOnOpen", TRUE);

# pass data file with pathway to use to the window so it will be shown when the window opens.
# window.setPWF("D:\\Work\\BI\\SVN\\testData\\test.gpml");
.jcall(window, "V", "setPWF", "D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml");

# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", "D:\\Project BioInformatica\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb");

# open window 
# window.open();
.jcall(window, "I", "open");
}


openExpr <- function(y1,y2,y3) {

# load library and set working directory
library("rJava")
setwd("D:/Project BioInformatica/SVN")

# initialize jvm
# loading classpath's : pathvisio_v2.jar, jface.jar, swt-win32.jar, swt-win32-lib.jar
base <- "D:\\Project BioInformatica\\SVN\\lib\\"
classpath = paste (base, c(
	"swt-win32.jar",
	"pathvisio_v2.jar",
	"jdom.jar",
	"JRI.jar",
	"org.eclipse.core.commands.jar",
	"org.eclipse.equinox.common.jar",
	"org.eclipse.jface.jar",
	"derby.jar",
	"junit.jar",
	"BrowserLauncher.jar",
	"resources.jar",
	"R-resources.jar"
), sep="");

.jinit(classpath)

# create window object
# gmmlVisionWindow window = new gmmlVisionWindow();
window <- .jcall("gmmlVision/GmmlVision", "LgmmlVision/GmmlVisionWindow;", "getWindow");

# Initiates some objects used by the program
# initiate()
.jcall("gmmlVision/GmmlVisionMain", "V", "initiate");

# pass control ot the program to the window
# window.setBlockOnOpen(true);
.jcall(window, "V", "setBlockOnOpen", TRUE);

# pass data file with pathway to use to the window so it will be shown when the window opens.
# window.setPWF("D:\\Work\\BI\\SVN\\testData\\test.gpml");
.jcall(window, "V", "setPWF", "D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml");

# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", "D:\\Project BioInformatica\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb");

# Select data file with expression dataset
.jcall(window, "V", "setExName", "D:\\Project BioInformatica\\Pathvisio-Data\\expression datasets\\expr_genmapp_format.pgex");

# open window 
# window.open();
.jcall(window, "I", "open");
}


createExpr <- function(y1,y2,y3) {

# load library and set working directory
library("rJava")
setwd("D:/Project BioInformatica/SVN")

# initialize jvm
# loading classpath's : pathvisio_v2.jar, jface.jar, swt-win32.jar, swt-win32-lib.jar
base <- "D:\\Project BioInformatica\\SVN\\lib\\"
classpath = paste (base, c(
	"swt-win32.jar",
	"pathvisio_v2.jar",
	"jdom.jar",
	"JRI.jar",
	"org.eclipse.core.commands.jar",
	"org.eclipse.equinox.common.jar",
	"org.eclipse.jface.jar",
	"derby.jar",
	"junit.jar",
	"BrowserLauncher.jar",
	"resources.jar",
	"R-resources.jar"
), sep="");

.jinit(classpath)

# create window object
# gmmlVisionWindow window = new gmmlVisionWindow();
window <- .jcall("gmmlVision/GmmlVision", "LgmmlVision/GmmlVisionWindow;", "getWindow");

# Initiates some objects used by the program
# initiate()
.jcall("gmmlVision/GmmlVisionMain", "V", "initiate");

# pass control ot the program to the window
# window.setBlockOnOpen(true);
.jcall(window, "V", "setBlockOnOpen", TRUE);

# pass data file with pathway to use to the window so it will be shown when the window opens.
# window.setPWF("D:\\Project BioInformatica\\SVN\\testData\\test.gpml");
.jcall(window, "V", "setPWF", "D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml");

# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", "D:\\Project BioInformatica\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb");

# create expression dataset
head = as.integer(1)
first = as.integer(2)
id = as.integer(0)
code = as.integer(1)
cols = .jarray(c(as.integer(0),as.integer(1)))
file = "D:\\Project BioInformatica\\Data\\expr_genmapp_format.txt"
dbName = "D:\\Project BioInformatica\\PathVisio-Data\\gene databases\\Rn_39_34i.pgdb"
.jnew("data/RImporter", cols, file, dbName, head, first, id, code)


# Select data file with expression dataset
.jcall(window, "V", "setExName", "D:\\Project BioInformatica\\Data\\expr_genmapp_format.pgex");

# open window 
# window.open();
.jcall(window, "I", "open");
}
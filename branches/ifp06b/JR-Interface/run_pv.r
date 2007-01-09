# run_pv.r: runs PathVisio from R environment

# load library
library("rJava")
setwd("D:/Work/BI/SVN")

# initialize jvm
# loading classpath's : pathvisio_v2.jar, jface.jar, swt-win32.jar, swt-win32-lib.jar
base <- "D:\\Work\\BI\\SVN\\"
classpath = paste (base, c(
	"lib\\swt-win32.jar",
	"pathvisio_v2.jar",
	"lib\\jdom.jar",
	"lib\\JRI.jar",
	"lib\\org.eclipse.core.commands.jar",
	"lib\\org.eclipse.equinox.common.jar",
	"lib\\org.eclipse.jface.jar",
	"lib\\derby.jar",
	"lib\\junit.jar",
	"lib\\BrowserLauncher.jar",
	"lib\\resources.jar",
	"lib\\R-resources.jar"
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

# pass data file with map to use to the window so it will be shown when the window opens.
# window.setPWF("D:\\Work\\BI\\SVN\\testData\\test.gpml");
.jcall(window, "V", "setPWF", "D:\\Work\\BI\\data\\GpmlFiles\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml");


# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", "D:\\Work\\BI\\data\\Rn_39_34i.pgdb");

# create expression dataset
head = as.integer(1)
first = as.integer(2)
id = as.integer(0)
code = as.integer(1)
cols = .jarray(c(as.integer(0),as.integer(1)))
file = "D:\\Work\\BI\\data\\expr_genmapp_format.txt"
dbName = "D:\\Work\\BI\\data\\Rn_39_34i.pgdb"
.jnew("data/RImporter", cols, file, dbName, head, first, id, code)


# Select data file with expression dataset
.jcall(window, "V", "setExName", "D:\\Work\\BI\\data\\expr_genmapp_format.pgex");


# open window 
# window.open();
.jcall(window, "I", "open");
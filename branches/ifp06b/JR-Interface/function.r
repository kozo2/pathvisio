# run_pv.r: runs PathVisio from R environment

# load library and set working directory
library("rJava")
setwd("D:/Project BioInformatica/SVN")



# function to set the base 
setBase <- function(pass="D:\\Project BioInformatica\\SVN\\") {
base <<- pass;
setClassPath();
}

# function to set classpath
setClassPath <- function() 
{
pass= paste (base, c(
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
classpath <<- pass
}



# function to open a pathway
# parameter y1: path of pathway
openPathway <- function(y1="D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml") {

# initialize jvm
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
.jcall(window, "V", "setPWF", y1);

# open window 
# window.open();
.jcall(window, "I", "open");
}




# function to open a pathway and gene database in Pathvisio
# first parameter y1: path of pathway
# second parameter y2: path of gene database
openGenDb <- function(	y1="D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml",
			y2="D:\\Project BioInformatica\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb") {

# initialize jvm
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
.jcall(window, "V", "setPWF", y1);

# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", y2);

# open window 
# window.open();
.jcall(window, "I", "open");
}




# function to open Pathvisio with pathway,gene database and expression dataset
# first parameter y1: path of pathway
# second parameter y2: path of gene database
# third parameter y3: path of expression dataset (.pgex)

openExpr <- function(	y1="D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml",
			y2="D:\\Project BioInformatica\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb",
			y3="D:\\Project BioInformatica\\Pathvisio-Data\\expression datasets\\expr_genmapp_format.pgex") {

# initialize jvm
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
.jcall(window, "V", "setPWF", y1);

# pass data file with gene database to use to the window
.jcall(window, "V", "setDbName", y2);

# Select data file with expression dataset
.jcall(window, "V", "setExName", y3);

# open window 
# window.open();
.jcall(window, "I", "open");
}



# function to create a expression dataset from a txt-bestand and show Pathvisio
# first parameter y1: path of pathway
# second parameter y2: path of gene database
# third parameter y3: path of txt file 
createExpr <- function(	y1="D:\\Project BioInformatica\\Pathvisio-Data\\pathways\\MAPPs\\Rn_Contributed_20060824\\cellular_process-GenMAPP\\Rn_Apoptosis.gpml",
			y2="D:\\Project BioInformatica\\Pathvisio-Data\\gene databases\\Rn_39_34i.pgdb",
			y3="D:\\Project BioInformatica\\Data\\expr_genmapp_format.txt") {

# initialize jvm
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
file = y3
dbName = "D:\\Project BioInformatica\\PathVisio-Data\\gene databases\\Rn_39_34i.pgdb"
.jnew("data/RImporter", cols, file, dbName, head, first, id, code)


# Select data file with expression dataset
.jcall(window, "V", "setExName", "D:\\Project BioInformatica\\Data\\expr_genmapp_format.pgex");

# open window 
# window.open();
.jcall(window, "I", "open");
}
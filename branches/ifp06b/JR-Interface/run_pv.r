# run_pv.r: runs PathVisio from R enviroment

# load library and set working directory
library("rJava")
setwd("D:/Project BioInformatica/SVN")

# initialize jvm
.jinit(classpath = "D:/Project BioInformatica/SVN/pathvisio_v1.jar")

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
.jcall(window, "V", "setPWF","C:\\Documents and Settings\\s040778\\Pathvisio-Data\\pathways\\MAPPs\\Hs_Apoptosis_plus.gpml");

# open window 
# window.open();
.jcall(window, "I", "open");

setwd("D:/Project BioInformatica/SVN")











# run_pv.r
library("rJava")
setwd("D:/Work/BI/SVN")
# initialize jvm
.jinit(classpath = "D:/Work/BI/SVN/pathvisio_v1.jar")

# create window object
# gmmlVisionWindow window = new gmmlVisionWindow();
window <- .jcall("gmmlVision/GmmlVision", "LgmmlVision/GmmlVisionWindow;", "getWindow");

# Initiates some objects used by the program
# initiate(classpath = "D:/Work/BI/SVN/pathvisio_v1.jar"););
.jcall("gmmlVision/GmmlVisionMain", "V", "initiate");
.jcall("gmmlVision/GmmlVision", "V", "openPathway", "D:\Work\BI\SVN\testData\test.gpml")


# open window [?]
# window.setBlockOnOpen(true);
# window.open();
.jcall(window, "V", "setBlockOnOpen", TRUE);
.jcall(window, "I", "open");

# open pathway
setwd("D:/Work/BI/R")






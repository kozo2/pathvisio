#!/bin/sh
export LD_LIBRARY_PATH=/usr/local/lib:/usr/lib/atlas:/usr/lib/firefox
export MOZILLA_FIVE_HOME=/usr/lib/firefox

MYCLASSPATH=lib/JRI.jar:\
lib/BrowserLauncher.jar:\
lib/org.eclipse.equinox.common.jar:\
lib/org.eclipse.equinox.supplement.jar:\
lib/org.eclipse.jface.jar:\
lib/swt-linux-lib/swt.jar:\
lib/org.eclipse.core.commands.jar:\
lib/jdom.jar:\
build/v1:\
lib/derby.jar:\
lib/swt-linux-lib.jar:\
lib/resources.jar:\
lib/batik/batik-awt-util.jar:\
lib/batik/batik-ext.jar:\
lib/batik/batik-script.jar:\
lib/batik/batik-util.jar:\
lib/batik/batik-dom.jar:\
lib/batik/xml-apis.jar:\
lib/batik/batik-xml.jar:\
lib/batik/batik-extension.jar:\
lib/batik/pdf-transcoder.jar:\
lib/batik/batik-css.jar:\
lib/batik/batik-transcoder.jar:\
lib/batik/batik-svg-dom.jar:\
lib/batik/batik-parser.jar:\
lib/batik/batik-svggen.jar:\
lib/batik/batik-bridge.jar:\
lib/batik/batik-gvt.jar

java -cp $MYCLASSPATH -Djava.library.path="/usr/lib/firefox:./lib/swt-linux-lib" org.pathvisio.gui.GuiMain 

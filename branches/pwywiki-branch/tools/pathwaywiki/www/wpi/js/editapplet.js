var image;
var applet;

function replaceWithApplet(idImg, idApplet, keys, values) {
	var image = document.getElementById(idImg);
	var applet = createAppletElement(idApplet, keys, values);
	var resizeDiv = document.createElement('div');
	
	//find getWidth of enclosing element / table
	var parent = image.parentNode;
	var nn = parent.nodeName.toLowerCase();
	if(nn == 'td' || nn == 'tr' || nn == 'tbody') {
		while(true) {
			if(parent.parentNode == null || parent.nodeName.toLowerCase() == 'table') {
				break;
			} else {
				parent = parent.parentNode;
			}
		}
	}
	var w = parent.offsetWidth;

	resizeDiv.setAttribute('style', 'width: ' + w + 'px; height: 500px');
	resizeDiv.setAttribute('class', 'thumbinner');
	resizeDiv.setAttribute('id', idApplet);
	resizeDiv.appendChild(applet);
	replaceElement(image, resizeDiv);
}

function restoreImage() {
	if(image != null && applet != null) {
		replaceElement(applet, image);
	}
}

function replaceElement(elmOld, elmNew) {
	p = elmOld.parentNode;
	p.insertBefore(elmNew, elmOld);
	p.removeChild(elmOld);
}

function createAppletElement(id, keys, values) {
	applet = document.createElement('applet');
	//applet.setAttribute('id', id);
	applet.setAttribute('CODEBASE', '/wpi/applet');
	applet.setAttribute('CODE', 'org.pathvisio.gui.wikipathways.AppletMain');
	applet.setAttribute('ARCHIVE', 'wikipathways.jar');
	applet.setAttribute('WIDTH', '100%');
	applet.setAttribute('HEIGHT', '100%');
	if(keys != null && values != null) {
		for(i=0; i < keys.length; i++) {
			param = document.createElement('PARAM');
			param.setAttribute('NAME', keys[i]);
			param.setAttribute('VALUE', values[i]);
			applet.appendChild(param);
		}
	}
	return applet;
}

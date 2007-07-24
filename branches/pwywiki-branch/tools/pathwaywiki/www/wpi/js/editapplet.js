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

    resizeDiv.style.width = w + 'px';
    resizeDiv.style.height = '500px';
    resizeDiv.className = 'thumbinner';
    resizeDiv.setAttribute('id', idApplet);
    resizeDiv.appendChild(applet);
    replaceElement(image, resizeDiv);
    new Resizeable(idApplet, {bottom: 10, right: 10, left: 0, top: 0});
}

function restoreImage() {
	if(image != null && applet != null) {
		replaceElement(applet, image);
	}
}

function replaceElement(elmOld, elmNew) {
	var p = elmOld.parentNode;
	p.insertBefore(elmNew, elmOld);
	p.removeChild(elmOld);
}

function createAppletElement(id, keys, values) {
	var applet = document.createElement('applet');
	applet.setAttribute('CODEBASE', '/wpi/applet');
	applet.setAttribute('CODE', 'org.pathvisio.gui.wikipathways.AppletMain');
	applet.setAttribute('ARCHIVE', 'wikipathways.jar');
	applet.setAttribute('width', '100%');
	applet.setAttribute('height', '100%');
	if(keys != null && values != null) {
		for(i=0; i < keys.length; i++) {
			var param = document.createElement('PARAM');
			//param.setAttribute('NAME', keys[i]);
			//param.setAttribute('VALUE', values[i]);
			param.name = keys[i];
			param.value = values[i];
			applet.appendChild(param);
		}
	}
	return applet;
}

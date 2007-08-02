//Uses appletobject.js
function doApplet(idImg, idApplet, keys, values) {
	var image = document.getElementById(idImg);

	var ao = new AppletObject(	'org.pathvisio.gui.wikipathways.AppletMain',
				['/wpi/applet/wikipathways.jar'],
				'100%', '100%', '1.5.0', 'false',
				'/wpi/applet',
				[],
				AppletObjects.TAG_OBJECT );
	if(keys != null && values != null) {
		for(i=0; i < keys.length; i++) {
					ao.addParam(keys[i], values[i]);
		}
	}

	image.setAttribute('class', 'thumbinner');

	var w = getParentWidth(image);

	image.style.width = w + 'px';
	image.style.height = '500px';

	new Resizeable(idImg, {bottom: 10, right: 10, left: 0, top: 0});
	ao.preload( idImg );
}

//Manually (doesn't work well, applet is started twice on firefox
function replaceWithApplet(idImg, idApplet, keys, values) {
	var image = document.getElementById(idImg);
	var applet = createObjectElement(idApplet, keys, values);

	image.setAttribute('class', 'thumbinner');

	var w = getParentWidth(image);

	image.style.width = w + 'px';
	image.style.height = '500px';

	new Resizeable(idImg, {bottom: 10, right: 10, left: 0, top: 0});
	image.innerHTML = applet;
}

function getParentWidth(elm) {
	var p = findEnclosingTable(elm);
	return p.offsetWidth;	
}

function findEnclosingTable(elm) {
	//find getWidth of enclosing element / table
	var parent = elm.parentNode;
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
	if(parent.nodeName.toLowerCase() == 'table') return parent;
	else return elm.parentNode; //Not in a table, just return the parent
}

function replaceElement(elmOld, elmNew) {
	var p = elmOld.parentNode;
	p.insertBefore(elmNew, elmOld);
	p.removeChild(elmOld);
}

function createObjectElement(id, keys, values) {
	var tag = '<object classid="java:org.pathvisio.gui.wikipathways.AppletMain.class"'
		+ ' TYPE="application/x-java-applet"'
		+ ' ARCHIVE="wikipathways.jar"'
		+ ' CODEBASE="/wpi/applet"'
		+ ' WIDTH="100%"'
		+ ' HEIGHT="100%"'
		+ ' STANDBY="Loading applet..."'
		+ ' >';
	if(keys != null && values != null) {
		for(var i = 0; i < keys.length; i++) {
			tag += '<param  name="' + keys[i]  + '" ' + 
				'value="' + values[i] + '" />';
		}
	}
	tag += '</object>';
		return tag;
}

function createAppletElement(id, keys, values) {
	var tag = '<applet code="org.pathvisio.gui.wikipathways.AppletMain"'
		+ 'ARCHIVE="wikipathways.jar"'
		+ 'CODEBASE="/wpi/applet"'
		+ 'WIDTH="100%"'
		+ '"HEIGHT="100%"'
		+ '>';
	if(keys != null && values != null) {
		for(var i = 0; i < keys.length; i++) {
			tag += '<param  name="' + keys[i]  + '" ' + 
				'value="' + values[i] + '" />';
		}
	}
	tag += '</applet>';
		return tag;

	/*
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
	 */
}

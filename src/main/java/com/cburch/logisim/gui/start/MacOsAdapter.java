/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.start;

import com.cburch.logisim.gui.prefs.PreferencesFrame;
import com.cburch.logisim.proj.ProjectActions;
import net.roydesign.event.ApplicationEvent;
import net.roydesign.mac.MRJAdapter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//MAC import java.io.File;
//MAC import com.apple.eawt.Application;
//MAC import com.apple.eawt.ApplicationAdapter;

class MacOsAdapter { //MAC extends ApplicationAdapter {

	static void addListeners(boolean added) {
		MyListener myListener = new MyListener();
		if (!added) MRJAdapter.addOpenDocumentListener(myListener);
		if (!added) MRJAdapter.addPrintDocumentListener(myListener);
		MRJAdapter.addPreferencesListener(myListener);
		MRJAdapter.addQuitApplicationListener(myListener);
		MRJAdapter.addAboutListener(myListener);
	}

	public static void register() {
		//MAC Application.getApplication().addApplicationListener(new MacOsAdapter());
	}
	
	/* MAC
	public void handleOpenFile(com.apple.eawt.ApplicationEvent event) {
		Startup.doOpen(new File(event.getFilename()));
	}
	
	public void handlePrintFile(com.apple.eawt.ApplicationEvent event) {
		Startup.doPrint(new File(event.getFilename()));
	}
	
	public void handlePreferences(com.apple.eawt.ApplicationEvent event) {
		PreferencesFrame.showPreferences();
	}
	*/

	private static class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			ApplicationEvent event2 = (ApplicationEvent) event;
			int type = event2.getType();
			switch (type) {
				case ApplicationEvent.ABOUT:
					About.showAboutDialog(null);
					break;
				case ApplicationEvent.QUIT_APPLICATION:
					ProjectActions.doQuit();
					break;
				case ApplicationEvent.OPEN_DOCUMENT:
					Startup.doOpen(event2.getFile());
					break;
				case ApplicationEvent.PRINT_DOCUMENT:
					Startup.doPrint(event2.getFile());
					break;
				case ApplicationEvent.PREFERENCES:
					PreferencesFrame.showPreferences();
					break;
			}
		}
	}
}

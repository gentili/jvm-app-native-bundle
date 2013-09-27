package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import ca.mcpnet.demurrage.GameEngine.GameLauncher.GameLauncher.OSNotSupported;

public class VerifyInstallWorker extends SwingWorker<Object, String> {

	private GameLauncher _gl;
	private boolean _success;
	private boolean _doinstall;

	public VerifyInstallWorker(GameLauncher gl) {
		_gl = gl;
		_success = false;
		_doinstall = false;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		publish("Verifying local installation...\n");
		// Check for filesystem directory
		String appdir;
		try {
			appdir = GameLauncher.getAppDirectory();
		} catch (OSNotSupported e) {
			publish("Unsupported Operating System: "+e.getMessage());
			return null;
		}
		String gamedir = appdir + "/demurrage";
		File f = new File(gamedir);
		if (!f.exists() || !f.isDirectory()) {
			publish("Installation not found: "+gamedir+"\n");
			_doinstall = true;
			return null;			
		}
		publish("INCOMPLETE IMPLEMENTATION\n");
		return null;
	}

	@Override
	protected void process(List<String> loglines) {
		for (String logline : loglines) {
			_gl.appendToLog(logline);
		}
	}

	@Override
	protected void done() {
		if (_doinstall) {
			_gl.appendToLog("Current installation missing, invalid, or out of date\n");
			_gl.startInstallStep();;
			return;
		}
		if (!_success) {
			_gl.appendToLog(">> Installation verification failed <<\n");
			_gl.enableAndFocusLogin();
			return;
		}
		_gl.appendToLog("Installation verified\n");
	}	
}

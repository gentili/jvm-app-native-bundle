package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.SwingWorker;

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
		String gamedir = _gl.getClientDir();
		File gamedirf = new File(gamedir);
		if (!gamedirf.exists() || !gamedirf.isDirectory()) {
			publish("Installation not found: "+gamedir+"\n");
			_doinstall = true;
			return null;			
		}
		// Check the version file
		File versionf = new File(gamedir,"VERSION.TXT");
		if (!versionf.exists()) {
			publish("No version file\n");
			_doinstall = true;
			return null;
		}
		String version;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(versionf);
			version = GameLauncher.InputStreamToString(fis);
			version = version.trim();
		} catch (Exception e) {
			publish(GameLauncher.StringFromNetException(e)+"\n");
			_doinstall = true;
			return null;
		} finally {
			if (fis != null)
				fis.close();
		}
		if (!version.equals(_gl.getClientVersion())) {
			publish("Local version ["+version+"] != Server version ["+_gl.getClientVersion()+"]\n");
			_doinstall = true;
			return null;
		}
		_success = true;
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
		_gl.appendToLog("Installation verified: Client Version "+_gl.getClientVersion()+"\n");
		_gl.startLaunchStep();
	}	
}

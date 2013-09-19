package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.util.List;

import javax.swing.SwingWorker;

public class VerifyInstallWorker extends SwingWorker<Object, String> {

	private GameLauncher _gl;
	private boolean _success;

	public VerifyInstallWorker(GameLauncher gl) {
		_gl = gl;
		_success = false;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		publish("Verifying local installation...\n");
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
		if (!_success) {
			_gl.appendToLog(">> Installation verification failed <<\n");
			_gl.enableAndFocusLogin();
			return;
		}
		_gl.appendToLog("Installation verified\n");
	}	
}

package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.util.List;

import javax.swing.SwingWorker;

public class LaunchWorker extends SwingWorker<Object, String> {

	private GameLauncher _gl;
	private boolean _success;

	public LaunchWorker(GameLauncher gl) {
		_gl = gl;
		_success = false;
	}
	public void publish(String message) {
		super.publish(message);
	}
	@Override
	protected Object doInBackground() throws Exception {
		publish("Launching Game Client...\n");
		// Start package download
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
		if (!_success) {
			_gl.appendToLog(">> Launch failed <<\n");
		} else {
			_gl.appendToLog("GameClient exited\n");
		}
		_gl.enableAndFocusLogin();
	}	
}

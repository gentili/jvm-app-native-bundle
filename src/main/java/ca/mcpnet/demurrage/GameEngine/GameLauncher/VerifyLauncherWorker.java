package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import ca.mcpnet.demurrage.GameEngine.GameLauncher.GameLauncher.OSNotSupported;

public class VerifyLauncherWorker extends SwingWorker<Object, String> {
	
	private GameLauncher _gl;
	private boolean _success;
	private String _failureMessage;

	public VerifyLauncherWorker(GameLauncher gl) {
		_gl = gl;
		_failureMessage = null;
		_success = false;
	}

	@Override
	protected String doInBackground() throws Exception {
		publish("Verifying GameLauncher...\n");

		try {
			_gl.setClientDir();
		} catch (OSNotSupported e) {
			publish("Unsupported Operating System: "+e.getMessage()+"\n");
			_failureMessage = "*** Unsupported Operating System! ***\nThis game requires OSX, Linux, or Windows";
			return null;
		}

		String version;
		if (GameLauncher.VERSION.equals("DEV-SNAPSHOT")) {
			publish("Development GameLauncher detected\nskipping verification\n");
			_success = true;
			return null;
		}
		try {
			URL url = new URL("http://videogamez.ca/demurrage/GameLauncher/VERSION.TXT");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			String verstring = GameLauncher.InputStreamToString((InputStream) connection.getContent());
			version = verstring.trim();
			connection.disconnect();
		} catch (Exception e) {
			publish (GameLauncher.StringFromNetException(e)+"\n");
			_failureMessage = "*** Problem verifying GameLauncher! ***\nWorking Internet Connection Required";
			return null;
		}
		if (!GameLauncher.VERSION.equals(version)) {
			publish ("Version mismatch: "+GameLauncher.VERSION+" != "+version+"\n");
			_failureMessage = "*** GameLauncher out of date! ***\nPlease download the latest GameLauncher";
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
		if (!_success) {
			_gl.appendToLog(">> Verification failed <<\n");
			JOptionPane.showMessageDialog(_gl, _failureMessage, "GameLauncher Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			return;
		}
		_gl.appendToLog("GameLauncher verification successful\n");
		_gl.switchToNewsPane();
		_gl.enableAndFocusLogin();
	}	
}

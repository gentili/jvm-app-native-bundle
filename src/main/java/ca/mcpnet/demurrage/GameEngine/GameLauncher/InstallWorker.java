package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.SwingWorker;

public class InstallWorker extends SwingWorker<Object, String> {

	private GameLauncher _gl;
	private boolean _success;

	public InstallWorker(GameLauncher gl) {
		_gl = gl;
		_success = false;
	}
	public void publish(String message) {
		super.publish(message);
	}
	@Override
	protected Object doInBackground() throws Exception {
		publish("Downloading and Installing Game Client...\n");
		// Start package download
		try {
			URL url = new URL("http://videogamez.ca/demurrage/GameClient/"+_gl.getClientFilename());
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			publish("Downloading "+connection.getContentLengthLong()+" bytes...\n");
			InputStream in = (InputStream) connection.getContent();
			ZipUtils.extract(in, new File(GameLauncher.getAppDirectory()),this);
		} catch (Exception e) {
			publish(GameLauncher.StringFromNetException(e)+"\n");
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
			_gl.appendToLog(">> Installation failed <<\n");
			_gl.enableAndFocusLogin();
			return;
		}
		_gl.appendToLog("Installation complete\n");
		_gl.startVerifyInstallStep();
	}	
}

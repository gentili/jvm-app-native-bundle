package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.SwingWorker;

import ca.mcpnet.demurrage.GameEngine.GameLauncher.GameLauncher.OSNotSupported;

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
			/*
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				publish(ze.getName()+"\n");
			    zin.closeEntry(); // not sure whether this is necessary
			}
			zin.close();
			*/
		} catch (Exception e) {
			publish(GameLauncher.StringFromNetException(e)+"\n");
			return null;
		}
		String appdir;
		try {
			appdir = GameLauncher.getAppDirectory();
		} catch (OSNotSupported e) {
			publish("Unsupported Operating System: "+e.getMessage());
			return null;
		}
		/*
		String gamedir = appdir + "/demurrage";
		File f = new File(gamedir);
		if (!f.exists() || !f.isDirectory()) {
			publish("Installation not found: "+gamedir+"\n");
			return null;			
		}
		*/
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
	}	
}

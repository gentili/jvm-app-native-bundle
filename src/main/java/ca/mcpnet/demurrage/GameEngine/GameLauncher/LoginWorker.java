package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

import javax.swing.SwingWorker;

public class LoginWorker extends SwingWorker<Object, String> {
	
	private GameLauncher _gl;
	private String _user;
	private String _password;
	private boolean _success;

	public LoginWorker(GameLauncher gl,String user, String password) {
		_gl = gl;
		_user = user;
		_password = password;
		_success = false;
	}

	@Override
	protected String doInBackground() throws Exception {
		if (_user.isEmpty() || _password.isEmpty()) {
			publish ("Empty username or password!\n");
			return null;
		}
		publish("Validating credentials and retrieving client info...\n");
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (_user, _password.toCharArray());
		    }
		});
		try {
			URL url = new URL("http://videogamez.ca/demurrage/GameClient/VERSION.TXT");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			String verstring = GameLauncher.InputStreamToString((InputStream) connection.getContent());
			_gl.setClientVersion(verstring.trim());
			connection.disconnect();
			url = new URL("http://videogamez.ca/demurrage/GameClient/CURRENT.TXT");
			connection = (HttpURLConnection)url.openConnection();
			_gl.setClientFilename(GameLauncher.InputStreamToString((InputStream) connection.getContent()));
			connection.disconnect();
		} catch (Exception e) {
			publish (GameLauncher.StringFromNetException(e)+"\n");
			return null;
		}
		_gl.setCredentials(_user,_password);
		_gl.storeCredentials();
				
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
			_gl.appendToLog(">> Verification and Retrieval failed <<\n");
			_gl.enableAndFocusLogin();
			return;
		}
		_gl.appendToLog("Retrieval successful\n");
		_gl.startVerifyInstallStep();
	}	
}

package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
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
			_gl.setClientVersion(InputStreamToString((InputStream) connection.getContent()));
			connection.disconnect();
			url = new URL("http://videogamez.ca/demurrage/GameClient/CURRENT.TXT");
			connection = (HttpURLConnection)url.openConnection();
			_gl.setClientFilename(InputStreamToString((InputStream) connection.getContent()));
			connection.disconnect();
		} catch (FileNotFoundException e) {
			publish("Login credentials verified\nUnable to retrieve "+e.getMessage()+"\n");
			return null;
		} catch (UnknownHostException e) {
			publish("Unable to resolve hostname\n");
			return null;
		} catch (ConnectException e) {
			publish("Connection problem: "+e.getMessage()+"\n");
			return null;
		} catch (ProtocolException e) {
			publish("Invalid username or password\n");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		_gl.setCredentials(_user,_password);
				
		_success = true;
		return null;
	}
	
	public String InputStreamToString(InputStream is) throws IOException {
		int c;
		StringWriter sw = new StringWriter();
		while ((c = is.read()) != -1) {
			sw.append((char) c);
		}
		return sw.toString(); 
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

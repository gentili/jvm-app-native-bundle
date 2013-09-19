package ca.mcpnet.demurrage.GameEngine.GameLauncher;

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
		publish("Connecting to auth server...\n");
		publish("Verifying Login Credentials...\n");
		if (!_user.equals("test") ||
				!_password.equals("password")) {
			publish ("Invalid username or password\n");
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
			_gl.appendToLog(">> Login failed <<\n");
			_gl.enableAndFocusLogin();
			return;
		}
		_gl.appendToLog("Login successful\n");
		_gl.setCredentials(_user,_password);
		_gl.startVerifyInstallStep();
	}	
}

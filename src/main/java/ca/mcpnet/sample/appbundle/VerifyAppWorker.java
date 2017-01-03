package ca.mcpnet.sample.appbundle;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class VerifyAppWorker extends SwingWorker<Object, String> {
	
	private SampleApp _gl;
	private boolean _success;
	private String _failureMessage;

	public VerifyAppWorker(SampleApp gl) {
		_gl = gl;
		_failureMessage = null;
		_success = false;
	}

	@Override
	protected String doInBackground() throws Exception {
		publish("Verifying SampleApp...\n");

		String version;
		if (SampleApp.VERSION.equals("DEV-SNAPSHOT")) {
			publish("Development SampleApp detected\nskipping verification\n");
			_success = true;
			return null;
		}
		try {
			URL url = new URL("http://mcpnet.ca/apps/SampleApp/VERSION.TXT");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			String verstring = SampleApp.InputStreamToString((InputStream) connection.getContent());
			version = verstring.trim();
			connection.disconnect();
		} catch (Exception e) {
			publish (SampleApp.StringFromNetException(e)+"\n");
			_failureMessage = "*** Problem verifying SampleApp! ***\nWorking Internet Connection Required";
			return null;
		}
		if (!SampleApp.VERSION.equals(version)) {
			publish ("Version mismatch: "+SampleApp.VERSION+" != "+version+"\n");
			_failureMessage = "*** SampleApp out of date! ***\nPlease download the latest SampleApp";
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
			JOptionPane.showMessageDialog(_gl, _failureMessage, "SampleApp Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			return;
		}
		_gl.appendToLog("SampleApp verification successful\n");
		_gl.switchToNewsPane();
		_gl.enableAndFocusLogin();
	}	
}

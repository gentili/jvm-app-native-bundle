package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

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
		String natives = "-Djava.library.path=natives";
		//+GameLauncher.getAppDirectory()+"/"+GameLauncher.CLIENTDIR+"/natives/";
		// publish (natives+"\n");
		ProcessBuilder pb =
				   new ProcessBuilder("java",natives,"-jar","GameClient.jar");
		Map<String, String> env = pb.environment();
		env.clear();
		env.put("LAUNCHER", "true");
		env.put("USER", _gl.getUser());
		env.put("PASSWORD", _gl.getPassword());
		env.put("SERVERADDR", "127.0.0.1");
		env.put("SERVERPORT", "1234");
		/*
		for (Entry<String, String> itr : env.entrySet()) {
			String key = itr.getKey();
			String value = itr.getValue();
			publish(key+"="+value+"\n");
		}
		*/
		pb.directory(new File(_gl.getClientDir()));
		pb.redirectErrorStream(true);
		try {
			Process p = pb.start();
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
		    String line;
		    while ((line = br.readLine()) != null) {
		      publish(line+'\n');
		    }
		    if (p.waitFor() == 0) {
		    	_success = true;
		    }
		    publish ("GameClient exited with return code "+p.exitValue()+"\n");					    	
		} catch (Exception e) {
			publish(e.getClass().getName()+":"+e.getMessage()+"\n");
		}
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
			_gl.appendToLog(">> GameClient execution error <<\n");
		} else {
			_gl.appendToLog("GameClient exited\n");
		}
		_gl.enableAndFocusLogin();
	}	
}

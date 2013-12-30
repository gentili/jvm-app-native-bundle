package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;
import javax.xml.bind.DatatypeConverter;

import java.awt.*;              //for layout managers and more
import java.awt.event.*;        //for action events
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class GameLauncher extends JPanel
                             implements ActionListener {
	public static String VERSION;
	
	private static final long serialVersionUID = 1L;
    protected static final String userFieldString = "User";
    protected static final String passwordFieldString = "Password";
    protected static final String loginButtonString = "Login";

    private static final String logPaneID = "LOG";
	private static final String newsPaneID = "NEWS";

	public static final String CLIENTDIR = "demurrage";

	private JTextField userField;
	private JPasswordField passwordField;
	private JButton loginButton;

	private JEditorPane newsPane;
	private JTextArea logPane;
	private JPanel cardPane;
	
	private String _clientDir;
	private String _user;
	private String _password;
	private String _clientVersion;
	private String _clientFilename;

    public GameLauncher() {
		VERSION = GameLauncher.class.getPackage().getImplementationVersion();
		if (VERSION == null) {
			VERSION = "DEV-SNAPSHOT";
		}
		    	
    	setMinimumSize(new Dimension(640, 480));
        setLayout(new BorderLayout());

        //Create the user field
        JLabel userFieldLabel = new JLabel(userFieldString + ": ");
        userFieldLabel.setLabelFor(userField);
        
        userField = new JTextField(10);
        userField.setText("");
        userField.setActionCommand(userFieldString);
        userField.setEnabled(false);
        userField.addActionListener(this);

        //Create a password field.
        JLabel passwordFieldLabel = new JLabel(passwordFieldString + ": ");
        passwordFieldLabel.setLabelFor(passwordField);

        passwordField = new JPasswordField(10);
        passwordField.setText("");
        passwordField.setActionCommand(passwordFieldString);
        passwordField.setEnabled(false);
        passwordField.addActionListener(this);
        
        //Create the login button
        loginButton = new JButton(loginButtonString);
        loginButton.setActionCommand(loginButtonString);
        loginButton.registerKeyboardAction(
                loginButton.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                        JComponent.WHEN_FOCUSED);        
        loginButton.registerKeyboardAction(
                loginButton.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                        JComponent.WHEN_FOCUSED);
        loginButton.setEnabled(false);
        loginButton.addActionListener(this);

        //Load the logo
        ImagePanel logoImage = new ImagePanel("/ditheredgorzo.gif");
        
        //Lay out the text controls and the labels.
        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbaglayout = new GridBagLayout();
        textControlsPane.setLayout(gridbaglayout);

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 3;
        c.fill = GridBagConstraints.NONE;
        textControlsPane.add(logoImage,c);
        c.gridheight = 1;  // RESET

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        textControlsPane.add(userFieldLabel, c);

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.NONE;
        textControlsPane.add(userField, c);

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        textControlsPane.add(passwordFieldLabel, c);

        c.anchor = GridBagConstraints.EAST;
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        textControlsPane.add(passwordField, c);
        
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        textControlsPane.add(loginButton, c);
        
        textControlsPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        //Create an editor pane.
        newsPane = createEditorPane();
        JScrollPane editorScrollPane = new JScrollPane(newsPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.setPreferredSize(new Dimension(640, 480));

        // Create the log pane
        logPane = new JTextArea();
        logPane.setEditable(false);
        ((DefaultCaret) logPane.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        logPane.append("*** Demurrage GameLauncher "+VERSION+" ***\n");
        JScrollPane logScrollPane = new JScrollPane(logPane);
        logScrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        // Put it together        
        cardPane = new JPanel(new CardLayout());
        cardPane.add(logScrollPane,logPaneID);
        cardPane.add(editorScrollPane,newsPaneID);
        
        add(cardPane, BorderLayout.NORTH);
        add(textControlsPane, BorderLayout.SOUTH);        
    }
    
    private JEditorPane createEditorPane() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        DefaultCaret caret = (DefaultCaret) editorPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        java.net.URL helpURL = null;
		try {
			helpURL = new URL("http://www.videogamez.ca/demurrage/GameLauncher/news.html");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
        if (helpURL != null) {
            try {
                editorPane.setPage(helpURL);
            } catch (IOException e) {
                System.err.println("Attempted to read a bad URL: " + helpURL);
            }
        } else {
            System.err.println("Couldn't find file: TextSampleDemoHelp.html");
        }

        return editorPane;
    }

    public void actionPerformed(ActionEvent e) {
        if (userFieldString.equals(e.getActionCommand())) {
            passwordField.requestFocusInWindow();
        } else if (passwordFieldString.equals(e.getActionCommand())) {
        	loginButton.requestFocusInWindow();
        } else if (loginButtonString.equals(e.getActionCommand())) {
        	switchToLogPane();
        	// Disable input fields
        	loginButton.setEnabled(false);
        	passwordField.setEnabled(false);
        	userField.setEnabled(false);
        	appendToLog(">> Beginning startup sequence <<	\n");
        	startLoginStep();
        	/*
            try {
            	SimpleAttributeSet keyWord = new SimpleAttributeSet();
            	StyleConstants.setForeground(keyWord, Color.BLACK);
            	StyleConstants.setBackground(keyWord, Color.WHITE);
            	StyleConstants.setBold(keyWord, true);
            	// editorPane.setText("");
            	editorPane.setBackground(Color.WHITE);
				editorPane.getDocument().insertString(editorPane.getDocument().getLength(), "Hey there!\n", keyWord);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}            
        	*/
        }
    }

	////////////////////////////
    // Startup Sequence Steps
	////////////////////////////
    private void startVerifyLauncherStep() {
		new VerifyLauncherWorker(this).execute();
    }
    
	private void startLoginStep() {
		new LoginWorker(this, userField.getText(), new String(passwordField.getPassword())).execute();
	}
	
	public void startVerifyInstallStep() {
		new VerifyInstallWorker(this).execute();
	}
	
	public void startInstallStep() {
		new InstallWorker(this).execute();
	}
	
	public void startLaunchStep() {
		new LaunchWorker(this).execute();
	}

	////////////////////////////
	// Control methods
	////////////////////////////
	public void enableAndFocusLogin() {
    	// Disable input fields
    	loginButton.setEnabled(true);
    	passwordField.setEnabled(true);
    	userField.setEnabled(true);
    	userField.requestFocusInWindow();
	}
	
	private void switchToLogPane() {
		CardLayout cardLayout = (CardLayout) cardPane.getLayout();
		cardLayout.show(cardPane, logPaneID);
	}
	
	public void switchToNewsPane() {
		CardLayout cardLayout = (CardLayout) cardPane.getLayout();
		cardLayout.show(cardPane, newsPaneID);		
	}

	private void setDefaultFocus() {
		userField.requestFocusInWindow();
	}
	
	public void setClientDir() throws OSNotSupported {
		_clientDir = GameLauncher.getAppDirectory() + "/" + GameLauncher.CLIENTDIR;		
	}
	
	public String getClientDir() {
		return _clientDir;
	}

	public void setCredentials(String user, String password) {
		_user = user;
		_password = password;
	}
	
	public void loadCredentials() {
		Properties props = new Properties();
		FileInputStream in = null;
		try {
			File f = new File(_clientDir,"auth.prop");
			in = new FileInputStream(f);
			props.load(in);
			if (props.containsKey("auth1"))
				_user = decrypt(props.getProperty("auth1"));
			if (props.containsKey("auth2"))
				_password = decrypt(props.getProperty("auth2"));
			userField.setText(_user);
			passwordField.setText(_password);
		} catch (Exception e) {
			// Ignore, go forward with empty credentials
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// Ignore
				}
		}
	}
	
	public void storeCredentials() throws Exception {
		Properties props = new Properties();
		props.setProperty("auth1", encrypt(_user));
		props.setProperty("auth2", encrypt(_password));
		File f = new File(_clientDir,"auth.prop");
		OutputStream out = new FileOutputStream(f);
		props.store(out, " Demurrage secure auth storage");
		out.close();
	}
	
	public String getUser() {
		return _user;
	}
	
	public String getPassword() {
		return _password;
	}
	
	public void setClientVersion(String clientVersion) {
		_clientVersion = clientVersion;
	}
	
	public String getClientVersion() {
		return _clientVersion;
	}

	public void setClientFilename(String clientFilename) {
		_clientFilename = clientFilename;
	}
	
	public String getClientFilename() {
		return _clientFilename;
	}
	
	public void appendToLog(String logline) {
		logPane.append(logline);
	}
	////////////////////////////
	// Static Methods
	////////////////////////////
	public static class OSNotSupported extends Exception {
		public OSNotSupported(String OS) {
			super(OS);
		}

		private static final long serialVersionUID = 1L;
	}


	public static String getAppDirectory() throws OSNotSupported
	{
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			return System.getenv("APPDATA");
		else if (OS.contains("MAC"))
			return System.getProperty("user.home") + "/Library/Application Support";
		else if (OS.contains("NUX"))
			return System.getProperty("user.home") + "/local";
		throw new OSNotSupported(OS);
	}
	
	public static String StringFromNetException(Exception e) {
		if (e instanceof FileNotFoundException) {
			return ("Unable to retrieve "+e.getMessage());
		} else if (e instanceof UnknownHostException) {
			return ("Unable to resolve hostname");
		} else if  (e instanceof ConnectException) {
			return ("Connection problem: "+e.getMessage());
		} else if  (e instanceof ProtocolException) {
			return ("Invalid username or password");
		} else {
			StringBuilder sb = new StringBuilder();
			StackTraceElement[] st = e.getStackTrace();
			for (StackTraceElement se : st) {
				sb.append(se.toString()+"\n");
			}
			return sb.toString();
		}
	}
	
	public static String InputStreamToString(InputStream is) throws IOException {
		int c;
		StringWriter sw = new StringWriter();
		while ((c = is.read()) != -1) {
			sw.append((char) c);
		}
		return sw.toString(); 
	}

	private static String _strkey = "demurrage";
	private static byte[] _iv = { 0, 0, 0, 0, 0, 0, 0, 0 };
	
	public static String encrypt(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		IvParameterSpec ivspec = new IvParameterSpec(_iv);
		SecretKeySpec key = new SecretKeySpec(_strkey.getBytes("UTF8"),"Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
		
		return DatatypeConverter.printBase64Binary(cipher.doFinal(data.getBytes("UTF8")));
	}
	
	public static String decrypt(String data) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		IvParameterSpec ivspec = new IvParameterSpec(_iv);
		SecretKeySpec key = new SecretKeySpec(_strkey.getBytes("UTF8"),"Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
		
		return new String(cipher.doFinal(DatatypeConverter.parseBase64Binary(data)));
	}

	public static ImageIcon loadImageIcon(String path) {
		ImageIcon imageIcon;
        java.net.URL imgURL = GameLauncher.class.getResource(path);
        if (imgURL != null) {
            imageIcon = new ImageIcon(imgURL);
        } else {
        	throw new RuntimeException("Could not load :"+path);
        }
        return imageIcon;
	}
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("GameLauncher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        GameLauncher gl = new GameLauncher();
        frame.add(gl);

        //Display the window.
        frame.pack();
        gl.setDefaultFocus();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setIconImage(loadImageIcon("/stars.png").getImage());
        gl.startVerifyLauncherStep();
    }

	public static void main(String[] args) {
        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 //Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
            }
        });
    }

}

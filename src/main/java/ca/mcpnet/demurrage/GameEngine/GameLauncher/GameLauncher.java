package ca.mcpnet.demurrage.GameEngine.GameLauncher;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;

import java.awt.*;              //for layout managers and more
import java.awt.event.*;        //for action events
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;

public class GameLauncher extends JPanel
                             implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String logPaneID = "LOG";
	private static final String editorPaneID = "EDITOR";
    protected static final String userFieldString = "User";
    protected static final String passwordFieldString = "Password";
    protected static final String loginButtonString = "Login";

    private JEditorPane editorPane;
	private JTextField userField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JTextArea logPane;
	private JPanel cardPane;
	
	private String _user;
	private String _password;
	private String _clientVersion;
	private String _clientFilename;

    public GameLauncher() {
    	
    	setMinimumSize(new Dimension(640, 480));
        setLayout(new BorderLayout());

        //Create the user field
        JLabel userFieldLabel = new JLabel(userFieldString + ": ");
        userFieldLabel.setLabelFor(userField);
        
        userField = new JTextField(10);
        userField.setText("test");
        userField.setActionCommand(userFieldString);
        userField.addActionListener(this);

        //Create a password field.
        JLabel passwordFieldLabel = new JLabel(passwordFieldString + ": ");
        passwordFieldLabel.setLabelFor(passwordField);

        passwordField = new JPasswordField(10);
        passwordField.setText("test");
        passwordField.setActionCommand(passwordFieldString);
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
        editorPane = createEditorPane();
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.setPreferredSize(new Dimension(640, 480));

        // Create the log pane
        logPane = new JTextArea();
        logPane.setEditable(false);
        logPane.append("*** Demurrage GameLauncher "+getClass().getPackage().getImplementationVersion()+" ***\n");
        logPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        // Put it together        
        cardPane = new JPanel(new CardLayout());
        cardPane.add(editorScrollPane,editorPaneID);
        cardPane.add(logPane,logPaneID);
        
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
        	appendToLog("== Beginning startup sequence ==\n");
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
	private void startLoginStep() {
		new LoginWorker(this, userField.getText(), new String(passwordField.getPassword())).execute();
	}
	public void startVerifyInstallStep() {
		new VerifyInstallWorker(this).execute();
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

	private void setDefaultFocus() {
		userField.requestFocusInWindow();
	}

	public void setCredentials(String user, String password) {
		_user = user;
		_password = password;
	}
	
	public void setClientVersion(String clientVersion) {
		_clientVersion = clientVersion;
	}

	public void setClientFilename(String clientFilename) {
		_clientFilename = clientFilename;
	}
	
	public void appendToLog(String logline) {
		logPane.append(logline);
	}
	////////////////////////////
	// Static Methods
	////////////////////////////

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

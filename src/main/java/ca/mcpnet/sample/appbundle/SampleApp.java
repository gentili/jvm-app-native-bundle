package ca.mcpnet.sample.appbundle;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;              //for layout managers and more
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;        //for action events
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;

public class SampleApp extends JPanel
                             implements ActionListener {
	public static String VERSION;
	
	private static final long serialVersionUID = 1L;
    protected static final String okButtonString = "OK";

    private static final String logPaneID = "LOG";
	private static final String newsPaneID = "NEWS";

	private JButton okButton;

	private JEditorPane newsPane;
	private JTextArea logPane;
	private JPanel cardPane;
	
	private String _clientVersion;

    public SampleApp() {
		VERSION = SampleApp.class.getPackage().getImplementationVersion();
		if (VERSION == null) {
			VERSION = "DEV-SNAPSHOT";
		}
		    	
    	setMinimumSize(new Dimension(640, 480));
        setLayout(new BorderLayout());
        
        //Create the login button
        okButton = new JButton(okButtonString);
        okButton.setActionCommand(okButtonString);
        okButton.registerKeyboardAction(
                okButton.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                        JComponent.WHEN_FOCUSED);        
        okButton.registerKeyboardAction(
                okButton.getActionForKeyStroke(
                        KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true),
                        JComponent.WHEN_FOCUSED);
        okButton.setEnabled(false);
        okButton.addActionListener(this);

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

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        textControlsPane.add(okButton, c);
        
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
        logPane.append("*** Sample App with Bundled JRE and natives "+VERSION+" ***\n");
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
			helpURL = new URL("http://mcpnet.ca");
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
        if (okButtonString.equals(e.getActionCommand())) {
        	switchToLogPane();
        	// Disable input fields
        	okButton.setEnabled(false);
        	appendToLog(">> Finishing up <<\n");
        	JOptionPane.showMessageDialog(this, "You clicked the right button!", "SampleApp Finished", JOptionPane.OK_OPTION);
			System.exit(0);
        }
    }

	////////////////////////////
    // Startup Sequence Steps
	////////////////////////////
    private void startVerifyAppStep() {
		new VerifyAppWorker(this).execute();
    }
    
	////////////////////////////
	// Control methods
	////////////////////////////
	public void enableAndFocusLogin() {
    	// Disable input fields
    	okButton.setEnabled(true);
    	okButton.requestFocusInWindow();
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
		okButton.requestFocusInWindow();
	}
	
	public void setClientVersion(String clientVersion) {
		_clientVersion = clientVersion;
	}
	
	public String getClientVersion() {
		return _clientVersion;
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

	public static ImageIcon loadImageIcon(String path) {
		ImageIcon imageIcon;
        java.net.URL imgURL = SampleApp.class.getResource(path);
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
        JFrame frame = new JFrame("SampleApp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        SampleApp gl = new SampleApp();
        frame.add(gl);

        //Display the window.
        frame.pack();
        gl.setDefaultFocus();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setIconImage(loadImageIcon("/stars.png").getImage());
        gl.startVerifyAppStep();
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

package UI;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.KeyStroke;

import com.tulskiy.keymaster.common.Provider;

/**
 * This class create the icon in the system tray.
 * It also creates global shortcuts to be used with GUI in pair.
 * Global shortcuts can be used in both Window and MacIntosh.
 * 
 * Preconditions 1 : GUI class must implement HotKeyListener
 * and one override-method onHotKey(), which corresponds to 
 * the global shortcuts created in this class.
 * 
 * Preconditions 2 : GUI main class and Logic class must make sure that the 
 * exit method from any other platforms must only set the JFrame 
 * to be ICONIFIED, except this class. Only this class can execute the 
 * system exit. 
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */

public class Tray {
	private FlexiPlannerUI instance;
	private JFrame frame;
	
	/** Global Shortcuts **/
	private Provider keyShortCuts = null;
	private String openShortCut = "control O";
	private String closeShortCut = "control M";
	
	public Tray(FlexiPlannerUI instance, JFrame frame) {
		this.instance = instance;
		this.frame = frame;
		initializeShortCuts();
	}
	
	/**
	 * This method placed the application in the system tray.
	 * Preconditions: Closing application will only make JFrame 
	 * to be ICOGNIFIED so that it can run in the background.
	 */
	public void createSystemTray() {
		if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
		
		final SystemTray tray = SystemTray.getSystemTray();
		final PopupMenu popupMenu = new PopupMenu();
		Image icon = Toolkit.getDefaultToolkit().getImage("logo.png");
		final TrayIcon trayIcon = new TrayIcon(icon, "FlexiPlanner", popupMenu);
		trayIcon.setImageAutoSize(true);
		
		MenuItem item = new MenuItem("Open");
		item.setShortcut(new MenuShortcut(KeyEvent.VK_O, false));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(true);
				frame.setExtendedState(JFrame.NORMAL);
			}
		});
		popupMenu.add(item);
		popupMenu.addSeparator();
		item = new MenuItem("Exit");
		item.setShortcut(new MenuShortcut(KeyEvent.VK_E, false));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopShortCuts(); // reset shortcuts when exit.
				System.exit(0);
			}
		});
		popupMenu.add(item);
		
		try {
			tray.add(trayIcon);
		} catch (AWTException awtException) {
			awtException.printStackTrace();
		}
	} 
	
	/**
	 * This method initializes the global shortcuts.
	 */
	private void initializeShortCuts() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (keyShortCuts == null) {
					keyShortCuts = Provider.getCurrentProvider(false);
				}
				keyShortCuts.reset();
				keyShortCuts.register(KeyStroke.getKeyStroke(openShortCut), instance);
				keyShortCuts.register(KeyStroke.getKeyStroke(closeShortCut), instance);
			}
		}).start();
	}
	
	/**
	 * This method removes the shortcuts created.
	 */
	private void stopShortCuts() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (keyShortCuts != null) {
					keyShortCuts.reset();
					keyShortCuts.stop();
				}
			}
		}).start();
	}
}

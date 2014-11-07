package ui;

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

//@author A0117989H

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
 */

public class Tray {
	
	private FlexiPlannerUI instance;
	private JFrame frame;
	
	private final String ERROR_NOT_SUPPORTED = "SystemTray is not supported.\n";
	
	private final String TITLE = "FlexiPlanner";
	private final String PATH_ICON = "/resources/logo.png";
	private final String MENUITEM_OPEN = "Open";
	private final String MENUITEM_EXIT = "Exit";
	
	/** Global Shortcuts **/
	private static Provider keyShortCuts = null;
	
	private final String SHORTCUT_LAUNCH = "control O";
	private final String SHORTCUT_CLOSE = "control M";
	private final String SHORTCUT_EXIT = "control E";
	
	public Tray(FlexiPlannerUI instance) {
		this.instance = instance;
		this.frame = instance.getJFrame();
		initializeShortCuts();
	}
	
	/**
	 * This method placed the application in the system tray.
	 * Preconditions: Closing application will only make JFrame 
	 * to be ICOGNIFIED so that it can run in the background.
	 */
	public void createSystemTray() {
		if (!SystemTray.isSupported()) {
            report(ERROR_NOT_SUPPORTED);
            return;
        }
		
		Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource(PATH_ICON));
		
		final SystemTray tray = SystemTray.getSystemTray();
		final PopupMenu popupMenu = new PopupMenu();
		final TrayIcon trayIcon = new TrayIcon(icon, TITLE, popupMenu);
		trayIcon.setImageAutoSize(true);
		
		// add "Open" to tray menu
		MenuItem item = new MenuItem(MENUITEM_OPEN);
		item.setShortcut(new MenuShortcut(KeyEvent.VK_O, false));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexiPlannerUI.save();
				frame.setVisible(true);
				frame.setExtendedState(JFrame.NORMAL);
			}
		});
		popupMenu.add(item);
		popupMenu.addSeparator();
		
		// add "Exit" to tray menu
		item = new MenuItem(MENUITEM_EXIT);
		item.setShortcut(new MenuShortcut(KeyEvent.VK_E, false));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexiPlannerUI.save();
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
				keyShortCuts.register(KeyStroke.getKeyStroke(SHORTCUT_LAUNCH), instance);
				keyShortCuts.register(KeyStroke.getKeyStroke(SHORTCUT_CLOSE), instance);
				keyShortCuts.register(KeyStroke.getKeyStroke(SHORTCUT_EXIT), instance);
			}
		}).start();
	}
	
	/**
	 * This method removes the shortcuts created.
	 */
	public static void stopShortCuts() {
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
	
	private void report(final String toReport) {
		System.out.print(toReport);
	}
}
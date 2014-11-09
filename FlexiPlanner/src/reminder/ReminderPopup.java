package reminder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ui.FlexiPlannerUI;

//@author A0117989H

/**
 * This class display the reminder pop-up with an alarm sound.
 *
 */

public class ReminderPopup {
	
	private final String REMINDER_ICON_PATH = "/resources/reminder-icon.png";
	private final String SOUND_FILE_PATH = "/resources/reminder.wav";
	private final String NOTHING = "";
	private final String HTML_ST = "<html>";
	private final String HTML_EN = "</html>";
	private final String HTML_BR = "<br>";
	private final String REGEX_NEXTLINE = "\\n";
	
	private final int FRAME_WIDTH = 350;
	private final int FRAME_HEIGHT = 100;
	private final int HGAP = 20;
	private final int VGAP = 0;
	private final int COLOR_R = 135;
	private final int COLOR_G = 206;
	private final int COLOR_B = 250;
	private final int IMAGE_WIDTH = 50;
	private final int IMAGE_HEIGHT = 50;
	private final int CONTENT_FONT_SIZE = 18;
	private final int GAP_POPUPS = 110;
	
	private float[] hsbvals = {0, 0, 0};
	
	private static int[] identity = {1, 2, 3, 4, 5, 6};
	
	private int uniqueIdentifier = 0;
	
	
	public ReminderPopup() {
		allocatePopupLocation();
	}
	
	/**
	 * This method calls the JFrame to pop up with alarm sound.
	 * 
	 * @param content - String
	 */
	public void displayPopupWSound(String content) {
		displayPopup(content);
		playReminderSound();
	}
	
	private void displayPopup(String content) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		
		BorderLayout borderLayout = new BorderLayout(HGAP, VGAP);
		frame.getContentPane().setLayout(borderLayout);
		
		Color.RGBtoHSB(COLOR_R, COLOR_G, COLOR_B, hsbvals);
		frame.setBackground(Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]));
		frame.getContentPane().setBackground(Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]));
		
		JLabel iconLabel = new JLabel();
		ImageIcon icon = new ImageIcon(getClass().getResource(REMINDER_ICON_PATH));
		Image image = icon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT,  java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(image);
		iconLabel.setIcon(icon);
		iconLabel.setText(NOTHING);
		
		JLabel contentLabel;
		
		String[] lines = content.split(REGEX_NEXTLINE);
		
		if (lines.length == 3) {
			contentLabel = new JLabel(HTML_ST + lines[0].trim() + 
									  HTML_BR + lines[1].trim() + 
									  HTML_BR + lines[2].trim() + 
									  HTML_EN);
		}
		else if (lines.length == 2) {
			contentLabel = new JLabel(HTML_ST + lines[0].trim() + 
									  HTML_BR + lines[1].trim() + 
									  HTML_EN);
		}
		else {
			contentLabel = new JLabel(HTML_ST + lines[0].trim() + HTML_EN);
		}
		
		contentLabel.setFont(new Font(NOTHING, Font.PLAIN, CONTENT_FONT_SIZE));
		
		frame.getContentPane().add(iconLabel, BorderLayout.WEST);
		frame.getContentPane().add(contentLabel, BorderLayout.CENTER);
		
		frame.getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				FlexiPlannerUI.save();
				frame.dispose();
				identity[uniqueIdentifier - 1] = uniqueIdentifier;
			}
		});
		
		frame.setLocation((int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth() 
				- FRAME_WIDTH), 30 + (((uniqueIdentifier - 1) * GAP_POPUPS)));
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}
	
	private void playReminderSound() {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(SOUND_FILE_PATH));
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	//to pop-up from upper right corner of the screen until the lower right corner in order systematically
	private void allocatePopupLocation() {
		for (int i = 0; i < 6; i++) {
			if (identity[i] != 0) {
				uniqueIdentifier = identity[i];
				identity[i] = 0;
				break;
			}
			if (identity[i] == 0 && i == 5) {
				for (int j = 0; j < 6; j++) {
					identity[j] = j + 1;
				}
				uniqueIdentifier = 1;
				identity[0] = 0;
			}
		}
	}
}

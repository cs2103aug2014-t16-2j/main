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


public class ReminderPopup {
	
	private float[] hsbvals = {0, 0, 0};
	
	private static int[] identity = {1, 2, 3, 4, 5, 6};
	
	private int uniqueIdentifier = 0;
	
	
	public ReminderPopup() {
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

	public void reminderPopup(String content) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(350, 100);
		
		BorderLayout borderLayout = new BorderLayout(20, 0);
		frame.getContentPane().setLayout(borderLayout);
		
		Color.RGBtoHSB(135, 206, 250, hsbvals);
		frame.setBackground(Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]));
		frame.getContentPane().setBackground(Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]));
		
		JLabel iconLabel = new JLabel();
		ImageIcon icon = new ImageIcon("reminder-icon.png");
		Image image = icon.getImage().getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(image);
		iconLabel.setIcon(icon);
		iconLabel.setText("");
		
		JLabel contentLabel;
		
		String[] lines = content.split("\\n");
		
		if (lines.length == 3) {
			contentLabel = new JLabel("<html>" + lines[0].trim() + "<br>" + lines[1].trim() + "<br>"+ lines[2].trim() + "</html>");
		}
		else if (lines.length == 2) {
			contentLabel = new JLabel("<html>" + lines[0].trim() + "<br>" + lines[1].trim() + "</html>");
		}
		else {
			contentLabel = new JLabel("<html>" + lines[0].trim() + "</html>");
		}
		
		contentLabel.setFont(new Font("", Font.PLAIN, 18));
		
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
		
		frame.setLocation((int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth() - 350), 30 + (((uniqueIdentifier - 1) * 110)));
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);

		try {
			File soundFile = new File("reminder2.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
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
}

package storage;

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


public class ReminderPopup {
	
	private static int counter = 0;
	
	private float[] hsbvals = {0, 0, 0};

	public ReminderPopup(final String content) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(300, 100);
		
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
		
		JLabel contentLabel = new JLabel("<html>" + content + "</html>");
		contentLabel.setFont(new Font("", Font.PLAIN, 18));
		
		frame.getContentPane().add(iconLabel, BorderLayout.WEST);
		frame.getContentPane().add(contentLabel, BorderLayout.CENTER);
		
		frame.getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				frame.dispose();
				counter--;
			}
		});
		
		counter++;
		if (counter == 7) {
			counter = 1;
		}
		
		frame.setLocation((int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth() - 300), 30 + (((counter-1) * 110)));
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);

		try {
			File soundFile = new File("reminder.wav");
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

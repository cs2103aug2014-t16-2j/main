package UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Logic.*;

/**
 *
 * @author Eugene Tan Teck Li
 */
public class FlexiPlannerUI {
	private JLabel displayedMonth, displayedYear;
	private JButton prevMonth, nextMonth;
	private JTable calendar1;
	private DefaultTableModel calendar2;
	private JScrollPane calendarScroll;
	private JPanel schedulerPanel;
	private JComboBox selectYear;
	private JFrame schedulerFrame;
	private JTextField inputCommand;
	private int actualYear, actualMonth, actualDay, currentDisplayedYear,
			currentDisplayedMonth;
	private String[] months = { "January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November",
			"December" };
	private String day;
	
	private static UICall logic;

	public FlexiPlannerUI() {
		try {
			logic  = new UICall();
		} catch (FileNotFoundException e) {
			System.out.println("Error");
		} catch (IOException e) {
			System.out.println("Error");
		}
	}

	public void loadInterfaceandData() {
		JFrame.setDefaultLookAndFeelDecorated(false);// the frame is changed to
														// different style here
		schedulerFrame = new JFrame("FlexiPlanner");// create new frame named
													// *CALENDAR*
		schedulerFrame.setUndecorated(false);// the frame is changed
		schedulerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Program
																		// exits
																		// when
																		// closed
		schedulerFrame.setSize(700, 410);// set frame size
		schedulerFrame.setResizable(false);
		schedulerFrame.setVisible(true);

		calendar2 = new DefaultTableModel() {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};// get dafault table model for calendar
		calendar1 = new JTable(calendar2);// create new table
		calendarScroll = new JScrollPane(calendar1);
		schedulerPanel = new JPanel(null);// this is needed in order to show
											// table

		schedulerFrame.getContentPane().add(schedulerPanel);// add panel to
															// frame(add table
															// to frame)

		// get calendar format
		GregorianCalendar cal = new GregorianCalendar();
		actualDay = cal.get(GregorianCalendar.DAY_OF_MONTH);
		actualMonth = cal.get(GregorianCalendar.MONTH);
		actualYear = cal.get(GregorianCalendar.YEAR);
		currentDisplayedMonth = actualMonth;
		currentDisplayedYear = actualYear;

		displayedMonth = new JLabel("January");
		displayedYear = new JLabel("Select Year:");
		selectYear = new JComboBox();
		prevMonth = new JButton("<");
		nextMonth = new JButton(">");

		displayedMonth.setBounds(
				160 - displayedMonth.getPreferredSize().width / 2, 5, 100, 25);
		displayedMonth.setForeground(Color.red);
		displayedYear.setBounds(10, 295, 80, 20);
		displayedYear.setForeground(Color.red);
		selectYear.setBounds(230, 295, 80, 20);
		selectYear.setForeground(Color.red);
		prevMonth.setBounds(10, 5, 50, 25);
		prevMonth.setForeground(Color.red);
		nextMonth.setBounds(260, 5, 50, 25);
		nextMonth.setForeground(Color.red);
		calendarScroll.setBounds(10, 35, 300, 250);

		inputCommand = new JTextField();
		inputCommand.setBackground(Color.LIGHT_GRAY);
		inputCommand.setForeground(Color.RED);
		inputCommand.setBounds(10, 326, 674, 46);
		inputCommand.setColumns(10);
		inputCommand.setFont(new Font("Times New Roman", Font.BOLD, 20));

		final JTextArea showContent = new JTextArea();
		showContent.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showContent.setForeground(Color.BLACK);
		showContent.setBackground(Color.LIGHT_GRAY);
		showContent.setText(logic.getData());
		showContent.setEditable(false);
		showContent.setBounds(324, 5, 360, 280);

		final JLabel commandFeedback = new JLabel("");
		commandFeedback.setBackground(new Color(240, 240, 240));
		commandFeedback.setForeground(Color.RED);
		commandFeedback.setFont(new Font("Times New Roman", Font.BOLD, 15));
		commandFeedback.setBounds(334, 295, 350, 20);

		schedulerPanel.add(displayedMonth);
		schedulerPanel.add(displayedYear);
		schedulerPanel.add(selectYear);
		schedulerPanel.add(prevMonth);
		schedulerPanel.add(nextMonth);
		schedulerPanel.add(calendarScroll);
		schedulerPanel.add(inputCommand);
		schedulerPanel.add(showContent);
		schedulerPanel.add(commandFeedback);
		schedulerPanel.setBounds(2, 1, 400, 335);

		String[] headers = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" }; // All
																				// headers
		for (int i = 0; i < 7; i++) {
			calendar2.addColumn(headers[i]);// add it on top column(left to
											// right)
		}

		calendar1.getTableHeader().setResizingAllowed(false);
		calendar1.getTableHeader().setReorderingAllowed(false);
		calendar1.setColumnSelectionAllowed(true);// set cell selectable
		calendar1.setRowSelectionAllowed(true);// set cell selectable
		calendar1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		calendar1.setRowHeight(40);
		calendar2.setColumnCount(7);// set no of columns of calendar(inner)
		calendar2.setRowCount(6);// set no of rows of calendar(inner)
		setValuesCombox();// combo box for selectYear

		refreshCalendar(actualMonth, actualYear); // Refresh calendar

		prevMonth.addActionListener(new Prev_Action());
		nextMonth.addActionListener(new Next_Action());
		selectYear.addActionListener(new Years_Action());
		inputCommand.requestFocusInWindow();
		executeKeyAction(commandFeedback,showContent);
	}

	private void executeKeyAction(final JLabel commandFeedback,final JTextArea showContent) {
		inputCommand.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					Toolkit.getDefaultToolkit().beep();
					String userCommand = inputCommand.getText();
					commandFeedback.setText(logic
							.executeInputCommand(userCommand)[0]);
					inputCommand.setText("");					
					if(userCommand.toLowerCase().startsWith("search")){
						showContent.setText("Show Search contents");
					}else{
						showContent.setText(logic.getData());
					}
					refreshCalendar(actualMonth, actualYear);
				}
			}

			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					;
				} else {
					commandFeedback.setText(inputCommand.getText());
				}
			}
		});
	}

	private void setValuesCombox() {
		for (int i = actualYear; i <= actualYear + 20; i++) {
			selectYear.addItem(String.valueOf(i));
		}
	}

	private void prevMth() {
		if (currentDisplayedMonth == 0) { // Back one year
			currentDisplayedMonth = 11;
			currentDisplayedYear -= 1;
		} else { // Back one month
			currentDisplayedMonth -= 1;
		}
		refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
	}

	private void nextMth() {
		if (currentDisplayedMonth == 11) { // Foward one year
			currentDisplayedMonth = 0;
			currentDisplayedYear += 1;
		} else { // Foward one month
			currentDisplayedMonth += 1;
		}
		refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
	}

	private void refreshCalendar(int month, int year) {

		int nod, som; // Number Of Days, Start Of Month
		prevMonth.setEnabled(true);// enable button
		nextMonth.setEnabled(true);// enable button
		if (month == 0 && year == actualYear) {
			prevMonth.setEnabled(false);
		} // disable button as already pass
		if (month == 11 && year >= actualYear + 20) {
			nextMonth.setEnabled(false);
		} // disable button as out of range
		displayedMonth.setText(months[month]); // Refresh the month label (at
												// the top)
		displayedMonth.setBounds(
				160 - displayedMonth.getPreferredSize().width / 2, 5, 180, 25); // Re-align
																				// label
																				// with
																				// calendar
		selectYear.setSelectedItem(String.valueOf(year)); // Select the correct
															// year in the combo
															// box

		// Clear table
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				calendar2.setValueAt(null, i, j);
			}
		}

		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		som = cal.get(GregorianCalendar.DAY_OF_WEEK);

		for (int i = 1; i <= nod; i++) {
			int row = new Integer((i + som - 2) / 7);
			int column = (i + som - 2) % 7;
			calendar2.setValueAt(i, row, column);
		}// set value for the days displayed

		calendar1.setDefaultRenderer(calendar1.getColumnClass(0),
				new Calendar1Renderer());// using Calendar1Renderer class to set
											// table display
	}// end of refreshCalendar method

	// class Calendar1Renderer used for editting how things are displayed
	class Calendar1Renderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, selected,
					focused, row, column);
			if (column == 0 || column == 6) { // Week-end
				setBackground(Color.MAGENTA);

			} else { // Weekday
				setBackground(Color.WHITE);
			}

			if (value != null) {
				String date = Integer.parseInt(value.toString()) + " "
						+ months[currentDisplayedMonth] + " "
						+ currentDisplayedYear;
				if (Integer.parseInt(value.toString()) == actualDay
						&& currentDisplayedMonth == actualMonth
						&& currentDisplayedYear == actualYear) { // Today
					if (logic.hasTask(date)) {
						setBackground(Color.RED);// set colour for current day
													// with task
					} else {
						setBackground(Color.LIGHT_GRAY);// set colour current
														// day
					}
				} else if (logic.hasTask(date)) {
					setBackground(Color.ORANGE);// set colour for days with task
				}

			}

			setBorder(null);
			return this;
		}
	}// end of class Calendar1Renderer

	// Prev_Action points to the previous year
	class Prev_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentDisplayedMonth == 0) { // Back one year
				currentDisplayedMonth = 11;
				currentDisplayedYear -= 1;
			} else { // Back one month
				currentDisplayedMonth -= 1;
			}
			refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
		}
	}// end of class Prev_Action
		// Next_Action points to the next year

	class Next_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (currentDisplayedMonth == 11) { // Foward one year
				currentDisplayedMonth = 0;
				currentDisplayedYear += 1;
			} else { // Foward one month
				currentDisplayedMonth += 1;
			}
			refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
		}
	}// end of class Next_Action

	class Years_Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (selectYear.getSelectedItem() != null) {
				String b = selectYear.getSelectedItem().toString();
				currentDisplayedYear = Integer.parseInt(b);
				refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
			}
		}
	}// end of class Years_Action
}// end of class FlexiPlannerUI
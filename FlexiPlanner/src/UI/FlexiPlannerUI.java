package UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
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
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.json.simple.parser.ParseException;

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
	private Border border;
	private JTextArea overDueTaskLabel;
	private JTextArea showOverDueTask;
	private JTextArea todayTasksLabel;
	private JTextArea showTodayTask;
	private JTextArea showUserExecutedCommand;
	private JScrollPane calendarScroll;
	private JScrollPane showTodayTaskScroll;
	private JScrollPane showOverDueTaskScroll;
	private JScrollPane showUserExecutedCommandScroll;
	private JLabel commandFeedback;
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadInterfaceandData() throws IOException, ParseException {
		JFrame.setDefaultLookAndFeelDecorated(false);// the frame is changed to
		// different style here
		schedulerFrame = new JFrame("FlexiPlanner");// create new frame named
		// *CALENDAR*
		schedulerFrame.setUndecorated(false);// the frame is changed
		schedulerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Program
		// exits
		// when
		// closed

		schedulerFrame.setSize(750, 410);// set frame size
		schedulerFrame.setResizable(false);
		schedulerFrame.setLocationRelativeTo(null);
		schedulerFrame.setVisible(true);

		calendar2 = new DefaultTableModel() {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};// get dafault table model for calendar
		calendar1 = new JTable(calendar2);// create new table
		calendarScroll = new JScrollPane(calendar1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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

		border = BorderFactory.createLineBorder(Color.BLACK);
		overDueTaskLabel = new JTextArea();
		overDueTaskLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		overDueTaskLabel.setBackground(Color.LIGHT_GRAY);
		overDueTaskLabel.setForeground(Color.BLACK);		
		overDueTaskLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		overDueTaskLabel.setText("Overdue tasks");

		showOverDueTask = new JTextArea();
		showOverDueTask.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showOverDueTask.setForeground(Color.BLACK);
		showOverDueTask.setBackground(Color.LIGHT_GRAY);
		showOverDueTask.setText("Get from logic to show overdue tasks");
		showOverDueTask.setEditable(false);

		showOverDueTaskScroll = new JScrollPane (showOverDueTask, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showOverDueTaskScroll.setRowHeaderView(overDueTaskLabel);
		showOverDueTaskScroll.setBounds(320, 4, 410, 50);

		todayTasksLabel = new JTextArea();
		todayTasksLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		todayTasksLabel.setBackground(Color.LIGHT_GRAY);
		todayTasksLabel.setForeground(Color.BLACK);
		todayTasksLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		todayTasksLabel.setText("Today tasks");

		showTodayTask = new JTextArea();
		showTodayTask.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showTodayTask.setForeground(Color.BLACK);
		showTodayTask.setBackground(Color.LIGHT_GRAY);
		showTodayTask.setText("Get from logic to show today tasks");
		showTodayTask.setEditable(false);

		showTodayTaskScroll = new JScrollPane (showTodayTask, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showTodayTaskScroll.setRowHeaderView(todayTasksLabel);
		showTodayTaskScroll.setBounds(320, 62, 410, 50);

		showUserExecutedCommand = new JTextArea();
		showUserExecutedCommand.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showUserExecutedCommand.setForeground(Color.BLACK);
		showUserExecutedCommand.setBackground(Color.LIGHT_GRAY);
		showUserExecutedCommand.setText(logic.getData(""));
		showUserExecutedCommand.setEditable(false);

		showUserExecutedCommandScroll = new JScrollPane (showUserExecutedCommand, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showUserExecutedCommandScroll.setBounds(320, 120, 410, 170);		

		commandFeedback = new JLabel("");
		commandFeedback.setBackground(new Color(240, 240, 240));
		commandFeedback.setForeground(Color.RED);
		commandFeedback.setFont(new Font("Times New Roman", Font.BOLD, 15));
		commandFeedback.setBounds(334, 295, 350, 20);

		inputCommand = new JTextField();
		inputCommand.setBackground(Color.LIGHT_GRAY);
		inputCommand.setForeground(Color.RED);
		inputCommand.setBounds(10, 326, 724, 46);
		inputCommand.setColumns(10);
		inputCommand.setFont(new Font("Times New Roman", Font.BOLD, 20));

		schedulerPanel.add(displayedMonth);
		schedulerPanel.add(displayedYear);
		schedulerPanel.add(selectYear);
		schedulerPanel.add(prevMonth);
		schedulerPanel.add(nextMonth);
		schedulerPanel.add(calendarScroll);
		schedulerPanel.add(showOverDueTaskScroll);
		schedulerPanel.add(showTodayTaskScroll);
		schedulerPanel.add(showUserExecutedCommandScroll);
		schedulerPanel.add(commandFeedback);
		schedulerPanel.add(inputCommand);
		schedulerPanel.setBounds(2, 1, 400, 335);
		String[] headers = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" }; // All
		// headers
		for (int i = 0; i < 7; i++) {
			calendar2.addColumn(headers[i]);// add it on top column(left to
			// right)
		}

		calendar1.getTableHeader().setResizingAllowed(false);
		calendar1.getTableHeader().setReorderingAllowed(false);
		calendar1.setColumnSelectionAllowed(false);
		calendar1.setRowSelectionAllowed(false);
		calendar1.setRowHeight(40);
		calendar2.setColumnCount(7);// set no of columns of calendar(inner)
		calendar2.setRowCount(6);// set no of rows of calendar(inner)
		setValuesCombox();// combo box for selectYear

		refreshCalendar(actualMonth, actualYear); // Refresh calendar

		prevMonth.addActionListener(new Prev_Action());
		nextMonth.addActionListener(new Next_Action());
		selectYear.addActionListener(new Years_Action());
		inputCommand.requestFocusInWindow();
		executeKeyAction(commandFeedback,showOverDueTask,showTodayTask,showUserExecutedCommand);
	}

	private void executeKeyAction(final JLabel commandFeedback,final JTextArea showOverDueTask,
			final JTextArea showTodayTask,final JTextArea showUserExecutedCommand) {
		inputCommand.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				switch (key){
				case KeyEvent.VK_ENTER: 
					String userCommand = inputCommand.getText();
					try {
						commandFeedback.setText(logic
								.executeInputCommand(userCommand)[0]);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					inputCommand.setText("");
					showOverDueTask.setText("Get Over due Task from logic");
					showTodayTask.setText("Get today task from logic");
					try {
						showUserExecutedCommand.setText(logic.getData(userCommand));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					refreshCalendar(actualMonth, actualYear);
					break;
				case KeyEvent.VK_F1:
					showUserExecutedCommand.setText("Guide\nFirst just relax\nSecond quit doing this shit");
					break;
				case KeyEvent.VK_F2:
					try {
						showUserExecutedCommand.setText(logic.getData(""));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					break;
				case KeyEvent.VK_F3:
					showOverDueTask.setText("Get Over due Task from logic");
					showTodayTask.setText("Get today task from logic");
					showUserExecutedCommand.setText("So some Undo shit");
					refreshCalendar(actualMonth, actualYear);
					break;
				case KeyEvent.VK_F4:
					showOverDueTask.setText("Get Over due Task from logic");
					showTodayTask.setText("Get today task from logic");
					showUserExecutedCommand.setText("So some Redo shit");
					refreshCalendar(actualMonth, actualYear);
					break;
				case KeyEvent.VK_PAGE_UP:
					if (currentDisplayedMonth  == 0 && currentDisplayedYear == actualYear) {
						break;
					}
					prevMth();
					break;
				case KeyEvent.VK_PAGE_DOWN:
					if (currentDisplayedMonth == 11 && currentDisplayedYear >= actualYear + 20) {
						break;
					} 
					nextMth();
					break;
				default: 
					break;
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
					try {
						if (logic.hasTask(date)) {
							setBackground(Color.RED);// set colour for current day
							// with task
						} else {
							setBackground(Color.LIGHT_GRAY);// set colour current
							// day
						}
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}setBackground(Color.LIGHT_GRAY);
				} else
					try {
						if (logic.hasTask(date)) {
							setBackground(Color.ORANGE);// set colour for days with task
						}
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
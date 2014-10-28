package UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.json.simple.parser.ParseException;
import org.jdesktop.swingx.JXCollapsiblePane;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;

import Logic.*;
//import com.apple.eawt.Application;

/**
 *
 * @author Eugene Tan Teck Li(A0111770R)
 */
public class FlexiPlannerUI implements HotKeyListener {
	private JLabel displayedMonth, displayedYear;
	private JLabel showUserGuideLabel;
	private JLabel todayTasksLabel;
	private JLabel overDueTaskLabel;
	private JLabel showUserExecutedCommandLabel;
	private JLabel showCategoryLabel;
	private JPanel schedulerPanel;
	private JButton prevMonth, nextMonth;
	private JTable calendar1;
	private DefaultTableModel calendar2;
	private Border border;
	private JXCollapsiblePane showUserGuidePane;
	private JXCollapsiblePane showOverDueCollapsePane;
	private JXCollapsiblePane todayCollapsePane;
	private JXCollapsiblePane showUserExecutedCommandCollapsePane;
	private JTextArea showOverDueTask;
	private JTextArea showTodayTask;
	private JTextArea showUserExecutedCommand;
	private JTextArea commandFeedback;
	private JTextArea showCategory;
	private JTextArea showUserGuide;
	private JScrollPane calendarScroll;
	private JScrollPane showUserGuideScroll;
	private JScrollPane showTodayTaskScroll;
	private JScrollPane showOverDueTaskScroll;
	private JScrollPane showUserExecutedCommandScroll;
	private JScrollPane showCategoryScroll;
	private JComboBox selectYear;
	private static JFrame schedulerFrame;
	private JTextField inputCommand;
	private int actualYear, actualMonth, actualDay, currentDisplayedYear,
	currentDisplayedMonth;
	private String[] months = { "January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November",
	"December" };
	private String day;

	private static Logic logic;

	public FlexiPlannerUI() {
		try {
			logic  = new Logic();
			schedulerFrame = new JFrame("FlexiPlanner");
		} catch (FileNotFoundException e) {
			System.out.println("Error");
		} catch (IOException e) {
			System.out.println("Error");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void loadInterfaceandData() throws IOException, ParseException {
		JFrame.setDefaultLookAndFeelDecorated(false);
		schedulerFrame.setUndecorated(false);// the frame is changed
		/** ICONIFIED so as to still run the application although close button is pressed. **/
		schedulerFrame.setDefaultCloseOperation(JFrame.ICONIFIED);
		ImageIcon img = new ImageIcon("logo.png");
		schedulerFrame.setIconImage(img.getImage());
		//Application.getApplication().setDockIconImage(new ImageIcon("logo.png").getImage());
		schedulerFrame.setSize(900, 620);// set frame size
		schedulerFrame.setResizable(false);
		schedulerFrame.setLocationRelativeTo(null);
		schedulerFrame.setVisible(true);

		calendar2 = new DefaultTableModel() {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};// get default table model for calendar
		calendar1 = new JTable(calendar2);// create new table
		calendarScroll = new JScrollPane(calendar1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		schedulerPanel = new JPanel(null);
		
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
		displayedMonth.setBounds(
				160 - displayedMonth.getPreferredSize().width / 2, 5, 100, 25);
		displayedMonth.setForeground(Color.BLUE);
		displayedMonth.setFont(new Font("Times New Roman", Font.BOLD, 15));

		displayedYear = new JLabel("Select Year:");
		displayedYear.setBounds(10, 295, 80, 20);
		displayedYear.setForeground(Color.BLUE);

		selectYear = new JComboBox();
		selectYear.setBounds(230, 295, 80, 20);
		selectYear.setForeground(Color.BLUE);

		prevMonth = new JButton("<");
		prevMonth.setBounds(10, 5, 50, 25);
		prevMonth.setForeground(Color.BLUE);

		nextMonth = new JButton(">");
		nextMonth.setBounds(260, 5, 50, 25);
		nextMonth.setForeground(Color.BLUE);

		calendarScroll.setBounds(10, 35, 300, 250);

		border = BorderFactory.createLineBorder(Color.BLACK);

		showUserGuideLabel = new JLabel();
		showUserGuideLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showUserGuideLabel.setForeground(Color.BLUE);
		showUserGuideLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		showUserGuideLabel.setText("Guide");

		showUserGuide = new JTextArea();
		showUserGuide.setFont(new Font("Times New Roman", Font.BOLD, 14));
		showUserGuide.setForeground(Color.CYAN);
		showUserGuide.setBackground(Color.BLUE);
		showUserGuide.setText(getGuide());
		showUserGuide.setLineWrap(true);
		showUserGuide.setEditable(false);

		showUserGuideScroll = new JScrollPane (showUserGuide, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showUserGuideScroll.setColumnHeaderView(showUserGuideLabel);

		showUserGuidePane = new JXCollapsiblePane();
		showUserGuidePane.setContentPane(showUserGuideScroll);
		showUserGuidePane.setCollapsed(true);
		showUserGuidePane.setBounds(320, 4, 570, 0);
		showUserGuidePane.setPreferredSize(new Dimension(570,495));			
		showUserGuidePane.setCollapsed(false);

		overDueTaskLabel = new JLabel();
		overDueTaskLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		overDueTaskLabel.setForeground(Color.RED);
		overDueTaskLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		overDueTaskLabel.setText("Overdue tasks");

		showOverDueTask = new JTextArea();
		showOverDueTask.setFont(new Font("Times New Roman", Font.BOLD, 14));
		showOverDueTask.setForeground(Color.WHITE);
		showOverDueTask.setBackground(Color.RED);
		showOverDueTask.setLineWrap(true);
		showOverDueTask.setText(logic.getOverdue());
		showOverDueTask.setEditable(false);

		showOverDueTaskScroll = new JScrollPane (showOverDueTask, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showOverDueTaskScroll.setColumnHeaderView(overDueTaskLabel);

		showOverDueCollapsePane = new JXCollapsiblePane();
		showOverDueCollapsePane.setContentPane(showOverDueTaskScroll);
		showOverDueCollapsePane.setCollapsed(true);
		showOverDueCollapsePane.setBounds(320, 4, 570, 0);
		showOverDueCollapsePane.setPreferredSize(new Dimension(570,495));

		todayTasksLabel = new JLabel();
		todayTasksLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		todayTasksLabel.setForeground(Color.MAGENTA);
		todayTasksLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		todayTasksLabel.setText("Today tasks");

		showTodayTask = new JTextArea();
		showTodayTask.setFont(new Font("Times New Roman", Font.BOLD, 14));
		showTodayTask.setForeground(Color.WHITE);
		showTodayTask.setBackground(Color.MAGENTA);
		showTodayTask.setLineWrap(true);
		showTodayTask.setText(logic.getTodayTask());
		showTodayTask.setEditable(false);

		showTodayTaskScroll = new JScrollPane (showTodayTask, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showTodayTaskScroll.setColumnHeaderView(todayTasksLabel);

		todayCollapsePane = new JXCollapsiblePane();
		todayCollapsePane.setContentPane(showTodayTaskScroll);
		todayCollapsePane.setCollapsed(true);
		todayCollapsePane.setBounds(320, 4, 570, 0);
		todayCollapsePane.setPreferredSize(new Dimension(570,495));

		showUserExecutedCommandLabel = new JLabel();
		showUserExecutedCommandLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showUserExecutedCommandLabel.setForeground(Color.BLUE);
		showUserExecutedCommandLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		showUserExecutedCommandLabel.setText("Executed Commands");

		showUserExecutedCommand = new JTextArea();
		showUserExecutedCommand.setFont(new Font("Times New Roman", Font.BOLD, 14));
		showUserExecutedCommand.setForeground(Color.CYAN);
		showUserExecutedCommand.setBackground(Color.BLUE);
		showUserExecutedCommand.setText("");
		showUserExecutedCommand.setLineWrap(true);
		showUserExecutedCommand.setEditable(false);

		showUserExecutedCommandScroll = new JScrollPane (showUserExecutedCommand, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showUserExecutedCommandScroll.setColumnHeaderView(showUserExecutedCommandLabel);

		showUserExecutedCommandCollapsePane = new JXCollapsiblePane();
		showUserExecutedCommandCollapsePane.setContentPane(showUserExecutedCommandScroll);
		showUserExecutedCommandCollapsePane.setCollapsed(true);
		showUserExecutedCommandCollapsePane.setBounds(320, 4, 570, 0);
		showUserExecutedCommandCollapsePane.setPreferredSize(new Dimension(570,495));			

		showCategoryLabel = new JLabel();
		showCategoryLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showCategoryLabel.setForeground(Color.BLUE);
		showCategoryLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		showCategoryLabel.setText("Categories");

		showCategory = new JTextArea();
		showCategory.setFont(new Font("Times New Roman", Font.BOLD, 14));
		showCategory.setForeground(Color.CYAN);
		showCategory.setBackground(Color.BLUE);
		showCategory.setLineWrap(true);
		showCategory.setText("Get category to display from logic");
		showCategory.setEditable(false);

		showCategoryScroll = new JScrollPane (showCategory, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showCategoryScroll.setColumnHeaderView(showCategoryLabel);
		showCategoryScroll.setBounds(10, 325, 300, 175);

		commandFeedback = new JTextArea("");
		commandFeedback.setBackground(new Color(240, 240, 240));
		commandFeedback.setForeground(Color.RED);
		commandFeedback.setFont(new Font("Times New Roman", Font.BOLD, 15));
		commandFeedback.setLineWrap(true);
		commandFeedback.setBounds(11, 500, 870, 40);

		inputCommand = new JTextField();
		inputCommand.setForeground(Color.BLUE);
		inputCommand.setBackground(Color.WHITE);
		inputCommand.setFont(new Font("Times New Roman", Font.BOLD, 20));
		inputCommand.setBounds(10, 540, 880, 46);

		schedulerPanel.add(displayedMonth);
		schedulerPanel.add(displayedYear);
		schedulerPanel.add(selectYear);
		schedulerPanel.add(prevMonth);
		schedulerPanel.add(nextMonth);
		schedulerPanel.add(calendarScroll);
		schedulerPanel.add(showUserGuidePane);
		schedulerPanel.add(showOverDueCollapsePane);
		schedulerPanel.add(todayCollapsePane);
		schedulerPanel.add(showUserExecutedCommandCollapsePane);
		schedulerPanel.add(showCategoryScroll);
		schedulerPanel.add(commandFeedback);
		schedulerPanel.add(inputCommand);

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
		executeKeyAction(commandFeedback,showOverDueTask,showTodayTask,showUserExecutedCommand,showCategory,
				showOverDueTaskScroll,showTodayTaskScroll,showUserExecutedCommandScroll,showCategoryScroll
				,showUserGuidePane,showOverDueCollapsePane,todayCollapsePane,showUserExecutedCommandCollapsePane);
	}

	private void executeKeyAction(final JTextArea commandFeedback,final JTextArea showOverDueTask,
			final JTextArea showTodayTask,final JTextArea showUserExecutedCommand,final JTextArea showCategory, 
			final JScrollPane showOverDueTaskScroll, final JScrollPane showTodayTaskScroll ,final JScrollPane showUserExecutedCommandScroll,final JScrollPane showCategoryScroll
			,final JXCollapsiblePane showUserGuidePane,final JXCollapsiblePane overDueCollapsePane,final JXCollapsiblePane todayCollapsePane,final JXCollapsiblePane showUserExecutedCommandCollapsePane) {
		inputCommand.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				int overDueScrollPane = showOverDueTaskScroll.getVerticalScrollBar().getModel().getValue();
				int valueTodayScrollPane = showTodayTaskScroll.getVerticalScrollBar().getModel().getValue();
				int valueCustomTextArea=showUserExecutedCommandScroll.getVerticalScrollBar().getModel().getValue();
				int valueCategoryScrollPane = showCategoryScroll.getVerticalScrollBar().getModel().getValue();
				switch (key){
				case KeyEvent.VK_ENTER: 
					String userCommand = inputCommand.getText();
					try {
						commandFeedback.setText(logic
								.executeInputCommand(userCommand));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					inputCommand.setText("");
					showCategory.setText("Update category\n");
					try {
						showUserExecutedCommand.setText(logic.getData(userCommand));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					showUserGuidePane.setCollapsed(true);
					overDueCollapsePane.setCollapsed(true);
					todayCollapsePane.setCollapsed(true);
					showUserExecutedCommandCollapsePane.setCollapsed(true);
					showUserExecutedCommandCollapsePane.setCollapsed(false);
					refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
					break;
				case KeyEvent.VK_F1:
					showUserGuidePane.setCollapsed(false);						
					showUserExecutedCommandCollapsePane.setCollapsed(true);
					overDueCollapsePane.setCollapsed(true);
					todayCollapsePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F2:
					userCommand = inputCommand.getText();
					try {
						showUserExecutedCommand.setText(logic.getData(userCommand));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					showUserExecutedCommandCollapsePane.setCollapsed(false);
					showUserGuidePane.setCollapsed(true);
					todayCollapsePane.setCollapsed(true);
					overDueCollapsePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F3:
					showOverDueTask.setText(logic.getOverdue());
					overDueCollapsePane.setCollapsed(false);
					showUserExecutedCommandCollapsePane.setCollapsed(true);
					todayCollapsePane.setCollapsed(true);
					showUserGuidePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F4:
					try {
						showTodayTask.setText(logic.getTodayTask());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					todayCollapsePane.setCollapsed(false);
					showUserExecutedCommandCollapsePane.setCollapsed(true);
					overDueCollapsePane.setCollapsed(true);
					showUserGuidePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F5:
					showOverDueTaskScroll.getVerticalScrollBar().getModel().setValue(overDueScrollPane-5);
					break;
				case KeyEvent.VK_F6:
					showOverDueTaskScroll.getVerticalScrollBar().getModel().setValue(overDueScrollPane+5);
					break;
				case KeyEvent.VK_F7:
					showTodayTaskScroll.getVerticalScrollBar().getModel().setValue(valueTodayScrollPane-5);
					break;
				case KeyEvent.VK_F8:
					showTodayTaskScroll.getVerticalScrollBar().getModel().setValue(valueTodayScrollPane+5);
					break;
				case KeyEvent.VK_F9:
					showUserExecutedCommandScroll.getVerticalScrollBar().getModel().setValue(valueCustomTextArea-5);
					break;					
				case KeyEvent.VK_F10:
					showUserExecutedCommandScroll.getVerticalScrollBar().getModel().setValue(valueCustomTextArea+5);
					break;
				case KeyEvent.VK_F11:
					showCategoryScroll.getVerticalScrollBar().getModel().setValue(valueCategoryScrollPane-5);
					break;					
				case KeyEvent.VK_F12:
					showCategoryScroll.getVerticalScrollBar().getModel().setValue(valueCategoryScrollPane+5);
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
	private String getGuide(){
		String guide="HotKeys:"
				+ "\n1: 'ctrl+o':Execute FlexiPlanner from system tray"
				+ "\n2: 'ctrl+m': Minimise FlexiPlanner to system tray"
				+ "\n3: 'f1': Guide"
				+ "\n4: 'f2': Executed commands"
				+ "\n5: 'f3': Over due tasks"
				+ "\n7: 'f4': Today task"
				+ "\n8: 'f5': Scroll up over due tasks"
				+ "\n9: 'f6': Scroll down over due tasks"
				+ "\n10: 'f7': Scroll up today tasks"
				+ "\n11: 'f8': Scroll down today tasks"
				+ "\n12: 'f9': Scroll up Executed commands"
				+ "\n13: 'f10': Scroll down Executed commands"
				+ "\n14: 'f11': Scroll up category"
				+ "\n15: 'f12': Scroll down category"
				+ "\n16: 'pgup': Previous month"
				+ "\n17: 'pgdw': Next month";
		return guide;
	}

	private void refreshTableForDisplay(String displayTable) {

		// Clear table
		;/*for (int i = 0; i < testTable.getRowCount(); i++) {
			for (int j = 0; j < testTable.getColumnCount(); j++) {
				testTable.setValueAt(null, i, j);
			}
		}

		for(int i = 0; i < testTable.getRowCount(); i++){
			for(int j = 0; j <testTable.getColumnCount(); j++){
				testTable.setValueAt("1", i, j);
			}
		};*/
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
				setBackground(Color.PINK);

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
							setBackground(Color.MAGENTA);// set colour for current day
							// with task
						} else {
							setBackground(Color.GRAY);// set colour current
							// day
						}
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						setBackground(Color.GRAY);
						e.printStackTrace();
					}
				} else
					try {
						if (logic.hasTask(date) 
								&& (Integer.parseInt(value.toString()) < actualDay)
								&& (currentDisplayedMonth == actualMonth)
								&& (currentDisplayedYear == actualYear)) {
							setBackground(Color.RED);
						}else if(logic.hasTask(date) 
								&& (currentDisplayedMonth < actualMonth)
								&& (currentDisplayedYear == actualYear)){
							setBackground(Color.RED);
						}else if(logic.hasTask(date) 
								&& (currentDisplayedYear < actualYear)){
							setBackground(Color.RED);
						}else if(logic.hasTask(date)){
							setBackground(Color.ORANGE);
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

	/**
	 * This method return a current JFrame.
	 * Implemented for global shortcuts.
	 * 
	 * @return JFrame
	 * 
	 * @author Moe Lwin Hein (A0117989H)
	 */
	public static JFrame getJFrame() {
 		return schedulerFrame;
 	}

	/**
	 * This method execute when a hotKey is pressed.
	 * 
	 * @author Moe Lwin Hein (A0117989H)
	 */
	@Override
	public void onHotKey(HotKey hotKey) {
		switch(hotKey.keyStroke.getKeyCode()) {
		case KeyEvent.VK_O : 
			schedulerFrame.setVisible(true);
			schedulerFrame.setExtendedState(JFrame.NORMAL);
			break;
		case KeyEvent.VK_M :
			schedulerFrame.setVisible(false);
			break;
		}
	}
}// end of class FlexiPlannerUI

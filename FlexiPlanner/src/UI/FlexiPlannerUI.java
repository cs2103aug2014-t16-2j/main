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
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import javax.swing.SwingUtilities;
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
import javax.swing.Timer;
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
	private JLabel displayLabel;
	private JLabel showUserRecentAddedTaskLabel;
	private JLabel showCategoryLabel;
	private JPanel schedulerPanel;
	private JButton prevMonth, nextMonth;
	private JTable calendar1;
	private JTable displaytaskTable;
	private DefaultTableModel calendar2;
	private DefaultTableModel displayTasksTableDTM;
	private Border border;
	private JXCollapsiblePane showUserGuidePane;
	private JXCollapsiblePane showTasksCollapsePane;
	private JXCollapsiblePane showUserRecentAddedTaskCollapsePane;
	private JTextArea showUserRecentAddedTaskCommand;
	private JTextArea commandFeedback;
	private JTextArea showCategory;
	private JTextArea showUserGuide;
	private JScrollPane calendarScroll;
	private JScrollPane showUserGuideScroll;
	private JScrollPane showTasksScroll;
	private JScrollPane showUserRecentAddedTaskScroll;
	private JScrollPane showCategoryScroll;
	private JComboBox selectYear;
	private static JFrame schedulerFrame;
	private JTextField inputCommand;
	private int actualYear, actualMonth, actualDay, currentDisplayedYear,
	currentDisplayedMonth;
	private String[] months = { "January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November",
	"December" };
	private String[] columnNames = {"No:","Priority","Catogery","Task","From","To"};
	private Object[][] dummyData = {{"","", "","", "", ""},};
	private String userCommand;
	private int overDueRow=0;
	private int frameNo=1;
	
	private static Logic logic;
	//@author A0111770R
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
	//@author A0111770R
	public void loadInterfaceandData() throws IOException, ParseException {
		JFrame.setDefaultLookAndFeelDecorated(false);
		schedulerFrame.setUndecorated(false);
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

		schedulerFrame.getContentPane().add(schedulerPanel);// add panel to frame

		displaytaskTable=new JTable(new DefaultTableModel(dummyData, columnNames){
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}});
		displayTasksTableDTM = (DefaultTableModel) displaytaskTable.getModel();
		displayTasksTableDTM.setRowCount(50);
		setDisplayTaskTableProperties();//Set table restrictions

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

		displayLabel = new JLabel();
		displayLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		displayLabel.setForeground(Color.BLUE);
		displayLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		displayLabel.setText("Tasks");

		showTasksScroll = new JScrollPane (displaytaskTable, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showTasksScroll.setPreferredSize(new Dimension(570,460));

		showTasksCollapsePane = new JXCollapsiblePane();
		showTasksCollapsePane.add(displayLabel);
		showTasksCollapsePane.add(showTasksScroll);
		showTasksCollapsePane.setCollapsed(true);
		showTasksCollapsePane.setBounds(320, 4, 570, 0);
		showTasksCollapsePane.setPreferredSize(new Dimension(570,495));

		showUserRecentAddedTaskLabel = new JLabel();
		showUserRecentAddedTaskLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showUserRecentAddedTaskLabel.setForeground(Color.BLUE);
		showUserRecentAddedTaskLabel.setBorder(BorderFactory.createCompoundBorder(border, 
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		showUserRecentAddedTaskLabel.setText("Recent Added Tasks");

		showUserRecentAddedTaskCommand = new JTextArea();
		showUserRecentAddedTaskCommand.setFont(new Font("Times New Roman", Font.BOLD, 14));
		showUserRecentAddedTaskCommand.setForeground(Color.CYAN);
		showUserRecentAddedTaskCommand.setBackground(Color.BLUE);
		showUserRecentAddedTaskCommand.setText("");
		showUserRecentAddedTaskCommand.setAutoscrolls(false);
		showUserRecentAddedTaskCommand.setLineWrap(true);
		showUserRecentAddedTaskCommand.setEditable(false);

		showUserRecentAddedTaskScroll = new JScrollPane (showUserRecentAddedTaskCommand, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showUserRecentAddedTaskScroll.setColumnHeaderView(showUserRecentAddedTaskLabel);

		showUserRecentAddedTaskCollapsePane = new JXCollapsiblePane();
		showUserRecentAddedTaskCollapsePane.setContentPane(showUserRecentAddedTaskScroll);
		showUserRecentAddedTaskCollapsePane.setCollapsed(true);
		showUserRecentAddedTaskCollapsePane.setBounds(320, 4, 570, 0);
		showUserRecentAddedTaskCollapsePane.setPreferredSize(new Dimension(570,495));			

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
		showCategory.setText(logic.getCategory());
		showCategory.setEditable(false);

		showCategoryScroll = new JScrollPane (showCategory, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showCategoryScroll.setColumnHeaderView(showCategoryLabel);
		showCategoryScroll.setBounds(10, 325, 300, 175);

		commandFeedback = new JTextArea("");
		commandFeedback.setBackground(new Color(240, 240, 240));
		commandFeedback.setForeground(Color.RED);
		commandFeedback.setFont(new Font("Times New Roman", Font.BOLD, 15));
		commandFeedback.setEditable(false);
		commandFeedback.setLineWrap(true);
		commandFeedback.setBounds(11, 500, 870, 40);

		inputCommand = new JTextField();
		inputCommand.setForeground(Color.BLUE);
		inputCommand.setBackground(Color.WHITE);
		inputCommand.setFont(new Font("Times New Roman", Font.BOLD, 20));
		inputCommand.setBounds(10, 540, 880, 46);

		//add components to panel
		schedulerPanel.add(displayedMonth);
		schedulerPanel.add(displayedYear);
		schedulerPanel.add(selectYear);
		schedulerPanel.add(prevMonth);
		schedulerPanel.add(nextMonth);
		schedulerPanel.add(calendarScroll);
		schedulerPanel.add(showUserGuidePane);
		schedulerPanel.add(showTasksCollapsePane);
		schedulerPanel.add(showUserRecentAddedTaskCollapsePane);
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
		executeKeyAction(commandFeedback,showUserRecentAddedTaskCommand,showCategory,
				showTasksScroll,showUserRecentAddedTaskScroll,showCategoryScroll
				,showUserGuidePane,showTasksCollapsePane,showUserRecentAddedTaskCollapsePane);
	}
	//@author A0111770R
	private void executeKeyAction(final JTextArea commandFeedback,
			final JTextArea showUserRecentAddedTaskCommand,final JTextArea showCategory, 
			final JScrollPane showTasksScroll, final JScrollPane showUserRecentAddedTaskScroll,final JScrollPane showCategoryScroll
			,final JXCollapsiblePane showUserGuidePane,final JXCollapsiblePane showTasksCollapsePane, final JXCollapsiblePane showUserRecentAddedTaskCollapsePane) {
		inputCommand.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				int tasksScrollPane = showTasksScroll.getVerticalScrollBar().getModel().getValue();
				int valueCustomTextArea=showUserRecentAddedTaskScroll.getVerticalScrollBar().getModel().getValue();
				int valueCategoryScrollPane = showCategoryScroll.getVerticalScrollBar().getModel().getValue();
				switch (key){
				case KeyEvent.VK_ENTER: 
					userCommand = inputCommand.getText();
					if (userCommand.toLowerCase().startsWith("exit")) {
						inputCommand.setText("");
						commandFeedback.setText("");
						getJFrame().setVisible(false);
						break;
					}
					if (frameNo==1){
						frameNo=3;
						showUserGuidePane.setCollapsed(true);						
						showUserRecentAddedTaskCollapsePane.setCollapsed(true);
						showTasksCollapsePane.setCollapsed(false);
					}
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
					try {
						showUserRecentAddedTaskCommand.setText(logic.getData(userCommand));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					inputCommand.setText("");
					showCategory.setText(logic.getCategory());
					refreshTasksTableForDisplay(userCommand);//refresh displayTasksTable
					refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
					break;
				
				case KeyEvent.VK_F1:
					frameNo=1;
					showUserGuidePane.setCollapsed(false);						
					showUserRecentAddedTaskCollapsePane.setCollapsed(true);
					showTasksCollapsePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F2:
					frameNo=2;
					showUserRecentAddedTaskCollapsePane.setCollapsed(false);
					showUserGuidePane.setCollapsed(true);
					showTasksCollapsePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F3:
					frameNo=3;
					refreshTasksTableForDisplay("");
					showTasksCollapsePane.setCollapsed(false);
					showUserRecentAddedTaskCollapsePane.setCollapsed(true);
					showUserGuidePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F5:
					showTasksScroll.getVerticalScrollBar().getModel().setValue(tasksScrollPane-5);
					break;
				case KeyEvent.VK_F6:
					showTasksScroll.getVerticalScrollBar().getModel().setValue(tasksScrollPane+5);
					break;
				case KeyEvent.VK_F7:
					showUserRecentAddedTaskScroll.getVerticalScrollBar().getModel().setValue(valueCustomTextArea-5);
					break;					
				case KeyEvent.VK_F8:
					showUserRecentAddedTaskScroll.getVerticalScrollBar().getModel().setValue(valueCustomTextArea+5);
					break;
				case KeyEvent.VK_F9:
					showCategoryScroll.getVerticalScrollBar().getModel().setValue(valueCategoryScrollPane-5);
					break;					
				case KeyEvent.VK_F10:
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
		});
	}

	//@author A0111770R
	private void setValuesCombox() {
		for (int i = actualYear; i <= actualYear + 20; i++) {
			selectYear.addItem(String.valueOf(i));
		}
	}
	
	//@author A0111770R
	private void prevMth() {
		if (currentDisplayedMonth == 0) { // Back one year
			currentDisplayedMonth = 11;
			currentDisplayedYear -= 1;
		} else { // Back one month
			currentDisplayedMonth -= 1;
		}
		refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
	}
	//@author A0111770R
	private void nextMth() {
		if (currentDisplayedMonth == 11) { // Foward one year
			currentDisplayedMonth = 0;
			currentDisplayedYear += 1;
		} else { // Foward one month
			currentDisplayedMonth += 1;
		}
		refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
	}
	//@author A0111770R
	private String getGuide(){
		String guide="HotKeys:"
				+ "\n1: 'ctrl+o':Execute FlexiPlanner from system tray"
				+ "\n2: 'ctrl+m': Minimise FlexiPlanner to system tray"
				+ "\n3: 'ctrl+e': Exit FlexiPlanner"
				+ "\n4: 'f1': Guide"
				+ "\n5: 'f2': Recent added tasks"
				+ "\n6: 'f3': Tasks"
				+ "\n7: 'f5': Scroll up Tasks"
				+ "\n8: 'f6': Scroll down Tasks"
				+ "\n9: 'f7': Scroll up Recent added tasks"
				+ "\n10: 'f8': Scroll down Recent added tasks"
				+ "\n11: 'f9': Scroll up category"
				+ "\n12: 'f10': Scroll down category"
				+ "\n13: 'pgup': Previous month"
				+ "\n14: 'pgdw': Next month";
		return guide;
	}
	//@author A0111770R
	private void setDisplayTaskTableProperties() {
		displaytaskTable.setModel(displayTasksTableDTM);
		displaytaskTable.setCellSelectionEnabled(false);
		displaytaskTable.setRowHeight(20);
		displaytaskTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		displaytaskTable.getColumnModel().getColumn(1).setPreferredWidth(55);
		displaytaskTable.getColumnModel().getColumn(2).setPreferredWidth(65);
		displaytaskTable.getColumnModel().getColumn(3).setPreferredWidth(210);
		displaytaskTable.getColumnModel().getColumn(3).setMaxWidth(210);
		displaytaskTable.getColumnModel().getColumn(4).setPreferredWidth(100);
		displaytaskTable.getColumnModel().getColumn(5).setPreferredWidth(100);
		displaytaskTable.getColumnModel().getColumn(5).setMaxWidth(100);
		displaytaskTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		displaytaskTable.getTableHeader().setResizingAllowed(false);
		displaytaskTable.getTableHeader().setReorderingAllowed(false);
		displaytaskTable.setColumnSelectionAllowed(false);
		displaytaskTable.setRowSelectionAllowed(false);
	}
	
	//@author A0111770R
	private void refreshTasksTableForDisplay(String userCommand) {
		// Clear table
		for (int i = 0; i < displayTasksTableDTM.getRowCount(); i++) {
			for (int j = 0; j < displayTasksTableDTM.getColumnCount(); j++) {
				displayTasksTableDTM.setValueAt("", i, j);
			}
		}
			overDueRow=logic.getOverdueRow();
			int row=0;
			/*for (Logic.DisplayedEntry t : logic.getRequiredTask(userCommand,frameNo)) {
				displayTasksTableDTM.setValueAt(row+1, row, 0);			
				if (t.getPriority() != null)
					displayTasksTableDTM.setValueAt(t.getPriority(),row,1);
				if (t.getCategory() != null)
					displayTasksTableDTM.setValueAt(t.getCategory(),row,2);
				displayTasksTableDTM.setValueAt(t.getContent(),row,3);
				if (t.getStartDateTime() != null){
					String startYear=t.getStartDateTime().toString().substring(0, 4);
					String startMonth=t.getStartDateTime().toString().substring(5, 7);
					String startDay=t.getStartDateTime().toString().substring(8, 10);
					String startTime=t.getStartDateTime().toString().substring(11, 16);
					String getStart=startDay+"/"+startMonth+"/"+startYear+" "+startTime;
					displayTasksTableDTM.setValueAt(getStart,row,4);
				}
				if (t.getEndDateTime() != null){
					String startYear=t.getEndDateTime().toString().substring(0, 4);
					String startMonth=t.getEndDateTime().toString().substring(5, 7);
					String startDay=t.getEndDateTime().toString().substring(8, 10);
					String startTime=t.getEndDateTime().toString().substring(11, 16);
					String getEnd=startDay+"/"+startMonth+"/"+startYear+" "+startTime;
					displayTasksTableDTM.setValueAt(getEnd,row,5);
				}				
				row++;
				if(row==50){break;}
			}*/
			
		
		displaytaskTable.setDefaultRenderer(displaytaskTable.getColumnClass(0),
				new TasksTableRenderer());// using Calendar1Renderer class to set

	}
	class TasksTableRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			super.getTableCellRendererComponent(table, value, selected,
					focused, row, column);

			if(row==overDueRow && overDueRow==0){
				setBackground(Color.WHITE);
			}
			else if (row <= overDueRow) {
				setBackground(Color.RED);
			} else { 
				setBackground(Color.WHITE);
			}

			if (value != null) {
				;				
			}
			setBorder(null);
			return this;
		}
	}// end of class TasksTableRenderer


	//@author A0111770R
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
	//@author A0111770R
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
	//@author A0111770R
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
	//@author A0111770R
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
	//@author A0111770R
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
		case KeyEvent.VK_E :
			Tray.stopShortCuts();
			System.exit(0);
		default : break;
		}
	}
	private void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				commandFeedback.append(text);
			}
		});
	}


	public void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				updateTextArea(String.valueOf((char) b));
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				updateTextArea(new String(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};


		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}
}// end of class FlexiPlannerUI

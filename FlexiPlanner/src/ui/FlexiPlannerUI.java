package ui;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

import logic.*;

import org.json.simple.parser.ParseException;
import org.jdesktop.swingx.JXCollapsiblePane;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.apple.eawt.Application;
/**
 *
 * @author Eugene Tan Teck Li(A0111770R)
 */
// @A0111770R
@SuppressWarnings("restriction")
public class FlexiPlannerUI implements HotKeyListener {
	private JLabel displayedMonth, displayedYear;
	private JLabel showUserGuideLabel;
	private JLabel displayLabel;
	private JLabel showBlockedLabel;
	private JLabel showUserRecentAddedTaskLabel;
	private JLabel showCategoryLabel;
	private JPanel schedulerPanel;
	private JButton prevMonth, nextMonth;
	private JTable calendar;
	private JTable displaytaskTable;
	private DefaultTableModel calendarDTM;
	private DefaultTableModel displayTasksTableDTM;
	private Border border;
	private JXCollapsiblePane showUserGuideCollapsePane;
	private JXCollapsiblePane showTasksCollapsePane;
	private JXCollapsiblePane showUserRecentAddedTaskCollapsePane;
	private JXCollapsiblePane showBlockedCollapsePane;
	private JXCollapsiblePane showCategoryCollapsePane;
	private JTextArea showUserRecentAddedTaskCommand;
	private JTextArea commandFeedback;
	private JTextArea showCategory;
	private JTextArea showUserGuide;
	private JTextArea showBlocked;
	private JScrollPane calendarScroll;
	private JScrollPane showUserGuideScroll;
	private JScrollPane showTasksScroll;
	private JScrollPane showUserRecentAddedTaskScroll;
	private JScrollPane showCategoryScroll;
	private JScrollPane showBlockedScroll;
	@SuppressWarnings("rawtypes")
	private JComboBox selectYear;
	private JFrame schedulerFrame;
	private JTextField inputCommand;
	private int actualYear, actualMonth, actualDay, currentDisplayedYear,
			currentDisplayedMonth;
	private String[] months = { "January", "February", "March", "April", "May",
			"June", "July", "August", "September", "October", "November",
			"December" };
	private String[] columnNames = { "No:", "Priority", "Category", "Task",
			"From", "To" };
	private Object[][] dummyData = { { "", "", "", "", "", "" }, };
	private String userCommand;
	private int overDueRow = 0;
	private static Logic logic;

	// @author A0111770R
	public FlexiPlannerUI() {
		try {
			logic = new Logic();
			schedulerFrame = new JFrame("FlexiPlanner");
		} catch (FileNotFoundException e) {
			System.out.println("Error");
		} catch (IOException e) {
			System.out.println("Error");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void loadUI() {
		try {
			loadInterfaceandData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @author A0111770R
	@SuppressWarnings({ "serial", "rawtypes" })
	private void loadInterfaceandData() throws IOException, ParseException {
		JFrame.setDefaultLookAndFeelDecorated(false);
		schedulerFrame.setUndecorated(false);
		/**
		 * ICONIFIED so as to still run the application although close button is
		 * pressed.
		 **/
		schedulerFrame.setDefaultCloseOperation(JFrame.ICONIFIED);
		ImageIcon img = new ImageIcon(getClass().getResource("/resources/logo.png"));
		schedulerFrame.setIconImage(img.getImage());
		if (System.getProperty("os.name").equals("Mac OS X")) {
			Application.getApplication().setDockIconImage(new
			ImageIcon(getClass().getResource("/resources/logo.png")).getImage());
		}
		schedulerFrame.setSize(900, 620);// set frame size
		schedulerFrame.setResizable(false);
		schedulerFrame.setLocationRelativeTo(null);
		schedulerFrame.setVisible(true);
		calendarDTM = new DefaultTableModel() {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		};// get default table model for calendar
		calendar = new JTable(calendarDTM);// create new table
		calendarScroll = new JScrollPane(calendar,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		schedulerPanel = new JPanel(null);
		schedulerFrame.getContentPane().add(schedulerPanel);// add panel to
		// frame
		displaytaskTable = new JTable(new DefaultTableModel(dummyData,
				columnNames) {
			public boolean isCellEditable(int rowIndex, int mColIndex) {
				return false;
			}
		});
		displayTasksTableDTM = (DefaultTableModel) displaytaskTable.getModel();
		displayTasksTableDTM.setRowCount(50);
		setDisplayTaskTableProperties();// Set table restrictions
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
		showUserGuide.setWrapStyleWord(true);
		showUserGuide.setEditable(false);
		showUserGuideScroll = new JScrollPane(showUserGuide,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showUserGuideScroll.setColumnHeaderView(showUserGuideLabel);
		showUserGuideCollapsePane = new JXCollapsiblePane();
		showUserGuideCollapsePane.setContentPane(showUserGuideScroll);
		showUserGuideCollapsePane.setCollapsed(true);
		showUserGuideCollapsePane.setBounds(320, 4, 570, 0);
		showUserGuideCollapsePane.setPreferredSize(new Dimension(570, 495));
		showUserGuideCollapsePane.setCollapsed(false);
		displayLabel = new JLabel();
		displayLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		displayLabel.setForeground(Color.BLUE);
		displayLabel.setBorder(BorderFactory.createCompoundBorder(border,
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		displayLabel.setText("Tasks");
		showTasksScroll = new JScrollPane(displaytaskTable,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showTasksScroll.setPreferredSize(new Dimension(570, 460));
		showTasksCollapsePane = new JXCollapsiblePane();
		showTasksCollapsePane.add(displayLabel);
		showTasksCollapsePane.add(showTasksScroll);
		showTasksCollapsePane.setCollapsed(true);
		showTasksCollapsePane.setBounds(320, 4, 570, 0);
		showTasksCollapsePane.setPreferredSize(new Dimension(570, 495));
		showUserRecentAddedTaskLabel = new JLabel();
		showUserRecentAddedTaskLabel.setFont(new Font("Times New Roman",
				Font.BOLD, 15));
		showUserRecentAddedTaskLabel.setForeground(Color.BLUE);
		showUserRecentAddedTaskLabel.setBorder(BorderFactory
				.createCompoundBorder(border,
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		showUserRecentAddedTaskLabel.setText("Recent Added Tasks");
		showUserRecentAddedTaskCommand = new JTextArea();
		showUserRecentAddedTaskCommand.setFont(new Font("Times New Roman",
				Font.BOLD, 14));
		showUserRecentAddedTaskCommand.setForeground(Color.CYAN);
		showUserRecentAddedTaskCommand.setBackground(Color.BLUE);
		showUserRecentAddedTaskCommand.setText("");
		showUserRecentAddedTaskCommand.setAutoscrolls(false);
		showUserRecentAddedTaskCommand.setLineWrap(true);
		showUserRecentAddedTaskCommand.setWrapStyleWord(true);
		showUserRecentAddedTaskCommand.setEditable(false);
		showUserRecentAddedTaskScroll = new JScrollPane(
				showUserRecentAddedTaskCommand,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showUserRecentAddedTaskScroll
				.setColumnHeaderView(showUserRecentAddedTaskLabel);
		showUserRecentAddedTaskCollapsePane = new JXCollapsiblePane();
		showUserRecentAddedTaskCollapsePane
				.setContentPane(showUserRecentAddedTaskScroll);
		showUserRecentAddedTaskCollapsePane.setCollapsed(true);
		showUserRecentAddedTaskCollapsePane.setBounds(320, 4, 570, 0);
		showUserRecentAddedTaskCollapsePane.setPreferredSize(new Dimension(570,
				495));
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
		showCategory.setWrapStyleWord(true);
		showCategory.setText(logic.getCategory());
		showCategory.setEditable(false);
		showCategoryScroll = new JScrollPane(showCategory,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showCategoryScroll.setColumnHeaderView(showCategoryLabel);
		showCategoryCollapsePane = new JXCollapsiblePane();
		showCategoryCollapsePane.setContentPane(showCategoryScroll);
		showCategoryCollapsePane.setBounds(10, 325, 300, 0);
		showCategoryCollapsePane.setPreferredSize(new Dimension(300, 175));
		showCategoryCollapsePane.setCollapsed(true);
		showCategoryCollapsePane.setCollapsed(false);
		showBlockedLabel = new JLabel();
		showBlockedLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		showBlockedLabel.setForeground(Color.BLUE);
		showBlockedLabel.setBorder(BorderFactory.createCompoundBorder(border,
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		showBlockedLabel.setText("Blocked");
		showBlocked = new JTextArea();
		showBlocked.setFont(new Font("Times New Roman", Font.BOLD, 14));
		showBlocked.setForeground(Color.CYAN);
		showBlocked.setBackground(Color.BLUE);
		showBlocked.setText("");
		showBlocked.setAutoscrolls(false);
		showBlocked.setLineWrap(true);
		showBlocked.setWrapStyleWord(true);
		showBlocked.setEditable(false);
		showBlockedScroll = new JScrollPane(showBlocked,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		showBlockedScroll.setColumnHeaderView(showBlockedLabel);
		showBlockedCollapsePane = new JXCollapsiblePane();
		showBlockedCollapsePane.setContentPane(showBlockedScroll);
		showBlockedCollapsePane.setCollapsed(true);
		showBlockedCollapsePane.setBounds(10, 325, 300, 0);
		showBlockedCollapsePane.setPreferredSize(new Dimension(300, 175));
		commandFeedback = new JTextArea("");
		commandFeedback.setBackground(new Color(240, 240, 240));
		commandFeedback.setForeground(Color.RED);
		commandFeedback.setFont(new Font("Times New Roman", Font.BOLD, 15));
		commandFeedback.setEditable(false);
		commandFeedback.setLineWrap(true);
		commandFeedback.setWrapStyleWord(true);
		commandFeedback.setBounds(11, 500, 870, 40);
		inputCommand = new JTextField();
		inputCommand.setForeground(Color.BLUE);
		inputCommand.setBackground(Color.WHITE);
		inputCommand.setFont(new Font("Times New Roman", Font.BOLD, 20));
		inputCommand.setBounds(10, 540, 880, 46);
		// add components to panel
		schedulerPanel.add(displayedMonth);
		schedulerPanel.add(displayedYear);
		schedulerPanel.add(selectYear);
		schedulerPanel.add(prevMonth);
		schedulerPanel.add(nextMonth);
		schedulerPanel.add(calendarScroll);
		schedulerPanel.add(showUserGuideCollapsePane);
		schedulerPanel.add(showTasksCollapsePane);
		schedulerPanel.add(showUserRecentAddedTaskCollapsePane);
		schedulerPanel.add(showBlockedCollapsePane);
		schedulerPanel.add(showCategoryCollapsePane);
		schedulerPanel.add(commandFeedback);
		schedulerPanel.add(inputCommand);
		String[] headers = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" }; // All
		// headers
		for (int i = 0; i < 7; i++) {
			calendarDTM.addColumn(headers[i]);// add it on top column(left to
			// right)
		}
		calendar.getTableHeader().setResizingAllowed(false);
		calendar.getTableHeader().setReorderingAllowed(false);
		calendar.setColumnSelectionAllowed(false);
		calendar.setRowSelectionAllowed(false);
		calendar.setRowHeight(40);
		calendarDTM.setColumnCount(7);// set no of columns of calendar(inner)
		calendarDTM.setRowCount(6);// set no of rows of calendar(inner)
		setValuesCombox();// combo box for selectYear
		refreshCalendar(actualMonth, actualYear); // Refresh calendar
		prevMonth.addActionListener(new Prev_Year());
		nextMonth.addActionListener(new Next_Year());
		selectYear.addActionListener(new Years_Action());
		inputCommand.requestFocusInWindow();
		executeKeyAction(commandFeedback, showUserRecentAddedTaskCommand,
				showCategory, showBlocked, showTasksScroll,
				showUserRecentAddedTaskScroll, showCategoryScroll,
				showUserGuideCollapsePane, showTasksCollapsePane,
				showUserRecentAddedTaskCollapsePane, showBlockedCollapsePane,
				showCategoryCollapsePane);
	}

	// @author A0111770R
	private void executeKeyAction(final JTextArea commandFeedback,
			final JTextArea showUserRecentAddedTaskCommand,
			final JTextArea showCategory, final JTextArea showBlocked,
			final JScrollPane showTasksScroll,
			final JScrollPane showUserRecentAddedTaskScroll,
			final JScrollPane showCategoryScroll,
			final JXCollapsiblePane showUserGuidePane,
			final JXCollapsiblePane showTasksCollapsePane,
			final JXCollapsiblePane showUserRecentAddedTaskCollapsePane,
			final JXCollapsiblePane showBlockedCollapsePane,
			final JXCollapsiblePane showCategoryCollapsePane) {
		inputCommand.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				int tasksScrollPane = showTasksScroll.getVerticalScrollBar()
						.getModel().getValue();
				int valueCustomTextArea = showUserRecentAddedTaskScroll
						.getVerticalScrollBar().getModel().getValue();
				int valueCategoryScrollPane = showCategoryScroll
						.getVerticalScrollBar().getModel().getValue();
				switch (key) {
				case KeyEvent.VK_ENTER:
					userCommand = inputCommand.getText();
					if (userCommand.toLowerCase().startsWith("exit")) {
						inputCommand.setText("");
						commandFeedback.setText("");
						save();
						getJFrame().setVisible(false);
						break;
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
					if (showUserRecentAddedTaskCollapsePane.isCollapsed()) {
						showUserGuidePane.setCollapsed(true);
						showUserRecentAddedTaskCollapsePane.setCollapsed(true);
						showTasksCollapsePane.setCollapsed(false);
						try {
							showUserRecentAddedTaskCommand.setText(logic
									.getData(userCommand));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						refreshTasksTableForDisplay(userCommand);// refresh
						// displayTasksTable
					} else {
						refreshTasksTableForDisplay(userCommand);// refresh
						// displayTasksTable
						try {
							showUserRecentAddedTaskCommand.setText(logic
									.getData(userCommand));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					inputCommand.setText("");
					showCategory.setText(logic.getCategory());
					refreshTasksTableForDisplay(userCommand);
					refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
					break;
				case KeyEvent.VK_F1:
					commandFeedback.setText("");
					showUserGuidePane.setCollapsed(false);
					showUserRecentAddedTaskCollapsePane.setCollapsed(true);
					showTasksCollapsePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F2:
					commandFeedback.setText("");
					try {
						showUserRecentAddedTaskCommand.setText(logic
								.getData(userCommand));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					showUserRecentAddedTaskCollapsePane.setCollapsed(false);
					showUserGuidePane.setCollapsed(true);
					showTasksCollapsePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F3:
					commandFeedback.setText("");
					refreshTasksTableForDisplay("");
					refreshTasksTableForDisplay("");
					showTasksCollapsePane.setCollapsed(false);
					showUserRecentAddedTaskCollapsePane.setCollapsed(true);
					showUserGuidePane.setCollapsed(true);
					break;
				case KeyEvent.VK_F4:
					if (showBlockedCollapsePane.isCollapsed()) {
						showBlockedCollapsePane.setCollapsed(false);
						showCategoryCollapsePane.setCollapsed(true);
					} else {
						showBlockedCollapsePane.setCollapsed(true);
						showCategoryCollapsePane.setCollapsed(false);
					}
					break;
				case KeyEvent.VK_F5:
					showTasksScroll.getVerticalScrollBar().getModel()
							.setValue(tasksScrollPane - 5);
					break;
				case KeyEvent.VK_F6:
					showTasksScroll.getVerticalScrollBar().getModel()
							.setValue(tasksScrollPane + 5);
					break;
				case KeyEvent.VK_F7:
					showUserRecentAddedTaskScroll.getVerticalScrollBar()
							.getModel().setValue(valueCustomTextArea - 5);
					break;
				case KeyEvent.VK_F8:
					showUserRecentAddedTaskScroll.getVerticalScrollBar()
							.getModel().setValue(valueCustomTextArea + 5);
					break;
				case KeyEvent.VK_F9:
					showCategoryScroll.getVerticalScrollBar().getModel()
							.setValue(valueCategoryScrollPane - 5);
					break;
				case KeyEvent.VK_F10:
					showCategoryScroll.getVerticalScrollBar().getModel()
							.setValue(valueCategoryScrollPane + 5);
					break;
				case KeyEvent.VK_PAGE_UP:
					if (currentDisplayedMonth == 0
							&& currentDisplayedYear == actualYear) {
						break;
					}
					prevMth();
					break;
				case KeyEvent.VK_PAGE_DOWN:
					if (currentDisplayedMonth == 11
							&& currentDisplayedYear >= actualYear + 20) {
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

	// @author A0111770R
	@SuppressWarnings("unchecked")
	private void setValuesCombox() {
		for (int i = actualYear; i <= actualYear + 20; i++) {
			selectYear.addItem(String.valueOf(i));
		}
	}

	// @author A0111770R
	private void prevMth() {
		if (currentDisplayedMonth == 0) { // Back one year
			currentDisplayedMonth = 11;
			currentDisplayedYear -= 1;
		} else { // Back one month
			currentDisplayedMonth -= 1;
		}
		refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
	}

	// @author A0111770R
	private void nextMth() {
		if (currentDisplayedMonth == 11) { // Foward one year
			currentDisplayedMonth = 0;
			currentDisplayedYear += 1;
		} else { // Foward one month
			currentDisplayedMonth += 1;
		}
		refreshCalendar(currentDisplayedMonth, currentDisplayedYear);
	}

	// @author A0111770R
	private String getGuide() {
		String guide = "HotKeys:" + "\n1: 'ctrl+o': Launch FlexiPlanner"
				+ "\n2: 'ctrl+m': Close FlexiPlanner"
				+ "\n3: 'ctrl+e': Exit FlexiPlanner" + "\n4: 'f1': Guide"
				+ "\n5: 'f2': Recent added tasks" + "\n6: 'f3': Tasks"
				+ "\n5: 'f4': Categories / Blocked"
				+ "\n7: 'f5': Scroll up Tasks" + "\n8: 'f6': Scroll down Tasks"
				+ "\n9: 'f7': Scroll up Recent added tasks"
				+ "\n10: 'f8': Scroll down Recent added tasks"
				+ "\n11: 'f9': Scroll up category"
				+ "\n12: 'f10': Scroll down category"
				+ "\n13: 'pgup': Previous month" + "\n14: 'pgdw': Next month";
		return guide;
	}

	// @author A0111770R
	private void setDisplayTaskTableProperties() {
		displaytaskTable.setModel(displayTasksTableDTM);
		displaytaskTable.setCellSelectionEnabled(false);
		displaytaskTable.setRowHeight(60);
		displaytaskTable.getColumnModel().getColumn(0)
				.setCellRenderer(new TasksTableRenderer());
		displaytaskTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		displaytaskTable.getColumnModel().getColumn(1).setPreferredWidth(55);
		displaytaskTable.getColumnModel().getColumn(2).setPreferredWidth(65);
		displaytaskTable.getColumnModel().getColumn(3).setPreferredWidth(220);
		displaytaskTable.getColumnModel().getColumn(3).setMaxWidth(220);
		displaytaskTable.getColumnModel().getColumn(4).setPreferredWidth(90);
		displaytaskTable.getColumnModel().getColumn(5).setPreferredWidth(90);
		displaytaskTable.getColumnModel().getColumn(5).setMaxWidth(110);
		displaytaskTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		displaytaskTable.getTableHeader().setResizingAllowed(false);
		displaytaskTable.getTableHeader().setReorderingAllowed(false);
		displaytaskTable.setColumnSelectionAllowed(false);
		displaytaskTable.setRowSelectionAllowed(false);
	}

	// @author A0111770R
	private void refreshTasksTableForDisplay(String userCommand) {
		// Clear table
		for (int i = 0; i < displayTasksTableDTM.getRowCount(); i++) {
			for (int j = 0; j < displayTasksTableDTM.getColumnCount(); j++) {
				displayTasksTableDTM.setValueAt("", i, j);
			}
		}
		overDueRow = logic.getOverdueRow();
		int row = 0;
		if (logic.getRequiredTask(userCommand) != null) {
			for (DisplayedEntry t : logic.getRequiredTask(userCommand)) {
				displayTasksTableDTM.setValueAt(row + 1, row, 0);
				if (t.getPriority() != null)
					displayTasksTableDTM.setValueAt(t.getPriority(), row, 1);
				if (t.getCategory() != null)
					displayTasksTableDTM.setValueAt(t.getCategory(), row, 2);
				if (t.getRemindDateTime() != null && t.getReminder() != null) {
					StringBuilder sb = new StringBuilder();
					DateTimeFormatter formatter = DateTimeFormatter
							.ofLocalizedDateTime(FormatStyle.MEDIUM,
									FormatStyle.SHORT);
					String remindDateTime = t.getRemindDateTime().format(
							formatter);
					sb.append(t.getContent() + " ");
					sb.append("[reminder:" + remindDateTime + "]");
					displayTasksTableDTM.setValueAt(sb.toString(), row, 3);
				} else {
					displayTasksTableDTM.setValueAt(t.getContent(), row, 3);
				}
				try {
					if (t.getStartDateTime() != null) {
						displayTasksTableDTM.setValueAt(t.getStartDateTime(),
								row, 4);
					}
					if (t.getEndDateTime() != null) {
						displayTasksTableDTM.setValueAt(t.getEndDateTime(),
								row, 5);
					}
				} catch (java.text.ParseException e) {
					System.out.println("");
				}
				row++;
				if (row == 50) {
					break;
				}
			}
		}
		displaytaskTable.setDefaultRenderer(displaytaskTable.getColumnClass(0),
				new TextWrapRenderer());
	}

	// @author A0111770R
	@SuppressWarnings("serial")
	class TasksTableRenderer extends DefaultTableCellRenderer {
		@SuppressWarnings("unused")
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			Component c = super.getTableCellRendererComponent(table, value,
					selected, focused, row, column);
			if (row == overDueRow && overDueRow == 0) {
				if (table.getModel().getValueAt(row, 4) != null
						&& !(((String) table.getModel().getValueAt(row, 4))
								.equals(""))) {
					String start = table.getModel().getValueAt(row, 4)
							.toString();
					LocalDateTime s = LocalDateTime.parse(start,
							DateTimeFormatter.ofPattern("d MMM yyyy HH:mm"));
					if ((s.getYear() == LocalDateTime.now().getYear())
							&& (s.getMonthValue() == LocalDateTime.now()
									.getMonthValue())
							&& (s.getDayOfMonth() == LocalDateTime.now()
									.getDayOfMonth())) {

						setBackground(Color.MAGENTA);
					} else if (s.isBefore(LocalDateTime.now())) {
						setBackground(Color.RED);
					} else {
						setBackground(Color.ORANGE);
					}
				} else if (table.getModel().getValueAt(row, 4) != null
						&& !(((String) table.getModel().getValueAt(row, 5))
								.equals(""))) {
					String end = table.getModel().getValueAt(row, 5).toString();
					LocalDateTime e = LocalDateTime.parse(end,
							DateTimeFormatter.ofPattern("d MMM yyyy HH:mm"));
					if ((e.getYear() == LocalDateTime.now().getYear())
							&& (e.getMonthValue() == LocalDateTime.now()
									.getMonthValue())
							&& (e.getDayOfMonth() == LocalDateTime.now()
									.getDayOfMonth())) {

						setBackground(Color.MAGENTA);
					} else if (e.isBefore(LocalDateTime.now())) {
						setBackground(Color.RED);
					} else {
						setBackground(Color.ORANGE);
					}
				} else {
					setBackground(Color.ORANGE);
				}
				if (table.getModel().getValueAt(row, 3) == null
						|| (((String) table.getModel().getValueAt(row, 3))
								.equals(""))) {
					setBackground(Color.WHITE);
				}
			} else if (row <= overDueRow - 1) {
				setBackground(Color.RED);
			} else {
				if (table.getModel().getValueAt(row, 4) != null
						&& !(((String) table.getModel().getValueAt(row, 4))
								.equals(""))) {
					String start = table.getModel().getValueAt(row, 4)
							.toString();
					LocalDateTime s = LocalDateTime.parse(start,
							DateTimeFormatter.ofPattern("d MMM yyyy HH:mm"));
					if ((s.getYear() == LocalDateTime.now().getYear())
							&& (s.getMonthValue() == LocalDateTime.now()
									.getMonthValue())
							&& (s.getDayOfMonth() == LocalDateTime.now()
									.getDayOfMonth())) {

						setBackground(Color.MAGENTA);
					} else if (s.isBefore(LocalDateTime.now())) {
						setBackground(Color.RED);
					} else {
						setBackground(Color.ORANGE);
					}
				} else if (table.getModel().getValueAt(row, 5) != null
						&& !(((String) table.getModel().getValueAt(row, 5))
								.equals(""))) {
					String end = table.getModel().getValueAt(row, 5).toString();
					LocalDateTime e = LocalDateTime.parse(end,
							DateTimeFormatter.ofPattern("d MMM yyyy HH:mm"));
					if ((e.getYear() == LocalDateTime.now().getYear())
							&& (e.getMonthValue() == LocalDateTime.now()
									.getMonthValue())
							&& (e.getDayOfMonth() == LocalDateTime.now()
									.getDayOfMonth())) {

						setBackground(Color.MAGENTA);
					} else if (e.isBefore(LocalDateTime.now())) {
						setBackground(Color.RED);
					} else {
						setBackground(Color.ORANGE);
					}
				} else {
					setBackground(Color.ORANGE);
				}
				if (table.getModel().getValueAt(row, 3) == null
						|| (((String) table.getModel().getValueAt(row, 3))
								.equals(""))) {
					setBackground(Color.WHITE);
				}
			}
			if (value != null) {
				;
			}
			setBorder(null);
			return this;
		}
	}// end of class TasksTableRenderer

	// @author A0111770R
	@SuppressWarnings("serial")
	class TextWrapRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {

			JTextArea area = new JTextArea();
			area.setEditable(false);
			area.setLineWrap(true);
			area.setWrapStyleWord(true);
			area.setText(value.toString());

			if (column == 3) {
				if (value instanceof String) {
					if (((String) value).startsWith("[reminder :")) {
						String[] string = ((String) value).split(" ");
						StringBuilder sb = new StringBuilder();
						sb.append(string[0] + " ");
						sb.append(string[1]);
						area.setText(sb.toString());
					}
				}
			}

			setBorder(null);
			return area;
		}
	}// end of class CellRenderer

	// @author A0111770R
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
				calendarDTM.setValueAt(null, i, j);
			}
		}
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		som = cal.get(GregorianCalendar.DAY_OF_WEEK);
		for (int i = 1; i <= nod; i++) {
			int row = new Integer((i + som - 2) / 7);
			int column = (i + som - 2) % 7;
			calendarDTM.setValueAt(i, row, column);
		}// set value for the days displayed
		calendar.setDefaultRenderer(calendar.getColumnClass(0),
				new CalendarRenderer());// using Calendar1Renderer class to set
		// table display
	}// end of refreshCalendar method
		// class Calendar1Renderer used for editting how things are displayed

	// @author A0111770R
	@SuppressWarnings("serial")
	class CalendarRenderer extends DefaultTableCellRenderer {
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
							setBackground(Color.MAGENTA);// set colour for
							// current day
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
						} else if (logic.hasTask(date)
								&& (currentDisplayedMonth < actualMonth)
								&& (currentDisplayedYear == actualYear)) {
							setBackground(Color.RED);
						} else if (logic.hasTask(date)
								&& (currentDisplayedYear < actualYear)) {
							setBackground(Color.RED);
						} else if (logic.hasTask(date)) {
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
	// @author A0111770R
	class Prev_Year implements ActionListener {
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
	// @author A0111770R
	class Next_Year implements ActionListener {
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

	// @author A0111770R
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
	 * This method return a current JFrame. Implemented for global shortcuts.
	 *
	 * @return JFrame
	 *
	 * @author Moe Lwin Hein (A0117989H)
	 */
	public JFrame getJFrame() {
		return schedulerFrame;
	}

	public static void save() {

		try {
			logic.saveData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method execute when a hotKey is pressed.
	 *
	 * @author Moe Lwin Hein (A0117989H)
	 */
	@Override
	public void onHotKey(HotKey hotKey) {
		switch (hotKey.keyStroke.getKeyCode()) {
		case KeyEvent.VK_O:
			schedulerFrame.setVisible(true);
			schedulerFrame.setExtendedState(JFrame.NORMAL);
			break;
		case KeyEvent.VK_M:
			schedulerFrame.setVisible(false);
			break;
		case KeyEvent.VK_E:
			Tray.stopShortCuts();
			System.exit(0);
		default:
			break;
		}
	}

}// end of class FlexiPlannerUI
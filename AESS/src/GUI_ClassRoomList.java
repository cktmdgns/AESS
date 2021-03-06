import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**강의실 목록 GUI**/
public class GUI_ClassRoomList extends JPanel implements MouseListener
{
	String id;
	Object nowListValue, nowRowB, nowRowC, nowRowM;
	int nowListRow, nowListCol;
	int dragStartRow, dragStartCol, dragEndRow;
	protected Connection conn;	//DB접속을 위한 컨넥션 변수
	User_Admin admin;
	JFrame Fr_addRoom, Fr_addSchedule;
	JTable table, table_Room;
	DefaultTableModel sche;
	MyTableModel DTM_Room;
	JButton bt_addRoom = new JButton("강의실 추가");
	JButton bt_delRoom = new JButton("강의실 삭제");
	JButton bt_popAdd = new JButton("추가");
	JButton enterB = new JButton("입력");
	JButton cancelB = new JButton("취소");
	JTextField tf_building, tf_room, tf_maxSeat;
	JPanel pn_roomList = new JPanel();
	JPanel pn_listButton = new JPanel();
	JPanel pn_roomInfo = new JPanel();
	String [][] schedule_no = new String[20][20];
	String [] col_roomList = {"건물","호수","인원"};
	String [][] data_roomList;
	
	String [] col_roomTimetable = {"시간","월","화","수","목","금","토","일"};
	String [][] data_roomTimetable = {{"1A(09:00~09:30)","","","","","","",""},
			{"1B(09:30~10:00)","","","","","","",""},
			{"2A(10:00~10:30)","","","","","","",""},
			{"2B(10:30~11:00)","","","","","","",""},
			{"3A(11:00~11:30)","","","","","","",""},
			{"3B(11:30~12:00)","","","","","","",""},
			{"4A(12:00~12:30)","","","","","","",""},
			{"4B(12:30~01:00)","","","","","","",""},
			{"5A(01:00~01:30)","","","","","","",""},
			{"5B(01:30~02:00)","","","","","","",""},
			{"6A(02:00~02:30)","","","","","","",""},
			{"6B(02:30~03:00)","","","","","","",""},
			{"7A(03:00~03:30)","","","","","","",""},
			{"7B(03:30~04:00)","","","","","","",""},
			{"8A(04:00~04:30)","","","","","","",""},
			{"8B(04:30~05:00)","","","","","","",""},
			{"9A(05:00~05:30)","","","","","","",""},
			{"9B(05:30~06:00)","","","","","","",""}};
	
	JRadioButton tableJRadioButton;
	JRadioButton eventJRadioButton;
	JLabel lb_name, lb_code, lb_prof;
	JComboBox cb_prof, cb_prof_id, dateC, timeC1, timeC2;
	JTextField tf_name, tf_code;
	int regularOrEtc = 0;
	Enums enums = new Enums();
	
	public GUI_ClassRoomList(Connection conn, String id)
	{
		this.id = id;
		this.conn = conn;
		info i = new info(id);
	
		if(info.is_A) admin = new User_Admin(id, conn);
	
		setLayout(new BorderLayout());
		
		/*************우측 리스트***************/
		pn_roomList.setBorder(new TitledBorder("강의실 List"));
		pn_roomList.setLayout(new BorderLayout());
		
		sche = new DefaultTableModel(data_roomList, col_roomList);
		
		table = new JTable(sche);		
		JScrollPane sp = new JScrollPane(table);
		listRoom();
					
		pn_listButton.add(bt_addRoom, "North");
		pn_listButton.add(bt_delRoom, "North");
		
		if(info.is_A) pn_roomList.add(pn_listButton, "North");
		pn_roomList.add(sp, "Center");
		add(pn_roomList,"East");
		pn_roomList.setPreferredSize(new java.awt.Dimension(200, 1));
		
		bt_addRoom.addActionListener(new adminListener());
		bt_delRoom.addActionListener(new adminListener());
		table.addMouseListener(this);
		
		/************* 메인 강의실 info ************/
		
		pn_roomInfo.setBorder(new TitledBorder("강의실 시간표"));
		pn_roomInfo.setLayout(new BoxLayout(pn_roomInfo, BoxLayout.Y_AXIS));
		
		/********************************************/
		JPanel scheP = new JPanel();
		DTM_Room = new MyTableModel(data_roomTimetable, col_roomTimetable);
		table_Room = new JTable(DTM_Room);
		JScrollPane sp_RoomTimetalbe = new JScrollPane(table_Room);
		
		sp_RoomTimetalbe.setPreferredSize(new java.awt.Dimension(650, 500));
		table_Room.setColumnSelectionAllowed(true);
		table_Room.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table_Room.setRowHeight(35);
		TableColumn column = table_Room.getColumnModel().getColumn(0);
		column.setPreferredWidth(30);
		table_Room.getColumn("시간").setPreferredWidth(95);
		if(info.is_A) table_Room.addMouseListener(this);
		
		scheP.add("Center",sp_RoomTimetalbe);
					
		/********************************************/
		JPanel buttonP = new JPanel();
		JButton okJButton = new JButton("확인");
		buttonP.add("South", okJButton);
		
		pn_roomInfo.add(scheP);
		pn_roomInfo.add(buttonP);
		
		
		add(pn_roomInfo,"Center");
		
	}
	
	public void addRoomPop(){
		/**처음으로 생성하는 강의실 추가 프레임만 새로 생성**/
		if(Fr_addRoom == null){
			Fr_addRoom = new JFrame("강의실 추가");
			
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder("강의실 추가"));
			panel.setLayout(new FlowLayout());
			
			/******************************************/
			JLabel buildL = new JLabel("건물");
			tf_building = new JTextField(5);
			
			panel.add(buildL);
			panel.add(tf_building);
			
			/******************************************/
			JLabel roomL = new JLabel("호수");
			tf_room = new JTextField(5);
			
			panel.add(roomL);
			panel.add(tf_room);
		
			/*****************************************/
			JLabel seatL = new JLabel("수용인원");
			tf_maxSeat = new JTextField(5);
			
			panel.add(seatL);
			panel.add(tf_maxSeat);
			
			/*****************************************/
			
			panel.add(bt_popAdd);
			bt_popAdd.addActionListener(new adminListener());
			
			/****************************************/
			
			Fr_addRoom.add(panel);
			Fr_addRoom.setSize(400, 100);
			Fr_addRoom.setLocationRelativeTo(pn_listButton);
		}
		Fr_addRoom.setVisible(true);
	}
	
	public void addSchedulePop(int dayIndex, int sTimeIndex, int eTimeIndex)
	{
		Fr_addSchedule = new JFrame("스케쥴 추가");
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		/*****************************************/
		JPanel panel1 = new JPanel();
		panel1.setBorder(new TitledBorder("입력할 정보 선택"));
		tableJRadioButton = new JRadioButton("정규시간표",true);
		eventJRadioButton = new JRadioButton("기타 일정",false);
		tableJRadioButton.addMouseListener(this);
		eventJRadioButton.addMouseListener(this);
		
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(tableJRadioButton);
		radioGroup.add(eventJRadioButton);
		
		panel1.add(tableJRadioButton);
		panel1.add(eventJRadioButton);
		
		/*****************************************/
		JPanel dateP = new JPanel();
		JLabel dateL = new JLabel("요일");
		String[] dates = {"월","화","수","목","금","토","일"};
		dateC = new JComboBox(dates);
		dateC.setSelectedIndex(dayIndex-1);
		dateC.setMaximumRowCount(3);
		
		dateP.add(dateL);
		dateP.add(dateC);
		
		JLabel timeL = new JLabel("시간");
		String[] Time = {"09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30","17:00","17:30","18:00"};
		timeC1 = new JComboBox(Time);
		timeC1.setSelectedIndex(sTimeIndex);
		timeC2 = new JComboBox(Time);
		timeC2.setSelectedIndex(eTimeIndex+1);
		
		dateP.add(timeL);
		dateP.add(timeC1);
		dateP.add(timeC2);
		
		/*****************************************/
		JPanel scheP = new JPanel();
		lb_code = new JLabel("과목코드");
		tf_code = new JTextField(8);
		
		scheP.add(lb_code);
		scheP.add(tf_code);
		
		lb_name = new JLabel("과목명");
		tf_name = new JTextField(10);
		
		scheP.add(lb_name);
		scheP.add(tf_name);
		
		lb_prof = new JLabel("담당교수");
		cb_prof = new JComboBox();
		cb_prof_id = new JComboBox();
		
		Statement profState;
		ResultSet profResult;
		try {
			profState=conn.createStatement();
			profResult = profState.executeQuery("select name, id from member where type='P'");
			while(profResult.next()) {
				cb_prof.addItem(profResult.getString("name"));
				cb_prof_id.addItem(profResult.getString("id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		scheP.add(lb_prof);
		scheP.add(cb_prof);
		
		/******************************************/
		JPanel buttonP = new JPanel();
		enterB.addActionListener(new adminListener());
		cancelB.addActionListener(new adminListener());
		
		buttonP.add(enterB);
		buttonP.add(cancelB);
		
		/******************************************/
		panel.add(panel1);
		panel.add(dateP);
		panel.add(scheP);
		panel.add(buttonP);
		
		Fr_addSchedule.add(panel);
		Fr_addSchedule.setSize(450, 250);
		Fr_addSchedule.setVisible(true);
	}
	
	public void listRoom() {
		Statement scheduleState;
		ResultSet scheduleResult;
		System.out.println("listRoom");
		
		Vector vRoomListHead = new Vector();
		vRoomListHead.addElement("건물");
		vRoomListHead.addElement("호수");	
		vRoomListHead.addElement("인원");	
		Vector vRoomList = new Vector();
		Vector vRoomListCol;
		try {
			scheduleState = conn.createStatement();
			scheduleResult = scheduleState.executeQuery("select * from classroom order by location");
			while(true){
				if(scheduleResult.next()){
					vRoomListCol = new Vector();
					vRoomListCol.addElement(scheduleResult.getString("location"));
					vRoomListCol.addElement(scheduleResult.getString("no"));		
					vRoomListCol.addElement(scheduleResult.getString("maxSeat"));
					vRoomList.addElement(vRoomListCol);
				}
				else{
					DefaultTableModel DTM2 = new DefaultTableModel(vRoomList, vRoomListHead);
					table.setModel(DTM2);
					table.getModel().addTableModelListener(new ChangeListener());
					table.revalidate();
					table.repaint();
					break;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void InitializeTable(String location, String roomNo) {
		Statement scheduleState, blockState;
		ResultSet scheduleResult, blockResult;
	
		int row;
		int col;
		int i =0;
		
		MyTableModel DTM_Room2 = new MyTableModel(data_roomTimetable, col_roomTimetable);
		table_Room.setModel(DTM_Room2);
		table_Room.setColumnSelectionAllowed(true);
		table_Room.setRowHeight(35);
		TableColumn column = table_Room.getColumnModel().getColumn(0);
		column.setPreferredWidth(30);
		table_Room.getColumn("시간").setPreferredWidth(95);
		tableCellCenter(table_Room);

		table_Room.revalidate();
		//table_Room.repaint();
		schedule_no = new String[20][20];
		
		try {
			blockState=conn.createStatement();
			blockResult = blockState.executeQuery("select * from timeblock where location='" +location+ "' and classroom='"+roomNo+"' and isAvailable='F'");
			System.out.println(location + roomNo);
			while(blockResult.next()) {
				scheduleState=conn.createStatement();
				scheduleResult = scheduleState.executeQuery("select * from schedule where no='" +blockResult.getString("scheduleNo")+ "'");
				row = enums.BlockToIndex(blockResult.getString("time"));
				col = enums.DayToIndex(blockResult.getString("day"));
				schedule_no[row][col] = blockResult.getString("scheduleNo");
				while(scheduleResult.next()) {
					System.out.println("schedule");
					table_Room.setValueAt(scheduleResult.getString("name"), row, col);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void tableCellColor(JTable t, int row, int col) // 셀 내용 안비었으면 색칠
	{
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setBackground(Color.yellow);
		TableColumnModel tcm = t.getColumnModel();
		tcm.getColumn(col).setCellRenderer(dtcr);
	}
	
	public void tableCellCenter(JTable t) // 셀 내용 가운데 정렬
	{
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);		
		TableColumnModel tcm = t.getColumnModel();		
		for(int i = 0; i<tcm.getColumnCount();i++)
		{
			tcm.getColumn(i).setCellRenderer(dtcr);
		}
	}
	
	public void mouseClicked(MouseEvent me) {
		if(me.getSource()==table) {
			this.nowListRow = table.getSelectedRow();
			this.nowListCol = table.getSelectedColumn();
			this.nowListValue = table.getValueAt(nowListRow, nowListCol);
			this.nowRowB = table.getValueAt(nowListRow, 0);
			this.nowRowC = table.getValueAt(nowListRow, 1);
			this.nowRowM = table.getValueAt(nowListRow, 2);
			System.out.println(nowListRow+","+nowListCol+","+nowListValue.toString()+"selected");
			InitializeTable(table.getValueAt(nowListRow, 0).toString(), table.getValueAt(nowListRow, 1).toString());
			System.out.println(table.getValueAt(nowListRow, 0).toString()+","+ table.getValueAt(nowListRow, 1).toString());
		} else if(me.getSource()==table_Room) {
			if(schedule_no[table_Room.getSelectedRow()][table_Room.getSelectedColumn()]!=null)
				PopUpMenu(me, schedule_no[table_Room.getSelectedRow()][table_Room.getSelectedColumn()]);
		}
		else if(me.getSource()==tableJRadioButton)
		{
			regularOrEtc = 0;
			lb_name.setText("과목명");
			lb_code.setVisible(true);
			tf_code.setVisible(true);
			lb_prof.setVisible(true);
			cb_prof.setVisible(true);
		}
		else if(me.getSource()==eventJRadioButton)
		{
			regularOrEtc = 1;
			lb_name.setText("일정제목");
			lb_code.setVisible(false);
			tf_code.setVisible(false);
			lb_prof.setVisible(false);
			cb_prof.setVisible(false);
		}
	}
	public void mouseEntered(MouseEvent Ee) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent pe) {
		if(pe.getSource()==table_Room) {
			this.dragStartRow = table_Room.getSelectedRow();
			this.dragStartCol = table_Room.getSelectedColumn();
			table_Room.setSelectionBackground(Color.yellow);
			System.out.print("("+dragStartRow+"행,"+dragStartCol+"열"+")");
		}
	}
	public void mouseReleased(MouseEvent re) {
		if(re.getSource()==table_Room) {
			this.dragEndRow = table_Room.getSelectedRow();
			if(dragEndRow!=dragStartRow) addSchedulePop(dragStartCol, dragStartRow, dragEndRow);			
			System.out.println("("+dragEndRow+"행,"+dragStartCol+"열"+")");
		}
	}
	public void mouseDragged(MouseEvent me){}
	
	class ChangeListener implements TableModelListener {
		public void tableChanged(TableModelEvent e) { 
	        System.out.println(table.getValueAt(nowListRow, nowListCol).toString());
	        if(!nowListValue.equals(table.getValueAt(nowListRow, nowListCol))) {
	       	 if(JOptionPane.showConfirmDialog(null,"강의실 정보를 변경 하시겠습니까?","강의실 정보 변경",
							JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)!=JOptionPane.NO_OPTION)
					{
	       		 		String changedRowB = table.getValueAt(nowListRow, 0).toString();
	       		 		String changedRowC = table.getValueAt(nowListRow, 1).toString();
	       		 		String changedRowM = table.getValueAt(nowListRow, 2).toString();
	       		 		System.out.printf("%s %s %s %s %s %s",nowRowB.toString(), nowRowC.toString(), nowRowM.toString(), changedRowB, changedRowC, changedRowM);
	       		 		admin.SetClassRoom(nowRowB.toString(), nowRowC.toString(), nowRowM.toString(), changedRowB, changedRowC, changedRowM);
	       		 		
					} else {
						table.setValueAt(nowListValue, nowListRow, nowListCol);
						System.out.println("변경안됨");
					}
	        }
	    }
	}
	
	class adminListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == bt_addRoom) {
				System.out.println("bt_addRoom Clicked");
				addRoomPop();
			}
			
			if(e.getSource() == bt_delRoom) {
				System.out.println(" bt_delRoomClicked");
				admin.DeleteClassRoom(table.getValueAt(nowListRow, 1).toString(), table.getValueAt(nowListRow, 0).toString());
				listRoom();
			}
			
			if(e.getSource() == bt_popAdd) {
				System.out.println("bt_popAdd Clicked");
				if(!tf_building.getText().startsWith("E")&&!tf_building.getText().startsWith("I"))
						JOptionPane.showMessageDialog(null, "건물을 잘못 입력하셨습니다.");
				else if(tf_room.getText().startsWith("E")||tf_room.getText().startsWith("I"))
						JOptionPane.showMessageDialog(null, "강의실을 잘못 입력하셨습니다.");
				
				else if(tf_building.getText()!=null&&tf_room.getText()!=null&&tf_maxSeat.getText()!=null) {
					admin.CreateClassRoom(tf_room.getText(), tf_building.getText(), tf_maxSeat.getText());
					listRoom();
					Fr_addRoom.setVisible(false);
				}				
			}
			
			if(e.getSource() == enterB) {
				int scheNo;
				System.out.println("enterB Clicked");
				if(regularOrEtc==0){
					System.out.println("업");
					scheNo = admin.CreateLectureSchedule(tf_name.getText(), cb_prof_id.getItemAt(cb_prof.getSelectedIndex()).toString(), tf_code.getText());
				}
				else{
					System.out.println("다운");
					scheNo = admin.CreateOtherSchedule(tf_name.getText());
				}
				
				int start_time = enums.TimeToIndex(timeC1.getSelectedItem().toString());
				int end_time = enums.TimeToIndex(timeC2.getSelectedItem().toString());
				int i=start_time;
				
				System.out.println("returned schNo : "+scheNo);
				for(i=start_time; i<end_time; i++) {
					System.out.printf("%d %s %d",nowListRow, dateC.getSelectedItem().toString(), scheNo);
					admin.CreateTimeBlock(cb_prof_id.getItemAt(cb_prof.getSelectedIndex()).toString(), table.getValueAt(nowListRow, 0).toString(), table.getValueAt(nowListRow, 1).toString(), dateC.getSelectedItem().toString(), enums.IndexToBlock[i], scheNo);
				}
				Fr_addSchedule.setVisible(false);
				InitializeTable(table.getValueAt(nowListRow, 0).toString(), table.getValueAt(nowListRow, 1).toString());
			}
			
			if(e.getSource() == cancelB) {
				Fr_addSchedule.setVisible(false);
			}
		}
	}
	
	public void PopUpMenu(MouseEvent ce, final String schedule){
		JPopupMenu Pmenu;
		Pmenu = new JPopupMenu();
		final JMenuItem modifySchedule = new JMenuItem("Sche No. "+schedule);
		Pmenu.add(modifySchedule);
		modifySchedule.setEnabled(false);
		final JMenuItem deleteSchedule = new JMenuItem("삭제하기");
		Pmenu.add(deleteSchedule);
		Pmenu.show(ce.getComponent(), ce.getX(), ce.getY());
		deleteSchedule.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(e.getSource()==deleteSchedule) {
					if(JOptionPane.showConfirmDialog(null,"일정을 삭제 하시겠습니까?", schedule,
							JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)!=JOptionPane.NO_OPTION)
					{
						admin.DeleteSchedule(schedule);
						InitializeTable(table.getValueAt(nowListRow, 0).toString(), table.getValueAt(nowListRow, 1).toString());
					} 
				}
			}
		});
   }
}

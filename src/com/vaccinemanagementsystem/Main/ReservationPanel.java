package com.vaccinemanagementsystem.Main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.JFileChooser; 
import javax.swing.filechooser.FileNameExtensionFilter;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class ReservationPanel implements MouseListener, ActionListener {
	//예약자 관리 페이지 객체
	
	private final String searchInfoTypes[] = {"예약자 이름", "주민등록번호", "백신 코드", "예약 날자", "접종 차수 정보", "센터 코드"};	//DB테이블 컬럼내용으로 받도록 수정해야함.
	private final String areaNames[] = {"전체", "서울", "경기", "강원", "충청", "전라", "경상", "인천", "제주"};
	private final String infoTypes[] = {"주민등록번호", "예약자 명", "백신 명", "예약 날짜", "접종 차수", "접종센터"};
	private Vector centerTitle;
	private Vector reservationTitle;
	private Vector centerOuter;
	private Vector reservationOuter;
	private JLabel centerLabel = new JLabel("접종 센터 목록");
	private JLabel reserveListLabel = new JLabel("예약자 목록");
	private JLabel infoLabel[] = new JLabel[6];
	private JFrame frame = new JFrame();
	private JPanel leftPanel = new JPanel();
	private JPanel leftCenterPanel = new JPanel();
	private JPanel leftTextPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel underPanel = new JPanel();
	private JPanel rightPanel = new JPanel();
	private JPanel rightButtonPanel = new JPanel();
	private JPanel rightInfoPanel = new JPanel();
	private JPanel selectedInfo[] = new JPanel[6];
	private JButton searchButton = new JButton("검색");
	private JButton allSearchButton = new JButton("전체 조회");
	private JButton reserveCancelButton = new JButton("예약 취소");
	private JButton reserveAddButton = new JButton("예약 추가");
	private JButton filePrintButton = new JButton("파일로 변환");
	private CustomTableModel centerTableModel = new CustomTableModel();
	private CustomTableModel reserveTableModel = new CustomTableModel();
	private JTable centerTable = new JTable(centerTableModel);
	private JTable reservationTable = new JTable(reserveTableModel);
	private JScrollPane centerScrollPane = new JScrollPane(centerTable);
	private JScrollPane reserveScrollPane = new JScrollPane(reservationTable);
	private JTextField reserveSearch = new JTextField();
	private JTextField infoTextField[] = new JTextField[6];
	private JComboBox<String> reserveCombo = new JComboBox<String>(searchInfoTypes);
	private JComboBox<String> centerCombo = new JComboBox<String>(areaNames);
	private CalendarPanel calendar = new CalendarPanel();
	private Container container = frame.getContentPane();
	private Database db = new Database();
	
	public ReservationPanel() {
		db.connect();
		initGUI();
	}
	
	private void initGUI() {
		LocalDate now = LocalDate.now();	//현재 날짜 확인
		
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		
		calendar.setDate(year, month, day);	//캘린더에 현재날짜를 표시

		
		// 모든 이벤트 발생 버튼에 ActionListner 등록
		searchButton.addActionListener(this);
		allSearchButton.addActionListener(this);
		reserveCancelButton.addActionListener(this);
		reserveAddButton.addActionListener(this);
		filePrintButton.addActionListener(this);
		centerCombo.addActionListener(this);
		reserveCombo.addActionListener(this);
		
		leftPanel.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
		leftPanel.setLayout(new BorderLayout(0, 20));
		leftPanel.add(calendar.getCalendar(), BorderLayout.NORTH); //왼쪽에 캘린더 부착
		
		leftTextPanel.setLayout(new BorderLayout(20, 0));
		centerCombo.setPreferredSize(new Dimension(100, 25));
		leftTextPanel.add(centerLabel, BorderLayout.WEST);
		leftTextPanel.add(centerCombo, BorderLayout.EAST); //캘린더 아래에 라벨, 콤보박스 부착 (center Combo의 center는 말그대로 센터를 의미)
		//
		leftCenterPanel.setLayout(new BorderLayout(0, 5));
		leftCenterPanel.add(leftTextPanel, BorderLayout.NORTH);
		centerTable.getTableHeader().setBackground(new Color(192, 192, 192));
		centerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		centerScrollPane.setPreferredSize(new Dimension(300, 500));
		initCenterTable();
		centerTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				initReservationTable();
			}
		});
		leftCenterPanel.add(centerScrollPane, BorderLayout.CENTER);
		leftPanel.add(leftCenterPanel, BorderLayout.CENTER); //테이블 부착.
		
		centerPanel.setBorder(BorderFactory.createEmptyBorder(13, 0, 13, 0));
		centerPanel.setLayout(new BorderLayout(0, 10));
		reservationTable.getTableHeader().setBackground(new Color(192, 192, 192));
		reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reserveScrollPane.setPreferredSize(new Dimension(150, 500));
		initReservationTable();
		reservationTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Vector<String[]> data = new Vector<String[]>();
				String query = "SELECT centerName FROM CenterInfo WHERE centerCode = \"";
				String vaccineName = "";
		        int centerIdx = centerTable.getSelectedRow();
		        int reserveIdx = reservationTable.getSelectedRow();
		        
		        infoTextField[0].setText((String)reservationTable.getValueAt(reserveIdx, 1));
		        infoTextField[1].setText((String)reservationTable.getValueAt(reserveIdx, 0));
		        switch(((String)reservationTable.getValueAt(reserveIdx, 2)).charAt(2)) {
		        case '1': vaccineName = "화이자"; break;
		        case '2': vaccineName = "모더나"; break;
		        case '3': vaccineName = "아스트라제네카"; break;
		        case '4': vaccineName = "얀센"; break;
		        }
		        infoTextField[2].setText(vaccineName);
		        infoTextField[3].setText((String)reservationTable.getValueAt(reserveIdx, 3));
		        infoTextField[4].setText((String)reservationTable.getValueAt(reserveIdx, 4));
		        
		        data = db.getData(query + (String)reservationTable.getValueAt(reserveIdx, 5) + "\"", 1);
		        infoTextField[5].setText(data.get(0)[0]);
			}
		});
		centerPanel.add(reserveListLabel, BorderLayout.NORTH);
		centerPanel.add(reserveScrollPane, BorderLayout.CENTER); //가운데에 라벨과 테이블 부착
		
		underPanel.setLayout(new BorderLayout(20, 0));
		reserveCombo.setPreferredSize(new Dimension(150, 25));
		searchButton.setPreferredSize(new Dimension(150, 15));
		underPanel.add(reserveCombo, BorderLayout.WEST);
		underPanel.add(reserveSearch, BorderLayout.CENTER);
		underPanel.add(searchButton, BorderLayout.EAST);
		centerPanel.add(underPanel, BorderLayout.SOUTH); //테이블 아래에 검색창 부착
		
		rightButtonPanel.setLayout(new GridLayout(5, 1, 0, 20));
		allSearchButton.setPreferredSize(new Dimension(250, 50));
		reserveCancelButton.setPreferredSize(new Dimension(250, 50));
		reserveAddButton.setPreferredSize(new Dimension(250, 50));
		filePrintButton.setPreferredSize(new Dimension(250, 50));
		allSearchButton.setFont(new Font("SansSerif", Font.BOLD, 20));
		reserveCancelButton.setFont(new Font("SansSerif", Font.BOLD, 20));
		reserveAddButton.setFont(new Font("SansSerif", Font.BOLD, 20));
		filePrintButton.setFont(new Font("SansSerif", Font.BOLD, 20));
		rightButtonPanel.add(allSearchButton);
		rightButtonPanel.add(reserveCancelButton);
		rightButtonPanel.add(reserveAddButton);
		rightButtonPanel.add(filePrintButton); //오른쪽에 버튼4개 부착 및 폰트수정, 크기조정
		
		rightInfoPanel.setLayout(new GridLayout(6, 1, 0, 5));
		for(int idx = 0; idx < 6; idx++) {
			infoLabel[idx] = new JLabel(infoTypes[idx]);
			infoLabel[idx].setFont(new Font("SansSerif", Font.BOLD, 14));
			infoTextField[idx] = new JTextField();
			infoTextField[idx].setEditable(false);
			infoTextField[idx].setPreferredSize(new Dimension(150, 25));
			
			selectedInfo[idx] = new JPanel();
			selectedInfo[idx].setLayout(new BorderLayout(20, 0));
			selectedInfo[idx].add(infoLabel[idx], BorderLayout.WEST);
			selectedInfo[idx].add(infoTextField[idx], BorderLayout.EAST);
			rightInfoPanel.add(selectedInfo[idx]);
		}
		//버튼 아래에 선택된값 패널 부착
		
		rightPanel.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
		rightPanel.setLayout(new BorderLayout(0, 10));
		rightPanel.add(rightButtonPanel, BorderLayout.NORTH);
		rightPanel.add(rightInfoPanel, BorderLayout.CENTER);
		//해당 패널 배치
		
		container.add(leftPanel, BorderLayout.WEST);
		container.add(centerPanel, BorderLayout.CENTER);
		container.add(rightPanel, BorderLayout.EAST);
		//reservation페이지 완성
	}
	
	private void initCenterTable() {
		String comboValue = (String)centerCombo.getSelectedItem();
		String query = "SELECT * FROM CenterInfo";
		Vector<String[]> data;
		
		centerTitle = new Vector<>();
		
		centerTitle.add("접종 센터명");
		centerTitle.add("센터 코드");
		centerTitle.add("주소");
		centerTitle.add("지역");
		
		centerOuter = new Vector<>();
		
		//데이터 넣기
		if(comboValue.equals("전체") == true) {
			data = db.getData(query, 4);
		} else {
			data = db.getData(query + " WHERE region = " + "\"" + comboValue + "\"", 4);
		}
		
		for(String[] values : data) {
			centerOuter.add(changeVector(values));
		}
		
		// JTable 관리자로 테이블 그리기
		centerTableModel.setDataVector((Vector)centerOuter, (Vector)centerTitle);
		
		// Table의 Sort 기능 활성화
		centerTable.setAutoCreateRowSorter(true);
		TableRowSorter<CustomTableModel> tableSorter = new TableRowSorter(centerTableModel);
		centerTable.setRowSorter(tableSorter);
		
		
		DefaultTableCellRenderer cellAlignCenter = new DefaultTableCellRenderer();
		cellAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		
		centerTable.getColumn("접종 센터명").setCellRenderer(cellAlignCenter);
		centerTable.getColumn("센터 코드").setCellRenderer(cellAlignCenter);
		centerTable.getColumn("주소").setCellRenderer(cellAlignCenter);
		centerTable.getColumn("지역").setCellRenderer(cellAlignCenter);
		
		centerTable.setBackground(new Color(255, 255, 255));
	}
	
	private void initReservationTable() {
		String query = "SELECT * FROM Reservation";
		Vector<String[]> data;
		
		reservationTitle = new Vector<>();
		
		reservationTitle.add("예약자 이름");
		reservationTitle.add("주민등록번호");
		reservationTitle.add("백신 코드");
		reservationTitle.add("예약 날짜");
		reservationTitle.add("접종 차수 정보");
		reservationTitle.add("센터 코드");
		
		reservationOuter = new Vector<>();
		
		//데이터 넣기
		if(centerTable.getSelectedRow() < 0) {
			data = db.getData(query, 6);
		} else {
			int centerIdx = centerTable.getSelectedRow();
			
			data = db.getData(query + " WHERE centerCode = " + "\"" 
					+ (String)centerTable.getValueAt(centerIdx, 1) + "\"", 6);
		}
		
		for(String[] values : data) {
			reservationOuter.add(changeVector(values));
		}
		
		// JTable 관리자로 테이블 그리기
		reserveTableModel.setDataVector((Vector)reservationOuter, (Vector)reservationTitle);
		
		// Table의 Sort 기능 활성화
		reservationTable.setAutoCreateRowSorter(true);
		TableRowSorter<CustomTableModel> tableSorter = new TableRowSorter(reserveTableModel);
		
		// 숫자일 경우 숫자로 비교하도록 Comparator 구현 (Override)
		Comparator<String> comparator = new Comparator<String>() {
        	@Override
            public int compare(String o1, String o2)  {
            	if (o1 == null) return -1;
                if (o2 == null) return 1;
                return Integer.valueOf(o2) - Integer.valueOf(o1);
            }
        };
        tableSorter.setComparator(4, comparator);	
		reservationTable.setRowSorter(tableSorter);
		
		DefaultTableCellRenderer cellAlignCenter = new DefaultTableCellRenderer();
		cellAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		
		
		reservationTable.getColumn("예약자 이름").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("주민등록번호").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("백신 코드").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("예약 날짜").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("접종 차수 정보").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("센터 코드").setCellRenderer(cellAlignCenter);
		
		reservationTable.setBackground(new Color(255, 255, 255));
	}
	
	private void setEntireTable() {
		initCenterTable();
		initReservationTable();
	}
	
	private void searchInReserveTable() {
		String value = (String)reserveCombo.getSelectedItem();
		
		String keyword = reserveSearch.getText();
		Vector<Vector> result = new Vector<>();
		
		int idx = 0;
		if(value.equals("예약자 이름")) {
			idx = 0;
		} else if(value.equals("주민등록번호")) {
			idx = 1;
		} else if(value.equals("백신 코드")) {
			idx = 2;
		} else if(value.equals("예약 날짜")) {
			idx = 3;
		} else if(value.equals("접종 차수 정보")) {
			idx = 4;
		} else if(value.equals("센터 코드")) {
			idx = 5;
		}
		for(Object in : reservationOuter) {
			Vector<String> tmp = (Vector<String>) in;
			if(keyword.equals(tmp.get(idx))) {
				result.add(tmp);
			}
		}
		if(result.size() > 0) {
			reserveTableModel.setDataVector(result, (Vector)reservationTitle);
		} else {
			showMessage("검색 결과가 없습니다.");
		}
	}
	
	private void deleteSelectedInfo() {
		int index = reservationTable.getSelectedRow();
		
		if(index < 0) {
			showMessage("삭제할 행을 선택해주세요.");
		} else {
			String reserveName = String.valueOf(reservationTable.getValueAt(index, 0));
			String reserveID = String.valueOf(reservationTable.getValueAt(index, 1));
			int check = JOptionPane.showConfirmDialog(null, "사용자 " + reserveName + "(" + reserveID
					+ ") 의 예약을 취소하시겠습니까?");
			
			if(check == 0) {
				String query = "DELETE FROM Reservation WHERE reservatorName = \"" + reserveName
						+ "\" AND socialSecurityNo = \"" + reserveID + "\"";
				db.queryToDB(query);
				centerTableModel.removeRow(index);
				initReservationTable();
				System.out.println("Reservation Canceled");
			}
		}
	}
	
	private void addInfo() {
		JDialog addDialog = new JDialog();
		JTextField inputData[] = new JTextField[6];
		JPanel inputPanel[] = new JPanel[7];
		JLabel inputLabel[] = new JLabel[6];
		JPanel addPanel = new JPanel();
		JButton confirmButton = new JButton("추가");
		JButton cancelButton = new JButton("취소");
		
		addDialog.setTitle("예약자 추가");
		addDialog.setSize(400, 350);
		inputLabel[0] = new JLabel("예약자 명");
		inputLabel[1] = new JLabel("주민등록번호");
		inputLabel[2] = new JLabel("백신 코드");
		inputLabel[3] = new JLabel("예약 날짜");
		inputLabel[4] = new JLabel("접종 차수");
		inputLabel[5] = new JLabel("센터 코드");
		addPanel.setLayout(new GridLayout(7, 1, 5, 15));
		addPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		for(int i = 0; i < 6; i++) {
			inputData[i] = new JTextField(20);
			inputPanel[i] = new JPanel();
			inputPanel[i].setLayout(new BorderLayout());
			inputPanel[i].add(inputLabel[i], BorderLayout.WEST);
			inputPanel[i].add(inputData[i], BorderLayout.EAST);
			
			addPanel.add(inputPanel[i]);
		}
		inputPanel[6] = new JPanel();
		inputPanel[6].setLayout(new BorderLayout());
		confirmButton.setPreferredSize(new Dimension(150, 35));
		cancelButton.setPreferredSize(new Dimension(150, 35));
		inputPanel[6].add(confirmButton, BorderLayout.WEST);
		inputPanel[6].add(cancelButton, BorderLayout.EAST);
		
		addPanel.add(inputPanel[6]);
		
		addDialog.add(addPanel);
		addDialog.setLocationRelativeTo(container);
		addDialog.setVisible(true);
		
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean checkNull = true;
				
				for(int i = 0; i < 6; i++) {
					System.out.print("|" + inputData[i].getText());
					if(inputData[i].getText().equals("")) {
						checkNull = false;
						break;
					}	
				}
				
				if(checkNull == false) {
					showMessage("모든 값을 입력해주세요.");
				} else {
					String temp = "INSERT INTO Reservation VALUES (\""
							+ inputData[0].getText() + "\", \""
							+ inputData[1].getText() + "\", \""
							+ inputData[2].getText() + "\", \""
							+ inputData[3].getText() + "\", \""
							+ inputData[4].getText() + "\", \""
							+ inputData[5].getText() + "\")";
					db.queryToDB(temp);
					initReservationTable();
					addDialog.setVisible(false);
				}
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDialog.setVisible(false);
			}
		});
	}
	
	private void changeFile() {
		JFileChooser jfc = new JFileChooser();
	        
	    FileNameExtensionFilter filterTxt = new FileNameExtensionFilter(".txt", "txt");
	    FileNameExtensionFilter filterXlxs = new FileNameExtensionFilter(".CSV","csv");
	
	    jfc.addChoosableFileFilter(filterTxt);
	    jfc.addChoosableFileFilter(filterXlxs);
	        
	    int returnVal = jfc.showSaveDialog(null);
	      
	    if(returnVal==0) {
	    	File file = jfc.getSelectedFile();
	
	         
	        try {
		        BufferedWriter bw = new BufferedWriter(
		        		new FileWriter(file.getAbsolutePath()+jfc.getFileFilter().getDescription().replace("All Files", ""))
		        		);
		           
		        Vector<Vector> tempData = reserveTableModel.getDataVector();
		        for(Vector<String> values : tempData) {
		        	bw.write((values+"\n").replaceAll("\\[","").replaceAll("\\]", ""));
		        }
		        bw.flush();
		        bw.close();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
	    }
	}
	
	
	
	private Vector<String> changeVector(String[] array) {
		Vector<String> in = new Vector<>();
		
		for(String data : array){
			in.add(data);
		}		
		return in;
	}
	
	private void showMessage(String message) {
		JOptionPane.showMessageDialog(frame, message, "메세지", JOptionPane.INFORMATION_MESSAGE);		
	}
	
	public Container getMainContainer() {
		return container;
	}
	
	public void exit() {
		System.out.println("System EXIT.");
		System.exit(0);
	}
	
	public void actionPerformed(ActionEvent w) {		
		Object o = w.getSource(); // 이벤트가 발생한 객체를 리턴
	
        if(o == allSearchButton) {
        	setEntireTable();
        } else if(o == reserveCancelButton) {
        	deleteSelectedInfo();
        } else if(o == reserveAddButton) {
        	addInfo();
        } else if(o == filePrintButton) {
        	changeFile();
        } else if(o == searchButton) {
        	searchInReserveTable();
        } else if(o == centerCombo) {
        	initCenterTable();
        }
	}
	
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	
}


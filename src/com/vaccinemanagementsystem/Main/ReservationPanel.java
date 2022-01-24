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
	//������ ���� ������ ��ü
	
	private final String searchInfoTypes[] = {"������ �̸�", "�ֹε�Ϲ�ȣ", "��� �ڵ�", "���� ����", "���� ���� ����", "���� �ڵ�"};	//DB���̺� �÷��������� �޵��� �����ؾ���.
	private final String areaNames[] = {"��ü", "����", "���", "����", "��û", "����", "���", "��õ", "����"};
	private final String infoTypes[] = {"�ֹε�Ϲ�ȣ", "������ ��", "��� ��", "���� ��¥", "���� ����", "��������"};
	private Vector centerTitle;
	private Vector reservationTitle;
	private Vector centerOuter;
	private Vector reservationOuter;
	private JLabel centerLabel = new JLabel("���� ���� ���");
	private JLabel reserveListLabel = new JLabel("������ ���");
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
	private JButton searchButton = new JButton("�˻�");
	private JButton allSearchButton = new JButton("��ü ��ȸ");
	private JButton reserveCancelButton = new JButton("���� ���");
	private JButton reserveAddButton = new JButton("���� �߰�");
	private JButton filePrintButton = new JButton("���Ϸ� ��ȯ");
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
		LocalDate now = LocalDate.now();	//���� ��¥ Ȯ��
		
		int year = now.getYear();
		int month = now.getMonthValue();
		int day = now.getDayOfMonth();
		
		calendar.setDate(year, month, day);	//Ķ������ ���糯¥�� ǥ��

		
		// ��� �̺�Ʈ �߻� ��ư�� ActionListner ���
		searchButton.addActionListener(this);
		allSearchButton.addActionListener(this);
		reserveCancelButton.addActionListener(this);
		reserveAddButton.addActionListener(this);
		filePrintButton.addActionListener(this);
		centerCombo.addActionListener(this);
		reserveCombo.addActionListener(this);
		
		leftPanel.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
		leftPanel.setLayout(new BorderLayout(0, 20));
		leftPanel.add(calendar.getCalendar(), BorderLayout.NORTH); //���ʿ� Ķ���� ����
		
		leftTextPanel.setLayout(new BorderLayout(20, 0));
		centerCombo.setPreferredSize(new Dimension(100, 25));
		leftTextPanel.add(centerLabel, BorderLayout.WEST);
		leftTextPanel.add(centerCombo, BorderLayout.EAST); //Ķ���� �Ʒ��� ��, �޺��ڽ� ���� (center Combo�� center�� ���״�� ���͸� �ǹ�)
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
		leftPanel.add(leftCenterPanel, BorderLayout.CENTER); //���̺� ����.
		
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
		        case '1': vaccineName = "ȭ����"; break;
		        case '2': vaccineName = "�����"; break;
		        case '3': vaccineName = "�ƽ�Ʈ������ī"; break;
		        case '4': vaccineName = "�Ἶ"; break;
		        }
		        infoTextField[2].setText(vaccineName);
		        infoTextField[3].setText((String)reservationTable.getValueAt(reserveIdx, 3));
		        infoTextField[4].setText((String)reservationTable.getValueAt(reserveIdx, 4));
		        
		        data = db.getData(query + (String)reservationTable.getValueAt(reserveIdx, 5) + "\"", 1);
		        infoTextField[5].setText(data.get(0)[0]);
			}
		});
		centerPanel.add(reserveListLabel, BorderLayout.NORTH);
		centerPanel.add(reserveScrollPane, BorderLayout.CENTER); //����� �󺧰� ���̺� ����
		
		underPanel.setLayout(new BorderLayout(20, 0));
		reserveCombo.setPreferredSize(new Dimension(150, 25));
		searchButton.setPreferredSize(new Dimension(150, 15));
		underPanel.add(reserveCombo, BorderLayout.WEST);
		underPanel.add(reserveSearch, BorderLayout.CENTER);
		underPanel.add(searchButton, BorderLayout.EAST);
		centerPanel.add(underPanel, BorderLayout.SOUTH); //���̺� �Ʒ��� �˻�â ����
		
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
		rightButtonPanel.add(filePrintButton); //�����ʿ� ��ư4�� ���� �� ��Ʈ����, ũ������
		
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
		//��ư �Ʒ��� ���õȰ� �г� ����
		
		rightPanel.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
		rightPanel.setLayout(new BorderLayout(0, 10));
		rightPanel.add(rightButtonPanel, BorderLayout.NORTH);
		rightPanel.add(rightInfoPanel, BorderLayout.CENTER);
		//�ش� �г� ��ġ
		
		container.add(leftPanel, BorderLayout.WEST);
		container.add(centerPanel, BorderLayout.CENTER);
		container.add(rightPanel, BorderLayout.EAST);
		//reservation������ �ϼ�
	}
	
	private void initCenterTable() {
		String comboValue = (String)centerCombo.getSelectedItem();
		String query = "SELECT * FROM CenterInfo";
		Vector<String[]> data;
		
		centerTitle = new Vector<>();
		
		centerTitle.add("���� ���͸�");
		centerTitle.add("���� �ڵ�");
		centerTitle.add("�ּ�");
		centerTitle.add("����");
		
		centerOuter = new Vector<>();
		
		//������ �ֱ�
		if(comboValue.equals("��ü") == true) {
			data = db.getData(query, 4);
		} else {
			data = db.getData(query + " WHERE region = " + "\"" + comboValue + "\"", 4);
		}
		
		for(String[] values : data) {
			centerOuter.add(changeVector(values));
		}
		
		// JTable �����ڷ� ���̺� �׸���
		centerTableModel.setDataVector((Vector)centerOuter, (Vector)centerTitle);
		
		// Table�� Sort ��� Ȱ��ȭ
		centerTable.setAutoCreateRowSorter(true);
		TableRowSorter<CustomTableModel> tableSorter = new TableRowSorter(centerTableModel);
		centerTable.setRowSorter(tableSorter);
		
		
		DefaultTableCellRenderer cellAlignCenter = new DefaultTableCellRenderer();
		cellAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		
		centerTable.getColumn("���� ���͸�").setCellRenderer(cellAlignCenter);
		centerTable.getColumn("���� �ڵ�").setCellRenderer(cellAlignCenter);
		centerTable.getColumn("�ּ�").setCellRenderer(cellAlignCenter);
		centerTable.getColumn("����").setCellRenderer(cellAlignCenter);
		
		centerTable.setBackground(new Color(255, 255, 255));
	}
	
	private void initReservationTable() {
		String query = "SELECT * FROM Reservation";
		Vector<String[]> data;
		
		reservationTitle = new Vector<>();
		
		reservationTitle.add("������ �̸�");
		reservationTitle.add("�ֹε�Ϲ�ȣ");
		reservationTitle.add("��� �ڵ�");
		reservationTitle.add("���� ��¥");
		reservationTitle.add("���� ���� ����");
		reservationTitle.add("���� �ڵ�");
		
		reservationOuter = new Vector<>();
		
		//������ �ֱ�
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
		
		// JTable �����ڷ� ���̺� �׸���
		reserveTableModel.setDataVector((Vector)reservationOuter, (Vector)reservationTitle);
		
		// Table�� Sort ��� Ȱ��ȭ
		reservationTable.setAutoCreateRowSorter(true);
		TableRowSorter<CustomTableModel> tableSorter = new TableRowSorter(reserveTableModel);
		
		// ������ ��� ���ڷ� ���ϵ��� Comparator ���� (Override)
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
		
		
		reservationTable.getColumn("������ �̸�").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("�ֹε�Ϲ�ȣ").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("��� �ڵ�").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("���� ��¥").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("���� ���� ����").setCellRenderer(cellAlignCenter);
		reservationTable.getColumn("���� �ڵ�").setCellRenderer(cellAlignCenter);
		
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
		if(value.equals("������ �̸�")) {
			idx = 0;
		} else if(value.equals("�ֹε�Ϲ�ȣ")) {
			idx = 1;
		} else if(value.equals("��� �ڵ�")) {
			idx = 2;
		} else if(value.equals("���� ��¥")) {
			idx = 3;
		} else if(value.equals("���� ���� ����")) {
			idx = 4;
		} else if(value.equals("���� �ڵ�")) {
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
			showMessage("�˻� ����� �����ϴ�.");
		}
	}
	
	private void deleteSelectedInfo() {
		int index = reservationTable.getSelectedRow();
		
		if(index < 0) {
			showMessage("������ ���� �������ּ���.");
		} else {
			String reserveName = String.valueOf(reservationTable.getValueAt(index, 0));
			String reserveID = String.valueOf(reservationTable.getValueAt(index, 1));
			int check = JOptionPane.showConfirmDialog(null, "����� " + reserveName + "(" + reserveID
					+ ") �� ������ ����Ͻðڽ��ϱ�?");
			
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
		JButton confirmButton = new JButton("�߰�");
		JButton cancelButton = new JButton("���");
		
		addDialog.setTitle("������ �߰�");
		addDialog.setSize(400, 350);
		inputLabel[0] = new JLabel("������ ��");
		inputLabel[1] = new JLabel("�ֹε�Ϲ�ȣ");
		inputLabel[2] = new JLabel("��� �ڵ�");
		inputLabel[3] = new JLabel("���� ��¥");
		inputLabel[4] = new JLabel("���� ����");
		inputLabel[5] = new JLabel("���� �ڵ�");
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
					showMessage("��� ���� �Է����ּ���.");
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
		JOptionPane.showMessageDialog(frame, message, "�޼���", JOptionPane.INFORMATION_MESSAGE);		
	}
	
	public Container getMainContainer() {
		return container;
	}
	
	public void exit() {
		System.out.println("System EXIT.");
		System.exit(0);
	}
	
	public void actionPerformed(ActionEvent w) {		
		Object o = w.getSource(); // �̺�Ʈ�� �߻��� ��ü�� ����
	
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


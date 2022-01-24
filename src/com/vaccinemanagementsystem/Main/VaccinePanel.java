package com.vaccinemanagementsystem.Main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class VaccinePanel implements MouseListener, ActionListener {
	//��� ��� ���� ������ ��ü
	
	private final String searchInfoTypes[] = {"���� ���� �ڵ�", "��� �ڵ�", "��� ���"};
	private final String areaNames[] = {"��ü", "����", "���", "����", "��û", "����", "���", "��õ", "����"};
	private final String infoTypes[] = {"��� ��", "���� ��", "��� ���"};
	private Vector centerTitle;
	private Vector vaccineTitle;
	private Vector vaccineStackTitle;
	private Vector centerOuter;
	private Vector vaccineOuter;
	private Vector vaccineStackOuter;
	private JLabel centerLabel = new JLabel("���� ���� ���");
	private JLabel vaccineListLabel = new JLabel("��� ���");
	private JLabel vaccineStackListLabel = new JLabel("��� ��� ���");
	private JFrame frame = new JFrame();
	private JLabel infoLabel[] = new JLabel[6];
	private JPanel leftPanel = new JPanel();
	private JPanel leftCenterPanel = new JPanel();
	private JPanel leftComboPanel = new JPanel();
	private JPanel leftVaccinePanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel underPanel = new JPanel();
	private JPanel rightPanel = new JPanel();
	private JPanel rightButtonPanel = new JPanel();
	private JPanel rightInfoPanel = new JPanel();
	private JPanel selectedInfo[] = new JPanel[3];
	private JButton searchButton = new JButton("�˻�");
	private JButton allSearchButton = new JButton("��ü ��ȸ");
	private JButton dataModifyButton = new JButton("������ ����");
	private JButton filePrintButton = new JButton("���Ϸ� ��ȯ");
	private CustomTableModel centerTableModel = new CustomTableModel();
	private CustomTableModel vaccineTableModel = new CustomTableModel();
	private CustomTableModel vaccineStackTableModel = new CustomTableModel();
	private JTable centerTable = new JTable(centerTableModel);
	private JTable vaccineTable = new JTable(vaccineTableModel);
	private JTable vaccineStackTable = new JTable(vaccineStackTableModel);
	private JScrollPane centerScrollPane = new JScrollPane(centerTable);
	private JScrollPane vaccineScrollPane = new JScrollPane(vaccineTable);
	private JScrollPane vaccineStackScrollPane = new JScrollPane(vaccineStackTable);
	private JTextField vaccineStackSearch = new JTextField();
	private JTextField infoTextField[] = new JTextField[3];
	private JComboBox<String> searchCombo = new JComboBox<String>(searchInfoTypes);
	private JComboBox<String> centerCombo = new JComboBox<String>(areaNames);
	private Container container = frame.getContentPane();
	private Database db = new Database();
	//private 
	
	public VaccinePanel() {
		db.connect();
		initGUI();
	}
	
	private void initGUI() {
		// ��� �̺�Ʈ �߻� ��ư�� ActionListner ���
		searchButton.addActionListener(this);
		allSearchButton.addActionListener(this);
		dataModifyButton.addActionListener(this);
		filePrintButton.addActionListener(this);
		centerCombo.addActionListener(this);
		searchCombo.addActionListener(this);
		
		leftPanel.setBorder(BorderFactory.createEmptyBorder(13, 13, 13, 13));
		leftPanel.setLayout(new BorderLayout(0, 20));
		
		leftVaccinePanel.setLayout(new BorderLayout(20, 0));
		vaccineTable.getTableHeader().setBackground(new Color(192, 192, 192));
		vaccineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//vaccineScrollPane.setPreferredSize(new Dimension(300, 300));
		initVaccineTable();
		vaccineTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				initVaccineStackTable();
			}
		});
		leftVaccinePanel.add(vaccineListLabel, BorderLayout.NORTH);
		leftVaccinePanel.add(vaccineScrollPane, BorderLayout.CENTER);
		
		leftComboPanel.setLayout(new BorderLayout(0, 5));
		centerCombo.setPreferredSize(new Dimension(100, 25));
		leftComboPanel.add(centerLabel, BorderLayout.WEST);
		leftComboPanel.add(centerCombo, BorderLayout.EAST); // ��, �޺��ڽ� ���� (center Combo�� center�� ���״�� ���͸� �ǹ�)
		
		leftCenterPanel.setLayout(new BorderLayout(0, 5));
		leftCenterPanel.add(leftComboPanel, BorderLayout.NORTH);
		centerTable.getTableHeader().setBackground(new Color(192, 192, 192));
		centerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//centerScrollPane.setPreferredSize(new Dimension(400, 400));
		initCenterTable();
		centerTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				initVaccineStackTable();
			}
		});
		leftCenterPanel.add(centerScrollPane, BorderLayout.CENTER);
		
		leftVaccinePanel.setPreferredSize(new Dimension(300, 200));
		leftCenterPanel.setPreferredSize(new Dimension(300, 500));
		leftPanel.add(leftVaccinePanel, BorderLayout.NORTH);
		leftPanel.add(leftCenterPanel, BorderLayout.CENTER); //���̺� ����.
		
		centerPanel.setBorder(BorderFactory.createEmptyBorder(13, 0, 13, 0));
		centerPanel.setLayout(new BorderLayout(0, 10));
		vaccineStackTable.getTableHeader().setBackground(new Color(192, 192, 192));
		vaccineStackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vaccineStackScrollPane.setPreferredSize(new Dimension(150, 500));
		initVaccineStackTable();
		vaccineStackTable.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				int vaccineIdx = centerTable.getSelectedRow();
		        int centerIdx = centerTable.getSelectedRow();
		        int vaccineStackIdx = vaccineStackTable.getSelectedRow();
		        
		        if(centerIdx >= 0 && vaccineStackIdx < 0) {
		        	infoTextField[0].setText((String)centerTable.getValueAt(centerIdx, 0));
		        }
				if(vaccineIdx >= 0 && vaccineStackIdx < 0) {
			        infoTextField[1].setText((String)vaccineTable.getValueAt(vaccineIdx, 0));      	
				}
				if(vaccineStackIdx >= 0) {
					String vaccineName = (String)vaccineStackTable.getValueAt(vaccineStackIdx, 1);
					String centerName = (String)vaccineStackTable.getValueAt(vaccineStackIdx, 0);
					for(Object in : vaccineOuter) {
						Vector<String> tmp = (Vector<String>)in;
						if(vaccineName.equals(tmp.get(1))) {
							vaccineName = tmp.get(0);
							break;
						}
					}
					for(Object in : centerOuter) {
						Vector<String> tmp = (Vector<String>)in;
						if(centerName.equals(tmp.get(1))) {
							centerName = tmp.get(0);
							break;
						}
					}
					infoTextField[0].setText(centerName); 
					infoTextField[1].setText(vaccineName); 
					infoTextField[2].setText((String)vaccineStackTable.getValueAt(vaccineStackIdx, 2)); 
				}
			}		
		});
		
		centerPanel.add(vaccineStackListLabel, BorderLayout.NORTH);
		centerPanel.add(vaccineStackScrollPane, BorderLayout.CENTER); //����� �󺧰� ���̺� ����
		
		underPanel.setLayout(new BorderLayout(20, 0));
		searchCombo.setPreferredSize(new Dimension(150, 25));
		searchButton.setPreferredSize(new Dimension(150, 15));
		underPanel.add(searchCombo, BorderLayout.WEST);
		underPanel.add(vaccineStackSearch, BorderLayout.CENTER);
		underPanel.add(searchButton, BorderLayout.EAST);
		centerPanel.add(underPanel, BorderLayout.SOUTH); //���̺� �Ʒ��� �˻�â ����
		
		rightButtonPanel.setLayout(new GridLayout(3, 1, 0, 20));
		allSearchButton.setPreferredSize(new Dimension(250, 50));
		dataModifyButton.setPreferredSize(new Dimension(250, 50));
		filePrintButton.setPreferredSize(new Dimension(250, 50));
		allSearchButton.setFont(new Font("SansSerif", Font.BOLD, 20));
		dataModifyButton.setFont(new Font("SansSerif", Font.BOLD, 20));
		filePrintButton.setFont(new Font("SansSerif", Font.BOLD, 20));
		rightButtonPanel.add(allSearchButton);
		rightButtonPanel.add(dataModifyButton);
		rightButtonPanel.add(filePrintButton); //�����ʿ� ��ư4�� ���� �� ��Ʈ����, ũ������
		
		rightInfoPanel.setLayout(new GridLayout(3, 1, 0, 5));
		for(int idx = 0; idx < 3; idx++) {
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
	}
	
	private void initVaccineStackTable() {
		
		String query = "SELECT * FROM VaccineStock";
		Vector<String[]> data;
		
		vaccineStackTitle = new Vector<>();
		
		vaccineStackTitle.add("���� ���� �ڵ�");
		vaccineStackTitle.add("��� �ڵ�");
		vaccineStackTitle.add("��� ���");
		
		vaccineStackOuter = new Vector<>();
		
		//������ �ֱ�
		if((centerTable.getSelectedRow() < 0) && (vaccineTable.getSelectedRow() < 0)) {
			data = db.getData(query, 3);
		} else if(centerTable.getSelectedRow() < 0) {
			int vaccineIdx = vaccineTable.getSelectedRow();
			
			String temp = " WHERE vaccineCode = \"" + (String)vaccineTable.getValueAt(vaccineIdx, 1) + "\"";
			
			data = db.getData(query + temp, 3);
		} else if(vaccineTable.getSelectedRow() < 0) {
			int centerIdx = centerTable.getSelectedRow();
			
			String temp = " WHERE centerCode = \"" + (String)centerTable.getValueAt(centerIdx, 1) + "\"";
			
			data = db.getData(query + temp, 3);
		} else {
			int centerIdx = centerTable.getSelectedRow();
			int vaccineIdx = vaccineTable.getSelectedRow();
			
			String temp = " WHERE vaccineCode = \"" 
						+ (String)vaccineTable.getValueAt(vaccineIdx, 1) + "\" "
						+ "AND centerCode = \"" + (String)centerTable.getValueAt(centerIdx, 1) + "\"";
			
			data = db.getData(query + temp, 3);
		}
		
		for(String[] values : data) {
			vaccineStackOuter.add(changeVector(values));
		}
		
		// JTable �����ڷ� ���̺� �׸���
		vaccineStackTableModel.setDataVector((Vector)vaccineStackOuter, (Vector)vaccineStackTitle);
		
		// Table�� Sort ��� Ȱ��ȭ
		vaccineStackTable.setAutoCreateRowSorter(true);
		TableRowSorter<CustomTableModel> tableSorter = new TableRowSorter(vaccineStackTableModel);
		vaccineStackTable.setRowSorter(tableSorter);
		
		DefaultTableCellRenderer cellAlignCenter = new DefaultTableCellRenderer();
		cellAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		
		vaccineStackTable.getColumn("���� ���� �ڵ�").setCellRenderer(cellAlignCenter);
		vaccineStackTable.getColumn("��� �ڵ�").setCellRenderer(cellAlignCenter);
		vaccineStackTable.getColumn("��� ���").setCellRenderer(cellAlignCenter);
		vaccineStackTable.setBackground(new Color(255, 255, 255));
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

	private void initVaccineTable() {
		
		String query = "SELECT * FROM Vaccine";
		Vector<String[]> data;
		
		vaccineTitle = new Vector<>();
		
		vaccineTitle.add("��� ��");
		vaccineTitle.add("��� �ڵ�");
		vaccineTitle.add("2�� ���� ����");
		vaccineTitle.add("���� ����");
		
		vaccineOuter = new Vector<>();
		
		//������ �ֱ�
		data = db.getData(query, 4);
		
		for(String[] values : data) {
			vaccineOuter.add(changeVector(values));
		}
		
		// JTable �����ڷ� ���̺� �׸���
		vaccineTableModel.setDataVector((Vector)vaccineOuter, (Vector)vaccineTitle);
		
		// Table�� Sort ��� Ȱ��ȭ
		vaccineTable.setAutoCreateRowSorter(true);
		TableRowSorter<CustomTableModel> tableSorter = new TableRowSorter(vaccineTableModel);
		vaccineTable.setRowSorter(tableSorter);
		
		DefaultTableCellRenderer cellAlignCenter = new DefaultTableCellRenderer();
		cellAlignCenter.setHorizontalAlignment(JLabel.CENTER);
		
		vaccineTable.getColumn("��� ��").setCellRenderer(cellAlignCenter);
		vaccineTable.getColumn("��� �ڵ�").setCellRenderer(cellAlignCenter);
		vaccineTable.getColumn("2�� ���� ����").setCellRenderer(cellAlignCenter);
		vaccineTable.getColumn("���� ����").setCellRenderer(cellAlignCenter);
		
		vaccineTable.setBackground(new Color(255, 255, 255));
	}
	
	private void dataModifyInfo() {
		JDialog addDialog = new JDialog();
		JTextField inputData[] = new JTextField[3];
		JPanel inputPanel[] = new JPanel[4];
		JLabel inputLabel[] = new JLabel[3];
		JPanel addPanel = new JPanel();
		JButton confirmButton = new JButton("����");
		JButton cancelButton = new JButton("���");
		int vaccineStackIdx = vaccineStackTable.getSelectedRow();
		
		addDialog.setTitle("������ ����");
		addDialog.setSize(400, 350);
		inputLabel[0] = new JLabel("���� �ڵ�");
		inputLabel[1] = new JLabel("��� �ڵ�");
		inputLabel[2] = new JLabel("��� ���");
		addPanel.setLayout(new GridLayout(4, 1, 5, 15));
		addPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		for(int i = 0; i < 3; i++) {
			inputData[i] = new JTextField(20);
			inputData[i].setText((String)vaccineStackTable.getValueAt(vaccineStackIdx, i));
			inputPanel[i] = new JPanel();
			inputPanel[i].setLayout(new BorderLayout());
			inputPanel[i].add(inputLabel[i], BorderLayout.WEST);
			inputPanel[i].add(inputData[i], BorderLayout.EAST);
			
			addPanel.add(inputPanel[i]);
		}
		
		inputPanel[3] = new JPanel();
		inputPanel[3].setLayout(new BorderLayout());
		confirmButton.setPreferredSize(new Dimension(150, 35));
		cancelButton.setPreferredSize(new Dimension(150, 35));
		inputPanel[3].add(confirmButton, BorderLayout.WEST);
		inputPanel[3].add(cancelButton, BorderLayout.EAST);
		
		addPanel.add(inputPanel[3]);
		
		addDialog.add(addPanel);
		addDialog.setLocationRelativeTo(container);
		addDialog.setVisible(true);
		
		confirmButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean checkNull = true;
				
				for(int i = 0; i < 3; i++) {
					System.out.print("|" + inputData[i].getText());
					if(inputData[i].getText().equals("")) {
						checkNull = false;
						break;
					}	
				}
				
				if(checkNull == false) {
					showMessage("��� ���� �Է����ּ���.");
				} else {
					String temp = "UPDATE VaccineStock SET stack = \""
							+ inputData[2].getText() + "\" WHERE centerCode = \""
							+ inputData[0].getText() + "\" AND vaccineCode = \""
							+ inputData[1].getText() + "\"";
					db.queryToDB(temp);
					initVaccineStackTable();
					infoTextField[2].setText(inputData[2].getText());
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
		           
		        Vector<Vector> tempData = vaccineStackTableModel.getDataVector();
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
	
	private void setEntireTable() {
		initVaccineTable();
		initCenterTable();
		initVaccineStackTable();
	}
	
	private void searchInVaccineStackTable() {
		String value = (String)searchCombo.getSelectedItem();
		
		String keyword = vaccineStackSearch.getText();
		Vector<Vector> result = new Vector<>();
		
		int idx = 0;
		if(value.equals("���� ���� �ڵ�")) {
			idx = 0;
		} else if(value.equals("��� �ڵ�")) {
			idx = 1;
		} else if(value.equals("��� ���")) {
			idx = 2;
		} 
		for(Object in : vaccineStackOuter) {
			Vector<String> tmp = (Vector<String>) in;
			if(keyword.equals(tmp.get(idx))) {
				result.add(tmp);
			}
		}
		if(result.size() > 0) {
			vaccineStackTableModel.setDataVector(result, (Vector)vaccineStackTitle);
		} else {
			showMessage("�˻� ����� �����ϴ�.");
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
        } else if(o == dataModifyButton) {
        	if(vaccineStackTable.getSelectedRow() < 0) {
        		showMessage("�����͸� ������ �ּ���.");
        	} else {
            	dataModifyInfo();
        	}
        } else if(o == filePrintButton) {
        	changeFile();
        } else if(o == searchButton) {
        	searchInVaccineStackTable();
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

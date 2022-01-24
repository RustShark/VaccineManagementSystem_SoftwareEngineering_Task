package com.vaccinemanagementsystem.Main;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class Mainframe {
	
	private JFrame frame = new JFrame();
	private JTabbedPane tab = new JTabbedPane();
	private ReservationPanel reservationC = new ReservationPanel();
	private VaccinePanel vaccineStackC = new VaccinePanel();
    
    public Mainframe() {
    	frame.setSize(1300, 750);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setTitle("��� ���� �ý��� _ ����Ʈ���� ���� 1��");
    	
    	tab.addTab("������ ����", reservationC.getMainContainer());
    	tab.addTab("��� ��� ����", vaccineStackC.getMainContainer());
    	
    	frame.add(tab);
    	frame.setVisible(true);
    }
   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Mainframe dbcon = new Mainframe();
	}

}

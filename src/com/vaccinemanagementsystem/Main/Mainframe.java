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
    	frame.setTitle("백신 관리 시스템 _ 소프트웨어 공학 1조");
    	
    	tab.addTab("예약자 관리", reservationC.getMainContainer());
    	tab.addTab("백신 재고 관리", vaccineStackC.getMainContainer());
    	
    	frame.add(tab);
    	frame.setVisible(true);
    }
   
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Mainframe dbcon = new Mainframe();
	}

}

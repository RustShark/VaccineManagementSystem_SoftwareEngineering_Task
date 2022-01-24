package com.vaccinemanagementsystem.Main;

import javax.swing.*;
import javax.swing.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class CalendarPanel {
	//Ä¶¸°´õ ÆÐ³Î °´Ã¼
	
	private UtilDateModel model;
	private JDatePanelImpl datePanel;
	private JDatePickerImpl datePicker;
	
	public CalendarPanel() {
		model = new UtilDateModel();
		datePanel = new JDatePanelImpl(model);
		datePicker = new JDatePickerImpl(datePanel);
	}
	
	public JDatePickerImpl getCalendar() {
		return datePicker;
	}
	
	public void setDate(int year, int month, int day) {
		model.setDate(year, month-1, day);
		model.setSelected(true);
	}
	
	public String getDate() {
		Date tmp = new Date();
		
		tmp.setYear(model.getYear() - 1900);
		tmp.setMonth(model.getMonth());
		tmp.setDate(model.getDay());
		String tmpString = new SimpleDateFormat("yyyy-MM-dd").format(tmp);
		
		return tmpString;
	}
	
	public int getYear() {
		return model.getYear();
	}
	
	public int getMonth() {
		return model.getMonth();
	}
	
	public int getDay() {
		return model.getDay();
	}
}

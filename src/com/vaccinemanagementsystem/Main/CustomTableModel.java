package com.vaccinemanagementsystem.Main;

import javax.swing.table.*;

public class CustomTableModel extends DefaultTableModel{
    
    @Override
    public boolean isCellEditable(int row, int column)
    {
    	return false;
    }
}
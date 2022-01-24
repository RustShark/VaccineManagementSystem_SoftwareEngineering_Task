package com.vaccinemanagementsystem.Main;

import static org.junit.Assert.*;

import org.junit.Test;

public class databaseTest {

	@Test
	public void testConnect() {
		Database db = new Database();
		int check = db.connect();
		
		assertEquals(1, check);
	}
}


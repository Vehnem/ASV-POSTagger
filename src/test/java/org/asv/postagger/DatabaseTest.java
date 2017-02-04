package org.asv.postagger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;

import org.junit.Test;

import junit.framework.TestCase;

public class DatabaseTest extends TestCase {

	@Test
	public void test() {
		Database database = new Database();
		
		database.writeFileFromDB("test", "localhost/test?useSSL=false", "root", "1234", "deu_news_2011", "sentence_tagged", "\\/",10, 1000);
		
	}
}

package org.asv.postagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Generate train and test data from file or database
 * 
 * @author Robert, Marvin
 *
 */
public class TrainFile {

	/**
	 * empty constructor
	 */
	public TrainFile() {
	}
	
	/**
	 * From file
	 * 
	 * @param pathToFile path to input file
	 * @param outPath output path to train and test data
	 * @param delimiter delimiter
	 * @param rate test rate
	 */
	public void writeFileFromFile(String pathToFile, String outPath, String delimiter, int rate){
		try {
			
			BufferedReader read = new BufferedReader(new FileReader(pathToFile));
			BufferedWriter writeTrain = new BufferedWriter(new FileWriter(outPath));
			BufferedWriter writeTest = new BufferedWriter(new FileWriter(outPath + "_test"));
			String line;
			int i = 0;
			while((line = read.readLine()) != null){
				line = line.replaceAll(delimiter, "/");
				if(i%rate == 0){
					System.out.println(0);
					writeTest.write(line + "\n");
				} else {
					System.out.println(1);
					writeTrain.write(line + "\n");
				}
				i++;
			}
			
			writeTrain.flush();
			writeTrain.close();
			
			writeTest.flush();
			writeTest.close();
			read.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * From database
	 * 
	 * @param path output path
	 * @param url database adress
	 * @param user database user
	 * @param pw database password
	 * @param table database table
	 * @param column table column
	 * @param delimiter delimiter
	 * @param rate test rate
	 * @param limit database line limit
	 */
	public void writeFileFromDB(String path, String url, String user, String pw, String table,
			String column, String delimiter, int rate, int limit) {
		int count = 0, trainLimit = 0, i = 1;

		String myUrl = "jdbc:mysql://" + url;
		String myDriver = "com.mysql.cj.jdbc.Driver";
		
		float frate = rate;
		float testrate = frate / 100;
		
		try {
			// writer for training data and other data
			BufferedWriter trainData = new BufferedWriter(new FileWriter(path));
			BufferedWriter test = new BufferedWriter(new FileWriter(path + "_test"));

			// create a MySQL database connection
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, user, pw);

			// get number of sentences
			ResultSet c = conn.createStatement()
					.executeQuery(String.format("SELECT COUNT(%1$s) FROM %2$s ", column, table));
			while (c.next()) {
				count = c.getInt(1);
			}

			String query_select = String.format("SELECT %1$s FROM %2$s", column, table);

			if (limit > 0) { // Limit vorhanden -> Limit anhaengen
				if (count > limit)
					count = limit;

				trainLimit = (int) Math.floor(count * (1.0 - testrate));
				// get the sentences
				query_select = String.format("SELECT %1$s FROM %2$s LIMIT %3$d", column, table, limit);
			}

			Statement stmt = conn.createStatement();
			stmt.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = stmt.executeQuery(query_select);

			// writefile
			while (r.next()) {
				String sentence = r.getString(1);
				//replace WORD/TAG delimiter with '/'
				sentence = sentence.replaceAll(delimiter, "/");
				//if i <= trainLimit
				if (i <= trainLimit+1) {
					// data for training
					trainData.write(sentence);
					trainData.newLine();
				} else {
					// data for testing
					test.write(sentence);
					test.newLine();
				}
				i++;
			}

			trainData.flush();
			trainData.close();

			test.flush();
			test.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
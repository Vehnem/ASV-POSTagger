package org.asv.postagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 
 * @author Robert, Marvin
 *
 */
public class Database {

	/**
	 * constructor
	 */
	public Database() {
	}
	
	public void write_train_file_from_file(String pathToFile, String outPath, String delimiter, float testPercent){
		try {
			BufferedReader read = new BufferedReader(new FileReader(pathToFile));
			OutputStreamWriter writeTrain = new OutputStreamWriter(new FileOutputStream(outPath));
			OutputStreamWriter writeTest = new OutputStreamWriter(new FileOutputStream(outPath + "_test"));
			String line;
			int i = 0;
			int test = (int) Math.floor(100/testPercent);
			while((line = read.readLine()) != null){
				line = line.replaceAll(delimiter, "/");
				if(i%test == 0){
					writeTest.write(line + "\n");
				} else {
					writeTrain.write(line + "\n");
				}
			}
			writeTrain.close();
			writeTest.close();
			read.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public int write_train_file_from_DB(String path, String driver, String url, String user, String pw, String table,
			String column, String delimiter, float testrate, int limit) {
		int count = 0, trainLimit = 0, i = 1;

		String myUrl = "jdbc:mysql://" + url;
		String myDriver = driver;
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
		return trainLimit;
	}

	// TODO single method
	
	public void writeFileFromFile(String inputFile, String filename, String delimiter, int rate) {
		String path = filename;
		float frate = rate;
		float testrate = frate / 100;
		System.out.println("\n====== Starting ======");
		write_train_file_from_file(inputFile, path, delimiter, testrate);

	}

	public void writeFileFromDB(String filename, String databaseUrl, String user, String pw, String tablename,
			String column, String delimiter, int rate, int limit) {
		String path = filename;
		String driver = "com.mysql.cj.jdbc.Driver";
		float frate = rate;
		float testrate = frate / 100;
		System.out.println("\n====== Starting ======");
		System.out.println(filename);
		System.out.println("database...");
		write_train_file_from_DB(path, driver, databaseUrl, user, pw, tablename, column, delimiter, testrate, limit);

	}
}
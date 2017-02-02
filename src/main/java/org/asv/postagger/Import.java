package org.asv.postagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: - Count words instead of sentences
 */
public class Import {

	public Import() {
	}

	private static final Logger LOGGER = Logger.getLogger(Import.class.getName());

	public int write_train_file_from_DB(String path, String driver, String url, String user, String pw, String database,
			String column, String delimiter, float testrate, int limit) {
		int count = 0, trainLimit = 0, i = 1;
		LOGGER.setLevel(Level.ALL);
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
					.executeQuery(String.format("SELECT COUNT(%1$s) FROM %2$s ", column, database));
			while (c.next()) {
				count = c.getInt(1);
			}
			
			LOGGER.log(Level.FINE, String.format("%nSentences found: %d", count));
			
			String query_select = String.format("SELECT %1$s FROM %2$s", column, database);
			
			if(limit > 0) { // Limit vorhanden -> Limit anhaengen
				if(count > limit) count = limit; 
				
				trainLimit = (int) Math.floor(count * (1 - testrate));

				// get the sentences
				query_select = String.format("SELECT %1$s FROM %2$s LIMIT %3$d", column, database, limit);
			}
			
			LOGGER.log(Level.FINE,
					String.format("Using %1$d sentences for training and %2$d for testing%n", trainLimit, count - trainLimit));
			
			Statement stmt = conn.createStatement();
			stmt.setFetchSize(Integer.MIN_VALUE);
			ResultSet r = stmt.executeQuery(query_select);

			// writefile
			while (r.next()) {

				String sentence = r.getString(1);
				sentence = sentence.replaceAll(delimiter, "/"); // replace
																// WORD/TAG
																// delimiter
																// with '/'
				if (i <= trainLimit) {
					// data for training
					trainData.write(sentence);
					trainData.newLine();
					/*for (String wordTagPair : sentence.split(" ")) {
						trainData.write(wordTagPair);
						trainData.newLine();
					}*/
				} else {
					// data for testing
					test.write(sentence);
					test.newLine();
					/*for (String wordTagPair : sentence.split(" ")) {
						test.write(wordTagPair);
						test.newLine();
					}*/
				}

				// Progress Update every 10%
				if ((i % ((int) (count / 10))) == 0) {
					LOGGER.log(Level.FINER, "====== " + i + "/" + count + " ======\n");
				}

				i++;

			}
			LOGGER.log(Level.FINE, "Done!");

			trainData.flush();
			trainData.close();

			test.flush();
			test.close();

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
			e.printStackTrace();
		}
		return trainLimit;
	}

	public static void main(String[] args) {
	}

	public void reformat(String path) {
		try {
			BufferedWriter write = new BufferedWriter(new FileWriter(path + "_formatted"));
			BufferedReader read = new BufferedReader(new FileReader(path));
			String thisLine = "";
			LOGGER.setLevel(Level.ALL);
			LOGGER.log(Level.FINE, "Starting formatting...");
			while ((thisLine = read.readLine()) != null) {
				String[] words = thisLine.split(" ");
				for (String word : words) {
					write.write(word);
					write.newLine();
				}
			}
			LOGGER.log(Level.FINE, "Done!");
			write.flush();
			write.close();
			read.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}

}

/*
 * UNUSED or DEPRECATED:
 * 
 * public void insert_tagged_words(String user, String pw, String origin_table,
 * String sentence_column) { // create a mysql database connection try {
 * Class.forName(myDriver); Connection conn = DriverManager.getConnection(myUrl,
 * user, pw); String query_select = " select sentence from sentence_tagged ";
 * String query_insert =
 * " insert into words_tagged (word, tag) values ( ? , ? )"; Statement stmt =
 * conn.createStatement(); ResultSet r = stmt.executeQuery(query_select); while
 * (r.next()) { String sentence = r.getString("sentence"); for (String s :
 * sentence.split(" ")) { String[] s_split = s.split("\\|"); if (s_split.length
 * == 2) { PreparedStatement preparedStmt_insert =
 * conn.prepareStatement(query_insert); preparedStmt_insert.setString(1,
 * s_split[0]); preparedStmt_insert.setString(2, s_split[1]); //
 * System.out.println(s_split[0] + " : " + s_split[1] );
 * preparedStmt_insert.execute(); } }
 * 
 * } r.close(); System.out.println("done"); } catch (Exception e) {
 * System.err.println("Got an exception!"); System.err.println(e.getMessage());
 * // e.printStackTrace(); }
 * 
 * }
 * 
 * public void insert_CSV(String filepath, String user, String pw) { try { //
 * create a mysql database connection Class.forName(myDriver); Connection conn =
 * DriverManager.getConnection(myUrl, user, pw);
 * 
 * // the mysql insert statement String query =
 * " insert into sentence_tagged (id, sentence) values (?, ?)";
 * 
 * // reading CSV CSVReader reader = new CSVReader(new FileReader(filepath),
 * '\t', CSVWriter.NO_QUOTE_CHARACTER, 1); String[] nextLine; while ((nextLine =
 * reader.readNext()) != null) { if (nextLine.length == 2) {
 * 
 * // create the mysql insert preparedstatement PreparedStatement preparedStmt =
 * conn.prepareStatement(query); preparedStmt.setInt(1,
 * Integer.parseInt(nextLine[0])); preparedStmt.setString(2, nextLine[1]);
 * 
 * // execute the preparedstatement preparedStmt.execute(); } }
 * 
 * conn.close(); } catch (Exception e) {
 * System.err.println("Got an exception!"); System.err.println(e.getMessage());
 * // e.printStackTrace(); } }
 * 
 */
package com.uiuc.socialbuzz.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Scanner;

import jnisvmlight.KernelParam;
import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.LearnParam;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import jnisvmlight.TrainingParameters;



public class TrainSVM 
{
	
	static int N = 18408;
	static int M = 10;
	//static int M = 18;
	
	public static void main(String[] args) throws SQLException, IOException {
		// The trainer interface with the native communication to the SVM-light shared
	    // libraries
	    SVMLightInterface trainer = new SVMLightInterface();

	    // The training data
	    LabeledFeatureVector[] trainData = new LabeledFeatureVector[N];

	    // Sort all feature vectors in ascedending order of feature dimensions
	    // before training the model
	    SVMLightInterface.SORT_INPUT_VECTORS = true;
		
	
		String user = "pawar2";
		String pw = "change_me#";
		String dbUrl = "jdbc:mysql://harrier02.cs.illinois.edu/test";
	   /* String user = "root";
		String pw = "root";
		String dbUrl = "jdbc:mysql://localhost/tedas";*/
		String dbClass = "com.mysql.jdbc.Driver";
		Connection con = null;
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, user, pw);
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String query = "select label, contributors, retweetcount, usermention, hashtag, url, favorited, media, favorites, friends, followers, listed, statuses, verified, gramsmatched, wordscore, corpusscore, editdistance, 4sq from features";
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		/*String[] dimensions = {
				"contributors",
				"retweetcount",
				"usermention",
				"hashtag",
				"url",
				"favorited",
				"media",
				"favorites",
				"friends",
				"followers",
				"listed",
				"statuses",
				"verified",
				"gramsmatched",
				"wordscore",
				"corpusscore",
				"editdistance",
				"4sq"
			};*/
		
		//int[] dimensions = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
		int[] dimensions = {1,2,3,4,5,6,7,8,9,10};
		double[] values ; 
		int label = 0; //fill up label
		int i = 0;
		while (rs.next() && i<N)
		{
			//set label
			label = rs.getString("label").equals("1")? +1 : -1;
			values = new double[M];
			
			//fill up values
			/*values[0] = rs.getInt("contributors"); 
			values[1] = rs.getInt("retweetcount");
			values[2] = rs.getInt("usermention");
			values[3] = rs.getInt("hashtag");
			values[4] = rs.getInt("url");
			values[5] = rs.getInt("favorited");
			values[6] = rs.getInt("media");
			values[7] = rs.getInt("favorites");
			values[8] = rs.getInt("friends");
			values[9] = rs.getInt("followers");
			values[10] = rs.getInt("listed");
			values[11] = rs.getInt("statuses");
			values[12] = rs.getInt("verified");
			values[13] = rs.getInt("gramsmatched");
			values[14] = rs.getDouble("wordscore");
			values[15] = rs.getDouble("corpusscore");
			values[16] = rs.getInt("editdistance");
			values[17] = rs.getInt("4sq");*/
			//values[0] = rs.getInt("contributors"); 
			//values[1] = rs.getInt("retweetcount");
			//values[2] = rs.getInt("usermention");
			//values[3] = rs.getInt("hashtag");
			values[0] = rs.getInt("url");
			//values[5] = rs.getInt("favorited");
		//	values[6] = rs.getInt("media");
			values[1] = rs.getInt("favorites");
			values[2] = rs.getInt("friends");
			values[3] = rs.getInt("followers");
			//values[10] = rs.getInt("listed");
			values[4] = rs.getInt("statuses");
		//	values[12] = rs.getInt("verified");
			values[5] = rs.getInt("gramsmatched");
			values[6] = rs.getDouble("wordscore");
			values[7] = rs.getDouble("corpusscore");
			values[8] = rs.getInt("editdistance");
			values[9] = rs.getInt("4sq");
			
			trainData[i] = new LabeledFeatureVector(label, dimensions, values);
			trainData[i].normalizeL2();
			i++;
		}
		
		
		// Initialize a new TrainingParamteres object with the default SVM-light
	    // values
	    TrainingParameters tp = new TrainingParameters();

	    // Switch on some debugging output
	    tp.getKernelParameters().kernel_type = 1;
	    tp.getLearningParameters().verbosity = 1;
	    //tp.getLearningParameters().svm_c_factor = 100;
	    
	    
	    
	    
	    System.out.println("\nTRAINING SVM-light MODEL ..");
	   // SVMLightModel model = trainer.trainModel(trainData, tp);
	    String[] argv = new String[6];
	    argv[0] = "-v";
	    argv[1] = "1";
	    argv[2] = "-t";
	    argv[3] = "1";
	    argv[4] = "-j";
	    argv[5] = "25";
	    SVMLightModel model = trainer.trainModel(trainData, argv);
	    //Write model to file
	    model.writeModelToFile("socialbuzz.svm.model");
	    System.out.println(" DONE.");	
	    
	    BufferedWriter bw = new BufferedWriter(new FileWriter(new File("predictions.txt")));
	 // Use the classifier on the randomly created feature vectors
	    System.out.println("\nVALIDATING SVM-light MODEL in Java..");
	    int precision = 0;
	    
	    int t=0, tn=0, fp=0, fn=0;
	    for (int j = 0; j < N; j++) {

	      // Classify a test vector using the Java object
	      // (in a real application, this should not be one of the training vectors)
	      double d = model.classify(trainData[j]);
	      bw.write(j+" "+trainData[j].getLabel()+" "+d+"  \n");
	      if ((trainData[j].getLabel() < 0 && d < 0)
	          || (trainData[j].getLabel() > 0 && d > 0)) {
	    	  
	        precision++;
	      }
	      
	      
	      if (trainData[j].getLabel() > 0 && d > 0)
	    	  t++;
	      if (trainData[j].getLabel() < 0 && d < 0)
	    	  tn++;
	      if (trainData[j].getLabel() < 0 && d > 0)
	    	  fp++;
	      if (trainData[j].getLabel() > 0 && d < 0)
	    	  fn++;
	      
	      if (d>0) System.out.println("positive!");
	      if (j % 10 == 0) {
	        System.out.print(i + ".");
	      }
	    }
	    bw.close();
	    System.out.println(" DONE.");
	    System.out.println("\n" + ((double) precision / N)
	        + " PRECISION=RECALL ON RANDOM TRAINING SET.");
	    System.out.println("TP : "+t+"\tTN : "+tn+"\tFP : "+fp+"\tFN : "+fn);
	}
}

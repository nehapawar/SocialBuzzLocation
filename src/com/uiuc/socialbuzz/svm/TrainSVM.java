package com.uiuc.socialbuzz.svm;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.util.Scanner;

import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.SVMLightModel;
import jnisvmlight.TrainingParameters;



public class TrainSVM 
{
	
	static int N = 18408;
	static int M = 18;
	
	public static void main(String[] args) throws FileNotFoundException {
		// The trainer interface with the native communication to the SVM-light shared
	    // libraries
	    SVMLightInterface trainer = new SVMLightInterface();

	    // The training data
	    LabeledFeatureVector[] trainData = new LabeledFeatureVector[N];

	    // Sort all feature vectors in ascedending order of feature dimensions
	    // before training the model
	    SVMLightInterface.SORT_INPUT_VECTORS = true;
		
		Scanner in = new Scanner(new File("train.dat"));
		
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
		
		int[] dimensions = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
		double[] values ; 
		int label = 0; //fill up label
		int i = 0;
		while (in.hasNext())
		{
			String line = in.nextLine();
			String[] array = line.split("\t");
			label = (array[0].equals("1")) ? 1 : -1;
			values = new double[M];
			
			//fill up values
			
			trainData[i] = new LabeledFeatureVector(label, dimensions, values);
			trainData[i].normalizeL2();
		}
		
		// Initialize a new TrainingParamteres object with the default SVM-light
	    // values
	    TrainingParameters tp = new TrainingParameters();

	    // Switch on some debugging output
	    tp.getLearningParameters().verbosity = 1;

	    System.out.println("\nTRAINING SVM-light MODEL ..");
	    SVMLightModel model = trainer.trainModel(trainData, tp);
	    
	    //Write model to file
	    model.writeModelToFile("socialbuzz.svm.model");
	    System.out.println(" DONE.");
	    
	    
	    
	   
		
	}
}

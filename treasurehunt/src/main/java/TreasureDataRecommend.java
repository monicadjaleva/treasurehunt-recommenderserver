import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TreasureDataRecommend {
	static ArrayList<String> recommendedTreasuresList=new ArrayList<String>();

	static ClassLoader classLoader = TreasureDataRecommend.class.getClassLoader();

	/* We pass the query username as String[]args */
	public static void main(String[]args) throws IOException, TasteException
	{
		recommendedTreasuresList.clear();
		String username=null;
		for(int i = 0; i < args.length; i++) {
			username=args[i];
		}
		// Convert username to long 
		long userID = 0;
		try{
			if(username!=null)
			{
				userID = new BigInteger(username.getBytes()).longValue();
			}else
			{
				userID=new BigInteger("Alice".getBytes()).longValue();
			}
		}catch(NumberFormatException e)
		{
			System.err.println("Unable to parse username. " + e.getMessage());
		}
		// Get treasure data file 
		File file = new File(classLoader.getResource("data.csv").getFile());

		// File file=new File("src/main/data/data.csv");
		DataModel model = new FileDataModel(file);

		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.2, similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);


		// userID=120325361197921L; // This is the user IDfileReader
		List<RecommendedItem> recommendations=null;
		try{
			recommendations = recommender.recommend(userID,2);
		}catch(NoSuchUserException e)
		{
			System.err.println("No such user. " +e.getMessage());
		}
		if(recommendations!=null)
		{
			for (RecommendedItem recommendation : recommendations) {
				long id=recommendation.getItemID();
				recommendTreasure(id);
			}
		}else{
			System.err.println("No recommendations");
		}
	}

	private static void recommendTreasure(long id) throws FileNotFoundException {
		File file = new File(classLoader.getResource("treasures.csv").getFile());
		FileReader fileReader=new FileReader(file);
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(fileReader);
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] treasure = line.split(cvsSplitBy);
				if(treasure[0]!=null)
				{
					long treasureID=Long.parseLong(treasure[0]);
					if(id==treasureID)
					{
						if(treasure[1]!=null)
						{
							String treasureName=treasure[1];
							System.out.println(treasureName);
							recommendedTreasuresList.add(treasureName);
						}
					}
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static ArrayList<String> getRecommendedTreasuresList() {
		return recommendedTreasuresList;
	}

	public static void setRecommendedTreasuresList(ArrayList<String> recommendedTreasuresList) {
		TreasureDataRecommend.recommendedTreasuresList = recommendedTreasuresList;
	}


}

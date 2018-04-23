import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.*;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;


public class TreasureDataRecommendClassifier {
	static String recommendedTreasure="";

	static ClassLoader classLoader = TreasureDataRecommend.class.getClassLoader();

	public static void main(String[]params) throws IOException, TasteException
	{
		File file = new File(classLoader.getResource("treasures.csv").getFile());
		
		// File file=new File("src/main/data/data.csv");
		DataModel model = new FileDataModel(file);
		/*Recommender recommender = new GenericItemBasedRecommender(model, itemSimilarity);
				Recommender cachingRecommender = new CachingRecommender(recommender);
				List<RecommendedItem> recommendations =
				  cachingRecommender.recommend(1234, 10);*/
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
							recommendedTreasure=treasureName;
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

	public static String getRecommendedTreasure() {
		return recommendedTreasure;
	}


	
}

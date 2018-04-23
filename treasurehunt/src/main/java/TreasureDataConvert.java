
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import model.FirebaseResponse;

import error.FirebaseException;
import service.Firebase;


public class TreasureDataConvert {

	static HashMap<String,Double> userCategoryMap=new HashMap<String,Double>();

	public static void main(String[]params) throws FirebaseException, IOException
	{
		Firebase firebase = new Firebase("https://treasurehunt-3540e.firebaseio.com/");
		FirebaseResponse response= firebase.get();
		Map<String,Object> responseMap=response.getBody();
		// We will write our Treasure Feedback data to csv files

		PrintWriter pwRatings = null;
		PrintWriter pwUsers = null;
		PrintWriter pwTreasures = null;
		PrintWriter pwCategories = null;
		File treasureFile=null;
		try {
			File file = new File(getResourcePath() + "/data.csv");
			System.out.println(file.getAbsolutePath());
			File ratingFile=new File("/Users/monicadzhaleva/Desktop/treasurehuntwebsite/webservlet/webservlet/treasurehunt/src/main/data/data.csv");
			pwRatings = new PrintWriter(ratingFile);
			File userFile=new File("/Users/monicadzhaleva/Desktop/treasurehuntwebsite/webservlet/webservlet/treasurehunt/src/main/data/users.csv");
			pwUsers = new PrintWriter(userFile);
			treasureFile=new File("/Users/monicadzhaleva/Desktop/treasurehuntwebsite/webservlet/webservlet/treasurehunt/src/main/data/treasures.csv");
			pwTreasures = new PrintWriter(treasureFile);
			File categoriesFile=new File("/Users/monicadzhaleva/Desktop/treasurehuntwebsite/webservlet/webservlet/treasurehunt/src/main/data/treasurecategories.csv");
			pwCategories = new PrintWriter(categoriesFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		StringBuilder sb = new StringBuilder();

		for(Entry<String, Object> obj: responseMap.entrySet())
		{
			String key=	obj.getKey();
			Object treasureMap= obj.getValue();
			if(key=="treasures_feedback") // We are interested in treasures feedback
			{
				writeRatings(treasureMap,pwRatings,treasureFile,pwCategories);
			}
			if(key=="users")
			{
				writeUsers(treasureMap,pwUsers);
			}
			if(key=="treasures")
			{
				writeTreasures(treasureMap,pwTreasures);

			}
		}
	}


	// Get ratings - store as username, treasure name & rating
	@SuppressWarnings({ "unchecked", "resource" })
	private static void writeRatings(Object treasureMap, PrintWriter pw, File treasureFile, PrintWriter pwCategories) throws IOException {
		for (Map.Entry<String, Object> treasure : ((Map<String, Object>) treasureMap).entrySet()) {

			String treasureName=treasure.getKey();
			Object userFeedbackMap= treasure.getValue(); // TREASURE
			for(Entry<String, Object> userFeedback: ((LinkedHashMap<String, Object>) userFeedbackMap).entrySet())
			{
				String userName=userFeedback.getKey(); // USER
				Object userFeedbackObj=userFeedback.getValue();
				for(Entry<String, Object> userFeedbackEntry: ((LinkedHashMap<String, Object>) userFeedbackObj).entrySet())
				{
					if(userFeedbackEntry.getKey().equals("rating")) // RATING
					{
						System.out.println(treasureName + "  " + userName);
						String rating=userFeedbackEntry.getValue().toString();
						/* write to the csv file the the user + the treasure name + the rating */
						long userNameDecoded = new BigInteger(userName.getBytes()).longValue();
						long treasureNameDecoded = new BigInteger(treasureName.getBytes()).longValue();


						float ratingFloat = (float)Integer.parseInt(rating);

						pw.write(userNameDecoded+","+treasureNameDecoded+","+ratingFloat+"\n");

						/* write to category csv file user + category + rating */
						FileReader fileReader=new FileReader(treasureFile);
						BufferedReader br = null;
						String line = "";
						String cvsSplitBy = ",";
						br = new BufferedReader(fileReader);
						while ((line = br.readLine()) != null) {

							// use comma as separator
							String[] treasureStr = line.split(cvsSplitBy);
							if(treasureStr[0]!=null&&Long.parseLong(treasureStr[0])==treasureNameDecoded)
							{
								if(treasureStr[2]!=null)
								{
									if(userCategoryMap.get(userNameDecoded+","+new BigInteger(treasureStr[3].getBytes()).longValue())==null)
									{
										userCategoryMap.put(userNameDecoded+","+new BigInteger(treasureStr[3].getBytes()).longValue(), 1.0);
									}else{
										double val=userCategoryMap.get(userNameDecoded+","+new BigInteger(treasureStr[3].getBytes()).longValue());
										userCategoryMap.put(userNameDecoded+","+new BigInteger(treasureStr[3].getBytes()).longValue(), val+1.0);
									}
								}
							}
						}


					}
				}
			}
		}
		for(Entry<String,Double> entry: userCategoryMap.entrySet())
		{
			String usercategory=entry.getKey();
			String[] arr=usercategory.split(",");
			String userNameDecoded=arr[0];
			String categoryDecoded=arr[1];
			double numberOfVisits=entry.getValue();
			pwCategories.write(userNameDecoded+","+categoryDecoded+","+numberOfVisits+"\n");
		}
		pwCategories.close();
		pw.close();
	}

	// Get users - decoded id, username
	@SuppressWarnings("unchecked")
	private static void writeUsers(Object usersMap, PrintWriter pwUsers) {
		for (Map.Entry<String, Object> user : ((Map<String, Object>) usersMap).entrySet()) {
			String userName=user.getKey();
			long userNameDecoded = new BigInteger(userName.getBytes()).longValue();
			pwUsers.write(userNameDecoded+","+userName+"\n");
		}
		pwUsers.close();
	}

	// Get treasures - decoded id, treasure name, categories
	@SuppressWarnings("unchecked")
	private static void writeTreasures(Object treasureMap,
			PrintWriter pwTreasures) {
		for (Map.Entry<String, Object> treasure : ((Map<String, Object>) treasureMap).entrySet()) 
		{
			String treasureName=treasure.getKey();
			long treasureNameDecoded = new BigInteger(treasureName.getBytes()).longValue();
			Object treasureObj=treasure.getValue();
			for(Entry<String, Object> treasureObjEntry: ((LinkedHashMap<String, Object>) treasureObj).entrySet())
			{
				if(treasureObjEntry.getKey().equals("category")) // RATING
				{
					String category = treasureObjEntry.getValue().toString();
					long categoryDecoded = new BigInteger(category.getBytes()).longValue();

					pwTreasures.write(treasureNameDecoded+","+treasureName+","+categoryDecoded+","+category+"\n");

				}
			}
		}
		pwTreasures.close();

	}


	private static String getResourcePath() {
		try {
			URI resourcePathFile = System.class.getResource("/RESOURCE_PATH").toURI();
			String resourcePath = Files.readAllLines(Paths.get(resourcePathFile),null).get(0);
			URI rootURI = new File("").toURI();
			URI resourceURI = new File(resourcePath).toURI();
			URI relativeResourceURI = rootURI.relativize(resourceURI);
			return relativeResourceURI.getPath();
		} catch (Exception e) {
			return null;
		}
	}




}



import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mahout.cf.taste.common.TasteException;

import error.FirebaseException;

/**
 * Servlet implementation class HttpServlet
 */
public class HttpServlet extends javax.servlet.http.HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see javax.servlet.http.HttpServlet#javax.servlet.http.HttpServlet()
     */
    public HttpServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().flush();
		String username=request.getParameter("username");
		System.out.println("Making a recommendation query for user: " + username);
		try {
			if(username!=null)
			{
			String[] params={username};
			// Get data
		//	TreasureDataConvert.main(null);
			TreasureDataRecommend.main(params);
			}else{
			String[] params={"monica"};
		//	TreasureDataConvert.main(null);
			TreasureDataRecommend.main(params);
			}
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> recommendedTreasures=TreasureDataRecommend.getRecommendedTreasuresList();
		for (String treasure: recommendedTreasures)
		{
			System.out.println(treasure);
			response.getWriter().append(treasure +",");
		}
		response.getWriter().close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

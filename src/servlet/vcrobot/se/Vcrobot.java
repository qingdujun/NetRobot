package servlet.vcrobot.se;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.index.bar.thread.StartAllIndexThread;
import servlet.vcrobot.ExecuteQuery;
/**
 * Lucene搜索引擎
 * @author Dolphix.J Qing
 *
 */
public class Vcrobot extends HttpServlet {
	private static final long serialVersionUID = -3442409593063914479L;
	
	public Vcrobot() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
	
		String keywords = request.getParameter("keywords");
		String email = request.getParameter("email");
	
		if (keywords == null) {
			out.print("not exit keywords.");
		}else {
			if (email == null) {
				email = "vcrobot";
			}
			System.out.println("S -"+keywords);
			String json = ExecuteQuery.analyseContain(keywords,email);
			if (json == null) {
				json = ExecuteQuery.executeQuery(email, keywords);
			}
			
			out.print(json);
		}
		
		
		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	public void init() throws ServletException {
		//启动搜索引擎
		StartAllIndexThread.openAllIndexThread();
	}

}



////搜索方式1
////String result = BarNRTSearch.searchRandomTopicReply(keywords);
////if (null == result || result.equals("")) {
////搜索方式2
//String	result = BarNRTSearch.searchRandomDetail(keywords);
////}
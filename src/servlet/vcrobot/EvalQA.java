package servlet.vcrobot;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import se.index.utils.JsonUtil;
import servlet.vcrobot.db.VcrobotDb;

public class EvalQA extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public EvalQA() {
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
		String rid = request.getParameter("rid");
		String op = request.getParameter("op");
		
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("key", "10001");
		hashMap.put("text", VcrobotDb.eval(rid, op));

		out.print(JsonUtil.parseJson(hashMap));

		out.flush();
		out.close();
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);

	}

	public void init() throws ServletException {
		// Put your code here
	}

}

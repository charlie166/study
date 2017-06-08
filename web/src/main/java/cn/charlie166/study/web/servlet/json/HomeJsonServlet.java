package cn.charlie166.study.web.servlet.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.charlie166.study.web.utils.ServletUtils;

/**
* @ClassName: HomeJsonServlet 
* @Description: 首页json请求模拟
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月8日 
*
 */
@WebServlet(urlPatterns = "/json/home")
public class HomeJsonServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		/**模拟JSON返回数据**/
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", 200);
		map.put("msg", "success");
		ServletUtils.json(resp, map);
	}
	
}	
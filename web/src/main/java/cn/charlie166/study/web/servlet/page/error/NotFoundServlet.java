package cn.charlie166.study.web.servlet.page.error;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.charlie166.study.web.utils.ServletUtils;

/***
* @ClassName: NotFoundServlet 
* @Description: 未找到资源响应
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月8日 
*
 */
@WebServlet(urlPatterns = {"/404"})
public class NotFoundServlet extends HttpServlet{
		
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletUtils.page(req, resp, "/WEB-INF/view/error/404.jsp");
	}

	
}	
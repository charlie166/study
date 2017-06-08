package cn.charlie166.study.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
* @ClassName: ServletUtils 
* @Description: servlet工具类
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年6月8日 
*
 */
public class ServletUtils {

	/**
	* @Title: page 
	* @Description: 页面响应返回
	* @param request 请求
	* @param resp 响应
	* @param viewFile 页面文件路径
	 */
	public static void page(HttpServletRequest request, HttpServletResponse resp, String viewFile){
		if(StringUtils.hasContent(viewFile)){
			String physicalDire = request.getServletContext().getRealPath("/");
			File file = new File(physicalDire + viewFile);
			if(file.isFile()){
				resp.setContentType("text/html");
				resp.setCharacterEncoding("UTF-8");
				try {
					InputStream stream = new FileInputStream(file);
		            OutputStream out = resp.getOutputStream();
		            byte buff[] = new byte[1024];
		            int length = 0;
		            while ((length = stream.read(buff)) > 0) {
		                out.write(buff,0,length);
		            }
		            stream.close();
		            out.close();
		            out.flush();
		            return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			resp.sendError(404, "Not Found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* @Title: json 
	* @Description: JSON数据响应
	* @param resp 响应
	* @param json 返回数据
	 */
	public static void json(HttpServletResponse resp, Object json){
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json");
			PrintWriter pw = resp.getWriter();
			if(json != null){
				Gson gson = new Gson();
				String jsonStr = gson.toJson(json);
				pw.write(jsonStr);
			} else {
				pw.write("null");
			}
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}	
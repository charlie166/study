package cn.charlie166.study.web.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.charlie166.study.web.utils.StringUtils;

/**
* @ClassName: ServletFilter 
* @Description: 请求过滤器
* @company 
* @author liyang
* @Email charlie166@163.com
* @date 2017年7月10日 
*
 */
public class ServletFilter implements Filter{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private String charset = "UTF-8";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String c = filterConfig.getInitParameter("charset");
		if(StringUtils.hasContent(c)){
			charset = c;
		}
		logger.debug("初始化过滤器:" + filterConfig.getFilterName() + "---设置编码为:" + charset);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(charset);
		response.setCharacterEncoding(charset);
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		if(request instanceof HttpServletRequest){
			HttpServletRequest hsr = (HttpServletRequest) request;
			logger.debug(now + " 请求 " + hsr.getRequestURL());
		} else {
			logger.debug(now + " 请求");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		logger.debug("销毁过滤器...");
	}

}	
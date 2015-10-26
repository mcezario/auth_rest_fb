package org.autenticacao.rest.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CORSFilter implements Filter {

	private static final String CALLBACK_PARAMETER = "callback";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletResponse r = ((HttpServletResponse) response);
		r.addHeader("Access-Control-Allow-Origin", "*");
		r.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, token, tokenFB, idFB");
		r.addHeader("Access-Control-Allow-Credentials", "true");
		r.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
		r.addHeader("Access-Control-Max-Age", "1209600");

		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;

        Map<String, String[]> parms = httpRequest.getParameterMap();
        if(parms.containsKey(CALLBACK_PARAMETER)) {
 
            OutputStream out = httpResponse.getOutputStream();
 
            GenericResponseWrapper wrapper = new GenericResponseWrapper(httpResponse);
 
            chain.doFilter(request, wrapper);
 
            out.write(new String(parms.get(CALLBACK_PARAMETER)[0] + "(").getBytes());
            out.write(wrapper.getData());
            out.write(new String(");").getBytes());
 
            wrapper.setContentType("text/javascript;charset=UTF-8");
 
            out.close();
        } else {
            chain.doFilter(request, response);
        }
        
	}
	
	@Override
	public void destroy() {
	}
}

package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/AuthFilter")
public class AuthFilter extends HttpFilter implements Filter {

	private static final long serialVersionUID = 1L;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) request;

		String uri = req.getRequestURI();
		System.out.println("Request uri : " + uri);

		if (uri.equals("/") || uri.equals("/login")) {
			res.sendRedirect(uri);
			return;
		}

		//HttpSession session = req.getSession();
		// 인증 처리

		chain.doFilter(request, response);
	}

}

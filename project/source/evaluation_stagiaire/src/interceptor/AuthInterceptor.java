package interceptor;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -5114658085937727056L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		Integer user_id = (Integer) session.get("user_id");
		
		if(user_id == null) {
			return ActionSupport.LOGIN;
		}
		
		return invocation.invoke();
	}

}

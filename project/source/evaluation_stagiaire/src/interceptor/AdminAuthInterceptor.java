package interceptor;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AdminAuthInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -1112833296923965088L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		Map<String, Object> session = invocation.getInvocationContext().getSession();
		Integer user_id = (Integer) session.get("user_id");
		Boolean user_admin = (Boolean) session.get("user_admin");
		
		if(user_id == null || user_admin == null || user_admin.booleanValue() == false) {
			return "forbidden";
		}
		
		return invocation.invoke();
	}

}

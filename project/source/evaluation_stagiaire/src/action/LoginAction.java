package action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Person;
import model.Status;

public class LoginAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 7493368296840488923L;
	
	public Person user = new Person();
	private SessionMap<String, Object> sessionMap;

	@Override
	public void setSession(Map<String, Object> map) {
		sessionMap = (SessionMap<String, Object>) map;
	}
	
	public String login() {
		// si on arrive sans submit (par lien)
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return ActionSupport.LOGIN;
		}
		
		// si on est déjà connecté
		if(sessionMap.get("user_id")!=null) {
			return ActionSupport.SUCCESS;
		}
		
		String mail = this.user.getMail();
		String pwd = this.user.getPwd();
		if(mail != null && pwd != null && !mail.isBlank() && !pwd.isBlank()) {
			ApiDao<Person> personDao = DAOFactory.getInstance().getPersonDao();
			Person filter = new Person();
			filter.setMail(mail);
			filter.setPwd(pwd);
			List<Person> matchUser = personDao.getAll(filter);
			if (matchUser.size() == 1) {
				// authentification ok,  create session
				this.user = matchUser.get(0);
				if(this.user.getStatus() == Status.ON) {
					sessionMap.put("user_id", this.user.getId());
					sessionMap.put("user_admin", this.user.isAdmin());
					return ActionSupport.SUCCESS;					
				}
				else {
			        addFieldError("user.mail", "Ce compte n'est pas actif.");		
					return ActionSupport.LOGIN;
				}
			}
		}
		
		// authentification error
        addFieldError("user.mail", "Erreur d'authentification.");	
		return ActionSupport.LOGIN;
	}
	
	public String logout() {
		sessionMap.remove("user_id");
		sessionMap.remove("user_admin");
		sessionMap.invalidate();
		
		return ActionSupport.SUCCESS;
	}
}

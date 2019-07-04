package action;

import java.time.LocalDateTime;
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

public class RegisterAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -6381560403984436742L;

	public Person user = new Person();
	private SessionMap<String, Object> sessionMap;
	
	@Override
	public void setSession(Map<String, Object> map) {
		sessionMap = (SessionMap<String, Object>) map;
	}
	
	private boolean isAdmin() {
		Boolean user_admin = (Boolean) sessionMap.get("user_admin");
		return ((Boolean) true).equals(user_admin);
	}
	
	public void validate() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return;
		}

		// test champs nulls
		if (this.user.getMail() == null || this.user.getMail().isBlank()) {
	        addFieldError("user.mail", "Le mail ne peut être nul.");			
		}
		if (this.user.getName() == null || this.user.getName().isBlank()) {
	        addFieldError("user.name", "Le nom ne peut être nul.");			
		}
		if (this.user.getPwd() == null || this.user.getPwd().isBlank()) {
	        addFieldError("user.pwd", "Le mot de passe ne peut être nul.");			
		}
		if (this.user.getPwd() != null || this.user.getPwd().length() < 6) {
	        addFieldError("user.pwd", "Le mot de passe doit contenir au minimum 6 charactères.");			
		}
		
		// test mail déjà existant
		ApiDao<Person> personDao = DAOFactory.getInstance().getPersonDao();
		
		Person filter = new Person();
		filter.setMail(this.user.getMail());
		List<Person> matchUser = personDao.getAll(filter);
		if (matchUser.size() > 0) {
	        addFieldError("user.mail", "Cet email est déjà utilisé.");
		}
	}
	
	public String execute() {
		// si déjà connecté & pas admin => redirection profile
		if(sessionMap.get("user_id")!=null && !this.isAdmin()) {
			return ActionSupport.SUCCESS;
		}
		
		// si on arrive sans submit (par lien)
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return ActionSupport.INPUT;
		}
		
		
		// création objet person & sauvegarde BDD
		ApiDao<Person> personDao = DAOFactory.getInstance().getPersonDao();
		
		Integer id;
		this.user.setStatus(Status.ON);
		this.user.setCreation_date(LocalDateTime.now());
		
		id = personDao.save(this.user);
		
		
		// affichage page 
		if (id == null) {
			// erreur création
			return ActionSupport.INPUT;
		}
		else {
			// creation réussie : afficher profile
			this.user.setId(id.intValue());
			return ActionSupport.SUCCESS;
		}
	}
}

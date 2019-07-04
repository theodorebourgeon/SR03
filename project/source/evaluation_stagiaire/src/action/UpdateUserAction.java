package action;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Person;

public class UpdateUserAction  extends ActionSupport implements SessionAware{
	
	private static final long serialVersionUID = -6582464217507673424L;
	
	public Person user = new Person();
	private Integer id;
	private SessionMap<String, Object> sessionMap;
	
	@Override
	public void setSession(Map<String, Object> map) {
		sessionMap = (SessionMap<String, Object>) map;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = null;
	    try {
	        this.id = Integer.parseInt(id);
	      } catch (NumberFormatException nfe){}
	}
	
	private boolean isAdmin() {
		Boolean user_admin = (Boolean) sessionMap.get("user_admin");
		return ((Boolean) true).equals(user_admin);
	}
	
	private int getUserIdToProcess() {
		// si administrateur : on peut choisir un autre profil:
		if(this.isAdmin() && this.id != null) {
			return this.id.intValue();
		}

		return  (int) sessionMap.get("user_id");
	}
	
	public void validate() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return;
		}

		// test champs nulls
		if (this.user.getName() == null || this.user.getName().isBlank()) {
	        addFieldError("user.name", "Le nom ne peut être nul.");			
		}
		if (this.user.getPwd() == null || this.user.getPwd().isBlank()) {
	        addFieldError("user.pwd", "Le mot de passe ne peut être nul.");			
		}
		if (this.user.getPwd() != null || this.user.getPwd().length() < 6) {
	        addFieldError("user.pwd", "Le mot de passe doit contenir au minimum 6 charactères.");			
		}
	}
	
	public String execute() {		
		// création objet person & sauvegarde BDD
		ApiDao<Person> personDao = DAOFactory.getInstance().getPersonDao();
				
		// si consultation (<=> meth = GET), on rempli user & renvoie INPUT => POST
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			
			this.user = personDao.get(this.getUserIdToProcess());
			return ActionSupport.INPUT;
		}
		// sinon : update
		else {
			// on récupère les infos
			Person person = personDao.get(this.getUserIdToProcess());
			
			// on update
			person.setName(this.user.getName());
			person.setPwd(this.user.getPwd());
			person.setCorporation(this.user.getCorporation());
			person.setPhone(this.user.getPhone());
			
			// verif droits de modifier status / admin
			if(isAdmin()) {
				person.setAdmin(this.user.isAdmin());	
				person.setStatus(this.user.getStatus());
			}
			
			personDao.update(person);
			
			this.user = person;
			return ActionSupport.SUCCESS;
		}
	}
}

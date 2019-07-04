package action;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Theme;

public class AddThemeAction extends ActionSupport {

	private static final long serialVersionUID = -5095337226731359650L;
	
	public Theme theme = new Theme();
	

	
	public String execute() {		
		// si on arrive sans submit (par lien)
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return ActionSupport.INPUT;
		}
		
		
		// création objet person & sauvegarde BDD
		ApiDao<Theme> themeDao = DAOFactory.getInstance().getThemeDao();
		
		Integer id;		
		id = themeDao.save(this.theme);
		
		// affichage page 
		if (id == null) {
			// erreur création
			return ActionSupport.INPUT;
		}
		else {
			// creation réussie : afficher profile
			this.theme.setId(id.intValue());
			return ActionSupport.SUCCESS;
		}
	}

}

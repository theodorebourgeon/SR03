package action;

import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Status;
import model.Theme;
import model.Survey;

public class AddSurveyAction extends ActionSupport {

	private static final long serialVersionUID = 1832950700612640071L;
	
	public Survey survey = new Survey();
	
	public List<Theme> getThemes(){
		return DAOFactory.getInstance().getThemeDao().getAll(null);
	}
	
	public void validate() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return;
		}

		// test champs nulls
		if (this.survey.getSubject() == null || this.survey.getSubject().isBlank()) {
	        addFieldError("survey.subject", "Le sujet ne peut être nul.");			
		}
	}
	
	public String execute() {		
		// si on arrive sans submit (par lien)
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return ActionSupport.INPUT;
		}
		
		
		// création objet person & sauvegarde BDD
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		
		Integer id;
		this.survey.setStatus(Status.ON);
		
		id = surveyDao.save(this.survey);
		
		// affichage page 
		if (id == null) {
			// erreur création
			return ActionSupport.INPUT;
		}
		else {
			// creation réussie : afficher profile
			this.survey.setId(id.intValue());
			return ActionSupport.SUCCESS;
		}
	}
}

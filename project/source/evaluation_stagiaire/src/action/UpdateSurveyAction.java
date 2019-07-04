package action;

import java.util.HashMap;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Theme;
import model.Question;
import model.Survey;

public class UpdateSurveyAction extends ActionSupport{
	
	private static final long serialVersionUID = 4628431451535027232L;
	
	public Survey survey = new Survey();
	public HashMap<Integer, Question> questions = new HashMap<Integer, Question>();
	private Integer id;
	
	public Integer getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = null;
	    try {
	        this.id = Integer.parseInt(id);
	      } catch (NumberFormatException nfe){}
	}
	
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
		// Récupération questionnaire
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		Survey old_survey = surveyDao.get(this.id);
		if(old_survey == null) {
			return ActionSupport.ERROR;
		}

		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		for(Survey.Ordered_question oq: old_survey.getQuestions()) {
			int iq = oq.getQuestionId();
			Question q = questionDao.get(iq);
			this.questions.put(oq.getOrder(), q);
		}
		
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			this.survey = old_survey;
			return ActionSupport.INPUT;
		}
		this.survey.setQuestions(old_survey.getQuestions());
		this.survey.setId(old_survey.getId());
		surveyDao.update(this.survey);
		return ActionSupport.SUCCESS;
	}
}

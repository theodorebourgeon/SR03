package action;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Question;
import model.Status;
import model.Survey;
import model.Survey.Ordered_question;

public class AddExistingQuestionAction extends ActionSupport {
	
	private static final long serialVersionUID = -2599071353004898218L;
	
	private Integer question;
	private Integer questionnaire;
	
	
	public void setQuestion(String question) {
		this.question = null;
	    try {
	        this.question = Integer.parseInt(question);
	      } catch (NumberFormatException nfe){}
	}
	
	public int getQuestion() {
		return question;
	}

	public void setQuestionnaire(String questionnaire) {
		this.questionnaire = null;
	    try {
	        this.questionnaire = Integer.parseInt(questionnaire);
	      } catch (NumberFormatException nfe){}
	}
	
	public int getQuestionnaire() {
		return questionnaire;
	}
	
	public String execute() {
		if(this.question == null || this.questionnaire == null) {
			return ActionSupport.ERROR;
		}
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		Question question = questionDao.get(this.question);
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		Survey survey = surveyDao.get(this.questionnaire);
		
		// On verifie que question existe
		if(question == null || survey == null) {
			return ActionSupport.ERROR;
		}

		// ajout ordered_question si la question n'est pas déjà dans le formulaire
		if (!survey.getQuestions().stream().anyMatch(q -> q.getQuestionId() == question.getId())) {
			Ordered_question oq = survey.new Ordered_question();
			oq.setQuestionId(question.getId());
			oq.setStatus(Status.ON);
			oq.setOrder(survey.getLastOrder() +1);
			
			survey.addQuestion(oq);
			surveyDao.update(survey);
		}
		
		return ActionSupport.SUCCESS;
	}
}
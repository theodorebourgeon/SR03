package action;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Status;
import model.Survey;

public class UpdateQuestionRankAction extends ActionSupport {
	
	private static final long serialVersionUID = 4628431451535027232L;

	private Integer questionnaire;
	private Integer question;
	

	public Integer getQuestionnaire() {
		return this.questionnaire;
	}
	public void setQuestionnaire(Integer questionnaire) {
		this.questionnaire = questionnaire;
	}
	public Integer getQuestion() {
		return this.question;
	}
	
	public void setQuestion(Integer question) {
		this.question = question;
	}
	
	public String up() {		
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		Survey survey = surveyDao.get(this.questionnaire);
		if (survey != null) {
			Survey.Ordered_question prev = null;
			for(Survey.Ordered_question q : survey.getQuestions()) {
				if(q.getOrder() == this.question) {
					if(prev != null) {
						q.setOrder(prev.getOrder());
						prev.setOrder(question);	
						surveyDao.update(survey);					
					}
					return ActionSupport.SUCCESS;
				}
				prev = q;
			}
		}
		return ActionSupport.ERROR;
	}
	
	public String down() {		
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		Survey survey = surveyDao.get(this.questionnaire);
		if (survey != null) {
			Survey.Ordered_question current = null;
			Survey.Ordered_question next = null;
			for(Survey.Ordered_question q : survey.getQuestions()) {
				if(q.getOrder() == this.question) {
					current = q;
				}
				else if (current != null) {
					next = q;
					break;
				}
			}
			if(current != null) {
				if(next != null) {
					current.setOrder(next.getOrder());
					next.setOrder(this.question);
					surveyDao.update(survey);
				}
				return ActionSupport.SUCCESS;
			}
		}
		return ActionSupport.ERROR;
	}
	
	public String status() {
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		Survey survey = surveyDao.get(this.questionnaire);
		if (survey != null) {
			for(Survey.Ordered_question q : survey.getQuestions()) {
				if(q.getOrder() == this.question) {
					q.setStatus(Status.switchStatus(q.getStatus()));;
					surveyDao.update(survey);
					return ActionSupport.SUCCESS;
				}
			}
		}
		return ActionSupport.ERROR;	
	}
}



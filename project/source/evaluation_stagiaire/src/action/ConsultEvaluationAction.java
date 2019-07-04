package action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Evaluation;
import model.Person;
import model.Survey;
import model.Question;
import model.Answer;

public class ConsultEvaluationAction  extends ActionSupport implements SessionAware{

	private static final long serialVersionUID = -3787676829028451861L;

	private SessionMap<String, Object> sessionMap;
	private Evaluation evaluation;
	private ArrayList<Question> questions = new ArrayList<Question>();
	private HashMap<Integer, Integer> choicesMap = new HashMap<Integer, Integer>(); ;
	private HashMap<Integer, String> answersMap =  new HashMap<Integer, String>();
	private Integer id;
	private String title;
	private String theme;

	@Override
	public void setSession(Map<String, Object> map) {
		sessionMap = (SessionMap<String, Object>) map;
	}
	
	private boolean isAdmin() {
		Boolean user_admin = (Boolean) sessionMap.get("user_admin");
		return ((Boolean) true).equals(user_admin);
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public String getTrainee() {
		Person p = DAOFactory.getInstance().getPersonDao().get(this.evaluation.getPersonId());
		String name = p.getName();
		return (name != null && !name.isBlank()) ? p.getName() : p.getMail() ;
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
	
	public Evaluation getEvaluation() {
		return evaluation;
	}
	
	public ArrayList<Question> getQuestions() {
		return questions;
	}

	public HashMap<Integer, Integer> getChoicesMap() {
		return choicesMap;
	}

	public HashMap<Integer, String> getAnswersMap() {
		return answersMap;
	}

	public String execute() {
		this.evaluation = this.id == null ? null : DAOFactory.getInstance().getEvaluationDao().get(this.id.intValue());
		
		// si l'évaluation n'existe pas, ou l'utilisateur n'est pas autorisé à la consulter => erreur
		if(this.evaluation == null || (!this.isAdmin() && evaluation.getPersonId() != (int) sessionMap.get("user_id"))) {
			return ActionSupport.ERROR;
		}
		
		// chargement du questionnaire
		Survey survey = DAOFactory.getInstance().getSurveyDao().get(this.evaluation.getSurveyId());
		this.title = survey.getSubject();
		this.theme = DAOFactory.getInstance().getThemeDao().get(survey.getThemeId()).getTitle();
		
		// chargement des questions
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		ApiDao<Answer> answerDao = DAOFactory.getInstance().getAnswerDao();
		for (Survey.Ordered_question oq : survey.getQuestions()) {
			// si la question est présente dans l'évaluation, on l'ajoute
			if(this.evaluation.getChoices().containsKey(oq.getQuestionId())) {
				// add question
				Question q = questionDao.get(oq.getQuestionId());
				this.questions.add(q);
				// add choice
				this.choicesMap.put(oq.getQuestionId(), this.evaluation.getChoices().get(oq.getQuestionId()));
				// add answers
				for(Question.Ordered_answer oa : q.getAnswers()) {
					// if not in hashmap, we add
					if(!this.answersMap.containsKey(oa.getAnswerId())) {
						this.answersMap.put(oa.getAnswerId(), answerDao.get(oa.getAnswerId()).getContent());
					}
				}
			}
		}
		
		return ActionSupport.SUCCESS;
	}

}

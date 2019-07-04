package action;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Answer;
import model.Evaluation;
import model.Question;
import model.Status;
import model.Survey;

public class SurveyAttemptAction extends ActionSupport implements SessionAware  {

	private static final long serialVersionUID = 3581827808428267486L;
	
	private SessionMap<String, Object> sessionMap;
	public Evaluation evaluation;
	private Survey questionnaire;
	private Integer questionId;
	private Integer answerId;
	
	private String title;
	private ArrayList<Answer> answers;
	
	@Override
	public void setSession(Map<String, Object> map) {
		sessionMap = (SessionMap<String, Object>) map;
	}
	
	public void setQuestionnaire(String questionnaire) {
		this.questionnaire = null;
	    try {
			ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
			this.questionnaire = surveyDao.get(Integer.parseInt(questionnaire));
	      } catch (NumberFormatException nfe){}
	}
	
	public Survey getQuestionnaire() {
		return questionnaire;
	}
	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}

	public Integer getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Integer answerId) {
		this.answerId = answerId;
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	
	public void loadQuestionContent(){
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		ApiDao<Answer> answerDao = DAOFactory.getInstance().getAnswerDao();
		Question question = questionDao.get(this.questionId);
		// set question title 
		this.title = question.getContent();
		// set answers
		this.answers = new ArrayList<Answer>();
		for(Question.Ordered_answer oa: question.getAnswers()) {
			if(oa.getStatus() == Status.ON)
				this.answers.add(answerDao.get(oa.getAnswerId()));
		}
	}
	
	public void validate() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return;
		}
		
		if(this.answerId == null) {
	        addFieldError("answerId", "Vous devez séléctionner une réponse");	
		}
	}
	
	public String execute() {
		if(this.questionnaire == null) {
			return ActionSupport.ERROR;
		}
		
		// on récupère une éventuelle évaluation en session
		this.evaluation = (Evaluation) sessionMap.get("survey_attempt");
		if(this.evaluation != null && this.evaluation.getSurveyId() == this.questionnaire.getId()) {
			// un essai était en cours, on le récupère
		}
		else {
			// on crée une évaluation & on la met en session
			sessionMap.remove("survey_attempt");
			
			this.evaluation = new Evaluation();
			this.evaluation.setPersonId((int) sessionMap.get("user_id"));
			this.evaluation.setSurveyId(this.questionnaire.getId());
			this.evaluation.setDateStart(LocalDateTime.now());
			sessionMap.put("survey_attempt", this.evaluation);
		}
		
		// si post & valeurs non nulles => on enregistre réponse à la question
		if (ServletActionContext.getRequest().getMethod().equals("POST") && this.questionId != null && this.answerId != null) {
			// check question & answer coherent
			Question question = this.questionId != null ? DAOFactory.getInstance().getQuestionDao().get(this.questionId) : null;
			if(question == null || !question.getAnswers().stream().anyMatch(a -> a.getAnswerId() == this.answerId)) {
		        addFieldError("answerId", "Une erreur s'est produite, réessayez.");					
			}
			else {
				// Question & answer exists, add choice
				this.evaluation.addChoice(this.questionId, this.answerId);

				// if right answer : score +1
				if(question.getIdRightAnswer() == this.answerId) {
					this.evaluation.incrementScore();
				}
				sessionMap.put("survey_attempt", this.evaluation);
			}
		}
		
		// on cherche première question active sans réponse
		Integer nextQuestion = null;
		for(Survey.Ordered_question oq: questionnaire.getQuestions()) {
			if(oq.getStatus() == Status.ON && !this.evaluation.getChoices().containsKey(oq.getQuestionId())) {
				nextQuestion = oq.getQuestionId();
				break;
			}
		}
		
		// s'il reste des questions sans réponse
		if(nextQuestion != null) {
			this.questionId = nextQuestion.intValue();
			this.answerId = null;
			
			this.loadQuestionContent();
			
			return ActionSupport.INPUT;
		}
		else {
			// sinon : on enregistre l'evaluation
			this.evaluation.setDateStop(LocalDateTime.now());
			this.evaluation.setScore(this.evaluation.getScore() / questionnaire.getNbActiveQuestion());
			
			ApiDao<Evaluation> evaluationDao = DAOFactory.getInstance().getEvaluationDao();
			this.evaluation.setId(evaluationDao.save(this.evaluation));
			
			sessionMap.remove("survey_attempt");
			
			return ActionSupport.SUCCESS;
		}
	}

}

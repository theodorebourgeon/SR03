package action;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Survey;
import model.Survey.Ordered_question;
import model.Question.Ordered_answer;
import model.Question;
import model.Status;
import model.Answer;

public class AddQuestionAction extends ActionSupport {

	private static final long serialVersionUID = -5144755479260531940L;
	
	private Integer id;
	private String content;
	private ArrayList<String> answers = new ArrayList<String>();
	private Integer correctAnswerIndex;
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setCorrectAnswerIndex(Integer correctAnswerIndex) {
		this.correctAnswerIndex = correctAnswerIndex;
	}
	
	public Integer getCorrectAnswerIndex() {
		return correctAnswerIndex;
	}
	
	public void setAnswers(ArrayList<String> answers) {
		this.answers = answers;
	}
	
	public ArrayList<String> getAnswers() {
		return answers;
	}
	
	public void validate() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return;
		}
		
		if (this.content == null || this.content.isBlank()) {
	        addFieldError("content", "L'intitulé de la question ne peut être vide.");			
		}
		
		if (this.correctAnswerIndex == null) {
	        addFieldError("answers", "Une réponse doit être cochée correcte.");			
		}
		
		answers.forEach(ans -> {
			if (ans == null || ans.isBlank()) {
		        addFieldError("answers", "Une réponse ne peut être vide.");			
			}
		});
	}
	
	public String addQuestion() {
		if(this.id == null) {
			return ActionSupport.ERROR;
		}
		
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		Survey survey = surveyDao.get(this.id);
		
		// on vï¿½rifie questionnaire existe
		if(survey == null) {
			return ActionSupport.ERROR;
		}
		
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			
			return ActionSupport.INPUT;
		}
		
		// prï¿½paration quesiton
		Question question = new Question();
		question.setContent(this.content);
		
		// ajout rï¿½ponses
		ApiDao<Answer> answerDao = DAOFactory.getInstance().getAnswerDao();
		for (int i = 0; i < this.answers.size(); i ++) {
			Answer answer = new Answer();
			answer.setContent(this.answers.get(i));
			
			List<Answer> exists = answerDao.getAll(answer);
			if(!exists.isEmpty()) {
				answer.setId(exists.get(0).getId());
			}
			else {
				answer.setId(answerDao.save(answer));
			}
			
			// ajout ordered_answers
			Ordered_answer oa = question.new Ordered_answer();
			oa.setAnswerId(answer.getId());
			oa.setCorrect(this.correctAnswerIndex == i);
			oa.setOrder(i);
			oa.setStatus(Status.ON);

			question.addAnswer(oa);
		}
		
		// ajout question
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		question.setId(questionDao.save(question));
		
		// ajout ordered_question
		Ordered_question oq = survey.new Ordered_question();
		oq.setQuestionId(question.getId());
		oq.setOrder(survey.getLastOrder() +1);
		oq.setStatus(Status.ON);
		survey.addQuestion(oq);
		surveyDao.update(survey);
		
		return ActionSupport.SUCCESS;
	}
	
	@SkipValidation
	public String addAnswer() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return ActionSupport.ERROR;
		}
		this.answers.add("");
		return ActionSupport.SUCCESS;
	}
}

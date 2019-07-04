package action;

import java.util.HashMap;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Question;
import model.Answer;

public class UpdateQuestionAction extends ActionSupport{
	
	private static final long serialVersionUID = 4628431451535027232L;
	
	public Question question = new Question();
	public HashMap<Integer, Answer> answers = new HashMap<Integer, Answer>();
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
	
	public List<Question> getQuestions(){
		return DAOFactory.getInstance().getQuestionDao().getAll(null);
	}
	
	public void validate() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return;
		}

		// test champs nulls
		if (this.question.getContent() == null || this.question.getContent().isBlank()) {
	        addFieldError("survey.subject", "La question ne peut pas etre nulle.");			
		}
	}
	
	public String execute() {		
		// R�cup�ration questionnaire
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		Question old_question = questionDao.get(this.id);
		if(old_question == null) {
			return ActionSupport.ERROR;
		}

		ApiDao<Answer> answerDao = DAOFactory.getInstance().getAnswerDao();
		for(Question.Ordered_answer oq: old_question.getAnswers()) {
			int iq = oq.getAnswerId();
			Answer a = answerDao.get(iq);
			this.answers.put(oq.getOrder(), a);
		}
		
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			this.question = old_question;
			return ActionSupport.INPUT;
		}
		this.question.setAnswers(old_question.getAnswers());
		this.question.setId(old_question.getId());
		questionDao.update(this.question);
		return ActionSupport.SUCCESS;
	}
}

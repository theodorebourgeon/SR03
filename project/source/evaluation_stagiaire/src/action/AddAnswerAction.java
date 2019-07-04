package action;

import java.util.List;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Question.Ordered_answer;
import model.Question;
import model.Status;
import model.Answer;

public class AddAnswerAction extends ActionSupport {

	private static final long serialVersionUID = -5144755479260531940L;
	
	private Integer id;
	private String content;
	
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
	
	public void validate() {
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return;
		}
		
		if (this.content == null || this.content.isBlank()) {
	        addFieldError("content", "Le contenu de de la réponse ne peut être vide.");			
		}
	}
	
	public String execute() {
		if(this.id == null) {
			return ActionSupport.ERROR;
		}
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		Question question = questionDao.get(this.id);
		
		// On verifie que question existe
		if(question == null) {
			return ActionSupport.ERROR;
		}
		
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			return ActionSupport.INPUT;
		}

		// ajout une reponse
		ApiDao<Answer> answerDao = DAOFactory.getInstance().getAnswerDao();
		Answer answer = new Answer();
		answer.setContent(this.content);
		
		// si la réponse existe déjà, on la récupère plutôt qu'en re-créer une
		List<Answer> exists = answerDao.getAll(answer);
		if(!exists.isEmpty()) {
			answer.setId(exists.get(0).getId());
		}
		else {
			answer.setId(answerDao.save(answer));
		}
		
		// ajout ordered_answer
		Ordered_answer oa = question.new Ordered_answer();
		oa.setAnswerId(answer.getId());
		oa.setOrder(question.getLastOrder() +1);
		oa.setCorrect(false);
		oa.setStatus(Status.ON);

		question.addAnswer(oa);
		questionDao.update(question);
		
		return ActionSupport.SUCCESS;
	}
}
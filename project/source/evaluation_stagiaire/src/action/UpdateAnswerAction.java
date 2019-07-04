package action;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Answer;

public class UpdateAnswerAction extends ActionSupport {

	private static final long serialVersionUID = 4297531486322083660L;
	
	private Integer id;
	private Integer question;
	private String content;
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getQuestion() {
		return this.question;
	}
	
	public void setQuestion(String question) {
		this.question = null;
	    try {
	        this.question = Integer.parseInt(question);
	      } catch (NumberFormatException nfe){}
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
	        addFieldError("content", "L'intitulé de la question ne peut être vide.");			
		}
	}
	
	public String execute() {
		if(this.id == null) {
			System.out.println("err2");
			return ActionSupport.ERROR;
		}

		ApiDao<Answer> answerDao = DAOFactory.getInstance().getAnswerDao();
		Answer answer = answerDao.get(this.id);

		if(answer == null) {
			System.out.println("err1");
			return ActionSupport.ERROR;
		}
		
		if (ServletActionContext.getRequest().getMethod().equals("GET")) {
			this.content = answer.getContent();
			return ActionSupport.INPUT;
		}
		
		// maj reponse
		answer.setContent(this.content);
		answerDao.update(answer);
		
		System.out.println("success");
		
		return ActionSupport.SUCCESS;
	}
	
}
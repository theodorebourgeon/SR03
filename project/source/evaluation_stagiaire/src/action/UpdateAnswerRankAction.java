package action;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Question;
import model.Status;

public class UpdateAnswerRankAction extends ActionSupport {

	private static final long serialVersionUID = 3085581001114286174L;
	
	private Integer question;
	private Integer reponse;
	
	public Integer getQuestion() {
		return this.question;
	}
	
	public void setQuestion(Integer question) {
		this.question = question;
	}
	
	public Integer getReponse() {
		return this.reponse;
	}
	
	public void setReponse(Integer reponse) {
		this.reponse = reponse;
	}
	
	public String up() {		
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		Question quest = questionDao.get(this.question);
		if (quest != null) {
			Question.Ordered_answer prev = null;
			for(Question.Ordered_answer a : quest.getAnswers()) {
				if(a.getOrder() == this.reponse) {
					if(prev != null) {
						a.setOrder(prev.getOrder());
						prev.setOrder(reponse);	
						questionDao.update(quest);					
					}
					return ActionSupport.SUCCESS;
				}
				prev = a;
			}
		}
		return ActionSupport.ERROR;
	}
	
	public String down() {		
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		Question quest = questionDao.get(this.question);
		if (quest != null) {
			Question.Ordered_answer current = null;
			Question.Ordered_answer next = null;
			for(Question.Ordered_answer a : quest.getAnswers()) {
				if(a.getOrder() == this.reponse) {
					current = a;
				}
				else if (current != null) {
					next = a;
					break;
				}
			}
			if(current != null) {
				if(next != null) {
					current.setOrder(next.getOrder());
					next.setOrder(this.reponse);
					questionDao.update(quest);
				}
				return ActionSupport.SUCCESS;
			}
		}
		return ActionSupport.ERROR;
	}
	
	public String status() {
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		Question quest = questionDao.get(this.question);
		if (quest != null) {
			for(Question.Ordered_answer a : quest.getAnswers()) {
				if(a.getOrder() == this.reponse) {
					if(!a.isCorrect()) {
						a.setStatus(Status.switchStatus(a.getStatus()));;
						questionDao.update(quest);						
					}
					return ActionSupport.SUCCESS;
				}
			}
		}
		return ActionSupport.ERROR;	
	}
	
	public String right() {
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		Question quest = questionDao.get(this.question);
		int changes = 0;
		if (quest != null) {
			for(Question.Ordered_answer a : quest.getAnswers()) {
				if(a.isCorrect()) {
					if(a.getOrder() != this.reponse) {
						a.setCorrect(false);
						changes++;
					}
					else {
						// nothing to change
						return ActionSupport.SUCCESS;
					}
				}
				else if(a.getOrder() == this.reponse) {
					if(a.getStatus() == Status.OFF) {
						// renvoyer erreur ? print quelque chose?
						return ActionSupport.ERROR;
					}
					a.setCorrect(true);
					changes ++;
				}
				if (changes == 2) {
					questionDao.update(quest);
					return ActionSupport.SUCCESS;					
				}
			}
		}
		return ActionSupport.ERROR;	
	}
}



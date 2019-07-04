package action;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Question;

public class ListQuestionsAction extends ActionSupport{
	
	private static final long serialVersionUID = -905809902072521550L;
	
	public List<Question> questions = new ArrayList<Question>();
	public String filtre;
	
	public int getCount() {
		return this.questions.size();
	}
	
	public String execute() {
		ApiDao<Question> questionDao = DAOFactory.getInstance().getQuestionDao();
		
		List<Question> allQuestions = questionDao.getAll(null);
		
		String f = filtre != null ? filtre.toUpperCase() : "";
		
		allQuestions.forEach(s -> {
			boolean contentOK = s.getContent().toUpperCase().contains(f);
			
			if(contentOK) {
				this.questions.add(s);
			}					
		});
		
		return ActionSupport.SUCCESS;
	}
}

package action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Evaluation;
import model.Person;
import model.Survey;
import model.Theme;

public class ListEvaluationAction extends ActionSupport implements SessionAware{

	private static final long serialVersionUID = -856826215772347545L;
	
	private static final int NB_EVALS = 10;
	
	private SessionMap<String, Object> sessionMap;
	private Integer id;
	private Integer quest;
	public int page=1;
	public int nbPages;
	
	private HashMap<Integer, String> surveysMap =  new HashMap<Integer, String>();
	private HashMap<Integer, String> themesMap =  new HashMap<Integer, String>();
	private HashMap<Integer, String> usersMap =  new HashMap<Integer, String>();
	public List<Evaluation> evaluations = new ArrayList<Evaluation>();

	@Override
	public void setSession(Map<String, Object> map) {
		sessionMap = (SessionMap<String, Object>) map;
	}
	
	public void setId(String id) {
		this.id = null;
	    try {
	        this.id = Integer.parseInt(id);
	      } catch (NumberFormatException nfe){}
	}
	
	private boolean isAdmin() {
		Boolean user_admin = (Boolean) sessionMap.get("user_admin");
		return ((Boolean) true).equals(user_admin);
	}
	
	public Integer getUserIdToProcess() {
		// si administrateur : on peut choisir un autre profil:
		if(this.isAdmin()) {
			return this.id;
		}

		return  (int) sessionMap.get("user_id");
	}

	public Integer getQuest() {
		return this.quest;
	}
	
	public void setQuest(String quest) {
		this.quest = null;
	    try {
	        this.quest = Integer.parseInt(quest);
	      } catch (NumberFormatException nfe){}
	}
	
	public int getNbPages() {
		return this.nbPages;
	}
	
	public int getPage() {
		return this.page;
	}
	
	public void setPage(String page) {
		this.page = 1;
	    try {
	        this.page = Integer.parseInt(page);
	      } catch (NumberFormatException nfe){}
	}
	
	public HashMap<Integer, String> getSurveysMap() {
		return surveysMap;
	}

	public HashMap<Integer, String> getThemesMap() {
		return themesMap;
	}
	
	public HashMap<Integer, String> getUsersMap() {
		return usersMap;
	}

	public String getTraineeName(int id) {
		Person p = DAOFactory.getInstance().getPersonDao().get(id);
		String name = p.getName();
		return (name != null && !name.isBlank()) ? p.getName() : p.getMail() ;
	}
	
	public String getIdName() {
		return getTraineeName(getUserIdToProcess());
	}
	
	public String getQuestName() {
		return this.quest != null ? DAOFactory.getInstance().getSurveyDao().get(this.quest).getSubject() : "";
	}

	public String execute() {
		Integer procededUser = getUserIdToProcess();
		Evaluation filter = new Evaluation();
		filter.setPersonId(procededUser);
		filter.setSurveyId(this.quest);
		this.evaluations = DAOFactory.getInstance().getEvaluationDao().getAll(filter);
		
		// pagination
		this.nbPages = (int) Math.ceil(this.evaluations.size() * 1.0 / NB_EVALS);
        if(this.page > this.nbPages)
        	this.page = this.nbPages;
		
		int start = NB_EVALS*((this.page < 1 ? 1 : this.page) - 1);
		int stop = Math.min(start+NB_EVALS, this.evaluations.size());
		
	    try {
			this.evaluations = this.evaluations.subList(start, stop);
	      } catch (IndexOutOfBoundsException e){System.out.print(e.getMessage());}
		
		// chargement noms questionnaires & themes
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		ApiDao<Theme> themeDao = DAOFactory.getInstance().getThemeDao();
		
		for(Evaluation ev : this.evaluations) {
			if(!this.surveysMap.containsKey(ev.getSurveyId())) {
				Survey s = surveyDao.get(ev.getSurveyId());
				this.surveysMap.put(ev.getSurveyId(), s.getSubject());
				this.themesMap.put(ev.getSurveyId(), themeDao.get(s.getThemeId()).getTitle());
			}
			if(procededUser==null && !this.usersMap.containsKey(ev.getPersonId())) {
				this.usersMap.put(ev.getPersonId(), getTraineeName(ev.getPersonId()));				
			}
		}
		
		return ActionSupport.SUCCESS;
	}
}

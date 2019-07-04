package action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

import dao.ApiDao;
import dao.DAOFactory;
import model.Theme;
import model.Evaluation;
import model.Survey;

public class ListSurveysAction extends ActionSupport implements SessionAware{
	
	private static final long serialVersionUID = -905809902072521550L;
	
	private static final int NB_SURVEYS = 10;
	
	private SessionMap<String, Object> sessionMap;
	public List<Survey> surveys = new ArrayList<Survey>();
	public List<Theme> themes = new ArrayList<Theme>();
	public String filtre;
	public int page=1;
	public int nbPages;
	public int NbSurveyAdmin;
	public ArrayList<Integer> NbSurveyAdminList = new ArrayList<Integer>();
	public int NbSurveyUser;
	public ArrayList<Integer> NbSurveyUserList = new ArrayList<Integer>();
	
	@Override
	public void setSession(Map<String, Object> map) {
		sessionMap = (SessionMap<String, Object>) map;
	}
	
	public int getCount() {
		return this.surveys.size();
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
	
	public String execute() {
		ApiDao<Survey> surveyDao = DAOFactory.getInstance().getSurveyDao();
		ApiDao<Theme> themeDao = DAOFactory.getInstance().getThemeDao();
		ApiDao<Evaluation> evaluationDao = DAOFactory.getInstance().getEvaluationDao();
		
		List<Survey> allSurveys = surveyDao.getAll(null);
		List<Theme> allThemes = themeDao.getAll(null);
		List<Evaluation> allEvaluations = evaluationDao.getAll(null);
		
		String f = filtre != null ? filtre.toUpperCase() : "";
		
		allSurveys.forEach(s -> {
			NbSurveyUser = 0;
			NbSurveyAdmin = 0;
			Theme associatedTheme = allThemes.stream().filter(t -> t.getId() == s.getThemeId()).findAny().get();

			boolean subjectOK = s.getSubject().toUpperCase().contains(f);
			boolean themeOK = associatedTheme.getTitle().toUpperCase().contains(f);
			
			allEvaluations.forEach(e -> {
				if(e.getSurveyId() == s.getId()) {
					if(e.getPersonId() == (int) sessionMap.get("user_id")) {
						NbSurveyUser +=1;
					}
					NbSurveyAdmin += 1;
				}
			});
			NbSurveyUserList.add(NbSurveyUser);
			NbSurveyAdminList.add(NbSurveyAdmin);
			if(subjectOK || themeOK) {
				this.surveys.add(s);
				this.themes.add(associatedTheme);				
			}		
		});
		
		// pagination
		this.nbPages = (int) Math.ceil(this.surveys.size() * 1.0 / NB_SURVEYS);
        if(this.page > this.nbPages)
        	this.page = this.nbPages;
		
		int start = NB_SURVEYS*((this.page < 1 ? 1 : this.page) - 1);
		int stop = Math.min(start+NB_SURVEYS, this.surveys.size());
		
	    try {
			this.surveys = this.surveys.subList(start, stop);
	      } catch (IndexOutOfBoundsException e){System.out.print(e.getMessage());}
		
		return ActionSupport.SUCCESS;
	}
}

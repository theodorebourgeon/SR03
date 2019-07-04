package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;


public class Evaluation {
	private Integer id;
	private Integer personId;
	private Integer surveyId;
	private LocalDateTime dateStart;
	private LocalDateTime dateStop;
	private double score;
	private HashMap<Integer, Integer> choices; // key = questionId, value= answerId
	
	public Evaluation() {
		this.choices = new HashMap<Integer, Integer>();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPersonId() {
		return personId;
	}
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}
	public Integer getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}
	public LocalDateTime getDateStart() {
		return dateStart;
	}
	public void setDateStart(LocalDateTime dateStart) {
		this.dateStart = dateStart;
	}
	public LocalDateTime getDateStop() {
		return dateStop;
	}
	public void setDateStop(LocalDateTime dateStop) {
		this.dateStop = dateStop;
	}
	public String getDuration() {
		Duration duration = Duration.between(this.dateStart, this.dateStop);		
		long days = duration.toDays();
		int hours = duration.toHoursPart();
		int minutes = duration.toMinutesPart();
		int seconds = duration.toSecondsPart();
		
		String result = days > 0 ? days + "j, " : "";
		result += (hours > 0 ? hours + " : " + String.format("%02d", minutes) + " : " : minutes + (minutes > 1 ? "mins " : "min "))
				+ String.format("%02d", seconds)+ "s";
		
		return result;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public void incrementScore() {
		this.score = score + 1;
	}
	
	public HashMap<Integer, Integer> getChoices() {
		return choices;
	}
	public void setChoices(HashMap<Integer, Integer> choices) {
		this.choices = choices;
	}
	public void addChoice(int questionId, Integer answerId) {
		this.choices.put(questionId, answerId);
	}
}

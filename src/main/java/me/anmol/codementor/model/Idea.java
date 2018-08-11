package me.anmol.codementor.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Idea implements Serializable {

	private static final long serialVersionUID = -295453018728346493L;

	@Id	
	private String id;

	private String content;

	private int impact;

	private int ease;

	private int confidence;

	private long created_at;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private IdeaPoolUser user;
	
	public Idea(String content, int impact, int ease, int confidence, IdeaPoolUser user) {
		super();
		this.content = validatedContent(content);
		this.impact = validatedScore(impact);
		this.ease = validatedScore(ease);
		this.confidence = validatedScore(confidence);
		this.created_at = System.currentTimeMillis();
		this.user = Objects.requireNonNull(user, "User must not be null.");
		this.id = UUID.randomUUID().toString();
	}

	private String validatedContent(String content2) {
		if(content2 == null){
			throw new InvalidDataException("Content must not be null.");
		}		
		int length = content2.length();
		if (length == 0 || length > 255) {
			throw new InvalidDataException(
					"Content must be between [1,255], but given content was of length: "
							+ length);
		}
		return content2;
	}
	
	private int validatedScore(int score){
		if(score <= 0 || score > 10) {
			throw new InvalidDataException("Score must be between [1,10] but it was: " + score);
		}
		return score;
	}

	public String getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public int getImpact() {
		return impact;
	}

	public int getEase() {
		return ease;
	}

	public int getConfidence() {
		return confidence;
	}

	public long getCreated_at() {
		return created_at;
	}

	public double getAverage_score() {
		double average = (impact + ease + confidence)/3.0d;
		return average;
	}

	public Idea updateSelf(String content, int impact, int ease, int confidence) {
		this.content = validatedContent(content);
		this.impact = validatedScore(impact);
		this.ease = validatedScore(ease);
		this.confidence = validatedScore(confidence);
		return this;
	}

	@Deprecated
	Idea() {
		super();	
	}
	
	public long userId(){
		return user.id();
	}

}

package me.anmol.codementor.model;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class UserToken implements Serializable {

	private static final long serialVersionUID = -4961706209605099773L;

	@Id
	private long pointId;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> tokens;

	private static final ZoneOffset offset = ZoneOffset.of("+05:30");

	private UserToken(long key, long issueTime, String value) {
		super();
		this.pointId = key;
		this.addToken(issueTime, value);
	}

	@Deprecated
	UserToken() {
		super();
	}

	public long getPointId() {
		return pointId;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public boolean addToken(long issueTime, String value) {
		checkIssueTimeIsValid(issueTime);
		String token = new String(value);
		if (tokens == null) {
			tokens = new ArrayList<>();
		}
		tokens.add(token);
		return true;
	}

	public static UserToken createNewUserToken(long key, long issueTime, String value) {
		Objects.requireNonNull(value, "Token value must not be null.");
		checkIssueTimeIsValid(issueTime);
		return new UserToken(key, issueTime, value);
	}

	private static void checkIssueTimeIsValid(long issueTime) {
		long currentTime = LocalDateTime.now().toEpochSecond(offset);
		if (currentTime - issueTime > 3600) {
			throw new IllegalArgumentException("Token can not be issued with a time in past more than 1 hour");
		}
	}
}

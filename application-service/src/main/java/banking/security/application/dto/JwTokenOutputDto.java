package banking.security.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwTokenOutputDto {

	@JsonProperty("access_token")
	private String accessToken;
	
	@JsonProperty("token_type")
	private String tokenType;
	
	@JsonProperty("expires_in")
	private long expiresIn;
	
	@JsonProperty("refresh_token")
	private String refreshToken;
	
	private String scope;

	public JwTokenOutputDto() {
		super();
	}

	public JwTokenOutputDto(String accessToken, String tokenType, long expiresIn, String refreshToken,
			String scope) {
		super();
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
		this.scope = scope;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getScope() {
		return scope;
	}

	
}

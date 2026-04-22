package com.Sorensen.FitMark.security.ratelimit;

public class RateLimitExceededException extends RuntimeException {

	public RateLimitExceededException(String message) {
		super(message);
	}
}


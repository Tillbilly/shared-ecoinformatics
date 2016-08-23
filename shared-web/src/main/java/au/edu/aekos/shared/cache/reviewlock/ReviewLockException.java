package au.edu.aekos.shared.cache.reviewlock;

public class ReviewLockException extends Exception{

	private static final long serialVersionUID = 2955153516781328727L;

	public ReviewLockException() {
		super();
	}

	public ReviewLockException(String message) {
		super(message);
	}

}

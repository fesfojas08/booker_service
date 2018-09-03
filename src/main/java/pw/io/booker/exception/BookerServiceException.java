package pw.io.booker.exception;

public class BookerServiceException extends RuntimeException {
	
	private final String userFriendlyErrorMessage;
	
	public BookerServiceException(Exception e, String userFriendlyErrorMessage) {
		super(e);
		this.userFriendlyErrorMessage = userFriendlyErrorMessage;
	}
	
	public BookerServiceException(String userFriendlyErrorMessage) {
		this.userFriendlyErrorMessage = userFriendlyErrorMessage;
	}
	
	@Override
	public String getMessage() {
		return userFriendlyErrorMessage;
	}
}

package org.autenticacao.service.dao.exception;

/**
 * <<JAVADOC>>
 *
 * @author mcezario
 *
 */
public class DaoException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Construtor.
	 */
	public DaoException() {
		super();
	}

	/**
	 * Construtor.
	 *
	 * @param message
	 *            A mensagem de erro.
	 */
	public DaoException(String message) {
		super(message);
	}

	/**
	 * Construtor.
	 *
	 * @param throwable
	 *            O erro.
	 */
	public DaoException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Construtor.
	 *
	 * @param message
	 *            A mensagem de erro.
	 * @param entity
	 *            A entidade em que aconteceu o erro.
	 * @param throwable
	 *            O erro.
	 */
	@SuppressWarnings("rawtypes")
	public DaoException(String message, Class entity, Throwable throwable) {
		super(message + ". Entity[" + entity + "].", throwable);
	}

}
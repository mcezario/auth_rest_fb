package org.autenticacao.service.dao.exception;

/**
 * Classe para subir exceções de DAO não verificadas.
 *
 * @author Maikon
 *
 */
public class DaoRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construtor.
	 */
	public DaoRuntimeException() {
		super();
	}

	/**
	 * Construtor.
	 *
	 * @param message
	 *            A mensagem do erro.
	 */
	public DaoRuntimeException(String message) {
		super(message);
	}

	/**
	 * Construtor.
	 *
	 * @param throwable
	 *            O erro.
	 */
	public DaoRuntimeException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Construtor.
	 *
	 * @param message
	 *            A mensagem do erro.
	 * @param entity
	 *            A entidade em que aconteceu o erro.
	 * @param throwable
	 *            O erro.
	 */
	public DaoRuntimeException(String message, String entity,
			Throwable throwable) {
		super(message + " Entity[" + entity + "].", throwable);
	}

}
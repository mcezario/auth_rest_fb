package org.autenticacao.service.exception;

/**
 * Classe para subir excecoes de serivicos verificadas.
 *
 * @author Maikon
 *
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message;

	/**
	 * Construtor.
	 */
	public ServiceException() {
		super();
	}

	/**
	 * Construtor.
	 *
	 * @param message
	 *            A mensagem de erro.
	 */
	public ServiceException(String message) {
		super(message);
		this.message = message;
	}

	/**
	 * Construtor.
	 *
	 * @param throwable
	 *            O erro.
	 */
	public ServiceException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Construtor.
	 *
	 * @param message
	 *            A mensagem de erro.
	 * @param throwable
	 *            O erro.
	 */
	public ServiceException(String message, Throwable throwable) {
		super(message, throwable);
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		return this.message;
	}

}
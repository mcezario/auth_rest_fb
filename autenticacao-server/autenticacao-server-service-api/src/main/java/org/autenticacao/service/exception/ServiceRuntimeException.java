package org.autenticacao.service.exception;

/**
 * Classe para subir excecoes de serivicos nao verificadas.
 *
 * @author Maikon
 *
 */
public class ServiceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

	/**
	 * Construtor.
	 */
	public ServiceRuntimeException() {
		super();
	}

	/**
	 * Construtor.
	 *
	 * @param message
	 *            A mensagem de erro.
	 */
	public ServiceRuntimeException(String message) {
		super(message);
		this.message = message;
	}

	/**
	 * Construtor.
	 *
	 * @param throwable
	 *            O erro.
	 */
	public ServiceRuntimeException(Throwable throwable) {
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
	public ServiceRuntimeException(String message, Throwable throwable) {
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
package org.autenticacao.service.api;

import org.autenticacao.service.exception.ServiceException;
import org.autenticacao.service.model.Usuario;

public interface IUsuarioService extends IService {
	
	public Usuario login(String email, String senha) throws ServiceException;
	
	public Usuario loginFacebook(String email) throws ServiceException;
	
	public Long adicionar(Usuario usuario) throws ServiceException;
	
	public boolean existeUsuarioCadastradoComEmail(String email);
	
}
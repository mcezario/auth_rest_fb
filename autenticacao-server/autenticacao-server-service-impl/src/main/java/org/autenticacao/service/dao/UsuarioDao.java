package org.autenticacao.service.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.autenticacao.service.model.Usuario;

@Stateless
@LocalBean
public class UsuarioDao extends GenericDao<Usuario> {

	public Usuario procuraUsuarioPorEmailESenha(String email, String senha) {
		final String query = "SELECT e FROM Usuario e WHERE e.email = :email AND e.senha = :senha";
    	return super.getQueryUnique(query, email, senha);
	}
	
	public Usuario procuraUsuarioPorEmail(String email) {
		final String query = "SELECT e FROM Usuario e WHERE e.email = :email";
    	return super.getQueryUnique(query, email);
	}

}
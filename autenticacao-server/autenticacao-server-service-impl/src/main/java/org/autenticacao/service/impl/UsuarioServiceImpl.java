package org.autenticacao.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.autenticacao.service.api.IUsuarioService;
import org.autenticacao.service.dao.UsuarioDao;
import org.autenticacao.service.exception.ServiceException;
import org.autenticacao.service.model.Usuario;

/**
 * 
 * @author mcezario
 *
 */
@Stateless
@Local(IUsuarioService.class)
public class UsuarioServiceImpl implements IUsuarioService {

	@EJB
	private UsuarioDao usuarioDao;
	
	@Override
	public Usuario login(String email, String senha) throws ServiceException {
		Usuario login = null;
		try {
			String senhaMD5 = generateMD5(senha);
			login = usuarioDao.procuraUsuarioPorEmailESenha(email, senhaMD5);
			if (login != null) {
				login.setToken(generateToken(login));
				login.setUltimoAcesso(new Date());
				
				usuarioDao.update(login);
			}
		} catch (Exception e) {
			throw new ServiceException("Erro ao realizar login.");
		}
		if (login == null) {
			throw new ServiceException("Usuário e/ou senha incorreto(s).");	
		}
		return login;
	}
	
	@Override
	public Usuario loginFacebook(String email) throws ServiceException {
		Usuario login = null;
		try {
			login = usuarioDao.procuraUsuarioPorEmail(email);
			if (login != null) {
				login.setToken(generateToken(login));
				login.setUltimoAcesso(new Date());
				
				usuarioDao.update(login);
			}
		} catch (Exception e) {
			throw new ServiceException("Erro ao realizar login.");
		}
		if (login == null) {
			throw new ServiceException("Usuário incorreto.");	
		}
		return login;
	}

	@Override
	public Long adicionar(Usuario usuario) throws ServiceException {
		if (existeUsuarioCadastradoComEmail(usuario.getEmail())) {
			throw new ServiceException("E-mail já cadastrado. Escolha outro e-mail");
		}
		try {
			usuario.setSenha(generateMD5(usuario.getSenha()));
			return usuarioDao.insert(usuario);
		} catch (Exception e) {
			throw new ServiceException("Erro ao realizar cadastro.");
		}
	}

	@Override
	public boolean existeUsuarioCadastradoComEmail(String email) {
		return usuarioDao.procuraUsuarioPorEmail(email)  != null;
	}
	
	private String generateToken(Usuario login) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String token = null;
		
		StringBuilder hash = new StringBuilder();
		hash.append(login.getId());
		hash.append(":");
		hash.append(login.getEmail());
		hash.append(":");
		hash.append(login.getSenha());
		hash.append(":");
		hash.append(new Date().getTime());
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(hash.toString().getBytes("UTF-8"));
		byte[] digest = md.digest();

		token = String.format("%064x", new java.math.BigInteger(1, digest));
		
		return token;
	}
	
	private String generateMD5(String senha) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytesOfMessage = senha.getBytes("UTF-8");

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		
		return String.format("%064x", new java.math.BigInteger(1, digest)); 
	}	
	
}
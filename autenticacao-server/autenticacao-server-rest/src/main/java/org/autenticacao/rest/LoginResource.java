package org.autenticacao.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.autenticacao.rest.model.LoginRS;
import org.autenticacao.service.api.IUsuarioService;
import org.autenticacao.service.exception.ServiceException;
import org.autenticacao.service.model.Usuario;

@Path("/")
public class LoginResource {

	@EJB
	private IUsuarioService usuarioService;
	
	@Path("/login/{email}/{senha}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@PathParam("email") final String email, @PathParam("senha") final String senha) {
		Response response = null;
		try {
			Usuario login = usuarioService.login(email, senha);
			LoginRS loginRS = new LoginRS();
			loginRS.setNome(login.getNome());
			loginRS.setEmail(login.getEmail());
			loginRS.setToken(login.getToken());
			
			response = Response.status(200).entity(loginRS).build();
		} catch (ServiceException e) {
			response = Response.status(500).entity("Erro ao realizar a autenticação. Tente novamente.").build();
		}
		return response;
	}
	
	@Path("/login/{email}/facebook")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpHeaders headers, @PathParam("email") final String email) {
		Response response = null; 

		List<String> tokenFBHeader = headers.getRequestHeader("tokenFB");
		List<String> idFBHeader = headers.getRequestHeader("idFB");
		
		String tokenFB = tokenFBHeader.size() > 0 ? tokenFBHeader.get(0) : "";
		String idFB = idFBHeader.size() > 0 ? idFBHeader.get(0) : "";
		if (tokenFB == null || tokenFB.equals("") || idFB == null || idFB.equals("")) {
			response = Response.status(403).entity("Autenticação com o facebook necessária. Envie o token da autenticação.").build();
		} else {
			if (!isAutenticacaoComFacebook(tokenFB, idFB)) {
				response = Response.status(403).entity("Erro ao verificar autenticidade da conexão com o facebook. Envie o token válido.").build();
			} else {
				try {
					Usuario login = usuarioService.loginFacebook(email);
					LoginRS loginRS = new LoginRS();
					loginRS.setNome(login.getNome());
					loginRS.setToken(login.getToken());
					
					response = Response.status(200).entity(loginRS).build();
				} catch (ServiceException e) {
					response = Response.status(500).entity("Erro ao realizar a autenticação. Tente novamente.").build();
				}
			}
		}
		return response;
	}
	
	@Path("/login/{email}/verificacao")
	@HEAD
	@Produces(MediaType.APPLICATION_JSON)
	public Response verificacaoLogin(@PathParam("email") final String email) {
		Response response = null;
		
		try {
			if (usuarioService.existeUsuarioCadastradoComEmail(email)) {
				response = Response.status(200).build();	
			} else {
				response = Response.status(404).build();
			}
		} catch (Exception e) {
			response = Response.status(500).build();
		}
		return response;
	}

	private boolean isAutenticacaoComFacebook(String token, String id) {
		final String urlFacebook = "https://graph.facebook.com/me?access_token=";
		boolean isAutenticado = false;

		BufferedReader rd = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlFacebook + token);
			HttpResponse response = client.execute(request);
			rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				if (line.contains(id)) {
					isAutenticado = true;
					break;
				}
			}
		} catch (Exception e) {
			// 
		} finally {
			try {
				if (rd != null) {
					rd.close();
				}
			} catch (Exception e) {
				//
			}
		}
		return isAutenticado;
	}
	
	
	
}
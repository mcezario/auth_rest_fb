package org.autenticacao.rest;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.autenticacao.service.api.IUsuarioService;
import org.autenticacao.service.exception.ServiceException;
import org.autenticacao.service.model.Usuario;

@Path("/")
public class CadastroResource {

	@EJB
	private IUsuarioService usuarioService;
	
	@Path("/cadastro")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response inclusao(Usuario usuario) {
		Response response = null;
		try {
			usuarioService.adicionar(usuario);
			
			response = Response.status(200).build();
		} catch (ServiceException e) {
			response = Response.status(500).entity(e.getMessage()).build();
		} catch (Exception e) {
			response = Response.status(500).entity("Erro ao realizar o cadastro.").build();
		}
		return response;
	}
	
}
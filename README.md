# auth_rest_fb

- *<strong>Projeto autenticacao-client</strong>*<br>
  Projeto client(front-end) utilizando *angularjs* e o plugin *openfb* para autenticação com o facebook.<br>
  Layout utilizando a biblioteca bootstrap.<br>
  Para utilizar a autenticação via *<strong>facebook</strong>* é necessário criar um *app* na sessão para 
  desenvolvedores (https://developers.facebook.com/). <br>
  Para maiores informações veja: https://developers.facebook.com/docs/apps/register<br>
  Após criar sua *app*, inserir seu app_id em */autenticacao_client/app-services/facebook.service.js*. Linha 12 trocar ->app_id<- pelo seu app_id
  
- *<strong>Projeto autenticacao-server</strong>*<br>
  Projeto server(back-end). Esse projeto é exposto via rest para os clientes. Esse exemplo foi testado utilizando o 
  servidor de aplicação jboss wildfly (http://download.jboss.org/wildfly/9.0.1.Final/wildfly-9.0.1.Final.zip).<br>
  O projeto está utilizando a biblioteca de implementação da especificação JAX-RS *resteasy* composta no wildfly.<br>
  Os serviços estão utilizando EJB para injeção de dependência e controle de transação.<br>
  Esse projeto explora as seguintes particularidades do rest:<br>
  - Está preparado para retornar chamadas via jsonp;
  - Recebe chamadas ajax para o servidor utilizando os seguintes métodos do protocolo HTTP: GET, POST e HEAD;
  - Retorna os seguintes códigos de erros para o client: 200, 403, 404 e 500.

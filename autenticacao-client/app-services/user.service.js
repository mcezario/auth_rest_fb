﻿(function () {
    'use strict';

    angular
        .module('app')
        .factory('UserService', UserService);

    UserService.$inject = ['$http', '$q', '$rootScope'];
    function UserService($http, $q, $rootScope) {
        var service = {};
        
        service.setDadosParaInclusaoViaFacebook = setDadosParaInclusaoViaFacebook;
        service.getDadosParaInclusaoViaFacebook = getDadosParaInclusaoViaFacebook;
        service.removeDadosParaInclusaoViaFacebook = removeDadosParaInclusaoViaFacebook;
        service.login = login;
        service.loginFacebook = loginFacebook;
        service.existeUsuarioPorEmail = existeUsuarioPorEmail;
        service.inclusao = inclusao;

        return service;

        function setDadosParaInclusaoViaFacebook(id, nome, email, token) {
        	$rootScope.continuacaoCadastro = {
				fb: {
					'id': id,
					'nome': nome,
					'email': email,
					'token': token
				}
			};
	    	localStorage.setItem('continuacaoCadastro', JSON.stringify($rootScope.continuacaoCadastro));
        }
        
        function getDadosParaInclusaoViaFacebook() {
        	$rootScope.continuacaoCadastro = JSON.parse(localStorage.getItem('continuacaoCadastro')) || {};
        }
        
        function removeDadosParaInclusaoViaFacebook() {
        	$rootScope.continuacaoCadastro = {};
            localStorage.removeItem('continuacaoCadastro')
        }
        
        function login(email, senha) {
        	var deferred = $q.defer();
        	
    		$http.jsonp($rootScope.server("/login/" + email + "/" + senha + "?callback=JSON_CALLBACK")).then(
				function (response) {
					deferred.resolve(response.data);
				},
				function(error) {
					deferred.reject(error.status + " - " + error.data);
				}
    		);
    		
    		return deferred.promise;
        }
        
        function loginFacebook(email, id, token) {
        	var deferred = $q.defer();
        	
        	var header = { headers: {'tokenFB': token, 'idFB': id } };
        	$http.get($rootScope.server("/login/" + email + "/facebook"), header).then(
				function (response) {
					deferred.resolve(response.data);
				},
				function(error) {
					deferred.reject(error.status + " - " + error.data);
				}
        	);
        	
        	return deferred.promise;
        }
        
        function existeUsuarioPorEmail(email) {
        	var deferred = $q.defer();
        	$http.head($rootScope.server("/login/" + email + "/verificacao")).then(
    			function(response) {
    				deferred.resolve();
    			},
    			function (error) {
    				if (error.status == 404) {
    					deferred.reject({ status: error.status, mensagem: 'Usuário inexistente.'  });
    				} else {
    					deferred.reject({ status: error.status, mensagem: 'Erro ao verificar e-mail.'  });
    				}
    			}
        	);
        	
        	return deferred.promise;
        }

        function inclusao(user) {
        	var deferred = $q.defer();
            $http.post($rootScope.server("/cadastro"), user).then(
        		function(response) {
    				deferred.resolve();
    			},
    			function (error) {
    				deferred.reject(error);
    			}
			);
            return deferred.promise;
        }

    }

})();

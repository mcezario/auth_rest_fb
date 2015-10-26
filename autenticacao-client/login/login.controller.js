﻿(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$location', '$q', '$route', '$rootScope', 'UserService', 'AuthenticationService', 'FacebookService', 'FlashService'];
    function LoginController($location, $q, $route, $rootScope, UserService, AuthenticationService, FacebookService, FlashService) {
    	
        var vm = this;
        vm.login = login;
        vm.loginFB = loginFB;

        (function initController() {
            // reset login status
        	UserService.removeDadosParaInclusaoViaFacebook();
        	var paramValue = $route.current.$$route.action;
        	if (paramValue != null && paramValue == 'logout') {
        		AuthenticationService.clearCredentials();
        		if ($rootScope.globals.currentUser != null && $rootScope.globals.currentUser.tokenFB != null) {
            		FacebookService.logout();
            	}
        	}
        	if ($rootScope.globals.currentUser) {
        		$location.path('/');
        	}
        })();

        function login() {
            vm.dataLoading = true;
            AuthenticationService.login(vm.username, vm.password).then(
        		function (response) {
        			AuthenticationService.setCredentials(response.nome, response.email, response.token, null);
        			$location.path('/');
        		},
        		function (error) {
        			FlashService.Error(error);
        		}
            ).finally(function() {
				vm.dataLoading = false;	
			});
        }
        
        function loginFB() {
        	vm.dataLoading = true;
        	FacebookService.login().then(
    			// Sucesso no login
        		function (respTokenFB) {
        			
    				FacebookService.getInfo(respTokenFB).then(
    					// Sucesso ao obter os dados do perfil
                    	function (respInfo) {
                    		UserService.existeUsuarioPorEmail(respInfo.email).then(
                    				
                    			// Sucesso na verificacao do email
            					function() {
            						AuthenticationService.loginFB(respInfo.email, respInfo.id, respTokenFB).then(
            							// Sucesso ao autenticar usuario
            							function (respAuth) {
    										AuthenticationService.setCredentials(respInfo.name, respInfo.email, respAuth.token, respTokenFB);
    										$location.path('/');
            							},
            							
            							// Erro ao autenticar usuario
            							function (error) {
    										FlashService.Error(error);
            							}
            						);
            					},
                    			// Erro ao obter usuario pelo email
            					function (error) {
            						if (error.status == 404) {
            							UserService.setDadosParaInclusaoViaFacebook(respInfo.id, respInfo.name, respInfo.email, respTokenFB);
            							$location.path('/cadastro');
                        			} else {	
                        				FlashService.Error(error);
                        				$location.path('/login');
                        			}
            					}
                    		);
                    		
                        },
    					// Erro ao obter os dados do perfil
                        function (error) {
                        	FlashService.Error(error, true);
                        	$location.path('/');
                        }
                    );
    				
    			},
    			// Erro no login
    			function (error) {
    				FlashService.Error(error);
    			}
        	).finally(function() {
				vm.dataLoading = false;	
			});
        }
    }
    
})();

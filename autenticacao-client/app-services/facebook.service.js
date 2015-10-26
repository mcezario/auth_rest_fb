(function () {
    'use strict';

    angular
        .module('app')
        .factory('FacebookService', FacebookService);

    FacebookService.$inject = ['$http', '$rootScope', '$q', 'UserService', 'AuthenticationService', 'ngFB'];
    function FacebookService($http, $rootScope, $q, UserService, AuthenticationService, ngFB) {
        var service = {};
        
        ngFB.init({appId: '->app_id<-', tokenStore: window.localStorage});
        
        service.login = login;
        service.logout = logout;
        service.getInfo = getInfo;

        return service;

        function login() {
        	var deferred = $q.defer();
        	ngFB.login({scope: 'email,publish_actions,user_birthday,public_profile'}).then(
	            function(response) {
	                deferred.resolve(response.authResponse.accessToken);
	            },
	            function(error) {
	            	deferred.reject(error);
	            }
        	);
            return deferred.promise;
        }
        
        function logout() {
        	ngFB.logout();
        }
        
        function getInfo(token) {
        	var deferred = $q.defer();
        	$http.jsonp('https://graph.facebook.com/me?access_token=' + token + '&fields=email,name,birthday,gender&callback=JSON_CALLBACK').then(
    			function(user) {
    				deferred.resolve(user.data);
    			},
 	            function(error) {
         			if (error.type == 'OAuthException' && error.code == 190) {
         				getNovoToken().then(
    						function(responseToken) {
    							getInfo(responseToken.token).then(
									function(user) {
										deferred.resolve(user.data);
									},
									function(error) {
										deferred.reject('Erro ao obter informacoes do perfil.');			
									}
								);
							},
							function(error) {
								deferred.reject('Erro ao obter token.');			
							}
    					);
    					
         			} else {
         				deferred.reject(error);
         			}
    			}
            );
        	return deferred.promise;
         }
        
        // private
        function getNovoToken() {
        	var deferred = $q.defer();
        	login(function (response) {
                if (response.success) {
                	AuthenticationService.refreshTokenFB(response.token);
                	deferred.resolve({ token: response.token });
                } else {
                	deferred.reject({ message: response.error });
                }
            });
        	return deferred.promise;
         }
    }

})();
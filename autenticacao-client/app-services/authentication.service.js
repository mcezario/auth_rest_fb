(function () {
    'use strict';

    angular
        .module('app')
        .factory('AuthenticationService', AuthenticationService);

    AuthenticationService.$inject = ['$http', '$q', '$rootScope', 'UserService'];
    function AuthenticationService($http, $q, $rootScope, UserService) {
        var service = {};

        service.login = login;
        service.loginFB = loginFB;
        service.setCredentials = setCredentials;
        service.clearCredentials = clearCredentials;

        return service;

        function login(username, password) {
        	var deferred = $q.defer();
            UserService.login(username, password).then(
                function (response) {
                	deferred.resolve(response);
                },
                function (error) {
                	deferred.reject(error);
                }
            );
            return deferred.promise;
        }
        
        function loginFB(username, id, token) {
        	var deferred = $q.defer();
            UserService.loginFacebook(username, id, token).then(
        		function (auth) {
                    deferred.resolve({ nome: auth.nome, token: auth.token });
                },
                function (error) {
                	deferred.reject(error);
                }
            );
            return deferred.promise;
        }

        function setCredentials(nome, email, tokenApp, tokenFB) {

            $rootScope.globals = {
                currentUser: {
                    username: nome,
                    email: email,
                    tokenApp: tokenApp,
                    tokenFB: tokenFB
                }
            };

            $http.defaults.headers.common['Authorization'] = 'Basic ' + tokenApp; // jshint ignore:line
            localStorage.setItem('globals', JSON.stringify($rootScope.globals));
        }
        
        function clearCredentials() {
        	$rootScope.globals = {};
            localStorage.removeItem('globals');
            
            $http.defaults.headers.common.Authorization = 'Basic ';
        }
        
    }

})();
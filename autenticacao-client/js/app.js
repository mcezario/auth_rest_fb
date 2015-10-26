(function () {
    'use strict';

    var app = angular
        .module('app', ['ngRoute', 'ngCookies', 'ngOpenFB', 'blockUI'])
        .config(config)
        .run(run);

    config.$inject = ['$routeProvider', '$locationProvider', '$httpProvider', 'blockUIConfig'];
    function config($routeProvider, $locationProvider, $httpProvider, blockUIConfig) {
    	blockUIConfig.message = 'Carregando...';
    	
    	$routeProvider
        .when('/', {
            controller: 'HomeController',
            templateUrl: 'home/home.view.html',
            controllerAs: 'vm'
        })

        .when('/login', {
            controller: 'LoginController',
            templateUrl: 'login/login.view.html',
            controllerAs: 'vm'
        })
        
        .when('/logout', {
            controller: 'LoginController',
            templateUrl: 'login/login.view.html',
            action: 'logout',
            controllerAs: 'vm'
        })

        .when('/cadastro', {
            controller: 'CadastroController',
            templateUrl: 'cadastro/cadastro.view.html',
            controllerAs: 'vm'
        })
        
        .otherwise({ redirectTo: '/login' });

    }

    run.$inject = ['$rootScope', '$location', '$http'];
    function run($rootScope, $location, $http) {
    	
    	$rootScope.globals = JSON.parse(localStorage.getItem('globals')) || {};
    	if ($rootScope.globals.currentUser) {
    		$http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.token; // jshint ignore:line
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            var restrictedPage = $.inArray($location.path(), ['/login', '/cadastro']) === -1;
            var loggedIn = $rootScope.globals.currentUser;
            if (restrictedPage && !loggedIn) {
                $location.path('/login');
            }
        });
        
        $rootScope.server = function(url){
        	var SERVER_URL = "http://localhost:8080/autenticacao/api";
            return SERVER_URL + url;
        };
        
        $rootScope.headers = function() {
        	var token = "";
        	if (localStorage.getItem('globals') != null) {
        		var globals = JSON.parse(localStorage.getItem('globals')) || {};
        		token = globals.currentUser.tokenApp;
        	}
        	return { headers: {'Token': token } };
        };

        $rootScope.go = function ( path ) {
        	$location.path( path );
      	};
      	
      	$rootScope.isMobile = true;
      	$rootScope.isOnline = navigator.onLine;
    }
    
    app.directive("passwordVerify", function() {
	   return {
	      require: "ngModel",
	      scope: {
	        passwordVerify: '='
	      },
	      link: function(scope, element, attrs, ctrl) {
	        scope.$watch(function() {
	            var combined;

	            if (scope.passwordVerify || ctrl.$viewValue) {
	               combined = scope.passwordVerify + '_' + ctrl.$viewValue; 
	            }                    
	            return combined;
	        }, function(value) {
	            if (value) {
	                ctrl.$parsers.unshift(function(viewValue) {
	                    var origin = scope.passwordVerify;
	                    if (origin !== viewValue) {
	                        ctrl.$setValidity("passwordVerify", false);
	                        return undefined;
	                    } else {
	                        ctrl.$setValidity("passwordVerify", true);
	                        return viewValue;
	                    }
	                });
	            }
	        });
	     }
	   };
	});
    
    app.directive("uniqueEmail", function(UserService) {
    	var toId;
 	   return {
 	      require: "ngModel",
 	      link: function(scope, element, attrs, ctrl) {
 	        scope.$watch(function() {
 	        	return ctrl.$viewValue;
 	        }, function(value) {
 	        	ctrl.$setValidity("uniqueEmail", true);
 	        	if (value) {
 	            	if (toId) {
	            		clearTimeout(toId);
	            	}
	                toId = setTimeout(function(){
	                	UserService.existeUsuarioPorEmail(value).then(
	                		function () {
	                            ctrl.$setValidity("uniqueEmail", false);
	                        },
	                        function (error) {
	                        	ctrl.$setValidity("uniqueEmail", true);
	                        }
	                    );
	                }, 800);
 	            }
 	        });
 	     }
 	   };
 	});

})();
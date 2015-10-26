(function () {
    'use strict';

    angular
        .module('app')
        .controller('CadastroController', CadastroController);

    CadastroController.$inject = ['UserService', 'AuthenticationService', '$location', '$rootScope', '$routeParams', 'FlashService'];
    function CadastroController(UserService, AuthenticationService, $location, $rootScope, $routeParams, FlashService) {
        var vm = this;
        vm.nome = "";
        vm.email = "";
        vm.disabledEmail = false;
        
        vm.cadastro = cadastro;
        
        (function initController() {
        	UserService.getDadosParaInclusaoViaFacebook();
        	if ($rootScope.continuacaoCadastro != null && $rootScope.continuacaoCadastro.fb != null) {
        		vm.nome = $rootScope.continuacaoCadastro.fb.nome;
        		vm.email = $rootScope.continuacaoCadastro.fb.email;
        		vm.disabledEmail = true;
        	}
        })();
        
        function cadastro() {
            UserService.inclusao(vm.user).then(
        		function () {
                    FlashService.Success('Cadastro realizado com sucesso!', true);
                    AuthenticationService.login(vm.user.email, vm.user.senha).then(
                    	function (response) {
	                     	
                			var tokenFB = null;
                			var cadastroFB = $rootScope.continuacaoCadastro;
                			if (cadastroFB != null && cadastroFB.fb != null) {
                				tokenFB = cadastroFB.fb.token;
                			}
                     	
                			AuthenticationService.setCredentials(response.nome, response.email, response.token, tokenFB);
                			$location.path('/');
	                     },
	                     function () {
	                    	 $location.path('/login');
	                     }         		
                    );
                },
                function (error) {
                    FlashService.Error(error);
                }
            );
        }

    }

})();

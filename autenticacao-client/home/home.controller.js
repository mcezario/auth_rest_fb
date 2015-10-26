(function () {
    'use strict';

    angular
        .module('app')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$rootScope', 'UserService', 'FacebookService', 'FlashService'];
    function HomeController($rootScope, UserService, FacebookService, FlashService) {
        var vm = this;
        vm.getInfoFacebook = getInfoFacebook;
        initController();

        function initController() {
            UserService.removeDadosParaInclusaoViaFacebook();
        }
        
        function getInfoFacebook() {
        	FacebookService.getInfo($rootScope.globals.currentUser.tokenFB).then(
    			function (response) {
    				vm.sexo = response.gender;
    				vm.aniversario = response.birthday;
    			},
    			function (error) {
    				FlashService.Error(error);
    			}
        	);
        }

    }

})();
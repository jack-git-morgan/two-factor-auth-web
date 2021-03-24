
try {
    var app = angular.module("myApp");
} catch (e) {
    var app = angular.module("myApp", ["ui.router"]);
}

app.config(['$stateProvider', '$urlRouterProvider', function ($stateProvider, $urlRouterProvider) {

        var loginPage = {
            name: 'login',
            url: '/',
            controller: 'loginController',
            templateUrl: 'HTML/login.html'
        }

        var createUserPage = {
            name: 'createuser',
            url: '/createuser',
            controller: 'createUserController',
            templateUrl: 'HTML/create-user.html'
        }

        console.log("Running Router!");
        $stateProvider.state(loginPage);
        $stateProvider.state(createUserPage);
        $urlRouterProvider.otherwise('/');
    }]);


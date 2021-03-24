
try {
    var app = angular.module("myApp");
} catch (e) {
    var app = angular.module("myApp", ["ui.router"]);
}

app.controller("loginController", ["$scope", "$http", function ($scope, $http) {

        $scope.loggedIn = false;
        $scope.failedAttempt = false;
        $scope.authenticated = false;
        $scope.failedAuthAttempt = false;

        /**
         * @returns {unresolved}
         */
        $scope.attemptLogin = function () {
            return $http.get("/two-factor-auth-web/api/auth/login/" + $scope.username + "/" + $scope.password).then(function (response) {
                if (response.data === "PASS")
                    $scope.loggedIn = true;
                else if (response.data === "FAIL")
                    $scope.failedAttempt = true;
                console.log("response", response);
            }, function (error) {
                console.log("response", error);
            });
        };

        /**
         * @returns {unresolved}
         */
        $scope.attemptAuthenticate = function () {
            return $http.get("/two-factor-auth-web/api/auth/authenticate/" + $scope.username + "/" + $scope.authCode).then(function (response) {

                if (response.data === "PASS")
                    $scope.authenticated = true;
                else if (response.data === "FAIL")
                    $scope.failedAuthAttempt = true;

                console.log("response", response);
            }, function (error) {
                console.log("error", error);
            });
        };

        $scope.returnToLogin = function () {
            $scope.loggedIn = false;
            $scope.authenticated = false;
        }
    }]);



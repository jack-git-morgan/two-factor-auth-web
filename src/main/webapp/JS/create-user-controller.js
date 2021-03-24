
try {
    var app = angular.module("myApp");
} catch (e) {
    var app = angular.module("myApp", ["ui.router"]);
}

app.controller("createUserController", ["$scope", "$http", function ($scope, $http) {
        console.log("createUserController");

        $scope.getGoogleAuthRegDetails = function () {

            $scope.authReady = true;
            return $http.get("/two-factor-auth-web/api/auth/createauthdetails/" + $scope.username + "/" + $scope.password).then(function (response) {
                console.log("response", response);

                $scope.setupKey = response.data.split("-")[0];
                $scope.barcodeEncoded = response.data.split("-")[1];

            }, function (error) {
                console.log("response", error);
            });
        }

        $scope.authReady = false;
    }]);
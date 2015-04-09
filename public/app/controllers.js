angular.module('searchApp')
    .controller('indexController',['$scope', '$state', '$rootScope',
        function($scope, $state, $rootScope){
            $rootScope.title = "Rate My Product";
            $scope.search = function() {
                $state.go('result',{text: $scope.text});
        };
    }])
    .controller('resultController',['$scope', '$stateParams', 'reviews', '$state', '$rootScope',
        function($scope, $stateParams, reviews, $state, $rootScope){
            $rootScope.title = 'Reviews for '+ $stateParams.text;
            $scope.label = 'none';
            $scope.search = $stateParams.text;
            $scope.reviews = reviews;
            $scope.newSearch = function() {
                $state.go('result',{text: $scope.text});
            };
        }]);
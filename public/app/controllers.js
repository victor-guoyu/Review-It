angular.module('searchApp')
    .controller('indexController',['$scope', '$state', function($scope, $state){
        $scope.search = function() {
            $state.go('result',{text: $scope.text});
        };
    }])
    .controller('resultController',['$scope', '$stateParams', 'reviews', '$state',
        function($scope, $stateParams, reviews, $state){
            $scope.reviews = reviews;
            $scope.search = $stateParams.text;
            $scope.newSearch = function() {
                $state.go('result',{text: $scope.text});
            };
        }]);
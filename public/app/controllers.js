angular.module('searchApp')
    .controller('indexController',['$scope' ,'$state' ,function($scope, $state){
        $scope.search = function() {
            $state.go('result',{text: $scope.text});
        };
    }])
    .controller('resultController',['$scope', '$stateParams', 'reviews',
        function($scope, $stateParams, reviews){
            $scope.reviews = reviews;
            $scope.search = $stateParams.text;
        }]);
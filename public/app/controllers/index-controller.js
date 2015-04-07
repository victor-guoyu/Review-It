angular.module('searchApp')
    .controller('indexController',['$scope' ,'$state' ,function($scope, $state){
        $scope.search = function() {
          $state.go('result',{text: $scope.text});
        };
    }]);
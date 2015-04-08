angular.module('searchApp')
    .controller('resultController',['$scope', '$stateParams', 'reviews',
        function($scope, $stateParams, reviews){
            window.t = reviews;
            $scope.reviews = reviews;
            $scope.search = $stateParams.text;
    }]);
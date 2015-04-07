angular.module('searchApp')
    .controller('resultController',['$scope' ,'reviews' ,function($scope, reviews){
      window.test = reviews;
    }]);
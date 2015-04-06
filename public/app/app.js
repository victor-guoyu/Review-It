angular.module('searchApp', ['ngRoute'])
    .config(['$routeProvider',function($routeProvider){
        'use strict';
        $routeProvider
            .when('/', {
                templateUrl: 'app/template/index.tpl.html',
                controller:'indexController'
            })
            .when('/result', {
                templateUrl: 'app/template/result.tpl.html',
                controller:'resultController'
            })
            .when('/404', {
                templateUrl: 'app/template/404.tpl.html'
            })
            .otherwise({redirectTo:'404'});
    }]);
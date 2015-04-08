angular.module('searchApp', ['ui.router'])
    .config(['$stateProvider', '$urlRouterProvider' ,
        function($stateProvider, $urlRouterProvider){
        'use strict';
        $stateProvider
            .state('default', {
                url:'/',
                templateUrl: 'app/template/index.tpl.html',
                controller:'indexController'
            })
            .state('result', {
                url:'/result/:text',
                templateUrl: 'app/template/result.tpl.html',
                controller:'resultController',
                resolve: {
                    reviewResource:'reviewResource',
                    $stateParams: '$stateParams',
                    reviews:function(reviewResource, $stateParams) {
                        return reviewResource.getReviews($stateParams.text);
                    }
                }
            })
            .state('404', {
                url: '/404',
                templateUrl: 'app/template/404.tpl.html'
            });
            $urlRouterProvider.otherwise('/');
    }])
    .run(['$rootScope', function($rootScope){
        $rootScope.$on('$viewContentLoaded', function(){
            $(document).foundation();
        });
    }]);
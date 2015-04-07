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
                    comments: function (reviewResource, $stateParams) {
                        return reviewResource.getComments($stateParams.text);
                    },
                    tweets: function (reviewResource, $stateParams) {
                        return reviewResource.getTweets($stateParams.text);
                    },
                    video:function (reviewResource, $stateParams) {
                        return reviewResource.getVideo($stateParams.text);
                    },
                    reviews:function(retrievedComments, retrievedTweets, retrievedVideo) {
                        return {
                            comments: retrievedComments,
                            tweets: retrievedTweets,
                            video: retrievedVideo
                        };
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
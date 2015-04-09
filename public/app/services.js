angular.module('searchApp')
    .factory('RPC_METHOD', [function(){
        return {
          searchComments : "commentSearch",
          searchTweets: "tweetSearch",
          searchVideos: "videoSearch"
        };
    }])
    .filter('videoUrl', ['$sce', function ($sce) {
        return function(videoId) {
            return $sce.trustAsResourceUrl('http://www.youtube.com/embed/' + videoId);
        };
    }])
    .filter('twitterSearchUrl', ['$sce', function ($sce) {
        return function(search) {
            return $sce.trustAsResourceUrl('https://twitter.com/search?q=' + search);
        };
    }])
    .filter('twitterProfileUrl', ['$sce', function ($sce) {
        return function(name) {
            return $sce.trustAsResourceUrl('https://twitter.com/' + name);
        };
    }])
    .filter('filterComment',[function() {
        return function(comments, label) {
            var filteredComments = [];
            if (label === 'none') {
                return comments;
            }
            angular.forEach(comments, function(comment) {
                if(comment.label === label) {
                    filteredComments.push(comment);
                }
            });
            return filteredComments;
        };
    }])
    .service('dataSource', ['$http', function($http){
      'use strict';
      var searchApi = '/search/';
      this.getData = function(text, rpcMethod) {
          var param = {"jsonrpc": "2.0",
                       "method": rpcMethod,
                       "params": {"text": text},
                       "id": 1
                      };
          return $http.post(searchApi, param);
      };
    }])
    .factory('reviewResource', ['dataSource', 'RPC_METHOD',
     function(dataSource, RPC_METHOD){
    'use strict';
      var resource = {};
         resource.getReviews = function(text) {
             var reviews = {};
            dataSource.getData(text, RPC_METHOD.searchComments)
                .then(function(serverReply) {
                    reviews.comments = serverReply.data.result;
                    return dataSource.getData(text, RPC_METHOD.searchTweets);
                })
                .then(function(serverReply) {
                    reviews.tweets = serverReply.data.result;
                    return dataSource.getData(text, RPC_METHOD.searchVideos);
                })
                .then(function(serverReply) {
                    reviews.video = serverReply.data.result;
                })
                .catch(function(error) {
                    console.log("Error occur while getting data from server: "+error);
                });
             return reviews;
         };
      return resource;
    }]);
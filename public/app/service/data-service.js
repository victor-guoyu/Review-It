angular.module('searchApp')
    .factory('RPC_METHOD', [function(){
        return {
          searchComments : "commentSearch",
          searchTweets: "tweetSearch",
          searchVideos: "videoSearch"
        };
    }])
    .service('dataSource', ['$http', function($http){
      'use strict';
      var searchApi = '/search/';
      var request = function(request, successCB, errorCB) {
        $http
          .post(searchApi, request)
          .success(successCB)
          .error(errorCB);
      };
      this.getData = function(text, rpcMethod) {
          var param = {"jsonrpc": "2.0",
                          "method": rpcMethod,
                          "params": {"text": text},
                          "id": 1
                      };
          var success = function(data) {
            return data.result;
          };
          var error = function(error){
            console.log("Error occur while getting comments from server"+error);
          };
          request(param, success, error);
      };
    }])
    .factory('reviewResource', ['dataSource', 'RPC_METHOD',
     function(dataSource, RPC_METHOD){
    'use strict';
      var resource = {};
      resource.getComments = function(text) {
        return dataSource.getData(text, RPC_METHOD.searchComments);
      };
      resource.getTweets = function(text) {
         return dataSource.getData(text, RPC_METHOD.searchTweets);
      };
      resource.getVideo = function(text) {
        return dataSource.getData(text, RPC_METHOD.searchVideos);
      };
      return resource;
    }]);
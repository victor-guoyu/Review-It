angular.module('searchApp')
    .controller('indexController',['$scope', '$state', '$rootScope',
        function($scope, $state, $rootScope){
            $rootScope.title = "Rate My Product";
            $scope.search = function() {
                $state.go('result',{text: $scope.text});
        };
    }])
    .controller('resultController',['$scope', '$stateParams', '$state', '$rootScope', 'reviews',
        function($scope, $stateParams, $state, $rootScope, reviews){
            var getLabelCount = function(comments, label) {
                var count = 0;
                angular.forEach(comments, function(comment) {
                    if(comment.commentLabel === label) {
                        count++;
                    }
                });
                return count;
                },
                numPos = getLabelCount(reviews.comments, 'positive'),
                numNet = getLabelCount(reviews.comments, 'netural'),
                numNeg = getLabelCount(reviews.comments, 'negative');
            
            $rootScope.title = 'Reviews for '+ $stateParams.text;
            $scope.label = 'none';
            $scope.search = $stateParams.text;
            $scope.reviews = reviews;
            $scope.config = {
                tooltips: true,
                legend: {
                  display: true,
                  position: 'right'
                },
                colors: ['#e94954', '#8a8081', '#e8b8bb']
            };
            $scope.data = {
                series:['Positive','Negative','Netural'],
                data:[{
                    x: "Positive",
                    y: [numPos]
                },{
                    x: "Negative",
                    y: [numNeg]
                },{
                    x: "Netural",
                    y: [numNet]
                }]
            };
            $scope.newSearch = function() {
                $state.go('result',{text: $scope.text});
            };
        }]);
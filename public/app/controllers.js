angular.module('searchApp')
    .controller('indexController',['$scope', '$state', '$rootScope',
        function($scope, $state, $rootScope){
            $rootScope.title = "Rate My Product";
            $scope.search = function() {
                $state.go('result',{text: $scope.text});
        };
    }])
    .controller('resultController',['$scope', '$stateParams', 'reviews', '$state', '$rootScope',
        function($scope, $stateParams, reviews, $state, $rootScope, filterFilter){
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
                    y: [100]
                },{
                    x: "Negative",
                    y: [50]
                },{
                    x: "Netural",
                    y: [30]
                }]
            };
            $scope.newSearch = function() {
                $state.go('result',{text: $scope.text});
            };
        }]);
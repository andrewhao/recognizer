(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ProcessedImageDetailController', ProcessedImageDetailController);

    ProcessedImageDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProcessedImage'];

    function ProcessedImageDetailController($scope, $rootScope, $stateParams, previousState, entity, ProcessedImage) {
        var vm = this;

        vm.processedImage = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('recognizerApp:processedImageUpdate', function(event, result) {
            vm.processedImage = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

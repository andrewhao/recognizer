(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ImageDetailController', ImageDetailController);

    ImageDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Image', 'ProcessedImage'];

    function ImageDetailController($scope, $rootScope, $stateParams, previousState, entity, Image, ProcessedImage) {
        var vm = this;

        vm.image = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('recognizerApp:imageUpdate', function(event, result) {
            vm.image = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();

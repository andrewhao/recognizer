(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ProcessedImageController', ProcessedImageController);

    ProcessedImageController.$inject = ['$scope', '$state', 'ProcessedImage'];

    function ProcessedImageController ($scope, $state, ProcessedImage) {
        var vm = this;
        
        vm.processedImages = [];

        loadAll();

        function loadAll() {
            ProcessedImage.query(function(result) {
                vm.processedImages = result;
            });
        }
    }
})();

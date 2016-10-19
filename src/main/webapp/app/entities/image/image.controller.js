(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ImageController', ImageController);

    ImageController.$inject = ['$scope', '$state', 'DataUtils', 'Image'];

    function ImageController ($scope, $state, DataUtils, Image) {
        var vm = this;
        
        vm.images = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        loadAll();

        function loadAll() {
            Image.query(function(result) {
                vm.images = result;
            });
        }
    }
})();

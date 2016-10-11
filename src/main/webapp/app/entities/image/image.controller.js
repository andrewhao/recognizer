(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ImageController', ImageController);

    ImageController.$inject = ['$scope', '$state', 'Image'];

    function ImageController ($scope, $state, Image) {
        var vm = this;
        
        vm.images = [];

        loadAll();

        function loadAll() {
            Image.query(function(result) {
                vm.images = result;
            });
        }
    }
})();

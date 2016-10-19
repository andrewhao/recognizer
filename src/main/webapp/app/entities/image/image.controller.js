(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ImageController', ImageController);

    ImageController.$inject = ['$scope', '$state', 'Image', 'Upload'];

    function ImageController ($scope, $state, Image, Upload) {
        var vm = this;

        vm.images = [];

        loadAll();

        $scope.file = {};

        function loadAll() {
            Image.query(function(result) {
                vm.images = result;
            });
        }
    }
})();

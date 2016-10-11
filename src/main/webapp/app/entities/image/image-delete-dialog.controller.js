(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ImageDeleteController',ImageDeleteController);

    ImageDeleteController.$inject = ['$uibModalInstance', 'entity', 'Image'];

    function ImageDeleteController($uibModalInstance, entity, Image) {
        var vm = this;

        vm.image = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Image.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

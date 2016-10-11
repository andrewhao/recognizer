(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ProcessedImageDeleteController',ProcessedImageDeleteController);

    ProcessedImageDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProcessedImage'];

    function ProcessedImageDeleteController($uibModalInstance, entity, ProcessedImage) {
        var vm = this;

        vm.processedImage = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProcessedImage.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();

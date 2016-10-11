(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ProcessedImageDialogController', ProcessedImageDialogController);

    ProcessedImageDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProcessedImage'];

    function ProcessedImageDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProcessedImage) {
        var vm = this;

        vm.processedImage = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.processedImage.id !== null) {
                ProcessedImage.update(vm.processedImage, onSaveSuccess, onSaveError);
            } else {
                ProcessedImage.save(vm.processedImage, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('recognizerApp:processedImageUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

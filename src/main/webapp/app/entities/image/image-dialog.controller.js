(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .controller('ImageDialogController', ImageDialogController);

    ImageDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Image', 'ProcessedImage', 'Upload'];

    function ImageDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Image, ProcessedImage, Upload) {
        var vm = this;

        vm.image = entity;
        vm.clear = clear;
        vm.save = save;
        vm.processedimages = ProcessedImage.query({filter: 'image-is-null'});
        $q.all([vm.image.$promise, vm.processedimages.$promise]).then(function() {
            if (!vm.image.processedImageId) {
                return $q.reject();
            }
            return ProcessedImage.get({id : vm.image.processedImageId}).$promise;
        }).then(function(processedImage) {
            vm.processedimages.push(processedImage);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;

            if (vm.image.id !== null) {
                Image.update(vm.image, onSaveSuccess, onSaveError);
            } else {
                Image.save(vm.image, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('recognizerApp:imageUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();

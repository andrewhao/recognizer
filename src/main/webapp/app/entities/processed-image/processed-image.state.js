(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('processed-image', {
            parent: 'entity',
            url: '/processed-image',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'recognizerApp.processedImage.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/processed-image/processed-images.html',
                    controller: 'ProcessedImageController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('processedImage');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('processed-image-detail', {
            parent: 'entity',
            url: '/processed-image/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'recognizerApp.processedImage.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/processed-image/processed-image-detail.html',
                    controller: 'ProcessedImageDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('processedImage');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProcessedImage', function($stateParams, ProcessedImage) {
                    return ProcessedImage.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'processed-image',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('processed-image-detail.edit', {
            parent: 'processed-image-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/processed-image/processed-image-dialog.html',
                    controller: 'ProcessedImageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProcessedImage', function(ProcessedImage) {
                            return ProcessedImage.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('processed-image.new', {
            parent: 'processed-image',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/processed-image/processed-image-dialog.html',
                    controller: 'ProcessedImageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                path: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('processed-image', null, { reload: 'processed-image' });
                }, function() {
                    $state.go('processed-image');
                });
            }]
        })
        .state('processed-image.edit', {
            parent: 'processed-image',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/processed-image/processed-image-dialog.html',
                    controller: 'ProcessedImageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProcessedImage', function(ProcessedImage) {
                            return ProcessedImage.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('processed-image', null, { reload: 'processed-image' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('processed-image.delete', {
            parent: 'processed-image',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/processed-image/processed-image-delete-dialog.html',
                    controller: 'ProcessedImageDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProcessedImage', function(ProcessedImage) {
                            return ProcessedImage.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('processed-image', null, { reload: 'processed-image' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

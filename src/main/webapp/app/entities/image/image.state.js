(function() {
    'use strict';

    angular
        .module('recognizerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('image', {
            parent: 'entity',
            url: '/image',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'recognizerApp.image.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image/images.html',
                    controller: 'ImageController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('image');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('image-detail', {
            parent: 'entity',
            url: '/image/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'recognizerApp.image.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/image/image-detail.html',
                    controller: 'ImageDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('image');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Image', function($stateParams, Image) {
                    return Image.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'image',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('image-detail.edit', {
            parent: 'image-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/image-dialog.html',
                    controller: 'ImageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('image.new', {
            parent: 'image',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/image-dialog.html',
                    controller: 'ImageDialogController',
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
                    $state.go('image', null, { reload: 'image' });
                }, function() {
                    $state.go('image');
                });
            }]
        })
        .state('image.edit', {
            parent: 'image',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/image-dialog.html',
                    controller: 'ImageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('image', null, { reload: 'image' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('image.delete', {
            parent: 'image',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/image/image-delete-dialog.html',
                    controller: 'ImageDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Image', function(Image) {
                            return Image.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('image', null, { reload: 'image' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();

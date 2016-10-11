(function() {
    'use strict';
    angular
        .module('recognizerApp')
        .factory('ProcessedImage', ProcessedImage);

    ProcessedImage.$inject = ['$resource'];

    function ProcessedImage ($resource) {
        var resourceUrl =  'api/processed-images/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();

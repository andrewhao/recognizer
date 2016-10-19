(function() {
    'use strict';
    angular
        .module('recognizerApp')
        .factory('Image', Image);

    Image.$inject = ['$resource'];

    function Image ($resource) {
        var resourceUrl =  'api/images/:id';

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
            'save': {
                transformRequest: function (data, getHeadersFn) {
                    var fd = new FormData();
                    angular.forEach(data, function(value, key) {
                        if (key !== "id" ) {
                            fd.append(key, value);
                        }
                    });
                    debugger;
                    return fd;
                },
                transformResponse: function (data, foo, bar) {
                    debugger;
                    return data;
                },
                method: 'POST',
                headers: { 'Content-Type': undefined, enctype: 'multipart/form-data' }
            },
            'update': { method:'PUT' }
        });
    }
})();

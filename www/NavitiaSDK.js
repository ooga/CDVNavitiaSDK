var exec = require('cordova/exec');
var PlacesRequestBuilder = (function () {
    function PlacesRequestBuilder() {
    }
    PlacesRequestBuilder.prototype.withQ = function (q) {
        this.q = q;
        return this;
    };
    PlacesRequestBuilder.prototype.withCount = function (count) {
        this.count = count;
        return this;
    };
    PlacesRequestBuilder.prototype.get = function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, 'NavitiaSDK', 'PlacesRequestBuilder.get', [{ q: this.q, count: this.count }]);
    };
    return PlacesRequestBuilder;
}());
var PlacesApi = (function () {
    function PlacesApi() {
    }
    PlacesApi.prototype.newPlacesRequestBuilder = function () {
        return new PlacesRequestBuilder();
    };
    return PlacesApi;
}());
var NavitiaSDK = (function () {
    function NavitiaSDK() {
    }
    NavitiaSDK.prototype.init = function (token, successCallback, errorCallback) {
        this.token = token;
        this.placesApi = new PlacesApi();
        exec(successCallback, errorCallback, 'NavitiaSDK', 'init', [token]);
    };
    return NavitiaSDK;
}());


module.exports = NavitiaSDK;
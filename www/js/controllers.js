angular.module('starter.controllers', [])

.service('localStorageService', function() {

    this.saveData = function(data) {
        window.localStorage.setItem("WallysphereIsFirstInstall", "true");
    };

    this.retrieveData = function() {
        return window.localStorage.getItem("WallysphereIsFirstInstall");
    };

    this.deleteData = function() {

    };

})

.service('sharedPreferences', function() {

    this.saveData = function(data) {
        cordova.plugins.notification.local.setSettings({
            onoff: data.onoff,
            frequency: data.frequency,
            network: data.network
        });
    };

    this.retrieveData = function() {
        cordova.plugins.notification.local.getSettings();
    };

    this.deleteData = function() {

    };

})

.controller('MainCtrl', function($ionicPlatform, $rootScope, $scope, $ionicSideMenuDelegate) {


    $rootScope.settings = {
        frequency: 'FW',
        network: 'NW',
        onoff: true
    };

    $ionicPlatform.ready(function() {
        cordova.plugins.notification.local.getSettings(
            function(data) {
                $rootScope.settings = JSON.parse(data);
                console.log(data);
            },
            function(error) {
                console.log(error);
            }
        );
    });

    $scope.toggleLeft = function() {
        $ionicSideMenuDelegate.toggleLeft();
    };
})

.controller('HomeCtrl', function($scope, $http, sharedPreferences) {

    $scope.wallpaperUrl = '';

    $scope.downloadwallpaper = function() {
        console.log('downloading wallpaper');

        var type = Math.floor((Math.random() * 2) + 1);
        switch (type) {
            case 1:
                $http.get('https://api.500px.com/v1/photos/search?term=night%2C%20sky%2C%20moon%2C%20stars&rpp=10&only=Landscapes%2C%20Nature&image_size=4&sort=highest_rating&consumer_key=QbKA0F86Jx6xvOpxzmciYlRbBQQoIykCuXFuFKOX').
                success(function(data, status, headers, config) {

                    var which = Math.floor((Math.random() * 8) + 0);
                    console.log(data.photos[which].image_url);

                    $scope.wallpaperUrl = data.photos[which].image_url;
                    DownloadFile($scope.wallpaperUrl, 'Wallysphere', 'night');

                }).
                error(function(data, status, headers, config) {
                    console.log(JSON.stringify(data));
                });
                break;
            case 2:
                $http.get('https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2962d4c497bd4b64a58dbdfb31e0da27&tags=night%2C+sky%2C+moon%2C+stars&text=night+sky+stars+moon&sort=interestingness-desc&content_type=1&media=photos&extras=url_o&per_page=10&page=1&format=json&nojsoncallback=1').
                success(function(data, status, headers, config) {
                    var which = Math.floor((Math.random() * 8) + 0);

                    console.log(data.photos.photo[which].title);

                    var flickrtitle = data.photos.photo[which].title;
                    var flickrid = data.photos.photo[which].id;
                    var flickrfarmid = data.photos.photo[which].farm;
                    var flickrserverid = data.photos.photo[which].server;
                    var flickrsecret = data.photos.photo[which].secret;

                    var flickrUrl = "https://farm" + flickrfarmid + ".staticflickr.com/" + flickrserverid + "/" + flickrid + "_" + flickrsecret + "_b.jpg";

                    $scope.wallpaperUrl = flickrUrl;

                    console.log(flickrUrl);
                    DownloadFile($scope.wallpaperUrl, 'Wallysphere', 'night');

                }).
                error(function(data, status, headers, config) {
                    console.log(JSON.stringify(data));
                });
                break;
        }

        // DownloadFile('https://www.google.co.in/images/srpr/logo11w.png', 'Wallysphere', 'morning');
        // DownloadFile($scope.wallpaperUrl, 'Wallysphere', 'morning');
    };

    $scope.setwallpaper = function() {

        SetWallpaper('morning');
    };

    $scope.getSharePreferenceData = function() {

        cordova.plugins.notification.local.getSettings(
            function(data) {
                console.log(data);
            },
            function(error) {
                console.log(error);
            }
        );

        cordova.plugins.notification.local.getScreenProperties(
            function(data) {
                console.log(data);
            },
            function(error) {
                console.log(error);
            }
        );
    };

    $scope.getNotificationDetails = function() {
        cordova.plugins.notification.local.getScheduledIds(function(ids) {
            console.log(ids.length === 0 ? '- none -' : ids.join(' ,'));
        });
    };
})

.controller('SettingsCtrl', function($scope, $rootScope, sharedPreferences) {

    function notifications() {

        cordova.plugins.notification.local.cancelAll();

        var current = new Date().getTime(),
            now = new Date(),
            downloadTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 30, 0, 0).getTime(),
            downloadmsg = 'This is a no show notification which on trigger downloads wallpaper',
            morning = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 8, 0, 0, 0).getTime(),
            morningmsg = 'Have a nice day.' + morning,
            afternoon = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 15, 0, 0, 0).getTime(),
            afternoonmsg = 'Bon apetite.' + afternoon,
            night = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 22, 0, 0, 0).getTime(),
            nightmsg = 'Thank you.' + night;

        if (downloadTime < current) {
            downloadTime = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1, 23, 30, 0, 0).getTime();
        }
        if (morning < current) {
            morning = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1, 8, 0, 0, 0).getTime();
        }
        if (afternoon < current) {
            afternoon = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1, 15, 0, 0, 0).getTime();
        }
        if (night < current) {
            night = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1, 22, 0, 0, 0).getTime();
        }


        // var now = new Date().getTime(),
        var _15_seconds_from_now = new Date(now.getTime() + 15 * 1000);
        var _45_seconds_from_now = new Date(now.getTime() + 30 * 1000);


        cordova.plugins.notification.local.schedule([{
            id: 11,
            title: 'Downloading Morning Wallpaper',
            text: 'This is a no show notification which on trigger download morning wallpaper',
            at: _15_seconds_from_now,
            smallIcon: 'res://icon'
        }, {
            id: 1,
            title: 'Downloading Wallpapers',
            text: downloadmsg,
            at: new Date(downloadTime),
            every: 'day',
            smallIcon: 'res://icon'
        }, {
            id: 2,
            title: 'Good Morning!!!',
            text: morningmsg,
            every: 'day',
            // at: new Date(morning),
            at: _45_seconds_from_now,
            smallIcon: 'res://icon'
        }, {
            id: 3,
            title: 'Good Afternoon!!!',
            text: afternoonmsg,
            every: 'day',
            at: new Date(afternoon),
            smallIcon: 'res://icon'
        }, {
            id: 4,
            title: 'Good Night!!!',
            text: nightmsg,
            every: 'day',
            at: new Date(night),
            smallIcon: 'res://icon'
        }]);

        cordova.plugins.notification.local.on("schedule", function(notification) {
            console.log("scheduled: " + notification.id);
        });

        cordova.plugins.notification.local.on("trigger", function(notification) {
            console.log("triggered: " + notification.id);
            // console.log('js triggered: ' + id);
            if (notification.id == '1') {
                // console.log('js inside ontrigger 1');

            } else if (notification.id == '2') {
                // console.log('js inside ontrigger 2');
                // SetWallpaper('morning');

            } else if (notification.id == '3') {
                // console.log('js inside ontrigger 3');
                // SetWallpaper('night');

            } else if (notification.id == '4') {
                // console.log('js inside ontrigger 4');
                // SetWallpaper('night');

            }
        });

    }

    $scope.frequencies = [{
        text: 'Every Day',
        value: 'FD'
    }, {
        text: 'Every Sunday',
        value: 'FW'
    }, {
        text: 'Every Alternate Sunday',
        value: 'FWA'
    }, {
        text: 'First Day of Every Month',
        value: 'FM'
    }];

    $scope.networks = [{
        text: 'Wifi and Mobile Data',
        value: 'NM'
    }, {
        text: 'Wifi Only',
        value: 'NW'
    }];

    $scope.change = function() {
        console.log("change: " + JSON.stringify($rootScope.settings));
        sharedPreferences.saveData($rootScope.settings);
        if (!$rootScope.settings.onoff) {
            cordova.plugins.notification.local.cancelAll();
        } else {
            notifications();
        }
    };
});

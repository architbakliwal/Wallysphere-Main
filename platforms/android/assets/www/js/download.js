//First step check parameters mismatch and checking network connection if available call    download function
function DownloadFile(URL, Folder_Name, File_Name) {
    //Parameters mismatch check
    console.log(URL);
    console.log(Folder_Name);
    console.log(File_Name);

    var deviceWidth = (window.innerWidth > 0) ? window.innerWidth : screen.width;
    var deviceHeight = (window.innerHeight > 0) ? window.innerHeight : screen.height;
    var devicePixelRatio = window.devicePixelRatio;
    console.log("WxH:P = " + deviceWidth + "x" + deviceHeight + ":" + devicePixelRatio);

    if (URL === null && Folder_Name === null && File_Name === null) {
        return;
    } else {
        //checking Internet connection availablity
        var networkState = navigator.connection.type;
        // console.log(networkState);
        if (networkState == Connection.NONE) {
            return;
        } else {
            // setWallpaper(URL, File_Name, Folder_Name);
            // download(URL, Folder_Name, File_Name); //If available download function call

            window.imageResizer.resizeImage(
                function(data) {
                    // console.log(JSON.stringify(data));
                    console.log(data.width);
                    console.log(data.height);
                },
                function(error) {
                    console.log("Error : \r\n" + error);
                },
                URL,
                deviceWidth,
                deviceHeight, {
                    imageData: 'urlImage',
                    quality: 100,
                    storeImage: true,
                    resizeType: 'maxPixelResize',
                    pixelDensity: true,
                    directory: Folder_Name,
                    filename: File_Name
                }
            );

        }
    }
}

function SetWallpaper(filename) {
    console.log('setting wallpaper');
    window.imageResizer.setWallpaper(
        function(data) {
            console.log(JSON.stringify(data));
        },
        function(error) {
            console.log("Error : \r\n" + error);
        },
        filename,
        'Wallysphere'
    );
}


function download(URL, Folder_Name, File_Name) {
    //step to request a file system 
    window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, fileSystemSuccess, fileSystemFail);

    function fileSystemSuccess(fileSystem) {
        var download_link = encodeURI(URL);
        ext = download_link.substr(download_link.lastIndexOf('.') + 1); //Get extension of URL

        var directoryEntry = fileSystem.root; // to get root path of directory
        directoryEntry.getDirectory(Folder_Name, {
            create: true,
            exclusive: false
        }, onDirectorySuccess, onDirectoryFail); // creating folder in sdcard
        var rootdir = fileSystem.root;
        var fp = rootdir.toURL(); // Returns Fulpath of local directory

        fp = fp + "/" + Folder_Name + "/" + File_Name + "." + ext; // fullpath and name of the file which we want to give
        console.log(fp);
        // download function call
        filetransfer(download_link, fp);
    }

    function onDirectorySuccess(parent) {
        // Directory created successfuly
        console.log("Directory created successfuly");
    }

    function onDirectoryFail(error) {
        //Error while creating directory
        console.log("Unable to create new directory: " + error.code);
    }

    function fileSystemFail(evt) {
        //Unable to access file system
        console.log(evt.target.error.code);
    }
}


function filetransfer(download_link, fp) {
    var fileTransfer = new FileTransfer();
    // File download function with URL and local path
    fileTransfer.download(download_link, fp,
        function(entry) {
            console.log("download complete: " + entry.fullPath);

            console.log(JSON.stringify(entry));

        },
        function(error) {
            //Download abort errors or download failed errors
            console.log("download error source " + error.source);
            //alert("download error target " + error.target);
            //alert("upload error code" + error.code);
        }
    );
}

function setWallpaper(imagePath, imageTitle, folderName) {
    var success = function() {
        console.log("Success");
    }; // Do something on success return.
    var error = function(message) {
        console.log("Oopsie! " + message);
    }; // Do something on error return.

    // For setting wallpaper & saving image
    wallpaper.setImage(imagePath, imageTitle, folderName, success, error);

}
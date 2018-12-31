app.controller('UserController', function ($controller,$scope,userService,uploadService) {
    //参一:继承父类Controller  参二:合并父类中和子类中的$scope域
    $controller("userBaseController", {$scope: $scope});

    //分页查询
    $scope.page = function (currentPage, itemsPerPage) {
        userService.queryPage(currentPage, itemsPerPage).success(

            function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            }


        )
    }

    //上传图片
    $scope.uploadExcel = function () {
            // 调用uploadService的方法完成文件的上传

            uploadService.uploadExcel().success(function (response) {
                alert(response);
            }
         )
    }



})




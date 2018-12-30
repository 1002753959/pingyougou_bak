app.controller('brandController', function ($controller,$scope,brandService) {
    //参一:继承父类Controller  参二:合并父类中和子类中的$scope域
    $controller("baseController",{$scope:$scope});

    //查询所有数据
    $scope.findAll = function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    }

    // 不携带搜索条件的分页方法
    $scope.page = function (currentPage,itemsPerPage) {
        brandService.queryPage(currentPage,itemsPerPage) .success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            }
        )
    }

    //携带搜索条件的分页方法
    $scope.search=function (currentPage,itemsPerPage) {

        brandService.search(currentPage,itemsPerPage,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            }
        )
    }


    //添加商品
    $scope.add = function () {
        var method = brandService.add($scope.entity);
        //进来判断这个按钮时添加还是修改
        if ($scope.entity.id != null) {
            //修改
            method = brandService.update($scope.entity);
        }
        //添加
        method.success(
           function (response) {
                if (response.flag) {
                    $scope.reloadList();
                } else {
                    alert(response.message);
                }
            }
        )
    }

    //根据id查询Brand
    $scope.findById = function (id) {
        brandService.queryBrandById(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }





    //点击删除按钮把数组传到后台删除
    $scope.delete = function () {
        brandService.delete($scope.selectIds).success(
            function (response) {
                if (response.flag) {
                    //成功跳转到刷新
                    $scope.reloadList();
                } else {
                    //失败提示信息
                    alert(response.message)
                }
            }
        )
    }
    $scope.status = ["待审核","审核通过","审核未通过","未审核"];

// 审核的方法
    $scope.updateStatus = function(status){
        brandService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }

    // 显示状态


   /* $scope.BrandList = [];
    // 显示分类:
    $scope.findBrandList = function(){

        brandService.findAll().success(function(response){
            for(var i=0;i<response.length;i++){
                $scope.BrandList[response[i].id] = response[i].name;
            }
        });
    }*/
});


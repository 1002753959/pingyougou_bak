 //控制层 
app.controller('orderStatController' ,function($scope,$controller,$location,$window,orderStatService){

    //$controller('baseController',{$scope:$scope});//继承

    $scope.time=0;

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
    	// alert($scope.time)
        orderStatService.findAll().success(
            orderStatService.findAll($scope.time).success(
                function(response){
                    $scope.list=response;
                    //$scope.paginationConf.totalItems=response.total;//更新总记录数
                }
            )
        )
    }

    // $scope.searchEntity={};//定义搜索对象




    //点击复选框更新ids数组
    $scope.selectIds = [];
    $scope.updateSelection = function ($event,id) {
        //判断这个复选框是否被选中
        if ($event.target.checked) {
            //选中了添加到数组总
            $scope.selectIds.push(id);
        }else {
            //没有选中,找到索引删掉
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);

        }

    };

});

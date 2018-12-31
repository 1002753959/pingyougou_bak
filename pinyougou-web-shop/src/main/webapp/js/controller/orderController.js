 //控制层 
app.controller('orderController' ,function($scope,$controller,$location,$window,orderService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findAll=function(){
    	// alert("你好")
        orderService.findAll().success(

            function(response){
                $scope.list=response;
            }
        );
    }

    //根据id查询Brand
    $scope.findById = function (orderId) {
        orderService.queryBrandByOrderId(orderId).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }

    $scope.searchEntity={};//定义搜索对象


    //搜索
    $scope.search=function(page,rows){
        orderService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

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
    //状态数组
    $scope.paymentType = ["","在线支付","货到付款"];
    //支付方式数组status
    $scope.status = ["","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];

    // 发货的方法:
    $scope.updateStatus = function(status){
        orderService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];

            }else{
                alert(response.message);
            }
        });
    }
});

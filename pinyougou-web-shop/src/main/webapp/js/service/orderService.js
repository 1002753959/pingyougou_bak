//服务层
app.service('orderService',function($http){

    //读取列表数据绑定到表单中
    this.findAll=function () {
        return $http.get('../order/findAll.do');
    }

    // 根据Id查询
    //根据Id查询
    this.queryBrandByOrderId=function (orderId) {
        return $http.get("../order/queryBrandByOrderId.do?orderId=" + orderId);
    }

    //搜索
    this.search=function(page,rows,searchEntity){
        return $http.post('../order/search.do?page='+page+"&rows="+rows, searchEntity);
    }

    // 发货
    this.updateStatus = function(ids,status){
        return $http.get('../order/updateStatus.do?ids='+ids+"&status="+status);
    }

});

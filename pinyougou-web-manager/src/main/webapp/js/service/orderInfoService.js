//服务层
app.service('orderInfoService',function($http){

    //读取列表数据绑定到表单中
    this.findAll=function (time) {
        return $http.get('../orderInfo/findLine.do?time='+time);
    }

    //搜索
    // this.search=function(page,rows,time){
    //     return $http.get('../orderStat/search.do?page='+page+"&rows="+rows+"&time="+time);
    // }

});

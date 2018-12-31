//服务层
app.service('orderstatisticalService',function($http){

	this.search=function(page,rows,searchEntity){
		return $http.post('../order/orderstatistical.do?page='+page+"&rows="+rows, searchEntity);
	}  
	

});

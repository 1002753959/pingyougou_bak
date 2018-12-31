//服务层
app.service('orderService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../specification/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../specification/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../specification/findOne.do?id='+id);
	}

	this.search=function(page,rows,searchEntity){
		return $http.post('../order/ordersearch.do?page='+page+"&rows="+rows, searchEntity);
	}  
	
	this.selectOptionList=function(){
		return $http.get("../specification/selectOptionList.do");
	}
});

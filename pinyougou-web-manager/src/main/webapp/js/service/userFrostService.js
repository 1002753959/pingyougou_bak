//服务层
app.service('userFrostService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../user/findAll.do');
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../user/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../user/findOne.do?id='+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../user/add.do',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../user/update.do',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../user/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../user/search.do?page='+page+"&rows="+rows, searchEntity);
	}    
	
	this.frostStatus = function(id){
		return $http.get('../user/frostStatus.do?id='+id);
	}
	this.updateStatus = function(id){
		return $http.get('../user/updateStatus.do?id='+id);
	}
	//用户数据导出到excel表
	this.excelExport = function(){
		return $http.get('../user/excelExport.do');
	}
	//用商品据导出到excel表
	this.excelExportProduct = function(){
		return $http.get('../user/excelExportProduct.do');
	}
	//用订单据导出到excel表
	this.excelExportOrder = function(){
		return $http.get('../user/excelExportOrder.do');
	}
});

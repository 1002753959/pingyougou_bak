 //控制层 
app.controller('orderstatisticalController' ,function($scope,$controller ,orderstatisticalService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		orderstatisticalService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){
		orderstatisticalService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){

		orderstatisticalService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

	// 显示状态
	$scope.status = ["未支付","已付款","未发货","已发货","交易成功","交易关闭","待评价"];

	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){
		orderstatisticalService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    
});	

 //控制层 
app.controller('userFrostController' ,function($scope,$controller,userFrostService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
        userFrostService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){
        userFrostService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
        userFrostService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=userFrostService.update( $scope.entity ); //修改
		}else{
			serviceObject=userFrostService.add( $scope.entity  );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.flag){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		userFrostService.dele( $scope.selectIds ).success(
			function(response){
				if(response.flag){
					$scope.reloadList();//刷新列表
					$scope.selectIds = [];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
    $scope.status = ["冻结","正常","审核未通过","关闭"];
	//搜索
	$scope.search=function(page,rows){			
		userFrostService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	$scope.frostStatus = function(id){
		userFrostService.frostStatus(id).success(function(response){
			if(response.flag){
				alert(response.message)
				//重新查询 
	        	$scope.reloadList();//重新加载
			}else{
				alert(response.message);
			}
		});
	}
	$scope.updateStatus = function(id){
		userFrostService.updateStatus(id).success(function(response){
			if(response.flag){
				alert(response.message)
				//重新查询
	        	$scope.reloadList();//重新加载
			}else{
				alert(response.message);
			}
		});
	}
	//用户表/商品表/订单表  的数据导入
	$scope.excelExport = function(){
		userFrostService.excelExport().success(function(response){
			if(response.flag){
				alert(response.message)
				//重新查询
	        	$scope.reloadList();//重新加载
			}else{
				alert(response.message);
			}
		});
	}
	$scope.excelExportProduct = function(){
		userFrostService.excelExportProduct().success(function(response){
			if(response.flag){
				alert(response.message)
				//重新查询
	        	$scope.reloadList();//重新加载
			}else{
				alert(response.message);
			}
		});
	}
	$scope.excelExportOrder = function(){
		userFrostService.excelExportOrder().success(function(response){
			if(response.flag){
				alert(response.message)
				//重新查询
	        	$scope.reloadList();//重新加载
			}else{
				alert(response.message);
			}
		});
	}
});	

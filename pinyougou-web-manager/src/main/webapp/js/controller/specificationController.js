 //控制层 
app.controller('specificationController' ,function($scope,$controller   ,specificationService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		specificationService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		specificationService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){

		specificationService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.specification.id!=null){//如果有ID
			serviceObject=specificationService.update( $scope.entity ); //修改  
		}else{
			serviceObject=specificationService.add( $scope.entity  );//增加 
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
		specificationService.dele($scope.selectIds).success(
			function(response){
				if(response.flag){
					$scope.reloadList();//刷新列表
					$scope.selectIds = [];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		specificationService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	
	$scope.addTableRow = function(){
		$scope.entity.specificationOptionList.push({});
	}
	
	$scope.deleteTableRow = function(index){
		$scope.entity.specificationOptionList.splice(index,1);
	}

    //根据id查询spec
    $scope.findById = function (id) {
        specificationService.findSpecById(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }

    $scope.status = ["待审核","审核通过","审核未通过","未审核"];

    // 审核的方法
    $scope.updateStatus = function(status){
        specificationService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }

    // 显示状态


    // $scope.SpecList = [];
    // // 显示分类:
    // $scope.findSpecList = function(){
    //
    //     specificationService.findAll().success(function(response){
    //         for(var i=0;i<response.length;i++){
    //             $scope.SpecList[response[i].id] = response[i].name;
    //         }
    //     });
    // }
});	

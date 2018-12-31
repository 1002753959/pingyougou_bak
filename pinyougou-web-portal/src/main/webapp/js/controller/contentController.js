app.controller("contentController",function($scope,contentService){

	//数组 首页广告太多了
	$scope.contentList = [];

	// 根据分类ID查询广告的方法:
	$scope.findByCategoryId = function(categoryId){
		contentService.findByCategoryId(categoryId).success(function(response){
			$scope.contentList[categoryId] = response;//List<Content>
		});
	}
	
	//搜索  （传递参数）
	$scope.search=function(){
		location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
	//根据分类ID查询全部商品分类的一级分类
    $scope.findByCategoryList1 = function(categoryId){
        contentService.findByCategoryList1(categoryId).success(function(response){
            $scope.itemCatList1 = response;//List<Content>
        });
    }
	
});
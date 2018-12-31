app.service("contentService",function($http){
	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}

	//修改
    this.update = function(entity){
        return $http.post('../content/update.do',entity);
    }
    //查询全部商品分类的一级分类
    this.findByCategoryList1 = function(categoryId){
        return $http.get("itemCat/findByCategoryList1.do?categoryId="+categoryId);
    }
});
app.service("contentService",function($http){
	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}

	//修改
    this.update = function(entity){
        return $http.post('../content/update.do',entity);
    }
});
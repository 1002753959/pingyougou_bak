app.service('searchService',function($http){
	
	
	this.search=function(searchMap){
		return $http.post('itemsearch/search.do',searchMap);
	}

    //收藏商品
    this.addGoodsCollection=function(itemId){
        return $http.get('cart/addGoodsCollection.do?itemId='+itemId);
    }


});
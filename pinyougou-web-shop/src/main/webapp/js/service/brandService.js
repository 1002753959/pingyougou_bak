app.service("brandService",function ($http) {
    //查询所有
    this.findAll=function () {
        return $http.get("../brand/query.do");
    }

    //不带条件的查询
    this.queryPage=function (currentPage,itemsPerPage) {
        return $http.get("../brand/queryPage.do?pageNum=" + currentPage +
            "&pageSize=" + itemsPerPage)
    }

    //带条件的查询
    this.search=function (currentPage,itemsPerPage,searchEntity) {
        return $http.post("../brand/queryPageWithSearch.do?pageNum=" + currentPage +
            "&pageSize=" + itemsPerPage, searchEntity);
    }

    //添加
    this.add=function (entity) {
        return $http.post("../brand/add.do", entity)
    }

    //更新
    this.update=function (entity) {
        return $http.post("../brand/update.do", entity)
    }


    //根据Id查询
    this.queryBrandById=function (id) {
        return $http.get("../brand/queryBrandById.do?id=" + id);
    }

    //删除
    this.deleteByIds=function (checkedIds) {
       return $http.get("../brand/deleteByIds.do?ids=" + checkedIds)
    }

    //查询所有返回List<map>集合
    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do")
    }

    this.updateStatus = function(ids,status){
        return $http.get('../brand/updateStatus.do?ids='+ids+"&status="+status);
    }

})
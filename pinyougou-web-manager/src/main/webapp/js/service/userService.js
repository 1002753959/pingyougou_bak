app.service("userService",function ($http) {

    //分页查询（不带条件）
    this.queryPage=function (currentPage,itemsPerPage) {
        return $http.get("/user/queryPage.do?pageNum=" + currentPage +
            "&pageSize=" + itemsPerPage)
    }


})